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

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.*;
import java.util.HashMap;
import java.util.Map;

public class ToscaFileContextDependentConstructor extends Constructor {
    private final Map<String, Mark> positions = new HashMap<>();
    private final StringBuilder currentPath = new StringBuilder();

    public ToscaFileContextDependentConstructor() {
        super(new LoaderOptions());
        this.yamlConstructors.put(new Tag(TOSCAFileParsingRecord.class), new ConstructToscaFile());
    }

    @Override
    protected Object constructObject(Node node) {
        if (node instanceof ScalarNode) {
            ScalarNode scalarNode = (ScalarNode) node;
            String key = !currentPath.isEmpty() ? currentPath + "." + scalarNode.getValue() : scalarNode.getValue();
            positions.put(key, scalarNode.getStartMark());
        } else if (node instanceof MappingNode) {
            processMappingNode((MappingNode) node);
        } else if (node instanceof SequenceNode) {
            processSequenceNode((SequenceNode) node);
        }
        return super.constructObject(node);
    }

    private void processMappingNode(MappingNode node) {
        String parentPath = currentPath.toString();
        for (NodeTuple tuple : node.getValue()) {
            Node keyNode = tuple.getKeyNode();
            Node valueNode = tuple.getValueNode();
            if (keyNode instanceof ScalarNode) {
                String key = ((ScalarNode) keyNode).getValue();
                if (!currentPath.isEmpty()) {
                    currentPath.append(".").append(key);
                } else {
                    currentPath.append(key);
                }
                constructObject(valueNode);
                currentPath.setLength(parentPath.length());
            }
        }
    }

    private void processSequenceNode(SequenceNode node) {
        String parentPath = currentPath.toString();
        for (int i = 0; i < node.getValue().size(); i++) {
            currentPath.append("[").append(i).append("]");
            constructObject(node.getValue().get(i));
            currentPath.setLength(parentPath.length());
        }
    }

    public Map<String, Mark> getPositions() {
        return positions;
    }

    private class ConstructToscaFile extends ConstructMapping {
        @Override
        public Object construct(Node node) {
            String parentPath = currentPath.toString();
            if (node instanceof MappingNode) {
                processMappingNode((MappingNode) node);
            } else if (node instanceof SequenceNode) {
                processSequenceNode((SequenceNode) node);
            }
            Object result = super.construct(node);
            currentPath.setLength(parentPath.length());
            return result;
        }
    }
}
