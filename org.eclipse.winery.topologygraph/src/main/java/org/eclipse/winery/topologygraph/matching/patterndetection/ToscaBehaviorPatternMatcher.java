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

package org.eclipse.winery.topologygraph.matching.patterndetection;

import java.util.Map;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.extensions.OTPatternRefinementModel;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.NamespaceManager;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaEntity;
import org.eclipse.winery.topologygraph.model.ToscaNode;

public class ToscaBehaviorPatternMatcher extends ToscaPatternMatcher {

    public ToscaBehaviorPatternMatcher(OTPatternRefinementModel prm, NamespaceManager namespaceManager) {
        super(prm, namespaceManager);
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

    @Override
    public boolean propertiesCompatible(ToscaEntity left, ToscaEntity right) {
        // By convention, the left node is always the element to search in right.
        TEntityTemplate detectorElement = left.getTemplate();
        TEntityTemplate candidateElement = right.getTemplate();
        return propertiesCompatible(detectorElement, candidateElement);
    }

    protected boolean propertiesCompatible(TEntityTemplate detectorElement, TEntityTemplate candidateElement) {
        boolean compatible = true;
        if (ModelUtilities.hasKvProperties(detectorElement) && ModelUtilities.hasKvProperties(candidateElement)) {
            Map<String, String> detectorProps = ModelUtilities.getPropertiesKV(detectorElement);
            Map<String, String> candidateProps = ModelUtilities.getPropertiesKV(candidateElement);

            compatible = detectorProps.entrySet().stream()
                .allMatch(detectorProp -> existsBehaviorPatternMapping(detectorElement, detectorProp.getKey())
                    || propertyValuesCompatible(detectorProp.getValue(), candidateProps.get(detectorProp.getKey()))
                );
        }
        return compatible;
    }

    private boolean existsBehaviorPatternMapping(TEntityTemplate detectorElement, String detectorPropKey) {
        return prm.getBehaviorPatternMappings() != null
            && prm.getBehaviorPatternMappings().stream()
            .anyMatch(bpm -> bpm.getDetectorElement().getId().equals(detectorElement.getId())
                && bpm.getProperty().getKey().equals(detectorPropKey));
    }

    private boolean propertyValuesCompatible(String detectorPropValue, String candidatePropValue) {
        return (detectorPropValue == null || detectorPropValue.isEmpty())
            || detectorPropValue.equalsIgnoreCase(candidatePropValue)
            || (detectorPropValue.equals("*") && (candidatePropValue != null && !candidatePropValue.isEmpty()));
    }
}
