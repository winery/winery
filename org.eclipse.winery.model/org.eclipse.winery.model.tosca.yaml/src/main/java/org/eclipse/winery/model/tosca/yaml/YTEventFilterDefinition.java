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

package org.eclipse.winery.model.tosca.yaml;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

public class YTEventFilterDefinition implements VisitorNode {
    private final String node;
    private final String requirement;
    private final String capability;

    private YTEventFilterDefinition(Builder builder) {
        this.node = builder.node;
        this.requirement = builder.requirement;
        this.capability = builder.capability;
    }

    public String getNode() {
        return node;
    }

    @Override
    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public String getRequirement() {
        return requirement;
    }

    public String getCapability() {
        return capability;
    }

    public static class Builder {
        private final String node;
        private String requirement;
        private String capability;

        public Builder(String node) {
            this.node = node;
        }

        public Builder setRequirement(String req) {
            this.requirement = req;
            return this;
        }

        public Builder setCapability(String cap) {
            this.capability = cap;
            return this;
        }

        public YTEventFilterDefinition build() {
            return new YTEventFilterDefinition(this);
        }
    }
}
