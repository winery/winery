/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.model.tosca.yaml.support;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.yaml.TPropertyFilterDefinition;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tMapPropertyFilterDefinition", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.0", propOrder = {
    "map"
})
public class TMapPropertyFilterDefinition implements Map<String, TPropertyFilterDefinition> {
    private Map<String, TPropertyFilterDefinition> map;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TMapPropertyFilterDefinition)) return false;
        TMapPropertyFilterDefinition that = (TMapPropertyFilterDefinition) o;
        return Objects.equals(getMap(), that.getMap());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMap());
    }

    public Map<String, TPropertyFilterDefinition> getMap() {
        return map;
    }

    public void setMap(Map<String, TPropertyFilterDefinition> map) {
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
    public TPropertyFilterDefinition get(Object o) {
        return this.map.get(o);
    }

    @Override
    public TPropertyFilterDefinition put(String s, TPropertyFilterDefinition propertyFilterDefinition) {
        return this.map.put(s, propertyFilterDefinition);
    }

    @Override
    public TPropertyFilterDefinition remove(Object o) {
        return this.map.remove(o);
    }

    @Override
    public void putAll(Map<? extends String, ? extends TPropertyFilterDefinition> map) {
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
    public Collection<TPropertyFilterDefinition> values() {
        return this.map.values();
    }

    @Override
    public Set<Entry<String, TPropertyFilterDefinition>> entrySet() {
        return this.map.entrySet();
    }
}
