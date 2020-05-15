/*******************************************************************************
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
package org.eclipse.winery.topologygraph.matching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicies;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.NamespaceManager;
import org.eclipse.winery.topologygraph.model.ToscaNode;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ToscaPrmPropertyMatcherTest {

    private static Stream<Arguments> compatiblePropertiesArguments() {
        Map<String, String> allInOneLeftProperties = new HashMap<>();
        allInOneLeftProperties.put("key0", null);
        allInOneLeftProperties.put("key1", "*");
        allInOneLeftProperties.put("key2", "");
        allInOneLeftProperties.put("key3", "special");
        Map<String, String> allInOneRightProperties = new HashMap<>();
        allInOneRightProperties.put("key0", "");
        allInOneRightProperties.put("key1", "I must be set");
        allInOneRightProperties.put("key2", "I can have anything");
        allInOneRightProperties.put("key3", "special");

        Map<String, String> mustBeSetLeftProperties = new HashMap<>();
        mustBeSetLeftProperties.put("key", "*");
        Map<String, String> mustBeSetRightProperties = new HashMap<>();
        mustBeSetRightProperties.put("key", "isSet");
        Map<String, String> mustBeSetButIsNotRightProperties = new HashMap<>();
        mustBeSetButIsNotRightProperties.put("key", "");

        Map<String, String> mustBeEqualsLeftProperties = new HashMap<>();
        mustBeEqualsLeftProperties.put("key", "must be equals");
        Map<String, String> mustBeEqualsRightProperties = new HashMap<>();
        mustBeEqualsRightProperties.put("key", "must Be equals");
        Map<String, String> mustBeEqualsButIsNotRightProperties = new HashMap<>();
        mustBeEqualsButIsNotRightProperties.put("key", "who cares?");

        return Stream.of(
            Arguments.of(allInOneLeftProperties, allInOneRightProperties, true, "All in one"),
            Arguments.of(mustBeSetLeftProperties, mustBeSetRightProperties, true, "Must be set"),
            Arguments.of(mustBeSetLeftProperties, mustBeSetButIsNotRightProperties, false, "Must be set but is not"),
            Arguments.of(mustBeEqualsLeftProperties, mustBeEqualsRightProperties, true, "Must be equals"),
            Arguments.of(mustBeEqualsLeftProperties, mustBeEqualsButIsNotRightProperties, false, "Must be equals but is not"),
            Arguments.of(null, null, true, "If there are no properties, they must still match!")
        );
    }

    @ParameterizedTest(name = "{index} => ''{3}''")
    @MethodSource("compatiblePropertiesArguments")
    public void compatibleProperties(LinkedHashMap<String, String> leftProperties, LinkedHashMap<String, String> rightProperties, boolean expected, String description) {
        // region ***** left *****
        TNodeTemplate left = new TNodeTemplate();
        if (Objects.nonNull(leftProperties)) {
            ModelUtilities.setPropertiesKV(left, leftProperties);
        }

        ToscaNode leftEntity = new ToscaNode();
        leftEntity.setNodeTemplate(left);

        List<TEntityTemplate> detectorElements = new ArrayList<>();
        detectorElements.add(left);
        // endregion

        // region ***** right *****
        TNodeTemplate right = new TNodeTemplate();
        if (Objects.nonNull(leftProperties)) {
            ModelUtilities.setPropertiesKV(right, rightProperties);
        }

        ToscaNode rightEntity = new ToscaNode();
        rightEntity.setNodeTemplate(right);
        // endregion

        assertEquals(
            expected,
            new ToscaPrmPropertyMatcher(detectorElements, new MockNamespaceManager())
                .propertiesCompatible(leftEntity, rightEntity)
        );
    }

    private static Stream<Arguments> characterizingPatternsCompatibleArguments() {
        NamespaceManager namespaceManager = new MockNamespaceManager();
        NamespaceManager patternNamespaceManager = new MockNamespaceManager() {
            @Override
            public boolean isPatternNamespace(String namespace) {
                return true;
            }
        };

        TPolicies leftPolicies1 = new TPolicies();
        TPolicy leftPolicy1 = new TPolicy();
        leftPolicy1.setPolicyType(QName.valueOf("{ns}policyType1"));
        leftPolicies1.getPolicy().add(leftPolicy1);

        TPolicies leftPolicies2 = new TPolicies();
        TPolicy leftPolicy2 = new TPolicy();
        leftPolicy2.setPolicyType(QName.valueOf("{ns}policyType1123"));
        leftPolicies2.getPolicy().add(leftPolicy2);

        TPolicies rightPolicies1 = new TPolicies();
        TPolicy rightPolicy1 = new TPolicy();
        rightPolicy1.setPolicyType(QName.valueOf("{ns}policyType1"));
        rightPolicies1.getPolicy().add(rightPolicy1);

        TPolicies rightPolicies2 = new TPolicies();
        TPolicy rightPolicy2 = new TPolicy();
        rightPolicy2.setPolicyType(QName.valueOf("{ns}policyType1"));
        rightPolicy2.setPolicyRef(QName.valueOf("{ns2}policyTemplate1"));
        rightPolicies2.getPolicy().add(rightPolicy2);

        return Stream.of(
            Arguments.of(leftPolicies1, rightPolicies1, patternNamespaceManager, true, "Matching policy types without templates"),
            Arguments.of(leftPolicies1, rightPolicies2, patternNamespaceManager, true, "Matching policy types and more specific policy template in the candidate"),
            Arguments.of(rightPolicies2, leftPolicies1, patternNamespaceManager, false, "Matching policy types but a specific policy template in the detector"),
            Arguments.of(leftPolicies2, rightPolicies1, patternNamespaceManager, false, "Different policy types"),
            Arguments.of(leftPolicies2, null, patternNamespaceManager, false, "Patterns annotated at the detector but not at the candidate"),
            Arguments.of(null, rightPolicies1, patternNamespaceManager, true, "Patterns annotated at the candidate but not at the detector"),
            Arguments.of(null, rightPolicies1, namespaceManager, true, "Polices annotated at the candidate but not patterns")
        );
    }

    @ParameterizedTest(name = "{index} => ''{4}''")
    @MethodSource("characterizingPatternsCompatibleArguments")
    public void characterizingPatternsCompatibleTest(TPolicies leftPolicies, TPolicies rightPolicies,
                                                     NamespaceManager namespaceManager, boolean expected, String description) {
        // region ***** left *****
        TNodeTemplate left = new TNodeTemplate();
        if (Objects.nonNull(leftPolicies)) {
            left.setPolicies(leftPolicies);
        }

        ToscaNode leftEntity = new ToscaNode();
        leftEntity.setNodeTemplate(left);

        List<TEntityTemplate> detectorElements = new ArrayList<>();
        detectorElements.add(left);
        // endregion

        // region ***** right *****
        TNodeTemplate right = new TNodeTemplate();
        if (Objects.nonNull(rightPolicies)) {
            right.setPolicies(rightPolicies);
        }

        ToscaNode rightEntity = new ToscaNode();
        rightEntity.setNodeTemplate(right);
        // endregion

        assertEquals(
            expected,
            new ToscaPrmPropertyMatcher(detectorElements, namespaceManager)
                .characterizingPatternsCompatible(leftEntity, rightEntity)
        );
    }
}
