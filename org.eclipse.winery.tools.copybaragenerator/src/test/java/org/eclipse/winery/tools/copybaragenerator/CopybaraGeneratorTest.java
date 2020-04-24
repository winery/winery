/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.tools.copybaragenerator;

import org.eclipse.winery.repository.TestWithGitBackedRepository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
class CopybaraGeneratorTest extends TestWithGitBackedRepository {

    @Test
    public void generatedConfigHasCorrectStrings() throws Exception {
        // we use the commit 4b218e6 instead of origin/plain, because the number of definitions changes fast
        this.setRevisionTo("4b218e6");
        CopybaraGenerator copybaraGenerator = new CopybaraGenerator();
        String config = copybaraGenerator.generateCopybaraConfigFile();
        assertEquals("urlOrigin = \"https://github.com/OpenTOSCA/tosca-definitions-internal.git\"\n" +
            "urlDestination = \"file:///tmp/copybara/tosca-definitions-public\"\n" +
            "core.workflow(\n" +
            "    name = \"default\",\n" +
            "    origin = git.origin(\n" +
            "        url = urlOrigin,\n" +
            "        ref = \"master\",\n" +
            "    ),\n" +
            "    destination = git.destination(\n" +
            "        url = urlDestination,\n" +
            "        fetch = \"master\",\n" +
            "        push = \"master\",\n" +
            "    ),\n" +
            "    authoring = authoring.pass_thru(\"OpenTOSCA Bot <opentosca@iaas.uni-stuttgart.de>\"),\n" +
            "    origin_files = glob([\"README.md\", \"LICENSE\", \"artifacttemplates/http%3A%2F%2Fplain.winery.opentosca.org%2Fartifacttemplates/ArtifactTemplateWithFilesAndSources-ArtifactTypeWithoutProperties/**\",\n" +
            "        \"artifacttemplates/http%3A%2F%2Fplain.winery.opentosca.org%2Fartifacttemplates/ArtifactTemplateWithoutAnyFiles-ArtifactTypeWithoutProperties/**\"]),\n" +
            "    destination_files = glob([\"**\"], exclude = [\"README_INTERNAL.md\"]),\n" +
            ")", config);
    }

    @Test
    public void generatedOriginFilesConfigHasCorrectStrings() throws Exception {
        // we use the commit 4b218e6 instead of origin/plain, because the number of definitions changes fast
        this.setRevisionTo("4b218e6");
        CopybaraGenerator copybaraGenerator = new CopybaraGenerator();
        String config = copybaraGenerator.generateOriginFilesConfig();
        assertEquals("origin_files = glob([\"README.md\", \"LICENSE\", \"artifacttemplates/http%3A%2F%2Fplain.winery.opentosca.org%2Fartifacttemplates/ArtifactTemplateWithFilesAndSources-ArtifactTypeWithoutProperties/**\",\n" +
            "        \"artifacttemplates/http%3A%2F%2Fplain.winery.opentosca.org%2Fartifacttemplates/ArtifactTemplateWithoutAnyFiles-ArtifactTypeWithoutProperties/**\"]),", config);
    }
}
