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

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;

public class PrefsTestEnabledGitBackedRepository extends PrefsTestEnabled {

	public PrefsTestEnabledGitBackedRepository() throws IOException, NoWorkTreeException, GitAPIException {
		super(false);
		// TODO: we should to a new clone of the repository
		// currently, we rely on the right configuration of the preferences to use a file-based repository

		// code similar to org.eclipse.winery.repository.Prefs.doRepositoryInitialization()
		String repositoryLocation = this.properties.getProperty("repositoryPath");
		this.repository = new GitBasedRepository(repositoryLocation);
	}

}
