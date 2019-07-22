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

package org.eclipse.winery.tools.deployablecomponents;

import javafx.util.Pair;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.tools.deployablecomponents.commons.Component;
import org.eclipse.winery.tools.deployablecomponents.commons.Dockerfile;
import org.eclipse.winery.tools.deployablecomponents.crawler.Crawler;
import org.eclipse.winery.tools.deployablecomponents.crawler.CrawlerType;
import org.eclipse.winery.tools.deployablecomponents.fileanalyzer.Fileanalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeployableComponents {

    private final Crawler crawler;
    private final Fileanalyzer analyzer;

    private final Map<Component, List<Pair<Component, Integer>>> foundComponents = new HashMap<>();

    private boolean crawlStatus = false;
    private final int CRAWL_AT_ONCE = 1;
    public static final int MAX_FAILED_CRAWLER_REQUESTS = 10;

    public DeployableComponents(CrawlerType type, String serviceName, String serviceToken, String localCopyPath) {
        crawler = new Crawler(type, serviceName, serviceToken, localCopyPath);
        analyzer = new Fileanalyzer();
    }

    public void setCrawlPoint(int number) {
        crawler.setStartPoint(number);
    }

    public void start() {
        crawlStatus = true;
        execute();
    }

    private void execute() {
        if (crawlStatus) {
            int crawledFileCount = crawler.crawlNewDockerfiles(CRAWL_AT_ONCE);

            // Immediately start next crawl
            Runnable nextTask = this::execute;
            Thread nextThread = new Thread(nextTask);
            nextThread.start();

            for (int i = 0; i < crawledFileCount; i++) {
                Dockerfile newDockerfile = crawler.nextDockerfile();
                for (Pair<Component, List<Component>> newComponents : analyzer.analyseDockerfile(newDockerfile)) {
                    addComponentsToLists(newComponents);
                }
            }
        }
    }

    public void stop() {
        crawlStatus = false;
    }

    private void addComponentsToLists(Pair<Component, List<Component>> newComponents) {
        Component newBaseComponent = newComponents.getKey();
        if (foundComponents.containsKey(newBaseComponent)) {
            List<Pair<Component, Integer>> oldValue = foundComponents.get(newBaseComponent);
            // increase counter of known components
            for (int i = 0; i < oldValue.size(); i++) {
                Component oldComponent = oldValue.get(i).getKey();
                if (newComponents.getValue().contains(oldComponent)) {
                    Pair<Component, Integer> newEntry = new Pair<>(oldValue.get(i).getKey(), oldValue.get(i).getValue() + 1);
                    // remove and add (update) at the same index to not break the loop!
                    oldValue.remove(i);
                    oldValue.add(i, newEntry);
                    newComponents.getValue().remove(oldComponent);
                }
            }
            // add components, which are not known yet (but base component is known)
            for (Component newComponent : newComponents.getValue()) {
                oldValue.add(new Pair<>(newComponent, 1));
            }
            foundComponents.put(newBaseComponent, oldValue);
        } else {
            // add not known base component
            List<Pair<Component, Integer>> newEntries = new ArrayList<>();
            for (Component newComponent : newComponents.getValue()) {
                newEntries.add(new Pair<>(newComponent, 1));
            }
            foundComponents.put(newBaseComponent, newEntries);
        }
    }

    public Map<Component, List<Pair<Component, Integer>>> getFoundComponents() {
        return foundComponents;
    }

    public List<TNodeType> asToscaModel() {
        return new DeployableComponentsToscaConverter().convertToToscaModel(foundComponents);
    }
}
