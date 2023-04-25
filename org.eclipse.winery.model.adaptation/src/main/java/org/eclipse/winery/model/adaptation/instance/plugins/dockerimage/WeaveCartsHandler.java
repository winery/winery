/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.adaptation.instance.InstanceModelUtils;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

public class WeaveCartsHandler implements ImageRefinementHandler {
    private static final String IMAGE_ID_WEAVE_CART = "weaveworksdemos/carts:0.4.8";
    private static final String IMAGE_ID_WEAVE_ORDER = "weaveworksdemos/orders:0.4.7";
    private static final String IMAGE_ID_WEAVE_SHIPPING = "weaveworksdemos/shipping:0.4.8";
    private static final QName QNAME_ALPINE_CONTAINER = QName.valueOf(
        "{https://examples.opentosca.org/edmm/nodetypes}Alpine-Container");
    private static final QName QNAME_JAVA8 = QName.valueOf("{http://opentosca.org/nodetypes}Java_8-w1");
    private static final QName QNAME_SPRING_WEB = QName.valueOf("{http://opentosca.org/nodetypes}SpringWebApp_w1");

    @Override
    public Set<String> getTargetImages() {
        return Stream.of(IMAGE_ID_WEAVE_CART, IMAGE_ID_WEAVE_ORDER, IMAGE_ID_WEAVE_SHIPPING)
            .collect(Collectors.toSet());
    }

    @Override
    public Set<QName> getProhibitedTypes() {
        return Stream.of(QNAME_ALPINE_CONTAINER).collect(Collectors.toSet());
    }

    @Override
    public Set<String> handleNode(
        TNodeTemplate dockerContainer,
        TTopologyTemplate topologyTemplate,
        String imageId) {
        Set<String> discoveredNodeIds = new HashSet<>();
        IRepository repository = RepositoryFactory.getRepository();

        dockerContainer.setType(QNAME_ALPINE_CONTAINER);

        TNodeType javaType = repository.getElement(new NodeTypeId(QNAME_JAVA8));
        TNodeTemplate java = ModelUtilities.instantiateNodeTemplate(javaType);
        InstanceModelUtils.setStateRunning(java);

        topologyTemplate.addNodeTemplate(java);

        ModelUtilities.createRelationshipTemplateAndAddToTopology(java,
            dockerContainer,
            ToscaBaseTypes.hostedOnRelationshipType,
            topologyTemplate);

        TNodeType springType = repository.getElement(new NodeTypeId(QNAME_SPRING_WEB));
        TNodeTemplate spring = ModelUtilities.instantiateNodeTemplate(springType);
        spring.setName(dockerContainer.getName());
        InstanceModelUtils.setStateRunning(spring);

        topologyTemplate.addNodeTemplate(spring);

        ModelUtilities.createRelationshipTemplateAndAddToTopology(spring,
            java,
            ToscaBaseTypes.hostedOnRelationshipType,
            topologyTemplate);

        discoveredNodeIds.add(dockerContainer.getId());
        discoveredNodeIds.add(java.getId());
        discoveredNodeIds.add(spring.getId());
        return discoveredNodeIds;
    }
}
