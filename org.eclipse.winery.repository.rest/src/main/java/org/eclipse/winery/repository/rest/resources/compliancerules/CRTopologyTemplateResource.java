/********************************************************************************
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
 ********************************************************************************/
package org.eclipse.winery.repository.rest.resources.compliancerules;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.winery.compliance.checking.ComplianceRuleChecker;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.rest.RestUtils;

import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CRTopologyTemplateResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(CRTopologyTemplateResource.class);

	private final TTopologyTemplate topologyTemplate;

	private final ComplianceRuleResource complianceRuleRes;

	/**
	 * A topology template is always nested in a service template
	 */
	public CRTopologyTemplateResource(ComplianceRuleResource parent, TTopologyTemplate template) {
		this.topologyTemplate = template;
		this.complianceRuleRes = parent;
	}

	// @formatter:off
	@GET
	@ApiOperation(value = "Returns a JSON representation of the topology template. <br />" +
		"X and Y coordinates are embedded as attributes. QName string with Namespace: <br />" +
		"{@link org.eclipse.winery.repository.common.constants.Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE} <br />" +
		"@return The JSON representation of the topology template <em>without</em> associated artifacts and without the parent service template")
	@Produces(MediaType.APPLICATION_JSON)
	// @formatter:on
	public Response getComponentInstanceJSON() {
		return Response.ok(this.topologyTemplate).build();
	}

	@PUT
	@ApiOperation(value = "Replaces the topology by the information given in the XML")
	@Consumes(MediaType.TEXT_XML)
	public Response setModel(@Context UriInfo uriInfo, TTopologyTemplate topologyTemplate) {
		return setTopologyAndPersist(uriInfo, topologyTemplate);
	}

	@PUT
	@ApiOperation(value = "Replaces the topology by the information given in the XML")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setModelJson(@Context UriInfo uriInfo, TTopologyTemplate topologyTemplate) throws Exception {
		ModelUtilities.patchAnyAttributes(topologyTemplate.getNodeTemplates());
		setIdentifier(topologyTemplate);
		return setTopologyAndPersist(uriInfo, topologyTemplate);
	}

	public Response setTopologyAndPersist(UriInfo uriInfo, TTopologyTemplate topologyTemplate) {
		String[] split = uriInfo.getPath().split("/");
		String lastSegment = split[split.length - 1];

		if (lastSegment.matches("identifier")) {
			ComplianceRuleChecker checker = new ComplianceRuleChecker(topologyTemplate, this.complianceRuleRes.getCompliancerule().getRequiredStructure(), null);
			if (!checker.isRuleValid()) {
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
			setIdentifier(topologyTemplate);
		} else if (lastSegment.matches("requiredstructure")) {
			ComplianceRuleChecker checker = new ComplianceRuleChecker(this.complianceRuleRes.getCompliancerule().getIdentifier(), topologyTemplate, null);
			if (!checker.isRuleValid()) {
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
			setRequiredStructure(topologyTemplate);
		}
		return persist(this.complianceRuleRes);
	}

	protected void setIdentifier(TTopologyTemplate topologyTemplate) {
		this.complianceRuleRes.getCompliancerule().setIdentifier(topologyTemplate);
	}

	protected void setRequiredStructure(TTopologyTemplate topologyTemplate) {
		this.complianceRuleRes.getCompliancerule().setRequiredStructure(topologyTemplate);
	}

	protected Response persist(ComplianceRuleResource complianceRuleRes) {
		return RestUtils.persist(this.complianceRuleRes);
	}

	// @formatter:off
	@GET
	@ApiOperation(value = "<p>Returns an XML representation of the topology template." +
		" X and Y coordinates are embedded as attributes. Namespace:" +
		"{@link org.eclipse.winery.repository.common.constants.Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE} </p>" +
		"<p>{@link org.eclipse.winery.repository.client.WineryRepositoryClient." +
		"getTopologyTemplate(QName)} consumes this template</p>" +
		"<p>@return The XML representation of the topology template <em>without</em>" +
		"associated artifacts and without the parent service template </p>")
	@Produces({MediaType.APPLICATION_XML, MediaType.TEXT_XML})
	// @formatter:on
	public Response getComponentInstanceXML() {
		return RestUtils.getXML(TTopologyTemplate.class, this.topologyTemplate);
	}
}
