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
/**
 * TOSCA File
 * For more details on the TOSCA specification, visit:
 *<a href="https://docs.oasis-open.org/tosca/TOSCA/v2.0/csd06/TOSCA-v2.0-csd06.html#61-keynames">TOSCA file Keynames</a>
 */
package org.eclipse.winery.lsp.Server.ServerCore.DataModels;

import io.soabase.recordbuilder.core.RecordBuilder;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaList;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaMap;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaString;
import java.util.Map;
import java.util.Optional;

@RecordBuilder
public record TOSCAFile(ToscaString toscaDefinitionsVersion, 
                        Optional<ToscaString> description,
                        Optional <ToscaMap<String, Object>> metadata,
                        Optional<Object> dslDefinitions,
                        Map<String, ArtifactType> artifactTypes,
                        ToscaMap<String, Object> dataTypes,//TODO Replace the objects with the real object representation
                        Map<String, CapabilityType> capabilityTypes,
                        ToscaMap<String, Object> interfaceTypes,
                        ToscaMap<String, RelationshipType> relationshipTypes,
                        ToscaMap<String, NodeType> nodeTypes,
                        ToscaMap<String, Object> groupTypes,
                        ToscaMap<String, Object> policyTypes,
                        ToscaMap<String, Object> repositories,
                        ToscaMap<String, Object> functions,
                        Optional<ToscaString> profile,
                        Optional<ToscaList<ImportDefinition>> imports,
                        Optional<ServiceTemplate> serviceTemplate) {
    
    public TOSCAFile updateArtifactTypes(String key, ArtifactType newArtifactType) {
            Map<String, ArtifactType> updatedArtifactTypes = artifactTypes;
            updatedArtifactTypes.put(key, newArtifactType);
            return new TOSCAFile(
                toscaDefinitionsVersion,
                 description,
                 metadata,
                 dslDefinitions,
                 updatedArtifactTypes,
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

    public TOSCAFile updateNodeTypes(String key, NodeType nodeType) {
            Map<String, NodeType> updatedNodeTypes = nodeTypes.getValue();
            updatedNodeTypes.put(key, nodeType);
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
                new ToscaMap<>(updatedNodeTypes),
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
