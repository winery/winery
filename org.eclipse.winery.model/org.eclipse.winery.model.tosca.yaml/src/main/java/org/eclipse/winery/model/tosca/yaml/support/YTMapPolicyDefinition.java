/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

import org.eclipse.winery.model.tosca.yaml.YTPolicyDefinition;

public class YTMapPolicyDefinition implements Map<String, YTPolicyDefinition> {
    private Map<String, YTPolicyDefinition> map;

    public YTMapPolicyDefinition() {
        this.map = new LinkedHashMap<>();
    }

    public YTMapPolicyDefinition(Map<String, YTPolicyDefinition> map) {
        this.map = map;
    }

    public Map<String, YTPolicyDefinition> getMap() {
        return map;
    }

    public void setMap(Map<String, YTPolicyDefinition> map) {
        this.map = map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YTMapPolicyDefinition that = (YTMapPolicyDefinition) o;
        return map.equals(that.map);
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
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public YTPolicyDefinition get(Object key) {
        return map.get(key);
    }

    @Override
    public YTPolicyDefinition put(String key, YTPolicyDefinition value) {
        return map.put(key, value);
    }

    @Override
    public YTPolicyDefinition remove(Object key) {
        return remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends YTPolicyDefinition> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<YTPolicyDefinition> values() {
        return map.values();
    }

    @Override
    public Set<Entry<String, YTPolicyDefinition>> entrySet() {
        return map.entrySet();
    }
}
