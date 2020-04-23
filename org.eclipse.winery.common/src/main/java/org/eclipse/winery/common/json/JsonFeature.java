/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.common.json;

import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.glassfish.jersey.internal.util.PropertiesHelper;

import static org.glassfish.jersey.internal.InternalProperties.JSON_FEATURE;

public class JsonFeature implements Feature {

    @Override
    public boolean configure(FeatureContext featureContext) {
        featureContext.register(JacksonProvider.class, MessageBodyReader.class, MessageBodyWriter.class);

        final Configuration config = featureContext.getConfiguration();
        // Disables discoverability of org.glassfish.jersey.jackson.JacksonFeature
        featureContext.property(
            PropertiesHelper.getPropertyNameForRuntime(JSON_FEATURE,
                config.getRuntimeType()), JSON_FEATURE);
        
        return true;
    }
}
