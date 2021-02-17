/*******************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.common.configuration;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.jdt.annotation.NonNull;

public class FileBasedRepositoryConfiguration {

    private Path repositoryPath = null;
    private RepositoryConfigurationObject.RepositoryProvider repositoryProvider;

    /**
     * There are no required values for the configuration
     */
    public FileBasedRepositoryConfiguration() {
    }

    public FileBasedRepositoryConfiguration(@NonNull Path repositoryPath) {
        this(repositoryPath, Environments.getInstance().getRepositoryConfig().getProvider());
    }

    // We are making the access public here to enable proper testing.
    public FileBasedRepositoryConfiguration(@NonNull Path repositoryPath, RepositoryConfigurationObject.RepositoryProvider repositoryProvider) {
        this.repositoryPath = Objects.requireNonNull(repositoryPath);
        this.repositoryProvider = Objects.requireNonNull(repositoryProvider);
    }

    public FileBasedRepositoryConfiguration(@NonNull FileBasedRepositoryConfiguration configuration) {
        Objects.requireNonNull(configuration);
        this.repositoryPath = Objects.requireNonNull(configuration.repositoryPath);
        this.repositoryProvider = Objects.requireNonNull(configuration.repositoryProvider);
    }

    public Optional<Path> getRepositoryPath() {
        return Optional.ofNullable(repositoryPath);
    }

    public void setRepositoryPath(@NonNull Path repositoryPath) {
        this.repositoryPath = Objects.requireNonNull(repositoryPath);
    }

    public RepositoryConfigurationObject.RepositoryProvider getRepositoryProvider() {
        return repositoryProvider;
    }

    public void setRepositoryProvider(RepositoryConfigurationObject.RepositoryProvider repositoryProvider) {
        this.repositoryProvider = repositoryProvider;
    }
}
