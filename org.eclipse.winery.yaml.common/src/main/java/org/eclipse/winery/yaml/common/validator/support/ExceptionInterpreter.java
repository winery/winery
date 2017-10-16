/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.yaml.common.validator.support;

import org.eclipse.winery.yaml.common.exception.InvalidSyntax;
import org.eclipse.winery.yaml.common.exception.InvalidType;
import org.eclipse.winery.yaml.common.exception.YAMLParserException;

import org.yaml.snakeyaml.constructor.ConstructorException;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import org.yaml.snakeyaml.scanner.ScannerException;

public class ExceptionInterpreter {
    public YAMLParserException interpret(ConstructorException e) {
        String context = "";
        if (e.getCause() instanceof MarkedYAMLException) {
            context = ((MarkedYAMLException) e.getCause()).getContext();
        } else {
            return new InvalidSyntax(e.toString());
        }
        String invalidDescription = "Cannot create property=description.*";
        if (context.matches(invalidDescription)) {
            return new InvalidType("description", e);
        }
        String invalidType = "Cannot create property=metadata.*";
        if (context.matches(invalidType)) {
            return new InvalidType("metadata", e);
        }

        return new InvalidSyntax(e.toString());
    }

    public YAMLParserException interpret(ScannerException e) {
        String scalarScanning = "while scanning a plain scalar";
        if (e.getContext().matches(scalarScanning)) {
            String unexpected = "found unexpected ':'";
            if (e.getProblem().matches(unexpected)) {
                String msg = "Using \":\" in values in flow context is invalid \n" +
                    "Put all names (especially URIs) in parentheses. \n\n" +
                    "(Example INVALID: { get_operation_output: [ SELF, http://www.example.com/interface/lifecycle, " +
                    "operationName, outputName ] })\n" +
                    "(Example VALID:   { get_operation_output: [ SELF, \"http://www.example.com/interface/lifecycle\", " +
                    "operationName, outputName ] })\n\n" +
                    e.getMessage();
                return new InvalidSyntax(msg);
            }
        }

        return new InvalidSyntax(e.toString());
    }
}
