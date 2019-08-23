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

package org.eclipse.winery.repository.backend.filebased;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.winery.common.edmm.EdmmMappingItem;
import org.eclipse.winery.repository.backend.EdmmManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonBasedEdmmManager implements EdmmManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonBasedEdmmManager.class);

    public final String ONE_TO_ONE = "oneToOneMapping";
    public final String TYPE_MAPPING = "edmmTypeMapping";

    private final File file;
    private final ObjectMapper objectMapper;
    private final Map<String, List<EdmmMappingItem>> edmmMappings;

    public JsonBasedEdmmManager(File file) {
        Objects.requireNonNull(file);
        this.file = file;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.edmmMappings = this.loadMappingsFromFile();
    }

    @Override
    public List<EdmmMappingItem> getOneToOneMappings() {
        return this.edmmMappings.get(ONE_TO_ONE);
    }

    @Override
    public void setOneToOneMappings(List<EdmmMappingItem> list) {
        this.edmmMappings.put(ONE_TO_ONE, list);
        this.save();
    }

    @Override
    public List<EdmmMappingItem> getTypeMappings() {
        return this.edmmMappings.get(TYPE_MAPPING);
    }

    @Override
    public void setTypeMappings(List<EdmmMappingItem> list) {
        this.edmmMappings.put(TYPE_MAPPING, list);
        this.save();
    }

    private Map<String, List<EdmmMappingItem>> loadMappingsFromFile() {
        Map<String, List<EdmmMappingItem>> edmmMappings = new HashMap<>();

        try {
            if (this.file.exists()) {
                TypeReference<HashMap<String, List<EdmmMappingItem>>> hashMapTypeReference =
                    new TypeReference<HashMap<String, List<EdmmMappingItem>>>() {
                    };
                edmmMappings = objectMapper.readValue(file, hashMapTypeReference);
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

            this.objectMapper.writeValue(this.file, this.edmmMappings);
        } catch (IOException e) {
            LOGGER.debug("Could not save EDMM Mappings to json file!", e);
        }
    }
}
