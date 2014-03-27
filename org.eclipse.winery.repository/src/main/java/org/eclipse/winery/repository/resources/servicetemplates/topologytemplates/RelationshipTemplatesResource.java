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
package org.eclipse.winery.repository.resources.servicetemplates.topologytemplates;

import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.repository.resources.entitytemplates.TEntityTemplatesResource;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource;

import com.sun.jersey.api.NotFoundException;

public class RelationshipTemplatesResource extends TEntityTemplatesResource<TRelationshipTemplate> {
	
	private final TTopologyTemplate topologyTemplate;
	
	
	public RelationshipTemplatesResource(List<TRelationshipTemplate> list, TTopologyTemplate topologyTemplate, ServiceTemplateResource res) {
		super(list, TRelationshipTemplate.class, RelationshipTemplateResource.class, res);
		this.topologyTemplate = topologyTemplate;
	}
	
	@Path("{id}/")
	@Override
	public RelationshipTemplateResource getTEntityTemplateResource(@PathParam("id") String id) {
		id = Util.URLdecode(id);
		List<TEntityTemplate> list = this.topologyTemplate.getNodeTemplateOrRelationshipTemplate();
		for (int i = 0; i < list.size(); i++) {
			TEntityTemplate template = list.get(i);
			if (template instanceof TRelationshipTemplate) {
				if (((TRelationshipTemplate) template).getId().equals(id)) {
					// we illegally convert the List<TEntityTemplate> to List<TRelationshipTemplate>, because we know
					// that the implementation works nevertheless
					// The alternative is to remove type safety at the resource, which brings more disadvantages
					@SuppressWarnings("unchecked")
					List<TRelationshipTemplate> l = (List<TRelationshipTemplate>) (List<?>) list;
					return new RelationshipTemplateResource((TRelationshipTemplate) template, l, i, (ServiceTemplateResource) this.res);
				}
			}
		}
		throw new NotFoundException();
	}
	
}
