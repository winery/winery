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

import org.eclipse.winery.model.tosca.yaml.TRequirementAssignment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tMapRequirementAssignment", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.0", propOrder = {
    "map"
})
public class TMapRequirementAssignment implements Map<String, TRequirementAssignment> {
    private Map<String, TRequirementAssignment> map;

    public TMapRequirementAssignment() {
        this.map = new LinkedHashMap<>();
    }

    public Map<String, TRequirementAssignment> getMap() {
        return map;
    }

    public TMapRequirementAssignment setMap(Map<String, TRequirementAssignment> map) {
        this.map = new LinkedHashMap<>(map);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TMapRequirementAssignment)) return false;
        TMapRequirementAssignment that = (TMapRequirementAssignment) o;
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
    public TRequirementAssignment get(Object o) {
        return this.map.get(o);
    }

    @Override
    public TRequirementAssignment put(String s, TRequirementAssignment requirementAssignment) {
        return this.map.put(s, requirementAssignment);
    }

    @Override
    public TRequirementAssignment remove(Object o) {
        return this.map.remove(o);
    }

    @Override
    public void putAll(Map<? extends String, ? extends TRequirementAssignment> map) {
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
    public Collection<TRequirementAssignment> values() {
        return this.map.values();
    }

    @Override
    public Set<Entry<String, TRequirementAssignment>> entrySet() {
        return this.map.entrySet();
    }
}
