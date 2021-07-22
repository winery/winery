/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.filebased;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.eclipse.winery.common.configuration.Environments;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.constants.Filename;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MultiRepositoryTest extends RepositoryTest{

    /**
     * This test checks whether the MultiRepository detects duplicates of GitHub Repositories in the dependencies despite different capitalization
     */
    @Test
    public void testDuplicatesInDependencies() {
        writeDependencyFile();
        Path repositoryRoot = Paths.get(Environments.getInstance().getRepositoryConfig().getRepositoryRoot());
        MultiRepository multiRepro = null;
        try {
            multiRepro = new MultiRepository(repositoryRoot);
        } catch (IOException | GitAPIException e) {
            LOGGER.debug("Error while initializing MultiRepository", e);
        }
        assertNotNull(multiRepro);
        LOGGER.debug("Repositories: \n\t" + 
            multiRepro.getRepositories().stream()
            .map(r -> r.getRepositoryRoot().toString())
            .collect(Collectors.joining(",\n\t")));
        assertEquals(3, multiRepro.getRepositories().size());
    }

    /**
     * Helper method to place a dummy repositories.json dependency file into the test directory.
     * Also reconfigures the Factory to a MultiRepository.
     */
    void writeDependencyFile() {
        File dependencyFile = Paths.get(Environments.getInstance().getRepositoryConfig().getRepositoryRoot(), Filename.FILENAME_JSON_REPOSITORIES).toFile();
        try (FileWriter writer = new FileWriter(dependencyFile)) {
            writer.write("[\n" +
                "  {\n" +
                "    \"name\": \"test\",\n" +
                "    \"url\": \"https://github.com/winery/mulit-repo-test\",\n" +
                "    \"branch\": \"master\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"test-dependency\",\n" +
                "    \"url\": \"https://github.com/winery/MULTI-repo-dependency\",\n" +
                "    \"branch\": \"master\"\n" +
                "  }\n" +
                "]");
        } catch (IOException e) {
            LOGGER.error("Error creating dependency file.", e);
        }
        try {
            RepositoryFactory.reconfigure();
        } catch (Exception e) {
            LOGGER.error("Error reconfiguring Factory.", e);
        }
    }
}
