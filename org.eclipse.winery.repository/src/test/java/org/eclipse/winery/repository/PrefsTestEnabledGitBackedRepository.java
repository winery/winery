/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class PrefsTestEnabledGitBackedRepository extends PrefsTestEnabled {

	public final Git git;

	public PrefsTestEnabledGitBackedRepository() throws Exception {
		super(false);

		Path repositoryPath = Paths.get(System.getProperty("java.io.tmpdir")).resolve("test-repository");
		if (!Files.exists(repositoryPath)) {
			Files.createDirectory(repositoryPath);
		}

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		if (!Files.exists(repositoryPath.resolve(".git"))) {
			this.git = Git.cloneRepository()
					.setURI("https://github.com/winery/test-repository.git")
					.setBare(false)
					.setCloneAllBranches(true)
					.setDirectory(repositoryPath.toFile())
					.call();
		} else {
			Repository gitRepo = builder.setWorkTree(repositoryPath.toFile()).setMustExist(false).build();
			this.git = new Git(gitRepo);
			this.git.fetch().call();
		}

		this.repository = new GitBasedRepository(repositoryPath.toString());
	}

}
