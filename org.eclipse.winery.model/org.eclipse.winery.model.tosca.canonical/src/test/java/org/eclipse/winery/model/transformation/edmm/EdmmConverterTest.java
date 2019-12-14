/*******************************************************************************
 * Copyright (c) 2019-2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.transformation.edmm;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

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

public class EdmmConverterTest extends EdmmDependantTest {

    protected EdmmConverterTest() throws UnsupportedEncodingException {
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
        assertEquals(9, transform.vertexSet().size());
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
        assertEquals(42, transform.vertexSet().size());
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
            "    artifacts:\n" +
            "    - war: /artifacttemplates/https%3A%2F%2Fex.org%2Ftosca%2Fto%2Fedmm/startTestNode4/files/script.sh\n" +
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
            "    operations:\n" +
            "      stop: /artifacttemplates/https%3A%2F%2Fex.org%2Ftosca%2Fto%2Fedmm/startTestNode4/files/script.sh\n" +
            "      start: /artifacttemplates/https%3A%2F%2Fex.org%2Ftosca%2Fto%2Fedmm/startTestNode4/files/script.sh\n" +
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
            "    extends: web_application\n" +
            "  software_component:\n" +
            "    extends: base\n", stringWriter.toString());
    }
}
