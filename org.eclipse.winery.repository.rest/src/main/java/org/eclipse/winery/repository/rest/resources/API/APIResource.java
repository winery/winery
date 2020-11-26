/*******************************************************************************
 * Copyright (c) 2015-2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.API;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.datatypes.select2.Select2DataWithOptGroups;
import org.eclipse.winery.repository.rest.resources.admin.AccountabilityConfigurationResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.ServiceTemplateResource;

import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;

@Api(tags = "API")
public class APIResource {

    @GET
    @Path("getallartifacttemplatesofcontaineddeploymentartifacts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllArtifactTemplatesOfContainedDeploymentArtifacts(@QueryParam("servicetemplate") String serviceTemplateQNameString, @QueryParam("nodetemplateid") String nodeTemplateId) {
        if (StringUtils.isEmpty(serviceTemplateQNameString)) {
            return Response.status(Status.BAD_REQUEST).entity("servicetemplate has be given as query parameter").build();
        }

        QName serviceTemplateQName = QName.valueOf(serviceTemplateQNameString);

        ServiceTemplateId serviceTemplateId = new ServiceTemplateId(serviceTemplateQName);
        final IRepository repository = RepositoryFactory.getRepository();
        if (!repository.exists(serviceTemplateId)) {
            return Response.status(Status.BAD_REQUEST).entity("service template does not exist").build();
        }
        ServiceTemplateResource serviceTemplateResource = new ServiceTemplateResource(serviceTemplateId);

        Collection<QName> artifactTemplates = new ArrayList<>();
        List<TNodeTemplate> allNestedNodeTemplates = BackendUtils.getAllNestedNodeTemplates(serviceTemplateResource.getServiceTemplate());
        for (TNodeTemplate nodeTemplate : allNestedNodeTemplates) {
            if (StringUtils.isEmpty(nodeTemplateId) || nodeTemplate.getId().equals(nodeTemplateId)) {
                Collection<QName> ats = BackendUtils.getArtifactTemplatesOfReferencedDeploymentArtifacts(nodeTemplate, repository);
                artifactTemplates.addAll(ats);
            }
        }

        // convert QName list to select2 data
        Select2DataWithOptGroups res = new Select2DataWithOptGroups();
        for (QName qName : artifactTemplates) {
            res.add(qName.getNamespaceURI(), qName.toString(), qName.getLocalPart());
        }
        return Response.ok().entity(res.asSortedSet()).build();
    }

    /**
     * Implementation similar to
     * getAllArtifactTemplatesOfContainedDeploymentArtifacts. Only difference is
     * "getArtifactTemplatesOfReferencedImplementationArtifacts" instead of
     * "getArtifactTemplatesOfReferencedDeploymentArtifacts".
     */
    @GET
    @Path("getallartifacttemplatesofcontainedimplementationartifacts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllArtifactTemplatesOfContainedImplementationArtifacts(@QueryParam("servicetemplate") String serviceTemplateQNameString, @QueryParam("nodetemplateid") String nodeTemplateId) {
        if (StringUtils.isEmpty(serviceTemplateQNameString)) {
            return Response.status(Status.BAD_REQUEST).entity("servicetemplate has be given as query parameter").build();
        }
        QName serviceTemplateQName = QName.valueOf(serviceTemplateQNameString);

        ServiceTemplateId serviceTemplateId = new ServiceTemplateId(serviceTemplateQName);
        final IRepository repository = RepositoryFactory.getRepository();
        if (!repository.exists(serviceTemplateId)) {
            return Response.status(Status.BAD_REQUEST).entity("service template does not exist").build();
        }
        ServiceTemplateResource serviceTemplateResource = new ServiceTemplateResource(serviceTemplateId);

        Collection<QName> artifactTemplates = new ArrayList<>();
        List<TNodeTemplate> allNestedNodeTemplates = BackendUtils.getAllNestedNodeTemplates(serviceTemplateResource.getServiceTemplate());
        for (TNodeTemplate nodeTemplate : allNestedNodeTemplates) {
            if (StringUtils.isEmpty(nodeTemplateId) || nodeTemplate.getId().equals(nodeTemplateId)) {
                Collection<QName> ats = BackendUtils.getArtifactTemplatesOfReferencedImplementationArtifacts(nodeTemplate, repository);
                artifactTemplates.addAll(ats);
            }
        }

        // convert QName list to select2 data
        Select2DataWithOptGroups res = new Select2DataWithOptGroups();
        for (QName qName : artifactTemplates) {
            res.add(qName.getNamespaceURI(), qName.toString(), qName.getLocalPart());
        }
        return Response.ok().entity(res.asSortedSet()).build();
    }

    @Path("accountability/{accountabilityId}")
    public AccountabilityResource getProvenance(@PathParam("accountabilityId") String accountabilityId) {
        return new AccountabilityResource(accountabilityId);
    }
    
    @Path("accountability/configuration")
    public AccountabilityConfigurationResource getConfiguration() {
        return new AccountabilityConfigurationResource();
    }
}
