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
package org.eclipse.winery.repository.backend.filebased;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CleanCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.winery.repository.Prefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used for testing only.
 * 
 * Allows to reset repository to a certain commit id
 */
public class GitBasedRepository extends FilebasedRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(GitBasedRepository.class);
	
	private final Repository gitRepo;
	private final Git git;
	private final CredentialsProvider cp;
	
	public static final String PREFERENCE_GIT_USERNAME = "git.username";
	public static final String PREFERENCE_GIT_PASSWORD = "git.password";
	
	
	/**
	 * @param repositoryLocation the location of the repository
	 * @throws IOException thrown if repository does not exist
	 */
	public GitBasedRepository(String repositoryLocation) throws IOException {
		super(repositoryLocation);
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		this.gitRepo = builder.setWorkTree(this.repositoryRoot.toFile()).setMustExist(true).build();
		this.git = new Git(this.gitRepo);
		
		this.cp = this.initializeCredentialsProvider();
	}
	
	/**
	 * Reads the properties stored in ".winery" in the repository
	 */
	private Properties dotWineryProperties() {
		Properties p = new Properties();
		File f = new File(this.repositoryRoot.toFile(), ".winery");
		InputStream is;
		try {
			is = new FileInputStream(f);
		} catch (FileNotFoundException e1) {
			// .winery does not exist in the file-based repository
			return p;
		}
		if (is != null) {
			try {
				p.load(is);
			} catch (IOException e) {
				GitBasedRepository.logger.debug(e.getMessage(), e);
			}
		}
		return p;
	}
	
	/**
	 * Uses git.username und git.password from .winery and winery.properties
	 * 
	 * Considering .winery is useful if the same war file is used on a dev
	 * server and a stable server. The WAR file cannot contain the credentials
	 * if committing is only allowed on only one of these servers
	 */
	private CredentialsProvider initializeCredentialsProvider() {
		CredentialsProvider cp;
		
		Properties wp = this.dotWineryProperties();
		
		String gitUserName = wp.getProperty(GitBasedRepository.PREFERENCE_GIT_USERNAME);
		if (gitUserName == null) {
			gitUserName = Prefs.INSTANCE.getProperties().getProperty(GitBasedRepository.PREFERENCE_GIT_USERNAME);
		}
		
		String gitPassword = wp.getProperty(GitBasedRepository.PREFERENCE_GIT_PASSWORD);
		if (gitPassword == null) {
			gitPassword = Prefs.INSTANCE.getProperties().getProperty(GitBasedRepository.PREFERENCE_GIT_PASSWORD);
		}
		
		if (gitUserName == null) {
			cp = null;
		} else if (gitPassword == null) {
			cp = null;
		} else {
			cp = new UsernamePasswordCredentialsProvider(gitUserName, gitPassword);
		}
		return cp;
	}
	
	public void addCommitPush() throws NoHeadException, NoMessageException, UnmergedPathsException, ConcurrentRefUpdateException, WrongRepositoryStateException, GitAPIException {
		AddCommand add = this.git.add();
		add.addFilepattern(".");
		add.call();
		
		CommitCommand commit = this.git.commit();
		commit.setMessage("Commit through Winery");
		commit.call();
		
		PushCommand push = this.git.push();
		if (this.cp != null) {
			push.setCredentialsProvider(this.cp);
		}
		push.call();
	}
	
	private void clean() throws NoWorkTreeException, GitAPIException {
		GitBasedRepository.logger.trace("git clean");
		// remove untracked files
		CleanCommand clean = this.git.clean();
		clean.setCleanDirectories(true);
		clean.call();
	}
	
	public void cleanAndResetHard() throws NoWorkTreeException, GitAPIException {
		// enable updating by resetting the content of the repository
		this.clean();
		
		// fetch the newest thing from upstream
		GitBasedRepository.logger.trace("git fetch");
		FetchCommand fetch = this.git.fetch();
		if (this.cp != null) {
			fetch.setCredentialsProvider(this.cp);
		}
		fetch.call();
		
		// after fetching, reset to the latest version
		GitBasedRepository.logger.trace("git reset --hard");
		ResetCommand reset = this.git.reset();
		reset.setMode(ResetType.HARD);
		reset.call();
	}
	
	public void setRevisionTo(String ref) throws CheckoutConflictException, GitAPIException {
		this.clean();
		
		// reset repository to the desired reference
		ResetCommand reset = this.git.reset();
		reset.setMode(ResetType.HARD);
		reset.setRef(ref);
		reset.call();
	}
	
	/**
	 * Returns true if authentification information (for instance, to push to
	 * upstream) is available
	 */
	public boolean authenticationInfoAvailable() {
		return this.cp != null;
	}
}
