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

import org.eclipse.winery.crawler.chefcookbooks.constants.ChefDslConstants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChefPackageTest {

    @Test
    public void getName() {
        ChefPackage installedPackage = new ChefPackage("openssh-client");
        assertEquals("openssh-client", installedPackage.getName());
    }

    @Test
    public void setName() {
        ChefPackage installedPackage = new ChefPackage("openssh-client");
        installedPackage.setName("openssh-server");
        assertEquals("openssh-server", installedPackage.getName());
    }

    @Test
    public void getVersion() {
        ChefPackage installedPackage = new ChefPackage("openssh-client", "7.9");
        assertEquals("7.9", installedPackage.getVersion());
    }

    @Test
    public void setVersion() {
        ChefPackage installedPackage = new ChefPackage("openssh-client");
        installedPackage.setVersion("7.9");
        assertEquals("7.9", installedPackage.getVersion());
    }

    @Test
    public void addProperty() {
        ChefPackage installedPackage = new ChefPackage("openssh-client");
        installedPackage.addProperty(ChefDslConstants.PACKAGE_NAME_PROPERTY, "java");
        installedPackage.addProperty(ChefDslConstants.PACKAGE_VERSION_PROPERTY, "8");
        assertEquals("java", installedPackage.getPackageName());
        assertEquals("8", installedPackage.getVersion());
    }
}
