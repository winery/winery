/**
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *     Kálmán Képes - refined tag suport
 *     Lukas Balzer - added support for angular frontend
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.tags;

import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.eclipse.winery.model.tosca.TTag;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.repository.rest.resources._support.collections.withoutid.EntityWithoutIdCollectionResource;

public class TagsResource extends EntityWithoutIdCollectionResource<TagResource, TTag> {

	public TagsResource(IPersistable res, List<TTag> list) {
		super(TagResource.class, TTag.class, list, res);
	}

	@Override
	@Path("{id}/")
	public TagResource getEntityResource(@PathParam("id") String id) {
		return this.getEntityResourceFromEncodedId(id);
	}

}
