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

package org.eclipse.winery.repository.yaml;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.eclipse.winery.common.configuration.RepositoryConfigurationObject;
import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.repository.TestWithGitBackedRepository;

import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YamlRepositoryIntegrationTests extends TestWithGitBackedRepository {

    /**
     * While this intends to hijack the infrastructure exposed by {@link TestWithGitBackedRepository} wwe need to use
     * a YAML based repository for these tests and as such require a slightly different setup
     */
    public YamlRepositoryIntegrationTests() {
        super(Paths.get(System.getProperty("java.io.tmpdir")).resolve("yaml-test-repository"),
            "https://github.com/radon-h2020/radon-particles.git",
            RepositoryConfigurationObject.RepositoryProvider.YAML);
    }
    
    
    @BeforeEach
    public void testSetup() throws GitAPIException {
        setRevisionTo("e76b05461e255dbf41a780c543fdc7ad2516cbbc");
    }
    
    @ParameterizedTest()
    @ArgumentsSource(KnownIdProvider.class)
    public void roundtripDoesNotChangeContents(DefinitionsChildId definitionsId) throws GitAPIException, IOException {
        TDefinitions retrieved = repository.getDefinitions(definitionsId);
        repository.putDefinition(definitionsId, retrieved);

        final Status gitStatus = git.status().call();
        assertTrue(gitStatus.isClean(), "Backing Repository is not clean!");
        assertFalse(gitStatus.hasUncommittedChanges(), "Backing Repository has uncommited changes!");
    }
    
    private static class KnownIdProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                Arguments.of(new ServiceTemplateId("radon.blueprints", "SockShop", false))
            );
        }
    }
}
