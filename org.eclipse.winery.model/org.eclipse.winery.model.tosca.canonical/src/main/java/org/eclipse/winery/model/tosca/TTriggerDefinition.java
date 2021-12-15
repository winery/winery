/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.tosca;

import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Class to represent a trigger definition in TOSCA YAML.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TriggerDefinition")
public class TTriggerDefinition implements HasName {
    @XmlElement
    private String name;
    @XmlElement
    private String description;
    @XmlTransient
    private String event;
    @XmlTransient
    private TEventFilterDefinition targetFilter;
    @XmlTransient
    private List<TActivityDefinition> action;

    @Deprecated // used for XML deserialization of API request content
    public TTriggerDefinition() {
    }

    private TTriggerDefinition(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.event = builder.event;
        this.targetFilter = builder.targetFilter;
        this.action = builder.action;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public String getEvent() {
        return event;
    }

    public TEventFilterDefinition getTargetFilter() {
        return targetFilter;
    }

    public List<TActivityDefinition> getAction() {
        return action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TTriggerDefinition that = (TTriggerDefinition) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public static class Builder {
        private final String name;
        private final String event;
        private String description;
        private TEventFilterDefinition targetFilter;
        private List<TActivityDefinition> action;

        public Builder(String name, String event) {
            this.name = name;
            this.event = event;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder targetFilter(TEventFilterDefinition targetFilter) {
            this.targetFilter = targetFilter;
            return this;
        }

        public Builder action(List<TActivityDefinition> actions) {
            this.action = actions;
            return this;
        }

        public TTriggerDefinition build() {
            return new TTriggerDefinition(this);
        }
    }
}
