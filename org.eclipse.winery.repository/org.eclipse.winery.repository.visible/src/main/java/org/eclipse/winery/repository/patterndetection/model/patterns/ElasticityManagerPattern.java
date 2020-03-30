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

public class ElasticityManagerPattern {

    private static final String propertiesFilename = "patterndetection.properties";

    private Properties properties;

    private String virtualHardware;
    private String service;

    private String connectsTo;

    private SimpleDirectedGraph<PatternComponent, RelationshipEdge> pattern;

    public ElasticityManagerPattern() {
        properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFilename);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        virtualHardware = properties.getProperty("labelVirtualHardware");
        service = properties.getProperty("labelService");

        connectsTo = properties.getProperty("relationConnectsTo");

        pattern = new SimpleDirectedGraph<>(RelationshipEdge.class);

        PatternComponent virtualHardwareComponent = new PatternComponent(virtualHardware, 1, 1);
        PatternComponent serviceComponent = new PatternComponent(service, 1, 1);

        pattern.addVertex(virtualHardwareComponent);
        pattern.addVertex(serviceComponent);

        pattern.addEdge(serviceComponent, virtualHardwareComponent, new RelationshipEdge(service, virtualHardwareComponent, connectsTo));

    }

    public SimpleDirectedGraph<PatternComponent, RelationshipEdge> getPatternGraph() {
        return pattern;
    }
}
