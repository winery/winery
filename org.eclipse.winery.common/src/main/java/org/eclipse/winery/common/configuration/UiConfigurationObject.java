/*******************************************************************************
 * Copyright (c) 2019-2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.common.configuration;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.eclipse.winery.common.configuration.RepositoryConfigurationObject.RepositoryProvider.YAML;

/**
 * This Class is used to create a JSON Object that is structured like the winery.yaml file. Therefore this class is a
 * structural copy of that file.
 */
public class UiConfigurationObject extends AbstractConfigurationObject {

    public static String apiUrlKey = "repositoryApiUrl";

    private static final Logger logger = LoggerFactory.getLogger(UiConfigurationObject.class);

    private static final String key = "ui";
    private static final String featurePrefix = key + ".features.";
    private static final String endpointPrefix = key + ".endpoints.";

    private Map<String, Boolean> features;
    private Map<String, String> endpoints;

    /**
     * Required for REST API
     */
    public UiConfigurationObject() {
        if (Environment.getInstance().checkConfigurationForUpdate()) {
            Environment.getInstance().updateConfig();
        }
        this.configuration = Environment.getInstance().getConfiguration();
    }

    UiConfigurationObject(YAMLConfiguration configuration) {
        this.update(configuration);
        initialize();
    }

    public Map<String, Boolean> getFeatures() {
        return features;
    }

    public Map<String, String> getEndpoints() {
        return endpoints;
    }

    @Override
    void save() {
        this.features.keySet().stream()
            .filter(p -> !YAML.toString().equals(p)
            ).forEach(property -> configuration.setProperty(featurePrefix + property, this.features.get(property)));
        this.endpoints.keySet()
            .forEach(property -> configuration.setProperty(endpointPrefix + property, this.endpoints.get(property)));
        Environment.getInstance().save();
    }

    @Override
    void update(YAMLConfiguration configuration) {
        this.configuration = configuration;
        Map<String, Boolean> features = new HashMap<>();
        Map<String, String> endpoints = new HashMap<>();
        Iterator<String> featureIterator = this.configuration.getKeys(featurePrefix);
        Iterator<String> endpointIterator = this.configuration.getKeys(endpointPrefix);
        featureIterator.forEachRemaining(key -> features.put(key.replace(featurePrefix, ""), this.configuration.getBoolean((key))));
        endpointIterator.forEachRemaining(key -> endpoints.put(key.replace(endpointPrefix, ""), this.configuration.getString(key)));
        final String providerAsString = this.configuration.getString(RepositoryConfigurationObject.getProviderConfigurationKey());

        if (YAML.toString().equals(providerAsString)) {
            features.put(YAML.toString(), true);
        } else {
            // closed-world assumption ... apparently.
            features.put(YAML.toString(), false);
        }

        this.features = features;
        this.endpoints = endpoints;
    }

    @Override
    void initialize() {
        InputStream is = null;
        YAMLConfiguration defaultConfiguration = new YAMLConfiguration();
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream("winery.yml");
            defaultConfiguration.read(is);
        } catch (ConfigurationException e) {
            logger.error("Error loading default configuration", e);
        } finally {
            IOUtils.closeQuietly(is);
        }
        Iterator<String> defaultEndpoints = defaultConfiguration.getKeys("ui.endpoints");
        while (defaultEndpoints.hasNext()) {
            String defaultEndpoint = defaultEndpoints.next();
            String defaultValue = defaultConfiguration.getString(defaultEndpoint);
            String key = defaultEndpoint.replace("ui.endpoints.", "");
            this.endpoints.putIfAbsent(key, defaultValue);
        }
    }
}
