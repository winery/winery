/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.backend.xsd;

import org.eclipse.winery.repository.TestWithGitBackedRepository;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RepositoryBasedXsdImportManagerTest extends TestWithGitBackedRepository {

    @Test
    public void getAllDeclaredElementsLocalNamesTest() throws Exception {
        this.setRevisionTo("5fdcffa9ccd17743d5498cab0914081fc33606e9");

        RepositoryBasedXsdImportManager manager = (RepositoryBasedXsdImportManager) this.repository.getXsdImportManager();
        List<NamespaceAndDefinedLocalNames> list = manager.getAllDeclaredElementsLocalNames();

        assertEquals(1, list.size());
    }

    @Test
    public void getAllDefinedLocalNamesForElementsTest() throws Exception {
        this.setRevisionTo("3465576f5b46079bb26f5c8e93663424440421a0");

        RepositoryBasedXsdImportManager manager = (RepositoryBasedXsdImportManager) this.repository.getXsdImportManager();
        List<NamespaceAndDefinedLocalNames> list = manager.getAllDefinedTypesLocalNames();

        assertEquals(1, list.size());
    }
}
