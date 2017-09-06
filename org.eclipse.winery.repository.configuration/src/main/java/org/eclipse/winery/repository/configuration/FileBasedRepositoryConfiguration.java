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
