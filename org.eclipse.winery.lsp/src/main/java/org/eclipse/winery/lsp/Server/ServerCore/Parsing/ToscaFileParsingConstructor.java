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
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.ScalarNode;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;

public class ToscaFileParsingConstructor extends Constructor {
    private final Map<String, Mark> positions = new HashMap<>();

    public ToscaFileParsingConstructor() {
        super(new LoaderOptions());
        this.yamlConstructors.put(new Tag(TOSCAFileParsingRecord.class), new ConstructToscaFile());
    }
    
    @Override
    protected Object constructObject(Node node) {
        if (node instanceof ScalarNode) {
            ScalarNode scalarNode = (ScalarNode) node;
            positions.put(scalarNode.getValue(), scalarNode.getStartMark());
        }
        return super.constructObject(node);
    }
    
    public Map<String, Mark> getPositions() {
        return positions;
    }

    private class ConstructToscaFile extends ConstructMapping {
        @Override
        public Object construct(Node node) {
            Map<Object, Object> map = constructMapping((MappingNode) node);
            return new TOSCAFileParsingRecord(
                (String) map.get("tosca_definitions_version"),
                Optional.ofNullable((String) map.get("description")),
                Optional.ofNullable((Map<String, Object>) map.get("metadata")),
                Optional.ofNullable(map.get("dsl_definitions")),
                Optional.ofNullable((Map<String, Object>) map.get("artifact_types")),
                Optional.ofNullable((Map<String, Object>) map.get("data_types")),
                Optional.ofNullable((Map<String, Object>) map.get("capability_types")),
                Optional.ofNullable((Map<String, Object>) map.get("interface_types")),
                Optional.ofNullable((Map<String, Object>) map.get("relationship_types")),
                Optional.ofNullable((Map<String, Object>) map.get("node_types")),
                Optional.ofNullable((Map<String, Object>) map.get("group_types")),
                Optional.ofNullable((Map<String, Object>) map.get("policy_types")),
                Optional.ofNullable((Map<String, Object>) map.get("repositories")),
                Optional.ofNullable((Map<String, Object>) map.get("functions")),
                Optional.ofNullable((String) map.get("profile")),
                Optional.ofNullable((List<Object>) map.get("imports")),
                Optional.ofNullable(map.get("service_template"))
            );
        }
    }
}
