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

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.Position;
import org.eclipse.winery.lsp.Server.ServerAPI.API.context.LSContext;
import org.eclipse.winery.lsp.Server.ServerCore.ToscaContext;
import java.util.List;
import java.util.Stack;

public class AutoCompletionHandler {
    LSContext lsContext;
    ToscaContext toscaContext;
    public AutoCompletionHandler(LSContext lsContext) {
        this.lsContext = lsContext;
    }

    public List<CompletionItem> handel(String line, Position position, String content) {
        toscaContext = new ToscaContext();
        toscaContext.buildContextStack(content, position.getLine());
        return handelCompletion(line, position, content);
    }

        private List<CompletionItem> handelCompletion(String line, Position position, String content) {
            // Auto complete the TOSCAFile Keywords when press space
            if (line.startsWith(" ") && line.length() <= 2) { 
                CompletionItemGetter completionItemGetter = new CompletionItemGetter();
                return completionItemGetter.getTOSCAFileKeywords(position);
            }    
            // Artifact type auto-completion logic 
            if (artifactTypeCompletion(line, position) != null && !artifactTypeCompletion(line, position).isEmpty()) {
                return artifactTypeCompletion(line, position);
            }
            // Capability type auto-completion logic
            if (capabilityTypeCompletion(line, position) != null && !capabilityTypeCompletion(line, position).isEmpty() ) {
                return capabilityTypeCompletion(line, position);
            }
            // Node type auto-completion logic
            if (nodeTypeCompletion(line, position) != null && !nodeTypeCompletion(line, position).isEmpty() ) {
                return nodeTypeCompletion(line, position);
            }
            // Node template auto-completion logic
            if (nodeTemplateCompletion(line, position) != null && !nodeTemplateCompletion(line, position).isEmpty()) {
                return nodeTemplateCompletion(line, position);
            }
            // Capability definition auto-completion logic
            if (capabilityDefinitionCompletion(line, position) != null && !capabilityDefinitionCompletion(line, position).isEmpty()) {
                return capabilityDefinitionCompletion(line, position);
            }
            // Relationship type auto-completion logic
            if (relationshipTypeCompletion(line, position) != null && !relationshipTypeCompletion(line, position).isEmpty()) {
                return relationshipTypeCompletion(line, position);
            }
            return List.of();
        }

    private List<CompletionItem> relationshipTypeCompletion(String line, Position position) {

        Stack<String> stack = new Stack<>();
        if (!toscaContext.getContextStack().isEmpty()) {
            stack = (Stack<String>) toscaContext.getContextStack().clone();
            while (!stack.isEmpty() && stack.peek().equals("capabilities")) {
                stack.pop();
            }
        }
        
        if (line.contains("derived_from:") && toscaContext.getContextStack() != null && !toscaContext.getContextStack().isEmpty() && (toscaContext.getContextStack().peek().equals("relationship_types") || (!stack.isEmpty() && stack.peek().equals("relationship_types"))) ) {
            CompletionItemGetter completionItemGetter = new CompletionItemGetter();
            return completionItemGetter.getAvailableRelationshipTypes(lsContext);
        }
        else if (line.startsWith("    ") && line.trim().isEmpty() && toscaContext.getContextStack() != null && !toscaContext.getContextStack().isEmpty() && (toscaContext.getContextStack().peek().equals("relationship_types"))) {
            CompletionItemGetter completionItemGetter = new CompletionItemGetter();
            return completionItemGetter.getRelationshipTypesKeyWords(position);
        }
        return List.of();
    }

    private List<CompletionItem> capabilityDefinitionCompletion(String line, Position position) {
        Stack<String> stack = new Stack<>();
        if (!toscaContext.getContextStack().isEmpty()) {
            stack = (Stack<String>) toscaContext.getContextStack().clone();
            while (!stack.isEmpty() && stack.peek().equals("capabilities")) {
                stack.pop();
            }
        }

        if (line.contains("type:") && toscaContext.getContextStack() != null && !toscaContext.getContextStack().isEmpty() && toscaContext.getContextStack().peek().equals("capabilities") && !(!stack.isEmpty() && stack.peek().equals("node_templates"))) { //checks if we are under a node_templates
            CompletionItemGetter completionItemGetter = new CompletionItemGetter();
            return completionItemGetter.getAvailableCapabilityTypes(lsContext);
        } else if (line.startsWith("    ") && line.trim().isEmpty() && toscaContext.getContextStack() != null && !toscaContext.getContextStack().isEmpty() && (toscaContext.getContextStack().peek().equals("capabilities"))) {
            CompletionItemGetter completionItemGetter = new CompletionItemGetter();
            return completionItemGetter.getCapabilityDefinitionKeyWords(position);
        }
        return List.of();
    }

    private List<CompletionItem> nodeTemplateCompletion(String line, Position position) {
        Stack<String> stack = new Stack<>();
        if (!toscaContext.getContextStack().isEmpty()) {
            stack = (Stack<String>) toscaContext.getContextStack().clone();
            while (!stack.isEmpty() && stack.peek().equals("capabilities")) {
                stack.pop();
            }
        }   
        
        if (line.contains("type:") && toscaContext.getContextStack() != null && !toscaContext.getContextStack().isEmpty() && (toscaContext.getContextStack().peek().equals("node_templates") || (!stack.isEmpty()  && stack.peek().equals("node_templates")))) {
            CompletionItemGetter completionItemGetter = new CompletionItemGetter();
            return completionItemGetter.getAvailableNodeTypes(lsContext);
        } else if (line.startsWith("    ") && line.trim().isEmpty() && toscaContext.getContextStack() != null && !toscaContext.getContextStack().isEmpty() && (toscaContext.getContextStack().peek().equals("node_templates"))) {
            CompletionItemGetter completionItemGetter = new CompletionItemGetter();
            return completionItemGetter.getNodeTemplateKeyWords(position);
        }
        return List.of();
    }

    private List<CompletionItem> artifactTypeCompletion(String line, Position position) {
        Stack<String> stack = new Stack<>();
        if (!toscaContext.getContextStack().isEmpty()) {
            stack = (Stack<String>) toscaContext.getContextStack().clone();
            while (!stack.isEmpty() && stack.peek().equals("capabilities")) {
                stack.pop();
            }
        }
        
        if (line.contains("derived_from:") && toscaContext.getContextStack() != null && !toscaContext.getContextStack().isEmpty() && (toscaContext.getContextStack().peek().equals("artifact_types") || (!stack.isEmpty() && stack.peek().equals("artifact_types")))) {
            CompletionItemGetter completionItemGetter = new CompletionItemGetter();
            return completionItemGetter.getAvailableArtifactTypes(lsContext);
        } else if (line.startsWith("    ") && line.trim().isEmpty() && toscaContext.getContextStack() != null && !toscaContext.getContextStack().isEmpty() && (toscaContext.getContextStack().peek().equals("artifact_types"))) {
            CompletionItemGetter completionItemGetter = new CompletionItemGetter();
            return completionItemGetter.getArtifactTypesKeyWords(position);
        }
        return List.of();
        }

        private List<CompletionItem> capabilityTypeCompletion(String line, Position position) {

            Stack<String> stack = new Stack<>();
            if (!toscaContext.getContextStack().isEmpty()) {
                stack = (Stack<String>) toscaContext.getContextStack().clone();
                while (!stack.isEmpty() && stack.peek().equals("capabilities")) {
                    stack.pop();
                }
            }

            if (line.contains("derived_from:") && toscaContext.getContextStack() != null && !toscaContext.getContextStack().isEmpty() && (toscaContext.getContextStack().peek().equals("capability_types") || (!stack.isEmpty() && stack.peek().equals("capability_types")))) {
            CompletionItemGetter completionItemGetter = new CompletionItemGetter();
            return completionItemGetter.getAvailableCapabilityTypes(lsContext);
        }
        else if (line.startsWith("    ") && line.trim().isEmpty() && toscaContext.getContextStack() != null && !toscaContext.getContextStack().isEmpty() && (toscaContext.getContextStack().peek().equals("capability_types"))) {
            CompletionItemGetter completionItemGetter = new CompletionItemGetter();
            return completionItemGetter.getCapabilityTypesKeyWords(position);
        }
        return List.of();
        }

    private List<CompletionItem> nodeTypeCompletion(String line, Position position) {

        Stack<String> stack = new Stack<>();
        if (!toscaContext.getContextStack().isEmpty()) {
            stack = (Stack<String>) toscaContext.getContextStack().clone();
            while (!stack.isEmpty() && stack.peek().equals("capabilities")) {
                stack.pop();
            }
        }
        
        if (line.contains("derived_from:") && toscaContext.getContextStack() != null && !toscaContext.getContextStack().isEmpty() && (toscaContext.getContextStack().peek().equals("node_types") || (!stack.isEmpty() && stack.peek().equals("node_types"))) ) {
            CompletionItemGetter completionItemGetter = new CompletionItemGetter();
            return completionItemGetter.getAvailableNodeTypes(lsContext);
        }
        else if (line.startsWith("    ") && line.trim().isEmpty() && toscaContext.getContextStack() != null && !toscaContext.getContextStack().isEmpty() && (toscaContext.getContextStack().peek().equals("node_types"))) {
            CompletionItemGetter completionItemGetter = new CompletionItemGetter();
            return completionItemGetter.getNodeTypesKeyWords(position);
        }
        return List.of();
    }
}
