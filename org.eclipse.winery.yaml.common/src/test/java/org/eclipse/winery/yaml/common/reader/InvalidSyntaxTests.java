/********************************************************************************
 * Copyright (c) {date} Contributors to the Eclipse Foundation
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
package org.eclipse.winery.yaml.common.reader;

import org.eclipse.winery.yaml.common.exception.MultiException;
import org.eclipse.winery.yaml.common.reader.yaml.Reader;
import org.junit.Test;

/**
 * Intention of this test class is to test the robustness of the parser Goal is to see no unexpected
 * exceptions
 */
public class InvalidSyntaxTests {

    private static String PREFIX = "src/test/resources/builder/invalid_syntax";

    /**
     * This test should check for "mapping values are not allowed" or the Exception Handling should be
     * rewritten so that {@link YAMLParserException} can be directly parsed.
     *
     * See also https://github.com/eclipse/winery/pull/188/files#r146142196
     */
    @Test(expected = MultiException.class)
    public void missingLineBreakThrowsException() throws MultiException {
        Reader reader = new Reader();
        reader.parse(PREFIX, "missing_linebreak.yaml");
    }
}
