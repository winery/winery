/********************************************************************************
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
 ********************************************************************************/
package org.eclipse.winery.topologygraph.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.winery.model.tosca.TEntityType;

public abstract class ToscaEntity {

    /**
     * List of all types of this TNodeTemplate. The first element must be the actual type.
     */
    private final List<TEntityType> types = new ArrayList<>();
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<TEntityType> getTypes() {
        return types;
    }

    public TEntityType getActualType() {
        return types.stream().findFirst().orElse(null);
    }

    public boolean addTEntityType(TEntityType type) {
        return this.types.add(type);
    }
}
