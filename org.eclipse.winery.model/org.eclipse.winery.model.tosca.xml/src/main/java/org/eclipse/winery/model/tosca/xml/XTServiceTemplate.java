/*******************************************************************************
 * Copyright (c) 2013-2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.tosca.xml;

import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.xml.utils.RemoveEmptyLists;
import org.eclipse.winery.model.tosca.xml.visitor.Visitor;

import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tServiceTemplate", propOrder = {
    "tags",
    "boundaryDefinitions",
    "topologyTemplate",
    "plans"
})
public class XTServiceTemplate extends XHasId implements XHasName, XHasTargetNamespace {

    @XmlElement(name = "Tags")
    protected XTTags tags;

    @XmlElement(name = "BoundaryDefinitions")
    protected XTBoundaryDefinitions boundaryDefinitions;

    @XmlElement(name = "TopologyTemplate", required = true)
    protected XTTopologyTemplate topologyTemplate;

    @XmlElement(name = "Plans")
    protected XTPlans plans;

    @XmlAttribute(name = "name")
    protected String name;

    @XmlAttribute(name = "targetNamespace")
    @XmlSchemaType(name = "anyURI")
    protected String targetNamespace;

    @XmlAttribute(name = "substitutableNodeType")
    protected QName substitutableNodeType;

    @Deprecated // required for XML deserialization
    public XTServiceTemplate() { }

    public XTServiceTemplate(Builder builder) {
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
        if (!(o instanceof XTServiceTemplate)) return false;
        if (!super.equals(o)) return false;
        XTServiceTemplate that = (XTServiceTemplate) o;
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
    public XTTags getTags() {
        return tags;
    }

    public void setTags(@Nullable XTTags value) {
        this.tags = value;
    }

    @Nullable
    public XTBoundaryDefinitions getBoundaryDefinitions() {
        return boundaryDefinitions;
    }

    public void setBoundaryDefinitions(@Nullable XTBoundaryDefinitions value) {
        this.boundaryDefinitions = value;
    }

    /**
     * Even though the XSD requires that the topology template is always set, during modeling, it might be null
     */
    @Nullable
    public XTTopologyTemplate getTopologyTemplate() {
        return topologyTemplate;
    }

    public void setTopologyTemplate(@Nullable XTTopologyTemplate value) {
        if (value != null) {
            RemoveEmptyLists removeEmptyLists = new RemoveEmptyLists();
            removeEmptyLists.removeEmptyLists(value);
        }

        this.topologyTemplate = value;
    }

    @Nullable
    public XTPlans getPlans() {
        return plans;
    }

    public void setPlans(@Nullable XTPlans value) {
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

    public static class Builder extends XHasId.Builder<Builder> {
        private final XTTopologyTemplate topologyTemplate;

        private XTTags tags;
        private XTBoundaryDefinitions boundaryDefinitions;
        private XTPlans plans;
        private String name;
        private String targetNamespace;
        private QName substitutableNodeType;
        
        public Builder(String id) {
            super(id);
            topologyTemplate = null;
        }

        public Builder(String id, XTTopologyTemplate topologyTemplate) {
            super(id);
            this.topologyTemplate = topologyTemplate;
        }

        public Builder setTags(XTTags tags) {
            this.tags = tags;
            return this;
        }

        public Builder setBoundaryDefinitions(XTBoundaryDefinitions boundaryDefinitions) {
            this.boundaryDefinitions = boundaryDefinitions;
            return this;
        }

        public Builder setPlans(XTPlans plans) {
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

        public Builder addTags(XTTags tags) {
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

        public Builder addTags(List<XTTag> tags) {
            if (tags == null) {
                return this;
            }

            XTTags tmp = new XTTags();
            tmp.getTag().addAll(tags);
            return addTags(tmp);
        }

        public Builder addTags(XTTag tags) {
            if (tags == null) {
                return this;
            }

            XTTags tmp = new XTTags();
            tmp.getTag().add(tags);
            return addTags(tmp);
        }

        @Override
        public Builder self() {
            return this;
        }

        public XTServiceTemplate build() {
            return new XTServiceTemplate(this);
        }
    }
}
