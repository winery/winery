/**
 * Copyright (c) 2017 Marvin Wohlfarth.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
package org.eclipse.winery.repository.patterndetection.model.patterns;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.winery.repository.patterndetection.model.RelationshipEdge;
import org.eclipse.winery.repository.patterndetection.model.PatternComponent;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

public class EnvironmentBasedAvailabilityPattern {
	private static final String propertiesFilename = "patterndetection.properties";

	private Properties properties;

	private String virtualHardware;
	private String os;
	private String hostedOn;

	private DirectedGraph<PatternComponent, RelationshipEdge> pattern;

	public EnvironmentBasedAvailabilityPattern() {
		properties = new Properties();
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFilename);
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		os = properties.getProperty("labelOS");
		virtualHardware = properties.getProperty("labelVirtualHardware");
		hostedOn = properties.getProperty("relationHostedOn");

		pattern = new DefaultDirectedGraph<>(RelationshipEdge.class);

		PatternComponent virtualHardwareComponent = new PatternComponent(virtualHardware, 1, 1);
		PatternComponent operatingSystem = new PatternComponent(os, 1, 1);

		pattern.addVertex(operatingSystem);
		pattern.addVertex(virtualHardwareComponent);

		pattern.addEdge(operatingSystem, virtualHardwareComponent, new RelationshipEdge(operatingSystem, virtualHardwareComponent, hostedOn));
	}

	public DirectedGraph<PatternComponent, RelationshipEdge> getPatternGraph() {
		return pattern;
	}
}
