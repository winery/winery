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
package org.eclipse.winery.repository.rest.resources.entitytemplates.policytemplates;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.eclipse.winery.model.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentInstanceResource;
import org.eclipse.winery.repository.rest.resources._support.IHasName;
import org.eclipse.winery.repository.rest.resources._support.VisualAppearanceResource;
import org.eclipse.winery.repository.rest.resources.entitytemplates.IEntityTemplateResource;
import org.eclipse.winery.repository.rest.resources.entitytemplates.PropertiesResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.boundarydefinitions.PropertyConstraintsResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PolicyTemplateResource extends AbstractComponentInstanceResource implements IEntityTemplateResource<TPolicyTemplate>, IHasName {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyTemplateResource.class);

    /**
     * Constructor has to be public because of test cases
     */
    public PolicyTemplateResource(PolicyTemplateId id) {
        super(id);
    }

    /**
     * Convenience method to avoid casting at the caller's side.
     */
    public TPolicyTemplate getPolicyTemplate() {
        return (TPolicyTemplate) this.getElement();
    }

    @Override
    protected TExtensibleElements createNewElement() {
        return new TPolicyTemplate();
    }

    @Override
    public PropertiesResource getPropertiesResource() {
        return new PropertiesResource(this.getPolicyTemplate(), this);
    }

    @Override
    public String getName() {
        String name = this.getPolicyTemplate().getName();
        if (name == null) {
            return this.getPolicyTemplate().getId();
        } else {
            return name;
        }
    }

    @Override
    public Response setName(String name) {
        this.getPolicyTemplate().setName(name);
        return RestUtils.persist(this);
    }

    @Path("propertyconstraints")
    public PropertyConstraintsResource getPropertyConstraints() {
        TEntityTemplate.PropertyConstraints constraints = this.getPolicyTemplate().getPropertyConstraints();
        if (constraints == null) {
            constraints = new TEntityTemplate.PropertyConstraints();
            this.getPolicyTemplate().setPropertyConstraints(constraints);
        }
        return new PropertyConstraintsResource(constraints, this);
    }

    @Path("appearance")
    public VisualAppearanceResource getVisualAppearanceResource() {
        return new VisualAppearanceResource(this, this.getElement().getOtherAttributes(), this.id);
    }
}
