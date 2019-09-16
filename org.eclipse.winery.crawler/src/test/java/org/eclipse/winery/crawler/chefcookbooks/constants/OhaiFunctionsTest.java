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

package org.eclipse.winery.crawler.chefcookbooks.constants;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OhaiFunctionsTest {

    @ParameterizedTest
    @CsvSource(value = {
        "debian:debian",
        "ubuntu:debian",
        "noplatformsupported:",
        "amazon:amazon",
        "opensuseleap:suse",
        "opensuse:suse",
        "windows:windows",
        "centos:rhel",
        "arch:arch"
    }, delimiter = ':')
    public void getPlatformFamilyFromPlatformTest(String platformName, String expectedPlatformfamily) {

        String actualPlatformfamily = OhaiFunctions.getPlatformFamilyFromPlatform(platformName);
        assertEquals(expectedPlatformfamily, actualPlatformfamily);
    }
}
