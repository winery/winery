/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources.entitytypeimplementations;

import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.repository.resources.AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal;
import org.eclipse.winery.repository.resources.IHasTypeReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class EntityTypeImplementationResource extends AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal implements IHasTypeReference {

	private static final Logger LOGGER = LoggerFactory.getLogger(EntityTypeImplementationResource.class);


	protected EntityTypeImplementationResource(TOSCAComponentId id) {
		super(id);
	}

}
