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
package org.eclipse.winery.repository.rest.resources.servicetemplates.topologytemplates;

import java.util.List;

import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.repository.rest.resources._support.collections.IIdDetermination;
import org.eclipse.winery.repository.rest.resources.entitytemplates.TEntityTemplateResource;

public class RelationshipTemplateResource extends TEntityTemplateResource<TRelationshipTemplate> {

	public RelationshipTemplateResource(IIdDetermination<TRelationshipTemplate> idDetermination, TRelationshipTemplate o, int idx, List<TRelationshipTemplate> list, IPersistable res) {
		super(idDetermination, o, idx, list, res);
	}
}
