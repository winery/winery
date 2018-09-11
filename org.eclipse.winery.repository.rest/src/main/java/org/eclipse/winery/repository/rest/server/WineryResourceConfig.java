/*******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.server;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Path;

import com.sun.jersey.api.core.DefaultResourceConfig;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class WineryResourceConfig extends DefaultResourceConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(WineryResourceConfig.class);

    private static Set<Class<?>> classes = scan("org.eclipse.winery.repository.rest.resources");

    public WineryResourceConfig() {
        super(classes);
    }

    private static Set<Class<?>> scan(String... packages) {
        Set<Class<?>> classes = new HashSet<>();
        for (String p : packages) {
            Reflections r = new Reflections(p);
            r.getTypesAnnotatedWith(Path.class)
                .parallelStream()
                .forEach((clazz) -> {
                    LOGGER.info("New resource registered: " + clazz.getName());
                    classes.add(clazz);
                });
        }
        return classes;
    }
}
