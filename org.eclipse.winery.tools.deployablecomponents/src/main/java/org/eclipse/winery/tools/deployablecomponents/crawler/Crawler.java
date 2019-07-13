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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Crawler {

    private ICrawlerImplementation crawlerInstance;

    // contains dockerfile, which are crawled but not yet retrieved
    private List<Dockerfile> crawledDockerfiles = new ArrayList<>();

    public final static Logger LOGGER = LoggerFactory.getLogger(Crawler.class);

    public Crawler(CrawlerType type, String serviceName, String serviceToken) {
        switch (type) {
            case GITHUB:
                crawlerInstance = new GithubCrawler(serviceName, serviceToken);
                break;
            case LOCAL:
                crawlerInstance = new LocalCrawler(serviceName);
                break;
        }
    }

    public void setStartPoint(int number) {
        crawlerInstance.setStartPoint(number);
    }

    // access to already crawled dockerfiles
    public Dockerfile nextDockerfile() {
        Dockerfile nextDockerfile;
        synchronized (crawledDockerfiles) {
            if (crawledDockerfiles.isEmpty()) {
                return null;
            }

            nextDockerfile = crawledDockerfiles.get(0);
            // all following elements are shifted one down, order is kept
            crawledDockerfiles.remove(0);
        }
        return nextDockerfile;
    }

    /* crawls at least the given number of dockerfiles
    one crawling-step (e.g. a repository) is always finished before the number is checked
    returns the actual crawled number of dockerfiles*/
    public int crawlNewDockerfiles(int number) {
        int crawledDockerfilesCount = 0;
        try {
            while (crawledDockerfilesCount < number) {
                List<Dockerfile> newDockerfiles = crawlerInstance.crawlDockerfiles();
                crawledDockerfilesCount += newDockerfiles.size();
                synchronized (crawledDockerfiles) {
                    crawledDockerfiles.addAll(newDockerfiles);
                }
                for (Dockerfile dockerfile : newDockerfiles) {
                    String filePath = dockerfile.getRepoName() + "." + dockerfile.getPath();
                    filePath = filePath.replace("/", ".");
                    filePath = System.getProperty("user.home") + "/Dockerfiles/" + filePath;
                    BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
                    writer.write(dockerfile.getRepoName() + "\n");
                    writer.write(dockerfile.getPath() + "\n");
                    writer.write(dockerfile.getContent());
                    writer.close();
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to write dockerfile to the file system", e);
        }
        return crawledDockerfilesCount;
    }
}
