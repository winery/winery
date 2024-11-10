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
package org.eclipse.winery.lsp.Server.ServerCore.Utils;

import org.eclipse.winery.lsp.Server.ServerAPI.API.context.LSContext;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCAFunctions.BooleanLogicFunctions;
import java.lang.reflect.Method;
import java.util.*;

public class ValidatingUtils {
    
    public static boolean isValidToscaType(String type) { //TODO return true if the type is a Tosca type 
        return true;
    }
        
    public static boolean isValueMatchType(Object value,String type) { //TODO return true if the Tosca type matches the value
            return true;
        }
        
    public static boolean validFunction(String function) {
        return function.startsWith("$") && function.charAt(1) != '$';
    }

    public static Object callBooleanFunction(String function, List<String> parameters, String type, LSContext context) {
        try {
            // Remove the leading $ from the function name
            String functionName = function.substring(1);

            // Get corresponding method to the function name
            Method[] methods = BooleanLogicFunctions.class.getMethods();
            Method targetMethod = null;
            for (Method method : methods) {
                if (method.getName().equals(functionName)) {
                    targetMethod = method;
                    break;
                }
            }

            if (targetMethod == null) {
                throw new IllegalArgumentException("Function not found: " + functionName);
            }

            // Convert string parameters to appropriate types
            Object[] convertedParameters = new Object[parameters.size()];

            for (int i = 0; i < parameters.size(); i++) {
                if (parameters.get(i) instanceof String && parameters.get(i).startsWith("[")) {
                    parameters.set(i,parameters.get(i).substring(1));
                } else if (parameters.get(i) instanceof String && parameters.get(i).endsWith("]")) {
                    parameters.set(i,parameters.get(i).substring(0, parameters.get(i).length() - 1));
                }
                switch (type) {
                    case "string" -> convertedParameters[i] = parameters.get(i);
                    case "integer" -> convertedParameters[i] = Integer.parseInt(parameters.get(i).trim());
                    case "float" -> convertedParameters[i] = Float.parseFloat(parameters.get(i).trim());
                    case "boolean" -> convertedParameters[i] = Boolean.parseBoolean(parameters.get(i).trim());
                    default -> throw new IllegalArgumentException("Unsupported type: " + type);
                }
            }
            
            if ("valid_values".equals(functionName)) {
                // The first parameter is the value, the second is a list
                Object value = convertedParameters[0];
                List<?> list = List.of(Arrays.copyOfRange(convertedParameters, 1, convertedParameters.length));
                return BooleanLogicFunctions.valid_values(value,  list);
            }
            
            // Invoke the target method with the converted parameters
            return targetMethod.invoke(null, convertedParameters);
            
        } catch (Exception e) {
            throw new RuntimeException("Error invoking function: " + e.getMessage());
        }
    }
    
    public static boolean isParametersContainsFunction(List<String> parameters) {
        for (String param : parameters) {
            if (param.startsWith("$") && param.charAt(1) != '$') {
                return true;
            }
        }
        return false;
    }

    public static List<String> replaceFunctionsByValue(Map<String, Object> functionValues, List<String> parameters) {
        List<String> replacedParameters = new ArrayList<>();
        for (String param : parameters) {
            if (param.startsWith("$")) {
                if (functionValues.containsKey(param)) {
                    replacedParameters.add(functionValues.get(param).toString());
                } else {
                    replacedParameters.add(param); // Keep the function if not found in the map
                }
            } else {
                replacedParameters.add(param);
            }
        }
        return replacedParameters;
    }
}
    
