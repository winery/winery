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

package org.eclipse.winery.model.adaptation.enhance;

import java.util.ArrayList;
import java.util.Objects;

import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicies;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TTags;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.OpenToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.RepositoryFactory;

public class EnhancementUtils {

    public static TTopologyTemplate annotateStatefulComponents(TTopologyTemplate topology) {
        for (TNodeTemplate nodeTemplate : topology.getNodeTemplates()) {
            if (isNodeTypeAnnotatedAsStateful(nodeTemplate)) {
                addStatefulPolicy(nodeTemplate);

                ArrayList<TNodeTemplate> hostedOnSuccessors = ModelUtilities.getHostedOnSuccessors(topology, nodeTemplate);
                hostedOnSuccessors.forEach(EnhancementUtils::addStatefulPolicy);
            }
        }

        return topology;
    }

    private static void addStatefulPolicy(TNodeTemplate nodeTemplate) {
        if (Objects.isNull(nodeTemplate.getPolicies())) {
            nodeTemplate.setPolicies(new TPolicies());
        }
        TPolicy stateful = new TPolicy();
        stateful.setPolicyType(OpenToscaBaseTypes.statefulComponentPolicyType);
        nodeTemplate.getPolicies().getPolicy().add(stateful);
    }

    public static boolean isNodeTypeAnnotatedAsStateful(TNodeTemplate nodeTemplate) {
        TTags tags = RepositoryFactory.getRepository().getElement(new NodeTypeId(nodeTemplate.getType())).getTags();
        return Objects.nonNull(tags) &&
            tags.getTag().stream().anyMatch(tag -> "type".equals(tag.getName()) && "stateful".equals(tag.getValue()));
    }
}
