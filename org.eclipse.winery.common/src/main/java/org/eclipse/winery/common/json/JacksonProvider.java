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

import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.Annotations;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class JacksonProvider extends JacksonJaxbJsonProvider {

    public static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(JacksonProvider.class);

    static {
        logger.info("Initializing json mapper...");

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setVisibility(
            mapper.getVisibilityChecker()
                .with(JsonAutoDetect.Visibility.ANY)
                .withFieldVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
        );
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        logger.info("Initialized JasonMapper with VisibilityChecker {}", mapper.getVisibilityChecker());
    }

    /**
     * Custom Jackson json provider to configure the output by our own.
     * <p>
     * See also https://stackoverflow.com/a/30082203/6592788.
     */
    public JacksonProvider() {
        super(Annotations.JACKSON);
        setMapper(mapper);
    }
}
