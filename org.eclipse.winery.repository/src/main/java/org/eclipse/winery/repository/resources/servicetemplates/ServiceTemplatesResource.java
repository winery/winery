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
package org.eclipse.winery.repository.resources.servicetemplates;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.eclipse.winery.repository.resources.AbstractComponentsResource;

public class ServiceTemplatesResource extends AbstractComponentsResource {
	
	@Override
	@Path("{namespace}/{id}/")
	public ServiceTemplateResource getComponentInstaceResource(@PathParam("namespace") String namespace, @PathParam("id") String id) {
		return (ServiceTemplateResource) this.getComponentInstaceResource(namespace, id, true);
	}
	
}
