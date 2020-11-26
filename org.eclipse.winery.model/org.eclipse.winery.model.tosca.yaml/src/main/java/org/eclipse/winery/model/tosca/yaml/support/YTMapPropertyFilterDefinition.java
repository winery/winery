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
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.winery.model.tosca.yaml.YTPropertyFilterDefinition;

public class YTMapPropertyFilterDefinition implements Map<String, YTPropertyFilterDefinition> {
    private Map<String, YTPropertyFilterDefinition> map;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YTMapPropertyFilterDefinition)) return false;
        YTMapPropertyFilterDefinition that = (YTMapPropertyFilterDefinition) o;
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

    public Map<String, YTPropertyFilterDefinition> getMap() {
        return map;
    }

    public void setMap(Map<String, YTPropertyFilterDefinition> map) {
        this.map = map;
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
    public YTPropertyFilterDefinition get(Object o) {
        return this.map.get(o);
    }

    @Override
    public YTPropertyFilterDefinition put(String s, YTPropertyFilterDefinition propertyFilterDefinition) {
        return this.map.put(s, propertyFilterDefinition);
    }

    @Override
    public YTPropertyFilterDefinition remove(Object o) {
        return this.map.remove(o);
    }

    @Override
    public void putAll(Map<? extends String, ? extends YTPropertyFilterDefinition> map) {
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
    public Collection<YTPropertyFilterDefinition> values() {
        return this.map.values();
    }

    @Override
    public Set<Entry<String, YTPropertyFilterDefinition>> entrySet() {
        return this.map.entrySet();
    }
}
