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

import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

public class MongoDbHandler implements ImageRefinementHandler {
    private static final String IMAGE_ID_MONGO = "mongo";
    private static final String IMAGE_ID_WEAVE_USER_DB = "weaveworksdemos/user-db:0.3.0";
    private static final QName QNAME_MONGO_DOCKER_CONTAINER = QName.valueOf(
        "{https://examples.opentosca.org/edmm/nodetypes}Mongo-Docker-Container");
    private static final QName QNAME_WEAVE_USER_DB_CONTAINER = QName.valueOf(
        "{https://examples.opentosca.org/edmm/nodetypes}Weaveworks-User-Db-Container");
    private static final QName QNAME_MONGO_DB = QName.valueOf("{http://opentosca.org/nodetypes}MongoDB-Server_3.2");
    private static final QName QNAME_UBUNTU_20_04 = QName.valueOf(
        "{https://examples.opentosca.org/edmm/nodetypes}Ubuntu_20.04.2-w1-wip1");

    @Override
    public Set<String> getTargetImages() {
        HashSet<String> targetImages = new HashSet<>();
        targetImages.add(IMAGE_ID_MONGO);
        targetImages.add(IMAGE_ID_WEAVE_USER_DB);
        return targetImages;
    }

    @Override
    public Set<QName> getProhibitedTypes() {
        return Stream.of(QNAME_MONGO_DOCKER_CONTAINER, QNAME_WEAVE_USER_DB_CONTAINER).collect(Collectors.toSet());
    }

    @Override
    public void handleNode(
        TNodeTemplate dockerContainer,
        TTopologyTemplate topologyTemplate,
        String imageId) {
        IRepository repository = RepositoryFactory.getRepository();

        QName type;
        switch (imageId) {
            case IMAGE_ID_MONGO:
                type = QNAME_MONGO_DOCKER_CONTAINER;
                break;
            case IMAGE_ID_WEAVE_USER_DB:
                type = QNAME_WEAVE_USER_DB_CONTAINER;
                break;
            default:
                type = null;
        }

        if (type != null) {
            dockerContainer.setType(type);
        }

        TNodeType ubuntuType = repository.getElement(new NodeTypeId(QNAME_UBUNTU_20_04));
        TNodeTemplate ubuntu = ModelUtilities.instantiateNodeTemplate(ubuntuType);

        topologyTemplate.addNodeTemplate(ubuntu);

        ModelUtilities.createRelationshipTemplateAndAddToTopology(ubuntu,
            dockerContainer,
            ToscaBaseTypes.hostedOnRelationshipType,
            topologyTemplate);

        TNodeType mongoType = repository.getElement(new NodeTypeId(QNAME_MONGO_DB));
        TNodeTemplate mongo = ModelUtilities.instantiateNodeTemplate(mongoType);

        topologyTemplate.addNodeTemplate(mongo);

        ModelUtilities.createRelationshipTemplateAndAddToTopology(mongo,
            ubuntu,
            ToscaBaseTypes.hostedOnRelationshipType,
            topologyTemplate);
    }
}
