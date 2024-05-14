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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.winery.common.json.JacksonProvider;
import org.eclipse.winery.edmm.model.EdmmMappingItem;
import org.eclipse.winery.edmm.model.EdmmType;

import io.github.edmm.model.component.Auth0;
import io.github.edmm.model.component.AwsAurora;
import io.github.edmm.model.component.AwsBeanstalk;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.Database;
import io.github.edmm.model.component.Dbaas;
import io.github.edmm.model.component.Dbms;
import io.github.edmm.model.component.Go;
import io.github.edmm.model.component.Mom;
import io.github.edmm.model.component.MongoDb;
import io.github.edmm.model.component.MongoDbSchema;
import io.github.edmm.model.component.MysqlDatabase;
import io.github.edmm.model.component.MysqlDbms;
import io.github.edmm.model.component.Paas;
import io.github.edmm.model.component.Platform;
import io.github.edmm.model.component.RabbitMq;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.component.Saas;
import io.github.edmm.model.component.SoftwareComponent;
import io.github.edmm.model.component.Tomcat;
import io.github.edmm.model.component.WebApplication;
import io.github.edmm.model.component.WebServer;
import io.github.edmm.model.relation.ConnectsTo;
import io.github.edmm.model.relation.DependsOn;
import io.github.edmm.model.relation.HostedOn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonBasedEdmmManager implements EdmmManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonBasedEdmmManager.class);

    private final File file;
    private final DataWrapper edmmTypes;

    public JsonBasedEdmmManager(File file) {
        Objects.requireNonNull(file);
        this.file = file;
        this.edmmTypes = this.loadEdmmTypesFromFile();

        // hopefully no race conditions occur :)
        if (getEdmmTypes().isEmpty()) {
            addDefaultEdmmTypes();
        }
    }

    private void addDefaultEdmmTypes() {
        List<EdmmType> defaultTypes = new ArrayList<>();
        Arrays.stream(new Class[] {
            Auth0.class,
            AwsAurora.class,
            AwsBeanstalk.class,
            Compute.class,
            Database.class,
            Dbaas.class,
            Dbms.class,
            Go.class,
            Mom.class,
            MongoDb.class,
            MongoDbSchema.class,
            MysqlDatabase.class,
            MysqlDbms.class,
            Paas.class,
            Platform.class,
            RabbitMq.class,
            RootComponent.class,
            Saas.class,
            SoftwareComponent.class,
            Tomcat.class,
            WebApplication.class,
            WebServer.class,
            DependsOn.class,
            ConnectsTo.class,
            HostedOn.class}).forEach(clazz -> defaultTypes.add(EdmmType.fromEntityClass(clazz)));
        this.setEdmmTypes(defaultTypes);
    }

    @Override
    public List<EdmmType> getEdmmTypes() {
        return this.edmmTypes.edmmTypes;
    }

    @Override
    public void setEdmmTypes(List<EdmmType> newTypes) {
        this.edmmTypes.edmmTypes = newTypes;
        this.save();
    }

    @Override
    public List<EdmmMappingItem> getOneToOneMappings() {
        return this.edmmTypes.oneToOneMapping;
    }

    @Override
    public void setOneToOneMappings(List<EdmmMappingItem> list) {
        Optional<EdmmMappingItem> mappingItem = list.stream().filter(i -> !getEdmmTypes().contains(i.edmmType)).findFirst();

        if (mappingItem.isPresent()) {
            throw new IllegalArgumentException("trying to add a mapping for a non-existing EDMM type: " +
                mappingItem.get().edmmType.getValue());
        }

        this.edmmTypes.oneToOneMapping = list;
        this.save();
    }

    private DataWrapper loadEdmmTypesFromFile() {
        DataWrapper edmmMappings = new DataWrapper();

        try {
            if (this.file.exists()) {
                edmmMappings = JacksonProvider.mapper.readValue(file, DataWrapper.class);
            }
        } catch (IOException e) {
            LOGGER.debug("Error while loading the edmm types file.", e);
            throw new RuntimeException();
        }

        return edmmMappings;
    }

    private void save() {
        try {
            if (!this.file.exists()) {
                if (this.file.getParentFile().mkdirs() || this.file.createNewFile()) {
                    LOGGER.debug("Created new EDMM types file at {}", this.file);
                } else {
                    LOGGER.error("Could not create EDMM types file at {}", this.file);
                }
            }

            JacksonProvider.mapper.writeValue(this.file, this.edmmTypes);
        } catch (IOException e) {
            LOGGER.debug("Could not save EDMM types to json file!", e);
        }
    }

    public static class DataWrapper {
        public List<EdmmMappingItem> oneToOneMapping;
        public List<EdmmType> edmmTypes;

        public DataWrapper() {
            this.oneToOneMapping = new ArrayList<>();
            this.edmmTypes = new ArrayList<>();
        }
    }
}
