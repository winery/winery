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

package org.eclipse.winery.repository.patterndetection.model.patterns;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.winery.repository.patterndetection.model.PatternComponent;
import org.eclipse.winery.repository.patterndetection.model.RelationshipEdge;

import org.jgrapht.graph.SimpleDirectedGraph;

public class ExecutionEnvironmentPattern2 {

    private static final String propertiesFilename = "patterndetection.properties";

    private Properties properties;

    private String os;
    private String server;
    private String application;
    private String hostedOn;
    private String deployedOn;

    private SimpleDirectedGraph<PatternComponent, RelationshipEdge> pattern;

    public ExecutionEnvironmentPattern2() {
        properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFilename);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server = properties.getProperty("labelServer");
        os = properties.getProperty("labelOS");
        application = properties.getProperty("labelApp");

        hostedOn = properties.getProperty("relationHostedOn");
        deployedOn = properties.getProperty("relationDeployedOn");

        pattern = new SimpleDirectedGraph<>(RelationshipEdge.class);

        PatternComponent operatingSystem = new PatternComponent(os, 1, 1);
        PatternComponent serverComponent = new PatternComponent(server, 1, Integer.MAX_VALUE);
        PatternComponent appComponent1 = new PatternComponent(application, 1, 1);
        PatternComponent appComponent2 = new PatternComponent(application, 1, 1);

        pattern.addVertex(operatingSystem);
        pattern.addVertex(serverComponent);
        pattern.addVertex(appComponent1);
        pattern.addVertex(appComponent2);

        pattern.addEdge(serverComponent, operatingSystem, new RelationshipEdge(serverComponent, operatingSystem, hostedOn));
        pattern.addEdge(appComponent1, serverComponent, new RelationshipEdge(appComponent1, serverComponent, deployedOn));
        pattern.addEdge(appComponent2, serverComponent, new RelationshipEdge(appComponent2, serverComponent, deployedOn));
    }

    public SimpleDirectedGraph<PatternComponent, RelationshipEdge> getPatternGraph() {
        return pattern;
    }
}
