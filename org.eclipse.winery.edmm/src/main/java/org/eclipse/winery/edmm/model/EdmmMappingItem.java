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

package org.eclipse.winery.edmm.model;

import java.util.Objects;

import javax.xml.namespace.QName;

public class EdmmMappingItem {

    public EdmmType edmmType;
    public QName toscaType;

    public EdmmMappingItem() {
    }

    public EdmmMappingItem(EdmmType edmmType, QName toscaType) {
        this.edmmType = edmmType;
        this.toscaType = toscaType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdmmMappingItem that = (EdmmMappingItem) o;
        return Objects.equals(edmmType, that.edmmType) && Objects.equals(toscaType, that.toscaType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(edmmType, toscaType);
    }
}
