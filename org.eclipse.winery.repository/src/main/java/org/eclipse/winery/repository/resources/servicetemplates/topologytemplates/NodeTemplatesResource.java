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
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.repository.resources.entitytemplates.TEntityTemplatesResource;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource;

import com.sun.jersey.api.NotFoundException;

public class NodeTemplatesResource extends TEntityTemplatesResource<TNodeTemplate> {
	
	private final TTopologyTemplate topologyTemplate;
	
	
	public NodeTemplatesResource(List<TNodeTemplate> list, TTopologyTemplate topologyTemplate, ServiceTemplateResource res) {
		super(list, TNodeTemplate.class, NodeTemplateResource.class, res);
		this.topologyTemplate = topologyTemplate;
	}
	
	@Path("{id}/")
	@Override
	public NodeTemplateResource getTEntityTemplateResource(@PathParam("id") String id) {
		id = Util.URLdecode(id);
		int idx = -1;
		List<TEntityTemplate> list = this.topologyTemplate.getNodeTemplateOrRelationshipTemplate();
		for (TEntityTemplate template : list) {
			idx++;
			if (template instanceof TNodeTemplate) {
				if (((TNodeTemplate) template).getId().equals(id)) {
					// we illegally convert the List<TEntityTemplate> to List<TNodeTemplate>, because we know
					// that the implementation works nevertheless
					// The alternative is to remove type safety at the resource, which brings more disadvantages
					@SuppressWarnings("unchecked")
					List<TNodeTemplate> l = (List<TNodeTemplate>) (List<?>) list;
					return new NodeTemplateResource((TNodeTemplate) template, l, idx, (ServiceTemplateResource) this.res);
				}
			}
		}
		throw new NotFoundException();
	}
	
}
