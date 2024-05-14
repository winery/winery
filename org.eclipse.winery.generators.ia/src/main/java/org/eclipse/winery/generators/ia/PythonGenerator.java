/*******************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.generators.ia;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;

public class PythonGenerator extends Generator {
    // According to https://peps.python.org/pep-0008/#indentation
    private static final String indentation = "    ";

    public PythonGenerator(TInterface tInterface, String operationName, Path workingDir) {
        super(tInterface, operationName, workingDir);
    }

    @Override
    public void generateImplementationArtifact() throws IOException {
        if (operationName.equals("interface")) {
            throw new IllegalArgumentException("The whole interface cannot be generated for bash as artifact type!");
        } else {
            StringBuilder sb = new StringBuilder();
            TOperation operationToImplement = tInterface.getOperations().stream().parallel().filter(operation1 -> operation1.getName().equals(operationName)).findAny().orElseThrow(IllegalArgumentException::new);
            String content = generatePythonFileContent(sb, tInterface.getName(), operationToImplement);
            Files.write(Paths.get(this.workingDirectory.toString(), operationName + ".py"), content.getBytes());
        }
    }

    private String generatePythonFileContent(StringBuilder sb, String interfaceName, TOperation operation) {
        sb.append("#!/usr/bin/env python\n");

        // Import statements:
        if (operation.getInputParameters() != null) {
            sb.append("from argparse import ArgumentParser\n");
        }
        sb.append("\n");
        sb.append("# Implements the ").append(operation.getName()).append(" operation of the ").append(interfaceName).append(" interface\n");
        // Add example call:
        sb.append("# Example call: ");
        sb.append("python ").append(operationName).append(".py ");
        if (operation.getInputParameters() != null) {
            operation.getInputParameters().forEach(tParameter -> sb.append("-").append(tParameter.getName()).append(" test").append(tParameter.getName()).append(" "));
        }
        sb.append("\n\n");

        // define operation as method
        String params = "";
        if (operation.getInputParameters() != null) {
            params = operation.getInputParameters().stream().map(tParameter -> tParameter.getName() + "=None").collect(Collectors.joining(", "));
        }
        sb.append("def ").append(operation.getName()).append("(");
        sb.append(params);
        sb.append("):\n");
        // Print inputs and provide types as comments
        if (operation.getInputParameters() != null) {
            sb.append(indentation).append("# Print input parameters:\n");
            operation.getInputParameters().forEach(tParameter -> sb.append(indentation).append("print(").append(tParameter.getName()).append(")").append(" # Type: ").append(tParameter.getType().replace("xsd:", "")).append("\n"));
            sb.append("\n");
        }
        sb.append(indentation).append("# Your code here\n");
        sb.append("\n");

        // define main method
        sb.append("def main():\n");

        if (operation.getInputParameters() != null) {
            // handle args parsing
            sb.append(indentation).append("parser = ArgumentParser()\n");
            operation.getInputParameters().forEach(tParameter -> sb.append(indentation)
                .append("parser.add_argument('-")
                .append(tParameter.getName())
                .append("', '--")
                .append(tParameter.getName())
                .append("', type=")
                // Todo: add lookup for the argument type from winery to argparse type (currently default: string)
                .append("str")
                .append(", dest='")
                .append(tParameter.getName())
                .append("')\n")
            );
            sb.append(indentation).append("args = parser.parse_args()\n\n");

            // TODO: Should we check for non-none args if they are required or set the required flag in argparse???
        }

        // call operation method
        // Add output parameters
        sb.append(indentation);
        if (operation.getOutputParameters() != null) {
            sb.append(operation.getOutputParameters().stream().map(TParameter::getName).collect(Collectors.joining(", ")));
            sb.append(" = ");
        }
        sb.append(operation.getName()).append("(");
        if (operation.getInputParameters() != null) {
            sb.append(operation.getInputParameters().stream().map(tParameter -> tParameter.getName() + "=args." + tParameter.getName()).collect(Collectors.joining(", ")));
        }
        sb.append(")\n");

        // Print output
        if (operation.getOutputParameters() != null) {
            sb.append(indentation).append("# Return\n");
            operation.getOutputParameters().forEach(tParameter -> sb.append(indentation).append("print('").append(tParameter.getName()).append("='").append(" + ").append("str(").append(tParameter.getName()).append("))\n"));
        }

        sb.append(indentation).append("quit()\n\n");

        sb.append("if __name__ == \"__main__\":\n").append(indentation).append("main()");

        return sb.toString();
    }
}
