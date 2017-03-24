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
 *     Armin Hüneburg - improved API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.backend.filebased;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import javax.ws.rs.core.MediaType;

import org.eclipse.winery.common.RepositoryFileReference;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CleanCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allows to reset repository to a certain commit id
 */
public class GitBasedRepository extends FilebasedRepository {

	/**
	 * Used for synchronizing the method {@link GitBasedRepository#addCommit(RepositoryFileReference)}
	 */
	private static final Object COMMIT_LOCK = new Object();
	private static final Logger LOGGER = LoggerFactory.getLogger(GitBasedRepository.class);

	private final Git git;

	/**
	 * @param repositoryLocation the location of the repository
	 * @throws IOException thrown if repository does not exist
	 * @throws GitAPIException thrown if there was an error while checking the status of the repository
	 * @throws NoWorkTreeException thrown if the directory is not a git work tree
	 */
	public GitBasedRepository(String repositoryLocation) throws IOException, NoWorkTreeException, GitAPIException {
		super(repositoryLocation);
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository gitRepo = builder.setWorkTree(this.repositoryRoot.toFile()).setMustExist(false).build();
		if (!Files.exists(this.repositoryRoot.resolve(".git"))) {
		    gitRepo.create();
		}
		this.git = new Git(gitRepo);
		if (!this.git.status().call().isClean()) {
			this.addCommit("Files changed externally.");
		}
	}

	/**
	 * This method is synchronized with an extra static object (meaning all instances are locked).
	 * The same lock object is also used in {@link #addCommit(RepositoryFileReference)}.
	 * This is to ensure that every commit only has one change.
	 *
	 * @param message The message that is used in the commit.
	 * @throws GitAPIException thrown when anything with adding or committing goes wrong.
	 */
	public void addCommit(String message) throws GitAPIException {
		synchronized (COMMIT_LOCK) {
			AddCommand add = this.git.add();
			add.addFilepattern(".");
			add.call();

			CommitCommand commit = this.git.commit();
			commit.setMessage(message);
			commit.call();
		}
	}

	/**
	 * This method is synchronized with an extra static object (meaning all instances are locked).
	 * The same lock object is also used in {@link #addCommit(String)}.
	 * This is to ensure that every commit only has one change.
	 *
	 * @param ref RepositoryFileReference to the file that was changed.
	 * @throws GitAPIException thrown when anything with adding or committing goes wrong.
	 */
	public void addCommit(RepositoryFileReference ref) throws GitAPIException {
		synchronized (COMMIT_LOCK) {
			String message;
            if (ref == null) {
                message = "Files changed externally.";
            } else {
                message = ref.toString() + " was updated";
            }
            addCommit(message);
		}
	}

	private void clean() throws NoWorkTreeException, GitAPIException {
		// remove untracked files
		CleanCommand clean = this.git.clean();
		clean.setCleanDirectories(true);
		clean.call();
	}

	public void cleanAndResetHard() throws NoWorkTreeException, GitAPIException {
		// enable updating by resetting the content of the repository
		this.clean();

		// reset to the latest version
		ResetCommand reset = this.git.reset();
		reset.setMode(ResetType.HARD);
		reset.call();
	}

	public void setRevisionTo(String ref) throws GitAPIException {
		this.clean();

		// reset repository to the desired reference
		ResetCommand reset = this.git.reset();
		reset.setMode(ResetType.HARD);
		reset.setRef(ref);
		reset.call();
	}

	@Override
	public void putContentToFile(RepositoryFileReference ref, InputStream inputStream, MediaType mediaType) throws IOException {
		super.putContentToFile(ref, inputStream, mediaType);
		try {
			this.addCommit(ref);
		} catch (GitAPIException e) {
			LOGGER.trace(e.getMessage(), e);
		}
	}
}
