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

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

public class WeaveCatalogueHandler implements ImageRefinementHandler {
    private static final String IMAGE_ID_WEAVE_CATALOGUE = "weaveworksdemos/catalogue:0.3.5";
    private static final QName QNAME_WEAVE_CATALOGUE_CONTAINER = QName.valueOf(
        "{https://examples.opentosca.org/edmm/nodetypes}Weaveworks-Catalogue-Container");
    private static final QName QNAME_ALPINE_3_4 = QName.valueOf(
        "{https://examples.opentosca.org/edmm/nodetypes}Alpine_3.4");
    private static final QName QNAME_GO_APP = QName.valueOf(
        "{https://examples.opentosca.org/edmm/nodetypes}Go_Application_w1");

    @Override
    public Set<String> getTargetImages() {
        return Stream.of(IMAGE_ID_WEAVE_CATALOGUE).collect(Collectors.toSet());
    }

    @Override
    public Set<QName> getProhibitedTypes() {
        return Stream.of(QNAME_WEAVE_CATALOGUE_CONTAINER).collect(Collectors.toSet());
    }

    @Override
    public void handleNode(
        TNodeTemplate dockerContainer,
        TTopologyTemplate topologyTemplate, String imageId) {
        IRepository repository = RepositoryFactory.getRepository();

        dockerContainer.setType(QNAME_WEAVE_CATALOGUE_CONTAINER);

        TNodeType alpineType = repository.getElement(new NodeTypeId(QNAME_ALPINE_3_4));
        TNodeTemplate alpine = ModelUtilities.instantiateNodeTemplate(alpineType);

        topologyTemplate.addNodeTemplate(alpine);

        ModelUtilities.createRelationshipTemplateAndAddToTopology(alpine,
            dockerContainer,
            ToscaBaseTypes.hostedOnRelationshipType,
            topologyTemplate);

        TNodeType goAppType = repository.getElement(new NodeTypeId(QNAME_GO_APP));
        TNodeTemplate goApp = ModelUtilities.instantiateNodeTemplate(goAppType);

        topologyTemplate.addNodeTemplate(goApp);

        ModelUtilities.createRelationshipTemplateAndAddToTopology(goApp,
            alpine,
            ToscaBaseTypes.hostedOnRelationshipType,
            topologyTemplate);
    }
}
