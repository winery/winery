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

import org.eclipse.winery.lsp.Server.ServerCore.DataModels.ArtifactType;
import org.eclipse.winery.lsp.Server.ServerCore.DataModels.PropertyDefinition;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaList;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaMap;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaString;
import java.util.*;
import java.util.stream.Collectors;

public class ArtifactTypeParser {
    private static final Map<String, ArtifactType> artifactTypesNamesMap = new HashMap<>();
    public static Map<String, ArtifactType> parseArtifactTypes(Map<String, Object> artifactTypesMap) {
        if (artifactTypesMap == null) {
            return Collections.emptyMap();
        } 
        return artifactTypesMap.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> {
                    ArtifactType artifactType  = new ArtifactType(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Map.of());
                    if (e.getValue() != null && e.getValue() instanceof Map) {
                        artifactType = ArtifactTypeParser.parseArtifactType((Map<String, Object>) e.getValue());
                        artifactTypesNamesMap.put(e.getKey(), artifactType);
                    }
                    artifactTypesNamesMap.put(e.getKey(), artifactType);
                    return artifactType;
                }
            ));
    }
    
    public static ArtifactType parseArtifactType(Map<String, Object> artifactTypeMap) {
        if (artifactTypeMap == null) {
            return null;
        }
        
        Optional<ToscaString> version = Optional.empty();
        if (artifactTypeMap.get("version") != null && artifactTypeMap.get("version") instanceof String) {
            version = Optional.of(new ToscaString((String) artifactTypeMap.get("version")));
        }
        Optional<ToscaMap<String, String>> metadata  = Optional.empty();
        if (artifactTypeMap.get("metadata") != null && artifactTypeMap.get("metadata") instanceof Map) {
            metadata = Optional.of(new ToscaMap<>((Map<String, String>) artifactTypeMap.get("metadata")));
        }

        Optional<ToscaString> description = Optional.empty();
        if (artifactTypeMap.get("description") != null && artifactTypeMap.get("description") instanceof String) {
            description = Optional.of(new ToscaString((String) artifactTypeMap.get("description")));
        }

        Optional<ToscaString> mimeType = Optional.empty();
        if (artifactTypeMap.get("mime_type") != null && artifactTypeMap.get("mime_type") instanceof String) {
            mimeType = Optional.of(new ToscaString((String) artifactTypeMap.get("mime_type")));
        }

        Optional<ToscaList<String>> fileExt  = Optional.empty();
        if (artifactTypeMap.get("file_ext") != null && artifactTypeMap.get("file_ext") instanceof List<?>) {
            fileExt = Optional.of(new ToscaList<>((List<String>) artifactTypeMap.get("file_ext")));
        }

        Map<String, PropertyDefinition> properties = new HashMap<>();
        if (artifactTypeMap.get("properties") != null && artifactTypeMap.get("properties") instanceof Map) {
            properties = PropertyDefinitionParser.parseProperties((Map<String, Object>) artifactTypeMap.get("properties"));
        }

        Optional<ArtifactType> derivedFrom = Optional.empty();
        try {
            if (artifactTypeMap.get("derived_from") != null && artifactTypeMap.get("derived_from")  instanceof String) {
                ArtifactType derivedFromValue = getArtifactType((String) artifactTypeMap.get("derived_from"));

                if (derivedFromValue != null) {
                    derivedFrom = Optional.of(derivedFromValue);
                    properties.putAll(derivedFromValue.properties());
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return new ArtifactType(
            derivedFrom,
            version,
            metadata,
            description,
            mimeType,
            fileExt,
            properties
        );
    }

    public static ArtifactType getArtifactType(String derivedFrom) {
        return artifactTypesNamesMap.getOrDefault(derivedFrom, null);
    }

}
