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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class InstanceModelUtils {

    public static final String VM_USER = "VMUserName";
    public static final String VM_PRIVATE_KEY = "VMPrivateKey";
    public static final String VM_IP = "VMIP";
    public static final String VM_SSH_PORT = "VMSSHPort";

    private static final Logger logger = LoggerFactory.getLogger(InstanceModelUtils.class);

    public static Set<String> getRequiredSSHInputs(TTopologyTemplate template, List<String> nodeIdsToBeReplaced) {
        Set<String> inputs = new HashSet<>();
        Map<String, String> sshCredentials = getSSHCredentials(template, nodeIdsToBeReplaced);
        String extractedPrivateKey = sshCredentials.get(VM_PRIVATE_KEY);
        if (extractedPrivateKey == null || extractedPrivateKey.isEmpty()
            || extractedPrivateKey.toLowerCase().startsWith("get_input")) {
            inputs.add(VM_PRIVATE_KEY);
        }
        String extractedUser = sshCredentials.get(VM_USER);
        if (extractedUser == null || extractedUser.isEmpty()
            || extractedUser.toLowerCase().startsWith("get_input")) {
            inputs.add(VM_USER);
        }
        String extractedIp = sshCredentials.get(VM_IP);
        if (extractedIp == null || extractedIp.isEmpty()
            || extractedIp.toLowerCase().startsWith("get_input")) {
            inputs.add(VM_IP);
        }
        return inputs;
    }

    public static Map<String, String> getSSHCredentials(TTopologyTemplate template, List<String> nodeIdsToBeReplaced) {
        Map<String, String> properties = new HashMap<>();

        template.getNodeTemplates().stream()
            .filter(node -> nodeIdsToBeReplaced.contains(node.getId()))
            .forEach(node -> {
                List<TNodeTemplate> hostedOnSuccessors = ModelUtilities.getHostedOnSuccessors(template, node);
                hostedOnSuccessors.add(node);

                for (TNodeTemplate host : hostedOnSuccessors) {
                    if (host.getProperties() != null && host.getProperties() instanceof TEntityTemplate.WineryKVProperties) {
                        Map<String, String> kvProperties = ((TEntityTemplate.WineryKVProperties) host.getProperties())
                            .getKVProperties();
                        kvProperties.forEach((key, value) -> {
                            if (VM_USER.equalsIgnoreCase(key)) {
                                properties.put(VM_USER, value);
                            } else if (VM_PRIVATE_KEY.equalsIgnoreCase(key)) {
                                properties.put(VM_PRIVATE_KEY, value);
                            } else if (VM_IP.equalsIgnoreCase(key)) {
                                properties.put(VM_IP, value);
                            } else if (VM_SSH_PORT.equalsIgnoreCase(key)) {
                                properties.put(VM_SSH_PORT, value);
                            }
                        });
                    }
                }
            });

        return properties;
    }

    public static Session createJschSession(TTopologyTemplate template, List<String> nodeIdsToBeReplaced) {
        Map<String, String> sshCredentials = getSSHCredentials(template, nodeIdsToBeReplaced);

        try {
            JSch jsch = new JSch();
            File key = File.createTempFile("key", "tmp", FileUtils.getTempDirectory());
            FileUtils.write(key, sshCredentials.get(VM_PRIVATE_KEY), "UTF-8");
            logger.info("tmp key file created: {}", key.exists());

            jsch.addIdentity(key.getAbsolutePath());
            Session session = sshCredentials.containsKey(VM_SSH_PORT)
                ? jsch.getSession(sshCredentials.get(VM_USER),
                sshCredentials.get(VM_IP),
                Integer.parseInt(sshCredentials.get(VM_SSH_PORT)))
                : jsch.getSession(sshCredentials.get(VM_USER), sshCredentials.get(VM_IP));
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            FileUtils.forceDelete(key);
            logger.info("tmp key file deleted: {}", key.exists());

            return session;
        } catch (JSchException | IOException e) {
            logger.error("Failed to connect to {} using user {}.",
                sshCredentials.get(VM_IP),
                sshCredentials.get(VM_USER),
                e);
            throw new RuntimeException(e);
        }
    }

    public static void setUserInputs(
        Map<String, String> userInputs,
        TTopologyTemplate template,
        List<String> nodeIdsToBeReplaced) {
        nodeIdsToBeReplaced.forEach(nodeId -> {
            TNodeTemplate node = template.getNodeTemplate(nodeId);
            List<TNodeTemplate> nodes = ModelUtilities.getHostedOnSuccessors(template, node);
            nodes.add(node);

            nodes.forEach(nodeTemplate -> {
                if (nodeTemplate.getProperties() != null && nodeTemplate.getProperties() instanceof TEntityTemplate.WineryKVProperties) {
                    Map<String, String> kvProperties = ((TEntityTemplate.WineryKVProperties) nodeTemplate.getProperties())
                        .getKVProperties();
                    Optional.ofNullable(userInputs).ifPresent(inputs -> inputs.forEach((key, value) -> {
                        if (kvProperties.containsKey(key)) {
                            kvProperties.put(key, value);
                        }
                    }));
                }
            });
        });
    }

    public static String executeCommand(Session session, String command) {
        ChannelExec channelExec = null;
        try {
            logger.info("Executing script: \"{}\"", command);
            channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(command);

            StringBuilder outputBuffer = new StringBuilder();
            StringBuilder errorBuffer = new StringBuilder();
            InputStream in = channelExec.getInputStream();
            InputStream err = channelExec.getExtInputStream();

            channelExec.connect();
            byte[] tmp = new byte[1024];
            int timer = 0;
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    outputBuffer.append(new String(tmp, 0, i));
                }
                while (err.available() > 0) {
                    int i = err.read(tmp, 0, 1024);
                    if (i < 0) break;
                    errorBuffer.append(new String(tmp, 0, i));
                }
                if (channelExec.isClosed() && in.available() == 0 && err.available() == 0) {
                    break;
                }
                if (timer++ % 5 == 0) {
                    logger.info("Still executing...");
                }
                //noinspection BusyWait
                Thread.sleep(1000);
            }

            if (!errorBuffer.toString().isEmpty()) {
                logger.error(errorBuffer.toString());
            }
            if (!outputBuffer.toString().isEmpty()) {
                logger.info(outputBuffer.toString());
            }

            logger.info("\"{}\" exited with code '{}'", command, channelExec.getExitStatus());

            return outputBuffer.toString().trim();
        } catch (JSchException | InterruptedException | IOException e) {
            throw new RuntimeException("Failed to execute command \"" + command + "\"");
        } finally {
            if (channelExec != null) {
                channelExec.disconnect();
            }
        }
    }
}
