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

import org.eclipse.winery.repository.patterndetection.model.PatternComponent;
import org.eclipse.winery.repository.patterndetection.model.RelationshipEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RelationalDatabasePattern {

    private static final String propertiesFilename = "patterndetection.properties";

    private Properties properties;

    private String os;
    private String storage;

    private String hostedOn;

    private DirectedGraph<PatternComponent, RelationshipEdge> pattern;

    public RelationalDatabasePattern() {
        properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFilename);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        os = properties.getProperty("labelOS");
        storage = properties.getProperty("labelStorage");

        hostedOn = properties.getProperty("relationHostedOn");

        pattern = new DefaultDirectedGraph<>(RelationshipEdge.class);

        PatternComponent operatingSystem = new PatternComponent(os, 1, 1);
        PatternComponent storageComponent1 = new PatternComponent(storage, 1, 1);
        PatternComponent storageComponent2 = new PatternComponent(storage, 1, 1);

        pattern.addVertex(operatingSystem);
        pattern.addVertex(storageComponent1);
        pattern.addVertex(storageComponent2);

        pattern.addEdge(storageComponent1, operatingSystem, new RelationshipEdge(storageComponent1, operatingSystem, hostedOn));
        pattern.addEdge(storageComponent2, storageComponent1, new RelationshipEdge(storageComponent2, storageComponent1, hostedOn));

    }

    public DirectedGraph<PatternComponent, RelationshipEdge> getPatternGraph() {
        return pattern;
    }
}
