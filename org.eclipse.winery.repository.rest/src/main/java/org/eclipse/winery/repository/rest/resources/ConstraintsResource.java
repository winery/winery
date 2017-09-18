/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources;

import java.util.List;

import org.eclipse.winery.model.tosca.TConstraint;
import org.eclipse.winery.repository.rest.resources._support.collections.withoutid.EntityWithoutIdCollectionResource;
import org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes.NodeTypeResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConstraintsResource extends EntityWithoutIdCollectionResource<ConstraintResource, TConstraint> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConstraintsResource.class);


	public ConstraintsResource(List<TConstraint> constraints, NodeTypeResource res) {
		super(ConstraintResource.class, TConstraint.class, constraints, res);
	}
}
