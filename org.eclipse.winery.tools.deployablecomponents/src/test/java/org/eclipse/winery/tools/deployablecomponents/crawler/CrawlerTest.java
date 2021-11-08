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

import java.util.List;

import org.eclipse.winery.tools.deployablecomponents.commons.Component;
import org.eclipse.winery.tools.deployablecomponents.commons.Dockerfile;
import org.eclipse.winery.tools.deployablecomponents.fileanalyzer.CommandAnalyzer.ApkAnalyzer;
import org.eclipse.winery.tools.deployablecomponents.fileanalyzer.CommandAnalyzer.AptgetAnalyzer;
import org.eclipse.winery.tools.deployablecomponents.fileanalyzer.CommandAnalyzer.NpmAnalyzer;
import org.eclipse.winery.tools.deployablecomponents.fileanalyzer.CommandAnalyzer.Pip3Analyzer;
import org.eclipse.winery.tools.deployablecomponents.fileanalyzer.CommandAnalyzer.PipAnalyzer;
import org.eclipse.winery.tools.deployablecomponents.fileanalyzer.CommandAnalyzer.YumAnalyzer;
import org.eclipse.winery.tools.deployablecomponents.fileanalyzer.Fileanalyzer;
import org.eclipse.winery.tools.deployablecomponents.fileanalyzer.Filesplitter;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CrawlerTest {

    // integration test
    // needs internet access
    @Test
    @Disabled("Disabled to speed up travis build")
    void TestGithubCrawler() {
        Crawler crawler = new Crawler(CrawlerType.GITHUB, "", "", "");
        crawler.setStartPoint(114777405);
        crawler.crawlNewDockerfiles(1);
        Dockerfile dockerfile = crawler.nextDockerfile();
        assertEquals("Dockerfile", dockerfile.getPath());
        assertEquals("pegaops/amazonlinux", dockerfile.getRepoName());
        assertEquals("FROM amazonlinux:2\n" +
            "RUN yum update -y && \\\n" +
            "    yum install -y \\\n" +
            "    nano \\\n" +
            "    aws-cli\n", dockerfile.getContent());
    }

    @Test
    void TestFilesplitter() {
        Dockerfile dockerfile = new Dockerfile("test/test", "FROM abc\nRUN def", "test");
        List<String> lines = new Filesplitter().splitDockerfile(dockerfile);
        assertEquals("FROM abc", lines.get(0));
        assertEquals("RUN def", lines.get(1));
    }

    @Test
    void TestAnalyzer() {
        Dockerfile dockerfile = new Dockerfile("test/test", "FROM abc:1\nRUN apt-get install def=2", "test");
        List<Pair<Component, List<Component>>> result = new Fileanalyzer().analyseDockerfile(dockerfile);

        assertEquals("abc", result.get(0).getKey().getName());
        assertEquals("1", result.get(0).getKey().getVersion());
        assertEquals("equals", result.get(0).getKey().getVersionOperator());

        assertEquals("def", result.get(0).getValue().get(0).getName());
        assertEquals("2", result.get(0).getValue().get(0).getVersion());
        assertEquals("equals", result.get(0).getValue().get(0).getVersionOperator());
    }

    @Test
    void TestApkCommandAnalyzer() {
        ApkAnalyzer analyzer = new ApkAnalyzer();
        List<Component> result1 = analyzer.analyze("apk add -abc winery@1.2.3 depComp");
        List<Component> result2 = analyzer.analyze("apk addr -def yreniw@3.2.1 pmoCped");

        String version = "1.2.3";
        String name = "winery";
        Component component1 = new Component(name, version, "equals");

        version = "undefined";
        name = "depComp";
        Component component2 = new Component(name, version, "equals");

        assertEquals(component1, result1.get(0));
        assertEquals(component2, result1.get(1));
        assertTrue(result2.isEmpty());
    }

    @Test
    void TestAptgetCommandAnalyzer() {
        AptgetAnalyzer analyzer = new AptgetAnalyzer();
        List<Component> result1 = analyzer.analyze("apt-get install -abc winery=1.2.3 depComp");
        List<Component> result2 = analyzer.analyze("apt-get installe -def yreniw=3.2.1 pmoCped");

        String version = "1.2.3";
        String name = "winery";
        Component component1 = new Component(name, version, "equals");

        version = "undefined";
        name = "depComp";
        Component component2 = new Component(name, version, "equals");

        assertEquals(component1, result1.get(0));
        assertEquals(component2, result1.get(1));
        assertTrue(result2.isEmpty());
    }

    @Test
    void TestNpmCommandAnalyzer() {
        NpmAnalyzer analyzer = new NpmAnalyzer();
        List<Component> result1 = analyzer.analyze("npm i -abc winery@1.2.3 depComp");
        List<Component> result2 = analyzer.analyze("npm ii -def yreniw@3.2.1 pmoCped");

        String version = "1.2.3";
        String name = "winery";
        Component component1 = new Component(name, version, "equals");

        version = "undefined";
        name = "depComp";
        Component component2 = new Component(name, version, "equals");

        assertEquals(component1, result1.get(0));
        assertEquals(component2, result1.get(1));
        assertTrue(result2.isEmpty());
    }

    @Test
    void TestPipCommandAnalyzer() {
        PipAnalyzer analyzer = new PipAnalyzer();
        List<Component> result1 = analyzer.analyze("pip install -abc winery>=1.2.3 depComp");
        List<Component> result2 = analyzer.analyze("pip iinstall -def yreniw==3.2.1 pmoCped");

        String version = "1.2.3";
        String name = "winery";
        Component component1 = new Component(name, version, ">=");

        version = "undefined";
        name = "depComp";
        Component component2 = new Component(name, version, "undefined");

        assertEquals(component1, result1.get(0));
        assertEquals(component2, result1.get(1));
        assertTrue(result2.isEmpty());
    }

    @Test
    void TestPip3CommandAnalyzer() {
        Pip3Analyzer analyzer = new Pip3Analyzer();
        List<Component> result1 = analyzer.analyze("pip3 install -abc winery>=1.2.3 depComp");
        List<Component> result2 = analyzer.analyze("pip3 iinstall -def yreniw==3.2.1 pmoCped");

        String version = "1.2.3";
        String name = "winery";
        Component component1 = new Component(name, version, ">=");

        version = "undefined";
        name = "depComp";
        Component component2 = new Component(name, version, "undefined");

        assertEquals(component1, result1.get(0));
        assertEquals(component2, result1.get(1));
        assertTrue(result2.isEmpty());
    }

    @Test
    void TestYumCommandAnalyzer() {
        YumAnalyzer analyzer = new YumAnalyzer();
        List<Component> result1 = analyzer.analyze("yum install -abc winery-1.2.3 depComp");
        List<Component> result2 = analyzer.analyze("yum iinstall -def yreniw-3.2.1 pmoCped");

        String version = "1.2.3";
        String name = "winery";
        Component component1 = new Component(name, version, "equals");

        version = "undefined";
        name = "depComp";
        Component component2 = new Component(name, version, "equals");

        assertEquals(component1, result1.get(0));
        assertEquals(component2, result1.get(1));
        assertTrue(result2.isEmpty());
    }
}
