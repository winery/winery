/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.adaptation.instance;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.TestWithGitRepoAndSshServer;

import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InstanceModelUtilsTest extends TestWithGitRepoAndSshServer {

    @BeforeAll
    static void beforeAll() throws IOException, URISyntaxException {
        setUp();
    }

    @AfterAll
    static void afterAll() throws IOException {
        shutDown();
    }

    @Test
    void testGetSshProps() throws Exception {
        this.setRevisionTo("origin/plain");
        TServiceTemplate serviceTemplate = this.repository.getElement(
            new ServiceTemplateId("http://opentosca.org/servicetemplates", "SshTest_w1-wip1", false)
        );

        Set<String> sshProps = InstanceModelUtils.getRequiredSSHInputs(serviceTemplate.getTopologyTemplate(), Collections.singletonList("OperatingSystem_0"));

        assertNotNull(sshProps);
        assertEquals(4, sshProps.size());
    }

    @Test
    void getSshConnection() throws Exception {
        this.setRevisionTo("origin/plain");
        TServiceTemplate serviceTemplate = this.repository.getElement(
            new ServiceTemplateId("http://opentosca.org/servicetemplates", "SshTest_w1-wip1", false)
        );
        Session jschSession = getSession(serviceTemplate);

        assertTrue(jschSession.isConnected());
    }

    @Test
    void executeCommandTest() throws Exception {
        this.setRevisionTo("origin/plain");
        TServiceTemplate serviceTemplate = this.repository.getElement(
            new ServiceTemplateId("http://opentosca.org/servicetemplates", "SshTest_w1-wip1", false)
        );

        String expectedOutput = "success";

        sshd.setCommandFactory((channel, command) -> new SupportSuccessCommand(command) {
            @Override
            public String getReturnValue() {
                return expectedOutput;
            }
        });

        Session session = getSession(serviceTemplate);
        String pwd = InstanceModelUtils.executeCommand(session, "pwd");

        assertNotNull(pwd);
        assertEquals(expectedOutput, pwd);
    }

    @Test
    void executeErrorCommandTest() throws Exception {
        this.setRevisionTo("origin/plain");
        TServiceTemplate serviceTemplate = this.repository.getElement(
            new ServiceTemplateId("http://opentosca.org/servicetemplates", "SshTest_w1-wip1", false)
        );

        sshd.setCommandFactory((channel, command) -> new SupportErrorCommand(command) {
            @Override
            public String getReturnValue() {
                return "error";
            }
        });

        Session session = getSession(serviceTemplate);
        String pwd = InstanceModelUtils.executeCommand(session, "pwd");

        assertNotNull(pwd);
        assertTrue(pwd.isEmpty());
    }

    private Session getSession(TServiceTemplate serviceTemplate) throws IOException {
        Map<String, String> inputs = new HashMap<>();
        InputStream resourceAsStream = ClassLoader.getSystemClassLoader().getResourceAsStream("winery.test");
        assertNotNull(resourceAsStream);

        inputs.put(InstanceModelUtils.vmPrivateKey, IOUtils.toString(resourceAsStream));
        inputs.put(InstanceModelUtils.vmUser, "test");
        inputs.put(InstanceModelUtils.vmIP, "localhost");
        inputs.put(InstanceModelUtils.vmSshPort, Integer.toString(sshPort));

        List<String> nodeIds = Collections.singletonList("OperatingSystem_0");
        InstanceModelUtils.setUserInputs(inputs, serviceTemplate.getTopologyTemplate(), nodeIds);

        assertNotNull(serviceTemplate.getTopologyTemplate());
        TNodeTemplate node = serviceTemplate.getTopologyTemplate().getNodeTemplate("OperatingSystem_0");
        assertNotNull(node);
        assertNotNull(node.getProperties());
        assertTrue(node.getProperties() instanceof TEntityTemplate.WineryKVProperties);

        assertEquals(inputs, ((TEntityTemplate.WineryKVProperties) node.getProperties()).getKVProperties());
        assertEquals(inputs, InstanceModelUtils.getSSHCredentials(serviceTemplate.getTopologyTemplate(), nodeIds));

        return InstanceModelUtils.createJschSession(serviceTemplate.getTopologyTemplate(), nodeIds);
    }
}
