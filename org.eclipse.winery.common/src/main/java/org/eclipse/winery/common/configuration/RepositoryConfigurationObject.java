/*******************************************************************************
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

package org.eclipse.winery.common.configuration;

import java.io.File;

import org.eclipse.winery.common.Constants;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.io.FileUtils;

public class RepositoryConfigurationObject extends AbstractConfigurationObject {
    private final String key = "repository.";
    private GitConfigurationObject gitConfiguration;
    private String repositoryRoot;
    private String provider;
    private YAMLConfiguration configuration;

    RepositoryConfigurationObject(YAMLConfiguration configuration) {
        this.repositoryRoot = configuration.getString(key + "repositoryRoot");
        this.setProvider(configuration.getString(key + "provider"));
        this.setGitConfiguration(Environments.getGitConfig());
        this.configuration = configuration;
    }

    @Override
    void save() {
        configuration.setProperty(key + "provider", this.getProvider());
        configuration.setProperty(key + "repositoryRoot", this.repositoryRoot);
        this.getGitConfiguration().save();
        Environment.save();
    }

    /**
     * Returns the path to the repositiory saved in the configuration file.
     *
     * @return path to configuration
     */
    public String getRepositoryRoot() {
        String repositoryRoot = this.repositoryRoot;
        if (repositoryRoot == null || repositoryRoot.isEmpty()) {
            return FileUtils.getUserDirectory().getAbsolutePath() + File.separator + Constants.DEFAULT_REPO_NAME;
        } else {
            return repositoryRoot;
        }
    }

    public void setRepositoryRoot(String changedRepositoryRoot) {
        this.repositoryRoot = changedRepositoryRoot;
        this.save();
    }

    public GitConfigurationObject getGitConfiguration() {
        return gitConfiguration;
    }

    public void setGitConfiguration(GitConfigurationObject gitConfiguration) {
        this.gitConfiguration = gitConfiguration;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
