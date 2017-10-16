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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.yaml.support.Metadata;
import org.eclipse.winery.model.tosca.yaml.support.TMapImportDefinition;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tServiceTemplate", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.0", propOrder = {
    "toscaDefinitionsVersion",
    "metadata",
    "description",
    "dslDefinitions",
    "repositories",
    "imports",
    "artifactTypes",
    "dataTypes",
    "capabilityTypes",
    "interfaceTypes",
    "relationshipTypes",
    "nodeTypes",
    "groupTypes",
    "policyTypes",
    "topologyTemplate"
})
public class TServiceTemplate implements VisitorNode {
    @XmlAttribute(name = "tosca_definitions_version", required = true)
    private String toscaDefinitionsVersion;
    private Metadata metadata;
    private String description;
    @XmlAttribute(name = "dsl_definitions")
    private Map<String, Object> dslDefinitions;
    private Map<String, TRepositoryDefinition> repositories;
    private List<TMapImportDefinition> imports;
    @XmlAttribute(name = "artifact_types")
    private Map<String, TArtifactType> artifactTypes;
    @XmlAttribute(name = "data_types")
    private Map<String, TDataType> dataTypes;
    @XmlAttribute(name = "capability_types")
    private Map<String, TCapabilityType> capabilityTypes;
    @XmlAttribute(name = "interface_types")
    private Map<String, TInterfaceType> interfaceTypes;
    @XmlAttribute(name = "relationship_types")
    private Map<String, TRelationshipType> relationshipTypes;
    @XmlAttribute(name = "node_types")
    private Map<String, TNodeType> nodeTypes;
    @XmlAttribute(name = "group_types")
    private Map<String, TGroupType> groupTypes;
    @XmlAttribute(name = "policy_types")
    private Map<String, TPolicyType> policyTypes;
    @XmlAttribute(name = "topology_template")
    private TTopologyTemplateDefinition topologyTemplate;

    public TServiceTemplate() {

    }

    public TServiceTemplate(Builder builder) {
        this.setToscaDefinitionsVersion(builder.toscaDefinitionsVersion);
        this.setMetadata(builder.metadata);
        this.setDescription(builder.description);
        this.setDslDefinitions(builder.dslDefinitions);
        this.setRepositories(builder.repositories);
        this.setImports(builder.imports);
        this.setArtifactTypes(builder.artifactTypes);
        this.setDataTypes(builder.dataTypes);
        this.setCapabilityTypes(builder.capabilityTypes);
        this.setInterfaceTypes(builder.interfaceTypes);
        this.setRelationshipTypes(builder.relationshipTypes);
        this.setNodeTypes(builder.nodeTypes);
        this.setGroupTypes(builder.groupTypes);
        this.setPolicyTypes(builder.policyTypes);
        this.setTopologyTemplate(builder.topologyTemplate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TServiceTemplate)) return false;
        TServiceTemplate that = (TServiceTemplate) o;
        return Objects.equals(getToscaDefinitionsVersion(), that.getToscaDefinitionsVersion()) &&
            Objects.equals(getMetadata(), that.getMetadata()) &&
            Objects.equals(getDescription(), that.getDescription()) &&
            Objects.equals(getDslDefinitions(), that.getDslDefinitions()) &&
            Objects.equals(getRepositories(), that.getRepositories()) &&
            Objects.equals(getImports(), that.getImports()) &&
            Objects.equals(getArtifactTypes(), that.getArtifactTypes()) &&
            Objects.equals(getDataTypes(), that.getDataTypes()) &&
            Objects.equals(getCapabilityTypes(), that.getCapabilityTypes()) &&
            Objects.equals(getInterfaceTypes(), that.getInterfaceTypes()) &&
            Objects.equals(getRelationshipTypes(), that.getRelationshipTypes()) &&
            Objects.equals(getNodeTypes(), that.getNodeTypes()) &&
            Objects.equals(getGroupTypes(), that.getGroupTypes()) &&
            Objects.equals(getPolicyTypes(), that.getPolicyTypes()) &&
            Objects.equals(getTopologyTemplate(), that.getTopologyTemplate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getToscaDefinitionsVersion(), getMetadata(), getDescription(), getDslDefinitions(), getRepositories(), getImports(), getArtifactTypes(), getDataTypes(), getCapabilityTypes(), getInterfaceTypes(), getRelationshipTypes(), getNodeTypes(), getGroupTypes(), getPolicyTypes(), getTopologyTemplate());
    }

    @NonNull
    public String getToscaDefinitionsVersion() {
        return toscaDefinitionsVersion;
    }

    public void setToscaDefinitionsVersion(String toscaDefinitionsVersion) {
        this.toscaDefinitionsVersion = toscaDefinitionsVersion;
    }

    @NonNull
    public Metadata getMetadata() {
        if (!Objects.nonNull(metadata)) {
            this.metadata = new Metadata();
        }

        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    @NonNull
    public Map<String, Object> getDslDefinitions() {
        if (this.dslDefinitions == null) {
            this.dslDefinitions = new LinkedHashMap<>();
        }

        return dslDefinitions;
    }

    public void setDslDefinitions(Map<String, Object> dslDefinitions) {
        this.dslDefinitions = dslDefinitions;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NonNull
    public Map<String, TRepositoryDefinition> getRepositories() {
        if (this.repositories == null) {
            this.repositories = new LinkedHashMap<>();
        }

        return repositories;
    }

    public void setRepositories(Map<String, TRepositoryDefinition> repositories) {
        this.repositories = repositories;
    }

    @NonNull
    public List<TMapImportDefinition> getImports() {
        if (this.imports == null) {
            this.imports = new ArrayList<>();
        }

        return imports;
    }

    public void setImports(List<TMapImportDefinition> imports) {
        this.imports = imports;
    }

    @NonNull
    public Map<String, TArtifactType> getArtifactTypes() {
        if (this.artifactTypes == null) {
            this.artifactTypes = new LinkedHashMap<>();
        }

        return artifactTypes;
    }

    public void setArtifactTypes(Map<String, TArtifactType> artifactTypes) {
        this.artifactTypes = artifactTypes;
    }

    @NonNull
    public Map<String, TDataType> getDataTypes() {
        if (this.dataTypes == null) {
            this.dataTypes = new LinkedHashMap<>();
        }

        return dataTypes;
    }

    public void setDataTypes(Map<String, TDataType> dataTypes) {
        this.dataTypes = dataTypes;
    }

    @NonNull
    public Map<String, TCapabilityType> getCapabilityTypes() {
        if (this.capabilityTypes == null) {
            this.capabilityTypes = new LinkedHashMap<>();
        }

        return capabilityTypes;
    }

    public void setCapabilityTypes(Map<String, TCapabilityType> capabilityTypes) {
        this.capabilityTypes = capabilityTypes;
    }

    @NonNull
    public Map<String, TInterfaceType> getInterfaceTypes() {
        if (this.interfaceTypes == null) {
            this.interfaceTypes = new LinkedHashMap<>();
        }

        return interfaceTypes;
    }

    public void setInterfaceTypes(Map<String, TInterfaceType> interfaceTypes) {
        this.interfaceTypes = interfaceTypes;
    }

    @NonNull
    public Map<String, TRelationshipType> getRelationshipTypes() {
        if (this.relationshipTypes == null) {
            this.relationshipTypes = new LinkedHashMap<>();
        }

        return relationshipTypes;
    }

    public void setRelationshipTypes(Map<String, TRelationshipType> relationshipTypes) {
        this.relationshipTypes = relationshipTypes;
    }

    @NonNull
    public Map<String, TNodeType> getNodeTypes() {
        if (this.nodeTypes == null) {
            this.nodeTypes = new LinkedHashMap<>();
        }

        return nodeTypes;
    }

    public void setNodeTypes(Map<String, TNodeType> nodeTypes) {
        this.nodeTypes = nodeTypes;
    }

    @NonNull
    public Map<String, TGroupType> getGroupTypes() {
        if (this.groupTypes == null) {
            this.groupTypes = new LinkedHashMap<>();
        }

        return groupTypes;
    }

    public void setGroupTypes(Map<String, TGroupType> groupTypes) {
        this.groupTypes = groupTypes;
    }

    @NonNull
    public Map<String, TPolicyType> getPolicyTypes() {
        if (this.policyTypes == null) {
            this.policyTypes = new LinkedHashMap<>();
        }

        return policyTypes;
    }

    public void setPolicyTypes(Map<String, TPolicyType> policyTypes) {
        this.policyTypes = policyTypes;
    }

    @Nullable
    public TTopologyTemplateDefinition getTopologyTemplate() {
        return topologyTemplate;
    }

    public void setTopologyTemplate(TTopologyTemplateDefinition topologyTemplate) {
        this.topologyTemplate = topologyTemplate;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder {
        private final String toscaDefinitionsVersion;
        private Metadata metadata;
        private String description;
        private Map<String, Object> dslDefinitions;
        private Map<String, TRepositoryDefinition> repositories;
        private List<TMapImportDefinition> imports;
        private Map<String, TArtifactType> artifactTypes;
        private Map<String, TDataType> dataTypes;
        private Map<String, TCapabilityType> capabilityTypes;
        private Map<String, TInterfaceType> interfaceTypes;
        private Map<String, TRelationshipType> relationshipTypes;
        private Map<String, TNodeType> nodeTypes;
        private Map<String, TGroupType> groupTypes;
        private Map<String, TPolicyType> policyTypes;
        private TTopologyTemplateDefinition topologyTemplate;

        public Builder(String toscaDefinitionsVersion) {
            this.toscaDefinitionsVersion = toscaDefinitionsVersion;
        }

        public Builder setMetadata(Metadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setDslDefinitions(Map<String, Object> dslDefinitions) {
            this.dslDefinitions = dslDefinitions;
            return this;
        }

        public Builder setRepositories(Map<String, TRepositoryDefinition> repositories) {
            this.repositories = repositories;
            return this;
        }

        public Builder setImports(List<TMapImportDefinition> imports) {
            this.imports = imports;
            return this;
        }

        public Builder setArtifactTypes(Map<String, TArtifactType> artifactTypes) {
            this.artifactTypes = artifactTypes;
            return this;
        }

        public Builder setDataTypes(Map<String, TDataType> dataTypes) {
            this.dataTypes = dataTypes;
            return this;
        }

        public Builder setCapabilityTypes(Map<String, TCapabilityType> capabilityTypes) {
            this.capabilityTypes = capabilityTypes;
            return this;
        }

        public Builder setInterfaceTypes(Map<String, TInterfaceType> interfaceTypes) {
            this.interfaceTypes = interfaceTypes;
            return this;
        }

        public Builder setRelationshipTypes(Map<String, TRelationshipType> relationshipTypes) {
            this.relationshipTypes = relationshipTypes;
            return this;
        }

        public Builder setNodeTypes(Map<String, TNodeType> nodeTypes) {
            this.nodeTypes = nodeTypes;
            return this;
        }

        public Builder setNodeType(String key, TNodeType nodeType) {
            if (this.nodeTypes == null) {
                this.nodeTypes = new LinkedHashMap<>();
            }
            this.nodeTypes.put(key, nodeType);
            return this;
        }

        public Builder setGroupTypes(Map<String, TGroupType> groupTypes) {
            this.groupTypes = groupTypes;
            return this;
        }

        public Builder setPolicyTypes(Map<String, TPolicyType> policyTypes) {
            this.policyTypes = policyTypes;
            return this;
        }

        public Builder setTopologyTemplate(TTopologyTemplateDefinition topologyTemplate) {
            this.topologyTemplate = topologyTemplate;
            return this;
        }

        public Builder addDslDefinitions(Map<String, Object> dslDefinitions) {
            if (dslDefinitions == null || dslDefinitions.isEmpty()) {
                return this;
            }

            if (this.dslDefinitions == null) {
                this.dslDefinitions = new LinkedHashMap<>(dslDefinitions);
            } else {
                this.dslDefinitions.putAll(dslDefinitions);
            }

            return this;
        }

        public Builder addDslDefinitions(String name, Object dslDefinition) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addDslDefinitions(Collections.singletonMap(name, dslDefinition));
        }

        public Builder addRepositories(Map<String, TRepositoryDefinition> repositories) {
            if (repositories == null || repositories.isEmpty()) {
                return this;
            }

            if (this.repositories == null) {
                this.repositories = new LinkedHashMap<>(repositories);
            } else {
                this.repositories.putAll(repositories);
            }

            return this;
        }

        public Builder addRepositories(String name, TRepositoryDefinition repository) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addRepositories(Collections.singletonMap(name, repository));
        }

        public Builder addImports(List<TMapImportDefinition> imports) {
            if (imports == null || imports.isEmpty()) {
                return this;
            }

            if (this.imports == null) {
                this.imports = new ArrayList<>(imports);
            } else {
                this.imports.addAll(imports);
            }

            return this;
        }

        public Builder addImports(TMapImportDefinition importDefinition) {
            if (importDefinition == null | importDefinition.isEmpty()) {
                return this;
            }

            return addImports(Collections.singletonList(importDefinition));
        }

        public Builder addImports(Map<String, TImportDefinition> imports) {
            if (imports == null || imports.isEmpty()) {
                return this;
            }

            imports.forEach((key, value) -> {
                TMapImportDefinition tmp = new TMapImportDefinition();
                tmp.put(key, value);
                addImports(tmp);
            });

            return this;
        }

        public Builder addImports(String name, TImportDefinition importDefinition) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addImports(Collections.singletonMap(name, importDefinition));
        }

        public Builder addArtifactTypes(Map<String, TArtifactType> artifactTypes) {
            if (artifactTypes == null || artifactTypes.isEmpty()) {
                return this;
            }

            if (this.artifactTypes == null) {
                this.artifactTypes = new LinkedHashMap<>(artifactTypes);
            } else {
                this.artifactTypes.putAll(artifactTypes);
            }

            return this;
        }

        public Builder addArtifactTypes(String name, TArtifactType artifactType) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addArtifactTypes(Collections.singletonMap(name, artifactType));
        }

        public Builder addDataTypes(Map<String, TDataType> dataTypes) {
            if (dataTypes == null || dataTypes.isEmpty()) {
                return this;
            }

            if (this.dataTypes == null) {
                this.dataTypes = new LinkedHashMap<>(dataTypes);
            } else {
                this.dataTypes.putAll(dataTypes);
            }

            return this;
        }

        public Builder addDataTypes(String name, TDataType dataType) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addDataTypes(Collections.singletonMap(name, dataType));
        }

        public Builder addCapabilityTypes(Map<String, TCapabilityType> capabilityTypes) {
            if (capabilityTypes == null || capabilityTypes.isEmpty()) {
                return this;
            }

            if (this.capabilityTypes == null) {
                this.capabilityTypes = new LinkedHashMap<>(capabilityTypes);
            } else {
                this.capabilityTypes.putAll(capabilityTypes);
            }

            return this;
        }

        public Builder addCapabilityTypes(String name, TCapabilityType capabilityType) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addCapabilityTypes(Collections.singletonMap(name, capabilityType));
        }

        public Builder addInterfaceTypes(Map<String, TInterfaceType> interfaceTypes) {
            if (interfaceTypes == null || interfaceTypes.isEmpty()) {
                return this;
            }

            if (this.interfaceTypes == null) {
                this.interfaceTypes = new LinkedHashMap<>(interfaceTypes);
            } else {
                this.interfaceTypes.putAll(interfaceTypes);
            }

            return this;
        }

        public Builder addInterfaceTypes(String name, TInterfaceType tInterfaceType) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addInterfaceTypes(Collections.singletonMap(name, tInterfaceType));
        }

        public Builder addRelationshipTypes(Map<String, TRelationshipType> relationshipTypes) {
            if (relationshipTypes == null || relationshipTypes.isEmpty()) {
                return this;
            }

            if (this.relationshipTypes == null) {
                this.relationshipTypes = new LinkedHashMap<>(relationshipTypes);
            } else {
                this.relationshipTypes.putAll(relationshipTypes);
            }

            return this;
        }

        public Builder addRelationshipTypes(String name, TRelationshipType relationshipType) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addRelationshipTypes(Collections.singletonMap(name, relationshipType));
        }

        public Builder addNodeTypes(Map<String, TNodeType> nodeTypes) {
            if (nodeTypes == null || nodeTypes.isEmpty()) {
                return this;
            }

            if (this.nodeTypes == null) {
                this.nodeTypes = new LinkedHashMap<>(nodeTypes);
            } else {
                this.nodeTypes.putAll(nodeTypes);
            }

            return this;
        }

        public Builder addNodeTypes(String name, TNodeType nodeType) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addNodeTypes(Collections.singletonMap(name, nodeType));
        }

        public Builder addGroupTypes(Map<String, TGroupType> groupTypes) {
            if (groupTypes == null || groupTypes.isEmpty()) {
                return this;
            }

            if (this.groupTypes == null) {
                this.groupTypes = new LinkedHashMap<>(groupTypes);
            } else {
                this.groupTypes.putAll(groupTypes);
            }

            return this;
        }

        public Builder addGroupTypes(String name, TGroupType groupType) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addGroupTypes(Collections.singletonMap(name, groupType));
        }

        public Builder addPolicyTypes(Map<String, TPolicyType> policyTypes) {
            if (policyTypes == null || policyTypes.isEmpty()) {
                return this;
            }

            if (this.policyTypes == null) {
                this.policyTypes = new LinkedHashMap<>(policyTypes);
            } else {
                this.policyTypes.putAll(policyTypes);
            }

            return this;
        }

        public Builder addPolicyTypes(String name, TPolicyType policyType) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addPolicyTypes(Collections.singletonMap(name, policyType));
        }

        public Builder addMetadata(Metadata metadata) {
            if (Objects.isNull(metadata) || metadata.isEmpty()) return this;
            if (Objects.isNull(this.metadata)) {
                this.metadata = metadata;
            } else {
                this.metadata.putAll(metadata);
            }
            return this;
        }

        public Builder addMetadata(String key, String value) {
            if (Objects.isNull(key) || Objects.isNull(value)) return this;
            return addMetadata(new Metadata().add(key, value));
        }

        public TServiceTemplate build() {
            return new TServiceTemplate(this);
        }
    }
}
