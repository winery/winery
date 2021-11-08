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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlatformTest {

    @Test
    public void getName() {
        Platform platform = new Platform("windows");
        assertEquals("windows", platform.getName());
    }

    @Test
    public void setName() {
        Platform platform = new Platform("windows");
        platform.setName("ubuntu");
        assertEquals("ubuntu", platform.getName());
    }

    @Test
    public void getVersion() {
        Platform platform = new Platform("ubuntu", "18.04");
        assertEquals("18.04", platform.getVersion());
    }

    @Test
    public void setVersion() {
        Platform platform = new Platform("ubuntu", "18.04");
        platform.setVersion("16.04");
        assertEquals("16.04", platform.getVersion());
    }
}
