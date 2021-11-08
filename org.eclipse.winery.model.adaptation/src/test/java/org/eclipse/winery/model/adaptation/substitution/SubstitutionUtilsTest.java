/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.adaptation.substitution;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubstitutionUtilsTest {

    private final static String GRANDFATHER = "grandfather";
    private final static String PARENT = "parent";
    private final static String MOTHER = "mother";
    private final static String AUNT = "aunt";
    private final static String CHILD = "child";
    private final static String GRANDCHILD = "grandchild";
    private final static String STRANGER = "stranger";

    private static Map<QName, TNodeType> nodeTypes;

    private static QName grandFather = new QName("https://example.org/tosca/substiution", GRANDFATHER);
    private static QName parent = new QName("https://example.org/tosca/substiution", PARENT);
    private static QName child = new QName("https://example.org/tosca/substiution", CHILD);
    private static QName grandChild = new QName("https://example.org/tosca/substiution", GRANDCHILD);

    private static QName mother = new QName("https://example.org/tosca/substiution", MOTHER);
    private static QName aunt = new QName("https://my.example.org/tosca/substiution", AUNT);
    private static QName stranger = new QName("ftp://example.org/stranger", STRANGER);

    @BeforeEach
    void setUp() {
        nodeTypes = new HashMap<>();

        nodeTypes.put(
            grandFather,
            new TNodeType.Builder(GRANDFATHER)
                .setAbstract(true)
                .build()
        );
        nodeTypes.put(
            parent,
            new TNodeType.Builder(PARENT)
                .setDerivedFrom(grandFather)
                .setAbstract(true)
                .build()
        );
        nodeTypes.put(
            stranger,
            new TNodeType.Builder(STRANGER)
                .build()
        );
        nodeTypes.put(
            child,
            new TNodeType.Builder(CHILD)
                .setDerivedFrom(parent)
                .setAbstract(true)
                .build()

        );
        nodeTypes.put(
            mother,
            new TNodeType.Builder(MOTHER)
                .setDerivedFrom(grandFather)
                .build()
        );
        nodeTypes.put(
            grandChild,
            new TNodeType.Builder(GRANDCHILD)
                .setDerivedFrom(child)
                .build()
        );
        nodeTypes.put(
            aunt,
            new TNodeType.Builder(AUNT)
                .setDerivedFrom(grandFather)
                .build()
        );
    }

    // region ########## collectTypeHierarchy ##########
    @Test
    public void retrieveTypeHierarchy() {
        Optional<List<Subtypes<TNodeType>>> tNodeTypeSubtypes = SubstitutionUtils.collectTypeHierarchy(nodeTypes, grandFather);

        assertTrue(tNodeTypeSubtypes.isPresent());

        List<Subtypes<TNodeType>> subtypes = tNodeTypeSubtypes.get();
        assertEquals(3, subtypes.size());

        assertTrue(subtypes.removeIf(nodeType -> PARENT.equals(nodeType.getElement().getName())));
        assertTrue(subtypes.removeIf(nodeType -> MOTHER.equals(nodeType.getElement().getName())));
        assertTrue(subtypes.removeIf(nodeType -> AUNT.equals(nodeType.getElement().getName())));
    }

    @Test
    public void retrieveTypeHierarchy2() {
        Optional<List<Subtypes<TNodeType>>> tNodeTypeSubtypes = SubstitutionUtils.collectTypeHierarchy(nodeTypes, parent);

        assertTrue(tNodeTypeSubtypes.isPresent());

        List<Subtypes<TNodeType>> subtypes = tNodeTypeSubtypes.get();
        assertEquals(1, subtypes.size());

        Subtypes<TNodeType> firstChild = subtypes.get(0);
        assertEquals(CHILD, firstChild.getElement().getName());
        assertEquals(1, firstChild.getChildren().size());

        Subtypes<TNodeType> childOfFirstChild = firstChild.getChildren().get(0);
        assertEquals(GRANDCHILD, childOfFirstChild.getElement().getName());
        assertNull(childOfFirstChild.getChildren());
    }

    @ParameterizedTest(name = "{index} => ''{1}''")
    @MethodSource("getEmptyTypeHierarchyArguments")
    void retrieveEmptyTypeHierarchy(QName qName, String description) {
        assertEquals(Optional.empty(), SubstitutionUtils.collectTypeHierarchy(nodeTypes, null));
    }

    private static Stream<Arguments> getEmptyTypeHierarchyArguments() {
        return Stream.of(
            Arguments.of(null, "expected empty hierarchy on null"),
            Arguments.of(stranger, "expected empty hierarchy for the stranger"),
            Arguments.of(grandChild, "expected empty hierarchy for the grandchild")
        );
    }
    // endregion

    // region ########## collectSubstitutableTemplates ##########

    @Test
    public void getSubstitutableTemplateMap() {
        List<TNodeTemplate> templates = Arrays.asList(
            new TNodeTemplate.Builder("id0", stranger).build(),
            new TNodeTemplate.Builder("id1", parent).build(),
            new TNodeTemplate.Builder("id2", grandFather).build(),
            new TNodeTemplate.Builder("id3", aunt).build()
        );

        Map<TNodeTemplate, List<Subtypes<TNodeType>>> substitutableTemplates = SubstitutionUtils.collectSubstitutableTemplates(templates, nodeTypes);

        assertEquals(2, substitutableTemplates.size());
        assertTrue(substitutableTemplates.entrySet()
            .removeIf(entry -> grandFather.equals(entry.getKey().getTypeAsQName()))
        );
        assertTrue(substitutableTemplates.entrySet()
            .removeIf(entry -> parent.equals(entry.getKey().getType()))
        );
    }

    // endregion
}
