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

package org.eclipse.winery.tools.deployablecomponents.commons;

public class Dockerfile {

    private String path;
    private String content;
    private String repoName;

    public Dockerfile(String path, String content, String repoName) {
        this.path = path;
        this.content = content;
        this.repoName = repoName;
    }


    public String getPath() {
        return path;
    }

    public String getContent() {
        return content;
    }

    public String getRepoName() {
        return repoName;
    }

    public enum DockerInstruction {
        FROM,
        RUN,
        ARG,
        ENV;

        public String asString() {
            switch (this) {
                case FROM:
                    return "FROM";
                case RUN:
                    return "RUN";
                case ARG:
                    return "ARG";
                case ENV:
                    return "ENV";
            }
            return "";
        }
    }
}
