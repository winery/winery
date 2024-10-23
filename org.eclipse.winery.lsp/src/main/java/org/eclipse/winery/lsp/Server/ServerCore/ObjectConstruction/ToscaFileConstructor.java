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

import org.eclipse.winery.lsp.Server.ServerCore.DataModels.*;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaList;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaMap;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaString;

import java.util.*;

public class ToscaFileConstructor {
    public static TOSCAFile ConstructToscaFile(Map<String, Object> yamlMap) {
        ToscaString toscaDefinitionsVersion = new ToscaString("");
        if (yamlMap.get("tosca_definitions_version") != null && yamlMap.get("tosca_definitions_version") instanceof String) {
             toscaDefinitionsVersion = new ToscaString ((String) yamlMap.get("tosca_definitions_version"));
        }
        Optional<ToscaString> description = Optional.empty();
        if (yamlMap.get("description") != null && yamlMap.get("description") instanceof String) {
            description = Optional.of(new ToscaString( (String) yamlMap.get("description")));
        }

        Optional<ToscaString> profile = Optional.empty();
        if (yamlMap.get("profile") != null && yamlMap.get("profile") instanceof String) {
            profile = Optional.of(new ToscaString((String) yamlMap.get("profile")));
        }
        
        Optional<ToscaMap<String, Object>> metadata = Optional.empty();
        if (yamlMap.get("metadata") != null && yamlMap.get("metadata") instanceof Map) {
            metadata = Optional.of(new ToscaMap<>(((Map<String, Object>) yamlMap.get("metadata"))));
        }
        
        Optional<Object> dslDefinitions = Optional.ofNullable(yamlMap.get("dsl_definitions"));
        
        Map<String, ArtifactType> artifactTypes = new HashMap<>();
        if (yamlMap.get("artifact_types") != null && yamlMap.get("artifact_types") instanceof Map) {
            artifactTypes = ArtifactTypeParser.parseArtifactTypes((Map<String, Object>) yamlMap.get("artifact_types"));
        }
        
        ToscaMap<String, Object> dataTypes = new ToscaMap<>(new HashMap<>());
        if (yamlMap.get("data_types") != null && yamlMap.get("data_types") instanceof Map) {
            dataTypes = new ToscaMap<>((Map<String, Object>) yamlMap.get("data_types"));
        }
        
        Map<String, CapabilityType> capabilityTypes = new HashMap<>();
        if (yamlMap.get("capability_types") != null && yamlMap.get("capability_types") instanceof Map) { 
            capabilityTypes = CapabilityTypeParser.parseCapabilityTypes((Map<String, Object>) yamlMap.get("capability_types"));
        }
        
        ToscaMap<String, Object> interfaceTypes = new ToscaMap<>(new HashMap<>());
        if (yamlMap.get("interface_types") != null && yamlMap.get("interface_types") instanceof Map) {
            interfaceTypes = new ToscaMap<>((Map<String, Object>) yamlMap.get("interface_types"));        
        }
        
        ToscaMap<String, NodeType> nodeTypes = new ToscaMap<>(new HashMap<>());
        if (yamlMap.get("node_types") != null && yamlMap.get("node_types") instanceof Map) {
        try {
            nodeTypes = new ToscaMap<>(NodeTypeParser.parseNodeTypes((Map<String, Object>) yamlMap.get("node_types")));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        }

        ToscaMap<String, Object> groupTypes = new ToscaMap<>(new HashMap<>());
        if (yamlMap.get("group_types") != null && yamlMap.get("group_types") instanceof Map) {
            groupTypes = new ToscaMap<>((Map<String, Object>) yamlMap.get("group_types"));
        }

        ToscaMap<String, Object> policyTypes = new ToscaMap<>(new HashMap<>());
        if (yamlMap.get("policy_types") != null && yamlMap.get("policy_types") instanceof Map) {
            policyTypes = new ToscaMap<>((Map<String, Object>) yamlMap.get("policy_types"));
        }

        ToscaMap<String, Object> repositories = new ToscaMap<>(new HashMap<>());
        if (yamlMap.get("repositories") != null && yamlMap.get("repositories") instanceof Map) {
            repositories = new ToscaMap<>((Map<String, Object>) yamlMap.get("repositories"));
        }

        ToscaMap<String, Object> functions  = new ToscaMap<>(new HashMap<>());
        if (yamlMap.get("repositories") != null && yamlMap.get("repositories") instanceof Map) {
            functions = new ToscaMap<>((Map<String, Object>) yamlMap.get("functions"));
        }

        Optional<ToscaList<ImportDefinition>> imports = Optional.empty();
        if (yamlMap.get("imports") != null) {
            imports = Optional.of(new ToscaList<>(ImportDefinitionParser.parseImportDefinitions(yamlMap.get("imports"))));
        }
        
        Optional<ServiceTemplate> serviceTemplate = Optional.empty();
        if (yamlMap.get("service_template") instanceof Map) {
            serviceTemplate = Optional.of(Objects.requireNonNull(ServiceTemplateParser.parseServiceTemplate( (Map<String, Object>) yamlMap.get("service_template"))));
        }
        
        ToscaMap<String, RelationshipType> relationshipTypes = new ToscaMap<>(new HashMap<>());
        if (yamlMap.get("relationship_types") != null && yamlMap.get("relationship_types") instanceof Map) {
            relationshipTypes = new ToscaMap<>(RelationshipTypeParser.parseRelationshipTypes((Map<String, Object>) yamlMap.get("relationship_types")));
        }

        return new TOSCAFile(
            toscaDefinitionsVersion,
            description,
            metadata,
            dslDefinitions,
            artifactTypes,
            dataTypes,
            capabilityTypes,
            interfaceTypes,
            relationshipTypes,
            nodeTypes,
            groupTypes,
            policyTypes,
            repositories,
            functions,
            profile,
            imports,
            serviceTemplate
        );
    }
    
}
