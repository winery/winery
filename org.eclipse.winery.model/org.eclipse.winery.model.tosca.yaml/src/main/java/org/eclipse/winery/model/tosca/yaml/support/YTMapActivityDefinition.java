/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

import org.eclipse.winery.model.tosca.yaml.YTActivityDefinition;

public class YTMapActivityDefinition implements Map<String, YTActivityDefinition> {
    private Map<String, YTActivityDefinition> map;

    public YTMapActivityDefinition() {
        this.map = new LinkedHashMap<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YTMapActivityDefinition)) return false;
        YTMapActivityDefinition that = (YTMapActivityDefinition) o;
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

    public Map<String, YTActivityDefinition> getMap() {
        return map;
    }

    public YTMapActivityDefinition setMap(Map<String, YTActivityDefinition> map) {
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
    public YTActivityDefinition get(Object o) {
        return this.map.get(o);
    }

    @Override
    public YTActivityDefinition put(String s, YTActivityDefinition activityDefinition) {
        return this.map.put(s, activityDefinition);
    }

    @Override
    public YTActivityDefinition remove(Object o) {
        return this.map.remove(o);
    }

    @Override
    public void putAll(Map<? extends String, ? extends YTActivityDefinition> map) {
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
    public Collection<YTActivityDefinition> values() {
        return this.map.values();
    }

    @Override
    public Set<Entry<String, YTActivityDefinition>> entrySet() {
        return this.map.entrySet();
    }
}
