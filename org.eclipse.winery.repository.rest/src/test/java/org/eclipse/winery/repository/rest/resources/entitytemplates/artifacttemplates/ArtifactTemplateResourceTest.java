/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.entitytemplates.artifacttemplates;

import org.eclipse.jetty.toolchain.test.MavenTestingUtils;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Path;

public class ArtifactTemplateResourceTest extends AbstractResourceTest {

    @Test
    public void getFilesZip() throws Exception {
        this.setRevisionTo("88e5ccd6c35aeffdebc19c8dda9cd76f432538f8");
        this.assertGet("artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/MyTinyTest/files/zip", "entitytemplates/artifacttemplates/MyTinyTest_src.zip");
    }

    @Test
    public void getSourceZip() throws Exception {
        this.setRevisionTo("88e5ccd6c35aeffdebc19c8dda9cd76f432538f8");
        this.assertGet("artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/MyTinyTest/source/zip", "entitytemplates/artifacttemplates/MyTinyTest_src.zip");
    }

    @Test
    @Ignore("Ignored, because git-lfs is not mandatory for Winery to work properly")
    public void lfsTest() throws Exception {
        this.setRevisionTo("6ca5993d6a9abd255fb28f70c4ea73b189a47a57");
        // in case git-lsf is not available this test fails
        // NOT because the .iso file contained in source is not 2 bytes as expected,
        // BUT git-lfs is not available in the path
        this.assertGet("artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/MyTinyTest/source/", "entitytemplates/artifacttemplates/largeSource.json");
    }

    @Test
    public void copySourcesToFilesWithoutSelection() throws Exception {
        this.setRevisionTo("62c0749a622b11474f865e3aa06ccff4c380efd8");
        this.assertPost("artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/MyTinyTest/source/", "entitytemplates/artifacttemplates/copySourcesToFiles_without_selection.json");
        this.assertGet("artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/MyTinyTest/files/", "entitytemplates/artifacttemplates/copySourcesToFiles_without_selection_response.json");
    }

    @Test
    public void copySourcesToFilesWithSelection() throws Exception {
        this.setRevisionTo("62c0749a622b11474f865e3aa06ccff4c380efd8");
        this.assertPost("artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/MyTinyTest/source/", "entitytemplates/artifacttemplates/copySourcesToFiles_with_selection.json");
        this.assertGet("artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/MyTinyTest/files/", "entitytemplates/artifacttemplates/copySourcesToFiles_with_selection_response.json");
    }

    @Test
    public void copyFilesToSource() throws Exception {
        this.setRevisionTo("62c0749a622b11474f865e3aa06ccff4c380efd8");
        this.assertPostExpectBadRequestResponse("artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/MyTinyTest/files/", "entitytemplates/artifacttemplates/copySourcesToFiles_with_selection.json");
    }

    @Test
    public void artifactTemplateContainsFileReferenceInJson() throws Exception {
        this.setRevisionTo("6aabc1c52ad74ab2692e7d59dbe22a263667e2c9");
        this.assertGet("artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/MyTinyTest", "entitytemplates/artifacttemplates/MyTinyTest.json");
    }

    @Test
    public void artifactTemplateContainsFileReferenceInXml() throws Exception {
        this.setRevisionTo("6aabc1c52ad74ab2692e7d59dbe22a263667e2c9");
        this.assertGet("artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/MyTinyTest", "entitytemplates/artifacttemplates/MyTinyTest.xml");
    }

    @Test
    public void artifactTemplateContainsUpdatedFileReferenceInJson() throws Exception {
        this.setRevisionTo("15cd64e30770ca7986660a34e1a4a7e0cf332f19");
        this.assertNotFound("artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/artifactTemplateContainsUpdatedFileReferenceInJson");
        this.assertPost("artifacttemplates/", "entitytemplates/artifacttemplates/artifactTemplateContainsUpdatedFileReferenceInJson-create.json");
        this.assertGet("artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/artifactTemplateContainsUpdatedFileReferenceInJson", "entitytemplates/artifacttemplates/artifactTemplateContainsUpdatedFileReferenceInJson-withoutFile.json");

        // post an arbitrary file
        final Path path = MavenTestingUtils.getProjectFilePath("src/test/resources/entitytemplates/artifacttemplates/empty_text_file.txt");
        this.assertPost("artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/artifactTemplateContainsUpdatedFileReferenceInJson/files/", path);

        this.assertGet("artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/artifactTemplateContainsUpdatedFileReferenceInJson", "entitytemplates/artifacttemplates/artifactTemplateContainsUpdatedFileReferenceInJson-withFile.json");
    }
}
