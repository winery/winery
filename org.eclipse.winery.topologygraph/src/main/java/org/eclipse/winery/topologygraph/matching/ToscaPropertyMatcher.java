/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.topologygraph.matching;

import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaEntity;
import org.eclipse.winery.topologygraph.model.ToscaNode;

import org.apache.commons.lang3.StringUtils;

public class ToscaPropertyMatcher extends ToscaTypeMatcher {

    private final boolean matchPropertiesByRegex;

    public ToscaPropertyMatcher() {
        this(false);
    }

    public ToscaPropertyMatcher(boolean matchPropertiesByRegex) {
        this.matchPropertiesByRegex = matchPropertiesByRegex;
    }

    @Override
    public boolean isCompatible(ToscaNode left, ToscaNode right) {
        return super.isCompatible(left, right)
            && propertiesCompatible(left, right);
    }

    @Override
    public boolean isCompatible(ToscaEdge left, ToscaEdge right) {
        return super.isCompatible(left, right)
            && propertiesCompatible(left, right);
    }

    public boolean propertiesCompatible(ToscaEntity left, ToscaEntity right) {
        // By convention, the left node is always the element to search in right.
        TEntityTemplate detectorElement = left.getTemplate();
        TEntityTemplate candidate = right.getTemplate();

        // TODO the implementation (currently) works for KV properties only
        Map<String, String> detectorProperties = ModelUtilities.getPropertiesKV(detectorElement);
        Map<String, String> candidateProperties = ModelUtilities.getPropertiesKV(candidate);

        if (detectorProperties == null || detectorProperties.isEmpty()) {
            return true; // detector and candidate are always compatible if detector specifies no properties
        }

        if (candidateProperties == null || candidateProperties.isEmpty()) {
            return false; // detector and candidate cannot be compatible if detector requires properties, but candidate has none
        }

        return detectorProperties.entrySet().stream()
            .allMatch(entry -> {
                String val = entry.getValue();
                if (StringUtils.isEmpty(val)) {
                    return true; // always match if detector value is empty
                }
                // Assumption: properties are simple KV Properties
                String refProp = candidateProperties.get(entry.getKey());
                if (StringUtils.isEmpty(refProp)) {
                    return false; // cannot match if candidate value is empty but detector value is not
                }
                if (val.equalsIgnoreCase("*")) {
                    // if the detector defines a wildcard, the property must be set in the candidate
                    return StringUtils.isNotEmpty(refProp);
                } else {
                    // if the detector defines a specific value, the candidate's property must match
                    if (matchPropertiesByRegex) {
                        return Pattern.matches(val, refProp);
                    } else {
                        return val.equalsIgnoreCase(refProp);
                    }
                }
            });
    }
}
