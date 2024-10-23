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
//TODO add java doc for why this file is created
package org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes;

import java.util.Map;

public class ToscaMap<K, V> {
    private Map<K, V> value;

    public ToscaMap(Map<K, V> value) {
        this.value = value;
    }
    
    public Map<K, V> getValue() {
        return value;
    }
}
