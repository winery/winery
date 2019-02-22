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

import java.util.Map;
import java.util.Objects;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TPolicies;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.OpenToscaBaseTypes;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

public class EnhancementUtils {

    public static TTopologyTemplate determineStatefulComponents(TTopologyTemplate topology) {
        IRepository repository = RepositoryFactory.getRepository();
        Map<QName, TNodeType> nodeTypes = repository.getQNameToElementMapping(NodeTypeId.class);

        topology.getNodeTemplates().stream()
            .filter(nodeTemplate -> {
                TNodeType type = nodeTypes.get(nodeTemplate.getType());
                if (Objects.nonNull(type.getTags())) {
                    return type.getTags().getTag()
                        .stream()
                        .anyMatch(tag -> "stateful".equals(tag.getName().toLowerCase()));
                }

                return false;
            })
            .forEach(node -> {
                TPolicies policies = node.getPolicies();
                if (Objects.isNull(policies)) {
                    policies = new TPolicies();
                    node.setPolicies(policies);
                }

                TPolicy statefulPolicy = new TPolicy();
                statefulPolicy.setPolicyType(OpenToscaBaseTypes.statefulComponentPolicyType);
                policies.getPolicy()
                    .add(statefulPolicy);
            });

        return topology;
    }
}
