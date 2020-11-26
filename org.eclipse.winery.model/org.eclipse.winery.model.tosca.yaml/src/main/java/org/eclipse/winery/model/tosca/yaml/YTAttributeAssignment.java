/********************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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

import java.util.Objects;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.eclipse.jdt.annotation.Nullable;

public class YTAttributeAssignment implements VisitorNode {

    private String description;
    private Object value;

    protected YTAttributeAssignment(Builder builder) {
        this.setDescription(builder.description);
        this.setValue(builder.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YTAttributeAssignment)) return false;
        YTAttributeAssignment that = (YTAttributeAssignment) o;
        return Objects.equals(getDescription(), that.getDescription()) &&
            Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDescription(), getValue());
    }

    @Override
    public String toString() {
        return "TAttributeAssignment{" +
            "description='" + getDescription() + '\'' +
            ", value=" + getValue() +
            '}';
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Nullable
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder {
        private String description;
        private Object value;

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setValue(Object value) {
            this.value = value;
            return this;
        }

        public YTAttributeAssignment build() {
            return new YTAttributeAssignment(this);
        }
    }
}
