/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 * Lukas Harzenetter - initial API and implementation
 */

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
