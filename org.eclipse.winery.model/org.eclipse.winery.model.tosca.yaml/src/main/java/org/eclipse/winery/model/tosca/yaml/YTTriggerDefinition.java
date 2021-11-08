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

import java.util.List;

import org.eclipse.winery.model.tosca.yaml.support.YTMapActivityDefinition;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

public class YTTriggerDefinition implements VisitorNode {
    private final String description;
    private final String event;
    private final YTEventFilterDefinition targetFilter;
    private final List<YTMapActivityDefinition> action;

    private YTTriggerDefinition(Builder builder) {
        description = builder.description;
        event = builder.event;
        targetFilter = builder.targetFilter;
        action = builder.action;
    }

    @Override
    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public String getDescription() {
        return description;
    }

    public String getEvent() {
        return event;
    }

    public YTEventFilterDefinition getTargetFilter() {
        return targetFilter;
    }

    public List<YTMapActivityDefinition> getAction() {
        return action;
    }

    public static class Builder {
        private final String event;
        private String description;
        private YTEventFilterDefinition targetFilter;
        private List<YTMapActivityDefinition> action;

        public Builder(String event) {
            this.event = event;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setTargetFilter(YTEventFilterDefinition targetFilter) {
            this.targetFilter = targetFilter;
            return this;
        }

        public Builder setAction(List<YTMapActivityDefinition> actions) {
            this.action = actions;
            return this;
        }

        public YTTriggerDefinition build() {
            return new YTTriggerDefinition(this);
        }
    }
}
