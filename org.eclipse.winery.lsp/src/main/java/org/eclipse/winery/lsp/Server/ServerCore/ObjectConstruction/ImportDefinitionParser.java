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

import org.eclipse.winery.lsp.Server.ServerCore.DataModels.ImportDefinition;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaMap;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaString;

import java.util.*;

public class ImportDefinitionParser {
    public static List<ImportDefinition> parseImportDefinitions(Object imports) {
        if (!(imports instanceof List<?>)) {
            return List.of();
        }

        List<ImportDefinition> importDefinitions = new ArrayList<>();

        for (Object importItem : (List<Object>) imports) {
            ImportDefinition importDef = null;
            if (importItem instanceof Map) {
                 importDef = parseImportDefinition((Map<?, ?>) importItem);
            }
            if (importDef != null) {
                importDefinitions.add(importDef);
            }
        }
        return importDefinitions;
    }

    private static ImportDefinition parseImportDefinition(Map<?, ?> importMap) {
        if (importMap == null) {
            return null;
        }

        Optional<ToscaString> url = Optional.empty();
        if (importMap.get("url") != null && importMap.get("url") instanceof String) {
            url = Optional.of(new ToscaString((String) importMap.get("url")));
        }

        Optional<ToscaString> profile = Optional.empty();
        if (importMap.get("profile") != null && importMap.get("profile") instanceof String) {
            profile = Optional.of(new ToscaString((String) importMap.get("profile")));
        }

        Optional<ToscaString> repository = Optional.empty();
        if (importMap.get("repository") != null && importMap.get("repository") instanceof String) {
            repository = Optional.of(new ToscaString((String) importMap.get("repository")));
        }

        Optional<ToscaString> namespace = Optional.empty();
        if (importMap.get("namespace") != null && importMap.get("namespace") instanceof String) {
            namespace = Optional.of(new ToscaString((String) importMap.get("namespace")));
        }

        Optional<ToscaString> description = Optional.empty();
        if (importMap.get("description") != null && importMap.get("description") instanceof String) {
            namespace = Optional.of(new ToscaString((String) importMap.get("description")));
        }

        Optional<ToscaMap<String, Object>> metadata = Optional.empty();
        if (importMap.get("metadata") != null && importMap.get("metadata") instanceof Map) {
            metadata = Optional.of(new ToscaMap<>((Map) importMap.get("metadata")));
        }

        return new ImportDefinition(url,
            profile,
            repository,
            namespace,
            description,
            metadata); 
    }

    private static Map<String, Object> parseStringToMap(String importItem) {
        Map<String, Object> map = new HashMap<>();
        String[] lines = importItem.split("\n");

        for (String line : lines) {
            line = line.trim();
            if (line.contains(":")) {
                String[] keyValue = line.split(":", 2);
                String key = keyValue[0].trim();
                Object value = keyValue[1].trim();
                map.put(key, value);
            }
        }
        return map;
    }
}
