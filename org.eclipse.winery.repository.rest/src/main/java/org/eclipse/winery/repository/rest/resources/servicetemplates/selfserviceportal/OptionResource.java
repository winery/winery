/*******************************************************************************
 * Copyright (c) 2012-2013 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.servicetemplates.selfserviceportal;

import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.model.selfservice.ApplicationOption;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.datatypes.ids.elements.SelfServiceMetaDataId;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.collections.IIdDetermination;
import org.eclipse.winery.repository.rest.resources._support.collections.withid.EntityWithIdResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.ServiceTemplateResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.util.List;

public class OptionResource extends EntityWithIdResource<ApplicationOption> {

    static final String ICON_JPG = "icon.jpg";
    static final String PLAN_INPUT_XML = "plan.input.xml";

    private static final Logger LOGGER = LoggerFactory.getLogger(OptionResource.class);

    private SelfServiceMetaDataId ssmdId;


    public OptionResource(IIdDetermination<ApplicationOption> idDetermination, ApplicationOption o, int idx, List<ApplicationOption> list, ServiceTemplateResource res) {
        super(idDetermination, o, idx, list, res);
        this.ssmdId = ((SelfServicePortalResource) this.res).getId();
    }

    private String getFileNamePrefix() {
        return OptionResource.getFileNamePrefix(this.o.getId());
    }

    public static String getFileNamePrefix(String id) {
        return "option_" + id + "_";
    }

    @Path(OptionResource.ICON_JPG)
    @GET
    public Response getIcon(@HeaderParam("If-Modified-Since") String modified) {
        RepositoryFileReference ref = new RepositoryFileReference(this.ssmdId, this.getFileNamePrefix() + OptionResource.ICON_JPG);
        return RestUtils.returnRepoPath(ref, modified);
    }

    @Path("planinputmessage")
    @GET
    public Response getPlanInputMessage(@HeaderParam("If-Modified-Since") String modified) {
        RepositoryFileReference ref = new RepositoryFileReference(this.ssmdId, this.getFileNamePrefix() + OptionResource.PLAN_INPUT_XML);
        return RestUtils.returnRepoPath(ref, modified);
    }

    @Override
    public Response onDelete() {
        // delete icon and plan model reference ...

        // delete icon
        // we use the URL stored in the data instead of the generated URL to be compatible with manually edits
        RepositoryFileReference ref = new RepositoryFileReference(this.ssmdId, this.o.getIconUrl());
        try {
            RepositoryFactory.getRepository().forceDelete(ref);
        } catch (IOException e) {
            OptionResource.LOGGER.error("Could not remove file", e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

        // delete plan input
        // we use the URL stored in the data instead of the generated URL to be compatible with manually edits
        ref = new RepositoryFileReference(this.ssmdId, this.o.getPlanInputMessageUrl());
        try {
            RepositoryFactory.getRepository().forceDelete(ref);
        } catch (IOException e) {
            OptionResource.LOGGER.error("Could not remove file", e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

        // after deleting files, continue with list deletion
        return super.onDelete();
    }
}
