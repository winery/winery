/********************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.model.tosca.yaml.support;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.winery.model.tosca.yaml.YTRequirementAssignment;

public class YTMapRequirementAssignment implements Map<String, YTRequirementAssignment> {
    private Map<String, YTRequirementAssignment> map;

    public YTMapRequirementAssignment() {
        this.map = new LinkedHashMap<>();
    }

    public Map<String, YTRequirementAssignment> getMap() {
        return map;
    }

    public YTMapRequirementAssignment setMap(Map<String, YTRequirementAssignment> map) {
        this.map = new LinkedHashMap<>(map);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YTMapRequirementAssignment)) return false;
        YTMapRequirementAssignment that = (YTMapRequirementAssignment) o;
        return Objects.equals(map, that.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(map);
    }

    @Override
    public String toString() {
        return map.toString();
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return this.map.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return this.map.containsValue(o);
    }

    @Override
    public YTRequirementAssignment get(Object o) {
        return this.map.get(o);
    }

    @Override
    public YTRequirementAssignment put(String s, YTRequirementAssignment requirementAssignment) {
        return this.map.put(s, requirementAssignment);
    }

    @Override
    public YTRequirementAssignment remove(Object o) {
        return this.map.remove(o);
    }

    @Override
    public void putAll(Map<? extends String, ? extends YTRequirementAssignment> map) {
        this.map.putAll(map);
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.map.keySet();
    }

    @Override
    public Collection<YTRequirementAssignment> values() {
        return this.map.values();
    }

    @Override
    public Set<Entry<String, YTRequirementAssignment>> entrySet() {
        return this.map.entrySet();
    }
}
