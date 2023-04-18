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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.adaptation.instance.InstanceModelRefinementPlugin;
import org.eclipse.winery.model.adaptation.instance.InstanceModelUtils;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.OpenToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

public class DockerLogsRefinementPlugin extends InstanceModelRefinementPlugin {

    private List<DockerLogsAnalyzer> logsAnalyzers;
    private final Map<QName, TNodeType> nodeTypes;

    public DockerLogsRefinementPlugin(Map<QName, TNodeType> nodeTypes) {
        super("DockerLogsRefinement");
        this.nodeTypes = nodeTypes;
        this.logsAnalyzers = List.of(
            // leave in this order!
            new LogAnalyzerSpring(nodeTypes),
            new LogAnalyzerSpringToMongoDB(nodeTypes)
        );
    }

    @Override
    public Set<String> apply(TTopologyTemplate template) {
        Set<String> discoveredNodeIds = new HashSet<>();

        Optional<TNodeTemplate> dockerContainer = InstanceModelUtils.getDockerContainer(template, this.matchToBeRefined.nodeIdsToBeReplaced, this.nodeTypes);
        if (dockerContainer.isPresent()) {
            TNodeTemplate dockerNodeTemplate = dockerContainer.get();

            String dockerLogs = InstanceModelUtils.getDockerLogs(template, this.matchToBeRefined.nodeIdsToBeReplaced);

            for (DockerLogsAnalyzer logsAnalyzer : this.logsAnalyzers) {
                if (logsAnalyzer.analyzeLog(dockerLogs, template, this.matchToBeRefined.nodeIdsToBeReplaced,
                    dockerNodeTemplate.getId(), discoveredNodeIds)
                ) {
                    discoveredNodeIds.add(dockerNodeTemplate.getId());
                }
            }
        }

        return discoveredNodeIds;
    }

    @Override
    public Set<String> determineAdditionalInputs(TTopologyTemplate template, ArrayList<String> nodeIdsToBeReplaced) {
        return InstanceModelUtils.getRequiredDockerTTYInputs(template, nodeIdsToBeReplaced);
    }

    @Override
    protected List<TTopologyTemplate> getDetectorGraphs() {
        IRepository repository = RepositoryFactory.getRepository();

        TNodeType dockerContainerType = repository.getElement(new NodeTypeId(OpenToscaBaseTypes.dockerContainerNodeType));
        TNodeTemplate container = ModelUtilities.instantiateNodeTemplate(dockerContainerType);

        return Collections.singletonList(
            new TTopologyTemplate.Builder()
                .addNodeTemplate(container)
                .build()
        );
    }
}
