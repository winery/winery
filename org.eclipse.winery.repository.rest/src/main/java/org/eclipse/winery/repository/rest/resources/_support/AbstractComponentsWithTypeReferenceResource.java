/*******************************************************************************
 * Copyright (c) 2012-2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources._support;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.ids.definitions.EntityTemplateId;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.HasType;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.rest.resources.apiData.QNameWithTypeApiData;

import org.apache.commons.lang3.StringUtils;

/**
 * This class does NOT inherit from TEntityTemplatesResource<ArtifactTemplate> as these templates are directly nested in
 * a TDefinitionsElement
 */
public abstract class AbstractComponentsWithTypeReferenceResource<T extends AbstractComponentInstanceResource> extends AbstractComponentsResource<T> {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response onJsonPost(QNameWithTypeApiData jsonData) {
        // only check for type parameter as namespace and name are checked in super.onPost
        if (StringUtils.isEmpty(jsonData.type)) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        ResourceResult creationResult = super.onPost(jsonData.namespace, jsonData.localname);
        if (!creationResult.isSuccess()) {
            return creationResult.getResponse();
        }
        if (creationResult.getStatus().equals(Status.CREATED)) {
            final DefinitionsChildId id = (DefinitionsChildId) creationResult.getId();
            final Definitions definitions = requestRepository.getDefinitions(id);
            final TExtensibleElements element = definitions.getElement();
            ((HasType) element).setType(jsonData.type);

            // This would be better implemented using inheritance,
            // but this would lead to a huge overhead in the implementation (checking for the creation result etc),
            // thus, we do the quick hack here

            if (id instanceof EntityTemplateId) {
                BackendUtils.initializeProperties(requestRepository, (TEntityTemplate) element);
            }

            try {
                BackendUtils.persist(requestRepository, id, definitions);
            } catch (IOException e) {
                throw new WebApplicationException(e);
            }
        }
        return creationResult.getResponse();
    }
}
