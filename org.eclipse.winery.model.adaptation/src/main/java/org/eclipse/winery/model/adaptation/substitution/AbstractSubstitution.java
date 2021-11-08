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

package org.eclipse.winery.model.adaptation.substitution;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.version.VersionSupport;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractSubstitution {

    private static final Logger LOGGER = LoggerFactory.getLogger(Substitution.class);

    protected final IRepository repository;
    protected final Map<QName, TNodeType> nodeTypes = new HashMap<>();
    protected final Map<QName, TRelationshipType> relationshipTypes = new HashMap<>();

    protected String versionAppendix = "substituted";

    public AbstractSubstitution() {
        this.repository = RepositoryFactory.getRepository();
        this.loadDefinitions();
    }

    private void loadDefinitions() {
        this.nodeTypes.putAll(this.repository.getQNameToElementMapping(NodeTypeId.class));
        this.relationshipTypes.putAll(this.repository.getQNameToElementMapping(RelationshipTypeId.class));
    }

    /**
     * Creates a new version of the given Service Template for the substitution.
     * If no new version can be created, the given Service Template will be returned.
     *
     * @param serviceTemplateId the Service Template containing abstract types to be substituted
     * @return the new Id of the Service Template
     */
    protected ServiceTemplateId getSubstitutionServiceTemplateId(ServiceTemplateId serviceTemplateId) {
        try {
            ServiceTemplateId substitutedServiceTemplateId = new ServiceTemplateId(
                serviceTemplateId.getNamespace().getDecoded(),
                VersionSupport.getNewComponentVersionId(serviceTemplateId, versionAppendix),
                false
            );

            repository.duplicate(serviceTemplateId, substitutedServiceTemplateId);
            LOGGER.debug("Created new Service Template version {}", substitutedServiceTemplateId);

            return substitutedServiceTemplateId;
        } catch (IOException e) {
            LOGGER.debug("Could not create new Service Template version during substitution", e);
            LOGGER.debug("Reusing existing element");
        }

        return serviceTemplateId;
    }
}
