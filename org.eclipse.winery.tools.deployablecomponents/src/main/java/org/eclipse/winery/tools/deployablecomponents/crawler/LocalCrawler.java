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

package org.eclipse.winery.tools.deployablecomponents.crawler;

import org.eclipse.winery.tools.deployablecomponents.commons.Dockerfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// "crawls" local stored dockerfiles
public class LocalCrawler implements ICrawlerImplementation {

    private File folder;

    public LocalCrawler(String path) {
        folder = new File(path);
    }

    public void setStartPoint(int number) {
        // does nothing, this crawler starts at the first file of a folder
    }

    public List<Dockerfile> crawlDockerfiles() throws IOException {
        if (!folder.isDirectory() || folder.listFiles() == null) {
            return new ArrayList<>();
        }

        List<Dockerfile> dockerfiles = new ArrayList<>();
        for (File file : folder.listFiles()) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String repoName = reader.readLine();
            String path = reader.readLine();
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            Dockerfile dockerfile = new Dockerfile(path, content.toString(), repoName);
            dockerfiles.add(dockerfile);
        }
        return dockerfiles;
    }
}
