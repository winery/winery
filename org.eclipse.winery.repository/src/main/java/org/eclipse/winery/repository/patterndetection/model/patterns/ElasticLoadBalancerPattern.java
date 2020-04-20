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

public class ElasticLoadBalancerPattern {

    private static final String propertiesFilename = "patterndetection.properties";

    private Properties properties;

    private String virtualHardware;
    private String os;
    private String server;
    private String application;
    private String service;
    private String hostedOn;
    private String connectsTo;
    private String deployedOn;

    private SimpleDirectedGraph<PatternComponent, RelationshipEdge> pattern;

    public ElasticLoadBalancerPattern() {
        properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFilename);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server = properties.getProperty("labelServer");
        os = properties.getProperty("labelOS");
        virtualHardware = properties.getProperty("labelVirtualHardware");
        application = properties.getProperty("labelApp");
        service = properties.getProperty("labelService");

        hostedOn = properties.getProperty("relationHostedOn");
        connectsTo = properties.getProperty("relationConnectsTo");
        deployedOn = properties.getProperty("relationDeployedOn");

        pattern = new SimpleDirectedGraph<>(RelationshipEdge.class);

        PatternComponent virtualHardwareComponent = new PatternComponent(virtualHardware, 1, 1);
        PatternComponent operatingSystem = new PatternComponent(os, 1, 1);
        PatternComponent serverComponent = new PatternComponent(server, 1, 1);
        PatternComponent appComponent1 = new PatternComponent(application, 1, 1);
        PatternComponent appComponent2 = new PatternComponent(application, 1, 1);
        PatternComponent serviceComponent1 = new PatternComponent(service, 1, 1);
        PatternComponent serviceComponent2 = new PatternComponent(service, 1, 1);

        pattern.addVertex(operatingSystem);
        pattern.addVertex(serviceComponent1);
        pattern.addVertex(virtualHardwareComponent);
        pattern.addVertex(serverComponent);
        pattern.addVertex(serviceComponent2);
        pattern.addVertex(appComponent1);
        pattern.addVertex(appComponent2);

        pattern.addEdge(operatingSystem, virtualHardwareComponent, new RelationshipEdge(operatingSystem, virtualHardwareComponent, hostedOn));
        pattern.addEdge(serviceComponent1, virtualHardwareComponent, new RelationshipEdge(serviceComponent1, virtualHardwareComponent, connectsTo));

        pattern.addEdge(serverComponent, operatingSystem, new RelationshipEdge(serverComponent, operatingSystem, hostedOn));

        pattern.addEdge(appComponent1, serverComponent, new RelationshipEdge(appComponent1, operatingSystem, deployedOn));
        pattern.addEdge(appComponent2, serverComponent, new RelationshipEdge(appComponent2, operatingSystem, deployedOn));

        pattern.addEdge(serviceComponent2, appComponent1, new RelationshipEdge(serviceComponent2, appComponent1, connectsTo));
        pattern.addEdge(serviceComponent2, appComponent2, new RelationshipEdge(serviceComponent2, appComponent2, connectsTo));
    }

    public SimpleDirectedGraph<PatternComponent, RelationshipEdge> getPatternGraph() {
        return pattern;
    }
}
