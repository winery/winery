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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
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

    public static String vmUser = "VMUserName";
    public static String vmPrivateKey = "VMPrivateKey";
    public static String vmIP = "VMIP";
    public static String vmSshPort = "VMSSHPort";

    private static final Logger logger = LoggerFactory.getLogger(InstanceModelUtils.class);

    public static Set<String> getRequiredSSHInputs(TTopologyTemplate topology, List<String> nodeIdsToBeRefined) {
        Set<String> inputs = new HashSet<>();
        Map<String, String> sshCredentials = getSSHCredentials(topology, nodeIdsToBeRefined);
        if (sshCredentials.get(vmPrivateKey) == null || sshCredentials.get(vmPrivateKey).isEmpty()
            || sshCredentials.get(vmPrivateKey).toLowerCase().startsWith("get_input")) {
            inputs.add(vmPrivateKey);
        }
        if (sshCredentials.get(vmUser) == null || sshCredentials.get(vmUser).isEmpty()
            || sshCredentials.get(vmUser).toLowerCase().startsWith("get_input")) {
            inputs.add(vmUser);
        }
        if (sshCredentials.get(vmIP) == null || sshCredentials.get(vmIP).isEmpty()
            || sshCredentials.get(vmIP).toLowerCase().startsWith("get_input")) {
            inputs.add(vmIP);
        }
        return inputs;
    }

    public static Map<String, String> getSSHCredentials(TTopologyTemplate topology, List<String> nodeIdsToBeRefined) {
        Map<String, String> properties = new HashMap<>();

        topology.getNodeTemplates().stream()
            .filter(node -> nodeIdsToBeRefined.contains(node.getId()))
            .forEach(node -> {
                List<TNodeTemplate> hostedOnSuccessors = ModelUtilities.getHostedOnSuccessors(topology, node);
                hostedOnSuccessors.add(node);

                for (TNodeTemplate host : hostedOnSuccessors) {
                    if (host.getProperties() != null && host.getProperties() instanceof TEntityTemplate.WineryKVProperties) {
                        Map<String, String> kvProperties = ((TEntityTemplate.WineryKVProperties) host.getProperties())
                            .getKVProperties();
                        kvProperties.forEach((key, value) -> {
                            if (vmUser.equalsIgnoreCase(key)) {
                                properties.put(vmUser, value);
                            } else if (vmPrivateKey.equalsIgnoreCase(key)) {
                                properties.put(vmPrivateKey, value);
                            } else if (vmIP.equalsIgnoreCase(key)) {
                                properties.put(vmIP, value);
                            } else if (vmSshPort.equalsIgnoreCase(key)) {
                                properties.put(vmSshPort, value);
                            }
                        });
                    }
                }
            });

        return properties;
    }

    public static Session createJschSession(TTopologyTemplate topology, List<String> nodeIdsToBeRefined) {
        Map<String, String> sshCredentials = getSSHCredentials(topology, nodeIdsToBeRefined);

        File key = null;
        try {
            JSch jsch = new JSch();
            key = File.createTempFile("key", "tmp", FileUtils.getTempDirectory());
            FileUtils.write(key, sshCredentials.get(vmPrivateKey), "UTF-8");
            logger.info("tmp key file created: {}", key.exists());

            jsch.addIdentity(key.getAbsolutePath());
            Session session = sshCredentials.containsKey(vmSshPort)
                ? jsch.getSession(sshCredentials.get(vmUser), sshCredentials.get(vmIP), Integer.parseInt(sshCredentials.get(vmSshPort)))
                : jsch.getSession(sshCredentials.get(vmUser), sshCredentials.get(vmIP));
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            return session;
        } catch (JSchException | IOException e) {
            logger.error("Failed to connect to {} using user {}.", sshCredentials.get(vmIP), sshCredentials.get(vmUser), e);
            throw new RuntimeException(e);
        } finally {
            if (key != null) {
                try {
                    FileUtils.forceDelete(key);
                } catch (IOException e) {
                    logger.warn("Could not delete file...");
                }
                logger.info("tmp key file deleted: {}", !key.exists());
            }
        }
    }

    public static void setUserInputs(Map<String, String> userInputs, TTopologyTemplate topology, List<String> nodeIdsToBeRefined) {
        nodeIdsToBeRefined.forEach(nodeId -> {
            TNodeTemplate node = topology.getNodeTemplate(nodeId);
            List<TNodeTemplate> nodes = ModelUtilities.getHostedOnSuccessors(topology, node);
            nodes.add(node);

            nodes.forEach(nodeTemplate -> {
                if (nodeTemplate.getProperties() != null && nodeTemplate.getProperties() instanceof TEntityTemplate.WineryKVProperties) {
                    Map<String, String> kvProperties = ((TEntityTemplate.WineryKVProperties) nodeTemplate.getProperties()).getKVProperties();
                    Optional.ofNullable(userInputs).ifPresent(inputs -> inputs.forEach((key, value) -> {
                        if (kvProperties.containsKey(key)) {
                            kvProperties.put(key, value);
                        }
                    }));
                }
            });
        });
    }

    static String executeCommand(Session session, String command) {
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

    public static List<String> executeCommands(TTopologyTemplate topology, List<String> nodeIdsToBeRefined,
                                               Map<QName, ? extends TEntityType> types, String ...commands) {
        List<String> output = new ArrayList<>();

        // determine whether the host is a Docker Container or a VM
        boolean docker = isDockerContainer(topology, nodeIdsToBeRefined, types);
        
        if (docker) {
            // todo
        } else {
            Session jschSession = createJschSession(topology, nodeIdsToBeRefined);
            for (String command : commands) {
                output.add(executeCommand(jschSession, command));   
            }
            jschSession.disconnect();
        }

        return output;
    }

    private static boolean isDockerContainer(TTopologyTemplate topology, List<String> nodeIdsToBeRefined, Map<QName, ? extends TEntityType> types) {
        return topology.getNodeTemplates().stream()
            .filter(node -> nodeIdsToBeRefined.contains(node.getId()))
            .anyMatch(node -> {
                List<TNodeTemplate> hostedOnSuccessors = ModelUtilities.getHostedOnSuccessors(topology, node);
                hostedOnSuccessors.add(node);

                return hostedOnSuccessors.stream()
                    .anyMatch(host -> ModelUtilities.isOfType(
                        QName.valueOf("{http://opentosca.org/nodetypes}DockerContainer_w1"),
                        host.getType(),
                        types)
                    );
            });
    }
}
