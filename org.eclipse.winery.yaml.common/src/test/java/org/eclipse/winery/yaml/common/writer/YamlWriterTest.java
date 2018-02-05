/********************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.yaml.common.writer;

import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.yaml.common.AbstractTest;
import org.eclipse.winery.yaml.common.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;
import java.nio.file.Paths;

public class YamlWriterTest extends AbstractTest {
    private static Path temporaryFolder;

    @BeforeAll
    private static void setPath() throws Exception {
        AbstractTest.path = Paths.get("src/test/resources/builder/simpleTests");
        temporaryFolder = Utils.getTmpDir(Paths.get("winery-yaml"));
    }

    @DisplayName("Test read and write round trip")
    @ParameterizedTest(name = "{index} name=''{0}''")
    @MethodSource("getYamlFiles")
    public void roundTripTest(Path fileName) throws Exception {
        TServiceTemplate serviceTemplate = this.getYamlServiceTemplate(fileName);
        writeYamlServiceTemplate(serviceTemplate, temporaryFolder.resolve(fileName));
        TServiceTemplate out = this.getYamlServiceTemplate(fileName, temporaryFolder);
        Assertions.assertEquals(serviceTemplate, out);
    }

    @DisplayName("Test read and write single round trip")
    @ParameterizedTest(name = "{index} name=''{0}''")
    @ValueSource(strings = {
        "example_16-topology_templates-1_1"
    })
    public void roundTripSingleTest(String file) throws Exception {
        Path fileName = getYamlFile(file);
        TServiceTemplate serviceTemplate = this.getYamlServiceTemplate(fileName);
        writeYamlServiceTemplate(serviceTemplate, temporaryFolder.resolve(fileName));
        TServiceTemplate out = this.getYamlServiceTemplate(fileName, temporaryFolder);
        Assertions.assertEquals(serviceTemplate, out);
    }
}
