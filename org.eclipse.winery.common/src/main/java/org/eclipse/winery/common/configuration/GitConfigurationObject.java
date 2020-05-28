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

import org.apache.commons.configuration2.YAMLConfiguration;

public class GitConfigurationObject extends AbstractConfigurationObject {

    private static final String key = "repository.git.";
    private String clientSecret;
    private String password;
    private String clientID;
    private String username;
    private boolean autocommit;

    GitConfigurationObject(YAMLConfiguration configuration) {
        this.update(configuration);
        initialize();
    }

    @Override
    void save() {
        configuration.setProperty(key + "clientSecret", this.getClientSecret());
        configuration.setProperty(key + "password", this.getPassword());
        configuration.setProperty(key + "clientID", this.getClientID());
        configuration.setProperty(key + "username", this.getUsername());
        configuration.setProperty(key + "autocommit", this.isAutocommit());
        Environment.getInstance().save();
    }

    @Override
    void update(YAMLConfiguration updatedConfiguration) {
        this.configuration = updatedConfiguration;
        this.setClientSecret(configuration.getString(key + "clientSecret"));
        this.setPassword(configuration.getString(key + "password"));
        this.setClientID(configuration.getString(key + "clientID"));
        this.setUsername(configuration.getString(key + "username"));
        this.setAutocommit(configuration.getBoolean(key + "autocommit", false));
    }

    @Override
    void initialize() {

    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAutocommit() {
        return autocommit;
    }

    public void setAutocommit(boolean autocommit) {
        this.autocommit = autocommit;
    }
}
