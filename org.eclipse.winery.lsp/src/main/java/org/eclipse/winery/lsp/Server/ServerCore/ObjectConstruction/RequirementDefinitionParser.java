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

import org.eclipse.winery.lsp.Server.ServerCore.DataModels.RequirementDefinition;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaMap;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaString;

import java.util.*;

public class RequirementDefinitionParser {

    public static List<RequirementDefinition> parseRequirementDefinitions(Object requirements) {
        if (!(requirements instanceof List<?>)) {
            return List.of();
        }

        List<RequirementDefinition> requirementDefinitions = new ArrayList<>();

        for (Object requirementItem : (List<?>) requirements) {
            RequirementDefinition requirementDef = null;
            if (requirementItem instanceof Map) {
                for (Object requirementElement :((Map<?, ?>) requirementItem).keySet()) {
                    if (((Map<?, ?>) requirementItem).get(requirementElement) instanceof Map) {
                        requirementDef = parseRequirementDefinition((Map<?, ?>) ((Map<?, ?>) requirementItem).get(requirementElement), (String) requirementElement);
                    }
                }
            }
            if (requirementDef != null) {
                requirementDefinitions.add(requirementDef);
            }
        }
        return requirementDefinitions;
    }

    private static RequirementDefinition parseRequirementDefinition(Map<?, ?> requirementMap, String requirementName) {
        if (requirementMap == null) {
            return null;
        }
        
        Optional<ToscaString> description = Optional.empty();
        if (requirementMap.get("description") != null && requirementMap.get("description") instanceof String) {
            description = Optional.of(new ToscaString((String) requirementMap.get("description")));
        }

        Optional<ToscaMap<String, Object>> metadata = Optional.empty();
        if (requirementMap.get("metadata") != null && requirementMap.get("metadata") instanceof Map) {
            metadata = Optional.of(new ToscaMap<>((Map<String, Object>) requirementMap.get("metadata")));
        }

        ToscaString relationship = null;
        if (requirementMap.get("relationship") != null && requirementMap.get("relationship") instanceof String) {
            relationship = new ToscaString((String) requirementMap.get("relationship"));
        }

        Optional<ToscaString> node = Optional.empty();
        if (requirementMap.get("node") != null && requirementMap.get("node") instanceof String) {
            node = Optional.of(new ToscaString((String) requirementMap.get("node")));
        }

        ToscaString capability = null;
        if (requirementMap.get("capability") != null && requirementMap.get("capability") instanceof String) {
            capability = new ToscaString((String) requirementMap.get("capability"));
        }

        Optional<Object> countRange = Optional.empty();
        if (requirementMap.get("count_range") != null) {
            countRange = Optional.of(requirementMap.get("count_range"));
        }

        Optional<Object> nodeFilter = Optional.empty();
        if (requirementMap.get("node_filter") != null) {
            nodeFilter = Optional.of(requirementMap.get("node_filter"));
        }

        return new RequirementDefinition(
            requirementName,
            description,
            metadata,
            relationship,
            node,
            capability,
            countRange,
            nodeFilter
        );
    }
}
