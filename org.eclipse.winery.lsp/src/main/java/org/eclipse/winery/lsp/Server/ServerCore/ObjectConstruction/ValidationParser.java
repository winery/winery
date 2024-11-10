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

package org.eclipse.winery.lsp.Server.ServerCore.ObjectConstruction;

import org.eclipse.winery.lsp.Server.ServerCore.TOSCAFunctions.FunctionParser;
import org.eclipse.winery.lsp.Server.ServerCore.Utils.CommonUtils;
import org.tinylog.Logger;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

public class ValidationParser {
    public static Optional<Stack<Map<String, List<String>>>> parseValidation(String validation) {
        FunctionParser functionParser = new FunctionParser();
        try {
            if (CommonUtils.isFunction(validation)) {
                functionParser.parseFunctionCall(validation);
            }
        } catch (Exception e) {
            Logger.error("The error message: " + e.getMessage(), e);
        }
        if (functionParser.getFunctionStack() != null && !functionParser.getFunctionStack().isEmpty()) {
            return Optional.ofNullable(functionParser.getFunctionStack());    
        }
        return  Optional.empty();
    }
}
