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

package org.eclipse.winery.common.configuration;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.configuration2.YAMLConfiguration;

/**
 * This Class is used to create a JSON Object that is structured like the winery.yaml file. Therefore this class is a
 * structural copy of that file.
 */
public class UiConfigurationObject extends AbstractConfigurationObject {

    HashMap<String, Boolean> features;
    HashMap<String, String> endpoints;
    private final String key = "ui";
    private final String featurePrefix = key + ".features.";
    private final String endpointPrefix = key + ".endpoints.";

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
        HashMap<String, Boolean> features = new HashMap<>();
        HashMap<String, String> endpoints = new HashMap<>();
        Iterator<String> featureIterator = configuration.getKeys(featurePrefix);
        Iterator<String> endpointIterator = configuration.getKeys(endpointPrefix);
        featureIterator.forEachRemaining(key -> features.put(key.replace(featurePrefix, ""), configuration.getBoolean((key))));
        endpointIterator.forEachRemaining(key -> endpoints.put(key.replace(endpointPrefix, ""), configuration.getString(key)));
        this.features = features;
        this.endpoints = endpoints;
        this.configuration = configuration;
        initialize();
    }

    public HashMap<String, Boolean> getFeatures() {
        return features;
    }

    public HashMap<String, String> getEndpoints() {
        return endpoints;
    }

    @Override
    void save() {
        this.features.keySet().forEach(property -> configuration.setProperty(featurePrefix + property, this.features.get(property)));
        this.endpoints.keySet().forEach(property -> configuration.setProperty(endpointPrefix + property, this.endpoints.get(property)));
        Environment.getInstance().save();
    }

    @Override
    void update(YAMLConfiguration configuration) {
        this.configuration = configuration;
        HashMap<String, Boolean> features = new HashMap<>();
        HashMap<String, String> endpoints = new HashMap<>();
        Iterator<String> featureIterator = configuration.getKeys(featurePrefix);
        Iterator<String> endpointIterator = configuration.getKeys(endpointPrefix);
        featureIterator.forEachRemaining(key -> features.put(key.replace(featurePrefix, ""), configuration.getBoolean((key))));
        endpointIterator.forEachRemaining(key -> endpoints.put(key.replace(endpointPrefix, ""), configuration.getString(key)));
        this.features = features;
        this.endpoints = endpoints;
    }

    @Override
    void initialize() {

    }
}
