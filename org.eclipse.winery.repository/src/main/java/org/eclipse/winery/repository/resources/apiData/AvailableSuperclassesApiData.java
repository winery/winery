/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzentter - initial API and implementation
 *******************************************************************************/

package org.eclipse.winery.repository.resources.apiData;

import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.resources.AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

public class AvailableSuperclassesApiData {

	public List<QNameWithIdApiData> classes;

	public AvailableSuperclassesApiData(AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal res) {
		SortedSet<? extends TOSCAComponentId> allTOSCAcomponentIds = Repository.INSTANCE.getAllTOSCAComponentIds(res.getId().getClass());
		allTOSCAcomponentIds.remove(res.getId());
		this.classes = new ArrayList<>();
		for (TOSCAComponentId id : allTOSCAcomponentIds) {
			QNameWithIdApiData q = new QNameWithIdApiData(id);
			this.classes.add(q);
		}
	}
}
