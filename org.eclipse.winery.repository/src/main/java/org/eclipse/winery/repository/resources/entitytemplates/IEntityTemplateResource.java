/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources.entitytemplates;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.repository.resources.AbstractComponentInstanceResource;
import org.eclipse.winery.repository.resources.IHasTypeReference;

/**
 * Interface ensuring that no methods are forgotten when implementing an
 * {@link AbstractComponentInstanceResource}, which is also a template
 */
public interface IEntityTemplateResource<E extends TEntityTemplate> extends IHasTypeReference {

	@Path("properties/")
	PropertiesResource getPropertiesResource();

	@DELETE
	Response onDelete();

}
