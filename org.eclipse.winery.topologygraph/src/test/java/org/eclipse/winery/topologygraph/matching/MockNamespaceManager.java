/*******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.topologygraph.matching;

import java.util.Collection;
import java.util.Map;

import org.eclipse.winery.repository.backend.NamespaceManager;
import org.eclipse.winery.repository.backend.filebased.NamespaceProperties;

import org.eclipse.jdt.annotation.NonNull;

public class MockNamespaceManager implements NamespaceManager {
    @Override
    public String getPrefix(String namespace) {
        return "";
    }

    @Override
    public boolean hasPermanentProperties(String namespace) {
        return false;
    }

    @Override
    public void removeNamespaceProperties(String namespace) {
    }

    @Override
    public void setNamespaceProperties(String namespace, NamespaceProperties properties) {
    }

    @Override
    public Map<String, NamespaceProperties> getAllNamespaces() {
        return null;
    }

    @Override
    public @NonNull NamespaceProperties getNamespaceProperties(String namespace) {
        return null;
    }

    @Override
    public void addAllPermanent(Collection<NamespaceProperties> properties) {
    }

    @Override
    public void replaceAll(Map<String, NamespaceProperties> map) {
    }

    @Override
    public void clear() {
    }

    @Override
    public boolean isPatternNamespace(String namespace) {
        return false;
    }
}
