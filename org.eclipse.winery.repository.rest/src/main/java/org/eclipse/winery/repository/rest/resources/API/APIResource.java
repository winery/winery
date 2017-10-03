/*******************************************************************************
 * Copyright (c) 2015-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation, maintenance
 *     Karoline Saatkamp - maintenance
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.API;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.datatypes.select2.Select2DataWithOptGroups;
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
		if (!RepositoryFactory.getRepository().exists(serviceTemplateId)) {
			return Response.status(Status.BAD_REQUEST).entity("service template does not exist").build();
		}
		ServiceTemplateResource serviceTemplateResource = new ServiceTemplateResource(serviceTemplateId);

		Collection<QName> artifactTemplates = new ArrayList<>();
		List<TNodeTemplate> allNestedNodeTemplates = BackendUtils.getAllNestedNodeTemplates(serviceTemplateResource.getServiceTemplate());
		for (TNodeTemplate nodeTemplate : allNestedNodeTemplates) {
			if (StringUtils.isEmpty(nodeTemplateId) || nodeTemplate.getId().equals(nodeTemplateId)) {
				Collection<QName> ats = BackendUtils.getArtifactTemplatesOfReferencedDeploymentArtifacts(nodeTemplate);
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
		if (!RepositoryFactory.getRepository().exists(serviceTemplateId)) {
			return Response.status(Status.BAD_REQUEST).entity("service template does not exist").build();
		}
		ServiceTemplateResource serviceTemplateResource = new ServiceTemplateResource(serviceTemplateId);

		Collection<QName> artifactTemplates = new ArrayList<>();
		List<TNodeTemplate> allNestedNodeTemplates = BackendUtils.getAllNestedNodeTemplates(serviceTemplateResource.getServiceTemplate());
		for (TNodeTemplate nodeTemplate : allNestedNodeTemplates) {
			if (StringUtils.isEmpty(nodeTemplateId) || nodeTemplate.getId().equals(nodeTemplateId)) {
				Collection<QName> ats = BackendUtils.getArtifactTemplatesOfReferencedImplementationArtifacts(nodeTemplate);
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
}
