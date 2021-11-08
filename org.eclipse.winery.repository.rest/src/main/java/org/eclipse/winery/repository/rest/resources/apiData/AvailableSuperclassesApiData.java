/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources.apiData;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal;

public class AvailableSuperclassesApiData {

    public List<NameAndQNameApiData> classes;

    public AvailableSuperclassesApiData(AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal res) {
        this.generateList(res.getId().getClass(), res.getId());
    }

    public AvailableSuperclassesApiData(Class<? extends DefinitionsChildId> clazz) {
        this.generateList(clazz, null);
    }

    private void generateList(Class<? extends DefinitionsChildId> clazz, DefinitionsChildId classToExclude) {
        SortedSet<? extends DefinitionsChildId> allDefinitionsChildIds = RepositoryFactory.getRepository().getAllDefinitionsChildIds(clazz);
        if (classToExclude != null) {
            allDefinitionsChildIds.remove(classToExclude);
        }
        this.classes = new ArrayList<>();
        for (DefinitionsChildId id : allDefinitionsChildIds) {
            NameAndQNameApiData q = new NameAndQNameApiData(id);
            this.classes.add(q);
        }
    }
}
