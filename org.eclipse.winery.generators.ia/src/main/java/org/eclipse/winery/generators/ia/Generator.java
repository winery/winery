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

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.winery.model.tosca.TInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used if no generator for the given artifact type exists.
 * Does not create any files.
 */
public abstract class Generator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Generator.class);
    
    // The name of the operation that an ImplementationArtifact should be generated for, if the name is "interface" an 
    // ImplementationArtifact for each operation will be generated
    final String operationName;
    final TInterface tInterface;
    final Path workingDirectory;
    
    public Generator(TInterface tInterface, String operationName, Path workingDir) {
        this.workingDirectory = workingDir;
        this.operationName = operationName;
        this.tInterface = tInterface;
        
        if (Files.exists(this.workingDirectory)) {
            LOGGER.error("Workdir " + this.workingDirectory + " already exits. This might lead to corrupted results if it is not empty!");
        }
    }

    public Path generateArtifact() throws Exception {
        Files.createDirectories(this.workingDirectory);
        Files.createFile(Paths.get(this.workingDirectory.toString(), "README.md"));
        generateImplementationArtifact();
        return this.workingDirectory;
    }

    /**
     * Hook used for the generation of the actual artifacts within the created working directory
     */
    public abstract void generateImplementationArtifact() throws Exception;

    public static Generator getGenerator(String artifactType, TInterface tInterface, String packageAndNamespace, URL iaArtifactTemplateUploadUrl, String name, Path workingDir, String operation) {
        artifactType = artifactType.substring(artifactType.lastIndexOf("}") + 1);
        return switch (artifactType) {
            case "Bash" -> new BashGenerator(tInterface, operation, workingDir);
            case "Python" -> new PythonGenerator(tInterface, operation, workingDir);
            case "WAR", "JAR" ->
                new JarAndWarGenerator(tInterface, packageAndNamespace, iaArtifactTemplateUploadUrl, name, workingDir);
            default -> new DefaultGenerator(tInterface, operation, workingDir);
        };
    }
}
