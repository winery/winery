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
package org.eclipse.winery.repository.rest.resources._support;

import java.lang.reflect.Method;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.tosca.HasInheritance;
import org.eclipse.winery.model.tosca.HasType;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.apiData.AvailableSuperclassesApiData;
import org.eclipse.winery.repository.rest.resources.apiData.InheritanceResourceApiData;
import org.eclipse.winery.repository.rest.resources.entitytypeimplementations.nodetypeimplementations.NodeTypeImplementationResource;
import org.eclipse.winery.repository.rest.resources.entitytypeimplementations.relationshiptypeimplementations.RelationshipTypeImplementationResource;
import org.eclipse.winery.repository.rest.resources.entitytypes.EntityTypeResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Models a component instance with name, derived from, abstract, and final Tags are provided by
 * AbstractComponentInstanceResource <p> This class mirrors AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinalConfigurationBacked.
 * We did not include interfaces as the getters are currently only called at the jsp.
 */
public abstract class AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal extends AbstractComponentInstanceResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal.class);

    protected AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal(DefinitionsChildId id) {
        super(id);
    }

    /**
     * @return The associated name of this resource. CSDPR01 foresees a NCName name and no ID for an entity type.
     * Therefore, we use the ID as unique identification and convert it to a name when a read request is put.
     */
    @GET
    @Path("name")
    public String getName() {
        return ModelUtilities.getName(this.getElement());
    }

    @PUT
    @Path("name")
    public Response putName(String name) {
        ModelUtilities.setName(this.getElement(), name);
        return RestUtils.persist(this);
    }

    /**
     * @return resource managing abstract, final, derivedFrom
     */
    @Path("inheritance/")
    public InheritanceResource getInheritanceManagement() {
        return new InheritanceResource(this);
    }

    @GET
    @Path("getAvailableSuperClasses")
    @Produces(MediaType.APPLICATION_JSON)
    public AvailableSuperclassesApiData getAvailableSuperClasses() {
        return new AvailableSuperclassesApiData(this);
    }

    public String getDerivedFrom() {
        if (((HasInheritance) this.getElement()).getDerivedFrom() != null) {
            return ((HasInheritance) this.getElement()).getDerivedFrom().getTypeAsQName().toString();
        } else {
            return "(none)";
        }
    }

    /**
     * @param methodName the method to call: getAbstract|getFinal
     * @return {@inheritDoc}
     */
    public String getTBoolean(String methodName) {
        // see getAvailableSuperClasses for verbose comments
        Method method;
        boolean tBoolean;
        try {
            method = this.getElement().getClass().getMethod(methodName);
            tBoolean = (boolean)method.invoke(this.getElement());
        } catch (Exception e) {
            AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal.LOGGER.error("Could not get boolean " + methodName, e);
            throw new IllegalStateException(e);
        }
        return tBoolean ? "yes" : "no";
    }

    /**
     * @return Response
     */
    protected Response putInheritance(InheritanceResourceApiData json) {
        HasType derivedFrom = null;

        // If (none) is selected, derivedFrom needs to be null in order to have valid XML in ALL cases!
        if (!json.derivedFrom.endsWith("(none)")) {
            QName qname = QName.valueOf(json.derivedFrom);
            if (this instanceof EntityTypeResource) {
                derivedFrom = new TEntityType.DerivedFrom();
            } else if (this instanceof RelationshipTypeImplementationResource) {
                derivedFrom = new TRelationshipTypeImplementation.DerivedFrom();
            } else if (this instanceof NodeTypeImplementationResource) {
                derivedFrom = new TNodeTypeImplementation.DerivedFrom();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("Type does not support inheritance!").build();
            }
            derivedFrom.setType(qname);
        }

        HasInheritance element = (HasInheritance) this.getElement();
        element.setDerivedFrom(derivedFrom);
        element.setAbstract(json.isAbstract.equalsIgnoreCase("yes"));
        element.setFinal(json.isFinal.equalsIgnoreCase("yes"));

        return RestUtils.persist(this);
    }
}
