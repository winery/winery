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

/**
 * This Class is used to create a JSON Object that is structured like the winery.yaml file. Therefore this class is a
 * structural copy of that file.
 */
public class ConfigurationObject {
    private HashMap<String, Boolean> features;
    private HashMap<String, String> endpoints;

    ConfigurationObject(HashMap<String, Boolean> featureMap, HashMap<String, String> endpointsMap) {
        this.features = featureMap;
        this.endpoints = endpointsMap;
    }

    public ConfigurationObject() {
    }

    public HashMap<String, Boolean> getFeatures() {
        return features;
    }

    public HashMap<String, String> getEndpoints() {
        return endpoints;
    }
    
}
