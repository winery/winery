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

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;

import java.util.*;

public class Metadata implements Map<String, String> {
    private Map<String, String> value;

    public Metadata() {
        this.value = new LinkedHashMap<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Metadata)) return false;
        Metadata metadata = (Metadata) o;
        return Objects.equals(value, metadata.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Metadata{" + value.toString() +
            '}';
    }

    public Metadata add(Metadata metadata) {
        this.putAll(metadata);
        return this;
    }

    public Metadata add(String key, String value) {
        this.put(key, value);
        return this;
    }

    @Override
    public int size() {
        return value.size();
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return value.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return value.containsValue(o);
    }

    @Override
    public String get(Object o) {
        return value.get(o);
    }

    @Override
    public String put(String s, String s2) {
        return value.put(s, s2);
    }

    @Override
    public String remove(Object o) {
        return value.remove(o);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> map) {
        value.putAll(map);
    }

    @Override
    public void clear() {
        value.clear();
    }

    @Override
    public Set<String> keySet() {
        return value.keySet();
    }

    @Override
    public Collection<String> values() {
        return value.values();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return value.entrySet();
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }
}
