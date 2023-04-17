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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.winery.model.adaptation.instance.plugins.SpringWebAppRefinementPlugin;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogAnalyzerSpring implements DockerLogsAnalyzer {

    private static final Logger logger = LoggerFactory.getLogger(LogAnalyzerSpring.class);

    public LogAnalyzerSpring() {
    }

    @Override
    public boolean analyzeLog(String log, TTopologyTemplate topology, List<String> nodeIdsToBeReplaced,
                              String containerNodeId, Set<String> discoveredNodeIds) {
        Pattern pattern = Pattern.compile("(:: Spring Boot ::)(\\s*)\\((v\\d\\.\\d\\.\\d\\..*)\\)");
        Matcher matcher = pattern.matcher(log);

        if (matcher.find()) {
            String springVersion = matcher.group(3);
            logger.info("Found Spring application in Spring version \"{}\"", springVersion);

            TNodeTemplate containerNode = topology.getNodeTemplate(containerNodeId);
            ArrayList<TNodeTemplate> hostedOnPredecessors = ModelUtilities.getHostedOnPredecessors(topology, containerNode);
            Optional<TNodeTemplate> springWebApp = hostedOnPredecessors.stream()
                .filter(node -> node.getType().getLocalPart().startsWith("SpringWebApp"))
                .findFirst();

            IRepository repo = RepositoryFactory.getRepository();

            TNodeType springType = repo.getElement(new NodeTypeId(SpringWebAppRefinementPlugin.springWebApp));
            TNodeTemplate webApp = ModelUtilities.instantiateNodeTemplate(springType);
            if (springWebApp.isPresent()) {
                webApp = springWebApp.get();
            } else {
                TRelationshipTemplate relationshipTemplate = ModelUtilities.instantiateRelationshipTemplate(
                    repo.getElement(new RelationshipTypeId(ToscaBaseTypes.hostedOnRelationshipType)),
                    webApp,
                    containerNode
                );
                topology.addNodeTemplate(webApp);
                topology.addRelationshipTemplate(relationshipTemplate);
            }

            discoveredNodeIds.add(webApp.getId());
            TEntityTemplate.Properties properties = webApp.getProperties();
            if (properties == null) {
                properties = new TEntityTemplate.WineryKVProperties();
                webApp.setProperties(properties);
            }

            if (properties instanceof TEntityTemplate.WineryKVProperties props) {
                props.getKVProperties()
                    .put("springVersion", springVersion);
            }

            return true;
        }

        return false;
    }
}
