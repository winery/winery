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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class YTListString implements List<String> {
    private List<String> list;

    public YTListString() {
        this.list = new ArrayList<>();
    }

    public YTListString(String str) {
        this.list = new ArrayList<>();
        this.list.add(str);
    }

    public YTListString(String... str) {
        this.list = new ArrayList<>(Arrays.asList(str));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YTListString)) return false;
        YTListString strings = (YTListString) o;
        return Objects.equals(getList(), strings.getList());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getList());
    }

    @Override
    public String toString() {
        return list.toString();
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<String> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return list.toArray(ts);
    }

    @Override
    public boolean add(String s) {
        return list.add(s);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return list.containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends String> collection) {
        return list.addAll(collection);
    }

    @Override
    public boolean addAll(int i, Collection<? extends String> collection) {
        return list.addAll(collection);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return list.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return list.retainAll(collection);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public String get(int i) {
        return list.get(i);
    }

    @Override
    public String set(int i, String s) {
        return list.set(i, s);
    }

    @Override
    public void add(int i, String s) {
        list.add(i, s);
    }

    @Override
    public String remove(int i) {
        return list.remove(i);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<String> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<String> listIterator(int i) {
        return list.listIterator(i);
    }

    @Override
    public List<String> subList(int i, int i1) {
        return list.subList(i, i1);
    }
}
