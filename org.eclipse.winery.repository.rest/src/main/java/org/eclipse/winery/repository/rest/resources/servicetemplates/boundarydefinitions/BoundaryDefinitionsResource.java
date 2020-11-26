/*******************************************************************************
 * Copyright (c) 2013-2014 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources.servicetemplates.boundarydefinitions;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.winery.model.tosca.TPolicies;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions.Capabilities;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions.Interfaces;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions.Properties;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions.Properties.PropertyMappings;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions.Requirements;
import org.eclipse.winery.model.tosca.TCapabilityRef;
import org.eclipse.winery.model.tosca.TRequirementRef;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.servicetemplates.ServiceTemplateResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.boundarydefinitions.interfaces.InterfacesResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.boundarydefinitions.policies.PoliciesResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.boundarydefinitions.reqscaps.CapabilitiesResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.boundarydefinitions.reqscaps.RequirementsResource;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.w3c.dom.Document;

public class BoundaryDefinitionsResource {

    private final ServiceTemplateResource serviceTemplateResource;
    private final TBoundaryDefinitions boundaryDefinitions;
    private final IRepository repository;

    public BoundaryDefinitionsResource(ServiceTemplateResource serviceTemplateResource, TBoundaryDefinitions boundaryDefinitions) {
        this.serviceTemplateResource = serviceTemplateResource;
        this.boundaryDefinitions = boundaryDefinitions;
        this.repository = RepositoryFactory.getRepository();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public TBoundaryDefinitions getJSON(@Context UriInfo uriInfo) {
        return new BoundaryDefinitionsJSPData(this.serviceTemplateResource.getServiceTemplate(), uriInfo.getBaseUri(), repository).getDefs();
    }

    @Path("xml/")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXML(@Context UriInfo uriInfo) {
        return new BoundaryDefinitionsJSPData(this.serviceTemplateResource.getServiceTemplate(), uriInfo.getBaseUri(), repository).getBoundaryDefinitionsAsXMLString();
    }

    @PUT
    @ApiOperation(value = "Replaces the boundary definitions by the information given in the XML")
    @Consumes( {MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    public Response setModel(TBoundaryDefinitions boundaryDefinitions) {
        this.serviceTemplateResource.getServiceTemplate().setBoundaryDefinitions(boundaryDefinitions);
        return RestUtils.persist(this.serviceTemplateResource);
    }

    @Path("properties/")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getProperties(@Context UriInfo uriInfo) {
        return new BoundaryDefinitionsJSPData(this.serviceTemplateResource.getServiceTemplate(), uriInfo.getBaseUri(), repository).getPropertiesAsXMLString();
    }

    /**
     * The well-formedness of the XML element is done using the framework. If you see <code>[Fatal Error] :1:19: The
     * prefix "tosca" for element "tosca:properties" is not bound.</code> in the console, it is an indicator that the XML element is not well-formed.
     */
    @Path("properties/")
    @PUT
    @Consumes( {MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    @ApiOperation(value = "saves properties of boundary definitions", notes = "Models the user-defined properties. The property mappings go into a separate resource propertymappings.")
    public Response putProperties(@ApiParam(value = "Stored properties. The XSD allows a single element only. Therefore, we go for the contained element") Document doc) {
        org.eclipse.winery.model.tosca.TBoundaryDefinitions.Properties properties = ModelUtilities.getProperties(this.boundaryDefinitions);
        properties.setAny(doc.getDocumentElement());
        return RestUtils.persist(this.serviceTemplateResource);
    }

    @Path("requirements/")
    public RequirementsResource getRequiremensResource() {
        Requirements requirements = this.boundaryDefinitions.getRequirements();
        if (requirements == null) {
            requirements = new Requirements();
            this.boundaryDefinitions.setRequirements(requirements);
        }
        List<TRequirementRef> refs = requirements.getRequirement();
        return new RequirementsResource(this.serviceTemplateResource, refs);
    }

    @Path("capabilities/")
    public CapabilitiesResource getCapabilitiesResource() {
        Capabilities caps = this.boundaryDefinitions.getCapabilities();
        if (caps == null) {
            caps = new Capabilities();
            this.boundaryDefinitions.setCapabilities(caps);
        }
        List<TCapabilityRef> refs = caps.getCapability();
        return new CapabilitiesResource(this.serviceTemplateResource, refs);
    }

    @Path("policies/")
    public PoliciesResource getPoliciesResource() {
        TPolicies policies = this.boundaryDefinitions.getPolicies();
        if (policies == null) {
            policies = new TPolicies();
            this.boundaryDefinitions.setPolicies(policies);
        }
        return new PoliciesResource(policies.getPolicy(), this.serviceTemplateResource);
    }

    /**
     * This path is below "boundary definitions" to ease implementation If it
     * was modeled following the XSD, it would have been nested below
     * "properties". We did not do that
     */
    @Path("propertymappings/")
    public PropertyMappingsResource getPropertyMappings() {
        Properties properties = this.boundaryDefinitions.getProperties();
        if (properties == null) {
            properties = new Properties();
            this.boundaryDefinitions.setProperties(properties);
        }
        PropertyMappings propertyMappings = properties.getPropertyMappings();
        if (propertyMappings == null) {
            propertyMappings = new PropertyMappings();
            properties.setPropertyMappings(propertyMappings);
        }
        return new PropertyMappingsResource(propertyMappings, this.serviceTemplateResource);
    }

    @Path("propertyconstraints")
    public PropertyConstraintsResource getPropertyConstraints() {
        TBoundaryDefinitions.PropertyConstraints constraints = this.boundaryDefinitions.getPropertyConstraints();
        if (constraints == null) {
            constraints = new TBoundaryDefinitions.PropertyConstraints();
            this.boundaryDefinitions.setPropertyConstraints(constraints);
        }
        return new PropertyConstraintsResource(constraints, this.serviceTemplateResource);
    }

    @Path("interfaces/")
    public InterfacesResource getInterfacesResource() {
        Interfaces interfaces = this.boundaryDefinitions.getInterfaces();
        if (interfaces == null) {
            interfaces = new Interfaces();
            this.boundaryDefinitions.setInterfaces(interfaces);
        }
        return new InterfacesResource(interfaces.getInterface(), this.serviceTemplateResource);
    }
}
