/*******************************************************************************
 * Copyright (c) 2014 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources.servicetemplates.plans;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.elements.PlanId;
import org.eclipse.winery.repository.Constants;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.Repository;
import org.restdoc.annotations.RestDoc;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

public class PlanFileResource {
	
	private final PlanId planId;
	
	
	public PlanFileResource(PlanId planId) {
		this.planId = planId;
	}
	
	private RepositoryFileReference getFileRef() {
		String fileName = this.planId.getXmlId().getEncoded() + Constants.SUFFIX_BPMN4TOSCA;
		return new RepositoryFileReference(this.planId, fileName);
	}
	
	@PUT
	@Consumes({MediaType.MULTIPART_FORM_DATA})
	@RestDoc(methodDescription = "Resource currently works for BPMN4TOSCA plans only")
	// @formatter:off
	public Response onPutFile(
		@FormDataParam("file") InputStream uploadedInputStream,
		@FormDataParam("file") FormDataContentDisposition fileDetail,
		@FormDataParam("file") FormDataBodyPart body
	) {
	// @formatter:on
		
		// Really store it
		try {
			Repository.INSTANCE.putContentToFile(this.getFileRef(), uploadedInputStream, body.getMediaType());
		} catch (IOException e1) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Could not store plan. " + e1.getMessage()).build();
		}
		
		return Response.noContent().build();
	}
	
	@PUT
	@Consumes({MediaType.APPLICATION_JSON})
	@RestDoc(methodDescription = "Resource currently works for BPMN4TOSCA plans only")
	// @formatter:off
	public Response onPutJson(InputStream is) {
		RepositoryFileReference ref = this.getFileRef();
		return BackendUtils.putContentToFile(ref, is, MediaType.APPLICATION_JSON_TYPE);
	}

	/**
	 * Returns the stored JSON. If file does not exist, "{}" is returned.
	 * If the whole plan does not exist, the parent resource returns 404.
	 */
	@GET
	public Response getFile(@HeaderParam("If-Modified-Since") String modified) {
		RepositoryFileReference ref = this.getFileRef();
		if (Repository.INSTANCE.exists(ref)) {
			return BackendUtils.returnRepoPath(ref, modified);
		} else {
			return Response.ok("{}").build();
		}
	}
}
