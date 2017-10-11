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

import static org.junit.Assert.assertEquals;

public class TypeMapperTest {
    /**
     * Tests if the instance can be mapped
     */
    @Test
    public void apply() throws Exception {
        Object test2 = new String("Test");
        TypeMapper<Object, String> typeMapper = new TypeMapper<>();
        String test = typeMapper.apply(test2);
        assertEquals(test, "Test");
    }

}
