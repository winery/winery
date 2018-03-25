/*******************************************************************************
 * Copyright (c) 2012-2014 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.entitytemplates;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentInstanceResource;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.repository.rest.resources._support.collections.IIdDetermination;
import org.eclipse.winery.repository.rest.resources._support.collections.withid.EntityWithIdResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.boundarydefinitions.PropertyConstraintsResource;

public class TEntityTemplateResource<E extends TEntityTemplate> extends EntityWithIdResource<E> implements IEntityTemplateResource<E> {

    /**
     * This constructor is used for both entity templates nested in an component
     * instance as well as for entity templates being component instances
     * itself.
     * <p>
     * As Java does not support multi-inheritance, we implemented a quick hack
     * to re-use this class as inner implementation at templates extending
     * AbstractComponentInstanceResourceDefinitionsBacked
     */
    public TEntityTemplateResource(IIdDetermination<E> idDetermination, E o, int idx, List<E> list, IPersistable res) {
        super(idDetermination, o, idx, list, res);
    }

    @Override
    public PropertiesResource getPropertiesResource() {
        return new PropertiesResource(this.o, (AbstractComponentInstanceResource) this.res);
    }

    @GET
    @Path("/propertyconstraints")
    public PropertyConstraintsResource getPropertyConstraints() {
        TEntityTemplate.PropertyConstraints constraints = this.o.getPropertyConstraints();
        if (constraints == null) {
            constraints = new TEntityTemplate.PropertyConstraints();
        }
        return new PropertyConstraintsResource(constraints, (AbstractComponentInstanceResource) this.res);
    }
}
