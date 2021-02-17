/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.adaptation.problemsolving.algorithms;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.adaptation.problemsolving.ComponentFinding;
import org.eclipse.winery.model.adaptation.problemsolving.SolutionInputData;
import org.eclipse.winery.model.adaptation.problemsolving.SolutionStrategy;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.OpenToscaBaseTypes;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.NamespaceManager;
import org.eclipse.winery.repository.backend.RepositoryFactory;

/**
 * For this algorithm to work, there are some assumptions:
 * <ol>
 *      <li>
 *          The virtual machines which were detected by the problem detector must inherit from
 *          {@link OpenToscaBaseTypes#virtualMachineNodeType}
 *      </li>
 *      <li>
 *          The secure VM type replacing the non-secure ones must
 *          <ol>
 *              <li>inherit from the currently used one</li>
 *              <li>be in another namespace which is flagged as a secure collection</li>
 *          </ol>
 *      </li>
 * </ol>
 */
public class IpSecAlgorithm implements SolutionStrategy {

    @Override
    public boolean applySolution(TTopologyTemplate topology, SolutionInputData inputData) {
        IRepository repository = RepositoryFactory.getRepository();
        NamespaceManager namespaceManager = repository.getNamespaceManager();

        Map<QName, TNodeType> nodeTypes = repository.getQNameToElementMapping(NodeTypeId.class);

        TNodeTemplate[] elements = new TNodeTemplate[2];

        for (int index = 0; index < 2; index++) {
            ComponentFinding componentFinding = inputData.getFindings().get(index);
            elements[index] = getVmHost(topology, componentFinding.getComponentId());

            if (Objects.isNull(elements[index])) {
                elements[index] = topology.getNodeTemplate(componentFinding.getComponentId());
            }

            Map<QName, TNodeType> children = ModelUtilities.getChildrenOf(elements[index].getType(), nodeTypes);

            // simply use the first element
            Optional<Map.Entry<QName, TNodeType>> firstSecure = children.entrySet()
                .stream()
                .filter(
                    entry -> namespaceManager.isSecureCollection(entry.getKey().getNamespaceURI())
                ).findFirst();

            if (firstSecure.isPresent()) {
                ModelUtilities.updateNodeTemplate(
                    topology,
                    elements[index].getId(),
                    firstSecure.get().getKey(),
                    firstSecure.get().getValue()
                );
            } else {
                return false;
            }
        }

        // forward connection
        ModelUtilities.createRelationshipTemplateAndAddToTopology(elements[1], elements[0],
            ToscaBaseTypes.connectsToRelationshipType, "securely_connectsTo", topology);
        // backward connection
        ModelUtilities.createRelationshipTemplateAndAddToTopology(elements[0], elements[1],
            ToscaBaseTypes.connectsToRelationshipType, "securely_connectsTo", topology);

        return true;
    }

    private TNodeTemplate getVmHost(TTopologyTemplate topology, String nodeTemplateId) {
        Map<QName, TNodeType> nodeTypes = RepositoryFactory.getRepository().getQNameToElementMapping(NodeTypeId.class);
        ArrayList<TNodeTemplate> hostedOnSuccessors = ModelUtilities.getHostedOnSuccessors(topology, nodeTemplateId);

        if (!hostedOnSuccessors.isEmpty()) {
            for (TNodeTemplate hostedOn : hostedOnSuccessors) {
                if (ModelUtilities.isOfType(OpenToscaBaseTypes.virtualMachineNodeType, hostedOn.getType(), nodeTypes)) {
                    return hostedOn;
                }
            }
        }

        return null;
    }
}
