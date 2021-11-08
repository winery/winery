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

package org.eclipse.winery.crawler.chefcookbooks.helper;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

class VersionTest {

    @ParameterizedTest(name = "{index} => ''{3}''")
    @MethodSource("getVersionsToCompare")
    void compareTo(Version version1, Version version2, int expectedResult) {
        assertEquals(expectedResult, version1.compareTo(version2));
    }

    private static Stream<Arguments> getVersionsToCompare() {
        return Stream.of(
            Arguments.of(new Version("1.0.0"), new Version("1.1.2"), -1),
            Arguments.of(new Version("2.0.0"), new Version("1.1.2"), 1),
            Arguments.of(new Version("1.0.0"), new Version("1.0.0"), 0)
        );
    }
}
