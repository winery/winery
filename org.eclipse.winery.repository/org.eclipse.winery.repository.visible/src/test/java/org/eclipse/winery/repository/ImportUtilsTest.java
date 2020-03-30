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
package org.eclipse.winery.repository;

import org.eclipse.winery.model.ids.Namespace;
import org.eclipse.winery.model.ids.XmlId;
import org.eclipse.winery.model.ids.definitions.imports.XSDImportId;
import org.eclipse.winery.repository.backend.ImportUtils;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImportUtilsTest extends TestWithGitBackedRepository {

    @Test
    public void getLocationForImportTest() throws Exception {
        this.setRevisionTo("5fdcffa9ccd17743d5498cab0914081fc33606e9");

        XSDImportId id = new XSDImportId(
            new Namespace("http://opentosca.org/nodetypes", false),
            new XmlId("CloudProviderProperties", false));
        Optional<String> importLocation = ImportUtils.getLocation(repository, id);

        assertEquals(true, importLocation.isPresent());
    }
}
