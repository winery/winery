/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/
package org.eclipse.winery.repository.backend.selfcontainmentpackager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.version.VersionSupport;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.filebased.FileUtils;
import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.repository.converter.support.Utils;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateFilesDirectoryId;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptPlugin implements SelfContainmentPlugin {

    private static final Logger logger = LoggerFactory.getLogger(ScriptPlugin.class);

    private void compressFolderContents(final String sourceDirectory, final String tarLocation) {
        try (
            FileOutputStream fos = new FileOutputStream(tarLocation);
            GZIPOutputStream gos = new GZIPOutputStream(new BufferedOutputStream(fos));
            TarArchiveOutputStream tarOs = new TarArchiveOutputStream(gos)
        ) {
            final File folder = new File(sourceDirectory);
            final File[] fileNames = folder.listFiles();
            if (fileNames != null) {
                for (final File file : fileNames) {
                    addFilesToTarGZ(file, tarOs);
                }
            }
        } catch (final IOException e) {
            logger.error("Error while creating tar-ball", e);
        }
    }

    static private void addFilesToTarGZ(final File file, final TarArchiveOutputStream tos) throws IOException {
        // New TarArchiveEntry
        tos.putArchiveEntry(new TarArchiveEntry(file, file.getName()));
        File[] files = file.listFiles();
        if (file.isDirectory() && files != null) {
            // no need to copy any content since it is a directory, just close the output stream
            tos.closeArchiveEntry();
            for (final File cFile : files) {
                // recursively call the method for all the subfolders
                addFilesToTarGZ(cFile, tos);
            }
        } else {
            try (FileInputStream fis = new FileInputStream(file);
                 BufferedInputStream bis = new BufferedInputStream(fis)) {
                IOUtils.copy(bis, tos);
                tos.closeArchiveEntry();
            }
        }
    }

    private String updateScriptFile(String packageFileName) {
        return "#//Self-contained CSAR//" + packageFileName + '\n' +
            "innerCsarRoot=$(find ~ -maxdepth 1 -path \"*.csar\");\n" +
            "IFS=';' read -ra NAMES <<< \"$DAs\";\n" +
            "for k in \"${NAMES[@]}\"; do\n" +
            "    IFS=',' read -ra PATH <<< \"$k\"; \n" +
            "    selfContainedDaDirName=$(/usr/bin/sudo /usr/bin/dirname $innerCsarRoot${PATH[1]})\n" +
            "    selfContainedDaName=$(/usr/bin/sudo /usr/bin/basename $innerCsarRoot${PATH[1]})\n" +
            "    selfContainedExtractFolderName=\"${selfContainedDaName%.*}\"\n" +
            "    if [[ \"${PATH[1]}\" == *" + packageFileName + " ]];\n" +
            "    then\n" +
            "        /usr/bin/sudo mkdir -p \"$selfContainedDaDirName/$selfContainedExtractFolderName\"\n" +
            "        /bin/tar  -xvzf \"$selfContainedDaDirName/$selfContainedDaName\" -C \"$selfContainedDaDirName/$selfContainedExtractFolderName\"\n" +
            "        break\n" +
            "    fi\n" +
            "done\n" +
            "export DEBIAN_FRONTEND=noninteractive\n" +
            "/usr/bin/sudo -E /usr/bin/dpkg -i -R -E -B \"$selfContainedDaDirName/$selfContainedExtractFolderName\"\n";
    }

    @Override
    public boolean canHandleArtifactType(QName artifactType, IRepository repository) {
        return artifactType != null && artifactType.equals(ToscaBaseTypes.scriptArtifactType);
    }

    @Override
    public GeneratedArtifacts downloadDependenciesBasedOnArtifact(QName artifactTemplate, IRepository repository) {
        ArtifactTemplateId originalId = new ArtifactTemplateId(artifactTemplate);
        QName selfContainedVersion = VersionSupport.getSelfContainedVersion(originalId);
        ArtifactTemplateId selfContainedId = new ArtifactTemplateId(selfContainedVersion);

        if (!repository.exists(selfContainedId)) {
            try {
                repository.duplicate(originalId, selfContainedId);
            } catch (IOException e) {
                logger.error("Could not create self-containd artifact template {}", selfContainedId, e);
            }
        }

        ArtifactTemplateFilesDirectoryId originalFilesId = new ArtifactTemplateFilesDirectoryId(selfContainedId);
        GeneratedArtifacts generatedArtifacts = new GeneratedArtifacts(artifactTemplate);
        generatedArtifacts.selfContainedArtifactQName = selfContainedVersion;

        boolean createdSelfContainedVersion = false;
        for (RepositoryFileReference containedFile : repository.getContainedFiles(originalFilesId)) {
            if (containedFile.getFileName().endsWith(".sh")) {
                StringBuilder newScriptContents = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(repository.ref2AbsolutePath(containedFile).toFile()))) {
                    String line;
                    ArrayList<String> packageNames = new ArrayList<>();
                    int packageNameCount = 0;

                    while ((line = reader.readLine()) != null) {
                        List<String> strings = Arrays.asList(line.replaceAll("[;&]", "").split("\\s+"));
                        Iterator<String> words = strings.iterator();

                        if (words.hasNext() && StringUtils.isNotBlank(line) && line.contains("apt")) {
                            String word = words.next();
                            while ("sudo".equals(word) || word.startsWith("-")) {
                                word = words.next();
                            }
                            if (words.hasNext() && ("apt-get".equals(word) || "apt".equals(word))) {
                                word = words.next();
                                while (word.startsWith("-")) {
                                    word = words.next();
                                }
                                if (word.equals("install") && words.hasNext()) {
                                    words.forEachRemaining(packageToInstall -> {
                                        if (!packageToInstall.startsWith("-")) {
                                            packageNames.add(createDeploymentArtifact(
                                                originalId, repository, generatedArtifacts, packageToInstall)
                                            );
                                        }
                                    });
                                }
                            }
                        }

                        if (!packageNames.isEmpty() && packageNameCount++ < packageNames.size()) {
                            createdSelfContainedVersion = true;
                            packageNames.forEach(packet -> newScriptContents.append(this.updateScriptFile(packet)));
                        } else {
                            newScriptContents.append(line).append("\n");
                        }
                    }
                } catch (IOException e) {
                    logger.error("Error while reading script file {}", repository.ref2AbsolutePath(containedFile), e);
                }

                if (newScriptContents.length() > 0) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(repository.ref2AbsolutePath(containedFile).toFile()))) {
                        writer.write(newScriptContents.toString());
                        writer.flush();
                    } catch (IOException e) {
                        logger.error("Error while writing to script file {}", repository.ref2AbsolutePath(containedFile), e);
                    }
                }
            }
        }

        if (createdSelfContainedVersion) {
            return generatedArtifacts;
        }

        try {
            repository.forceDelete(selfContainedId);
        } catch (IOException e) {
            logger.error("Could not delete not required self-contained {}!", selfContainedId, e);
        }

        return null;
    }

    private String createDeploymentArtifact(ArtifactTemplateId artifactTemplate, IRepository repository,
                                            GeneratedArtifacts generatedArtifacts, String packageToInstall) {
        QName packageDaId = new QName(
            artifactTemplate.getQName().getNamespaceURI(),
            VersionSupport.getNewComponentVersionId(artifactTemplate, packageToInstall + "-DA")
        );
        ArtifactTemplateId deployArtId = new ArtifactTemplateId(packageDaId);
        String generatedPackage = packageToInstall + ".tar.gz";

        if (!repository.exists(deployArtId)) {
            String cmd = "/usr/bin/apt download $(/usr/bin/apt-cache depends --recurse --no-recommends --no-suggests --no-conflicts " +
                "--no-breaks --no-replaces --no-enhances --no-pre-depends " + packageToInstall + " | /bin/grep \"^\\w\")";

            logger.info("Executing command: \"{}\"", cmd);

            Path tempDirectory = null;
            try {
                repository.setElement(deployArtId,
                    new TArtifactTemplate.Builder(
                        deployArtId.getXmlId().getDecoded(),
                        ToscaBaseTypes.archiveArtifactType)
                        .build()
                );

                tempDirectory = Files.createTempDirectory(packageDaId.getLocalPart());
                ArtifactTemplateFilesDirectoryId filesId = new ArtifactTemplateFilesDirectoryId(deployArtId);

                try {
                    Utils.execute(tempDirectory.toString(), "bash", "-c", cmd);
                } catch (IOException | InterruptedException e) {
                    logger.info("Cannot perform download, skipping it! You must add the DA contents yourself!");
                    logger.debug("Root cause:", e);
                }

                // Ensure the folder structure exists.
                RepositoryFileReference tarFileRef = new RepositoryFileReference(filesId, generatedPackage);
                if (repository.id2AbsolutePath(filesId).toFile().mkdirs()) {
                    compressFolderContents(tempDirectory.toString(), repository.ref2AbsolutePath(tarFileRef).toString());

                    BackendUtils.synchronizeReferences(repository, deployArtId);
                } else {
                    logger.error("Could not create folders: {}", repository.id2AbsolutePath(filesId).toFile().getAbsolutePath());
                }
            } catch (Exception e) {
                logger.error("Error while downloading artifacts...", e);
            } finally {
                if (tempDirectory != null) {
                    FileUtils.forceDelete(tempDirectory);
                }
            }
        }

        generatedArtifacts.deploymentArtifactsToAdd.add(packageDaId);

        return generatedPackage;
    }

    @Override
    public boolean canHandleNodeType(QName nodeType, IRepository repository) {
        return false;
    }

    @Override
    public void downloadDependenciesBasedOnNodeType(TNodeTypeImplementation nodeTypeImplementation, IRepository repository) {
        // noop
    }
}
