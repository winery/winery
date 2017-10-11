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

import java.util.Optional;

import static org.junit.Assert.*;

public class NullCheckerTest {
    /**
     * Checks not null
     */
    @Test
    public void checkNotNull() throws Exception {
        String notNull = "notNull";
        Optional<String> check = NullChecker.check(notNull);
        boolean present = check.isPresent();
        assertTrue(present);
        assertEquals(check.get(), notNull);
    }

    /**
     * Checks if null
     */
    @Test
    public void checkNull() throws Exception {
        String isNull = null;
        boolean present = NullChecker.check(isNull).isPresent();
        assertFalse(present);
    }


    /**
     * Checks if an Exception if thrown
     */
    @Test(expected = NullPointerException.class)
    public void orThrow() throws Exception {
        Object nullObject = null;
        NullChecker.orThrow(nullObject, new NullPointerException());
        fail();
    }

    /**
     * Checks if an Exception is not thrown
     */
    @Test
    public void orNotThrow() throws Exception {
        Object notNullObject = new Object();
        boolean present = NullChecker.orThrow(notNullObject, new NullPointerException()).isPresent();
        assertTrue(present);
    }

}
