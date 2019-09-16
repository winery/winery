/********************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.backend.filebased.management;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.SortedSet;

import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.backend.filebased.FilebasedRepository;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class RepositoryResolverTest extends TestWithGitBackedRepository {

    private final String url = "https://github.com/winery/test-repository";
    private IRepositoryResolver resolver;

    @BeforeEach
    public void initResolver() {
        Optional<IRepositoryResolver> optResolver = RepositoryResolverFactory.getResolver(url);
        optResolver.ifPresent(repositoryResolver -> resolver = repositoryResolver);
    }

    @Test
    public void getRepositoryName() {
        assertEquals("test-repository", resolver.getRepositoryName());
    }

    @Test
    public void getRepositoryMaintainer() {
        assertEquals("winery", resolver.getRepositoryMaintainer());
    }

    @Test
    public void getRepositoryMaintainerUrl() {
        assertEquals("https://github.com/winery", resolver.getRepositoryMaintainerUrl());
    }

    @Test
    public void getRepositoryUrl() {
        assertEquals("https://github.com/winery/test-repository", resolver.getUrl());
    }

    @Test
    public void getRepositoryVcs() {
        assertEquals("git", resolver.getVcsSystem());
    }

    @Test
    public void getRepository() throws GitAPIException {
        this.setRevisionTo("1f24de8867bf3df5d26b932abf4526c625d8502f");
        Path resolverRepositoryPath = Paths.get(System.getProperty("java.io.tmpdir")).resolve("winery").resolve("test-repository");

        try {
            FilebasedRepository resolverRepository = resolver.createRepository(resolverRepositoryPath.toFile());
            assertEquals(59, resolverRepository.getNamespaceManager().getAllNamespaces().size());

            SortedSet<NodeTypeId> allNodeTypes = resolverRepository.getAllDefinitionsChildIds(NodeTypeId.class);
            assertEquals(45, allNodeTypes.size());
        } catch (IOException | GitAPIException ex) {
            ex.getStackTrace();
        }
    }

    @Test
    public void testEmptyRepository() {
        Optional<IRepositoryResolver> resolver = RepositoryResolverFactory.getResolver("https:/1337/github.com/winry/test-repo");
        assertFalse(resolver.isPresent());
    }
}
