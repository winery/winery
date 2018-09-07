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

package org.eclipse.winery.model.substitution;

import java.util.List;

import org.eclipse.winery.model.tosca.HasInheritance;
import org.eclipse.winery.model.tosca.HasType;
import org.eclipse.winery.model.tosca.TBoolean;

public class FindFirstSubstitutionStrategy<R extends HasType, T extends HasInheritance> extends SubstitutionStrategy<R, T> {

    @Override
    protected T selectElement(List<T> subtypes) {
        return subtypes.stream()
            .filter(o -> TBoolean.NO.equals(o.getAbstract()))
            .findFirst()
            .orElse(null);
    }
}
