/*******************************************************************************
 * Copyright (c) 2019-2021 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.edmm.model;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.winery.edmm.EdmmDependantTest;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

import io.github.edmm.core.parser.Entity;
import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.core.parser.ScalarEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EdmmExporterTest extends EdmmDependantTest {

    protected EdmmExporterTest() throws UnsupportedEncodingException {
    }

    @Test
    void transformOneNodeTemplate() {
        // region *** build the TopologyTemplate ***
        TTopologyTemplate.Builder topology = new TTopologyTemplate.Builder();
        topology.addNodeTemplate(nodeTemplates.get("test_node_1"));
        // endregion

        TServiceTemplate serviceTemplate = new TServiceTemplate();
        serviceTemplate.setTopologyTemplate(topology.build());

        EdmmExporter edmmExporter = new EdmmExporter(nodeTypes, relationshipTypes, nodeTypeImplementations,
            relationshipTypeImplementations, artifactTemplates, edmm1to1Mapping);
        EntityGraph transform = edmmExporter.transform(serviceTemplate);

        assertNotNull(transform);
        assertEquals(15, transform.vertexSet().size());
    }

    @Test
    void transformDerivedFrom() {
        // region *** build the TopologyTemplate ***
        TTopologyTemplate.Builder topology = new TTopologyTemplate.Builder();
        topology.addNodeTemplate(nodeTemplates.get("test_node_2"));
        // endregion

        TServiceTemplate serviceTemplate = new TServiceTemplate();
        serviceTemplate.setTopologyTemplate(topology.build());

        EdmmExporter edmmExporter = new EdmmExporter(nodeTypes, relationshipTypes, nodeTypeImplementations,
            relationshipTypeImplementations, artifactTemplates, edmm1to1Mapping);
        EntityGraph transform = edmmExporter.transform(serviceTemplate);

        assertNotNull(transform);
        assertTrue(transform.vertexSet().stream().anyMatch(entity ->
            entity instanceof ScalarEntity
                && entity.getName().equals("extends")
                && "software_component".equals(((ScalarEntity) entity).getValue())));
    }

    @Test
    void transformProperties() {
        // region *** build the TopologyTemplate ***
        TTopologyTemplate.Builder topology = new TTopologyTemplate.Builder();
        topology.addNodeTemplate(nodeTemplates.get("test_node_3"));
        // endregion

        TServiceTemplate serviceTemplate = new TServiceTemplate();
        serviceTemplate.setTopologyTemplate(topology.build());

        EdmmExporter edmmExporter = new EdmmExporter(nodeTypes, relationshipTypes, nodeTypeImplementations,
            relationshipTypeImplementations, artifactTemplates, edmm1to1Mapping);
        EntityGraph transform = edmmExporter.transform(serviceTemplate);

        assertNotNull(transform);
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
        TTopologyTemplate.Builder topology = new TTopologyTemplate.Builder();
        topology.addNodeTemplate(nodeTemplates.get("test_node_1"));
        topology.addNodeTemplate(nodeTemplates.get("test_node_2"));
        topology.addNodeTemplate(nodeTemplates.get("test_node_3"));
        topology.addRelationshipTemplate(relationshipTemplates.get("1_hosted_on_3"));
        topology.addRelationshipTemplate(relationshipTemplates.get("2_hosted_on_3"));
        topology.addRelationshipTemplate(relationshipTemplates.get("1_connects_to_2"));
        // endregion

        TServiceTemplate serviceTemplate = new TServiceTemplate();
        serviceTemplate.setTopologyTemplate(topology.build());

        EdmmExporter edmmExporter = new EdmmExporter(nodeTypes, relationshipTypes, nodeTypeImplementations,
            relationshipTypeImplementations, artifactTemplates, edmm1to1Mapping);
        EntityGraph transform = edmmExporter.transform(serviceTemplate);

        assertNotNull(transform);
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
        TTopologyTemplate.Builder topology = new TTopologyTemplate.Builder();
        topology.addNodeTemplate(nodeTemplates.get("test_node_4"));
        // endregion

        TServiceTemplate serviceTemplate = new TServiceTemplate.Builder("testST", topology.build())
            .build();

        EdmmExporter edmmExporter = new EdmmExporter(nodeTypes, relationshipTypes, nodeTypeImplementations,
            relationshipTypeImplementations, artifactTemplates, edmm1to1Mapping, false);
        EntityGraph transform = edmmExporter.transform(serviceTemplate);

        assertNotNull(transform);

        Optional<Entity> operations = transform.getEntity(Arrays.asList("0", "components", "test_node_4", "operations"));
        assertTrue(operations.isPresent());
        Optional<Entity> start = transform.getEntity(Arrays.asList("0", "components", "test_node_4", "operations", "start"));
        assertTrue(start.isPresent());
        assertTrue(start.get() instanceof ScalarEntity);
        assertEquals("/artifacttemplates/https%3A%2F%2Fex.org%2Ftosca%2Fto%2Fedmm/startTestNode4/files/script.sh", ((ScalarEntity) start.get()).getValue());
        Optional<Entity> stop = transform.getEntity(Arrays.asList("0", "components", "test_node_4", "operations", "stop"));
        assertTrue(stop.isPresent());
        assertTrue(stop.get() instanceof ScalarEntity);
        assertEquals("/artifacttemplates/https%3A%2F%2Fex.org%2Ftosca%2Fto%2Fedmm/startTestNode4/files/script.sh", ((ScalarEntity) stop.get()).getValue());
    }

    @Test
    void transformTopology() {
        // region *** build the TopologyTemplate ***
        TTopologyTemplate.Builder topology = new TTopologyTemplate.Builder();
        topology.addNodeTemplate(nodeTemplates.get("test_node_1"));
        topology.addNodeTemplate(nodeTemplates.get("test_node_2"));
        topology.addNodeTemplate(nodeTemplates.get("test_node_3"));
        topology.addNodeTemplate(nodeTemplates.get("test_node_4"));
        topology.addRelationshipTemplate(relationshipTemplates.get("1_hosted_on_3"));
        topology.addRelationshipTemplate(relationshipTemplates.get("2_hosted_on_3"));
        topology.addRelationshipTemplate(relationshipTemplates.get("4_hosted_on_1"));
        topology.addRelationshipTemplate(relationshipTemplates.get("1_connects_to_2"));
        TServiceTemplate serviceTemplate = new TServiceTemplate();
        serviceTemplate.setTopologyTemplate(topology.build());
        // endregion

        EdmmExporter edmmExporter = new EdmmExporter(nodeTypes, relationshipTypes, nodeTypeImplementations,
            relationshipTypeImplementations, artifactTemplates, edmm1to1Mapping, false);
        EntityGraph transform = edmmExporter.transform(serviceTemplate);
        StringWriter stringWriter = new StringWriter();
        transform.generateYamlOutput(stringWriter);

        assertEquals("""
            ---
            components:
              test_node_1:
                type: great_type
                relations:
                - connects_to: test_node_2
                - hosted_on: test_node_3
                properties:
                  name: test_node_1
                artifacts:
                - war: /artifacttemplates/https%3A%2F%2Fex.org%2Ftosca%2Fto%2Fedmm/testNode1-DA/files/da.war
              test_node_3:
                type: https+----ex.org--tosca--to--edmm__test_node_type_3
                properties:
                  public_key: '-----BEGIN PUBLIC KEY----- ... -----END PUBLIC KEY-----'
                  ssh_port: '22'
                  os_family: ubuntu
                  name: test_node_3
              test_node_2:
                type: https+----ex.org--tosca--to--edmm__test_node_type_2
                relations:
                - hosted_on: test_node_3
                properties:
                  name: test_node_2
              test_node_4:
                operations:
                  stop: /artifacttemplates/https%3A%2F%2Fex.org%2Ftosca%2Fto%2Fedmm/startTestNode4/files/script.sh
                  start: /artifacttemplates/https%3A%2F%2Fex.org%2Ftosca%2Fto%2Fedmm/startTestNode4/files/script.sh
                type: https+----ex.org--tosca--to--edmm__test_node_type_4
                relations:
                - hosted_on: test_node_1
                properties:
                  name: test_node_4
            relation_types:
              depends_on:
                extends: null
              hosted_on:
                extends: depends_on
              connects_to:
                extends: depends_on
            multi_id: '12345'
            component_types:
              great_type:
                extends: base
              compute:
                extends: base
              web_application:
                extends: base
              https+----ex.org--tosca--to--edmm__test_node_type_2:
                extends: software_component
              https+----ex.org--tosca--to--edmm__test_node_type_3:
                extends: compute
                properties:
                  public_key:
                    type: string
                  ssh_port:
                    type: number
                  os_family:
                    type: string
              https+----ex.org--tosca--to--edmm__test_node_type_4:
                extends: web_application
              software_component:
                extends: base
              base:
                extends: null
            version: edm_1_0
            """, stringWriter.toString());
    }
}
