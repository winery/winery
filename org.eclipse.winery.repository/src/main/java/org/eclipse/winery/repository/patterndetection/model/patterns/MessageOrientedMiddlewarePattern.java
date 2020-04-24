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

public class MessageOrientedMiddlewarePattern {

    private static final String propertiesFilename = "patterndetection.properties";

    private Properties properties;

    private String os;
    private String application;
    private String messaging;

    private String hostedOn;
    private String connectsTo;

    private SimpleDirectedGraph<PatternComponent, RelationshipEdge> pattern;

    public MessageOrientedMiddlewarePattern() {
        properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFilename);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        os = properties.getProperty("labelOS");
        messaging = properties.getProperty("labelMessaging");
        application = properties.getProperty("labelApp");

        hostedOn = properties.getProperty("relationHostedOn");
        connectsTo = properties.getProperty("relationConnectsTo");

        pattern = new SimpleDirectedGraph<>(RelationshipEdge.class);

        PatternComponent operatingSystem = new PatternComponent(os, 1, 1);
        PatternComponent appComponent = new PatternComponent(application, 1, 1);
        PatternComponent messagingComponent1 = new PatternComponent(messaging, 1, 1);
        PatternComponent messagingComponent2 = new PatternComponent(messaging, 1, 1);

        pattern.addVertex(operatingSystem);
        pattern.addVertex(messagingComponent1);
        pattern.addVertex(messagingComponent2);
        pattern.addVertex(appComponent);

        pattern.addEdge(messagingComponent1, operatingSystem, new RelationshipEdge(messagingComponent1, operatingSystem, hostedOn));
        pattern.addEdge(messagingComponent2, messagingComponent1, new RelationshipEdge(messagingComponent2, messagingComponent1, hostedOn));

        pattern.addEdge(appComponent, messagingComponent2, new RelationshipEdge(appComponent, messagingComponent2, connectsTo));
    }

    public SimpleDirectedGraph<PatternComponent, RelationshipEdge> getPatternGraph() {
        return pattern;
    }
}
