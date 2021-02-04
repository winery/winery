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
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.TestWithGitBackedRepository;

import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;
import org.apache.sshd.common.util.ValidateUtils;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
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
            Paths.get(
                Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource("authorized_keys"))
                    .toURI())
            )
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

    public static abstract class SupportCommand implements Command, Runnable {

        private final String command;
        @SuppressWarnings("unused")
        protected InputStream in;
        @SuppressWarnings("unused")
        protected OutputStream out;
        protected OutputStream err;
        private ExitCallback callback;

        public SupportCommand(String command) {
            this.command = ValidateUtils.checkNotNullAndNotEmpty(command, "Received command: " + command);
        }

        public String getCommand() {
            return command;
        }

        public abstract String getReturnValue();

        public abstract OutputStream getStream();

        public abstract int getExitValue();

        @Override
        public void setInputStream(InputStream in) {
            this.in = in;
        }

        @Override
        public void setOutputStream(OutputStream out) {
            this.out = out;
        }

        @Override
        public void setErrorStream(OutputStream err) {
            this.err = err;
        }

        @Override
        public void setExitCallback(ExitCallback callback) {
            this.callback = callback;
        }

        @Override
        public void run() {
            String message = getReturnValue();
            OutputStream output = getStream();
            writeToStream(message, output);
        }

        private void writeToStream(String message, OutputStream output) {
            try {
                try {
                    output.write(message.getBytes(StandardCharsets.UTF_8));
                    output.write('\n');
                } finally {
                    output.flush();
                }
            } catch (IOException e) {
                // ignored
            }

            if (callback != null) {
                callback.onExit(getExitValue());
            }
        }

        @Override
        public void start(ChannelSession channel, Environment env) {
            Thread thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }

        @Override
        public void destroy(ChannelSession channel) {
            // ignored
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getCommand());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }

            return Objects.equals(this.getCommand(), ((SupportCommand) obj).getCommand());
        }

        @Override
        public String toString() {
            return getReturnValue();
        }
    }

    public static abstract class SupportSuccessCommand extends SupportCommand {

        public SupportSuccessCommand(String command) {
            super(command);
        }

        @Override
        public OutputStream getStream() {
            return this.out;
        }

        @Override
        public int getExitValue() {
            return 0;
        }
    }

    public static abstract class SupportErrorCommand extends SupportCommand {

        public SupportErrorCommand(String command) {
            super(command);
        }

        @Override
        public OutputStream getStream() {
            return this.err;
        }

        @Override
        public int getExitValue() {
            return 1;
        }
    }
}
