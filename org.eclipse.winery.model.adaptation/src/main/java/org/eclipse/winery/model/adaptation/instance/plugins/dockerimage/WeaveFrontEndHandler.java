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

public class WeaveFrontEndHandler implements ImageRefinementHandler {
    private static final String IMAGE_ID_WEAVE_FRONTEND = "weaveworksdemos/front-end:0.3.12";
    private static final String IMAGE_ID_WEAVE_FRONTEND_ALT = "public.ecr.aws/s9j5x8n9/sock-shop-frontend:0.0.5";
    private static final String IMAGE_ID_WEAVE_FRONTEND_LUKAS = "lharzenetter/sockshopfrontend";
    private static final QName QNAME_ALPINE_CONTAINER = QName.valueOf(
        "{https://examples.opentosca.org/edmm/nodetypes}Alpine-Container");
    private static final QName QNAME_NODEJS_10 = QName.valueOf("{http://opentosca.org/nodetypes}NodeJS_10.0");
    private static final QName QNAME_NODE_APP = QName.valueOf("{http://opentosca.org/nodetypes}NodeJS-App_10.0");

    @Override
    public Set<String> getTargetImages() {
        return Stream.of(IMAGE_ID_WEAVE_FRONTEND, IMAGE_ID_WEAVE_FRONTEND_ALT, IMAGE_ID_WEAVE_FRONTEND_LUKAS).collect(Collectors.toSet());
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

        TNodeType nodeJsType = repository.getElement(new NodeTypeId(QNAME_NODEJS_10));
        TNodeTemplate nodeJs = ModelUtilities.instantiateNodeTemplate(nodeJsType);
        InstanceModelUtils.setStateRunning(nodeJs);

        topologyTemplate.addNodeTemplate(nodeJs);
        nodeJs.setX(dockerContainer.getX());
        nodeJs.setY(String.valueOf(Integer.parseInt(dockerContainer.getY()) - 160));

        ModelUtilities.createRelationshipTemplateAndAddToTopology(nodeJs,
            dockerContainer,
            ToscaBaseTypes.hostedOnRelationshipType,
            topologyTemplate);

        TNodeType nodeAppType = repository.getElement(new NodeTypeId(QNAME_NODE_APP));
        TNodeTemplate nodeApp = ModelUtilities.instantiateNodeTemplate(nodeAppType);
        nodeApp.setName(dockerContainer.getName());
        InstanceModelUtils.setStateRunning(nodeApp);

        topologyTemplate.addNodeTemplate(nodeApp);
        nodeApp.setX(nodeJs.getX());
        nodeApp.setY(String.valueOf(Integer.parseInt(nodeJs.getY()) - 160));

        ModelUtilities.createRelationshipTemplateAndAddToTopology(nodeApp,
            nodeJs,
            ToscaBaseTypes.hostedOnRelationshipType,
            topologyTemplate);

        discoveredNodeIds.add(dockerContainer.getId());
        discoveredNodeIds.add(nodeJs.getId());
        discoveredNodeIds.add(nodeApp.getId());
        return discoveredNodeIds;
    }
}
