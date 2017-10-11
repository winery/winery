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

import org.eclipse.winery.bpel2bpmn.model.gen.ObjectFactory;
import org.eclipse.winery.bpel2bpmn.model.gen.TCopy;
import org.eclipse.winery.bpel2bpmn.model.gen.TTo;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ParameterFilterTest {
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();
    private ParameterFilter parameterFilter;

    @Before
    public void before() {
        parameterFilter = new ParameterFilter();
    }


    /**
     * Checks if {@link TCopy} is found
     */
    @Test
    public void test_success() throws Exception {
        TCopy copy = this.createTestTCopy("prop_TestScope_Host");
        boolean test = parameterFilter.test(copy);
        assertTrue(test);
    }

    /**
     * Test wrong class
     */
    @Test
    public void test_wrongInstance() throws Exception {
        boolean test = parameterFilter.test(OBJECT_FACTORY.createTActivity());
        assertFalse(test);
    }

    /**
     * Test if pattern does not match
     */
    @Test
    public void test_patternNotMatching() throws Exception {
        TCopy tCopy = this.createTestTCopy("prop_testScope_Host");
        parameterFilter.test(tCopy);
    }

    /**
     * Creates a simple tcopy with a variable
     *
     * @param variable The variable
     * @return The {@link TCopy}
     */
    private TCopy createTestTCopy(final String variable) {
        TTo tTo = OBJECT_FACTORY.createTTo();
        tTo.setVariable(variable);
        TCopy tCopy = OBJECT_FACTORY.createTCopy();
        tCopy.setTo(tTo);
        return tCopy;
    }

}
