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
package org.eclipse.winery.repository.backend.xsd;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.winery.model.ids.Namespace;

import java.util.List;

public class NamespaceAndDefinedLocalNames {

    private final Namespace namespace;

    private final MutableList<String> definedLocalNames;

    public NamespaceAndDefinedLocalNames(Namespace namespace) {
        this.namespace = namespace;
        this.definedLocalNames = Lists.mutable.empty();
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public List<String> getDefinedLocalNames() {
        return this.definedLocalNames.asUnmodifiable();
    }

    public void addLocalName(String localName) {
        this.definedLocalNames.add(localName);
    }
}
