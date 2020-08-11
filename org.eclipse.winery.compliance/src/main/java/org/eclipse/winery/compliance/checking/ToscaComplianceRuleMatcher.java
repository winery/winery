/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.compliance.checking;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.topologygraph.matching.IToscaMatcher;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaEntity;
import org.eclipse.winery.topologygraph.model.ToscaNode;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TPolicy;

import io.github.adr.embedded.ADR;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;

public class ToscaComplianceRuleMatcher implements IToscaMatcher {

    @Override
    public boolean isCompatible(ToscaNode left, ToscaNode right) {
        //match by NodeType
        if (!isTEntityTypesCompatible(left, right)) {
            return false;
        }

        //match by Properties -> left.properties must be subset of right.properties
        if (!isPropertiesCompatible(left, right)) return false;

        // match by Policies -> left.policies must be subset of right.policies
        return isPoliciesCompatible(left, right);
    }

    public boolean isPoliciesCompatible(ToscaNode left, ToscaNode right) {
        if (left.getTemplate().getPolicies() != null) {
            if (right.getTemplate().getPolicies() != null) {
                return mapToStringList(right.getTemplate().getPolicies().getPolicy()).containsAll(mapToStringList(left.getTemplate().getPolicies().getPolicy()));
            } else {
                return false;
            }
        }
        return true;
    }

    private List<String> mapToStringList(@NonNull List<TPolicy> policy) {
        return policy.stream().map(p -> p.getPolicyType().toString() + p.getPolicyRef().toString()).collect(Collectors.toList());
    }

    public boolean isPropertiesCompatible(ToscaNode left, ToscaNode right) {
        TEntityTemplate.Properties leftProps = left.getTemplate().getProperties();
        TEntityTemplate.Properties rightProps = right.getTemplate().getProperties();
        if (leftProps == null) {
            // no constraints on the properties, so all rightProps are valid
            return true;
        }
        if (rightProps == null) {
            // there are property requirements, so right must have a value
            return false;
        }
        // property types are different
        if (leftProps.getClass() != rightProps.getClass()) {
            return false;
        }
        if (leftProps instanceof TEntityTemplate.WineryKVProperties) {
            assert (rightProps instanceof TEntityTemplate.WineryKVProperties);

            Map<String, String> leftMap = ((TEntityTemplate.WineryKVProperties) leftProps).getKVProperties();
            Map<String, String> rightMap = ((TEntityTemplate.WineryKVProperties) rightProps).getKVProperties();

            for (Entry<String, String> leftEntry : leftMap.entrySet()) {
                if (!isPropertyCompatible(leftEntry, rightMap)) {
                    return false;
                }
            }
            return true;
        } else if (leftProps instanceof TEntityTemplate.XmlProperties) {
            assert (rightProps instanceof TEntityTemplate.XmlProperties);

            return ((TEntityTemplate.XmlProperties) leftProps).getAny().equals(((TEntityTemplate.XmlProperties) rightProps).getAny());
        } else {
            // There's no ComplianceRules in YAML mode
            return false;
        }
    }

    public boolean isPropertyCompatible(Entry<String, String> leftEntry, @ADR(12) Map<String, String> rightProperties) {
        return rightProperties.containsKey(leftEntry.getKey()) &&
            rightProperties.get(leftEntry.getKey()) != null &&
            isPropertyValueCompatible(leftEntry.getValue(), rightProperties.get(leftEntry.getKey()));
    }

    private boolean isPropertyValueCompatible(Object leftValue, Object rightValue) {
        if (leftValue != null) {
            if (rightValue != null) {
                //compareRegex
                return rightValue.toString().matches(leftValue.toString());
            } else {
                return false;
            }
        }
        return false;
    }

    public boolean isLeftSubtypeOfRight(ToscaEntity left, ToscaEntity right) {
        return left.getTypes().stream().anyMatch(lType -> equals(lType, right.getActualType()));
    }

    public boolean isTEntityTypesCompatible(ToscaEntity left, ToscaEntity right) {
        return isLeftSubtypeOfRight(left, right) || isLeftSubtypeOfRight(right, left);
    }

    public boolean equals(TEntityType lType, TEntityType rType) {
        return StringUtils.equals(lType.getIdFromIdOrNameField(), rType.getIdFromIdOrNameField())
            && StringUtils.equals(lType.getTargetNamespace(), rType.getTargetNamespace());
    }

    @Override
    public boolean isCompatible(ToscaEdge left, ToscaEdge right) {
        //match by RelationshipType
        return isTEntityTypesCompatible(left, right);
    }
}
