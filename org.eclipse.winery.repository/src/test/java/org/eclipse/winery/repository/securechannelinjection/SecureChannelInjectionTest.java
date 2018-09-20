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

package org.eclipse.winery.repository.securechannelinjection;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Uses the TestVSphere_1-w1-wip1 service template that is checked in tosca-definitions-public. Needs to be checked out.
 */
@Disabled("Needs to have the winery repository tosca-definitions-public checked out")
public class SecureChannelInjectionTest {

    private ServiceTemplateId testServiceId = new ServiceTemplateId("http://www.example.org/tosca/servicetemplates", "TestVSphere_1-w1-wip1", false);
    private TServiceTemplate serviceTemplate;
    private Map<String, TEntityTemplate> context;
    private ServiceTemplateId newTemplate;

    @BeforeEach
    public void setUp() {

        newTemplate = null;
        assertTrue(RepositoryFactory.getRepository().exists(testServiceId));
        serviceTemplate = RepositoryFactory.getRepository().getElement(testServiceId);
        Map<String, TEntityTemplate> initialContext = new HashMap<>();
        TNodeTemplate source = serviceTemplate.getTopologyTemplate().getNodeTemplate("JavaSender_1-w1-wip1");
        TNodeTemplate target = serviceTemplate.getTopologyTemplate().getNodeTemplate("JavaReceiver_1-w1-wip1");
        initialContext.put("sourceNode", source);
        initialContext.put("targetNode", target);
        context = initialContext;
    }

    @Test
    public void testPreconditions() {
        PreconditionChecker preChecker = new PreconditionChecker(serviceTemplate, context);
        assertTrue(preChecker.checkPreconditions(Arrays.asList("runsOnVm sourceNode", "hasSecureVersion sourceNodeHost")));
    }

    @Test
    public void testInjection() throws IOException {
        SecureChannelInjector injector = new SecureChannelInjector();
        newTemplate = injector.createSecureChannel(testServiceId, context.get("sourceNode").getId(), context.get("targetNode").getId());
        newTemplate = injector.createSecureChannel(testServiceId, context.get("targetNode").getId(), context.get("sourceNode").getId());
        assertFalse(newTemplate.equals(testServiceId));
    }

    @Test
    public void testModification() throws IOException {

        TNodeTemplate host = serviceTemplate.getTopologyTemplate().getNodeTemplate("Ubuntu-14.04-VM");
        TNodeTemplate secureHost = ModelUtilities.instantiateNodeTemplate(RepositoryFactory.getRepository()
            .getElement(new NodeTypeId("http://opentosca.org/nodetypes", "Ubuntu-14.04-VM-secure_w1-wip1", false)));
        context.put("host", host);
        context.put("secureHost", secureHost);
        TopologyModificator modificator = new TopologyModificator(serviceTemplate, context);
        TNodeTemplate target = (TNodeTemplate) context.get("targetNode");
        newTemplate = modificator.modifyTopology(Arrays.asList("deleteNode targetNode", "replaceNode host secureHost"));
        TServiceTemplate newServiceTemplate = RepositoryFactory.getRepository().getElement(newTemplate);
        assertFalse(newServiceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate().contains(target));
    }

    @Test
    public void testObjectMapping() throws Exception {
        String validJson = "{\"problemOccurrences\": [\n" +
            "\t{\"problem\":\"Insecure Communication Channel\",\n" +
            "\t\"pattern\":\"Secure Channel\",\n" +
            "\t\"description\":\"\",\n" +
            "\t\"findings\":[\n" +
            "\t\t{\"Component_2\":\"JavaSender_1-w1-wip1\",\n" +
            "\t\t\"Component_1\":\"JavaReceiver_1-w1-wip1\"}\n" +
            "\t]}\n" +
            "]\n" +
            "}";
        ObjectMapper om = new ObjectMapper();
        ProblemJson json = om.readValue(validJson, ProblemJson.class);
        assertNotNull(json);
        ProblemJson.Problem problem = json.getProblemOccurrences().get(0);
        assertEquals("Insecure Communication Channel", problem.getProblem());
        assertEquals("Secure Channel", problem.getPattern());
        assertEquals("", problem.getDescription());
        assertEquals("JavaSender_1-w1-wip1", problem.getFindings().get(0).getComponent2());
        assertEquals("JavaReceiver_1-w1-wip1", problem.getFindings().get(0).getComponent1());
    }

    @Test
    public void testNoDirectRelationship() {
        TNodeTemplate host = serviceTemplate.getTopologyTemplate().getNodeTemplate("Ubuntu-14.04-VM");
        assertThrows(SecureChannelException.class, () -> new SecureChannelInjector()
            .createSecureChannel(testServiceId, host.getId(), context.get("target").getId()));
    }

    @AfterEach
    public void cleanUp() {
        if (newTemplate != null) {
            try {
                RepositoryFactory.getRepository().forceDelete(newTemplate);
            } catch (Exception e) {

            }
        }
    }
}
