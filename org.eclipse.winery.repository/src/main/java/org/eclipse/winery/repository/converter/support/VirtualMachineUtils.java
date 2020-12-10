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

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.constants.OpenToscaBaseTypes;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;

public class VirtualMachineUtils {

    public static ArtifactTemplateId createSelfArtifactTemplateForUbuntu(NodeTypeImplementationId childid, IRepository repository) throws IOException {
        String repositoryPath = repository.getRepositoryRoot().toString();

        TArtifactTemplate artifactTemplate = new TArtifactTemplate();
        ArtifactTemplateId artifactTemplateId = new ArtifactTemplateId(
            "http://opentosca.org/artifacttemplates", childid.getXmlId().getDecoded() + "-dependency",
            false);

        if (repository.exists(artifactTemplateId)) {
            return artifactTemplateId;
        }

        artifactTemplate.setName(artifactTemplateId.getQName().getLocalPart());
        artifactTemplate.setId(artifactTemplateId.getQName().getLocalPart());
        artifactTemplate.setType(OpenToscaBaseTypes.isoArtifactType);

        repository.setElement(artifactTemplateId, artifactTemplate);

        try {
            String[] splitFileName = childid.getQName().getLocalPart().split("-");
            downloadIsoFile(repositoryPath + "/" + artifactTemplateId.getNamespace().getDecoded() + "/"
                + artifactTemplateId.getQName().getLocalPart(), splitFileName[2]);

            // Set references
            BackendUtils.synchronizeReferences(repository, artifactTemplateId);
            repository.setElement(artifactTemplateId, artifactTemplate);
        } catch (Exception e) {
            throw new RuntimeException();
        }

        return artifactTemplateId;
    }

    private static void downloadIsoFile(String path, String version) throws IOException {
        Files.createDirectories(Paths.get(path + "/files"));
        path = path + "/files/dependency.iso";
        // TODO: use cloud-images... https://cloud-images.ubuntu.com/
        URL website = new URL("http://old-releases.ubuntu.com/releases/" + version + ".0/ubuntu-" + version + "-server-amd64.iso");
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(path);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }
}
