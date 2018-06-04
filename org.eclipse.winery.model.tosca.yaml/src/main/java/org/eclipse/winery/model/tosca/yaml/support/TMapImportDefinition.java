/********************************************************************************
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
package org.eclipse.winery.model.tosca.yaml.support;

import org.eclipse.winery.model.tosca.yaml.TImportDefinition;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tMapImportDefinition", namespace = "http://docs.oasis-open.org/tosca/ns/simple/yaml/1.0", propOrder = {
    "map"
})
public class TMapImportDefinition implements Map<String, TImportDefinition> {
    private Map<String, TImportDefinition> map;

    public TMapImportDefinition() {
        this.map = new LinkedHashMap<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TMapImportDefinition)) return false;
        TMapImportDefinition that = (TMapImportDefinition) o;
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

    public Map<String, TImportDefinition> getMap() {
        return map;
    }

    public void setMap(Map<String, TImportDefinition> map) {
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
    public TImportDefinition get(Object o) {
        return this.map.get(o);
    }

    @Override
    public TImportDefinition put(String s, TImportDefinition importDefinition) {
        return this.map.put(s, importDefinition);
    }

    @Override
    public TImportDefinition remove(Object o) {
        return this.map.remove(o);
    }

    @Override
    public void putAll(Map<? extends String, ? extends TImportDefinition> map) {
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
    public Collection<TImportDefinition> values() {
        return this.map.values();
    }

    @Override
    public Set<Entry<String, TImportDefinition>> entrySet() {
        return this.map.entrySet();
    }
}
