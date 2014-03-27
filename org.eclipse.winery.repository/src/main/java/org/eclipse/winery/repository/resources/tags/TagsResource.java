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
package org.eclipse.winery.repository.resources.tags;

import java.util.Iterator;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.backend.constants.Filename;
import org.eclipse.winery.repository.resources.GenericKeyValueResource;
import org.restdoc.annotations.RestDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.jersey.api.view.Viewable;

public class TagsResource extends GenericKeyValueResource {
	
	private static final Logger logger = LoggerFactory.getLogger(TagsResource.class);
	
	
	public TagsResource(TOSCAComponentId parentId) {
		super(new RepositoryFileReference(parentId, Filename.FILENAME_PROPERTIES_TAGS));
	}
	
	@GET
	@RestDoc(methodDescription = "Returns a JSON object containing all tags withOUT their values  { ('<name>', )* }")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllTags() {
		Iterator<String> keys = this.configuration.getKeys();
		String resStr;
		try {
			resStr = Utils.mapper.writeValueAsString(keys);
		} catch (JsonProcessingException e) {
			TagsResource.logger.error(e.getMessage(), e);
			resStr = "[]";
		}
		return Response.ok().entity(resStr).build();
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Viewable getHTML() {
		return new Viewable("/jsp/tags/tags.jsp", this);
	}
	
}
