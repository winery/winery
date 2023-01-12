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

public class BashGenerator extends Generator {

    public BashGenerator(TInterface tInterface, String operationName, Path workingDir) {
        super(tInterface, operationName, workingDir);
    }
    
    @Override
    public void generateImplementationArtifact() throws IOException {
        if (operationName.equals("interface")) {
            throw new IllegalArgumentException("The whole interface cannot be generated for bash as artifact type!");
        } else {
            StringBuilder sb = new StringBuilder();
            TOperation operationToImplement = tInterface.getOperations().stream().parallel().filter(operation1 -> operation1.getName().equals(operationName)).findAny().orElseThrow(IllegalArgumentException::new);
            String content = generateBashFileContent(sb, tInterface.getName(), operationToImplement);
            Files.write(Paths.get(this.workingDirectory.toString(), operationName + ".sh"), content.getBytes());
        }
    }

    /**
     * Generates the bash file for the implementation of the provided operation of the provided interface.
     *
     * @return The contents of the bash file
     */
    private String generateBashFileContent(StringBuilder sb, String interfaceName, TOperation operation) {
        sb.append("#!/bin/bash\n");
        sb.append("# Implements the ").append(operation.getName()).append(" operation of the ").append(interfaceName).append(" interface\n");
        // Add example call:
        sb.append("# Example call: ");
        if (operation.getInputParameters() != null) {
            operation.getInputParameters().forEach(tParameter -> sb.append(tParameter.getName()).append("=test").append(tParameter.getName()).append(" "));
        }
        sb.append("./").append(operationName).append(".sh\n\n");

        // Add input parameters and assign them to the positional arguments
        if (operation.getInputParameters() != null) {
            sb.append("# Print input parameters:\n");
            operation.getInputParameters().forEach(tParameter -> sb.append("echo $").append(tParameter.getName()).append(" # Type: ").append(tParameter.getName().replace("xsd:", "")).append("\n"));
        }
        // Add output parameters
        if (operation.getOutputParameters() != null) {
            sb.append("\n# The Output parameters and their expected types:\n");
            String outputParametersAndType = operation.getOutputParameters().stream().map(tParameter -> tParameter.getName() + " # Type: " + tParameter.getType().replace("xsd:", "")).collect(Collectors.joining(",\n"));
            sb.append(outputParametersAndType);
            sb.append("\n");
        }

        sb.append("\n# Your Code here\n\n");

        if (operation.getOutputParameters() != null) {
            sb.append("# Return\n");
            for (TParameter output : operation.getOutputParameters()) {
                sb.append("echo \"").append(output.getName()).append("=${").append(output.getName()).append("}\"\n");
            }
        }
        return sb.toString();
    }
}
