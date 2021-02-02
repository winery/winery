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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.TestWithGitBackedRepository;

import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.config.keys.AuthorizedKeysAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InstanceModelUtilsTest extends TestWithGitBackedRepository {

    private static final int sshPort = 50022;

    private static SshServer sshd;

    @BeforeAll
    static void setUp() throws IOException, URISyntaxException {
        sshd = SshServer.setUpDefaultServer();
        sshd.setHost("localhost");
        sshd.setPort(sshPort);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(new File("host.ser").toPath()));
        sshd.setPublickeyAuthenticator(new AuthorizedKeysAuthenticator(
            Paths.get(ClassLoader.getSystemClassLoader().getResource("authorized_keys").toURI()))
        );
        sshd.start();
    }

    @AfterAll
    static void shutDown() throws IOException {
        if (sshd != null) {
            sshd.stop(true);
        }
    }

    @Test
    void testGetSshProps() throws Exception {
        this.setRevisionTo("origin/plain");
        TServiceTemplate serviceTemplate = this.repository.getElement(
            new ServiceTemplateId("http://opentosca.org/servicetemplates", "SshTest_w1-wip1", false)
        );

        Set<String> sshProps = InstanceModelUtils.getRequiredSSHInputs(serviceTemplate.getTopologyTemplate(), Collections.singletonList("OperatingSystem_0"));

        assertNotNull(sshProps);
        assertEquals(3, sshProps.size());
    }

    @Test
    void getSshConnection() throws Exception {
        this.setRevisionTo("origin/plain");
        TServiceTemplate serviceTemplate = this.repository.getElement(
            new ServiceTemplateId("http://opentosca.org/servicetemplates", "SshTest_w1-wip1", false)
        );

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

        Session jschSession = InstanceModelUtils.createJschSession(serviceTemplate.getTopologyTemplate(), nodeIds);

        assertTrue(jschSession.isConnected());
    }
}
