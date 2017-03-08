/**
 * Copyright (c) 2012-2013, 2016 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *     Kálmán Képes - refined tag suport
 *******************************************************************************/
package org.eclipse.winery.repository.resources.tags;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.model.tosca.TTag;
import org.eclipse.winery.repository.resources._support.IPersistable;
import org.eclipse.winery.repository.resources._support.collections.CollectionsHelper;
import org.eclipse.winery.repository.resources._support.collections.withoutid.EntityWithoutIdCollectionResource;

import com.sun.jersey.api.view.Viewable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TagsResource extends EntityWithoutIdCollectionResource<TagResource, TTag> {

	private static final Logger LOGGER = LoggerFactory.getLogger(TagsResource.class);

	public TagsResource(IPersistable res, List<TTag> list) {
		super(TagResource.class, TTag.class, list, res);
	}

	public Viewable getHTML() {
		return new Viewable("/jsp/tags/tags.jsp", this);
	}


	/**
	 * Adds an element using form-encoding
	 *
	 * This is necessary as TRequirementRef contains an IDREF and the XML
	 * snippet itself does not contain the target id
	 * @param name the optional name of the requirement
	 * @param value the reference to a requirement in the topology
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response addNewElement(@FormParam("id") String id, @FormParam("name") String name,
			@FormParam("value") String value) {

		TTag tag = new TTag();

		tag.setName(name);
		tag.setValue(value);

		this.list.add(tag);
		return CollectionsHelper.persist(this.res, this, tag);
	}

}
