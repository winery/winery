/*******************************************************************************
 * Copyright (c) 2014-2015 University of Stuttgart.
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

import java.io.File;
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
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;
import org.restdoc.annotations.RestDoc;

public class PlanFileResource {

	private final PlanId planId;
	private TPlan plan;
	private ServiceTemplateResource res;


	public PlanFileResource(ServiceTemplateResource res, PlanId planId, TPlan plan) {
		this.res = res;
		this.planId = planId;
		this.plan = plan;
	}

	/**
	 * Extracts the file reference from plan's planModelReference
	 */
	private RepositoryFileReference getFileRef() {
		String reference = this.plan.getPlanModelReference().getReference();
		File f = new File(reference);
		return new RepositoryFileReference(this.planId, f.getName());
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

		String fileName = fileDetail.getFileName();
		RepositoryFileReference ref = new RepositoryFileReference(this.planId, fileName);
		RepositoryFileReference oldRef = this.getFileRef();
		boolean persistanceNecessary;
		if (ref.equals(oldRef)) {
			// nothing todo, file will be replaced
			persistanceNecessary = false;
		} else {
			// new filename sent
			BackendUtils.delete(oldRef);
			PlansResource.setPlanModelReference(this.plan, this.planId, fileName);
			persistanceNecessary = true;
		}

		// Really store it
		try {
			Repository.INSTANCE.putContentToFile(ref, uploadedInputStream, body.getMediaType());
		} catch (IOException e1) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Could not store plan. " + e1.getMessage()).build();
		}

		if (persistanceNecessary) {
			return BackendUtils.persist(this.res);
		} else {
			return Response.noContent().build();
		}
	}

	@PUT
	@Consumes({MediaType.APPLICATION_JSON})
	// @formatter:off
	public Response onPutJSON(InputStream is) {
		RepositoryFileReference ref = this.getFileRef();
		return BackendUtils.putContentToFile(ref, is, MediaType.APPLICATION_JSON_TYPE);
	}

	/**
	 * Returns the stored file.
	 */
	@GET
	public Response getFile(@HeaderParam("If-Modified-Since") String modified) {
		RepositoryFileReference ref = this.getFileRef();
		return BackendUtils.returnRepoPath(ref, modified);
	}
}
