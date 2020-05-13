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

package org.eclipse.winery.edmm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.winery.common.json.JacksonProvider;
import org.eclipse.winery.edmm.model.EdmmMappingItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonBasedEdmmManager implements EdmmManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonBasedEdmmManager.class);

    private final File file;
    private final MappingsWrapper edmmMappings;

    public JsonBasedEdmmManager(File file) {
        Objects.requireNonNull(file);
        this.file = file;
        this.edmmMappings = this.loadMappingsFromFile();
    }

    @Override
    public List<EdmmMappingItem> getOneToOneMappings() {
        return this.edmmMappings.oneToOneMapping;
    }

    @Override
    public void setOneToOneMappings(List<EdmmMappingItem> list) {
        this.edmmMappings.oneToOneMapping = list;
        this.save();
    }

    @Override
    public List<EdmmMappingItem> getTypeMappings() {
        return this.edmmMappings.edmmTypeMapping;
    }

    @Override
    public void setTypeMappings(List<EdmmMappingItem> list) {
        this.edmmMappings.edmmTypeMapping = list;
        this.save();
    }

    private MappingsWrapper loadMappingsFromFile() {
        MappingsWrapper edmmMappings = new MappingsWrapper();

        try {
            if (this.file.exists()) {
                edmmMappings = JacksonProvider.mapper.readValue(file, MappingsWrapper.class);
            }
        } catch (IOException e) {
            LOGGER.debug("Error while loading the namespace file.", e);
            throw new RuntimeException();
        }

        return edmmMappings;
    }

    private void save() {
        try {
            if (!this.file.exists()) {
                if (this.file.getParentFile().mkdirs() || this.file.createNewFile()) {
                    LOGGER.debug("Created new EDMM Mappings file at {}", this.file);
                } else {
                    LOGGER.error("Could not create EDMM Mappings file at {}", this.file);
                }
            }

            JacksonProvider.mapper.writeValue(this.file, this.edmmMappings);
        } catch (IOException e) {
            LOGGER.debug("Could not save EDMM Mappings to json file!", e);
        }
    }

    public static class MappingsWrapper {
        public List<EdmmMappingItem> oneToOneMapping;
        public List<EdmmMappingItem> edmmTypeMapping;

        public MappingsWrapper() {
            this.oneToOneMapping = new ArrayList<>();
            this.edmmTypeMapping = new ArrayList<>();
        }
    }
}
