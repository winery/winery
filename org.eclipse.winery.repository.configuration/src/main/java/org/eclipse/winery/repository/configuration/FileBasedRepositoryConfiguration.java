package org.eclipse.winery.repository.configuration;

import java.nio.file.Path;

public class FileBasedRepositoryConfiguration {

	// may be null, because org.eclipse.winery.repository.backend.filebased.FilebasedRepository() determines the location automatically if no path exists
	private Path repositoryPath = null;

	/**
	 * There are no required values for the configuration
	 */
	public FileBasedRepositoryConfiguration() {
	}

	public FileBasedRepositoryConfiguration(Path repositoryPath) {
		this.repositoryPath = repositoryPath;
	}

	public Path getRepositoryPath() {
		return repositoryPath;
	}

	public void setRepositoryPath(Path repositoryPath) {
		this.repositoryPath = repositoryPath;
	}

}
