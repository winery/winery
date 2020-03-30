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

package org.eclipse.winery.repository.backend.filebased;

import java.io.Serializable;

public class RepositoryProperties implements Serializable, Comparable<RepositoryProperties> {

    private String name;
    private String url;
    private String branch;

    public RepositoryProperties() {
    }

    public RepositoryProperties(String name, String url) {

        this.name = name;
        this.url = url;
        this.branch = "master";
    }

    public RepositoryProperties(String name, String url, String branch) {

        this.name = name;
        this.url = url;
        this.branch = branch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    @Override
    public int compareTo(RepositoryProperties o) {
        int compareTo = this.name.compareTo(o.name);

        if (compareTo == 0) {
            return this.url.compareTo(o.url);
        }

        return compareTo;
    }
}
