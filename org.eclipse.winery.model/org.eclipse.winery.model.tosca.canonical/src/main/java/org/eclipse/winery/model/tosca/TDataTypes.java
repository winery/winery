/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

/**
 * Data type for complex tDataTypes representation of TOSCA YAML datatypes stored as DefinitionsChild
 */
public class TDataTypes implements Serializable {
    
    protected List<TDataType> dataTypes;
    
    public TDataTypes() {
        this(Collections.emptyList());
    }
    
    public TDataTypes(List<TDataType> dataTypes) {
        this.dataTypes = dataTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TDataTypes that = (TDataTypes) o;
        return Objects.equals(dataTypes, that.dataTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataTypes);
    }

    @NonNull
    public List<TDataType> getDataType() {
        return dataTypes != null ? dataTypes : Collections.emptyList();
    }
}
