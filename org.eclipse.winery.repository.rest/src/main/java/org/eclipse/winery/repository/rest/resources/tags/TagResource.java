/********************************************************************************
 * Copyright (c) 2016 Contributors to the Eclipse Foundation
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
