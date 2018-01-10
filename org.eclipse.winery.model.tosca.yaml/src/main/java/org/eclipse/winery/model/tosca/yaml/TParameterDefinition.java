/********************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;

import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tParameterDefinition", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.0", propOrder = {
    "value"
})
public class TParameterDefinition extends TPropertyDefinition {
    private Object value;

    public TParameterDefinition() {
    }

    public TParameterDefinition(Builder builder) {
        super(builder);
        this.setValue(builder.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TParameterDefinition)) return false;
        if (!super.equals(o)) return false;
        TParameterDefinition that = (TParameterDefinition) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getValue());
    }

    @Nullable
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        R ir1 = super.accept(visitor, parameter);
        R ir2 = visitor.visit(this, parameter);
        if (ir1 == null) {
            return ir2;
        } else {
            return ir1.add(ir2);
        }
    }

    public static class Builder extends TPropertyDefinition.Builder<Builder> {
        private Object value;

        public Builder(QName type) {
            super(type);
        }

        public Builder(TPropertyDefinition propertyDefinition) {
            super(propertyDefinition);
        }

        public Builder setValue(Object value) {
            this.value = value;
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }

        public TParameterDefinition build() {
            return new TParameterDefinition(this);
        }
    }
}
