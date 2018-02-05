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
package org.eclipse.winery.repository.backend;

import org.eclipse.winery.common.ids.Namespace;

import java.util.Collection;

public interface NamespaceManager {

    /**
     * SIDEFFECT: URI is added to list of known namespaces if it did not exist
     * before
     */
    default String getPrefix(Namespace namespace) {
        String ns = namespace.getDecoded();
        return this.getPrefix(ns);
    }

    /**
     * SIDEFFECT: URI is added to list of known namespaces if it did not exist
     * before
     */
    String getPrefix(String namespace);

    boolean hasPrefix(String namespace);

    void remove(String namespace);

    void setPrefix(String namespace, String prefix);

    Collection<String> getAllPrefixes();

    Collection<String> getAllNamespaces();

    default void addNamespace(String namespace) {
        this.getPrefix(namespace);
    }

    /**
     * Removes all namespace mappings
     */
    void clear();

}
