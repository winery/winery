/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources.apiData;

import java.util.Objects;

import org.eclipse.winery.common.ids.Namespace;

public class NamespaceWithPrefix implements Comparable<NamespaceWithPrefix> {

    public String prefix = "";
    public String namespace = "";

    /**
     * Default constructor is required by jaxb to instantiate posted list.
     */
    public NamespaceWithPrefix() {
    }

    public NamespaceWithPrefix(Namespace ns, String prefix) {
        Objects.requireNonNull(ns);
        Objects.requireNonNull(prefix);
        this.namespace = ns.getDecoded();
        this.prefix = prefix;
    }

    @Override
    public int compareTo(NamespaceWithPrefix o) {
        return this.namespace.compareTo(o.namespace);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamespaceWithPrefix)) return false;
        NamespaceWithPrefix that = (NamespaceWithPrefix) o;
        return Objects.equals(prefix, that.prefix) &&
            Objects.equals(namespace, that.namespace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, namespace);
    }
}
