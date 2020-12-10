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
package org.eclipse.winery.repository.converter.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;

import org.apache.commons.lang3.StringUtils;

public class DockerUtils {

    public static String buildDockerImage(ArtifactTemplateId artifactTemplateId, IRepository repository) throws IOException {
        TArtifactTemplate newDockerArtifactTemplate = repository.getElement(artifactTemplateId);

        String fileLocation = Utils.findFileLocation(newDockerArtifactTemplate, repository);
        String folder = fileLocation.substring(0, fileLocation.lastIndexOf("/"));
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileLocation));
        List<String> zipFiles = Utils.getFileListFromZip(zis, folder.concat("/"));

        StringBuilder referencedFile = new StringBuilder();
        if (zipFiles.contains("Dockerfile")) {
            // DockerFile exists in the source. Image will be created.
            String dockerImageName = fileLocation.substring(fileLocation.lastIndexOf(File.pathSeparator) + 1, fileLocation.lastIndexOf("."));
            try {
                commandLineExecutor(folder, "docker", "build", "-t", dockerImageName, ".");
                commandLineExecutor(folder, "docker", "save", "-o", dockerImageName + ".tar", dockerImageName + ":latest");

                referencedFile.append(dockerImageName + ".tar.gz");

                Utils.compressTarFile(new File(folder + File.pathSeparator + dockerImageName + ".tar"));
                File folderFile = new File(folder);

                // Image has been compressed, clear the folder.
                Utils.deleteFilesInFolder(folderFile, "gz");
            } catch (Exception e) {
                throw new RuntimeException("Failed to build Docker image");
            }
        }

        if (StringUtils.isNotBlank(referencedFile)) {
            BackendUtils.synchronizeReferences(repository, artifactTemplateId);
            repository.setElement(artifactTemplateId, newDockerArtifactTemplate);
        }

        return referencedFile.toString();
    }

    private static void commandLineExecutor(final String directoryPath, final String... command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(new File(directoryPath));

        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        pb.redirectInput(ProcessBuilder.Redirect.INHERIT);

        pb.start()
            .waitFor();
    }
}
