/*******************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.edmm.model;

import java.io.File;

import org.eclipse.winery.repository.TestWithGitBackedRepository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EdmmImporterTest extends TestWithGitBackedRepository {
    
    @Test
    void importComponentTypesTest() throws Exception {
        EdmmImporter edmmImporter = new EdmmImporter();

        File edmmFile = new File(ClassLoader.getSystemClassLoader().getResource("edmmModels/componentTypes.yaml").toURI());
        assertTrue(
            edmmImporter.transform(edmmFile.toPath())
        );
    }

}