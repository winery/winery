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
package org.eclipse.winery.repository.rest.resources.entitytypes.policytypes;

import org.eclipse.winery.model.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.model.ids.definitions.PolicyTypeId;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TPolicyType;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;
import org.eclipse.winery.repository.rest.datatypes.select2.Select2OptGroup;
import org.eclipse.winery.repository.rest.resources._support.VisualAppearanceResource;
import org.eclipse.winery.repository.rest.resources.entitytypes.EntityTypeResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import java.util.SortedSet;

public final class PolicyTypeResource extends EntityTypeResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyTypeResource.class);


    /**
     * Constructor has to be public because of test cases
     */
    public PolicyTypeResource(PolicyTypeId id) {
        super(id);
    }

    /**
     * Convenience method to avoid casting at the caller's side.
     */
    public TPolicyType getPolicyType() {
        return (TPolicyType) this.getElement();
    }

    @Override
    protected TExtensibleElements createNewElement() {
        return new TPolicyType();
    }

    @Path("appliesto/")
    public AppliesToResource getAppliesTo() {
        return new AppliesToResource(this);
    }

    @Path("language/")
    public LanguageResource getLanguage() {
        return new LanguageResource(this);
    }

    @Override
    public SortedSet<Select2OptGroup> getListOfAllInstances() {
        try {
            return this.getListOfAllInstances(PolicyTemplateId.class);
        } catch (RepositoryCorruptException e) {
            throw new WebApplicationException(e);
        }
    }

    @Path("templates/")
    public TemplatesOfOnePolicyTypeResource getImplementations() {
        return new TemplatesOfOnePolicyTypeResource((PolicyTypeId) this.id);
    }

    @Path("appearance")
    public VisualAppearanceResource getVisualAppearanceResource() {
        return new VisualAppearanceResource(this, this.getElement().getOtherAttributes(), this.id);
    }
}
