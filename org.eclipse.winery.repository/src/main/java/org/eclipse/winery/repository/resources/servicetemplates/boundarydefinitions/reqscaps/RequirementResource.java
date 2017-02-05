/*******************************************************************************
 * Copyright (c) 2014 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources.servicetemplates.boundarydefinitions.reqscaps;

import java.util.List;

import org.eclipse.winery.model.tosca.TRequirementRef;
import org.eclipse.winery.repository.resources._support.IPersistable;
import org.eclipse.winery.repository.resources._support.collections.withoutid.EntityWithoutIdResource;

public class RequirementResource extends EntityWithoutIdResource<TRequirementRef> {

	public RequirementResource(TRequirementRef o, int idx, List<TRequirementRef> list, IPersistable res) {
		super(o, idx, list, res);
	}

}
