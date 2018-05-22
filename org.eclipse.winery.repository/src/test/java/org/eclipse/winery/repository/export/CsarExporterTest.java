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
package org.eclipse.winery.repository.export;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CsarExporterTest extends TestWithGitBackedRepository {

    public ByteArrayInputStream createOutputAndInputStream(String commitId, DefinitionsChildId id) throws Exception {
        setRevisionTo(commitId);
        CsarExporter exporter = new CsarExporter();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        exporter.writeCsar(RepositoryFactory.getRepository(), id, os);
        return new ByteArrayInputStream(os.toByteArray());
    }

    @Test
    public void csarIsValidZipForArtifactTemplateWithFilesAndSources() throws Exception {
        try (InputStream inputStream = this.createOutputAndInputStream("origin/plain", new ArtifactTemplateId("http://plain.winery.opentosca.org/artifacttemplates", "ArtifactTemplateWithFilesAndSources-ArtifactTypeWithoutProperties", false)); ZipInputStream zis = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                assertNotNull(name);
                assertFalse(name.contains("\\"), "name contains backslashes");
            }
        }
    }
}
