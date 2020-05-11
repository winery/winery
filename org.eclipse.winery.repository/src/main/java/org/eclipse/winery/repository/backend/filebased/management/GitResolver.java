/********************************************************************************
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

package org.eclipse.winery.repository.backend.filebased.management;

public class GitResolver extends AbstractGitResolver {

    public GitResolver(String url) {
        super(url);
    }

    public GitResolver(String url, String branch) {
        super(url, branch);
    }

    @Override
    public String getRepositoryMaintainerUrl() {
        return this.getUrl().substring(0, this.getUrl().lastIndexOf("/"));
    }

    @Override
    public String getRepositoryMaintainer() {
        String[] urlSplit = this.getUrl().split("/");
        return urlSplit[urlSplit.length - 2];
    }

    @Override
    public String getRepositoryName() {
        String[] urlSplit = this.getUrl().split("/");
        String fullRepositoryName = urlSplit[urlSplit.length - 1];

        if (!fullRepositoryName.contains("." + getVcsSystem())) {
            return fullRepositoryName;
        } else {
            return fullRepositoryName.split("." + getVcsSystem())[0];
        }
    }
}
