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
import java.util.Objects;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaEntity;
import org.eclipse.winery.topologygraph.model.ToscaNode;

public class ToscaPropertyMatcher extends ToscaTypeMatcher {

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
        boolean propertiesCompatible = true;

        // By convention, the left node is always the element to search in right.
        TEntityTemplate detectorElement = left.getTemplate();
        TEntityTemplate candidate = right.getTemplate();

        Map<String, String> detectorProperties = ModelUtilities.getPropertiesKV(detectorElement);
        Map<String, String> candidateProperties = ModelUtilities.getPropertiesKV(candidate);
        if (Objects.nonNull(detectorElement.getProperties()) && Objects.nonNull(candidate.getProperties())
            // TODO the implementation (currently) works for KV properties only
            && Objects.nonNull(detectorProperties)
            && Objects.nonNull(candidateProperties)) {

            propertiesCompatible = detectorProperties.entrySet().stream()
                .allMatch(entry -> {
                    if (entry.getValue() == null) {
                        return true;
                    }
                    String val = entry.getValue();
                    if (val.isEmpty()) {
                        return true;
                    }
                    // Assumption: properties are simple KV Properties
                    String refProp = candidateProperties.get(entry.getKey());
                    if (val.equalsIgnoreCase("*")) {
                        // if the detector defines a wildcard, the property must be set in the candidate
                        return !refProp.isEmpty();
                    } else {
                        // if the detector defines a specific value, the candidate's property must match
                        return val.equalsIgnoreCase(refProp);
                    }
                });
        }

        return propertiesCompatible;
    }
}
