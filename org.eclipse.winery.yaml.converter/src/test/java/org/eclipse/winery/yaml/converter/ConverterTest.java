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

package org.eclipse.winery.yaml.converter;

import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.repository.TestWithGitBackedRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConverterTest extends TestWithGitBackedRepository {

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

    private Converter converter;

    @BeforeEach
    public void initializeConverter() throws Exception {
        this.setRevisionTo("origin/plain");
        converter = new Converter();
    }

    @Test
    public void convertServiceTemplateWithTwoNodeTemplatesToYaml() throws Exception {
        ServiceTemplateId id = new ServiceTemplateId("http://plain.winery.opentosca.org/servicetemplates", "ServiceTemplateWithTwoNodeTemplates_w2-wip2", false);

        String yamlRepresentation = converter.convertDefinitionsChildToYaml(id);

        assertEquals("tosca_definitions_version: tosca_simple_yaml_1_1\n" +
            "\n" +
            "metadata:\n" +
            "  targetNamespace: http://plain.winery.opentosca.org/servicetemplates\n" +
            "imports:\n" +
            "  - NodeTypeWithTwoKVProperties.yml:\n" +
            "      file: NodeType/http%3A%2F%2Fplain.winery.opentosca.org%2Fnodetypes/NodeTypeWithTwoKVProperties.yml\n" +
            "      namespace_uri: http://plain.winery.opentosca.org/nodetypes\n" +
            "      namespace_prefix: nodetypes\n" +
            "  - NodeTypeWithoutProperties.yml:\n" +
            "      file: NodeType/http%3A%2F%2Fplain.winery.opentosca.org%2Fnodetypes/NodeTypeWithoutProperties.yml\n" +
            "      namespace_uri: http://plain.winery.opentosca.org/nodetypes\n" +
            "      namespace_prefix: nodetypes\n" +
            "  - RelationshipTypeWithoutProperties.yml:\n" +
            "      file: RelationshipType/http%3A%2F%2Fplain.winery.opentosca.org%2Frelationshiptypes/RelationshipTypeWithoutProperties.yml\n" +
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
            "      type: relationshiptypes:RelationshipTypeWithoutProperties\n", yamlRepresentation);
    }

    @Test
    public void convertNodeTypeWithoutVPropertiesWip2ToYaml() throws Exception {
        NodeTypeId id = new NodeTypeId("http://plain.winery.opentosca.org/nodetypes", "NodeTypeWithoutProperties", false);
        String yamlRepresentation = converter.convertDefinitionsChildToYaml(id);

        assertEquals(nodeTypeWithoutPropertiesAsYaml, yamlRepresentation);
    }

    @Test
    public void convertNodeTypeWithTwoKVPropertiesWip2ToYaml() throws Exception {
        NodeTypeId id = new NodeTypeId("http://plain.winery.opentosca.org/nodetypes", "NodeTypeWithTwoKVProperties-wip2", false);
        // FIXME: the converter does not convert the K/V properties of the node type
        String yamlRepresentation = converter.convertDefinitionsChildToYaml(id);

        assertEquals(nodeTypeWithTwoKVPropertiesWip2AsYaml, yamlRepresentation);
    }
}
