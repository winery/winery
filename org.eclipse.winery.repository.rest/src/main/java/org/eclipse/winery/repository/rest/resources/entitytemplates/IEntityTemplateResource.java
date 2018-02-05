/*******************************************************************************
 * Copyright (c) 2012-2013 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.entitytemplates;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentInstanceResource;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Interface ensuring that no methods are forgotten when implementing an
 * {@link AbstractComponentInstanceResource}, which is also a template
 */
public interface IEntityTemplateResource<E extends TEntityTemplate> {

    @Path("properties/")
    PropertiesResource getPropertiesResource();

    @DELETE
    Response onDelete();

}
