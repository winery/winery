/*******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.threats;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.model.threatmodeling.Threat;
import org.eclipse.winery.model.threatmodeling.ThreatCreationApiData;
import org.eclipse.winery.model.threatmodeling.ThreatModelingUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

public class ThreatsResource {

    // reuse repository
    private IRepository repository = RepositoryFactory.getRepository();

    /**
     * generate list of all threat policy templates in the repository
     */
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public List<Threat> threatCatalogue() {
        ThreatModelingUtils tmu = new ThreatModelingUtils(this.repository);
        return tmu.getThreatCatalogue();
    }

    /**
     * create new threat based on submitted form
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response threatCreation(
        ThreatCreationApiData data
    ) {
        ThreatModelingUtils tmu = new ThreatModelingUtils(this.repository);
        String msg;
        try {
            msg = tmu.createThreatAndMitigationTemplates(data);
        } catch (IOException e) {
            return Response.status(400).build();
        }
        return Response.ok(msg).build();
    }

    /**
     * create all required policy and node types for threat modeling
     */
    @Path("setup/")
    @Produces(MediaType.TEXT_PLAIN)
    @GET
    public Response threatModelingSetup() {
        ThreatModelingUtils tmu = new ThreatModelingUtils(this.repository);
        try {
            tmu.setupThreatModelingTypes();
        } catch (Exception e) {
            return Response.ok(e.getMessage()).build();
        }
        return Response.ok("Threat modeling was set up successfully!").build();
    }
}
