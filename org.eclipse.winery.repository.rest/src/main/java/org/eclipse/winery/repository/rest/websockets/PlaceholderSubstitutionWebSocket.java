/*******************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.NotFoundException;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.json.JacksonProvider;
import org.eclipse.winery.model.adaptation.substitution.refinement.placeholder.PlaceholderSubstitution;
import org.eclipse.winery.model.adaptation.substitution.refinement.placeholder.PlaceholderSubstitutionCandidate;
import org.eclipse.winery.model.adaptation.substitution.refinement.placeholder.SubstitutionChooser;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.rest.resources.apiData.PlaceholderSubstitutionElementApiData;
import org.eclipse.winery.repository.rest.resources.apiData.RefinementElementApiData;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint(value = "/substituteplaceholder")
public class PlaceholderSubstitutionWebSocket extends AbstractWebSocket implements SubstitutionChooser {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlaceholderSubstitutionWebSocket.class);
    private CompletableFuture<Integer> future;
    private boolean running = false;
    private PlaceholderSubstitution placeholderSubstitution;
    private ServiceTemplateId substitutionServiceTemplateId;

    @Override
    public PlaceholderSubstitutionCandidate chooseSubstitution(List<PlaceholderSubstitutionCandidate> candidates, ServiceTemplateId substitutionServiceTemplateId) {
        this.substitutionServiceTemplateId = substitutionServiceTemplateId;
        try {
            this.future = new CompletableFuture<>();

            PlaceholderSubstitutionElementApiData element = new PlaceholderSubstitutionElementApiData(candidates, substitutionServiceTemplateId);
            this.sendAsync(element);

            int id = future.get();

            if (id >= 0) {
                return candidates.stream()
                    .filter(candidate -> candidate.getId() == id)
                    .findFirst()
                    .orElseThrow(NotFoundException::new);
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error while retrieving ");
        } catch (JsonProcessingException e) {
            LOGGER.error("Error while creating JSON request", e);
        }

        return null;
    }

    @Override
    protected void onOpen() {
    }

    @Override
    public void onMessage(String message, Session session) throws IOException {
        PlaceholderSubstitutionWebSocket.PlaceholderSubstitutionWebSocketApiData data =
            JacksonProvider.mapper.readValue(message, PlaceholderSubstitutionWebSocket.PlaceholderSubstitutionWebSocketApiData.class);
        this.placeholderSubstitution = new PlaceholderSubstitution(new ServiceTemplateId(data.serviceTemplate), data.subgraphDetector, this);
        
        switch (data.task) {
            case START:
                if (!running) {
                    Thread thread = new Thread(() -> {
                        PlaceholderSubstitutionElementApiData element = new PlaceholderSubstitutionElementApiData();
                        element.serviceTemplateContainingSubstitution = placeholderSubstitution.substituteServiceTemplate();
                        try {
                            this.sendAsync(element);
                            this.session.close();
                            this.session = null;
                        } catch (JsonProcessingException e) {
                            LOGGER.error("Error while sending placeholder substitution result", e);
                        } catch (IOException e) {
                            LOGGER.error("Error while closing the session", e);
                        }
                        running = false;
                    });
                    this.running = true;
                    thread.start();
                }
                break;
            case REFINE_WITH:
                this.future.complete(data.substituteWith);
                break;
            case STOP:
                this.future.complete(-1);
                this.sendAsync(new PlaceholderSubstitutionElementApiData(null, this.substitutionServiceTemplateId));
                this.onClose(this.session);
                break;
        }
    }

    public static class PlaceholderSubstitutionWebSocketApiData {
        public PlaceholderSubstitutionWebSocket.Tasks task;
        public QName serviceTemplate;
        public TTopologyTemplate subgraphDetector;
        public int substituteWith;
    }

    public enum Tasks {
        START,
        REFINE_WITH,
        STOP
    }
}
