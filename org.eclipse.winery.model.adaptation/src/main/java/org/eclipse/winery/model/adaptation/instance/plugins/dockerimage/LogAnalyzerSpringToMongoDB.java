/*******************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.adaptation.instance.plugins.dockerimage;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.adaptation.instance.InstanceModelUtils;
import org.eclipse.winery.model.adaptation.instance.plugins.SpringWebAppRefinementPlugin;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogAnalyzerSpringToMongoDB implements DockerLogsAnalyzer {

    private static final Logger logger = LoggerFactory.getLogger(LogAnalyzerSpringToMongoDB.class);
    private final Map<QName, TNodeType> nodeTypes;

    private final QName mongoDBServer = QName.valueOf("{http://opentosca.org/nodetypes}MongoDB-Server_w1-wip1");
    private final QName mongoDB = QName.valueOf("{http://opentosca.org/nodetypes}MongoDB_w1-wip1");

    public LogAnalyzerSpringToMongoDB(Map<QName, TNodeType> nodeTypes) {
        this.nodeTypes = nodeTypes;
    }

    @Override
    public boolean analyzeLog(String log, TTopologyTemplate topology, List<String> nodeIdsToBeReplaced,
                              String containerNodeId, Set<String> discoveredNodeIds) {
        Pattern pattern = Pattern.compile("(org\\.mongodb\\.driver\\.connection)(.*)(Opened connection)(.*)(to )(.*)");
        Matcher matcher = pattern.matcher(log);

        if (matcher.find()) {
            String mongoDBConnectionTo = matcher.group(6);
            logger.info("Found connection between Spring app and MongoDB!");
            logger.info("Spring App \"{}\" connects to \"{}\"", containerNodeId, mongoDBConnectionTo);

            TNodeTemplate webApp = InstanceModelUtils.getOrAddNodeTemplateMatchingTypeAndHost(topology, containerNodeId,
                SpringWebAppRefinementPlugin.springWebApp, this.nodeTypes);

            String[] dbAddress = mongoDBConnectionTo.split(":");

            List<TNodeTemplate> dbHostCandidate = topology.getNodeTemplates().stream()
                .filter(node -> node.getName() != null && node.getName().startsWith(dbAddress[0]))
                .toList();

            if (dbHostCandidate.size() == 1) {
                TNodeTemplate dbHost = dbHostCandidate.get(0);
                logger.info("Found candidate: \"{}\"", dbHost.getId());

                List<TNodeTemplate> hostedOnPredecessors = ModelUtilities.getHostedOnPredecessors(topology, dbHost);
                TNodeTemplate dbms = hostedOnPredecessors.stream()
                    .filter(node -> node.getType().getLocalPart().toLowerCase().startsWith("MongoDB-Server_".toLowerCase()))
                    .findFirst()
                    .orElseGet(() -> {
                            TNodeTemplate dbmsNode = InstanceModelUtils.getOrAddNodeTemplateMatchingTypeAndHost(topology, dbHost.getId(), mongoDBServer, this.nodeTypes);
                            discoveredNodeIds.add(dbmsNode.getId());
                            return dbmsNode;
                        }
                    );

                TNodeTemplate db = hostedOnPredecessors.stream()
                    .filter(node -> node.getType().getLocalPart().toLowerCase().startsWith("MongoDB_".toLowerCase()))
                    .findFirst()
                    .orElseGet(() -> {
                            TNodeTemplate dbNode = InstanceModelUtils.getOrAddNodeTemplateMatchingTypeAndHost(topology, dbms.getId(), mongoDB, this.nodeTypes);
                            discoveredNodeIds.add(dbNode.getId());
                            return dbNode;
                        }
                    );

                TRelationshipTemplate relationshipTemplate = ModelUtilities.instantiateRelationshipTemplate(
                    RepositoryFactory.getRepository().getElement(new RelationshipTypeId(ToscaBaseTypes.connectsToRelationshipType)),
                    webApp,
                    db
                );
                topology.addRelationshipTemplate(relationshipTemplate);

                discoveredNodeIds.add(dbHost.getId());
            }
        }

        return false;
    }
}
