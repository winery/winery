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
package org.eclipse.winery.model.tosca.kvproperties;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PropertyDefinitionKV implements Serializable {

    private String key;
    private String type;
    private Boolean required;
    private String defaultValue;
    private String description;
    
    private ConstraintClauseKVList constraintList;

    public PropertyDefinitionKV() {
        super();
    }

    public PropertyDefinitionKV(String key, String type) {
        super();
        this.setKey(key);
        this.setType(type);
    }

    public PropertyDefinitionKV(String key, String type, Boolean required, String defaultValue) {
        this(key, type);
        this.setRequired(required);
        this.setDefaultValue(defaultValue);
    }

    public PropertyDefinitionKV(String key, String type, Boolean required, String defaultValue, String description) {
        this(key, type, required, defaultValue);
        this.setDescription(description);
    }
    
    public PropertyDefinitionKV(String key, String type, Boolean required, String defaultValue, String description, ConstraintClauseKVList constraints) {
        this(key, type, required, defaultValue, description);
        this.setConstraints(constraints);
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        this.key = key;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        if (type == null) {
            throw new IllegalArgumentException();
        }
        this.type = type;
    }

    public Boolean isRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ConstraintClauseKVList getConstraints() {
        return this.constraintList;
    }
    public void setConstraints(ConstraintClauseKVList constraintList) {
        this.constraintList = constraintList;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyDefinitionKV that = (PropertyDefinitionKV) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return this.key.hashCode();
    }
}
