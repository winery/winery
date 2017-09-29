/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Christoph Kleine - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.model.tosca.yaml.support;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;

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
