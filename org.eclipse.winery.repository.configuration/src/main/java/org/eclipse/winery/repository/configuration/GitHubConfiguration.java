/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.configuration;

import java.util.Objects;

public class GitHubConfiguration {

    private String gitHubClientId;
    private String gitHubClientSecret;

    public GitHubConfiguration(String gitHubClientId, String gitHubClientSecret) {
        this.gitHubClientId = gitHubClientId;
        this.gitHubClientSecret = gitHubClientSecret;
    }

    public String getGitHubClientId() {
        return gitHubClientId;
    }

    public void setGitHubClientId(String gitHubClientId) {
        this.gitHubClientId = gitHubClientId;
    }

    public String getGitHubClientSecret() {
        return gitHubClientSecret;
    }

    public void setGitHubClientSecret(String gitHubClientSecret) {
        this.gitHubClientSecret = gitHubClientSecret;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gitHubClientId, gitHubClientSecret);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GitHubConfiguration that = (GitHubConfiguration) o;
        return Objects.equals(gitHubClientId, that.gitHubClientId) &&
            Objects.equals(gitHubClientSecret, that.gitHubClientSecret);
    }
}
