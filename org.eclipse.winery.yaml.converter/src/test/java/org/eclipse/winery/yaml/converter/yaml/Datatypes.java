/********************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.yaml.converter.yaml;

import java.nio.file.Paths;

import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.yaml.converter.yaml.support.AbstractTestY2X;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Datatypes extends AbstractTestY2X {

    public Datatypes() {
        super(Paths.get("src/test/resources/yaml/Datatypes"));
    }

    @Test
    public void testDataTypes() throws Exception {
        String name = "data_types";
        String namespace = "http://www.example.com/DataTypesTest";

        TServiceTemplate serviceTemplate = readServiceTemplate(name, namespace);
        Definitions definitions = convert(serviceTemplate, name, namespace);
        writeXml(definitions, name, namespace);

        assertNotNull(definitions);
    }

    @Test
    public void testDataTypesWithImport() throws Exception {
        String name = "data_types-with_import";
        String namespace = "http://www.example.com/DataTypesWithImportTest";

        TServiceTemplate serviceTemplate = readServiceTemplate(name, namespace);
        Definitions definitions = convert(serviceTemplate, name, namespace);
        writeXml(definitions, name, namespace);

        assertNotNull(definitions);
    }

    @Test
    public void testDataTypesRecursive() throws Exception {
        String name = "data_types-recursive";
        String namespace = "http://www.example.com/DataTypesRecursive";

        TServiceTemplate serviceTemplate = readServiceTemplate(name, namespace);
        Definitions definitions = convert(serviceTemplate, name, namespace);
        writeXml(definitions, name, namespace);

        assertNotNull(definitions);
    }

    @Test
    public void testNodeTemplateWithDataTypes() throws Exception {
        String name = "node_template-using-data_types";
        String namespace = "http://www.example.com/NodeTemplateUsingDataType";

        TServiceTemplate serviceTemplate = readServiceTemplate(name, namespace);
        Definitions definitions = convert(serviceTemplate, name, namespace);
        writeXml(definitions, name, namespace);

        assertNotNull(definitions);
    }
}
