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

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.adaptation.problemsolving.ComponentFinding;
import org.eclipse.winery.model.adaptation.problemsolving.SolutionInputData;
import org.eclipse.winery.model.adaptation.problemsolving.SolutionStrategy;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.NamespaceManager;
import org.eclipse.winery.repository.backend.RepositoryFactory;

public class IpSecAlgorithm implements SolutionStrategy {

    @Override
    public boolean applySolution(TTopologyTemplate topology, SolutionInputData inputData) {
        IRepository repository = RepositoryFactory.getRepository();
        NamespaceManager namespaceManager = repository.getNamespaceManager();

        Map<QName, TNodeType> nodeTypes = repository.getQNameToElementMapping(NodeTypeId.class);

        TNodeTemplate[] elements = new TNodeTemplate[2];
        
        for (int index = 0; index < 2; index++) {
            ComponentFinding componentFinding  = inputData.getFindings().get(index);
            QName currentNodeType = topology.getNodeTemplate(componentFinding.getComponentId()).getType();

            if (ModelUtilities.isOfType(QName.valueOf("{http://opentosca.org/baseelements/nodetypes}VM"), currentNodeType, nodeTypes)) {
                Map<QName, TNodeType> children = ModelUtilities.getChildrenOf(currentNodeType, nodeTypes);

                // simply use the first element
                Optional<Map.Entry<QName, TNodeType>> firstSecure = children.entrySet()
                    .stream()
                    .filter(
                        entry -> namespaceManager.isSecureCollection(entry.getKey().getNamespaceURI())
                    ).findFirst();

                if (firstSecure.isPresent()) {
                    ModelUtilities.updateNodeTemplate(
                        topology,
                        componentFinding.getComponentId(),
                        firstSecure.get().getKey(),
                        firstSecure.get().getValue()
                    );
                } else {
                    return false;
                }
                
                elements[index] = topology.getNodeTemplate(componentFinding.getComponentId());
            }
        }
        
        if (Objects.isNull(elements[0]) || Objects.isNull(elements[1])) {
            return false;
        }

        TRelationshipTemplate forward = new TRelationshipTemplate();
        forward.setType(QName.valueOf("{http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes}ConnectsTo"));
        forward.setId(elements[0].getId() + "_securely-connectsTo_" + elements[1].getId());
        forward.setTargetNodeTemplate(elements[0]);
        forward.setSourceNodeTemplate(elements[1]);
        
        TRelationshipTemplate backward = new TRelationshipTemplate();
        backward.setType(QName.valueOf("{http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes}ConnectsTo"));
        backward.setId(elements[1].getId() + "_securely-connectsTo_" + elements[0].getId());
        backward.setTargetNodeTemplate(elements[1]);
        backward.setSourceNodeTemplate(elements[0]);
        
        topology.addRelationshipTemplate(forward);
        topology.addRelationshipTemplate(backward);

        return true;
    }
}
