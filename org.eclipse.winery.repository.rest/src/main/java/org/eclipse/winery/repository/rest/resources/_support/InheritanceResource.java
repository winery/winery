/*******************************************************************************
 * Copyright (c) 2012-2017 Contributors to the Eclipse Foundation
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

import org.eclipse.winery.repository.rest.resources.apiData.InheritanceResourceApiData;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

    private AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal managedResource;

    public InheritanceResource(AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal res) {
        this.managedResource = res;
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
