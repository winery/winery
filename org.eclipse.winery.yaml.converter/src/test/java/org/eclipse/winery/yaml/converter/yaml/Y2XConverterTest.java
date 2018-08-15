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
import java.nio.file.Paths;

import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.XmlId;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.yaml.common.Utils;
import org.eclipse.winery.yaml.common.reader.yaml.Reader;

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
        "  NodeTypeWithTwoKVProperties:\n" +
        "    metadata:\n" +
        "      targetNamespace: http://plain.winery.opentosca.org/nodetypes\n" +
        "      abstract: no\n" +
        "      final: no\n" +
        "    properties:\n" +
        "      key1:\n" +
        "        type: string\n" +
        "        required: true\n" +
        "        status: supported\n" +
        "      key2:\n" +
        "        type: string\n" +
        "        required: false\n" +
        "        status: supported";

    private static final String relationshipTypeWithoutPropertiesYaml = "tosca_definitions_version: tosca_simple_yaml_1_1\n" +
        "\n" +
        "relationship_types:\n" +
        "  RelationshipTypeWithoutProperties:\n" +
        "    metadata:\n" +
        "      targetNamespace: http://plain.winery.opentosca.org/relationshiptypes\n" +
        "      abstract: no\n" +
        "      final: no\n";

    /**
     * @return the Path to the serviceTemplate.yml file
     */
    private Path initializeServiceTemplateWithTwoNodeTemplatesW2wip2() throws Exception {
        Path workdir = Files.createTempDirectory("winery-yaml-tests");
        Files.write(workdir.resolve("NodeTypeWithoutProperties.yml"), nodeTypeWithoutPropertiesAsYaml.getBytes());
        Files.write(workdir.resolve("NodeTypeWithTwoKVProperties-wip2.yml"), nodeTypeWithTwoKVPropertiesWip2AsYaml.getBytes());
        Files.write(workdir.resolve("RelationshipTypeWithoutProperties.yml"), relationshipTypeWithoutPropertiesYaml.getBytes());

        String serviceTemplateYaml = "tosca_definitions_version: tosca_simple_yaml_1_1\n" +
            "\n" +
            "metadata:\n" +
            "  targetNamespace: http://plain.winery.opentosca.org/servicetemplates\n" +
            "imports:\n" +
            "  - NodeTypeWithTwoKVProperties.yml:\n" +
            "      file: NodeTypeWithTwoKVProperties-wip2.yml\n" +
            "      namespace_uri: http://plain.winery.opentosca.org/nodetypes\n" +
            "      namespace_prefix: nodetypes\n" +
            "  - NodeTypeWithoutProperties.yml:\n" +
            "      file: NodeTypeWithoutProperties.yml\n" +
            "      namespace_uri: http://plain.winery.opentosca.org/nodetypes\n" +
            "      namespace_prefix: nodetypes\n" +
            "  - RelationshipTypeWithoutProperties.yml:\n" +
            "      file: RelationshipTypeWithoutProperties.yml\n" +
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
        Path serviceTemplateYamlFile = workdir.resolve("ServiceTemplate.yml");
        Files.write(serviceTemplateYamlFile, serviceTemplateYaml.getBytes());

        return serviceTemplateYamlFile;
    }

    @Test
    public void testDataReadCorrectly() throws Exception {
        Reader reader = Reader.getReader();
        Path serviceTemplateYamlFile = this.initializeServiceTemplateWithTwoNodeTemplatesW2wip2();
        TServiceTemplate serviceTemplate = reader.parse(serviceTemplateYamlFile.getParent(), serviceTemplateYamlFile, "http://plain.winery.opentosca.org/servicetemplates");
        assertNotNull(serviceTemplate);
    }

    @Test
    public void convertServiceTemplateWithTwoNodeTemplatesToXml() throws Exception {
        this.setRevisionTo("origin/plain");

        Path tmpDir = Utils.getTmpDir(Paths.get("convertSTWithTwoNodeTemplatesToXml"));

        Reader reader = Reader.getReader();
        Path serviceTemplateYamlFile = this.initializeServiceTemplateWithTwoNodeTemplatesW2wip2();

        String namespace = "http://plain.winery.opentosca.org/servicetemplates";
        String id = "ServiceTemplateWithTwoNodeTemplates_w2-wip2";
        TServiceTemplate serviceTemplate = reader.parse(serviceTemplateYamlFile.getParent(), serviceTemplateYamlFile, namespace);

        Y2XConverter y2xConverter = new Y2XConverter(this.repository);
        y2xConverter.convert(
            serviceTemplate,
            id,
            namespace,
            serviceTemplateYamlFile.getParent(),
            tmpDir.resolve("xml")
        );

        org.eclipse.winery.model.tosca.TServiceTemplate xmlServiceTemplate = repository.getElement(new ServiceTemplateId(new Namespace(namespace, false), new XmlId(id, false)));
        assertEquals("<ServiceTemplate name=\"ServiceTemplateWithTwoNodeTemplates_w2-wip2\" targetNamespace=\"http://plain.winery.opentosca.org/servicetemplates\" id=\"ServiceTemplateWithTwoNodeTemplates_w2-wip2\" xmlns=\"http://docs.oasis-open.org/tosca/ns/2011/12\" xmlns:selfservice=\"http://www.eclipse.org/winery/model/selfservice\" xmlns:winery=\"http://www.opentosca.org/winery/extensions/tosca/2013/02/12\" xmlns:testwineryopentoscaorg=\"http://test.winery.opentosca.org\">\n" +
            "    <BoundaryDefinitions/>\n" +
            "    <TopologyTemplate>\n" +
            "        <NodeTemplate name=\"NodeTypeWithTwoKVProperties\" type=\"nodetypes:NodeTypeWithTwoKVProperties\" id=\"NodeTypeWithTwoKVProperties\" xmlns:nodetypes=\"http://plain.winery.opentosca.org/nodetypes\">\n" +
            "            <Properties>\n" +
            "                <Properties xmlns=\"http://plain.winery.opentosca.org/nodetypes\" xmlns:tosca=\"http://docs.oasis-open.org/tosca/ns/2011/12\" xmlns:ns4=\"http://plain.winery.opentosca.org/nodetypes\" xmlns:ns3=\"http://www.opentosca.org/winery/extensions/tosca/2013/02/12\">\n" +
            "                    <ns4:key1>MyKeyElement</ns4:key1>\n" +
            "                    <ns4:key2/>\n" +
            "                    </Properties>\n" +
            "            </Properties>\n" +
            "        </NodeTemplate>\n" +
            "        <NodeTemplate name=\"NodeTypeWithoutProperties\" type=\"nodetypes:NodeTypeWithoutProperties\" id=\"NodeTypeWithoutProperties\" xmlns:nodetypes=\"http://plain.winery.opentosca.org/nodetypes\">\n" +
            "            <Properties>\n" +
            "                <Properties xmlns=\"http://plain.winery.opentosca.org/nodetypes\" xmlns:tosca=\"http://docs.oasis-open.org/tosca/ns/2011/12\" xmlns:ns4=\"http://plain.winery.opentosca.org/nodetypes\" xmlns:ns3=\"http://www.opentosca.org/winery/extensions/tosca/2013/02/12\"/>\n" +
            "            </Properties>\n" +
            "        </NodeTemplate>\n" +
            "        <RelationshipTemplate name=\"NodeTypeWithTwoKVProperties_RelationshipTypeWithoutProperties_NodeTypeWithoutProperties\" type=\"relationshiptypes:RelationshipTypeWithoutProperties\" id=\"NodeTypeWithTwoKVProperties_RelationshipTypeWithoutProperties_NodeTypeWithoutProperties\" xmlns:relationshiptypes=\"http://plain.winery.opentosca.org/relationshiptypes\">\n" +
            "            <SourceElement ref=\"NodeTypeWithTwoKVProperties\"/>\n" +
            "            <TargetElement ref=\"NodeTypeWithoutProperties\"/>\n" +
            "        </RelationshipTemplate>\n" +
            "    </TopologyTemplate>\n" +
            "</ServiceTemplate>", BackendUtils.getXMLAsString(xmlServiceTemplate));

        assertEquals("<NodeType name=\"NodeTypeWithoutProperties\" targetNamespace=\"http://plain.winery.opentosca.org/nodetypes\" xmlns=\"http://docs.oasis-open.org/tosca/ns/2011/12\" xmlns:selfservice=\"http://www.eclipse.org/winery/model/selfservice\" xmlns:winery=\"http://www.opentosca.org/winery/extensions/tosca/2013/02/12\" xmlns:testwineryopentoscaorg=\"http://test.winery.opentosca.org\">\n" +
                "    <Tags>\n" +
                "        <Tag name=\"targetNamespace\" value=\"http://plain.winery.opentosca.org/nodetypes\"/>\n" +
                "        <Tag name=\"abstract\" value=\"false\"/>\n" +
                "        <Tag name=\"final\" value=\"false\"/>\n" +
                "    </Tags>\n" +
                "</NodeType>", BackendUtils.getXMLAsString(repository.getElement(
            new NodeTypeId(
                new Namespace("http://plain.winery.opentosca.org/nodetypes", false),
                new XmlId("NodeTypeWithoutProperties", false)))
            )
        );

        assertEquals("<NodeType name=\"NodeTypeWithTwoKVProperties\" targetNamespace=\"http://plain.winery.opentosca.org/nodetypes\" xmlns=\"http://docs.oasis-open.org/tosca/ns/2011/12\" xmlns:selfservice=\"http://www.eclipse.org/winery/model/selfservice\" xmlns:winery=\"http://www.opentosca.org/winery/extensions/tosca/2013/02/12\" xmlns:testwineryopentoscaorg=\"http://test.winery.opentosca.org\">\n" +
                "    <winery:PropertiesDefinition>\n" +
                "        <winery:properties>\n" +
                "            <winery:key>key1</winery:key>\n" +
                "            <winery:type>xsd:string</winery:type>\n" +
                "        </winery:properties>\n" +
                "        <winery:properties>\n" +
                "            <winery:key>key2</winery:key>\n" +
                "            <winery:type>xsd:string</winery:type>\n" +
                "        </winery:properties>\n" +
                "    </winery:PropertiesDefinition>\n" +
                "    <Tags>\n" +
                "        <Tag name=\"targetNamespace\" value=\"http://plain.winery.opentosca.org/nodetypes\"/>\n" +
                "        <Tag name=\"abstract\" value=\"false\"/>\n" +
                "        <Tag name=\"final\" value=\"false\"/>\n" +
                "    </Tags>\n" +
                "    <ns5:PropertiesDefinition element=\"NodeTypeWithTwoKVProperties_Properties\" xmlns=\"\" xmlns:ns5=\"http://docs.oasis-open.org/tosca/ns/2011/12\"/>\n" +
                "</NodeType>", BackendUtils.getXMLAsString(repository.getElement(
            new NodeTypeId(
                new Namespace("http://plain.winery.opentosca.org/nodetypes", false),
                new XmlId("NodeTypeWithTwoKVProperties", false)))
            )
        );

        assertEquals("<RelationshipType name=\"RelationshipTypeWithoutProperties\" targetNamespace=\"http://plain.winery.opentosca.org/relationshiptypes\" xmlns=\"http://docs.oasis-open.org/tosca/ns/2011/12\" xmlns:selfservice=\"http://www.eclipse.org/winery/model/selfservice\" xmlns:winery=\"http://www.opentosca.org/winery/extensions/tosca/2013/02/12\" xmlns:testwineryopentoscaorg=\"http://test.winery.opentosca.org\">\n" +
                "    <Tags>\n" +
                "        <Tag name=\"targetNamespace\" value=\"http://plain.winery.opentosca.org/relationshiptypes\"/>\n" +
                "        <Tag name=\"abstract\" value=\"false\"/>\n" +
                "        <Tag name=\"final\" value=\"false\"/>\n" +
                "    </Tags>\n" +
                "</RelationshipType>", BackendUtils.getXMLAsString(repository.getElement(
            new RelationshipTypeId(
                new Namespace("http://plain.winery.opentosca.org/relationshiptypes", false),
                new XmlId("RelationshipTypeWithoutProperties", false)))
            )
        );
    }

    @Test
    public void testDataReadCorrectlyWithoutValidation() throws Exception {
        Reader reader = Reader.getReader();
        Path serviceTemplateYamlFile = this.initializeServiceTemplateWithTwoNodeTemplatesW2wip2();
        TServiceTemplate serviceTemplate = reader.parseSkipTest(serviceTemplateYamlFile, "http://plain.winery.opentosca.org/servicetemplates");
        assertNotNull(serviceTemplate);
    }
}
