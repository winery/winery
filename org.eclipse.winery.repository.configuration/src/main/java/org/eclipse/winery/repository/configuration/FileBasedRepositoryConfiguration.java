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

package org.eclipse.winery.repository.configuration;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.jdt.annotation.NonNull;

public class FileBasedRepositoryConfiguration {

	// may be null, because org.eclipse.winery.repository.backend.filebased.FilebasedRepository() determines the location automatically if no path exists
	private Path repositoryPath = null;

	/**
	 * There are no required values for the configuration
	 */
	public FileBasedRepositoryConfiguration() {
	}

	public FileBasedRepositoryConfiguration(@NonNull Path repositoryPath) {
		this.repositoryPath = Objects.requireNonNull(repositoryPath);
	}

	public FileBasedRepositoryConfiguration(@NonNull FileBasedRepositoryConfiguration fileBasedRepositoryConfiguration) {
		this.repositoryPath = Objects.requireNonNull(fileBasedRepositoryConfiguration).repositoryPath;
	}

	public Optional<Path> getRepositoryPath() {
		return Optional.ofNullable(repositoryPath);
	}

	public void setRepositoryPath(@NonNull Path repositoryPath) {
		this.repositoryPath = Objects.requireNonNull(repositoryPath);
	}
}
