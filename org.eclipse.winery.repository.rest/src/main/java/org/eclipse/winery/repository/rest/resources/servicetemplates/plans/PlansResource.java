/*******************************************************************************
 * Copyright (c) 2012-2019 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.servicetemplates.plans;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.eclipse.winery.common.Constants;
import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.repository.common.Util;
import org.eclipse.winery.common.configuration.Environments;
import org.eclipse.winery.model.ids.EncodingUtil;
import org.eclipse.winery.model.ids.XmlId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.ids.elements.PlanId;
import org.eclipse.winery.model.ids.elements.PlansId;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPlan.PlanModelReference;
import org.eclipse.winery.model.tosca.constants.Namespaces;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.collections.EntityCollectionResource;
import org.eclipse.winery.repository.rest.resources._support.collections.withid.EntityWithIdCollectionResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.ServiceTemplateResource;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
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
     * This overrides {@link EntityCollectionResource#addNewElement(java.lang.Object)}. A special handling for Plans is
     * required as a special validation is in place
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
        String xmlId = RestUtils.createXmlIdAsString(newPlan.getName());
        newPlan.setId(xmlId);

        this.list.add(newPlan);

        Response response = this.saveFile(newPlan, null, null, null);
        if (response.getStatus() == 204) {
            return Response.created(URI.create(EncodingUtil.URLencode(xmlId))).entity(newPlan).build();
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

    private Response saveFile(TPlan tPlan, InputStream uploadedInputStream, FormDataContentDisposition fileDetail,
                              FormDataBodyPart body) {
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
                RestUtils.putContentToFile(ref, "{}", MediaType.APPLICATION_JSON_TYPE);
            } else {
                // We use the filename also as local file name. Alternatively, we could use the xml id
                // With URL encoding, this should not be an issue
                fileName = EncodingUtil.URLencode(fileDetail.getFileName());

                // Really store it
                RepositoryFileReference ref = new RepositoryFileReference(planId, fileName);
                // Errors are ignored in the following call
                RestUtils.putContentToFile(ref, uploadedInputStream, body.getMediaType());
            }

            PlansResource.setPlanModelReference(tPlan, planId, fileName);
        }

        return RestUtils.persist(this.res);
    }

    @Override
    @Path("{id}/")
    public PlanResource getEntityResource(@PathParam("id") String id) {
        return this.getEntityResourceFromEncodedId(id);
    }

    /**
     * Endpoint to generate management plans using the container's plan builder. The plan builder generates all
     * supported management plans (e.g., build plans, termination plans) at once and uploads it into Winery.
     *
     * @param uriInfo the {@link UriInfo} object for this endpoint
     */
    @POST
    @Path("generate")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces( {MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    public Response generatePlans(@Context UriInfo uriInfo) {
        LOGGER.info("Generating plans for Service Template...");
        // Only if plan builder endpoint is available
        String planBuilderBaseUrl =
            Environments.getInstance().getUiConfig().getEndpoints().get("container") + "/containerapi/planbuilder";
        if (RestUtils.isResourceAvailable(planBuilderBaseUrl)) {
            // Determine URIs
            String plansURI = uriInfo.getAbsolutePath().resolve("../plans").toString();
            String csarURI = uriInfo.getAbsolutePath().resolve("../?csar").toString();
            // Prepare XML request
            String request = "<generatePlanForTopology><CSARURL>";
            request += csarURI;
            request += "</CSARURL><PLANPOSTURL>";
            request += plansURI;
            request += "</PLANPOSTURL></generatePlanForTopology>";
            // Create client and execute request (handle exceptions generally)
            ClientBuilder.newClient()
                .target(planBuilderBaseUrl + "/sync")
                .request()
                .post(Entity.xml(request), String.class);
            LOGGER.info("Plans successfully generated");
        } else {
            String message = "Plan Builder service is not available. No plans were generated.";
            LOGGER.warn(message);
            return Response.serverError().entity(message).build();
        }
        return Response.noContent().build();
    }
}
