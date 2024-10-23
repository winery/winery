/*******************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.lsp.Server.ServerCore.TOSCAFunctions;

import org.eclipse.winery.lsp.Server.ServerCore.DataModels.PropertyDefinition;

import java.util.List;

public class GraphQueryFunctions {
    
    public static Object value(PropertyDefinition propertyDefinition,int index) {
            if (propertyDefinition.value().isPresent() && propertyDefinition.value().get() instanceof List<?>) {
                return ((List<?>) propertyDefinition.value().get()).get(index);
            }
            return null;
    }
        
}
