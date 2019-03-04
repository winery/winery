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

package org.eclipse.winery.model.adaptation.substitution;

import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubstitutionTestWithGitBackedRepository extends TestWithGitBackedRepository {

    private IRepository repo = RepositoryFactory.getRepository();

    @Test
    public void substituteNodeTemplateTypes() throws Exception {
        this.setRevisionTo("16a4ec8a55c4a87d8a46088d283640adadd2f07c");

        Substitution substitution = new Substitution();
        ServiceTemplateId serviceTemplateId = new ServiceTemplateId("http://plain.winery.org/pattern-based/servicetemplates",
            "ServiceTemplateContainingAbstractNodeTemplates_w1-wip1", false);

        ServiceTemplateId newId = substitution.substituteTopologyOfServiceTemplate(serviceTemplateId);
        TServiceTemplate element = repo.getElement(newId);

        assertNotNull(element.getTopologyTemplate());

        List<TNodeTemplate> nodeTemplates = element.getTopologyTemplate().getNodeTemplates();
        assertEquals(5, nodeTemplates.size());

        // ensure these types do not exist anymore
        assertFalse(nodeTemplates.removeIf(tNodeTemplate ->
                new QName("http://plain.winery.opentosca.org/patterns", "Infrastructure-As-A-Service_w1")
                    .equals(tNodeTemplate.getType())
                    ||
                    new QName("http://plain.winery.opentosca.org/pattern-based/nodetypes", "AbstractNodeTypeWithProperties_1-w1-wip1")
                        .equals(tNodeTemplate.getType())
            )
        );

        assertTrue(nodeTemplates.removeIf(tNodeTemplate ->
                new QName("http://plain.winery.org/pattern-based/nodetypes", "Infrastructure-As-A-Service-Implementation_1-w1-wip1")
                    .equals(tNodeTemplate.getType())
            )
        );
        assertTrue(nodeTemplates.removeIf(tNodeTemplate ->
                new QName("http://plain.winery.org/pattern-based/nodetypes", "NodeTypeInheritingFromAbstractType_1-w1-wip1")
                    .equals(tNodeTemplate.getType())
            )
        );
    }

    @Test
    public void substituteNodeTemplateWithServiceTemplate() throws Exception {
        this.setRevisionTo("origin/plain");

        Substitution substitution = new Substitution();
        ServiceTemplateId serviceTemplateId = new ServiceTemplateId("http://plain.winery.org/pattern-based/servicetemplates",
            "ServiceTemplateContainingAbstractNodeTemplates_w2-wip1", false);

        ServiceTemplateId substitutedServiceTemplate = substitution.substituteTopologyOfServiceTemplate(serviceTemplateId);
        TServiceTemplate element = repo.getElement(substitutedServiceTemplate);

        assertNotNull(element.getTopologyTemplate());
        assertNotNull(element.getTopologyTemplate().getNodeTemplates());
        assertEquals(8, element.getTopologyTemplate().getNodeTemplates().size());
        assertNotNull(element.getTopologyTemplate().getRelationshipTemplates());
        assertEquals(7, element.getTopologyTemplate().getRelationshipTemplates().size());
        assertEquals("NodeTypeWithThreeReqCapPairsCoveringAllReqCapVariants_w1-wip1",
            element.getTopologyTemplate().getRelationshipTemplate("con_5").getTargetElement().getRef().getId());
    }

    @Test
    public void substituteNodeTemplateWithServiceTemplateAndOutgoingRelationship() throws Exception {
        this.setRevisionTo("origin/plain");

        Substitution substitution = new Substitution();
        ServiceTemplateId serviceTemplateId = new ServiceTemplateId("http://plain.winery.org/pattern-based/servicetemplates",
            "ServiceTemplateContainingAbstractNodeTemplates_w3-wip1", false);

        ServiceTemplateId substitutedServiceTemplate = substitution.substituteTopologyOfServiceTemplate(serviceTemplateId);
        TServiceTemplate element = repo.getElement(substitutedServiceTemplate);

        assertNotNull(element.getTopologyTemplate());
        assertNotNull(element.getTopologyTemplate().getNodeTemplates());
        assertEquals(5, element.getTopologyTemplate().getNodeTemplates().size());
        assertNotNull(element.getTopologyTemplate().getRelationshipTemplates());
        assertEquals(4, element.getTopologyTemplate().getRelationshipTemplates().size());
        assertEquals("NodeTypeWithThreeReqCapPairsCoveringAllReqCapVariants_w1-wip1",
            element.getTopologyTemplate().getRelationshipTemplate("con_2").getSourceElement().getRef().getId());
    }
}
