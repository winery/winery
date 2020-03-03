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

package org.eclipse.winery.model.tosca;

import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.utils.RemoveEmptyLists;
import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tServiceTemplate", propOrder = {
    "tags",
    "boundaryDefinitions",
    "topologyTemplate",
    "plans"
})
public class TServiceTemplate extends HasId implements HasName, HasTargetNamespace {

    @XmlElement(name = "Tags")
    protected TTags tags;

    @XmlElement(name = "BoundaryDefinitions")
    protected TBoundaryDefinitions boundaryDefinitions;

    @XmlElement(name = "TopologyTemplate", required = true)
    protected TTopologyTemplate topologyTemplate;

    @XmlElement(name = "Plans")
    protected TPlans plans;

    @XmlAttribute(name = "name")
    protected String name;

    @XmlAttribute(name = "targetNamespace")
    @XmlSchemaType(name = "anyURI")
    protected String targetNamespace;

    @XmlAttribute(name = "substitutableNodeType")
    protected QName substitutableNodeType;

    public TServiceTemplate() {
    }

    public TServiceTemplate(Builder builder) {
        super(builder);
        this.tags = builder.tags;
        this.boundaryDefinitions = builder.boundaryDefinitions;
        this.topologyTemplate = builder.topologyTemplate;
        this.plans = builder.plans;
        this.name = builder.name;
        this.targetNamespace = builder.targetNamespace;
        this.substitutableNodeType = builder.substitutableNodeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TServiceTemplate)) return false;
        if (!super.equals(o)) return false;
        TServiceTemplate that = (TServiceTemplate) o;
        return Objects.equals(tags, that.tags) &&
            Objects.equals(boundaryDefinitions, that.boundaryDefinitions) &&
            Objects.equals(topologyTemplate, that.topologyTemplate) &&
            Objects.equals(plans, that.plans) &&
            Objects.equals(name, that.name) &&
            Objects.equals(targetNamespace, that.targetNamespace) &&
            Objects.equals(substitutableNodeType, that.substitutableNodeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tags, boundaryDefinitions, topologyTemplate, plans, name, targetNamespace, substitutableNodeType);
    }

    @Nullable
    public TTags getTags() {
        return tags;
    }

    public void setTags(@Nullable TTags value) {
        this.tags = value;
    }

    @Nullable
    public TBoundaryDefinitions getBoundaryDefinitions() {
        return boundaryDefinitions;
    }

    public void setBoundaryDefinitions(@Nullable TBoundaryDefinitions value) {
        this.boundaryDefinitions = value;
    }

    /**
     * Even though the XSD requires that the topology template is always set, during modeling, it might be null
     */
    @Nullable
    public TTopologyTemplate getTopologyTemplate() {
        return topologyTemplate;
    }

    public void setTopologyTemplate(@Nullable TTopologyTemplate value) {
        if (value != null) {
            RemoveEmptyLists removeEmptyLists = new RemoveEmptyLists();
            removeEmptyLists.removeEmptyLists(value);
        }
        this.topologyTemplate = value;
    }

    @Nullable
    public TPlans getPlans() {
        return plans;
    }

    public void setPlans(@Nullable TPlans value) {
        this.plans = value;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String value) {
        this.name = value;
    }

    @Nullable
    public String getTargetNamespace() {
        return targetNamespace;
    }

    public void setTargetNamespace(@Nullable String value) {
        this.targetNamespace = value;
    }

    @Nullable
    public QName getSubstitutableNodeType() {
        return substitutableNodeType;
    }

    public void setSubstitutableNodeType(@Nullable QName value) {
        this.substitutableNodeType = value;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static class Builder extends HasId.Builder<Builder> {
        private final TTopologyTemplate topologyTemplate;

        private TTags tags;
        private TBoundaryDefinitions boundaryDefinitions;
        private TPlans plans;
        private String name;
        private String targetNamespace;
        private QName substitutableNodeType;
        
        public Builder(String id) {
            super(id);
            topologyTemplate = null;
        }

        public Builder(String id, TTopologyTemplate topologyTemplate) {
            super(id);
            this.topologyTemplate = topologyTemplate;
        }

        public Builder setTags(TTags tags) {
            this.tags = tags;
            return this;
        }

        public Builder setBoundaryDefinitions(TBoundaryDefinitions boundaryDefinitions) {
            this.boundaryDefinitions = boundaryDefinitions;
            return this;
        }

        public Builder setPlans(TPlans plans) {
            this.plans = plans;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setTargetNamespace(String targetNamespace) {
            this.targetNamespace = targetNamespace;
            return this;
        }

        public Builder setSubstitutableNodeType(QName substitutableNodeType) {
            this.substitutableNodeType = substitutableNodeType;
            return this;
        }

        public Builder addTags(TTags tags) {
            if (tags == null || tags.getTag().isEmpty()) {
                return this;
            }

            if (this.tags == null) {
                this.tags = tags;
            } else {
                this.tags.getTag().addAll(tags.getTag());
            }
            return this;
        }

        public Builder addTags(List<TTag> tags) {
            if (tags == null) {
                return this;
            }

            TTags tmp = new TTags();
            tmp.getTag().addAll(tags);
            return addTags(tmp);
        }

        public Builder addTags(TTag tags) {
            if (tags == null) {
                return this;
            }

            TTags tmp = new TTags();
            tmp.getTag().add(tags);
            return addTags(tmp);
        }

        @Override
        public Builder self() {
            return this;
        }

        public TServiceTemplate build() {
            return new TServiceTemplate(this);
        }
    }
}
