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

package org.eclipse.winery.repository.rest.websockets;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.json.JacksonProvider;
import org.eclipse.winery.model.adaptation.instance.InstanceModelPluginChooser;
import org.eclipse.winery.model.adaptation.instance.InstanceModelRefinement;
import org.eclipse.winery.model.adaptation.instance.InstanceModelRefinementPlugin;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint("/refineInstanceModel")
public class InstanceModelWebSocket extends AbstractWebSocket implements InstanceModelPluginChooser {

    private static final Logger logger = LoggerFactory.getLogger(InstanceModelWebSocket.class);

    private InstanceModelRefinement instanceModelRefiner;
    private boolean running;
    private CompletableFuture<ReceivedData> runPlugin;

    protected void onOpen() {
    }

    public void onMessage(String message, Session session) throws IOException {
        ReceivedData data = JacksonProvider.mapper.readValue(message, ReceivedData.class);

        switch (data.task) {
            case START:
                if (instanceModelRefiner != null) {
                    this.startInstanceModelRefinement(data.serviceTemplate);
                }
                break;
            case APPLY_PLUGIN:
                runPlugin.complete(data);
                break;
            case STOP:
                if (runPlugin != null) {
                    runPlugin.complete(null);
                }
                this.onClose(session);
                break;
        }
    }

    private void startInstanceModelRefinement(QName serviceTemplate) {
        Thread thread = new Thread(() -> {
            this.instanceModelRefiner = new InstanceModelRefinement(this);

            DataToSend dataToSend = new DataToSend();
            dataToSend.topologyTemplate = this.instanceModelRefiner.refine(new ServiceTemplateId(serviceTemplate));

            try {
                this.sendAsync(dataToSend);
                this.onClose(this.session);
            } catch (JsonProcessingException e) {
                logger.error("Error while sending final topology template!", e);
            } catch (IOException e) {
                logger.error("Error while closing web socket!", e);
            }
        });
        thread.start();
    }

    @Override
    public InstanceModelRefinementPlugin selectPlugin(List<InstanceModelRefinementPlugin> plugins) {
        if (plugins != null) {
            try {
                DataToSend dataToSend = new DataToSend();
                dataToSend.plugins = plugins;
                this.sendAsync(dataToSend);

                this.runPlugin = new CompletableFuture<>();
                ReceivedData data = this.runPlugin.get();
                Optional<InstanceModelRefinementPlugin> first = plugins.stream()
                    .filter(plugin -> plugin.getId().equals(data.pluginId))
                    .findFirst();
                if (first.isPresent()) {
                    InstanceModelRefinementPlugin instanceModelRefinementPlugin = first.get();
                    instanceModelRefinementPlugin.setUserInputs(data.userInputs);
                    instanceModelRefinementPlugin.setSelectedMatchId(data.matchId);
                    return instanceModelRefinementPlugin;
                }
            } catch (JsonProcessingException | InterruptedException | ExecutionException e) {
                logger.error("Error while waiting for plugin to be selected", e);
            }
        }

        return null;
    }

    public static class ReceivedData {
        public WebSocketTasks task;
        public QName serviceTemplate;
        public Map<String, String> userInputs;
        public String pluginId;
        public int matchId;
    }

    public static class DataToSend {
        public TTopologyTemplate topologyTemplate;
        public Set<String> requiredUserInputs;
        public List<InstanceModelRefinementPlugin> plugins;
    }

    public enum WebSocketTasks {
        START,
        APPLY_PLUGIN,
        STOP
    }
}
