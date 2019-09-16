/********************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.backend.filebased.management;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.eclipse.winery.common.configuration.FileBasedRepositoryConfiguration;
import org.eclipse.winery.common.configuration.GitBasedRepositoryConfiguration;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;

import org.eclipse.jgit.api.errors.GitAPIException;

public abstract class AbstractGitResolver implements IRepositoryResolver {

    private final String vcsSystem = "git";
    private final String repositoryUrl;
    private final String repositoryBranch;
    private final String repositoryMaintainer;
    private final String repositoryName;

    public AbstractGitResolver(String url) {
        this.repositoryUrl = url;
        this.repositoryBranch = "master";
        this.repositoryMaintainer = getRepositoryMaintainer();
        this.repositoryName = getRepositoryName();
    }

    public AbstractGitResolver(String url, String branch) {
        this.repositoryUrl = url;
        this.repositoryBranch = branch;
        this.repositoryMaintainer = getRepositoryMaintainer();
        this.repositoryName = getRepositoryName();
    }

    @Override
    public String getVcsSystem() {
        return this.vcsSystem;
    }

    @Override
    public String getUrl() {
        return this.repositoryUrl;
    }

    @Override
    public GitBasedRepository createRepository(File repositoryLocation) throws IOException, GitAPIException {
        GitBasedRepositoryConfiguration configuration = new GitBasedRepositoryConfiguration(false, repositoryUrl, repositoryBranch, new FileBasedRepositoryConfiguration(Paths.get(repositoryLocation.toString())));
        return new GitBasedRepository(configuration);
    }
}
