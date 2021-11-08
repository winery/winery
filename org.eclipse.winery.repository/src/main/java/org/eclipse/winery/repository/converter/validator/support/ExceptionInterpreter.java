/********************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.converter.validator.support;

import org.eclipse.winery.model.converter.support.exception.InvalidYamlSyntax;

import org.yaml.snakeyaml.constructor.ConstructorException;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import org.yaml.snakeyaml.scanner.ScannerException;

public class ExceptionInterpreter {
    public Exception interpret(ConstructorException e) {
        String context = "";
        if (e.getCause() instanceof MarkedYAMLException) {
            context = ((MarkedYAMLException) e.getCause()).getContext();
        } else {
            return new InvalidYamlSyntax(e.toString());
        }
        String messagePattern = "The property= '{}' could not be interpreted by the SnakeYAML parser\n{}";
        String invalidDescription = "Cannot create property=description.*";
        if (context.matches(invalidDescription)) {
            return new InvalidYamlSyntax(messagePattern, "description", e);
        }
        String invalidType = "Cannot create property=metadata.*";
        if (context.matches(invalidType)) {
            return new InvalidYamlSyntax(messagePattern, "metadata", e);
        }

        return new InvalidYamlSyntax(e.toString());
    }

    public Exception interpret(ScannerException e) {
        String scalarScanning = "while scanning a plain scalar";
        if (e.getContext() != null && e.getContext().matches(scalarScanning)) {
            String unexpected = "found unexpected ':'";
            if (e.getProblem().matches(unexpected)) {
                String msg = "Using \":\" in values in flow context is invalid \n" +
                    "Put all names (especially URIs) in parentheses. \n\n" +
                    "(Example INVALID: { get_operation_output: [ SELF, http://www.example.com/interface/lifecycle, " +
                    "operationName, outputName ] })\n" +
                    "(Example VALID:   { get_operation_output: [ SELF, \"http://www.example.com/interface/lifecycle\", " +
                    "operationName, outputName ] })\n\n" +
                    e.getMessage();
                return new InvalidYamlSyntax(msg);
            }
        }

        return new InvalidYamlSyntax(e.toString());
    }
}
