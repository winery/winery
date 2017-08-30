/*******************************************************************************
 * Copyright (c) 2016 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Kálmán Képes - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.tags;

import java.util.List;

import org.eclipse.winery.model.tosca.TTag;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.repository.rest.resources._support.collections.withoutid.EntityWithoutIdResource;

public class TagResource extends EntityWithoutIdResource<TTag> {

	public TagResource(TTag o, int idx, List<TTag> list, IPersistable res) {
		super(o, idx, list, res);
	}

	public String getId() {
		return this.idDetermination.getId(o);
	}

	public String getName() {
		return this.o.getName();
	}

	public String getValue() {
		return this.o.getValue();
	}

}
