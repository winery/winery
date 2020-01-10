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

package org.eclipse.winery.model.tosca;

import java.io.Serializable;
import java.util.Objects;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class TRequirementRef implements Serializable {

    protected String name;
    protected TRequirement ref;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TRequirementRef)) return false;
        TRequirementRef that = (TRequirementRef) o;
        return Objects.equals(name, that.name) &&
            Objects.equals(ref, that.ref);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, ref);
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String value) {
        this.name = value;
    }

    @NonNull
    public TRequirement getRef() {
        return ref;
    }

    public void setRef(@NonNull TRequirement value) {
        Objects.requireNonNull(value);
        this.ref = value;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
