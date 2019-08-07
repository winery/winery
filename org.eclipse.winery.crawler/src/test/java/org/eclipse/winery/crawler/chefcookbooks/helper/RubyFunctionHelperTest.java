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

import org.eclipse.winery.crawler.chefcookbooks.constants.ChefDslConstants;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

class RubyFunctionHelperTest {

    @ParameterizedTest(name = "{index} => ''{2}''")
    @MethodSource("getStringsAndInts")
    void stringToInt(String intString, Integer expectedResult) {
        assertEquals(expectedResult, RubyFunctionHelper.stringToInt(intString));
    }

    private static Stream<Arguments> getStringsAndInts() {
        return Stream.of(
            Arguments.of("16.04", 16),
            Arguments.of("2012R2", 2012),
            Arguments.of("all", null),
            Arguments.of(ChefDslConstants.SUPPORTSALLPLATFORMVERSIONS, 0)
        );
    }
}
