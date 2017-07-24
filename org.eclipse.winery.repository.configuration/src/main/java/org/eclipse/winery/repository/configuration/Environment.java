/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 * Lukas Harzenetter - initial API and implementation
 */
package org.eclipse.winery.repository.configuration;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Environment {

	public static final Configuration CONFIGURATION = getConfiguration();

	private static final Logger LOGGER = LoggerFactory.getLogger(Environment.class);

	/**
	 * Prevent utility class from getting instantiated.
	 */
	private Environment() {

	}

	public static Configuration getConfiguration() {
		if (CONFIGURATION != null) {
			return CONFIGURATION;
		}

		ObjectMapper mapper = new ObjectMapper(new JsonFactory());
		try (InputStream inputStream = Environment.class.getResourceAsStream("/environments/config.json")) {
			return mapper.readValue(inputStream, Configuration.class);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
