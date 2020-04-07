/*******************************************************************************
 * Copyright (c) 2017-2019 Contributors to the Eclipse Foundation
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

import java.util.Objects;

public class GitBasedRepositoryConfiguration extends FileBasedRepositoryConfiguration {

    private boolean autoCommit;
    private String repositoryUrl;
    private String branch;

    public GitBasedRepositoryConfiguration(boolean autoCommit, FileBasedRepositoryConfiguration fileBasedRepositoryConfiguration) {
        super(Objects.requireNonNull(fileBasedRepositoryConfiguration));
        this.autoCommit = autoCommit;
    }

    public GitBasedRepositoryConfiguration(boolean autoCommit, String repositoryUrl, String branch, FileBasedRepositoryConfiguration fileBasedRepositoryConfiguration) {
        super(Objects.requireNonNull(fileBasedRepositoryConfiguration));
        this.autoCommit = autoCommit;
        this.repositoryUrl = repositoryUrl;
        this.branch = branch;
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }
}
