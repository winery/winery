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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.apiData.InheritanceResourceApiData;
import org.eclipse.winery.repository.rest.resources.servicetemplates.ServiceTemplateResource;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Class for managing inheritance properties: abstract, final, derivedFrom
 * <p>
 * The linking in the resources tree is different than the others. Here, there
 * is no additional Id generated.
 * <p>
 * We separated the code here to have the collection of valid super types in a
 * separate class. We think, this is less confusing than including this
 * functionality in
 * AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinalDefinitionsBacked
 */
public class InheritanceResource {

    /**
     * Private wrapper class for ServiceTemplates to emulate the derivedFrom,
     * abstract and final attributes. For other TOSCA elements, inheritance is supported, but not for service templates.
     *
     * We put the code inside the InheritanceResource as this is a little helper class not be used anywhere else.
     */
    private static class ServiceTemplateResourceWrapper
        extends AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal {
        private ServiceTemplateResource res;

        ServiceTemplateResourceWrapper(ServiceTemplateResource res) {
            super(res.getId());
            this.res = res;
        }

        @Override
        protected TExtensibleElements createNewElement() {
            throw new IllegalStateException("Method should never be called as the wrapper takes care at other places to return the correct service template");
        }

        @Override
        public @Nullable String getDerivedFrom() {
            return this.res.getServiceTemplate().getDerivedFrom();
        }

        @Override
        public @Nullable String getTBoolean(@NonNull String key) {
            if ("getAbstract".equals(key)) {
                return this.res.getServiceTemplate().getAbstract();
            } else if ("getFinal".equals(key)) {
                return this.res.getServiceTemplate().getFinal();
            } else {
                return null;
            }
        }

        @Override
        public Response putInheritance(InheritanceResourceApiData json) {
            this.res.getServiceTemplate().setAbstract(json.isAbstract);
            this.res.getServiceTemplate().setDerivedFrom(json.derivedFrom);
            this.res.getServiceTemplate().setFinal(json.isFinal);
            return RestUtils.persist(res);
        }
    }

    private AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal managedResource;

    public InheritanceResource(AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal res) {
        this.managedResource = res;
    }

    public InheritanceResource(ServiceTemplateResource res) {
        this.managedResource = new ServiceTemplateResourceWrapper(res);
    }

    public String getDerivedFrom() {
        return this.managedResource.getDerivedFrom();
    }

    /**
     * Produces a JSON object containing all necessary data for displaying and editing the inheritance.
     *
     * @return JSON object in the format
     * {
     * "isAbstract": "no",
     * "isFinal": "yes",
     * "derivedFrom": "[QName]"
     * }
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public InheritanceResourceApiData getInheritanceManagementJSON() {
        return new InheritanceResourceApiData(this.managedResource);
    }

    /**
     * Saves the inheritance management from a putted json object in the format:
     * {
     * "isAbstract": "no",
     * "isFinal": "yes",
     * "derivedFrom": "[QName]"
     * }
     *
     * @param json Should at least contain values for abstract, final and QName.
     * @return Response
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveInheritanceManagementFromJSON(InheritanceResourceApiData json) {
        return this.managedResource.putInheritance(json);
    }
}
