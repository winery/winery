/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.tosca.utils;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

public class ModelUtilitiesTest {

    @Test
    public void patchAnyItem_InvalidXML_ThrowsException() {
        Assertions.assertThrows(
            IOException.class,
            () -> ModelUtilities.patchAnyItem("nonsense")
        );
    }

    @Test
    public void patchAnyItem_NonString_IsPassedThrough() throws IOException {
        Object item = new Object();
        Object result = ModelUtilities.patchAnyItem(item);

        Assertions.assertSame(item, result);
    }

    @Test
    public void patchAnyItem_Null_ReturnsNull() throws IOException {
        Assertions.assertNull(ModelUtilities.patchAnyItem(null));
    }

    @Test
    public void patchAnyItem_ValidXML_ReturnsElement() throws IOException {
        Object result = ModelUtilities.patchAnyItem("<root><value>item</value></root>");

        Assertions.assertTrue(result instanceof Element);
    }
}
