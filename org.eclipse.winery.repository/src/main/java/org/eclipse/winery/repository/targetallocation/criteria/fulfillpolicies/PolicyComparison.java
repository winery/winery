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
 ********************************************************************************/

package org.eclipse.winery.repository.targetallocation.criteria.fulfillpolicies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.targetallocation.util.AllocationUtils;

/**
 * Compares Policy Template with the Policy Templates of given fragments by the defined operators.
 * Fragments are assumed to be PaaS, i.e. only contain one Node Template.
 */
public class PolicyComparison {

    private List<PolicyWrapper> policies;
    private List<TTopologyTemplate> fragments;
    private List<FragmentWithPolicies> fragmentsWithPolicies = new ArrayList<>();

    public PolicyComparison(List<PolicyWrapper> policies, List<TTopologyTemplate> fragments) {
        this.policies = policies;
        this.fragments = fragments;

        // extract policies from fragments
        for (TTopologyTemplate fragment : fragments) {
            List<TPolicyTemplate> policyTemplates =
                AllocationUtils.getPolicyTemplates(fragment.getNodeTemplates().get(0));
            List<PolicyWrapper> policyWrappers = new ArrayList<>();

            for (TPolicyTemplate policyTemplate : policyTemplates) {
                for (PolicyWrapper policyWrapper : policies) {
                    if (policyTemplate.getType().equals(policyWrapper.getPolicy().getType())) {
                        policyWrappers.add(new PolicyWrapper(policyTemplate, policyWrapper.getPropertyKey()));
                    }
                }
            }
            fragmentsWithPolicies.add(new FragmentWithPolicies(fragment, policyWrappers));
        }
    }

    public PolicyComparison(List<PolicyWrapper> policies, Collection<TNodeTemplate> nodeTemplates) {
        this(policies, toFragments(nodeTemplates));
    }

    private static List<TTopologyTemplate> toFragments(Collection<TNodeTemplate> nodeTemplates) {
        List<TTopologyTemplate> fragments = new ArrayList<>();
        for (TNodeTemplate nodeTemplate : nodeTemplates) {
            TTopologyTemplate topologyTemplate = new TTopologyTemplate();
            topologyTemplate.addNodeTemplate(nodeTemplate);
            fragments.add(topologyTemplate);
        }
        return fragments;
    }

    public List<TTopologyTemplate> getFragmentsFulfillingPolicies() {
        // no policies present -> all fragments fulfilling
        if (policies.isEmpty()) {
            return fragments;
        }

        Map<PolicyWrapper, List<TTopologyTemplate>> fragmentsByFulfilledPolicy = new HashMap<>();
        for (PolicyWrapper policy : policies) {
            List<TTopologyTemplate> fragmentsFulfillingPolicy = getFragmentsFulfilling(policy);
            if (fragmentsFulfillingPolicy.isEmpty()) {
                // no match can be found
                return new ArrayList<>();
            } else {
                fragmentsByFulfilledPolicy.put(policy, fragmentsFulfillingPolicy);
            }
        }

        // get fragments fulfilling all policies
        Iterator<List<TTopologyTemplate>> iterator = fragmentsByFulfilledPolicy.values().iterator();
        List<TTopologyTemplate> intersection = iterator.next();
        while (iterator.hasNext()) {
            intersection.retainAll(iterator.next());
        }
        return intersection;
    }

    private List<TTopologyTemplate> getFragmentsFulfilling(PolicyWrapper policyWrapper) {
        switch (policyWrapper.getOperator()) {
            case "min":
                return getMinMax(policyWrapper, ">");
            case "max":
                return getMinMax(policyWrapper, "<");
            case "approx":
                return getApprox(policyWrapper);
            default:
                return getOtherOperators(policyWrapper);
        }
    }

    /**
     * Returns all fragments fulfilling the operator <, <=, >, >=, = or !=.
     */
    private List<TTopologyTemplate> getOtherOperators(PolicyWrapper policyWrapper) {
        List<TTopologyTemplate> fragmentsFulfillingPolicy = new ArrayList<>();

        for (FragmentWithPolicies fragmentWithPolicies : fragmentsWithPolicies) {
            for (PolicyWrapper policyOfFragment : fragmentWithPolicies.policies) {
                if (policyOfFragment.equals(policyWrapper)) {
                    Object comparisonValue = policyWrapper.getProperty();
                    String operator = policyWrapper.getOperator();
                    Object value = policyOfFragment.getProperty();

                    if (compare(value, operator, comparisonValue)) {
                        fragmentsFulfillingPolicy.add(fragmentWithPolicies.fragment);
                    }
                    break;
                }
            }
        }
        return fragmentsFulfillingPolicy;
    }

    /**
     * Returns all fragments with the same max/min value.
     */
    private List<TTopologyTemplate> getMinMax(PolicyWrapper policyWrapper, String operator) {
        List<TTopologyTemplate> minOrMaxFragments = new ArrayList<>();
        Object minOrMax = null;

        for (FragmentWithPolicies fragmentWithPolicies : fragmentsWithPolicies) {
            for (PolicyWrapper policyOfFragment : fragmentWithPolicies.policies) {
                if (policyOfFragment.equals(policyWrapper)) {
                    if (minOrMax == null || compare(minOrMax, operator, policyOfFragment.getProperty())) {
                        minOrMaxFragments.clear();
                        minOrMaxFragments.add(fragmentWithPolicies.fragment);
                        minOrMax = policyOfFragment.getProperty();
                    } else if (compare(minOrMax, "=", policyOfFragment.getProperty())) {
                        minOrMaxFragments.add(fragmentWithPolicies.fragment);
                    }
                    break;
                }
            }
        }
        return minOrMaxFragments;
    }

    /**
     * Return fragments with closest value to the specified value.
     * Only numbers supported.
     */
    private List<TTopologyTemplate> getApprox(PolicyWrapper policyWrapper) {
        if (!(policyWrapper.getProperty() instanceof Number)) {
            return new ArrayList<>();
        }
        List<TTopologyTemplate> approxFragments = new ArrayList<>();
        double property = new Double(policyWrapper.getProperty().toString());
        double difference = Double.MAX_VALUE;

        for (FragmentWithPolicies fragmentWithPolicies : fragmentsWithPolicies) {
            for (PolicyWrapper policyOfFragment : fragmentWithPolicies.policies) {
                if (policyOfFragment.equals(policyWrapper)) {
                    double otherProperty = new Double(policyOfFragment.getProperty().toString());
                    double newDifference = Math.abs(otherProperty - property);

                    if (newDifference < difference) {
                        approxFragments.clear();
                        approxFragments.add(fragmentWithPolicies.fragment);
                        difference = newDifference;
                    } else if (newDifference == difference) {
                        approxFragments.add(fragmentWithPolicies.fragment);
                    }
                    break;
                }
            }
        }
        return approxFragments;
    }

    private <T extends Comparable<T>> boolean compare(Object value, String operator, Object comparisonValue) {
        try {
            T valueComparable = (T) value;
            T comparisonValueComparable = (T) comparisonValue;
            int compareResult = valueComparable.compareTo(comparisonValueComparable);

            switch (operator) {
                case ">":
                    return compareResult > 0;
                case ">=":
                    return ((compareResult > 0) || (compareResult == 0));
                case "<":
                    return compareResult < 0;
                case "<=":
                    return ((compareResult < 0) || (compareResult == 0));
                case "=":
                    return compareResult == 0;
                case "!=":
                    return compareResult != 0;
                default:
                    return false;
            }
        } catch (ClassCastException e) {
            return false;
        }
    }

    private static class FragmentWithPolicies {
        private TTopologyTemplate fragment;
        private List<PolicyWrapper> policies;

        private FragmentWithPolicies(TTopologyTemplate fragment, List<PolicyWrapper> policies) {
            this.fragment = fragment;
            this.policies = policies;
        }
    }
}
