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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tTopologyTemplate", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.3", propOrder = {
    "description",
    "inputs",
    "nodeTemplates",
    "relationshipTemplates",
    "groups",
    "policies",
    "outputs",
    "substitutionMappings"
})
public class TTopologyTemplateDefinition implements VisitorNode {
    private String description;
    private Map<String, TParameterDefinition> inputs;
    @XmlAttribute(name = "node_templates")
    private Map<String, TNodeTemplate> nodeTemplates;
    @XmlAttribute(name = "relationship_templates")
    private Map<String, TRelationshipTemplate> relationshipTemplates;
    private Map<String, TGroupDefinition> groups;
    private Map<String, TPolicyDefinition> policies;
    private Map<String, TParameterDefinition> outputs;
    @XmlAttribute(name = "substitution_mappings")
    private TSubstitutionMappings substitutionMappings;

    public TTopologyTemplateDefinition() {
    }

    public TTopologyTemplateDefinition(Builder builder) {
        this.setDescription(builder.description);
        this.setInputs(builder.inputs);
        this.setNodeTemplates(builder.nodeTemplates);
        this.setRelationshipTemplates(builder.relationshipTemplates);
        this.setGroups(builder.groups);
        this.setPolicies(builder.policies);
        this.setOutputs(builder.outputs);
        this.setSubstitutionMappings(builder.substitutionMappings);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TTopologyTemplateDefinition)) return false;
        TTopologyTemplateDefinition that = (TTopologyTemplateDefinition) o;
        return Objects.equals(getDescription(), that.getDescription()) &&
            Objects.equals(getInputs(), that.getInputs()) &&
            Objects.equals(getNodeTemplates(), that.getNodeTemplates()) &&
            Objects.equals(getRelationshipTemplates(), that.getRelationshipTemplates()) &&
            Objects.equals(getGroups(), that.getGroups()) &&
            Objects.equals(getPolicies(), that.getPolicies()) &&
            Objects.equals(getOutputs(), that.getOutputs()) &&
            Objects.equals(getSubstitutionMappings(), that.getSubstitutionMappings());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDescription(), getInputs(), getNodeTemplates(), getRelationshipTemplates(), getGroups(), getPolicies(), getOutputs(), getSubstitutionMappings());
    }

    @Override
    public String toString() {
        return "TTopologyTemplateDefinition{" +
            "description='" + getDescription() + '\'' +
            ", inputs=" + getInputs() +
            ", nodeTemplates=" + getNodeTemplates() +
            ", relationshipTemplates=" + getRelationshipTemplates() +
            ", groups=" + getGroups() +
            ", policies=" + getPolicies() +
            ", outputs=" + getOutputs() +
            ", substitutionMappings=" + getSubstitutionMappings() +
            '}';
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NonNull
    public Map<String, TParameterDefinition> getInputs() {
        if (this.inputs == null) {
            this.inputs = new LinkedHashMap<>();
        }

        return inputs;
    }

    public void setInputs(Map<String, TParameterDefinition> inputs) {
        this.inputs = inputs;
    }

    @NonNull
    public Map<String, TNodeTemplate> getNodeTemplates() {
        if (this.nodeTemplates == null) {
            this.nodeTemplates = new LinkedHashMap<>();
        }

        return nodeTemplates;
    }

    public void setNodeTemplates(Map<String, TNodeTemplate> nodeTemplates) {
        this.nodeTemplates = nodeTemplates;
    }

    @NonNull
    public Map<String, TRelationshipTemplate> getRelationshipTemplates() {
        if (this.relationshipTemplates == null) {
            this.relationshipTemplates = new LinkedHashMap<>();
        }

        return relationshipTemplates;
    }

    public void setRelationshipTemplates(Map<String, TRelationshipTemplate> relationshipTemplates) {
        this.relationshipTemplates = relationshipTemplates;
    }

    @NonNull
    public Map<String, TGroupDefinition> getGroups() {
        if (this.groups == null) {
            this.groups = new LinkedHashMap<>();
        }

        return groups;
    }

    public void setGroups(Map<String, TGroupDefinition> groups) {
        this.groups = groups;
    }

    @NonNull
    public Map<String, TPolicyDefinition> getPolicies() {
        if (this.policies == null) {
            this.policies = new LinkedHashMap<>();
        }

        return policies;
    }

    public void setPolicies(Map<String, TPolicyDefinition> policies) {
        this.policies = policies;
    }

    @NonNull
    public Map<String, TParameterDefinition> getOutputs() {
        if (this.outputs == null) {
            this.outputs = new LinkedHashMap<>();
        }

        return outputs;
    }

    public void setOutputs(Map<String, TParameterDefinition> outputs) {
        this.outputs = outputs;
    }

    @Nullable
    public TSubstitutionMappings getSubstitutionMappings() {
        return substitutionMappings;
    }

    public void setSubstitutionMappings(TSubstitutionMappings substitutionMappings) {
        this.substitutionMappings = substitutionMappings;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder {
        private String description;
        private Map<String, TParameterDefinition> inputs;
        private Map<String, TNodeTemplate> nodeTemplates;
        private Map<String, TRelationshipTemplate> relationshipTemplates;
        private Map<String, TGroupDefinition> groups;
        private Map<String, TPolicyDefinition> policies;
        private Map<String, TParameterDefinition> outputs;
        private TSubstitutionMappings substitutionMappings;

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setInputs(Map<String, TParameterDefinition> inputs) {
            this.inputs = inputs;
            return this;
        }

        public Builder setNodeTemplates(Map<String, TNodeTemplate> nodeTemplates) {
            this.nodeTemplates = nodeTemplates;
            return this;
        }

        public Builder setRelationshipTemplates(Map<String, TRelationshipTemplate> relationshipTemplates) {
            this.relationshipTemplates = relationshipTemplates;
            return this;
        }

        public Builder setGroups(Map<String, TGroupDefinition> groups) {
            this.groups = groups;
            return this;
        }

        public Builder setPolicies(Map<String, TPolicyDefinition> policies) {
            this.policies = policies;
            return this;
        }

        public Builder setOutputs(Map<String, TParameterDefinition> outputs) {
            this.outputs = outputs;
            return this;
        }

        public Builder setSubstitutionMappings(TSubstitutionMappings substitutionMappings) {
            this.substitutionMappings = substitutionMappings;
            return this;
        }

        public Builder addInputs(Map<String, TParameterDefinition> inputs) {
            if (inputs == null || inputs.isEmpty()) {
                return this;
            }

            if (this.inputs == null) {
                this.inputs = new LinkedHashMap<>(inputs);
            } else {
                this.inputs.putAll(inputs);
            }

            return this;
        }

        public Builder addInputs(String name, TParameterDefinition input) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addInputs(Collections.singletonMap(name, input));
        }

        public Builder addNodeTemplates(Map<String, TNodeTemplate> nodeTemplates) {
            if (nodeTemplates == null || nodeTemplates.isEmpty()) {
                return this;
            }

            if (this.nodeTemplates == null) {
                this.nodeTemplates = new LinkedHashMap<>(nodeTemplates);
            } else {
                this.nodeTemplates.putAll(nodeTemplates);
            }

            return this;
        }

        public Builder addNodeTemplates(String name, TNodeTemplate nodeTemplate) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addNodeTemplates(Collections.singletonMap(name, nodeTemplate));
        }

        public Builder addRelationshipTemplates(Map<String, TRelationshipTemplate> relationshipTemplates) {
            if (relationshipTemplates == null || relationshipTemplates.isEmpty()) {
                return this;
            }

            if (this.relationshipTemplates == null) {
                this.relationshipTemplates = new LinkedHashMap<>(relationshipTemplates);
            } else {
                this.relationshipTemplates.putAll(relationshipTemplates);
            }

            return this;
        }

        public Builder addRelationshipTemplates(String name, TRelationshipTemplate relationshipTemplate) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addRelationshipTemplates(Collections.singletonMap(name, relationshipTemplate));
        }

        public Builder addGroups(Map<String, TGroupDefinition> groups) {
            if (groups == null || groups.isEmpty()) {
                return this;
            }

            if (this.groups == null) {
                this.groups = new LinkedHashMap<>(groups);
            } else {
                this.groups.putAll(groups);
            }

            return this;
        }

        public Builder addGroups(String name, TGroupDefinition group) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addGroups(Collections.singletonMap(name, group));
        }

//        public Builder addPolicies(Map<String, TPolicyDefinition> policies) {
//            if (policies == null || policies.isEmpty()) {
//                return this;
//            }
//
//            if (this.policies == null) {
//                this.policies = new LinkedHashMap<>(policies);
//            } else {
//                this.policies.putAll(policies);
//            }
//
//            return this;
//        }
//
//        public Builder addPolicies(String name, TPolicyDefinition policy) {
//            if (name == null || name.isEmpty()) {
//                return this;
//            }
//
//            return addPolicies(Collections.singletonMap(name, policy));
//        }

        public Builder addOutputs(Map<String, TParameterDefinition> outputs) {
            if (outputs == null || outputs.isEmpty()) {
                return this;
            }

            if (this.outputs == null) {
                this.outputs = new LinkedHashMap<>(outputs);
            } else {
                this.outputs.putAll(outputs);
            }

            return this;
        }

        public Builder addOutputs(String name, TParameterDefinition output) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addOutputs(Collections.singletonMap(name, output));
        }

        public TTopologyTemplateDefinition build() {
            return new TTopologyTemplateDefinition(this);
        }
    }
}
