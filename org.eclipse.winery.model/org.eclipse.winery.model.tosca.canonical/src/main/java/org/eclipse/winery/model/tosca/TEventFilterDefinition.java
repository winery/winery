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

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Class to represent an event filter definition in TOSCA YAML.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventFilterDefinition")
public class TEventFilterDefinition {
    @XmlElement
    private String node;
    @XmlTransient
    private String requirement;
    @XmlTransient
    private String capability;

    @Deprecated // used for XML deserialization of API request content
    public TEventFilterDefinition() {
    }

    private TEventFilterDefinition(Builder builder) {
        this.node = builder.node;
        this.requirement = builder.requirement;
        this.capability = builder.capability;
    }

    public String getNode() {
        return node;
    }

    public String getRequirement() {
        return requirement;
    }

    public String getCapability() {
        return capability;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TEventFilterDefinition that = (TEventFilterDefinition) o;
        return Objects.equals(node, that.node);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node);
    }

    public static class Builder {
        private final String node;
        private String requirement;
        private String capability;

        public Builder(String node) {
            this.node = node;
        }

        public Builder requirement(String req) {
            this.requirement = req;
            return this;
        }

        public Builder capability(String cap) {
            this.capability = cap;
            return this;
        }

        public TEventFilterDefinition build() {
            return new TEventFilterDefinition(this);
        }
    }
}
