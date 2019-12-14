/*******************************************************************************
 * Copyright (c) 2017-2019 Contributors to the Eclipse Foundation
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
import java.util.Map;
import java.util.Objects;

import org.eclipse.winery.model.ids.Namespace;
import org.eclipse.winery.repository.backend.filebased.NamespaceProperties;

import org.eclipse.jdt.annotation.NonNull;

public interface NamespaceManager {

    default String getPrefix(Namespace namespace) {
        Objects.requireNonNull(namespace);

        String ns = namespace.getDecoded();
        return this.getPrefix(ns);
    }

    /**
     * Returns a prefix for the given namespace. With two different namespaces, to different prefixes are returned. The
     * returned prefixes are not persistest. Thus, two instances of a NamespaceManager might return different prefixes
     * when called in another order.
     */
    String getPrefix(String namespace);

    /**
     * Determines whether the storage has a namespace prefix stored permanently. This differs from just issuuing a
     * {@link #getPrefix(String)} request, which just determines something, but does not persist it between calls.
     */
    boolean hasPermanentProperties(String namespace);

    void removeNamespaceProperties(String namespace);

    /**
     * Permanently stores a prefix. No action, if namespace or prefix are null or empty.
     */
    void setNamespaceProperties(String namespace, NamespaceProperties properties);

    Map<String, NamespaceProperties> getAllNamespaces();

    @NonNull
    public NamespaceProperties getNamespaceProperties(String namespace);

    /**
     * Add new properties for a namespace if it does not exist yet. Otherwise no action will be performed.
     *
     * @param namespace the namespace to be added
     */
    default void addPermanentNamespace(String namespace) {
        if (!hasPermanentProperties(namespace)) {
            this.setNamespaceProperties(namespace, this.getNamespaceProperties(namespace));
        }
    }

    void addAllPermanent(Collection<NamespaceProperties> properties);

    void replaceAll(Map<String, NamespaceProperties> map);

    /**
     * Removes all namespace mappings
     */
    void clear();

    boolean isPatternNamespace(String namespace);

    boolean isSecureCollection(String namespace);

    boolean isGeneratedNamespace(String namespace);
}
