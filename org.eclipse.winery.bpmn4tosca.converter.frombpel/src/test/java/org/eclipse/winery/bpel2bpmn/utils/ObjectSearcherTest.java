/*
 * *****************************************************************************
 * Copyright (c) 2015-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Alex Frank - initial API and implementation
 * *****************************************************************************
 *
 */

package org.eclipse.winery.bpel2bpmn.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class ObjectSearcherTest {

    /**
     * Finds the first object
     */
    @Test
    public void findFirst() throws Exception {
        List<Object> objects = Arrays.asList(new String("first"), new Integer(1), new String("last"));
        Optional<String> firstString = ObjectSearcher.findFirst(objects, String.class);
        assertTrue(firstString.isPresent());
        assertEquals(firstString.get(), "first");
    }

    /**
     * Does not find any object
     */
    @Test
    public void doNotFindFirst() throws Exception {
        List<Object> objects = Arrays.asList(new String("first"), new Integer(1), new String("last"));
        Optional<ArrayList> firstString = ObjectSearcher.findFirst(objects, ArrayList.class);
        assertFalse(firstString.isPresent());
    }

    /**
     * Find any objects
     */
    @Test
    public void findAny() throws Exception {
        List<Object> objects = Arrays.asList(new String("first"), new Integer(1), new String("last"));
        List<String> strings = ObjectSearcher.findAny(objects, String.class);
        assertEquals(strings.size(), 2);
        assertArrayEquals(strings.toArray(new String[2]), new String[]{"first", "last"});
    }

    /**
     * Does not find any objects
     */
    @Test
    public void doNotFindAny() throws Exception {
        List<Object> objects = Arrays.asList(new String("first"), new Integer(1), new String("last"));
        List<ArrayList> lists = ObjectSearcher.findAny(objects, ArrayList.class);
        assertTrue(lists.isEmpty());
    }
}
