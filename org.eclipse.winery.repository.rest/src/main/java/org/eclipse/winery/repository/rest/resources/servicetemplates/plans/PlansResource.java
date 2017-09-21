/*******************************************************************************
 * Copyright (c) 2012-2015 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.servicetemplates.plans;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.XmlId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.common.ids.elements.PlanId;
import org.eclipse.winery.common.ids.elements.PlansId;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPlan.PlanModelReference;
import org.eclipse.winery.model.tosca.constants.Namespaces;
import org.eclipse.winery.repository.Constants;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.constants.MediaTypes;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.collections.EntityCollectionResource;
import org.eclipse.winery.repository.rest.resources._support.collections.withid.EntityWithIdCollectionResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.ServiceTemplateResource;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Presents the plans nested in one Service Template
 */
public class PlansResource extends EntityWithIdCollectionResource<PlanResource, TPlan> {

	private static final Logger LOGGER = LoggerFactory.getLogger(PlansResource.class);

	public PlansResource(List<TPlan> plans, ServiceTemplateResource res) {
		super(PlanResource.class, TPlan.class, plans, res);
	}

	/**
	 * This overrides {@link EntityCollectionResource#addNewElement(java.lang.Object)}.
	 * A special handling for Plans is required as a special validation is in place
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response onPost(TPlan newPlan) {
		if (StringUtils.isEmpty(newPlan.getName())) {
			return Response.status(Status.BAD_REQUEST).entity("planName must be given").build();
		}
		if (StringUtils.isEmpty(newPlan.getPlanType())) {
			return Response.status(Status.BAD_REQUEST).entity("planType must be given").build();
		}
		if (StringUtils.isEmpty(newPlan.getPlanLanguage())) {
			return Response.status(Status.BAD_REQUEST).entity("planLanguage must be given").build();
		}

		// A plan carries both a name and an ID
		// To be user-friendly, we create the ID based on the name
		// the drawback is, that we do not allow two plans with the same name
		// during creation, but allow renaming plans to the same name (as we do
		// not allow ID renaming)
		String xmlId = RestUtils.createXMLidAsString(newPlan.getName());
		newPlan.setId(xmlId);

		this.list.add(newPlan);

		Response response = this.saveFile(newPlan, null, null, null);
		if (response.getStatus() == 204) {
			return Response.created(RestUtils.createURI(Util.URLencode(xmlId))).entity(newPlan).build();
		}

		return response;
	}

	static void setPlanModelReference(TPlan plan, PlanId planId, String fileName) {
		PlanModelReference pref = new PlanModelReference();
		// Set path relative to Definitions/ path inside CSAR.
		pref.setReference("../" + Util.getUrlPath(planId) + fileName);
		plan.setPlanModelReference(pref);
	}

	@Override
	public String getId(TPlan plan) {
		return plan.getId();
	}

	private TPlan getPlanInList(String id) {
		final TPlan[] plan = new TPlan[1];
		this.list.forEach(tPlan -> {
			if (tPlan.getId().equalsIgnoreCase(id)) {
				plan[0] = tPlan;
			}
		});
		return plan[0];
	}

	private Response saveFile(TPlan tPlan, InputStream uploadedInputStream, FormDataContentDisposition fileDetail, FormDataBodyPart body) {
		boolean bpmn4toscaMode = Namespaces.URI_BPMN4TOSCA_20.equals(tPlan.getPlanLanguage());

		if (uploadedInputStream != null || bpmn4toscaMode) {
			// Determine Id
			PlansId plansId = new PlansId((ServiceTemplateId) ((ServiceTemplateResource) this.res).getId());
			PlanId planId = new PlanId(plansId, new XmlId(tPlan.getId(), false));
			// Ensure overwriting
			if (RepositoryFactory.getRepository().exists(planId)) {
				try {
					RepositoryFactory.getRepository().forceDelete(planId);
					// Quick hack to remove the deleted plan from the plans element
					((ServiceTemplateResource) this.res).synchronizeReferences();
				} catch (IOException e) {
					return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
				}
			}

			String fileName;
			if (bpmn4toscaMode) {
				fileName = tPlan.getId() + Constants.SUFFIX_BPMN4TOSCA;
				RepositoryFileReference ref = new RepositoryFileReference(planId, fileName);
				// Errors are ignored in the following call
				RestUtils.putContentToFile(ref, "{}", MediaTypes.MEDIATYPE_APPLICATION_JSON);
			} else {
				// We use the filename also as local file name. Alternatively, we could use the xml id
				// With URL encoding, this should not be an issue
				fileName = Util.URLencode(fileDetail.getFileName());

				// Really store it
				RepositoryFileReference ref = new RepositoryFileReference(planId, fileName);
				// Errors are ignored in the following call
				RestUtils.putContentToFile(ref, uploadedInputStream, body.getMediaType());
			}

			PlansResource.setPlanModelReference(tPlan, planId, fileName);
		}

		return RestUtils.persist(this.res);
	}
}
