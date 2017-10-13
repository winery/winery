/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.model.tosca.yaml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;

import org.eclipse.jdt.annotation.NonNull;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPolicyType", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.0", propOrder = {
    "targets",
    "triggers"
})
public class TPolicyType extends TEntityType {
    private List<QName> targets;
    private Object triggers;

    public TPolicyType() {
    }

    public TPolicyType(Builder builder) {
        super(builder);
        this.setTargets(builder.targets);
        this.setTriggers(builder.triggers);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TPolicyType)) return false;
        if (!super.equals(o)) return false;
        TPolicyType that = (TPolicyType) o;
        return Objects.equals(getTargets(), that.getTargets()) &&
            Objects.equals(getTriggers(), that.getTriggers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTargets(), getTriggers());
    }

    @NonNull
    public List<QName> getTargets() {
        if (this.targets == null) {
            this.targets = new ArrayList<>();
        }

        return targets;
    }

    public void setTargets(List<QName> targets) {
        this.targets = targets;
    }

    public Object getTriggers() {
        return triggers;
    }

    public void setTriggers(Object triggers) {
        this.triggers = triggers;
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

    public static class Builder extends TEntityType.Builder<Builder> {
        private List<QName> targets;
        private Object triggers;

        public Builder() {

        }

        public Builder(TEntityType entityType) {
            super(entityType);
        }

        @Override
        public Builder self() {
            return this;
        }

        public Builder setTargets(List<QName> targets) {
            this.targets = targets;
            return this;
        }

        public Builder setTriggers(Object triggers) {
            this.triggers = triggers;
            return this;
        }

        public Builder addTargets(List<QName> targets) {
            if (targets == null || targets.isEmpty()) {
                return this;
            }

            if (this.targets == null) {
                this.targets = new ArrayList<>(targets);
            } else {
                this.targets.addAll(targets);
            }

            return this;
        }

        public Builder addTargets(QName target) {
            if (target == null) {
                return this;
            }

            return addTargets(Collections.singletonList(target));
        }

        public TPolicyType build() {
            return new TPolicyType(this);
        }
    }
}
