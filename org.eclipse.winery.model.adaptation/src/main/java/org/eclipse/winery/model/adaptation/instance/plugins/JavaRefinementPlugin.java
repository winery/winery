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

package org.eclipse.winery.model.adaptation.instance.plugins;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.adaptation.instance.InstanceModelRefinementPlugin;
import org.eclipse.winery.model.adaptation.instance.InstanceModelUtils;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.OpenToscaBaseTypes;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import static org.eclipse.winery.model.adaptation.instance.InstanceModelUtils.getClosestVersionMatchOfVersion;

public class JavaRefinementPlugin extends InstanceModelRefinementPlugin {

    private final Map<QName, TNodeType> nodeTypes;

    public JavaRefinementPlugin(Map<QName, TNodeType> nodeTypes) {
        super("Java");
        this.nodeTypes = nodeTypes;
    }

    @Override
    public Set<String> apply(TTopologyTemplate topology) {
        Set<String> discoveredNodeIds = new HashSet<>();

        List<String> output = InstanceModelUtils.executeCommands(topology, this.matchToBeRefined.nodeIdsToBeReplaced, this.nodeTypes,
            "java -version");

        String javaOutput = output.get(0).split("\n")[0];

        if (javaOutput != null && !javaOutput.isBlank() && !javaOutput.toLowerCase().contains("does not exist")
            && !javaOutput.toLowerCase().contains("not found")) {
            TNodeTemplate[] host = new TNodeTemplate[1];

            Pattern pattern = Pattern.compile("(\\d*\\.(\\d|\\.)*)");
            Matcher matcher = pattern.matcher(javaOutput);

            if (matcher.find()) {
                String javaVersion = matcher.group(1).startsWith("1.") ? matcher.group(1).substring(2) : matcher.group(1);
                QName javaQName = getClosestVersionMatchOfVersion(OpenToscaBaseTypes.OT_Namespace, "Java", javaVersion, this.nodeTypes);

                TNodeTemplate java = topology.getNodeTemplates().stream()
                    .filter(node -> this.matchToBeRefined.nodeIdsToBeReplaced.contains(node.getId()))
                    // Because of the detectors, it can only be one node which is the host.
                    .peek(node -> host[0] = node)
                    .flatMap(node -> ModelUtilities.getHostedOnPredecessors(topology, node).stream())
                    .distinct()
                    .filter(node -> node.getType().getLocalPart().toLowerCase().startsWith("java_"))
                    .findFirst()
                    .orElseGet(() -> {
                        TNodeType javaNodeType = RepositoryFactory.getRepository().getElement(new NodeTypeId(javaQName));
                        TNodeTemplate javaNode = ModelUtilities.instantiateNodeTemplate(javaNodeType);
                        InstanceModelUtils.setStateRunning(javaNode);
                        InstanceModelUtils.addNodeAsHostedOnSuccessor(topology, host[0], javaNode);
                        discoveredNodeIds.add(javaNode.getId());
                        discoveredNodeIds.add(host[0].getId());

                        return javaNode;
                    });

                if (!java.getType().equals(javaQName)) {
                    discoveredNodeIds.add(java.getId());
                    discoveredNodeIds.add(host[0].getId());
                    java.setType(javaQName);
                }
            }
        }

        return discoveredNodeIds;
    }

    @Override
    protected List<TTopologyTemplate> getDetectorGraphs() {
        IRepository repository = RepositoryFactory.getRepository();

        TNodeType dockerContainerType = repository.getElement(new NodeTypeId(OpenToscaBaseTypes.dockerContainerNodeType));
        TNodeTemplate container = ModelUtilities.instantiateNodeTemplate(dockerContainerType);

        TNodeType computeType = repository.getElement(new NodeTypeId(ToscaBaseTypes.compute));
        TNodeTemplate compute = ModelUtilities.instantiateNodeTemplate(computeType);

        return List.of(
            new TTopologyTemplate.Builder()
                .addNodeTemplate(container)
                .build(),
            new TTopologyTemplate.Builder()
                .addNodeTemplate(compute)
                .build()
        );
    }
}
