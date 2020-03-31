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
package org.eclipse.winery.repository.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilTests {

    @Test
    public void testNamespaceToJavaPackageFullURL() {
        assertEquals("org.example.www.tosca.nodetypes", Util.namespaceToJavaPackage("http://www.example.org/tosca/nodetypes"));
    }

    @Test
    public void testNamespaceToJavaPackageURLWithHostOnly() {
        assertEquals("org.example.www", Util.namespaceToJavaPackage("http://www.example.org/"));
    }

    @Test
    public void testNamespaceToJavaPackageURLWithHostOnlyAndNoFinalSlash() {
        assertEquals("org.example.www", Util.namespaceToJavaPackage("http://www.example.org"));
    }

    @Test
    public void testNamespaceToJavaPackageURLWithNoHost() {
        assertEquals("plainNCname", Util.namespaceToJavaPackage("plainNCname"));
    }

    @Test
    public void testNCNameFromURL() {
        assertEquals("http___www.example.org", Util.makeNCName("http://www.example.org"));
    }

    @Test
    public void testNCNameFromNCName() {
        assertEquals("NCName", Util.makeNCName("NCName"));
    }
}
