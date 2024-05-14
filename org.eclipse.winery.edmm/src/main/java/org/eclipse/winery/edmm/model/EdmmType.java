/*******************************************************************************
 * Copyright (c) 2019-2021 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.edmm.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;
import io.github.edmm.model.support.ModelEntity;
import io.github.edmm.model.support.TypeResolver;

public class EdmmType implements Serializable {
    private static final long serialVersionUID = -8206466497675515123L;
    private final String value;

    public EdmmType(String value) {
        this.value = value;
    }

    public static EdmmType fromEntityClass(Class<? extends ModelEntity> entityClass) {
        return new EdmmType(TypeResolver.resolve(entityClass));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdmmType edmmType = (EdmmType) o;
        return Objects.equals(value, edmmType.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
