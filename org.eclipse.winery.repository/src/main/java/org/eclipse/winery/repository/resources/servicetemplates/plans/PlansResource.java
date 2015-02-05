/*******************************************************************************
 * Copyright (c) 2012-2015 University of Stuttgart.
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
import java.io.StringWriter;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.XMLId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.common.ids.elements.PlanId;
import org.eclipse.winery.common.ids.elements.PlansId;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPlan.PlanModelReference;
import org.eclipse.winery.repository.Constants;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.resources._support.collections.withid.EntityWithIdCollectionResource;
import org.eclipse.winery.repository.resources.admin.types.PlanLanguagesManager;
import org.eclipse.winery.repository.resources.admin.types.PlanTypesManager;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource;
import org.restdoc.annotations.RestDoc;
import org.restdoc.annotations.RestDocParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

/**
 * Presents the plans nested in one Service Template
 */
public class PlansResource extends EntityWithIdCollectionResource<PlanResource, TPlan> {
	
	private static final Logger logger = LoggerFactory.getLogger(PlansResource.class);
	
	
	public PlansResource(List<TPlan> plans, ServiceTemplateResource res) {
		super(PlanResource.class, TPlan.class, plans, res);
	}
	
	@Override
	public Viewable getHTML() {
		return new Viewable("/jsp/servicetemplates/plans/plans.jsp", new PlansResourceData(this.list));
	}
	
	@POST
	@RestDoc(methodDescription = "<p>Linked plans are currently not supported. Existing plans with the same id are overwritten</p> <p>@return JSON with .tableData: Array with row data for dataTable</p>")
	@Consumes({MediaType.MULTIPART_FORM_DATA})
	@Produces(MediaType.APPLICATION_JSON)
	// the supertype consumes JSON and XML at org.eclipse.winery.repository.resources._support.collections.EntityCollectionResource.addNewElement(EntityT)
	// @formatter:off
	public Response onPost(
		@FormDataParam("planName") String name,
		@FormDataParam("planType") String type,
		@FormDataParam("planLanguage") @RestDocParam(description = "the plan language (e..g, BPMN or BPEL). Full URL.") String language,
		@FormDataParam("file") @RestDocParam(description="(optional in the case of BPMN4TOSCA) file containing the plan.") InputStream uploadedInputStream,
		@FormDataParam("file") FormDataContentDisposition fileDetail,
		@FormDataParam("file") FormDataBodyPart body
	) {
	// @formatter:on
		if (StringUtils.isEmpty(name)) {
			return Response.status(Status.BAD_REQUEST).entity("planName must be given").build();
		}
		if (StringUtils.isEmpty(type)) {
			return Response.status(Status.BAD_REQUEST).entity("planType must be given").build();
		}
		if (StringUtils.isEmpty(language)) {
			return Response.status(Status.BAD_REQUEST).entity("planLanguage must be given").build();
		}
		
		boolean bpmn4toscaMode = org.eclipse.winery.common.constants.Namespaces.URI_BPMN4TOSCA_20.equals(language);
		if (!bpmn4toscaMode) {
			if (uploadedInputStream == null) {
				return Response.status(Status.BAD_REQUEST).entity("file must be given").build();
			}
		}
		
		// A plan carries both a name and an ID
		// To be user-friendly, we create the ID based on the name
		// the drawback is, that we do not allow two plans with the same name
		// during creation, but allow renaming plans to the same name (as we do
		// not allow ID renaming)
		String xmlId = Utils.createXMLidAsString(name);
		
		// BEGIN: Store plan file
		
		// Determine Id
		PlansId plansId = new PlansId((ServiceTemplateId) ((ServiceTemplateResource) this.res).getId());
		PlanId planId = new PlanId(plansId, new XMLId(xmlId, false));
		// Ensure overwriting
		if (Repository.INSTANCE.exists(planId)) {
			try {
				Repository.INSTANCE.forceDelete(planId);
				// Quick hack to remove the deleted plan from the plans element
				((ServiceTemplateResource) this.res).synchronizeReferences();
			} catch (IOException e) {
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
			}
		}
		
		String fileName;
		if (bpmn4toscaMode) {
			fileName = xmlId + Constants.SUFFIX_BPMN4TOSCA;
			RepositoryFileReference ref = new RepositoryFileReference(planId, fileName);
			try {
				Repository.INSTANCE.putContentToFile(ref, "{}", MediaType.APPLICATION_JSON_TYPE);
			} catch (IOException e1) {
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Could not create empty plan. " + e1.getMessage()).build();
			}
		} else {
			// We use the filename also as local file name. Alternatively, we could use the xml id
			// With URL encoding, this should not be an issue
			fileName = Util.URLencode(fileDetail.getFileName());
			
			// Really store it
			RepositoryFileReference ref = new RepositoryFileReference(planId, fileName);
			try {
				Repository.INSTANCE.putContentToFile(ref, uploadedInputStream, body.getMediaType());
			} catch (IOException e1) {
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Could not store plan. " + e1.getMessage()).build();
			}
		}
		// END: Store plan file
		
		TPlan plan = new TPlan();
		plan.setId(xmlId);
		plan.setName(name);
		plan.setPlanType(type);
		plan.setPlanLanguage(language);
		PlansResource.setPlanModelReference(plan, planId, fileName);
		this.list.add(plan);
		
		// prepare result
		JsonFactory jsonFactory = new JsonFactory();
		StringWriter sw = new StringWriter();
		try {
			JsonGenerator jGenerator = jsonFactory.createGenerator(sw);
			jGenerator.writeStartObject();
			jGenerator.writeFieldName("tableData");
			jGenerator.writeStartArray();
			jGenerator.writeString(xmlId);
			jGenerator.writeString(""); // precondition
			jGenerator.writeString(name);
			jGenerator.writeString(PlanTypesManager.INSTANCE.getShortName(type));
			jGenerator.writeString(PlanLanguagesManager.INSTANCE.getShortName(language));
			jGenerator.writeEndArray();
			jGenerator.writeEndObject();
			jGenerator.close();
			sw.close();
		} catch (JsonGenerationException e) {
			PlansResource.logger.error(e.getMessage(), e);
			return Response.serverError().build();
		} catch (IOException e) {
			PlansResource.logger.error(e.getMessage(), e);
			return Response.serverError().build();
		}
		
		Response res = BackendUtils.persist(this.res);
		if (res.getStatus() == 204) {
			// everything OK, return created
			return Response.created(Utils.createURI(Util.URLencode(xmlId))).entity(sw.toString()).build();
		} else {
			return res;
		}
	}
	
	static void setPlanModelReference(TPlan plan, PlanId planId, String fileName) {
		PlanModelReference pref = new PlanModelReference();
		// Set path relative to Definitions/ path inside CSAR.
		pref.setReference("../" + Utils.getURLforPathInsideRepo(BackendUtils.getPathInsideRepo(planId)) + fileName);
		plan.setPlanModelReference(pref);
	}
	
	@Override
	public String getId(TPlan plan) {
		return plan.getId();
	}
}
