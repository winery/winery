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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.yaml.TPolicyDefinition;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tMapPolicyDefinition", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.3", propOrder = {
    "map"
})
public class TMapPolicyDefinition implements Map<String, TPolicyDefinition> {
    private Map<String, TPolicyDefinition> map;

    public TMapPolicyDefinition() {
        this.map = new LinkedHashMap<>();
    }

    public TMapPolicyDefinition(Map<String, TPolicyDefinition> map) {
        this.map = map;
    }

    public Map<String, TPolicyDefinition> getMap() {
        return map;
    }

    public void setMap(Map<String, TPolicyDefinition> map) {
        this.map = map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TMapPolicyDefinition that = (TMapPolicyDefinition) o;
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
    public TPolicyDefinition get(Object key) {
        return map.get(key);
    }

    @Override
    public TPolicyDefinition put(String key, TPolicyDefinition value) {
        return map.put(key, value);
    }

    @Override
    public TPolicyDefinition remove(Object key) {
        return remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends TPolicyDefinition> m) {
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
    public Collection<TPolicyDefinition> values() {
        return map.values();
    }

    @Override
    public Set<Entry<String, TPolicyDefinition>> entrySet() {
        return map.entrySet();
    }
}
