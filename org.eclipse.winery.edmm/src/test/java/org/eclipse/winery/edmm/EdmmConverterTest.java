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

package org.eclipse.winery.edmm;

import java.util.HashMap;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.kvproperties.PropertyDefinitionKV;
import org.eclipse.winery.model.tosca.kvproperties.PropertyDefinitionKVList;
import org.eclipse.winery.model.tosca.kvproperties.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;

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
        nodeType2.setName(nodeType1QName.getLocalPart());
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
        // endregion

        // region *** RelationType setup ***
        // endregion

        // region *** creation of the ServiceTemplate ***
        // region *** create the NodeTemplates ***
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
        // endregion 

        // region *** create the RelationshipTemplate ***
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

        EdmmConverter edmmConverter = new EdmmConverter(nodeTypes, relationshipTypes);
        EntityGraph transform = edmmConverter.transform(serviceTemplate);

        assertNotNull(transform);
        assertEquals(6, transform.vertexSet().size());
    }

    @Test
    void transformDerivedFrom() {
        // region *** build the TopologyTemplate ***
        TTopologyTemplate topology = new TTopologyTemplate();
        topology.addNodeTemplate(nodeTemplates.get("test_node_2"));
        // endregion

        TServiceTemplate serviceTemplate = new TServiceTemplate();
        serviceTemplate.setTopologyTemplate(topology);

        EdmmConverter edmmConverter = new EdmmConverter(nodeTypes, relationshipTypes);
        EntityGraph transform = edmmConverter.transform(serviceTemplate);

        assertNotNull(transform);
        assertEquals(8, transform.vertexSet().size());
        assertTrue(transform.vertexSet().stream().anyMatch(entity ->
            entity instanceof ScalarEntity
                && entity.getName().equals("extends")
                && ((ScalarEntity) entity).getValue().equals("https_ex.orgtoscatoedmm__test_node_type")));
    }

    @Test
    void transformProperties() {
        // region *** build the TopologyTemplate ***
        TTopologyTemplate topology = new TTopologyTemplate();
        topology.addNodeTemplate(nodeTemplates.get("test_node_3"));
        // endregion

        TServiceTemplate serviceTemplate = new TServiceTemplate();
        serviceTemplate.setTopologyTemplate(topology);

        EdmmConverter edmmConverter = new EdmmConverter(nodeTypes, relationshipTypes);
        EntityGraph transform = edmmConverter.transform(serviceTemplate);

        assertNotNull(transform);
        assertEquals(17, transform.vertexSet().size());
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
}
