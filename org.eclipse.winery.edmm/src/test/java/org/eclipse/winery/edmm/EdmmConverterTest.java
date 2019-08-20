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

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

import io.github.edmm.core.parser.EntityGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        // endregion

        // region *** RelationType setup ***
        // endregion

        // region *** creation of the ServiceTemplate ***
        // region *** create the NodeTemplates ***
        TNodeTemplate nt1 = new TNodeTemplate();
        nt1.setType(nodeType1QName);
        nt1.setId("test_node_1");

        nodeTemplates.put(nt1.getId(), nt1);
        // endregion 

        // region *** create the RelationshipTemplate ***
        // endregion
    }

    @Test
    void transformNodeTypeToAComponentType() {
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
}
