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
package org.eclipse.winery.repository.rest.resources.entitytypeimplementations;

import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class EntityTypeImplementationResource extends AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityTypeImplementationResource.class);


    protected EntityTypeImplementationResource(DefinitionsChildId id) {
        super(id);
    }
}
