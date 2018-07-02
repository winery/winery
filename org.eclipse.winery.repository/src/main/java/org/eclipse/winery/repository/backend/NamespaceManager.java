/*******************************************************************************
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
package org.eclipse.winery.repository.backend;

import java.util.Collection;
import java.util.Objects;

import org.eclipse.winery.common.ids.Namespace;

public interface NamespaceManager {

    default String getPrefix(Namespace namespace) {
        Objects.requireNonNull(namespace);

        String ns = namespace.getDecoded();
        return this.getPrefix(ns);
    }

    /**
     * Returns a prefix for the given namespace. With two different namespaces, to different prefixes are returned.
     * The returned prefixes are not persistest. Thus, two instances of a NamespaceManager might return different
     * prefixes when called in another order.
     */
    String getPrefix(String namespace);

    /**
     * Determines whether the storage has a namespace prefix stored permanently. This differs from just issuuing a
     * {@link #getPrefix(String)} request, which just determines something, but does not persist it between calls.
     */
    boolean hasPermanentPrefix(String namespace);

    void removePermanentPrefix(String namespace);

    /**
     * Permanently stores a prefix. No action, if namespace or prefix are null or empty.
     */
    void setPermanentPrefix(String namespace, String prefix);

    Collection<String> getAllPermanentPrefixes();

    Collection<String> getAllPermanentNamespaces();

    default void addPermanentNamespace(String namespace) {
        this.setPermanentPrefix(namespace, this.getPrefix(namespace));
    }

    /**
     * Removes all namespace mappings
     */
    void clear();
}
