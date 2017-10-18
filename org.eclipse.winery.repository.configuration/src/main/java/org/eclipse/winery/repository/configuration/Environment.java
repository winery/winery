/**
 * Copyright (c) 2017 University of Stuttgart. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 and the Apache License 2.0 which both accompany this
 * distribution, and are available at http://www.eclipse.org/legal/epl-v20.html and
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors: 
 * - Lukas Harzenetter - initial API and implementation 
 * - Oliver Kopp - separate configuration types
 * - Michael Wurster - Add classloader fallback and fix 
 */
package org.eclipse.winery.repository.configuration;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Environment {

	static final String KEY_URL_TOPOLOGY_MODELER = "topologymodeler";
	static final String KEY_URL_BPMN4TOSCA_MODELER = "bpmn4toscamodelerBaseURI";

	private static final String KEY_GITHUB_CLIENT_ID = "gitHubClientId";
	private static final String KEY_GITHUB_CLIENT_SECRET = "gitHubClientSecret";

	private static final String KEY_GIT_AUTOCOMMIT = "repository.git.autocommit";

	private static final String KEY_REPOSITORY_PATH = "repositoryPath";

	private static final String KEY_JCLOUDS_CONTEXT_PROVIDER = "jclouds.context.provider";
	private static final String KEY_JCLOUDS_CONTEXT_IDENTITY = "jclouds.context.identity";
	private static final String KEY_JCLOUDS_CONTEXT_CREDENTIAL = "jclouds.context.credential";
	private static final String KEY_JCLOUDS_BLOBSTORE_LOCATION = "jclouds.blobstore.location";
	private static final String KEY_JCLOUDS_CONTAINERNAME = "jclouds.blobstore.container";
	private static final String KEY_JCLOUDS_END_POINT = "jclouds.blobstore.endpoint";

	// start with an empty configuration, without link to the file system
	private static Configuration CONFIGURATION = new BaseConfiguration();

	// self-managed here
	private static UrlConfiguration URL_CONFIGURATION = new UrlConfiguration();

	private static final Logger LOGGER = LoggerFactory.getLogger(Environment.class);

	static {
		final String filename = "winery.properties";
		URL wineryPropertiesResource = Environment.class.getClassLoader().getResource(filename);
		if (wineryPropertiesResource == null) {
			wineryPropertiesResource = Thread.currentThread().getContextClassLoader()
				.getResource(filename);
			if (wineryPropertiesResource == null) {
				wineryPropertiesResource = ClassLoader.getSystemClassLoader().getResource(filename);
			}
		}
		Configurations configs = new Configurations();
		try {
			CONFIGURATION = configs.properties(wineryPropertiesResource);
		} catch (ConfigurationException e) {
			LOGGER.debug("Could not load by using getResource", e);
		}
	}

	/**
	 * Prevent utility class from getting instantiated.
	 */
	private Environment() {
	}

	/**
	 * @return the version of Winery
	 */
	public static String getVersion() {
		return org.eclipse.winery.repository.configuration.Version.VERSION;
	}

	/**
	 * Overwrite configuration parameters by using the given file
	 *
	 * @param path a path pointing to a file where the configuration should be read from
	 */
	public static void copyConfiguration(Path path) throws Exception {
		Configurations configs = new Configurations();
		Configuration configuration = configs.properties(path.toFile());
		copyConfiguration(configuration);
	}

	/**
	 * Overwrite configuration parameters by using the given URL
	 *
	 * @param url a URL pointing to a file where the configuration should be read from
	 */
	public static void copyConfiguration(URL url) throws Exception {
		Configurations configs = new Configurations();
		Configuration configuration = configs.properties(url);
		copyConfiguration(configuration);
	}

	public static void copyConfiguration(Configuration configuration) {
		ConfigurationUtils.copy(configuration, CONFIGURATION);
		URL_CONFIGURATION.update(configuration);
	}

	public static Optional<GitBasedRepositoryConfiguration> getGitBasedRepositoryConfiguration() {
		if (CONFIGURATION.containsKey(KEY_GIT_AUTOCOMMIT)) {
			final FileBasedRepositoryConfiguration filebasedRepositoryConfiguration = getFilebasedRepositoryConfiguration().orElse(new FileBasedRepositoryConfiguration());
			return Optional.of(new GitBasedRepositoryConfiguration(CONFIGURATION.getBoolean(KEY_GIT_AUTOCOMMIT), filebasedRepositoryConfiguration));
		} else {
			return Optional.empty();
		}
	}

	public static Optional<JCloudsConfiguration> getJCloudsConfiguration() {
		if (CONFIGURATION.containsKey(KEY_JCLOUDS_CONTEXT_PROVIDER)) {
			return Optional.of(new JCloudsConfiguration(
					CONFIGURATION.getString(KEY_JCLOUDS_CONTEXT_IDENTITY),
					CONFIGURATION.getString(KEY_JCLOUDS_CONTEXT_CREDENTIAL),
					CONFIGURATION.getString(KEY_JCLOUDS_BLOBSTORE_LOCATION),
					CONFIGURATION.getString(KEY_JCLOUDS_CONTAINERNAME),
					CONFIGURATION.getString(KEY_JCLOUDS_END_POINT)));
		} else {
			return Optional.empty();
		}
	}

	public static Optional<FileBasedRepositoryConfiguration> getFilebasedRepositoryConfiguration() {
		if (CONFIGURATION.containsKey(KEY_REPOSITORY_PATH)) {
			final String configuredPath = CONFIGURATION.getString(KEY_REPOSITORY_PATH);
			final Path path = Paths.get(configuredPath);
			return Optional.of(new FileBasedRepositoryConfiguration(path));
		} else {
			return Optional.empty();
		}
	}

	public static Optional<GitHubConfiguration> getGitHubConfiguration() {
		if (CONFIGURATION.containsKey(KEY_GITHUB_CLIENT_ID) &&
				CONFIGURATION.containsKey(KEY_GITHUB_CLIENT_SECRET)) {
			return Optional.of(new GitHubConfiguration(
					CONFIGURATION.getString(KEY_GITHUB_CLIENT_ID),
					CONFIGURATION.getString(KEY_GITHUB_CLIENT_SECRET)));
		} else {
			return Optional.empty();
		}
	}

	/**
	 * @return a modifiable object of the current URL configuration
	 */
	public static UrlConfiguration getUrlConfiguration() {
		return URL_CONFIGURATION;
	}

	public static void setUrlConfiguration(UrlConfiguration urlConfiguration) {
		urlConfiguration.update(CONFIGURATION);
		URL_CONFIGURATION = urlConfiguration;
	}
}
