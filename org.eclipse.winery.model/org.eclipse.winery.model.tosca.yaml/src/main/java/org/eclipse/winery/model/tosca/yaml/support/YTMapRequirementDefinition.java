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

import org.eclipse.winery.model.tosca.yaml.YTRequirementDefinition;

public class YTMapRequirementDefinition implements Map<String, YTRequirementDefinition> {
    private Map<String, YTRequirementDefinition> map;

    public YTMapRequirementDefinition() {
        this.map = new LinkedHashMap<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YTMapRequirementDefinition)) return false;
        YTMapRequirementDefinition that = (YTMapRequirementDefinition) o;
        return Objects.equals(getMap(), that.getMap());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMap());
    }

    @Override
    public String toString() {
        return map.toString();
    }

    public Map<String, YTRequirementDefinition> getMap() {
        return map;
    }

    public YTMapRequirementDefinition setMap(Map<String, YTRequirementDefinition> map) {
        this.map = new LinkedHashMap<>(map);
        return this;
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
    public YTRequirementDefinition get(Object o) {
        return this.map.get(o);
    }

    @Override
    public YTRequirementDefinition put(String s, YTRequirementDefinition requirementDefinition) {
        return this.map.put(s, requirementDefinition);
    }

    @Override
    public YTRequirementDefinition remove(Object o) {
        return this.map.remove(o);
    }

    @Override
    public void putAll(Map<? extends String, ? extends YTRequirementDefinition> map) {
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
    public Collection<YTRequirementDefinition> values() {
        return this.map.values();
    }

    @Override
    public Set<Entry<String, YTRequirementDefinition>> entrySet() {
        return this.map.entrySet();
    }
}
