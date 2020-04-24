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
package org.eclipse.winery.repository.rest.resources;

import java.nio.file.Path;

import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class MainResourceTest extends AbstractResourceTest {

    @Test
    @Disabled("The NamespaceManager does not/ reload Namespaces.properties upon each change. Thus, this tests fails under certain conditions -- winery-defs-for_servicetemplates-ImportCsarWithOverwriteTest vs. winery-defs-for_servicetemplates1-ImportCsarWithOverwriteTest")
    public void importCSARTestWithOverwrite() throws Exception {
        setRevisionTo("dc30db8f6086a8bcf6b39881d124f15fb05168f4");
        this.assertGet("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fservicetemplates/ImportCsarWithOverwriteTest/", "entitytypes/servicetemplates/importCsarWithOverwriteTest_initial.json");
        final Path path = MavenTestingUtils.getProjectFilePath("src/test/resources/entitytypes/servicetemplates/ImportCsarOverwriteTest.csar");
        this.assertPostWithOverwrite("", path, true);
        this.assertGet("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fservicetemplates/ImportCsarWithOverwriteTest/", "entitytypes/servicetemplates/importCsarWithOverwriteTest_afterOverwrite.json");
    }
}
