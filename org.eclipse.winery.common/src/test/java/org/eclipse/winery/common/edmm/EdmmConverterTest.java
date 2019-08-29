/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.common.edmm;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TInterfaces;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.kvproperties.PropertyDefinitionKV;
import org.eclipse.winery.model.tosca.kvproperties.PropertyDefinitionKVList;
import org.eclipse.winery.model.tosca.kvproperties.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;

import io.github.edmm.core.parser.Entity;
import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.core.parser.ScalarEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EdmmConverterTest {

    private static final String NAMESPACE = "https://ex.org/tosca/to/edmm";
    private static final HashMap<QName, TNodeType> nodeTypes = new HashMap<>();
    private static final HashMap<QName, TRelationshipType> relationshipTypes = new HashMap<>();
    private static final HashMap<String, TNodeTemplate> nodeTemplates = new HashMap<>();
    private static final HashMap<String, TRelationshipTemplate> relationshipTemplates = new HashMap<>();
    private static final HashMap<QName, TNodeTypeImplementation> nodeTypeImplementations = new HashMap<>();
    private static final HashMap<QName, TRelationshipTypeImplementation> relationshipTypeImplementations = new HashMap<>();
    private static final HashMap<QName, TArtifactTemplate> artifactTemplates = new HashMap<>();
    private static final HashMap<QName, EdmmType> edmmTypeExtendsMapping = new HashMap<>();
    private static final HashMap<QName, EdmmType> edmm1to1Mapping = new HashMap<>();

    @BeforeEach
    void setup() {
        // region *** NodeType setup ***
        QName nodeType1QName = QName.valueOf("{" + NAMESPACE + "}" + "test_node_type");
        TNodeType nodeType1 = new TNodeType();
        nodeType1.setName(nodeType1QName.getLocalPart());
        nodeType1.setTargetNamespace(nodeType1QName.getNamespaceURI());
        nodeTypes.put(nodeType1QName, nodeType1);

        QName nodeType2QName = QName.valueOf("{" + NAMESPACE + "}" + "test_node_type_2");
        TNodeType nodeType2 = new TNodeType();
        nodeType2.setName(nodeType2QName.getLocalPart());
        nodeType2.setTargetNamespace(nodeType2QName.getNamespaceURI());
        TEntityType.DerivedFrom derivedFrom = new TNodeType.DerivedFrom();
        derivedFrom.setTypeRef(nodeType1QName);
        nodeType2.setDerivedFrom(derivedFrom);
        nodeTypes.put(nodeType2QName, nodeType2);

        QName nodeType3QName = QName.valueOf("{" + NAMESPACE + "}" + "test_node_type_3");
        TNodeType nodeType3 = new TNodeType();
        nodeType3.setName(nodeType3QName.getLocalPart());
        nodeType3.setTargetNamespace(nodeType3QName.getNamespaceURI());
        PropertyDefinitionKVList kvList = new PropertyDefinitionKVList();
        kvList.add(new PropertyDefinitionKV("os_family", "xsd:string"));
        kvList.add(new PropertyDefinitionKV("public_key", "xsd:string"));
        kvList.add(new PropertyDefinitionKV("ssh_port", "number"));
        WinerysPropertiesDefinition wpd = new WinerysPropertiesDefinition();
        wpd.setPropertyDefinitionKVList(kvList);
        ModelUtilities.replaceWinerysPropertiesDefinition(nodeType3, wpd);
        nodeTypes.put(nodeType3QName, nodeType3);

        QName nodeType4QName = QName.valueOf("{" + NAMESPACE + "}" + "test_node_type_4");
        TNodeType nodeType4 = new TNodeType();
        nodeType4.setName(nodeType4QName.getLocalPart());
        nodeType4.setTargetNamespace(nodeType4QName.getNamespaceURI());
        TOperation start = new TOperation();
        start.setName("start");
        TOperation stop = new TOperation();
        stop.setName("stop");
        TInterface lifecycle = new TInterface();
        lifecycle.setName("lifecycle_interface");
        lifecycle.getOperation().add(start);
        lifecycle.getOperation().add(stop);
        TInterfaces tInterfaces = new TInterfaces();
        tInterfaces.getInterface().add(lifecycle);
        nodeType4.setInterfaces(tInterfaces);
        nodeTypes.put(nodeType4QName, nodeType4);
        // endregion

        // region *** ArtifactTemplates setup ***
        TArtifactReference startArtifactReference = new TArtifactReference();
        startArtifactReference.setReference("/artifacttemplates/ns/startTestNode4/files/script.sh");
        TArtifactTemplate startArtifactTemplate = new TArtifactTemplate();
        TArtifactTemplate.ArtifactReferences startArtifactReferences = new TArtifactTemplate.ArtifactReferences();
        startArtifactReferences.getArtifactReference().add(startArtifactReference);
        startArtifactTemplate.setArtifactReferences(startArtifactReferences);
        TArtifactTemplate startArtifactIA = new TArtifactTemplate();
        QName startArtifactIAQName = QName.valueOf("{" + NAMESPACE + "}" + "Start_IA");
        startArtifactIA.setName(startArtifactIAQName.getLocalPart());
        startArtifactIA.setArtifactReferences(startArtifactReferences);
        artifactTemplates.put(startArtifactIAQName, startArtifactIA);

        TArtifactReference stopArtifactReference = new TArtifactReference();
        stopArtifactReference.setReference("/artifacttemplates/ns/stopTestNode4/files/script.sh");
        TArtifactTemplate stopArtifactTemplate = new TArtifactTemplate();
        TArtifactTemplate.ArtifactReferences stopArtifactReferences = new TArtifactTemplate.ArtifactReferences();
        stopArtifactReferences.getArtifactReference().add(startArtifactReference);
        stopArtifactTemplate.setArtifactReferences(stopArtifactReferences);
        TArtifactTemplate stopArtifactIA = new TArtifactTemplate();
        QName stopArtifactIAQName = QName.valueOf("{" + NAMESPACE + "}" + "Stop_IA");
        stopArtifactIA.setName(stopArtifactIAQName.getLocalPart());
        stopArtifactIA.setArtifactReferences(stopArtifactReferences);
        artifactTemplates.put(stopArtifactIAQName, stopArtifactIA);
        // endregion

        // region *** NodeTypeImplementations setup ***
        TImplementationArtifacts artifacts = new TImplementationArtifacts();
        QName nodeTypeImpl4QName = QName.valueOf("{" + NAMESPACE + "}" + "test_node_type_Impl_4");
        TNodeTypeImplementation nodeTypeImpl4 = new TNodeTypeImplementation();
        nodeTypeImpl4.setNodeType(nodeType4QName);
        nodeTypeImpl4.setName(nodeTypeImpl4QName.getLocalPart());
        TImplementationArtifacts.ImplementationArtifact startArtifact = new TImplementationArtifacts.ImplementationArtifact();
        startArtifact.setArtifactRef(startArtifactIAQName);
        startArtifact.setInterfaceName("lifecycle_interface");
        startArtifact.setOperationName("start");
        TImplementationArtifacts.ImplementationArtifact stopArtifact = new TImplementationArtifacts.ImplementationArtifact();
        stopArtifact.setArtifactRef(stopArtifactIAQName);
        stopArtifact.setInterfaceName("lifecycle_interface");
        stopArtifact.setOperationName("stop");
        artifacts.getImplementationArtifact().add(startArtifact);
        artifacts.getImplementationArtifact().add(stopArtifact);
        nodeTypeImpl4.setImplementationArtifacts(artifacts);
        nodeTypeImplementations.put(nodeTypeImpl4QName, nodeTypeImpl4);

        // endregion

        // region *** RelationType setup ***
        QName hostedOnQName = QName.valueOf("{" + NAMESPACE + "}" + "hostedOn");
        TRelationshipType hostedOnType = new TRelationshipType();
        hostedOnType.setName(hostedOnQName.getLocalPart());
        hostedOnType.setTargetNamespace(hostedOnQName.getNamespaceURI());
        relationshipTypes.put(hostedOnQName, hostedOnType);

        QName connectsToQName = QName.valueOf("{" + NAMESPACE + "}" + "connectsTo");
        TRelationshipType connectsToType = new TRelationshipType();
        connectsToType.setName(connectsToQName.getLocalPart());
        connectsToType.setTargetNamespace(connectsToQName.getNamespaceURI());
        relationshipTypes.put(connectsToQName, connectsToType);
        // endregion

        // region *** create NodeTemplates ***
        TNodeTemplate nt1 = new TNodeTemplate();
        nt1.setType(nodeType1QName);
        nt1.setId("test_node_1");
        nodeTemplates.put(nt1.getId(), nt1);

        TNodeTemplate nt2 = new TNodeTemplate();
        nt2.setType(nodeType2QName);
        nt2.setId("test_node_2");
        nodeTemplates.put(nt2.getId(), nt2);

        TNodeTemplate nt3 = new TNodeTemplate();
        nt3.setType(nodeType3QName);
        nt3.setId("test_node_3");
        TEntityTemplate.Properties properties = new TEntityTemplate.Properties();
        HashMap<String, String> nt3Properties = new HashMap<>();
        nt3Properties.put("os_family", "ubuntu");
        nt3Properties.put("public_key", "-----BEGIN PUBLIC KEY----- ... -----END PUBLIC KEY-----");
        nt3Properties.put("ssh_port", "22");
        properties.setKVProperties(nt3Properties);
        nt3.setProperties(properties);
        nodeTemplates.put(nt3.getId(), nt3);

        TNodeTemplate nt4 = new TNodeTemplate();
        nt4.setType(nodeType4QName);
        nt4.setId("test_node_4");
        nodeTemplates.put(nt4.getId(), nt4);
        // endregion 

        // region *** create RelationshipTemplate ***
        TRelationshipTemplate rt13 = new TRelationshipTemplate();
        rt13.setType(hostedOnQName);
        rt13.setId("1_hosted_on_3");
        rt13.setSourceNodeTemplate(nt1);
        rt13.setTargetNodeTemplate(nt3);
        relationshipTemplates.put(rt13.getId(), rt13);

        TRelationshipTemplate rt23 = new TRelationshipTemplate();
        rt23.setType(hostedOnQName);
        rt23.setId("2_hosted_on_3");
        rt23.setSourceNodeTemplate(nt2);
        rt23.setTargetNodeTemplate(nt3);
        relationshipTemplates.put(rt23.getId(), rt23);

        TRelationshipTemplate rt41 = new TRelationshipTemplate();
        rt41.setType(hostedOnQName);
        rt41.setId("4_hosted_on_1");
        rt41.setSourceNodeTemplate(nt4);
        rt41.setTargetNodeTemplate(nt1);
        relationshipTemplates.put(rt41.getId(), rt41);

        TRelationshipTemplate rt12 = new TRelationshipTemplate();
        rt12.setType(connectsToQName);
        rt12.setId("1_connects_to_2");
        rt12.setSourceNodeTemplate(nt1);
        rt12.setTargetNodeTemplate(nt2);
        relationshipTemplates.put(rt12.getId(), rt12);
        // endregion

        // region *** create edmm type mapping ***
        edmm1to1Mapping.put(nodeType1QName, EdmmType.SOFTWARE_COMPONENT);
        // edmmTypeMapping.put(nodeType2QName, EdmmType.SOFTWARE_COMPONENT);
        edmmTypeExtendsMapping.put(nodeType3QName, EdmmType.COMPUTE);
        edmmTypeExtendsMapping.put(nodeType4QName, EdmmType.WEB_APPLICATION);
        edmm1to1Mapping.put(hostedOnQName, EdmmType.HOSTED_ON);
        edmm1to1Mapping.put(connectsToQName, EdmmType.CONNECTS_TO);
        // endregion
    }

    @Test
    void transformOneNodeTemplate() {
        // region *** build the TopologyTemplate ***
        TTopologyTemplate topology = new TTopologyTemplate();
        topology.addNodeTemplate(nodeTemplates.get("test_node_1"));
        // endregion

        TServiceTemplate serviceTemplate = new TServiceTemplate();
        serviceTemplate.setTopologyTemplate(topology);

        EdmmConverter edmmConverter = new EdmmConverter(nodeTypes, relationshipTypes, nodeTypeImplementations,
            relationshipTypeImplementations, artifactTemplates, edmmTypeExtendsMapping, edmm1to1Mapping);
        EntityGraph transform = edmmConverter.transform(serviceTemplate);

        assertNotNull(transform);
        assertEquals(7, transform.vertexSet().size());
    }

    @Test
    void transformDerivedFrom() {
        // region *** build the TopologyTemplate ***
        TTopologyTemplate topology = new TTopologyTemplate();
        topology.addNodeTemplate(nodeTemplates.get("test_node_2"));
        // endregion

        TServiceTemplate serviceTemplate = new TServiceTemplate();
        serviceTemplate.setTopologyTemplate(topology);

        EdmmConverter edmmConverter = new EdmmConverter(nodeTypes, relationshipTypes, nodeTypeImplementations,
            relationshipTypeImplementations, artifactTemplates, edmmTypeExtendsMapping, edmm1to1Mapping);
        EntityGraph transform = edmmConverter.transform(serviceTemplate);

        assertNotNull(transform);
        assertEquals(9, transform.vertexSet().size());
        assertTrue(transform.vertexSet().stream().anyMatch(entity ->
            entity instanceof ScalarEntity
                && entity.getName().equals("extends")
                && ((ScalarEntity) entity).getValue().equals("software_component")));
    }

    @Test
    void transformProperties() {
        // region *** build the TopologyTemplate ***
        TTopologyTemplate topology = new TTopologyTemplate();
        topology.addNodeTemplate(nodeTemplates.get("test_node_3"));
        // endregion

        TServiceTemplate serviceTemplate = new TServiceTemplate();
        serviceTemplate.setTopologyTemplate(topology);

        EdmmConverter edmmConverter = new EdmmConverter(nodeTypes, relationshipTypes, nodeTypeImplementations,
            relationshipTypeImplementations, artifactTemplates, edmmTypeExtendsMapping, edmm1to1Mapping);
        EntityGraph transform = edmmConverter.transform(serviceTemplate);

        assertNotNull(transform);
        assertEquals(20, transform.vertexSet().size());
        assertTrue(transform.vertexSet().stream().anyMatch(entity ->
            entity instanceof MappingEntity
                && entity.getName().equals("properties")
        ));
        Stream.of("os_family", "public_key", "ssh_port").forEach(key -> {
            assertTrue(transform.vertexSet().stream().anyMatch(entity ->
                entity instanceof MappingEntity
                    && entity.getName().equals(key)
                    && entity.getParent().isPresent()
                    && entity.getParent().get().getName().equals("properties")
                    && entity.getChildren().size() == 1
            ));
        });
        Stream.of("os_family", "public_key", "ssh_port").forEach(key -> {
            assertTrue(transform.vertexSet().stream().anyMatch(entity ->
                entity instanceof ScalarEntity
                    && entity.getName().equals(key)
                    && !((ScalarEntity) entity).getValue().isEmpty()
                    && entity.getParent().isPresent()
                    && entity.getParent().get().getName().equals("properties")
            ));
        });
    }

    @Test
    void transformTopologyWithRelationsAndRelationTypes() {
        // region *** build the TopologyTemplate ***
        TTopologyTemplate topology = new TTopologyTemplate();
        topology.addNodeTemplate(nodeTemplates.get("test_node_1"));
        topology.addNodeTemplate(nodeTemplates.get("test_node_2"));
        topology.addNodeTemplate(nodeTemplates.get("test_node_3"));
        topology.addRelationshipTemplate(relationshipTemplates.get("1_hosted_on_3"));
        topology.addRelationshipTemplate(relationshipTemplates.get("2_hosted_on_3"));
        topology.addRelationshipTemplate(relationshipTemplates.get("1_connects_to_2"));
        // endregion

        TServiceTemplate serviceTemplate = new TServiceTemplate();
        serviceTemplate.setTopologyTemplate(topology);

        EdmmConverter edmmConverter = new EdmmConverter(nodeTypes, relationshipTypes, nodeTypeImplementations,
            relationshipTypeImplementations, artifactTemplates, edmmTypeExtendsMapping, edmm1to1Mapping);
        EntityGraph transform = edmmConverter.transform(serviceTemplate);

        assertNotNull(transform);
        assertEquals(40, transform.vertexSet().size());
        assertTrue(transform.vertexSet().stream().anyMatch(entity ->
            entity instanceof ScalarEntity
                && entity.getName().equals("hosted_on")
                && ((ScalarEntity) entity).getValue().equals("test_node_3")
                && entity.getParent().isPresent()
                && entity.getParent().get().getName().equals("relations")
        ));
        assertTrue(transform.vertexSet().stream().anyMatch(entity ->
            entity instanceof ScalarEntity
                && entity.getName().equals("connects_to")
                && ((ScalarEntity) entity).getValue().equals("test_node_2")
                && entity.getParent().isPresent()
                && entity.getParent().get().getName().equals("relations")
        ));
    }

    @Test
    void transformTopologyWithOperations() {
        // region *** build the TopologyTemplate ***
        TTopologyTemplate topology = new TTopologyTemplate();
        topology.addNodeTemplate(nodeTemplates.get("test_node_4"));
        // endregion

        TServiceTemplate serviceTemplate = new TServiceTemplate();
        serviceTemplate.setTopologyTemplate(topology);

        EdmmConverter edmmConverter = new EdmmConverter(nodeTypes, relationshipTypes, nodeTypeImplementations,
            relationshipTypeImplementations, artifactTemplates, edmmTypeExtendsMapping, edmm1to1Mapping, false);
        EntityGraph transform = edmmConverter.transform(serviceTemplate);

        assertNotNull(transform);
        assertEquals(12, transform.vertexSet().size());

        Optional<Entity> operations = transform.getEntity(Arrays.asList("0", "component_types", "https_ex.orgtoscatoedmm__test_node_type_4", "operations"));
        assertTrue(operations.isPresent());
        Optional<Entity> start = transform.getEntity(Arrays.asList("0", "component_types", "https_ex.orgtoscatoedmm__test_node_type_4", "operations", "start"));
        assertTrue(start.isPresent());
        assertTrue(start.get() instanceof ScalarEntity);
        assertEquals("/artifacttemplates/ns/startTestNode4/files/script.sh", ((ScalarEntity) start.get()).getValue());
        Optional<Entity> stop = transform.getEntity(Arrays.asList("0", "component_types", "https_ex.orgtoscatoedmm__test_node_type_4", "operations", "stop"));
        assertTrue(stop.isPresent());
        assertTrue(stop.get() instanceof ScalarEntity);
        assertEquals("/artifacttemplates/ns/startTestNode4/files/script.sh", ((ScalarEntity) stop.get()).getValue());
    }

    @Test
    void transformTopology() {
        // region *** build the TopologyTemplate ***
        TTopologyTemplate topology = new TTopologyTemplate();
        topology.addNodeTemplate(nodeTemplates.get("test_node_1"));
        topology.addNodeTemplate(nodeTemplates.get("test_node_2"));
        topology.addNodeTemplate(nodeTemplates.get("test_node_3"));
        topology.addNodeTemplate(nodeTemplates.get("test_node_4"));
        topology.addRelationshipTemplate(relationshipTemplates.get("1_hosted_on_3"));
        topology.addRelationshipTemplate(relationshipTemplates.get("2_hosted_on_3"));
        topology.addRelationshipTemplate(relationshipTemplates.get("4_hosted_on_1"));
        topology.addRelationshipTemplate(relationshipTemplates.get("1_connects_to_2"));
        TServiceTemplate serviceTemplate = new TServiceTemplate();
        serviceTemplate.setTopologyTemplate(topology);
        // endregion

        EdmmConverter edmmConverter = new EdmmConverter(nodeTypes, relationshipTypes, nodeTypeImplementations,
            relationshipTypeImplementations, artifactTemplates, edmmTypeExtendsMapping, edmm1to1Mapping, false);
        EntityGraph transform = edmmConverter.transform(serviceTemplate);
        StringWriter stringWriter = new StringWriter();
        transform.generateYamlOutput(stringWriter);

        assertEquals("---\n" +
            "components:\n" +
            "  test_node_1:\n" +
            "    type: software_component\n" +
            "    relations:\n" +
            "    - hosted_on: test_node_3\n" +
            "    - connects_to: test_node_2\n" +
            "  test_node_3:\n" +
            "    type: https_ex.orgtoscatoedmm__test_node_type_3\n" +
            "    properties:\n" +
            "      public_key: '-----BEGIN PUBLIC KEY----- ... -----END PUBLIC KEY-----'\n" +
            "      ssh_port: '22'\n" +
            "      os_family: ubuntu\n" +
            "  test_node_2:\n" +
            "    type: https_ex.orgtoscatoedmm__test_node_type_2\n" +
            "    relations:\n" +
            "    - hosted_on: test_node_3\n" +
            "  test_node_4:\n" +
            "    type: https_ex.orgtoscatoedmm__test_node_type_4\n" +
            "    relations:\n" +
            "    - hosted_on: test_node_1\n" +
            "relation_types:\n" +
            "  depends_on:\n" +
            "    extends: null\n" +
            "  hosted_on:\n" +
            "    extends: depends_on\n" +
            "  connects_to:\n" +
            "    extends: depends_on\n" +
            "component_types:\n" +
            "  https_ex.orgtoscatoedmm__test_node_type_2:\n" +
            "    extends: software_component\n" +
            "  compute:\n" +
            "    extends: base\n" +
            "  https_ex.orgtoscatoedmm__test_node_type_3:\n" +
            "    extends: compute\n" +
            "    properties:\n" +
            "      public_key:\n" +
            "        type: string\n" +
            "      ssh_port:\n" +
            "        type: number\n" +
            "      os_family:\n" +
            "        type: string\n" +
            "  web_application:\n" +
            "    extends: base\n" +
            "  https_ex.orgtoscatoedmm__test_node_type_4:\n" +
            "    operations:\n" +
            "      stop: /artifacttemplates/ns/startTestNode4/files/script.sh\n" +
            "      start: /artifacttemplates/ns/startTestNode4/files/script.sh\n" +
            "    extends: web_application\n" +
            "  software_component:\n" +
            "    extends: base\n", stringWriter.toString());
    }
}
