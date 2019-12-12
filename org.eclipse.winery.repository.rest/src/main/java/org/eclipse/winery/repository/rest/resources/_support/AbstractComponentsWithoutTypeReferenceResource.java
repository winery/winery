/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.resources.apiData.QNameApiData;

/**
 * This class does NOT inherit from TEntityTemplatesResource<ArtifactTemplate> as these templates are directly nested in
 * a TDefinitionsElement
 */
public abstract class AbstractComponentsWithoutTypeReferenceResource<T extends AbstractComponentInstanceResource> extends AbstractComponentsResource<T> {

    /**
     * Creates a new component instance in the given namespace
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response onJsonPost(QNameApiData jsonData) {
        ResourceResult creationResult = super.onPost(jsonData.namespace, jsonData.localname);
        DefinitionsChildId definitionsChildId = this.getDefinitionsChildId(jsonData.namespace, jsonData.localname, false);
        creationResult.setMessage(RepositoryFactory.getRepository().getElement(definitionsChildId));
        return creationResult.getResponse();
    }
}
