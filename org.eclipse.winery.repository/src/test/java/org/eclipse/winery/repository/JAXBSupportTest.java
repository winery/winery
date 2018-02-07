/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository;

import org.junit.Assert;
import org.junit.Test;

public class JAXBSupportTest {

    @Test
    public void createMarshaller() throws Exception {
        Assert.assertNotNull(JAXBSupport.createMarshaller(true));
    }

    @Test
    public void createUnmarshaller() throws Exception {
        Assert.assertNotNull(JAXBSupport.createUnmarshaller());
    }

    @Test
    public void JAXBContextIsNotNull() {
        Assert.assertNotNull(JAXBSupport.context);
    }

}
