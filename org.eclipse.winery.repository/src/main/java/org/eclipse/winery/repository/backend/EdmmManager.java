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

package org.eclipse.winery.repository.backend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.edmm.EdmmMappingItem;
import org.eclipse.winery.common.edmm.EdmmType;

public interface EdmmManager {

    List<EdmmMappingItem> getOneToOneMappings();

    void setOneToOneMappings(List<EdmmMappingItem> list);

    List<EdmmMappingItem> getTypeMappings();

    void setTypeMappings(List<EdmmMappingItem> list);

    default Map<QName, EdmmType> getTypeMap() {
        Map<QName, EdmmType> typeMappings = new HashMap<>();
        this.getTypeMappings().forEach(m -> typeMappings.put(m.toscaType, m.edmmType));
        return typeMappings;
    }

    default Map<QName, EdmmType> getOneToOneMap() {
        Map<QName, EdmmType> typeMappings = new HashMap<>();
        this.getOneToOneMappings().forEach(m -> typeMappings.put(m.toscaType, m.edmmType));
        return typeMappings;
    }
}
