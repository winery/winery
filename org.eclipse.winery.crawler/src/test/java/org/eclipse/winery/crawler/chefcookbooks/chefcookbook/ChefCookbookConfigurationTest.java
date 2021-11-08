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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChefCookbookConfigurationTest {

    private ChefCookbookConfiguration cookbookConfiguration = new ChefCookbookConfiguration();

    @BeforeAll
    public void beforeAll() {
        cookbookConfiguration.setName("java");
    }

    @Test
    public void getName() {
        assertEquals("java", cookbookConfiguration.getName());
    }

    @Test
    public void setName() {
        cookbookConfiguration.setName("openssh");
        assertEquals("openssh", cookbookConfiguration.getName());
    }

    @Test
    public void getSupports() {
        cookbookConfiguration.setSupports(new Platform("ubuntu"));
        assertEquals("ubuntu", cookbookConfiguration.getSupports().getName());
        assertNull(cookbookConfiguration.getSupports().getVersion());
    }

    @Test
    public void setSupports() {
        Platform platform = new Platform("ubuntu", "18.04");
        cookbookConfiguration.setSupports(platform);
        assertEquals("ubuntu", cookbookConfiguration.getSupports().getName());
        assertEquals("18.04", cookbookConfiguration.getSupports().getVersion());
        assertEquals(platform, cookbookConfiguration.getSupports());
        assertTrue(cookbookConfiguration.hasPlatform(new HashSet<>(Arrays.asList("ubuntu", "windows"))));
        assertTrue(cookbookConfiguration.hasPlatformFamily(new HashSet<>(Arrays.asList("debian", "windows"))));
    }

    @Test
    public void addDepends() {
        cookbookConfiguration.addDepends("java", ">=6");
        assertEquals(">=6", cookbookConfiguration.getDepends().get("java"));
    }

    @Test
    public void addInstalledPackage() {
        ChefPackage installedPackage = new ChefPackage("java", "8");
        cookbookConfiguration.addInstalledPackage(installedPackage);
        assertEquals(installedPackage, cookbookConfiguration.getInstalledPackages().get("java"));
    }

    @Test
    public void addRequiredPackage() {
        ChefPackage requiredPackage = new ChefPackage("java", "8");
        cookbookConfiguration.addRequiredPackage(requiredPackage);
        assertEquals(requiredPackage, cookbookConfiguration.getRequiredPackages().get("java"));
    }

    @Test
    public void getAttribute() {
        String attributeValue = "openssh-client";
        List<String> expectedValues = new ArrayList<>(Arrays.asList(attributeValue));
        cookbookConfiguration.putAttribute("[openssh][package_name]", Collections.singletonList(attributeValue));
        assertEquals(expectedValues, cookbookConfiguration.getAttribute("[openssh][package_name]"));
        assertNull(cookbookConfiguration.getAttribute("thiskeydoesnoetexist"));
    }
}
