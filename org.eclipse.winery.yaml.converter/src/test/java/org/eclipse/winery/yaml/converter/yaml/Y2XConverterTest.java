/*******************************************************************************
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

package org.eclipse.winery.yaml.converter.yaml;

import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.yaml.common.reader.yaml.Reader;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Y2XConverterTest extends TestWithGitBackedRepository {

    private static final String nodeTypeWithoutPropertiesAsYaml = "tosca_definitions_version: tosca_simple_yaml_1_1\n" +
        "\n" +
        "node_types:\n" +
        "  NodeTypeWithoutProperties:\n" +
        "    metadata:\n" +
        "      targetNamespace: http://plain.winery.opentosca.org/nodetypes\n" +
        "      abstract: no\n" +
        "      final: no\n";

    private static final String nodeTypeWithTwoKVPropertiesWip2AsYaml = "tosca_definitions_version: tosca_simple_yaml_1_1\n" +
        "\n" +
        "node_types:\n" +
        "  NodeTypeWithTwoKVProperties-wip2:\n" +
        "    metadata:\n" +
        "      targetNamespace: http://plain.winery.opentosca.org/nodetypes\n" +
        "      abstract: no\n" +
        "      final: no\n";

    /**
     * @return the Path to the serviceTemplate.yaml file
     */
    private Path initializeServiceTemplateWithTwoNodeTemplates_w2wip2() throws Exception {
        Path workdir = Files.createTempDirectory("winery-yaml-tests");
        Files.write(workdir.resolve("NodeTypeWithoutProperties.yaml"), nodeTypeWithoutPropertiesAsYaml.getBytes());
        Files.write(workdir.resolve("NodeTypeWithTwoKVProperties-wip2.yaml"), nodeTypeWithTwoKVPropertiesWip2AsYaml.getBytes());

        String serviceTemplateYaml = "tosca_definitions_version: tosca_simple_yaml_1_1\n" +
            "\n" +
            "metadata:\n" +
            "  targetNamespace: http://plain.winery.opentosca.org/servicetemplates\n" +
            "imports:\n" +
            "  - NodeTypeWithTwoKVProperties.yml:\n" +
            "      file: NodeTypeWithTwoKVProperties-wip2.yaml\n" +
            "      namespace_uri: http://plain.winery.opentosca.org/nodetypes\n" +
            "      namespace_prefix: nodetypes\n" +
            "  - NodeTypeWithoutProperties.yml:\n" +
            "      file: NodeTypeWithoutProperties.yml\n" +
            "      namespace_uri: http://plain.winery.opentosca.org/nodetypes\n" +
            "      namespace_prefix: nodetypes\n" +
            "  - RelationshipTypeWithoutProperties.yml:\n" +
            "      file: RelationshipType\\http%3A%2F%2Fplain.winery.opentosca.org%2Frelationshiptypes\\RelationshipTypeWithoutProperties.yml\n" +
            "      namespace_uri: http://plain.winery.opentosca.org/relationshiptypes\n" +
            "      namespace_prefix: relationshiptypes\n" +
            "topology_template:\n" +
            "  node_templates:\n" +
            "    NodeTypeWithTwoKVProperties:\n" +
            "      type: nodetypes:NodeTypeWithTwoKVProperties\n" +
            "      properties:\n" +
            "        key1: \"MyKeyElement\"\n" +
            "        key2: \"\"\n" +
            "      requirements:\n" +
            "        - NodeTypeWithTwoKVProperties_RelationshipTypeWithoutProperties_NodeTypeWithoutProperties:\n" +
            "            node: NodeTypeWithoutProperties\n" +
            "    NodeTypeWithoutProperties:\n" +
            "      type: nodetypes:NodeTypeWithoutProperties\n" +
            "  relationship_templates:\n" +
            "    NodeTypeWithTwoKVProperties_RelationshipTypeWithoutProperties_NodeTypeWithoutProperties:\n" +
            "      type: relationshiptypes:RelationshipTypeWithoutProperties\n";
        Path serviceTemplateYamlFile = workdir.resolve("ServiceTemplate.yaml");
        Files.write(serviceTemplateYamlFile, serviceTemplateYaml.getBytes());
        
        return serviceTemplateYamlFile;
    }
    
    @Test
    @Disabled("Imports of files of current directory does not work")
    public void testDataReadCorrectly() throws Exception {
        Reader reader = Reader.getReader();
        Path serviceTemplateYamlFile = this.initializeServiceTemplateWithTwoNodeTemplates_w2wip2();
        TServiceTemplate serviceTemplate = reader.parse(serviceTemplateYamlFile.getParent(), serviceTemplateYamlFile, "http://plain.winery.opentosca.org/servicetemplates");
        assertNotNull(serviceTemplate);
    }

    @Test
    @Disabled("Imports of files of current directory does not work")
    public void convertServiceTemplateWithTwoNodeTemplatesToXml() throws Exception {
        this.setRevisionTo("origin/plain");

        Reader reader = Reader.getReader();
        Path serviceTemplateYamlFile = this.initializeServiceTemplateWithTwoNodeTemplates_w2wip2();
        TServiceTemplate serviceTemplate = reader.parse(serviceTemplateYamlFile.getParent(), serviceTemplateYamlFile, "http://plain.winery.opentosca.org/servicetemplates");

        Y2XConverter y2xConverter = new Y2XConverter(this.repository);
        org.eclipse.winery.model.tosca.TServiceTemplate xmlServiceTemplate = y2xConverter.convertServiceTemplate(serviceTemplate, "ServiceTemplateWithTwoNodeTemplates_w2-wip2", "http://plain.winery.opentosca.org/servicetemplates");

        assertEquals("", BackendUtils.getXMLAsString(xmlServiceTemplate));
    }

    @Test
    public void testDataReadCorrectlyWithoutValidation() throws Exception {
        Reader reader = Reader.getReader();
        Path serviceTemplateYamlFile = this.initializeServiceTemplateWithTwoNodeTemplates_w2wip2();
        TServiceTemplate serviceTemplate = reader.parseSkipTest(serviceTemplateYamlFile, "http://plain.winery.opentosca.org/servicetemplates");
        assertNotNull(serviceTemplate);
    }

    @Test
    public void convertServiceTemplateWithTwoNodeTemplatesToXmlWithoutValidation() throws Exception {
        this.setRevisionTo("origin/plain");

        Reader reader = Reader.getReader();
        Path serviceTemplateYamlFile = this.initializeServiceTemplateWithTwoNodeTemplates_w2wip2();
        TServiceTemplate serviceTemplate = reader.parseSkipTest(serviceTemplateYamlFile, "http://plain.winery.opentosca.org/servicetemplates");

        Y2XConverter y2xConverter = new Y2XConverter(this.repository);
        org.eclipse.winery.model.tosca.TServiceTemplate xmlServiceTemplate = y2xConverter.convertServiceTemplate(serviceTemplate, "ServiceTemplateWithTwoNodeTemplates_w2-wip2", "http://plain.winery.opentosca.org/servicetemplates");

        assertEquals("", BackendUtils.getXMLAsString(xmlServiceTemplate));
    }

}
