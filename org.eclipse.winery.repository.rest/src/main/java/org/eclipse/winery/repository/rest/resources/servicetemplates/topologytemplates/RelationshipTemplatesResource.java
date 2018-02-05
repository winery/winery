/*******************************************************************************
 * Copyright (c) 2012-2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.servicetemplates.topologytemplates;

import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.repository.rest.resources.entitytemplates.TEntityTemplatesResource;

public class RelationshipTemplatesResource extends TEntityTemplatesResource<RelationshipTemplateResource, TRelationshipTemplate> {

	public RelationshipTemplatesResource(List<TRelationshipTemplate> list, IPersistable res) {
		super(RelationshipTemplateResource.class, TRelationshipTemplate.class, list, res);
	}

	@Override
	public String getId(TRelationshipTemplate entity) {
		return entity.getId();
	}

	@Override
	@Path("{id}/")
	public RelationshipTemplateResource getEntityResource(@PathParam("id") String id) {
		return this.getEntityResourceFromEncodedId(id);
	}
}
