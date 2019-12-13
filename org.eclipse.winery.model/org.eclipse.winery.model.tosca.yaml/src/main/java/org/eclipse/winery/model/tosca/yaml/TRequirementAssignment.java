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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tRequirementAssignment", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.3", propOrder = {
    "capability",
    "node",
    "relationship",
    "nodeFilter",
    "occurrences"
})
public class TRequirementAssignment implements VisitorNode {
    private QName node;
    private TRelationshipAssignment relationship;
    private QName capability;
    @XmlAttribute(name = "node_filter")
    private TNodeFilterDefinition nodeFilter;
    private List<String> occurrences;

    public TRequirementAssignment() {
    }

    public TRequirementAssignment(QName node) {
        this.node = node;
    }

    public TRequirementAssignment(Builder builder) {
        this.setCapability(builder.capability);
        this.setNode(builder.node);
        this.setRelationship(builder.relationship);
        this.setNodeFilter(builder.nodeFilter);
        this.setOccurrences(builder.occurrences);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TRequirementAssignment)) return false;
        TRequirementAssignment that = (TRequirementAssignment) o;
        return Objects.equals(getNode(), that.getNode()) &&
            Objects.equals(getRelationship(), that.getRelationship()) &&
            Objects.equals(getCapability(), that.getCapability()) &&
            Objects.equals(getNodeFilter(), that.getNodeFilter()) &&
            Objects.equals(getOccurrences(), that.getOccurrences());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNode(), getRelationship(), getCapability(), getNodeFilter(), getOccurrences());
    }

    @Override
    public String toString() {
        return "TRequirementAssignment{" +
            "node=" + getNode() +
            ", relationship=" + getRelationship() +
            ", capability=" + getCapability() +
            ", nodeFilter=" + getNodeFilter() +
            ", occurrences=" + getOccurrences() +
            '}';
    }

    @Nullable
    public QName getCapability() {
        return capability;
    }

    public void setCapability(QName capability) {
        this.capability = capability;
    }

    @Nullable
    public QName getNode() {
        return node;
    }

    public void setNode(QName node) {
        this.node = node;
    }

    @Nullable
    public TRelationshipAssignment getRelationship() {
        return relationship;
    }

    public void setRelationship(TRelationshipAssignment relationship) {
        this.relationship = relationship;
    }

    @Nullable
    public TNodeFilterDefinition getNodeFilter() {
        return nodeFilter;
    }

    public void setNodeFilter(TNodeFilterDefinition nodeFilter) {
        this.nodeFilter = nodeFilter;
    }

    @NonNull
    public List<String> getOccurrences() {
        if (this.occurrences == null) {
            this.occurrences = new ArrayList<>();
        }

        return occurrences;
    }

    public void setOccurrences(List<String> occurrences) {
        this.occurrences = occurrences;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder {
        private QName capability;
        private QName node;
        private TRelationshipAssignment relationship;
        private TNodeFilterDefinition nodeFilter;
        private List<String> occurrences;

        public Builder setCapability(QName capability) {
            this.capability = capability;
            return this;
        }

        public Builder setNode(QName node) {
            this.node = node;
            return this;
        }

        public Builder setRelationship(TRelationshipAssignment relationship) {
            this.relationship = relationship;
            return this;
        }

        public Builder setNodeFilter(TNodeFilterDefinition nodeFilter) {
            this.nodeFilter = nodeFilter;
            return this;
        }

        public Builder setOccurrences(List<String> occurrences) {
            this.occurrences = occurrences;
            return this;
        }

        public TRequirementAssignment build() {
            return new TRequirementAssignment(this);
        }
    }
}
