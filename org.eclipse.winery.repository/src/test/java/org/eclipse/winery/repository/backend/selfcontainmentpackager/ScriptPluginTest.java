/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

import java.nio.charset.StandardCharsets;
import java.util.SortedSet;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateFilesDirectoryId;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScriptPluginTest extends TestWithGitBackedRepository {

    @Test
    void downloadDeps() throws Exception {
        this.setRevisionTo("origin/plain");
        ScriptPlugin scriptPlugin = new ScriptPlugin();
        QName originalQName = QName.valueOf("{http://opentosca.org/add/management/to/instances/nodetypeimplementations}MySQL-DBMS_5.7-Install-w1");
        SelfContainmentPlugin.GeneratedArtifacts generatedArtifacts = scriptPlugin.downloadDependenciesBasedOnArtifact(
            originalQName,
            RepositoryFactory.getRepository()
        );

        ArtifactTemplateId selfContainedId = new ArtifactTemplateId(
            "http://opentosca.org/add/management/to/instances/nodetypeimplementations",
            "MySQL-DBMS_5.7-Install-w1-selfContained-w1-wip1",
            false
        );
        ArtifactTemplateId generatedDAId = new ArtifactTemplateId(
            "http://opentosca.org/add/management/to/instances/nodetypeimplementations",
            "MySQL-DBMS_5.7-Install-w1-mysql-server-5.7-DA-w1-wip1",
            false
        );

        assertNotNull(generatedArtifacts);
        assertEquals(1, generatedArtifacts.deploymentArtifactsToAdd.size());
        assertEquals(generatedDAId.getQName(), generatedArtifacts.deploymentArtifactsToAdd.get(0));
        assertEquals(ToscaBaseTypes.archiveArtifactType, repository.getElement(generatedDAId).getType());
        assertEquals(originalQName, generatedArtifacts.artifactToReplaceQName);
        assertEquals(selfContainedId.getQName(), generatedArtifacts.selfContainedArtifactQName);

        assertTrue(repository.exists(selfContainedId));
        assertTrue(repository.exists(generatedDAId));

        ArtifactTemplateFilesDirectoryId installScriptId = new ArtifactTemplateFilesDirectoryId(selfContainedId);
        SortedSet<RepositoryFileReference> containedFiles = repository.getContainedFiles(installScriptId);
        assertEquals(1, containedFiles.size());
        String targetFileStr = IOUtils.toString(repository.newInputStream(containedFiles.first()), StandardCharsets.UTF_8);
        assertEquals(
            "#!/bin/sh\n" +
                "sudo sh -c \"echo '127.0.0.1' $(hostname) >> /etc/hosts\";\n" +
                "sudo apt-get update -qq;\n" +
                "# disables setting the root password with gui, root password etc. will be set in the configure.sh\n" +
                "export DEBIAN_FRONTEND=noninteractive;\n" +
                "#//Self-contained CSAR//mysql-server-5.7.tar.gz\n" +
                "innerCsarRoot=$(find ~ -maxdepth 1 -path \"*.csar\");\n" +
                "IFS=';' read -ra NAMES <<< \"$DAs\";\n" +
                "for k in \"${NAMES[@]}\"; do\n" +
                "    IFS=',' read -ra PATH <<< \"$k\"; \n" +
                "    selfContainedDaDirName=$(/usr/bin/sudo /usr/bin/dirname $innerCsarRoot${PATH[1]})\n" +
                "    selfContainedDaName=$(/usr/bin/sudo /usr/bin/basename $innerCsarRoot${PATH[1]})\n" +
                "    selfContainedExtractFolderName=\"${selfContainedDaName%.*}\"\n" +
                "    if [[ \"${PATH[1]}\" == *mysql-server-5.7.tar.gz ]];\n" +
                "    then\n" +
                "        /usr/bin/sudo mkdir -p \"$selfContainedDaDirName/$selfContainedExtractFolderName\"\n" +
                "        /bin/tar  -xvzf \"$selfContainedDaDirName/$selfContainedDaName\" -C \"$selfContainedDaDirName/$selfContainedExtractFolderName\"\n" +
                "        break\n" +
                "    fi\n" +
                "done\n" +
                "export DEBIAN_FRONTEND=noninteractive\n" +
                "/usr/bin/sudo -E /usr/bin/dpkg -i -R -E -B \"$selfContainedDaDirName/$selfContainedExtractFolderName\"\n",
            targetFileStr);

        ArtifactTemplateFilesDirectoryId daId = new ArtifactTemplateFilesDirectoryId(generatedDAId);
        containedFiles = repository.getContainedFiles(daId);
        assertEquals(1, containedFiles.size());
        assertEquals("mysql-server-5.7.tar.gz", containedFiles.first().getFileName());
    }
}
