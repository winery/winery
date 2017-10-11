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
import org.eclipse.winery.bpel2bpmn.model.gen.TProcess;
import org.eclipse.winery.bpel2bpmn.model.gen.TScope;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ScopeFilterTest {
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();
    private ScopeFilter scopeFilter;

    @Before
    public void before() {
        scopeFilter = new ScopeFilter();
    }

    /**
     * Test if scopename does matches
     */
    @Test
    public void test_Successful() throws Exception {
        TScope tScope = OBJECT_FACTORY.createTScope();
        tScope.setName("Test_scope");
        boolean test = scopeFilter.test(tScope);
        assertTrue(test);
    }

    /**
     * Test when we set a wrong instance
     */
    @Test
    public void test_wrongInstance() throws Exception {
        TProcess tProcess = OBJECT_FACTORY.createTProcess();
        boolean test = scopeFilter.test(tProcess);
        assertFalse(test);
    }

    /**
     * Test if scopename does not matches
     */
    @Test
    public void test_patternNotMatching() throws Exception {
        TProcess tProcess = OBJECT_FACTORY.createTProcess();
        tProcess.setName("Test_SCOPE");
        boolean test = scopeFilter.test(tProcess);
        assertFalse(test);
    }
}
