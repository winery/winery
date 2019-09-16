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
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Crawler {

    private ICrawlerImplementation crawlerInstance;

    // contains dockerfile, which are crawled but not yet retrieved
    private final Deque<Dockerfile> crawledDockerfiles = new ConcurrentLinkedDeque<>();
    
    private final String localCopyPath;

    public final static Logger LOGGER = LoggerFactory.getLogger(Crawler.class);

    public Crawler(CrawlerType type, String serviceName, String serviceToken, String localCopyPath) {
        switch (type) {
            case GITHUB:
                crawlerInstance = new GithubCrawler(serviceName, serviceToken);
                break;
            case LOCAL:
                crawlerInstance = new LocalCrawler(serviceName);
                break;
        }
        this.localCopyPath = localCopyPath;
    }

    public void setStartPoint(int number) {
        crawlerInstance.setStartPoint(number);
    }

    // access to already crawled dockerfiles
    public Dockerfile nextDockerfile() {
        return crawledDockerfiles.pollFirst();
    }

    /**
     * crawls at least the given number of dockerfiles
     * one crawling-step (e.g. a repository) is always finished before the number is checked
     * returns the actual crawled number of dockerfiles
     *
     * @param number how many dockerfiles should be crawled
     * @return actual number of crawled dockerfiles
     */
    public int crawlNewDockerfiles(int number) {
        int crawledDockerfilesCount = 0;
        try {
            while (crawledDockerfilesCount < number) {
                List<Dockerfile> newDockerfiles = crawlerInstance.crawlDockerfiles();
                crawledDockerfilesCount += newDockerfiles.size();
                synchronized (crawledDockerfiles) {
                    crawledDockerfiles.addAll(newDockerfiles);
                }
                if (!localCopyPath.isEmpty()) {
                    for (Dockerfile dockerfile : newDockerfiles) {
                        String filePath = dockerfile.getRepoName() + "." + dockerfile.getPath();
                        filePath = filePath.replace("/", ".");
                        filePath = localCopyPath + filePath;
                        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
                        writer.write(dockerfile.getRepoName() + "\n");
                        writer.write(dockerfile.getPath() + "\n");
                        writer.write(dockerfile.getContent());
                        writer.close();
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to write dockerfile to the file system", e);
        }
        return crawledDockerfilesCount;
    }
}
