/*******************************************************************************
 * Copyright (c) 2019-2020 Contributors to the Eclipse Foundation
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

import org.eclipse.winery.common.Util;

import org.apache.commons.configuration2.YAMLConfiguration;

public class RepositoryConfigurationObject extends AbstractConfigurationObject {

    private static final String key = "repository.";
    private GitConfigurationObject gitConfiguration;

    private RepositoryProvider provider;
    private String repositoryRoot;
    private String csarOutputPath;

    private YAMLConfiguration configuration;

    public enum RepositoryProvider {

        FILE("file"), YAML("yaml");

        private final String name;

        RepositoryProvider(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    RepositoryConfigurationObject(YAMLConfiguration configuration, GitConfigurationObject gitConfigurationObject) {
        this.setGitConfiguration(gitConfigurationObject);
        this.update(configuration);
    }

    public static String getProviderConfigurationKey() {
        return key + "provider";
    }

    @Override
    void save() {
        configuration.setProperty(key + "provider", this.getProvider().toString());
        configuration.setProperty(key + "repositoryRoot", this.repositoryRoot);
        configuration.setProperty(key + "csarOutputPath", this.csarOutputPath);
        this.getGitConfiguration().save();
        Environment.getInstance().save();
    }

    @Override
    void update(YAMLConfiguration updatedConfiguration) {
        this.configuration = updatedConfiguration;
        this.repositoryRoot = configuration.getString(key + "repositoryRoot");
        this.csarOutputPath = configuration.getString(key + "csarOutputPath");
        String provider = Environment.getInstance().getConfiguration().getString(getProviderConfigurationKey());
        if (provider.equalsIgnoreCase(RepositoryProvider.YAML.name())) {
            this.setProvider(RepositoryProvider.YAML);
        } else {
            this.setProvider(RepositoryProvider.FILE);
        }
    }

    @Override
    void initialize() {

    }

    public RepositoryConfigurationObject.RepositoryProvider getProvider() {
        return provider;
    }

    public void setProvider(RepositoryProvider provider) {
        this.provider = provider;
    }

    /**
     * Returns the path to the repository saved in the configuration file.
     *
     * @return path to configuration
     */
    public String getRepositoryRoot() {
        String repositoryRoot = this.repositoryRoot;
        if (repositoryRoot == null || repositoryRoot.isEmpty()) {
            repositoryRoot = Util.determineAndCreateRepositoryPath().toString();
        }
        setRepositoryRoot(repositoryRoot);
        return repositoryRoot;
    }

    public void setRepositoryRoot(String changedRepositoryRoot) {
        this.repositoryRoot = changedRepositoryRoot;
        this.save();
    }

    public String getCsarOutputPath() {
        String csarOutputPath = this.csarOutputPath;
        if (csarOutputPath == null || csarOutputPath.isEmpty()) {
            csarOutputPath = getRepositoryRoot() + File.separator + "csars";
        }
        setCsarOutputPath(csarOutputPath);
        Util.createCsarOutputPath(csarOutputPath);
        return csarOutputPath;
    }

    public void setCsarOutputPath(String csarOutputPath) {
        this.csarOutputPath = csarOutputPath;
        this.save();
    }

    public GitConfigurationObject getGitConfiguration() {
        return gitConfiguration;
    }

    public void setGitConfiguration(GitConfigurationObject gitConfiguration) {
        this.gitConfiguration = gitConfiguration;
    }
}
