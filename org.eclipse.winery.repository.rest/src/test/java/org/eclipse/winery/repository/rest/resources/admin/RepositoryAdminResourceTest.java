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
package org.eclipse.winery.repository.rest.resources.admin;

import java.nio.file.Path;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.junit.Test;

public class RepositoryAdminResourceTest extends AbstractResourceTest {

    @Test
    public void importIntoEmptyRepository() throws Exception {
        this.setRevisionTo("15cd64e30770ca7986660a34e1a4a7e0cf332f19"); // empty repository
        this.assertGet("servicetemplates/", "entitytypes/admin/servicetemplates_at_emtpy_repository.json");
        final Path path = MavenTestingUtils.getProjectFilePath("src/test/resources/entitytypes/admin/test-repository-with-single-empty-servicetemplate.zip");
        this.assertNoContentPost("admin/repository/", path);
        this.assertGet("servicetemplates/", "entitytypes/admin/servicetemplates_after_import_into_emtpy_repository.json");
    }

    @Test
    public void importIntoExistingRepository() throws Exception {
        // we need a non-changing state of the repository not containing {http://www.example.org}uploadTest
        // we (arbitrarily) choose branch "black" on 2017-09-26
        this.setRevisionTo("5142e3f95295710778060479aac6c2099e68703c");
        this.assertNotFound("servicetemplates/http%253A%252F%252Fwww.example.org/uploadTest");
        final Path path = MavenTestingUtils.getProjectFilePath("src/test/resources/entitytypes/admin/test-repository-with-single-empty-servicetemplate.zip");
        this.assertNoContentPost("admin/repository/", path);
        this.assertGet("servicetemplates/", "entitytypes/admin/servicetemplates_after_import_into_non_empty_repository.json");
    }
}
