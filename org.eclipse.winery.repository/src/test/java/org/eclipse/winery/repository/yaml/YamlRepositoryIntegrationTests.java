/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.yaml;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.configuration.RepositoryConfigurationObject;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TGroupDefinition;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.backend.BackendUtils;

import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.Preconditions;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YamlRepositoryIntegrationTests extends TestWithGitBackedRepository {

    public YamlRepositoryIntegrationTests() {
        super(RepositoryConfigurationObject.RepositoryProvider.YAML);
    }

    @Test
    public void testGetServiceTemplate() throws Exception {
        this.setRevisionTo("bab12e7a8ca7af1c0a0ce186c81bab3899ab989b");

        assertEquals(10, repository.getAllDefinitionsChildIds().size());
        TServiceTemplate element = repository.getElement(
            new ServiceTemplateId(QName.valueOf("{example.org.tosca.servicetemplates}demo_w1-wip1"))
        );

        assertNotNull(element);
        assertNotNull(element.getTopologyTemplate());
        assertEquals(3, element.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate().size());
        assertNotNull(element.getTopologyTemplate().getNodeTemplate("compute_w1-wip1_0"));
        assertNotNull(element.getTopologyTemplate().getNodeTemplate("software_w1-wip1_0"));

        TRelationshipTemplate relation = element.getTopologyTemplate().getRelationshipTemplate("con_hostedOn_0");
        assertNotNull(relation);
        assertEquals("software_w1-wip1_0", relation.getSourceElement().getRef().getId());
        assertEquals("compute_w1-wip1_0", relation.getTargetElement().getRef().getId());
    }

    @Test
    public void testGroupDefinitions() throws Exception {
        this.setRevisionTo("origin/yaml");

        // Setup test
        ServiceTemplateId id = new ServiceTemplateId(QName.valueOf("{example.org.tosca.servicetemplates}demo_w1-wip1"));
        TServiceTemplate element = repository.getElement(id);

        assertNotNull(element.getTopologyTemplate());
        TNodeTemplate nodeTemplate = element.getTopologyTemplate().getNodeTemplate("compute_w1-wip1_0");
        assertNotNull(nodeTemplate);

        TGroupDefinition testGroup = new TGroupDefinition.Builder("test", QName.valueOf("{tosca.groups}Root"))
            .setDescription("This is a description")
            .addMembers(QName.valueOf(nodeTemplate.getId()))
            .build();

        // Save group
        element.getTopologyTemplate().addGroup(testGroup);
        BackendUtils.persist(repository, id, element);

        // Assertions
        element = repository.getElement(id);
        assertNotNull(element.getTopologyTemplate());
        assertNotNull(element.getTopologyTemplate().getGroups());
        assertEquals(1, element.getTopologyTemplate().getGroups().size());
        TGroupDefinition actualGroup = element.getTopologyTemplate().getGroups().get(0);
        assertEquals(testGroup.getDescription(), actualGroup.getDescription());
        assertEquals(testGroup.getMembers().get(0), actualGroup.getMembers().get(0));
    }

    @Test
    public void testCreateGroupWithNoType() throws Exception {
        this.setRevisionTo("origin/yaml");

        // Setup test
        ServiceTemplateId id = new ServiceTemplateId(QName.valueOf("{example.org.tosca.servicetemplates}demo_w1-wip1"));
        TServiceTemplate element = repository.getElement(id);

        assertNotNull(element.getTopologyTemplate());
        TNodeTemplate nodeTemplate = element.getTopologyTemplate().getNodeTemplate("compute_w1-wip1_0");
        assertNotNull(nodeTemplate);

        TGroupDefinition testGroup = new TGroupDefinition.Builder("test", null)
            .setDescription("This is a description")
            .addMembers(QName.valueOf(nodeTemplate.getId()))
            .build();

        // Save group
        element.getTopologyTemplate().addGroup(testGroup);
        BackendUtils.persist(repository, id, element);

        // Assertions
        element = repository.getElement(id);
        assertNotNull(element.getTopologyTemplate());
        assertNotNull(element.getTopologyTemplate().getGroups());
        assertEquals(1, element.getTopologyTemplate().getGroups().size());
        TGroupDefinition actualGroup = element.getTopologyTemplate().getGroups().get(0);
        assertEquals(QName.valueOf("{tosca.groups}Root"), actualGroup.getType());
    }

    @Test
    @Disabled("for this to work properly we need a \"touched\" repository state as well as deterministic serialization")
    public void roundTripDoesNotChangeContent() throws Exception {
        this.setRevisionTo("origin/yaml");
        assertAll(
            repository.getAllDefinitionsChildIds().stream()
                .map(definitionsId -> () -> {
                    TDefinitions retrieved = repository.getDefinitions(definitionsId);
                    try {
                        repository.putDefinition(definitionsId, retrieved);
                        final Status gitStatus = git.status().call();
                        assertTrue(gitStatus.isClean(), "Failed for definitionsId " + definitionsId);
                    } catch (IOException | GitAPIException e) {
                        Preconditions.condition(false, "Exception occurred during validation");
                    }
                })
        );
    }
}
