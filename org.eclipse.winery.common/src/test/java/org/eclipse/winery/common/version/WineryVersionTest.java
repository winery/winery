/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
 ********************************************************************************/
package org.eclipse.winery.common.version;

import io.github.adr.embedded.ADR;
import org.junit.Assert;
import org.junit.Test;

public class WineryVersionTest {
    @Test
    public void testCompareToSmallerComponentVersion() {
        WineryVersion smaller = new WineryVersion("1.0.0", 1, 1);
        WineryVersion greater = new WineryVersion("1.0.1", 1, 1);
        Assert.assertEquals(1, greater.compareTo(smaller));
    }

    @Test
    public void testCompareToSmallerWineryVersion() {
        WineryVersion smaller = new WineryVersion("1.0.0", 1, 1);
        WineryVersion greater = new WineryVersion("1.0.0", 2, 1);
        Assert.assertEquals(1, greater.compareTo(smaller));
    }

    @Test
    public void testCompareToSmallerWipVersion() {
        WineryVersion smaller = new WineryVersion("1.0.0", 1, 1);
        WineryVersion greater = new WineryVersion("1.0.0", 1, 5);
        Assert.assertEquals(1, greater.compareTo(smaller));
    }

    @Test
    public void testCompareToGreaterComponentVersion() {
        WineryVersion smaller = new WineryVersion("1.0.0", 7, 1);
        WineryVersion greater = new WineryVersion("1.0.1", 1, 6);
        Assert.assertEquals(-1, smaller.compareTo(greater));
    }

    @Test
    public void testCompareToGreaterWineryVersion() {
        WineryVersion smaller = new WineryVersion("1.0.0", 1, 1);
        WineryVersion greater = new WineryVersion("1.0.0", 9, 5);
        Assert.assertEquals(-1, smaller.compareTo(greater));
    }

    @Test
    public void testCompareToGreaterWipVersion() {
        WineryVersion smaller = new WineryVersion("1.0.0", 1, 1);
        WineryVersion greater = new WineryVersion("1.0.0", 6, 6);
        Assert.assertEquals(-1, smaller.compareTo(greater));
    }

    @Test
    public void testCompareToEqualVersion() {
        WineryVersion smaller = new WineryVersion("1.6.3", 4, 8);
        WineryVersion greater = new WineryVersion("1.6.3", 4, 8);
        Assert.assertEquals(0, smaller.compareTo(greater));
    }

    @Test
    public void testCompareToWipVersionShouldBeLessThanActualWineryRelease() {
        WineryVersion smaller = new WineryVersion("1.0.0", 6, 6);
        WineryVersion greater = new WineryVersion("1.0.0", 6, 0);
        Assert.assertEquals(-1, smaller.compareTo(greater));
        Assert.assertEquals(1, greater.compareTo(smaller));
    }

    @Test
    public void testToString() {
        String componentVersion = "789.67.79823";
        int wineryVersion = 2347;
        int wipVersion = 3673;
        WineryVersion v = new WineryVersion(componentVersion, wineryVersion, wipVersion);

        String expected = componentVersion
            + WineryVersion.WINERY_VERSION_SEPARATOR + WineryVersion.WINERY_VERSION_PREFIX + wineryVersion
            + WineryVersion.WINERY_VERSION_SEPARATOR + WineryVersion.WINERY_WIP_VERSION_PREFIX + wipVersion;

        Assert.assertEquals(expected, v.toString());
    }

    @Test
    public void testToStringWithoutWipVersion() {
        String componentVersion = "8.0.6-alpha";
        int wineryVersion = 1;
        int wipVersion = 0;
        WineryVersion v = new WineryVersion(componentVersion, wineryVersion, wipVersion);

        String expected = componentVersion
            + WineryVersion.WINERY_VERSION_SEPARATOR + WineryVersion.WINERY_VERSION_PREFIX + wineryVersion;

        Assert.assertEquals(expected, v.toString());
    }

    @Test
    public void testToStringWithoutWineryVersion() {
        String componentVersion = "funnyVersionIdentifier";
        int wineryVersion = 0;
        int wipVersion = 0;
        WineryVersion v = new WineryVersion(componentVersion, wineryVersion, wipVersion);

        String expected = componentVersion;

        Assert.assertEquals(expected, v.toString());
    }

    @Test
    public void testToStringWithoutComponentButWineryVersion() {
        String componentVersion = "";
        int wineryVersion = 6;
        int wipVersion = 0;
        WineryVersion v = new WineryVersion(componentVersion, wineryVersion, wipVersion);

        String expected = componentVersion
            + WineryVersion.WINERY_VERSION_SEPARATOR + WineryVersion.WINERY_VERSION_PREFIX + wineryVersion;

        Assert.assertEquals(expected, v.toString());
    }
}
