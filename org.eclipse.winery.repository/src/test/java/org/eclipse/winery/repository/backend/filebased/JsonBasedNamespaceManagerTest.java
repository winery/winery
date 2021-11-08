/*******************************************************************************
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
 *******************************************************************************/

package org.eclipse.winery.repository.backend.filebased;

import java.io.File;

import org.eclipse.winery.model.tosca.constants.Namespaces;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonBasedNamespaceManagerTest {

    @Test
    public void getNamespacePrefix() {
        String systemTmpDir = System.getProperty("java.io.tmpdir");
        File f = new File(systemTmpDir + "/org.eclipse.winery.test/namespace.json");
        f.delete();

        JsonBasedNamespaceManager namespaceManager = new JsonBasedNamespaceManager(f);

        String prefix = namespaceManager.getPrefix(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE);
        
        assertEquals("winery", prefix);
        assertFalse(f.exists());
    }
}
