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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.xml.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tTopologyTemplate", propOrder = {
    "nodeTemplateOrRelationshipTemplate"
})
public class XTTopologyTemplate extends XTExtensibleElements {
    @XmlElements( {
        @XmlElement(name = "RelationshipTemplate", type = XTRelationshipTemplate.class),
        @XmlElement(name = "NodeTemplate", type = XTNodeTemplate.class)
    })
    protected List<XTEntityTemplate> nodeTemplateOrRelationshipTemplate;

    @Deprecated // required for XML deserialization
    public XTTopologyTemplate() {
    }

    public XTTopologyTemplate(Builder builder) {
        super(builder);
        this.nodeTemplateOrRelationshipTemplate = builder.getNodeTemplateOrRelationshipTemplate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XTTopologyTemplate)) return false;
        if (!super.equals(o)) return false;
        XTTopologyTemplate that = (XTTopologyTemplate) o;
        return Objects.equals(nodeTemplateOrRelationshipTemplate, that.nodeTemplateOrRelationshipTemplate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), nodeTemplateOrRelationshipTemplate);
    }

    @NonNull
    public List<XTEntityTemplate> getNodeTemplateOrRelationshipTemplate() {
        if (nodeTemplateOrRelationshipTemplate == null) {
            nodeTemplateOrRelationshipTemplate = new ArrayList<>();
        }
        return this.nodeTemplateOrRelationshipTemplate;
    }

    /**
     * @return all nodes templates of the topologyTemplate
     */
    @NonNull
    public List<XTNodeTemplate> getNodeTemplates() {
        return this.getNodeTemplateOrRelationshipTemplate()
            .stream()
            .filter(x -> x instanceof XTNodeTemplate)
            .map(XTNodeTemplate.class::cast)
            .collect(Collectors.toList());
    }

    public void setNodeTemplates(List<XTNodeTemplate> nodeTemplates) {
        this.nodeTemplateOrRelationshipTemplate = Stream.concat(
            nodeTemplates.stream().map(XTEntityTemplate.class::cast),
            this.getRelationshipTemplates().stream().map(XTEntityTemplate.class::cast))
            .collect(Collectors.toList());
    }

    /**
     * @return node template having the given id. null if not found
     */
    @Nullable
    public XTNodeTemplate getNodeTemplate(String id) {
        Objects.requireNonNull(id);
        return this.getNodeTemplates().stream()
            .filter(x -> id.equals(x.getId()))
            .findAny()
            .orElse(null);
    }

    /**
     * @return all relationship templates of the topologyTemplate
     */
    @NonNull
    public List<XTRelationshipTemplate> getRelationshipTemplates() {
        return this.getNodeTemplateOrRelationshipTemplate()
            .stream()
            .filter(x -> x instanceof XTRelationshipTemplate)
            .map(XTRelationshipTemplate.class::cast)
            .collect(Collectors.toList());
    }

    public void setRelationshipTemplates(List<XTRelationshipTemplate> relationshipTemplates) {
        this.nodeTemplateOrRelationshipTemplate = Stream.concat(
            this.getNodeTemplates().stream().map(XTEntityTemplate.class::cast),
            relationshipTemplates.stream().map(XTEntityTemplate.class::cast))
            .collect(Collectors.toList());
    }

    /**
     * @return relationship template having the given id. null if not found
     */
    @Nullable
    public XTRelationshipTemplate getRelationshipTemplate(String id) {
        Objects.requireNonNull(id);
        return this.getRelationshipTemplates().stream()
            .filter(x -> id.equals(x.getId()))
            .findAny()
            .orElse(null);
    }

    public void addNodeTemplate(XTNodeTemplate nt) {
        this.getNodeTemplateOrRelationshipTemplate().add(nt);
    }

    public void addRelationshipTemplate(XTRelationshipTemplate rt) {
        this.getNodeTemplateOrRelationshipTemplate().add(rt);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static class Builder extends XTExtensibleElements.Builder<Builder> {
        private List<XTNodeTemplate> nodeTemplates;
        private List<XTRelationshipTemplate> relationshipTemplates;

        public Builder() {
        }

        @Override
        public Builder self() {
            return this;
        }

        public Builder setNodeTemplates(List<XTNodeTemplate> nodeTemplates) {
            this.nodeTemplates = nodeTemplates;
            return this;
        }

        public Builder setRelationshipTemplates(List<XTRelationshipTemplate> relationshipTemplates) {
            this.relationshipTemplates = relationshipTemplates;
            return this;
        }

        public Builder addNodeTemplates(List<XTNodeTemplate> nodeTemplates) {
            if (nodeTemplates == null || nodeTemplates.isEmpty()) {
                return this;
            }

            if (this.nodeTemplates == null) {
                this.nodeTemplates = nodeTemplates;
            } else {
                this.nodeTemplates.addAll(nodeTemplates);
            }
            return this;
        }

        public Builder addNodeTemplates(XTNodeTemplate nodeTemplates) {
            if (nodeTemplates == null) {
                return this;
            }

            List<XTNodeTemplate> tmp = new ArrayList<>();
            tmp.add(nodeTemplates);
            return addNodeTemplates(tmp);
        }

        public Builder addRelationshipTemplates(List<XTRelationshipTemplate> relationshipTemplates) {
            if (relationshipTemplates == null || relationshipTemplates.isEmpty()) {
                return this;
            }

            if (this.relationshipTemplates == null) {
                this.relationshipTemplates = relationshipTemplates;
            } else {
                this.relationshipTemplates.addAll(relationshipTemplates);
            }
            return this;
        }

        public Builder addRelationshipTemplate(XTRelationshipTemplate relationshipTemplate) {
            if (relationshipTemplate == null) {
                return this;
            }

            List<XTRelationshipTemplate> tmp = new ArrayList<>();
            tmp.add(relationshipTemplate);
            return addRelationshipTemplates(tmp);
        }

        public List<XTEntityTemplate> getNodeTemplateOrRelationshipTemplate() {
            List<XTEntityTemplate> tmp = new ArrayList<>();
            Optional.ofNullable(nodeTemplates).ifPresent(tmp::addAll);
            Optional.ofNullable(relationshipTemplates).ifPresent(tmp::addAll);
            return tmp;
        }

        public XTTopologyTemplate build() {
            return new XTTopologyTemplate(this);
        }
    }
}
