package org.eclipse.winery.repository.configuration;

public class FileBasedRepositoryConfiguration {

	// may be null, because org.eclipse.winery.repository.backend.filebased.FilebasedRepository() determines the location automatically if no path exists
	private String repositoryPath = null;

	/**
	 * There are no required values for the configuration
	 */
	public FileBasedRepositoryConfiguration() {
	}

	public FileBasedRepositoryConfiguration(String repositoryPath) {
		this.repositoryPath = repositoryPath;
	}

	public String getRepositoryPath() {
		return repositoryPath;
	}

	public void setRepositoryPath(String repositoryPath) {
		this.repositoryPath = repositoryPath;
	}

}
