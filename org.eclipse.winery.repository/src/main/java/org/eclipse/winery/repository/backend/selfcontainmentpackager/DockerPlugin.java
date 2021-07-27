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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.tosca.TArtifactType;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.constants.OpenToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.model.version.VersionSupport;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.FileUtils;
import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.repository.converter.support.Utils;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateFilesDirectoryId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DockerPlugin implements SelfContainmentPlugin {

    private static final Logger logger = LoggerFactory.getLogger(DockerPlugin.class);

    private final Map<QName, TNodeType> nodeTypes;
    private final Map<QName, TArtifactType> artifactTypes;

    public DockerPlugin() {
        IRepository repository = RepositoryFactory.getRepository();
        nodeTypes = repository.getQNameToElementMapping(NodeTypeId.class);
        artifactTypes = repository.getQNameToElementMapping(ArtifactTypeId.class);
    }

    @Override
    public boolean canHandleArtifactType(QName artifactType, IRepository repository) {
        return artifactType != null &&
            ModelUtilities.isOfType(OpenToscaBaseTypes.dockerContainerArtifactType, artifactType, artifactTypes);
    }

    @Override
    public boolean canHandleNodeType(QName nodeType, IRepository repository) {
        return nodeType != null &&
            ModelUtilities.isOfType(OpenToscaBaseTypes.dockerContainerNodeType, nodeType, this.nodeTypes);
    }

    @Override
    public GeneratedArtifacts downloadDependenciesBasedOnArtifact(QName artifactTemplate,
                                                                  IRepository repository) {
        ArtifactTemplateId originalId = new ArtifactTemplateId(artifactTemplate);
        ArtifactTemplateFilesDirectoryId originalFilesId = new ArtifactTemplateFilesDirectoryId(originalId);
        GeneratedArtifacts generatedArtifacts = new GeneratedArtifacts(artifactTemplate);

        for (RepositoryFileReference containedFile : repository.getContainedFiles(originalFilesId)) {
            if (containedFile.getFileName().endsWith(".zip")) {
                try (InputStream in = repository.newInputStream(containedFile)) {
                    Path tempDirectory = Files.createTempDirectory(containedFile.getFileName());
                    List<String> fileListFromZip = Utils.getFileListFromZip(new ZipInputStream(in), tempDirectory.toAbsolutePath().toString());

                    if (fileListFromZip.contains("Dockerfile")) {
                        generatedArtifacts.selfContainedArtifactQName = this.createDockerImage(tempDirectory, originalId, repository);
                    }

                    FileUtils.forceDelete(tempDirectory);
                } catch (IOException e) {
                    logger.error("Error while opening input-stream to zip file!", e);
                }
            } else if (containedFile.getFileName().equals("Dockerfile")) {
                try {
                    Path tempDirectory = Files.createTempDirectory(containedFile.getFileName());
                    FileUtils.copyFiles(repository.ref2AbsolutePath(containedFile).getParent(), tempDirectory, new ArrayList<>());
                    generatedArtifacts.selfContainedArtifactQName =
                        this.createDockerImage(tempDirectory, originalId, repository);
                    FileUtils.forceDelete(tempDirectory);
                } catch (IOException e) {
                    logger.error("Error while creating temp directory!", e);
                }
            }
        }

        return generatedArtifacts;
    }

    @Override
    public void downloadDependenciesBasedOnNodeType(TNodeTypeImplementation nodeTypeImplementation, IRepository repository) {
        if (nodeTypeImplementation.getDeploymentArtifacts() != null) {
            nodeTypeImplementation.getDeploymentArtifacts().stream()
                .filter(da -> da.getArtifactType() != null)
                .filter(da -> da.getArtifactType().equals(OpenToscaBaseTypes.dockerContainerArtifactType))
                .filter(da -> da.getArtifactRef() != null)
                .forEach(da -> {
                    GeneratedArtifacts generatedArtifacts = downloadDependenciesBasedOnArtifact(da.getArtifactRef(), repository);
                    if (generatedArtifacts.selfContainedArtifactQName != null) {
                        da.setArtifactRef(generatedArtifacts.selfContainedArtifactQName);
                    }
                });
        }
    }

    private QName createDockerImage(Path tempDirectory, ArtifactTemplateId artifactTemplate, IRepository repository) {
        String dockerImageName = artifactTemplate.getQName().getLocalPart().toLowerCase().replaceAll("(\\s)|(_)|(-)", "");
        QName selfContainedVersion = VersionSupport.getSelfContainedVersion(artifactTemplate);
        ArtifactTemplateId generatedArtifactTemplateId = new ArtifactTemplateId(selfContainedVersion);

        if (!repository.exists(generatedArtifactTemplateId)) {
            try {
                Utils.execute(tempDirectory.toString(), "docker", "build", "-t", dockerImageName, ".");
                String tarball = dockerImageName + ".tar";
                Utils.execute(tempDirectory.toString(), "docker", "save", "-o", tarball, dockerImageName + ":latest");

                Utils.compressTarBallAndAddToArtifact(tempDirectory, repository, generatedArtifactTemplateId, tarball);

                FileUtils.forceDelete(tempDirectory);
            } catch (InterruptedException | IOException e) {
                logger.error("Error while creating Dockerfile...", e);
                return null;
            }
        }

        return selfContainedVersion;
    }
}
