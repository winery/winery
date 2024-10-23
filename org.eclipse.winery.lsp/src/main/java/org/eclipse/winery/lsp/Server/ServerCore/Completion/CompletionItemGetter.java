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
package org.eclipse.winery.lsp.Server.ServerCore.Completion;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.winery.lsp.Server.ServerAPI.API.context.LSContext;
import org.eclipse.winery.lsp.Server.ServerCore.DataModels.TOSCAFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;

public class CompletionItemGetter {
    public List<CompletionItem> getAvailableArtifactTypes(LSContext lsContext) {
        List<String> artifactTypes = new ArrayList<>();
        if (lsContext.getCurrentToscaFile() != null) {
            if (!lsContext.getCurrentToscaFile().artifactTypes().isEmpty()) {
                for (String key : lsContext.getCurrentToscaFile().artifactTypes().keySet()) {
                    artifactTypes.add(" " + key);
                }
            }
            artifactTypes.addAll(getArtifactTypesInImportedFiles(lsContext));

            return artifactTypes.stream()
                .map(type -> {
                    CompletionItem item = new CompletionItem(type);
                    item.setKind(CompletionItemKind.Value);
                    return item;
                })
                .collect(toList());
        }
        return new ArrayList<>();
    }

    private Collection<String> getArtifactTypesInImportedFiles(LSContext context) {
        List<String> artifactTypes = new ArrayList<>();
        if (context.getCurrentToscaFile().imports().isPresent()) {
            Collection<Map<String, TOSCAFile>> imports = context.getImportedToscaFiles().get(context.getCurrentToscaFilePath());
            for (Map<String, TOSCAFile> mapOfImportedFiles : imports) {
                for (TOSCAFile file : mapOfImportedFiles.values()) {
                    if (file != null && file.artifactTypes() != null) {
                        for (String key : file.artifactTypes().keySet()) {
                            artifactTypes.add(" " + key);
                        }
                    }
                }
            }
        }
        return artifactTypes;
    }

    public List<CompletionItem> getAvailableCapabilityTypes(LSContext lsContext) {
        List<String> capabilityTypes = new ArrayList<>();
        if (lsContext.getCurrentToscaFile() != null) {
            if ( lsContext.getCurrentToscaFile().capabilityTypes() != null && !lsContext.getCurrentToscaFile().capabilityTypes().isEmpty()) {
                for (String key : lsContext.getCurrentToscaFile().capabilityTypes().keySet()) {
                    capabilityTypes.add(" " + key);
                }
            }
            capabilityTypes.addAll(getCapabilityTypesInImportedFiles(lsContext));
            return capabilityTypes.stream()
                .map(type -> {
                    CompletionItem item = new CompletionItem(type);
                    item.setKind(CompletionItemKind.Value);
                    return item;
                })
                .collect(toList()); 
        }
        return new ArrayList<>();
    }

    public List<CompletionItem> getTOSCAFileKeywords(Position position) {
        List<String> keywords = List.of(
            "tosca_definitions_version:", "description:", "metadata:", "dsl_definitions:",
            "artifact_types:", "data_types:", "capability_types:", "interface_types:",
            "relationship_types:", "node_types:", "group_types:", "policy_types:",
            "repositories:", "functions:", "profile:", "imports:", "service_template:"
        );

        return getCompletionItems(position, keywords);
    }

    public List<CompletionItem> getArtifactTypesKeyWords(Position position) {
        List<String> keywords = List.of(
            "derived_from:", "version:", "metadata:", "description:",
            "mime_type:", "file_ext:", "properties:"
        );

        return getCompletionItems(position, keywords);
    }

    public List<CompletionItem> getCapabilityTypesKeyWords(Position position) {
        List<String> keywords = List.of(
            "derived_from:", "version:", "metadata:", "description:",
            "properties:", "attributes:", "valid_source_node_types:", "valid_relationship_types:"
        );

        return getCompletionItems(position, keywords);
    }

    public List<CompletionItem> getCompletionItems(Position position, List<String> keywords) {
        return keywords.stream()
            .map(keyword -> {
                CompletionItem item = new CompletionItem(keyword);
                item.setKind(CompletionItemKind.Keyword);
                // Create a TextEdit to remove the trailing space
                TextEdit textEdit = new TextEdit(
                    new Range(
                        new Position(position.getLine(), Math.max(0, position.getCharacter() - 1)), // Ensure character index is not negative
                        new Position(position.getLine(), position.getCharacter())
                    ),
                    keyword
                );
                item.setTextEdit(Either.forLeft(textEdit));
                return item;
            })
            .collect(toList());
    }
    
    public List<CompletionItem> getNodeTypesKeyWords(Position position) {
        List<String> keywords = List.of(
            "derived_from:", "version:", "metadata:", "description:", "properties:", "attributes:", "capabilities:", "requirements:","interfaces:", "artifacts:"
        );
        return getCompletionItems(position, keywords);
    }

    public List<CompletionItem> getNodeTemplateKeyWords(Position position) {
        List<String> keywords = List.of(
            "type:", "description:", "metadata:", "directives:", "properties:", "attributes:", "requirements:", "capabilities:","interfaces:", "artifacts:", "count:", "node_filter:", "copy:"
        );
        return getCompletionItems(position, keywords);
    }
    
    public List<CompletionItem> getAvailableNodeTypes(LSContext lsContext) {
        List<String> nodeTypes = new ArrayList<>();
        if (lsContext.getCurrentToscaFile() != null) {
            if (lsContext.getCurrentToscaFile().nodeTypes() != null && lsContext.getCurrentToscaFile().nodeTypes() != null) {
                for (String key : lsContext.getCurrentToscaFile().nodeTypes().getValue().keySet()) {
                    nodeTypes.add(" " + key);
                }
            }
            nodeTypes.addAll(getNodeTypesInImportedFiles(lsContext));
            return nodeTypes.stream()
                .map(type -> {
                    CompletionItem item = new CompletionItem(type);
                    item.setKind(CompletionItemKind.Value);
                    return item;
                })
                .collect(toList());
        }
        return new ArrayList<>();
    }

    private List<String> getCapabilityTypesInImportedFiles(LSContext context) {
        List<String> capabilityTypes = new ArrayList<>();
        if (context.getCurrentToscaFile().imports().isPresent()) {
            Collection<Map<String, TOSCAFile>> imports = context.getImportedToscaFiles().get(context.getCurrentToscaFilePath());
            for (Map<String, TOSCAFile> mapOfImportedFiles : imports) {
                for (TOSCAFile file : mapOfImportedFiles.values()) {
                    if (file != null && file.capabilityTypes() != null) {
                        for (String key : file.capabilityTypes().keySet()) {
                            capabilityTypes.add(" " + key);
                        }
                    }
                }
            }
        }
        return capabilityTypes;
    }

    private List<String> getNodeTypesInImportedFiles(LSContext context) {
        List<String> nodeTypes = new ArrayList<>();
            if (context.getCurrentToscaFile().imports().isPresent()) {
                Collection<Map<String, TOSCAFile>> imports = context.getImportedToscaFiles().get(context.getCurrentToscaFilePath());
                for (Map<String, TOSCAFile> mapOfImportedFiles : imports) {
                    for (TOSCAFile file : mapOfImportedFiles.values()) {
                        if (file != null && file.nodeTypes() != null && !file.nodeTypes().getValue().isEmpty()) {
                            for (String key : file.nodeTypes().getValue().keySet()) {
                                nodeTypes.add(" " + key);
                            }
                        }
                    }
                }
            }
         return nodeTypes;
    }

    public List<CompletionItem> getCapabilityDefinitionKeyWords(Position position) {
        List<String> keywords = List.of(
            "type:", "description:", "metadata:", "properties:",
            "attributes:", "valid_source_node_types:", "valid_relationship_types:"
        );

        return getCompletionItems(position, keywords);
    }

    public List<CompletionItem> getAvailableRelationshipTypes(LSContext lsContext) {
        List<String> relationshipTypes = new ArrayList<>();
        if (lsContext.getCurrentToscaFile() != null) {
            if (lsContext.getCurrentToscaFile().relationshipTypes() != null) {
                for (String key : lsContext.getCurrentToscaFile().relationshipTypes().getValue().keySet()) {
                    relationshipTypes.add(" " + key);
                }   
            }
            relationshipTypes.addAll(getRelationshipTypesInImportedFiles(lsContext));
            return relationshipTypes.stream()
                .map(type -> {
                    CompletionItem item = new CompletionItem(type);
                    item.setKind(CompletionItemKind.Value);
                    return item;
                })
                .collect(toList());
        }
        return new ArrayList<>();
    }
    
    private List<String> getRelationshipTypesInImportedFiles(LSContext context) {
        List<String> RelationshipTypes = new ArrayList<>();
        if (context.getCurrentToscaFile().imports().isPresent()) {
            Collection<Map<String, TOSCAFile>> imports = context.getImportedToscaFiles().get(context.getCurrentToscaFilePath());
            for (Map<String, TOSCAFile> mapOfImportedFiles : imports) {
                for (TOSCAFile file : mapOfImportedFiles.values()) {
                    if (file != null && file.relationshipTypes() != null) {
                        for (String key : file.relationshipTypes().getValue().keySet()) {
                            RelationshipTypes.add(" " + key);
                        }
                    }
                }
            }
        }
        return RelationshipTypes;
    }
    
    public List<CompletionItem> getRelationshipTypesKeyWords(Position position) {
        List<String> keywords = List.of(
            "derived_from", "version", "metadata", "description", "properties", "attributes", "interfaces", "valid_capability_types","valid_target_node_types", "valid_source_node_types"

        );
        return getCompletionItems(position, keywords);
    }
}

