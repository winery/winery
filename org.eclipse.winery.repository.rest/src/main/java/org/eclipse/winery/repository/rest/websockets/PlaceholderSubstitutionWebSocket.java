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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.NotFoundException;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.json.JacksonProvider;
import org.eclipse.winery.model.adaptation.substitution.refinement.placeholder.PlaceholderSubstitution;
import org.eclipse.winery.model.adaptation.substitution.refinement.placeholder.PlaceholderSubstitutionCandidate;
import org.eclipse.winery.model.adaptation.substitution.refinement.placeholder.PlaceholderSubstitutionException;
import org.eclipse.winery.model.adaptation.substitution.refinement.placeholder.SubstitutionChooser;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.resources.apiData.PlaceholderSubstitutionElementApiData;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint(value = "/substitutePlaceholder")
public class PlaceholderSubstitutionWebSocket extends AbstractWebSocket implements SubstitutionChooser {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlaceholderSubstitutionWebSocket.class);
    private CompletableFuture<Integer> future;
    private boolean running = false;
    private PlaceholderSubstitution placeholderSubstitution;
    private ServiceTemplateId substitutionServiceTemplateId;

    @Override
    public PlaceholderSubstitutionCandidate chooseSubstitution(List<PlaceholderSubstitutionCandidate> candidates, ServiceTemplateId substitutionServiceTemplateId, TTopologyTemplate currentTopology) {
        this.substitutionServiceTemplateId = substitutionServiceTemplateId;
        try {
            this.future = new CompletableFuture<>();

            PlaceholderSubstitutionElementApiData element = new PlaceholderSubstitutionElementApiData(candidates, substitutionServiceTemplateId, currentTopology, null);
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
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        PlaceholderSubstitutionWebSocket.PlaceholderSubstitutionWebSocketApiData data =
            JacksonProvider.mapper.readValue(message, PlaceholderSubstitutionWebSocket.PlaceholderSubstitutionWebSocketApiData.class);
        if (this.placeholderSubstitution == null) {
            this.placeholderSubstitution = new PlaceholderSubstitution(new ServiceTemplateId(data.serviceTemplate), this);
        }
        switch (data.task) {
            case START:
                if (!running) {
                    Thread thread = new Thread(() -> {
                        TServiceTemplate serviceTemplate = RepositoryFactory.getRepository().getElement(new ServiceTemplateId(data.serviceTemplate));
                        PlaceholderSubstitutionElementApiData element = new PlaceholderSubstitutionElementApiData();
                        TTopologyTemplate subgraphDetector = null;
                        try {
                            subgraphDetector = this.getSubgraphDetector(serviceTemplate, data.selectedNodeTemplateIds);
                            element.serviceTemplateContainingSubstitution = placeholderSubstitution.substituteServiceTemplate(subgraphDetector);
                            element.currentTopology = RepositoryFactory.getRepository().getElement(element.serviceTemplateContainingSubstitution).getTopologyTemplate();
                        } catch (PlaceholderSubstitutionException e) {
                            try {
                                PlaceholderSubstitutionElementApiData error = new PlaceholderSubstitutionElementApiData(null, null, null, e.getMessage());
                                LOGGER.error(e.getMessage());
                                running = false;
                                this.sendAsync(error);
                            } catch (JsonProcessingException jsonProcessingException) {
                                jsonProcessingException.printStackTrace();
                            }
                        }

                        try {
                            running = false;
                            this.sendAsync(element);
                        } catch (JsonProcessingException e) {
                            LOGGER.error("Error while sending placeholder substitution result", e);
                        }
                    });
                    this.running = true;
                    thread.start();
                }
                break;
            case REFINE_WITH:
                this.future.complete(data.refineWith);
                break;
            case STOP:
                this.future.complete(-1);
                this.sendAsync(new PlaceholderSubstitutionElementApiData(null, this.substitutionServiceTemplateId, null, null));
                this.onClose(this.session);
                break;
        }
    }

    public static class PlaceholderSubstitutionWebSocketApiData {
        public PlaceholderSubstitutionWebSocket.Tasks task;
        public QName serviceTemplate;
        public ArrayList<String> selectedNodeTemplateIds;
        public int refineWith;
    }

    public enum Tasks {
        START,
        REFINE_WITH,
        STOP
    }

    private TTopologyTemplate getSubgraphDetector(TServiceTemplate serviceTemplate, ArrayList<String> nodeTemplateIDs) throws PlaceholderSubstitutionException {
        ArrayList<TNodeTemplate> listOfSelectedNodeTemplate = new ArrayList<>();
        nodeTemplateIDs.stream()
            .forEach(id -> listOfSelectedNodeTemplate.add(serviceTemplate.getTopologyTemplate().getNodeTemplate(id)));

        //participant-aware placeholder selection - only nodes with the placeholder owning the service template are considered
        if (ModelUtilities.getOwnerParticipantOfServiceTemplate(serviceTemplate) != null) {
            boolean wrongParticipantSelection = listOfSelectedNodeTemplate.stream().anyMatch(nt -> ModelUtilities.getParticipant(nt).isPresent() &&
                !ModelUtilities.getParticipant(nt).get().equalsIgnoreCase(ModelUtilities.getOwnerParticipantOfServiceTemplate(serviceTemplate)));
            if (wrongParticipantSelection) {
                throw new PlaceholderSubstitutionException("Substitution cannot be executed: Nodes which are owned by other participants have been selected");
            }
        }

        List<TRelationshipTemplate> relationsBetweenNodes = serviceTemplate.getTopologyTemplate().getRelationshipTemplates().stream().filter(rt ->
            listOfSelectedNodeTemplate.contains(ModelUtilities.getNodeTemplateFromRelationshipSourceOrTarget(serviceTemplate.getTopologyTemplate(), rt.getSourceElement().getRef()))
                && listOfSelectedNodeTemplate.contains(ModelUtilities.getNodeTemplateFromRelationshipSourceOrTarget(serviceTemplate.getTopologyTemplate(), rt.getTargetElement().getRef()))).collect(Collectors.toList());

        return new TTopologyTemplate.Builder()
            .addNodeTemplates(listOfSelectedNodeTemplate)
            .addRelationshipTemplates(relationsBetweenNodes).build();
    }
}
