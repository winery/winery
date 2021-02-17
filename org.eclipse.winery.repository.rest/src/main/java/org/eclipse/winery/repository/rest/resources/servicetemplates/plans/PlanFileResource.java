/*******************************************************************************
 * Copyright (c) 2012-2018 Contributors to the Eclipse Foundation
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

import java.io.File;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.model.ids.elements.PlanId;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.repository.backend.constants.MediaTypes;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.servicetemplates.ServiceTemplateResource;

import io.swagger.annotations.ApiOperation;
import org.eclipse.jdt.annotation.Nullable;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

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
    private @Nullable RepositoryFileReference getFileRef() {
        if (this.plan.getPlanModelReference() == null) {
            return null;
        }
        String reference = this.plan.getPlanModelReference().getReference();
        if (reference == null) {
            return null;
        }
        File f = new File(reference);
        return new RepositoryFileReference(this.planId, f.getName());
    }

    @PUT
    @Consumes( {MediaType.MULTIPART_FORM_DATA})
    @ApiOperation(value = "Resource currently works for BPMN4TOSCA plans only")
    public Response onPutFile(
        @FormDataParam("file") InputStream uploadedInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @FormDataParam("file") FormDataBodyPart body
    ) {
        String fileName = fileDetail.getFileName();
        RepositoryFileReference ref = new RepositoryFileReference(this.planId, fileName);

        RepositoryFileReference oldRef = this.getFileRef();
        // if oldRef exists -> check for equality and afterwards overwrite the old content
        if (oldRef != null && !ref.equals(oldRef)) {
            // new filename sent
            RestUtils.delete(oldRef);
            PlansResource.setPlanModelReference(this.plan, this.planId, fileName);
            RestUtils.persist(this.res);
        }

        return RestUtils.putContentToFile(ref, uploadedInputStream, org.apache.tika.mime.MediaType.parse(body.getMediaType().toString()));
    }

    @PUT
    @Consumes( {MediaType.APPLICATION_JSON})
    public Response onPutJSON(InputStream is) {
        RepositoryFileReference ref = this.getFileRef();
        return RestUtils.putContentToFile(ref, is, MediaTypes.MEDIATYPE_APPLICATION_JSON);
    }

    /**
     * Returns the stored file.
     */
    @GET
    public Response getFile(@HeaderParam("If-Modified-Since") String modified) {
        RepositoryFileReference ref = this.getFileRef();
        return RestUtils.returnRepoPath(ref, modified);
    }
}
