/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.winery.common.configuration.Environments;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.constants.Filename;
import org.eclipse.winery.repository.backend.filebased.RepositoryProperties;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MultiRepositoryManagerTest extends RepositoryTest{
    

    /**
     * Tests whenever a file is created for the repository list in the root folder.
     */
    @Test
    public void testInitializeRepositoryList() throws IOException {
        ArrayList<RepositoryProperties> repositoryList = new ArrayList<>();
        repositoryList.add(new RepositoryProperties("mainTestRepository",
            "https://github.com/winery/mulit-repo-test", "master"));
        MultiRepositoryManager multiRepositoryManager = new MultiRepositoryManager();
        multiRepositoryManager.initializeRepositoryListForMultiRepositoryAndReconfigureFactory(repositoryList);
        assertTrue(Paths.get(Environments.getInstance().getRepositoryConfig().getRepositoryRoot(),
            Filename.FILENAME_JSON_MUTLI_REPOSITORIES).toFile().exists());
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectReader reader = objectMapper.readerFor(new TypeReference<List<RepositoryProperties>>() {
        });
        repositoryList = reader.readValue(Paths.get(Environments.getInstance().getRepositoryConfig().getRepositoryRoot(),
            Filename.FILENAME_JSON_MUTLI_REPOSITORIES).toFile());
        assertEquals(1, repositoryList.size());
        assertEquals("https://github.com/winery/mulit-repo-test", repositoryList.get(0).getUrl());
        assertEquals("master", repositoryList.get(0).getBranch());
        assertEquals("mainTestRepository", repositoryList.get(0).getName());
    }

    /**
     * Test whenever the initzialize method reconfigures the RepositoryFactory correctly.
     */
    @Test
    public void testMultiRepositoryCreation() {
        ArrayList<RepositoryProperties> repositoryList = new ArrayList<>();
        repositoryList.add(new RepositoryProperties("mainTestRepository",
            "https://github.com/winery/mulit-repo-test", "master"));
        MultiRepositoryManager multiRepositoryManager = new MultiRepositoryManager();
        multiRepositoryManager.initializeRepositoryListForMultiRepositoryAndReconfigureFactory(repositoryList);
        assertTrue(RepositoryFactory.getRepository() instanceof MultiRepository);
    }

    /**
     * This test checks whenever the repositories of the repositoryList are imported correctly, and whenever the
     * dependencies of those repositories are also correctly imported
     */
    @Test
    public void testRepositoryListImport() throws UnsupportedEncodingException {
        ArrayList<RepositoryProperties> repositoryList = new ArrayList<>();
        repositoryList.add(new RepositoryProperties("mainTestRepository",
            "https://github.com/winery/mulit-repo-test", "master"));
        repositoryList.add(new RepositoryProperties("standardTestRepository", "https://github.com/winery/test-repository", "plain"));
        MultiRepositoryManager multiRepositoryManager = new MultiRepositoryManager();
        multiRepositoryManager.addRepositoryToFile(repositoryList);
        try {
            assertTrue(Paths.get(Environments.getInstance().getRepositoryConfig().getRepositoryRoot(), URLEncoder.encode("https://github.com/winery", "UTF-8")).toFile().exists());
            assertTrue(Paths.get(Environments.getInstance().getRepositoryConfig().getRepositoryRoot(), URLEncoder.encode("https://github.com/winery", "UTF-8"), "mulit-repo-test").toFile().exists());
            assertTrue(Paths.get(Environments.getInstance().getRepositoryConfig().getRepositoryRoot(), URLEncoder.encode("https://github.com/winery", "UTF-8"), "multi-repo-dependency").toFile().exists());
            assertTrue(Paths.get(Environments.getInstance().getRepositoryConfig().getRepositoryRoot(), URLEncoder.encode("https://github.com/winery", "UTF-8"), "test-repository").toFile().exists());
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Error while encoding.");
            throw e;
        }
    }

    /**
     * This test ckecks whenever the repository list is correctly retrieved from the json file.
     */
    @Test
    public void testGetRepositoriesAsList() {
        writeDependencyFile();
        MultiRepositoryManager multiRepositoryManager = new MultiRepositoryManager();
        List<RepositoryProperties> dependencies = multiRepositoryManager.getRepositoriesAsList();
        assertEquals(1, dependencies.size());
        RepositoryProperties testRepoDependency = dependencies.get(0);
        assertEquals("test", testRepoDependency.getName());
        assertEquals("plain", testRepoDependency.getBranch());
        assertEquals("https://github.com/winery/test-repository", testRepoDependency.getUrl());
    }

    /**
     * This test checks whenever an empty collection is returned in the case of an invocation of the
     * getRepositoriesAsList on a Repository which is not a MultiRepository.
     */
    @Test
    public void testGetRepositoriesAsListOnNonMultiRepository() {
        try {
            //RepositoryFactory.reconfigure();
        } catch (Exception e) {
            e.printStackTrace();
        }
        MultiRepositoryManager multiRepositoryManager = new MultiRepositoryManager();
        List<RepositoryProperties> dependencies = multiRepositoryManager.getRepositoriesAsList();
        if (dependencies.size() > 0) {
            dependencies.forEach(dependency -> {
                System.out.println(dependency.getName());
            });
        }
        assertEquals(0, dependencies.size());
    }

    /**
     * Helper method to place a dummy repositories.json dependency file into the test directory.
     * Also reconfigures the Factory to a MultiRepository.
     */
    void writeDependencyFile() {
        File dependencyFile = Paths.get(Environments.getInstance().getRepositoryConfig().getRepositoryRoot(), Filename.FILENAME_JSON_MUTLI_REPOSITORIES).toFile();
        try (FileWriter writer = new FileWriter(dependencyFile)) {
            writer.write("[\n" +
                "   {\n" +
                "      \"name\": \"test\",\n" +
                "      \"url\": \"https://github.com/winery/test-repository\",\n" +
                "      \"branch\": \"plain\"\n" +
                "   }\n" +
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
