/*******************************************************************************
 * Copyright (c) 2012-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.entitytypes.policytypes;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TAppliesTo;
import org.eclipse.winery.model.tosca.TPolicyType;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.apiData.QNameApiData;
import org.eclipse.winery.repository.rest.resources.apiData.ValidSourceTypesApiData;

public class AppliesToResource {

    private final PolicyTypeResource policyTypeResource;

    public AppliesToResource(PolicyTypeResource policyTypeResource) {
        this.policyTypeResource = policyTypeResource;
    }
    
    @GET
    public ValidSourceTypesApiData getValidSourceTypes() {
        List<QName> qNames = null;

        if (getPolicyType().getAppliesTo() != null) {
            qNames = getPolicyType()
                .getAppliesTo()
                .getNodeTypeReference()
                .stream()
                .map(TAppliesTo.NodeTypeReference::getTypeRef)
                .collect(Collectors.toList());
        }
        return new ValidSourceTypesApiData(qNames);
    }
    
    @PUT
    public Response saveValidSourceTypes(ValidSourceTypesApiData newValidSourceTypes) {
        TPolicyType t = this.getPolicyType();
        List<TAppliesTo.NodeTypeReference> references = newValidSourceTypes
            .getNodes()
            .stream()
            .map(QNameApiData::asQName)
            .map(qName -> {
                    TAppliesTo.NodeTypeReference ntr = new TAppliesTo.NodeTypeReference();
                    ntr.setTypeRef(qName);
                    return ntr;
                }
            )
            .collect(Collectors.toList());
        TAppliesTo appliesTo = new TAppliesTo();
        appliesTo.getNodeTypeReference().addAll(references);
        t.setAppliesTo(appliesTo);

        return RestUtils.persist(this.policyTypeResource);
    }

    public TPolicyType getPolicyType() {
        return this.policyTypeResource.getPolicyType();
    }
}
