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
package org.eclipse.winery.lsp.Server.ServerCore.Parsing;

import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.winery.lsp.Server.ServerCore.DataModels.TOSCAFile;
import org.eclipse.winery.lsp.Server.ServerCore.ObjectConstruction.ToscaFileConstructor;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.Mark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class TOSCAFileParser implements Parser {
    
    private Map<String, Mark>  ConstructorPositions;
    private Map<String, Mark> ContextDependentConstructorPositions;

    private TOSCAFile ToscaFile;
    private String yamlContent;

    public Map<String, Mark> getContextDependentConstructorPositions() {
        return ContextDependentConstructorPositions;
    }

    public String getYamlContent() {
        return yamlContent;
    }

    public Map<String, Mark> getConstructorPositions() {
        return ConstructorPositions;
    }

    public TOSCAFile getToscaFile() {
        return ToscaFile;
    }

    @Override
    public Map<String, Object>  ParseTOSCAFile(Path path, LanguageClient client) throws IOException {
            this.yamlContent = Files.readString(path);
        return getStringObjectMap(yamlContent, client);
    }

    @Override
    public Map<String, Object> ParseTOSCAFile(String content, LanguageClient client) {
        return getStringObjectMap(content,client);
    }

    public Map<String, Object> getStringObjectMap(String yamlContent, LanguageClient client) {
        ToscaFileParsingConstructor constructor = new ToscaFileParsingConstructor();
        ToscaFileContextDependentConstructor validatingConstructor = new ToscaFileContextDependentConstructor();
        // Load the YAML content to parse it
        Yaml yaml = new Yaml(constructor);
        Yaml uniqueKeysYaml = new Yaml(validatingConstructor);
        uniqueKeysYaml.load(yamlContent);
        Map<String, Object> yamlMap = yaml.load(yamlContent);
        try {
            ToscaFile = ToscaFileConstructor.ConstructToscaFile(yamlMap) ;
        } catch (Exception e) {
            MessageParams messageParams = new MessageParams();
            messageParams.setMessage(e.getMessage());
            client.logMessage(messageParams);
        }
        
        ConstructorPositions = constructor.getPositions();
        ContextDependentConstructorPositions = validatingConstructor.getPositions(); 
        return yamlMap;
    }
}
