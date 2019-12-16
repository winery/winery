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

package org.eclipse.winery.crawler.chefcookbooks.chefcookbookcrawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the possibility to crawl multiple cookbooks from the Chef Supermarket in parallel by the name
 * of the cookbook.
 */
public class CrawlCookbookRunnable implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlCookbookRunnable.class.getName());
    private Thread thread;
    private String threadName;
    private String cookbookDirectory;
    private String tempDirectory;
    private String versionRestriction;

    public CrawlCookbookRunnable(String cookbookName, String cookbookDirectory, String tempDirectory) {
        this.threadName = cookbookName;
        this.cookbookDirectory = cookbookDirectory;
        this.tempDirectory = tempDirectory;
    }

    public CrawlCookbookRunnable(String cookbookName, String versionRestriction, String cookbookDirectory, String tempDirectory) {
        this.threadName = cookbookName;
        this.cookbookDirectory = cookbookDirectory;
        this.tempDirectory = tempDirectory;
        this.versionRestriction = versionRestriction;
    }

    @Override
    public void run() {
        try {
            ChefSupermarketCrawler chefSupermarketCrawler = new ChefSupermarketCrawler(this.cookbookDirectory, this.tempDirectory);
            if (versionRestriction != null) {
                chefSupermarketCrawler.getCookbook(this.threadName, this.versionRestriction);
            } else {
                chefSupermarketCrawler.getCookbook(this.threadName);
            }
        } catch (Exception e) {
            LOGGER.error("Downloading " + threadName + " failed." + e);
        }
    }

    public void start() {
        LOGGER.info("Start downloading " + threadName + " cookbook");
        if (thread == null) {
            thread = new Thread(this, threadName);
            thread.start();
        }
    }

    /**
     * Wait for the crawling process to finish.
     *
     * @throws InterruptedException when process is interrupted.
     */
    public void join() throws InterruptedException {
        if (thread != null) {
            this.thread.join();
        }
    }

    /**
     * This method checks if crawling process is running.
     *
     * @return Returns true if crawling process is not finished.
     */
    public boolean isAlive() {
        if (thread != null) {
            return this.thread.isAlive();
        } else {
            return false;
        }
    }

    public String getThreadName() {
        return threadName;
    }

    public String getCookbookDirectory() {
        return cookbookDirectory;
    }

    public String getTempDirectory() {
        return tempDirectory;
    }
}
