/*******************************************************************************
 * Copyright (c) 2012-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.backend.filebased;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * We do not count loads and saves as in {@link org.apache.commons.configuration.builder.AutoSaveListener}, because
 * ConfigurationListener is not aware of such things
 */
class AutoSaveListener implements EventListener<ConfigurationEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoSaveListener.class);

    private final Path path;
    private final PropertiesConfiguration configuration;

    /**
     * @param path          the file path to write to
     * @param configuration the configuration, where the change events come from. This is needed as
     *                      <code>event.getSource()</code> does not work
     */
    public AutoSaveListener(Path path, PropertiesConfiguration configuration) {
        this.path = path;
        this.configuration = configuration;
    }

    @Override
    public void onEvent(ConfigurationEvent event) {
        if (!event.isBeforeUpdate()) {
            try {
                if (!Files.exists(this.path.getParent())) {
                    Files.createDirectories(this.path.getParent());
                }
            } catch (IOException ce) {
                LOGGER.error("Could not update properties file", ce);
                return;
            }
            try (OutputStream out = Files.newOutputStream(this.path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                OutputStreamWriter writer = new OutputStreamWriter(out);
                this.configuration.write(writer);
            } catch (ConfigurationException | IOException ce) {
                LOGGER.error("Could not update properties file", ce);
            }
        }
    }
}
