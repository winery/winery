/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API, implementation, and maintenance
 *******************************************************************************/
package org.eclipse.winery.repository.backend;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.winery.repository.backend.filebased.FilebasedRepository;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;
import org.eclipse.winery.repository.configuration.Environment;
import org.eclipse.winery.repository.configuration.FileBasedRepositoryConfiguration;
import org.eclipse.winery.repository.configuration.GitBasedRepositoryConfiguration;
import org.eclipse.winery.repository.configuration.JCloudsConfiguration;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryFactory.class);

	private static GitBasedRepositoryConfiguration gitBasedRepositoryConfiguration = null;
	private static FileBasedRepositoryConfiguration fileBasedRepositoryConfiguration = null;
	private static JCloudsConfiguration jCloudsConfiguration;

	private static IRepository repository = null;

	public static void reconfigure(GitBasedRepositoryConfiguration gitBasedRepositoryConfiguration) throws IOException, GitAPIException {
		RepositoryFactory.gitBasedRepositoryConfiguration = gitBasedRepositoryConfiguration;
		RepositoryFactory.fileBasedRepositoryConfiguration = null;
		RepositoryFactory.jCloudsConfiguration = null;

		repository = new GitBasedRepository(gitBasedRepositoryConfiguration);
	}

	public static void reconfigure(FileBasedRepositoryConfiguration fileBasedRepositoryConfiguration) {
		RepositoryFactory.fileBasedRepositoryConfiguration = fileBasedRepositoryConfiguration;
		RepositoryFactory.gitBasedRepositoryConfiguration = null;
		RepositoryFactory.jCloudsConfiguration = null;

		repository = new FilebasedRepository(fileBasedRepositoryConfiguration);
	}

	public static void reconfigure(JCloudsConfiguration jCloudsConfiguration) {
		RepositoryFactory.jCloudsConfiguration = jCloudsConfiguration;
		RepositoryFactory.fileBasedRepositoryConfiguration = null;
		RepositoryFactory.gitBasedRepositoryConfiguration = null;

		// TODO
	}

	/**
	 * Reconfigures based on Environment
	 */
	public static void reconfigure() throws Exception {
		final Optional<JCloudsConfiguration> jCloudsConfiguration = Environment.getJCloudsConfiguration();
		if (jCloudsConfiguration.isPresent()) {
			reconfigure(jCloudsConfiguration.get());
		} else {
			final Optional<GitBasedRepositoryConfiguration> gitBasedRepositoryConfiguration = Environment.getGitBasedRepositoryConfiguration();
			if (gitBasedRepositoryConfiguration.isPresent()) {
				reconfigure(gitBasedRepositoryConfiguration.get());
			} else {
				final Optional<FileBasedRepositoryConfiguration> filebasedRepositoryConfiguration = Environment.getFilebasedRepositoryConfiguration();
				reconfigure(filebasedRepositoryConfiguration.orElse(new FileBasedRepositoryConfiguration()));
			}
		}
	}

	public static IRepository getRepository() {
		if ((gitBasedRepositoryConfiguration == null) && (fileBasedRepositoryConfiguration == null) && (jCloudsConfiguration == null)) {
			// in case nothing is configured, use the file-based repository as fallback
			LOGGER.debug("No repository configuration available. Using default configuration.");
			reconfigure(new FileBasedRepositoryConfiguration());
		}
		return repository;
	}

	/**
	 * Generates a new IRepository working on the specified path. No git support, just plain file system
	 */
	public static IRepository getRepository(Path path) {
		Objects.requireNonNull(path);
		FileBasedRepositoryConfiguration fileBasedRepositoryConfiguration = new FileBasedRepositoryConfiguration(path);
		return getRepository(fileBasedRepositoryConfiguration);
	}

	public static IRepository getRepository(FileBasedRepositoryConfiguration fileBasedRepositoryConfiguration) {
		return new FilebasedRepository(Objects.requireNonNull(fileBasedRepositoryConfiguration));
	}
}
