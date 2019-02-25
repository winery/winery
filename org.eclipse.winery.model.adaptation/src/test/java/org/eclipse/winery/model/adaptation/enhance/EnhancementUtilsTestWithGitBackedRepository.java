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

package org.eclipse.winery.model.adaptation.enhance;

import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.OpenToscaBaseTypes;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnhancementUtilsTestWithGitBackedRepository extends TestWithGitBackedRepository {

    @Test
    void determineStatefulComponentsTest() throws Exception {
        this.setRevisionTo("origin/plain");

        TServiceTemplate element = RepositoryFactory.getRepository()
            .getElement(new ServiceTemplateId(
                    QName.valueOf("{http://opentosca.org/examples/servicetemplates}TopologyWithStatefulComponent_w1-wip1")
                )
            );

        TTopologyTemplate topologyTemplate = EnhancementUtils.determineStatefulComponents(element.getTopologyTemplate());

        TNodeTemplate statefulComponent = topologyTemplate.getNodeTemplate("statefulComponent");
        assertNotNull(statefulComponent);
        TPolicy stateful = statefulComponent.getPolicies().getPolicy().get(0);
        assertEquals(OpenToscaBaseTypes.statefulComponentPolicyType, stateful.getPolicyType());

        TNodeTemplate stateless = topologyTemplate.getNodeTemplate("stateless");
        assertNotNull(stateless);
        assertNull(stateless.getPolicies());
    }

    @Test
    void determineFreezableComponents() throws Exception {
        this.setRevisionTo("origin/plain");

        TServiceTemplate element = RepositoryFactory.getRepository()
            .getElement(new ServiceTemplateId(
                    QName.valueOf("{http://opentosca.org/examples/servicetemplates}TopologyWithStatefulComponent_w1-wip2")
                )
            );
        TPolicy expectedPolicy = new TPolicy();
        expectedPolicy.setPolicyType(OpenToscaBaseTypes.freezableComponentPolicyType);
        expectedPolicy.setName("freezable");

        TopologyAndErrorList result = EnhancementUtils.determineFreezableComponents(element.getTopologyTemplate());

        assertEquals(0, result.errorList.size());

        TTopologyTemplate topologyTemplate = result.topologyTemplate;
        assertNull(topologyTemplate.getNodeTemplate("VM_2").getPolicies());

        List<TPolicy> statefulFreezableComponentPolicies = topologyTemplate.getNodeTemplate("statefulFreezableComponent")
            .getPolicies().getPolicy();
        assertEquals(2, statefulFreezableComponentPolicies.size());
        assertTrue(statefulFreezableComponentPolicies.contains(expectedPolicy));

        List<TPolicy> statefulNotFreezableComponentPolicies = topologyTemplate.getNodeTemplate("statefulNotFreezableComponent")
            .getPolicies().getPolicy();
        assertEquals(1, statefulNotFreezableComponentPolicies.size());
        assertFalse(statefulNotFreezableComponentPolicies.contains(expectedPolicy));

        List<TPolicy> statelessFreezableComponentPolicies = topologyTemplate.getNodeTemplate("statelessFreezableComponent")
            .getPolicies().getPolicy();
        assertEquals(1, statelessFreezableComponentPolicies.size());
        assertTrue(statelessFreezableComponentPolicies.contains(expectedPolicy));

        assertNull(topologyTemplate.getNodeTemplate("AbstractNodeTypeWithProperties_1-w1-wip1").getPolicies());
        assertNull(topologyTemplate.getNodeTemplate("Infrastructure-As-A-Service-Implementation_1-w1-wip1").getPolicies());
    }

    @Test
    void cleanFreezableComponents() throws Exception {
        this.setRevisionTo("origin/plain");

        TServiceTemplate element = RepositoryFactory.getRepository()
            .getElement(new ServiceTemplateId(
                    QName.valueOf("{http://opentosca.org/examples/servicetemplates}TopologyWithStatefulComponent_w1-wip3")
                )
            );

        TPolicy freezablePolicy = new TPolicy();
        freezablePolicy.setPolicyType(OpenToscaBaseTypes.freezableComponentPolicyType);
        freezablePolicy.setName("freezable");

        TTopologyTemplate topologyTemplate = EnhancementUtils.cleanFreezableComponents(element.getTopologyTemplate());
        assertTrue(topologyTemplate.getNodeTemplate("statefulFreezableComponent").getPolicies().getPolicy().contains(freezablePolicy));

        assertFalse(topologyTemplate.getNodeTemplate("statefulNotFreezableComponent").getPolicies().getPolicy().contains(freezablePolicy));
        assertFalse(topologyTemplate.getNodeTemplate("AbstractNodeTypeWithProperties_1-w1-wip1").getPolicies().getPolicy().contains(freezablePolicy));
        assertTrue(topologyTemplate.getNodeTemplate("statelessFreezableComponent").getPolicies().getPolicy().contains(freezablePolicy));

        assertFalse(topologyTemplate.getNodeTemplate("statefulFreezableImplicitlyProvisioned").getPolicies().getPolicy().contains(freezablePolicy));
        assertTrue(topologyTemplate.getNodeTemplate("VM_3").getPolicies().getPolicy().contains(freezablePolicy));
    }
}
