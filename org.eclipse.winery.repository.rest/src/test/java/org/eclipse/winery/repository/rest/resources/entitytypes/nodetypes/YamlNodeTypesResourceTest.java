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

package org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes;

import java.nio.file.Path;

import org.eclipse.winery.common.configuration.RepositoryConfigurationObject;
import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class YamlNodeTypesResourceTest extends AbstractResourceTest {

    public YamlNodeTypesResourceTest() {
        super(RepositoryConfigurationObject.RepositoryProvider.YAML);
    }

    @Test
    public void createNodeType() throws Exception {
        this.setRevisionTo("bab12e7a8ca7af1c0a0ce186c81bab3899ab989b");

        this.assertPost("nodetypes", "entitytypes/nodetypes/addYamlNodeType.json");
        this.assertGetSize("nodetypes", 3);

        Path filePath = this.repositoryPath.resolve("nodetypes/org.example.tosca.nodetypes/myLittleExample_1.0.0-w1-1/NodeType.tosca");
        String fileContent = FileUtils.readFileToString(filePath.toFile(), "UTF-8");

        assertEquals(readFromClasspath("entitytypes/nodetypes/addecYamlNodeType.yml"), fileContent);
    }
}
