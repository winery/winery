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

package org.eclipse.winery.repository.xml;

import java.io.IOException;
import java.util.stream.Stream;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.backend.IRepository;

import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.platform.commons.util.Preconditions;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XmlRepositoryIntegrationTests extends TestWithGitBackedRepository {
    
    static IRepository staticRepo;
    @BeforeEach
    public void setUp() throws GitAPIException {
        this.setRevisionTo("origin/plain");
        staticRepo = repository;
    }
    
    @Test
    @Disabled("for this to work properly we need a \"touched\" repository state as well as deterministic serialization")
    public void roundtripDoesNotChangeContents() {
        assertAll(
            repository.getAllDefinitionsChildIds().stream()
                .map(definitionsId -> () -> {
                    TDefinitions retrieved = repository.getDefinitions(definitionsId);
                    try {
                        repository.putDefinition(definitionsId, retrieved);

                        final Status gitStatus = git.status().call();
                        assertTrue(gitStatus.isClean(), "Failed for definitionsId " + definitionsId);
                    } catch (IOException | GitAPIException e) {
                        Preconditions.condition(false, "Exception occured during validation");
                    }
                })
        );
    }
}
