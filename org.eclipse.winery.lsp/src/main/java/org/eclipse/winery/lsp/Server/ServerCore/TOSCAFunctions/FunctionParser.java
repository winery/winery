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

import org.eclipse.winery.lsp.Server.ServerCore.Utils.CommonUtils;
import org.yaml.snakeyaml.Yaml;

import java.util.*;
import java.util.stream.Collectors;

public class FunctionParser {
    private static final Yaml yaml = new Yaml();
    private final Stack<Map<String, List<String>>> functionStack = new Stack<>();

    public Stack<Map<String, List<String>>> getFunctionStack() {
        return functionStack;
    }

    public void parseFunctionCall(String line) {
        line = line.trim();
        if (line.startsWith("{") && line.endsWith("}")) {
            line = line.substring(1, line.length() - 1).trim();    
        }
        int separatorIndex = Math.max(line.indexOf(':'), line.indexOf('='));
        List<String> arguments;
        String key;
        if (separatorIndex != -1) {
            key = line.substring(0, separatorIndex).trim();
            String value = line.substring(separatorIndex + 1).trim();

            // Check if it's the only key in a YAML map using SnakeYAML
            Map<String, Object> map = yaml.load(key + ": " + value);
            if (map.size() == 1) {
                arguments = parseArguments(value);
                functionStack.push(Map.of(key, arguments));
                for (String argument : arguments) {
                    if (CommonUtils.isFunctionCall( argument)) {
                        parseFunctionCall(argument);
                    }
                }
            } else {
                throw new IllegalArgumentException("Malformed function: more than one key in the map");
            }
        } else {
            key = line;
            functionStack.push(Map.of(key, Collections.emptyList()));
        }
    }

    private List<String> parseArguments(String value) {
        value = value.trim();
        if (value.startsWith("[") && value.endsWith("]")) {
            value = value.substring(1, value.length() - 1).trim();
            if (value.isEmpty()) {
                return Collections.emptyList();
            }
            // Split the string, trim each element, and return the list
            return Arrays.stream(value.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("Malformed arguments: not a valid list");
        }
    }
}
