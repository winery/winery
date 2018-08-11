/*******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.substitution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.winery.model.tosca.HasInheritance;
import org.eclipse.winery.model.tosca.HasType;

public abstract class SubstitutionStrategy<R extends HasType, T extends HasInheritance> {

    public Map<R, T> getReplacementMap(Map<R, List<Subtypes<T>>> inheritanceMap) {
        HashMap<R, T> map = new HashMap<>();

        inheritanceMap.forEach((template, subtypesHierarchy) -> {
            Subtypes<T> dummy = new Subtypes<>(null);
            dummy.addChildren(subtypesHierarchy);
            List<T> subtypes = dummy.asList();
            
            if (subtypes.size() == 1) {
                map.put(template, subtypes.get(0));
            } else if (subtypes.size() > 1) {
                map.put(template, selectElement(subtypes));
            }
        });

        return map;
    }

    protected abstract T selectElement(List<T> subtypes);
}
