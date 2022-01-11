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

package org.eclipse.winery.model.adaptation.problemsolving.algorithms;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.OpenToscaBaseTypes;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;

public class PubSubProxyAlgorithm extends AbstractProxyAlgorithm { 
    
    @Override
    protected boolean insertProxy(TNodeTemplate sourceNode, TNodeTemplate targetNode, TRelationshipTemplate oldConnection, TTopologyTemplate topology) {
        topology.getNodeTemplateOrRelationshipTemplate().remove(oldConnection);

        TNodeTemplate sourceNodeProxy = new TNodeTemplate();
        sourceNodeProxy.setType(OpenToscaBaseTypes.publisherProxy);
        sourceNodeProxy.setName(OpenToscaBaseTypes.publisherProxy.getLocalPart());
        sourceNodeProxy.setId(sourceNode.getId() + "_proxy");
        topology.addNodeTemplate(sourceNodeProxy);
        
        
        TNodeTemplate targetNodeProxy = new TNodeTemplate();
        sourceNodeProxy.setType(OpenToscaBaseTypes.subscriberProxy);
        sourceNodeProxy.setName(OpenToscaBaseTypes.subscriberProxy.getLocalPart());
        sourceNodeProxy.setId(sourceNode.getId() + "_proxy");
        topology.addNodeTemplate(targetNodeProxy);

        ModelUtilities.createRelationshipTemplateAndAddToTopology(sourceNode, sourceNodeProxy,
            ToscaBaseTypes.connectsToRelationshipType, "connectsTo", topology);
        ModelUtilities.createRelationshipTemplateAndAddToTopology(sourceNodeProxy, targetNodeProxy,
            ToscaBaseTypes.connectsToRelationshipType, "connectsTo", topology);
        ModelUtilities.createRelationshipTemplateAndAddToTopology(targetNodeProxy, targetNode,
            ToscaBaseTypes.connectsToRelationshipType, "connectsTo", topology);
        return true;
    }
    
}
