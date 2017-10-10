/**
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
package org.eclipse.winery.repository.rest.resources.entitytypeimplementations;

import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.repository.rest.resources.AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class EntityTypeImplementationResource extends AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal {

	private static final Logger LOGGER = LoggerFactory.getLogger(EntityTypeImplementationResource.class);


	protected EntityTypeImplementationResource(DefinitionsChildId id) {
		super(id);
	}
}
