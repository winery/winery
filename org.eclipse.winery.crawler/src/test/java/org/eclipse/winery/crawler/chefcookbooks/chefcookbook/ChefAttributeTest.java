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

package org.eclipse.winery.crawler.chefcookbooks.chefcookbook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChefAttributeTest {
    private ChefAttribute attribute;

    @BeforeEach
    public void setUp() {
        attribute = new ChefAttribute("nums", "1");
    }

    @Test
    public void addAttributeList() {
        List<String> expectedValues = new ArrayList<>(Arrays.asList("1", "2", "3"));
        ArrayList<String> addValues = new ArrayList<>(Arrays.asList("2", "3"));
        attribute.addAttribute(addValues);
        assertEquals(expectedValues, attribute.getValues());
    }

    @Test
    public void getName() {
        assertEquals("nums", attribute.getName());
    }

    @Test
    public void getValues() {
        List<String> expectedValues = new ArrayList<>(Arrays.asList("1"));
        assertEquals(expectedValues, attribute.getValues());
    }

    @Test
    public void isArray() {
        assertFalse(attribute.isArray());
        attribute.addAttribute("2");
        assertTrue(attribute.isArray());
    }
}
