/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.adaptation.substitution;

import java.util.List;
import java.util.Objects;

import org.eclipse.winery.model.tosca.HasInheritance;
import org.eclipse.winery.model.tosca.HasType;
import org.eclipse.winery.model.tosca.TEntityType;

public class NamespaceSubstitutionStrategy<R extends HasType, T extends HasInheritance> extends SubstitutionStrategy<R, T> {

    private String requiredNamespace;

    public NamespaceSubstitutionStrategy(String requiredNamespace) {
        this.requiredNamespace = Objects.requireNonNull(requiredNamespace);
    }

    @Override
    protected T selectElement(List<T> subtypes) {
        return subtypes.stream()
            .filter(o -> {
                if (!o.getAbstract() && o instanceof TEntityType) {
                    TEntityType subtype = (TEntityType) o;
                    return this.requiredNamespace.equals(subtype.getTargetNamespace());
                }
                return false;
            })
            .findFirst()
            .orElse(null);
    }
}
