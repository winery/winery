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

package org.eclipse.winery.repository.xml;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TGroupDefinition;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTag;
import org.eclipse.winery.model.tosca.TTags;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.backend.BackendUtils;

import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.Preconditions;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XmlRepositoryIntegrationTests extends TestWithGitBackedRepository {

    @BeforeEach
    public void setUp() throws GitAPIException {
        this.setRevisionTo("origin/plain");
    }

    @Test
    @Disabled("for this to work properly we need a \"touched\" repository state as well as deterministic serialization")
    public void roundTripDoesNotChangeContent() {
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

    @Test
    public void testGroupDefinitions() throws Exception {

        // Setup test
        ServiceTemplateId id = new ServiceTemplateId(QName.valueOf("{http://plain.winery.opentosca.org/servicetemplates}ServiceTemplateWithOneNodeTemplate_w1-wip1"));
        TServiceTemplate element = repository.getElement(id);

        assertNotNull(element.getTopologyTemplate());
        TNodeTemplate nodeTemplate = element.getTopologyTemplate().getNodeTemplate("NodeTypeWith5Versions_0_3.4-w3-wip1");
        assertNotNull(nodeTemplate);

        TGroupDefinition testGroup = new TGroupDefinition.Builder("test", QName.valueOf("{tosca.groups}Root"))
            .setDescription("test")
            .build();

        if (element.getTags() == null) {
            element.setTags(new TTags());
        }
        element.getTags().getTag().add(new TTag.Builder().setName("test").setValue("test").build());

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
        assertTrue(testGroup.getMembers().isEmpty());

        assertNotNull(element.getTags());
        assertEquals(1, element.getTags().getTag().size());
    }
}
