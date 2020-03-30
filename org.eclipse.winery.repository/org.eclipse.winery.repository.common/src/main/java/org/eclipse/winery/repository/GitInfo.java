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
package org.eclipse.winery.repository;

/**
 * Class for handling information of Git repositories.
 */
public class GitInfo {
    /**
     * The URL of the git repository
     */
    public final String URL;
    /**
     * The branch or tag that should be pulled from the repository
     */
    public final String BRANCH;

    /**
     * Constructs a new pair of values.
     *
     * @param url    The URL of the git repository
     * @param branch The branch or tag that should be pulled from the repository
     */
    public GitInfo(String url, String branch) {
        this.URL = url;
        this.BRANCH = branch;
    }
}
