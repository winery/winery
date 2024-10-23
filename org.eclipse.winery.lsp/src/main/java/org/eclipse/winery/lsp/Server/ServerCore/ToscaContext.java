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
package org.eclipse.winery.lsp.Server.ServerCore;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class ToscaContext {
    Set<String> contextSet;
    private final Stack<String> contextStack = new Stack<>();

    public ToscaContext() {
        contextSet = new HashSet<>();
        contextSet.add("artifact_types:");
        contextSet.add("capability_types:");
        contextSet.add("node_types:");
        contextSet.add("service_template:");
        contextSet.add("node_templates:");
        contextSet.add("capabilities:");
        contextSet.add("relationship_types:"); // TODO add more keynames
    }
    
    public Stack<String> getContextStack() {
        return contextStack;
    }
    
    public void buildContextStack(String content, int lineLimit) {
        String[] lines = content.split("\n");
        for (int i = 0; i < lineLimit; i++) {
            String currentLine = lines[i].trim();
            if (currentLine.endsWith(":") && contextSet.contains(currentLine)) {
                String key = currentLine.substring(0, currentLine.length() - 1).trim();
                contextStack.push(key);
            }
        }
    }
}
