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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.winery.model.tosca.yaml.support.Metadata;
import org.eclipse.winery.model.tosca.yaml.support.YTMapImportDefinition;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class YTServiceTemplate implements VisitorNode {
    private String toscaDefinitionsVersion;
    private Metadata metadata;
    private String description;
    private Map<String, Object> dslDefinitions;
    private Map<String, YTRepositoryDefinition> repositories;
    private List<YTMapImportDefinition> imports;
    private Map<String, YTArtifactType> artifactTypes;
    private Map<String, YTDataType> dataTypes;
    private Map<String, YTCapabilityType> capabilityTypes;
    private Map<String, YTInterfaceType> interfaceTypes;
    private Map<String, YTRelationshipType> relationshipTypes;
    private Map<String, YTNodeType> nodeTypes;
    private Map<String, YTGroupType> groupTypes;
    private Map<String, YTPolicyType> policyTypes;
    private YTTopologyTemplateDefinition topologyTemplate;

    protected YTServiceTemplate(Builder builder) {
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
        if (!(o instanceof YTServiceTemplate)) return false;
        YTServiceTemplate that = (YTServiceTemplate) o;
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

    @Override
    public String toString() {
        return "TServiceTemplate{" +
            "toscaDefinitionsVersion='" + getToscaDefinitionsVersion() + '\'' +
            ", metadata=" + getMetadata() +
            ", description='" + getDescription() + '\'' +
            ", dslDefinitions=" + getDslDefinitions() +
            ", repositories=" + getRepositories() +
            ", imports=" + getImports() +
            ", artifactTypes=" + getArtifactTypes() +
            ", dataTypes=" + getDataTypes() +
            ", capabilityTypes=" + getCapabilityTypes() +
            ", interfaceTypes=" + getInterfaceTypes() +
            ", relationshipTypes=" + getRelationshipTypes() +
            ", nodeTypes=" + getNodeTypes() +
            ", groupTypes=" + getGroupTypes() +
            ", policyTypes=" + getPolicyTypes() +
            ", topologyTemplate=" + getTopologyTemplate() +
            '}';
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
    public Map<String, YTRepositoryDefinition> getRepositories() {
        if (this.repositories == null) {
            this.repositories = new LinkedHashMap<>();
        }

        return repositories;
    }

    public void setRepositories(Map<String, YTRepositoryDefinition> repositories) {
        this.repositories = repositories;
    }

    @NonNull
    public List<YTMapImportDefinition> getImports() {
        if (this.imports == null) {
            this.imports = new ArrayList<>();
        }

        return imports;
    }

    public void setImports(List<YTMapImportDefinition> imports) {
        this.imports = imports;
    }

    @NonNull
    public Map<String, YTArtifactType> getArtifactTypes() {
        if (this.artifactTypes == null) {
            this.artifactTypes = new LinkedHashMap<>();
        }

        return artifactTypes;
    }

    public void setArtifactTypes(Map<String, YTArtifactType> artifactTypes) {
        this.artifactTypes = artifactTypes;
    }

    @NonNull
    public Map<String, YTDataType> getDataTypes() {
        if (this.dataTypes == null) {
            this.dataTypes = new LinkedHashMap<>();
        }

        return dataTypes;
    }

    public void setDataTypes(Map<String, YTDataType> dataTypes) {
        this.dataTypes = dataTypes;
    }

    @NonNull
    public Map<String, YTCapabilityType> getCapabilityTypes() {
        if (this.capabilityTypes == null) {
            this.capabilityTypes = new LinkedHashMap<>();
        }

        return capabilityTypes;
    }

    public void setCapabilityTypes(Map<String, YTCapabilityType> capabilityTypes) {
        this.capabilityTypes = capabilityTypes;
    }

    @NonNull
    public Map<String, YTInterfaceType> getInterfaceTypes() {
        if (this.interfaceTypes == null) {
            this.interfaceTypes = new LinkedHashMap<>();
        }

        return interfaceTypes;
    }

    public void setInterfaceTypes(Map<String, YTInterfaceType> interfaceTypes) {
        this.interfaceTypes = interfaceTypes;
    }

    @NonNull
    public Map<String, YTRelationshipType> getRelationshipTypes() {
        if (this.relationshipTypes == null) {
            this.relationshipTypes = new LinkedHashMap<>();
        }

        return relationshipTypes;
    }

    public void setRelationshipTypes(Map<String, YTRelationshipType> relationshipTypes) {
        this.relationshipTypes = relationshipTypes;
    }

    @NonNull
    public Map<String, YTNodeType> getNodeTypes() {
        if (this.nodeTypes == null) {
            this.nodeTypes = new LinkedHashMap<>();
        }

        return nodeTypes;
    }

    public void setNodeTypes(Map<String, YTNodeType> nodeTypes) {
        this.nodeTypes = nodeTypes;
    }

    @NonNull
    public Map<String, YTGroupType> getGroupTypes() {
        if (this.groupTypes == null) {
            this.groupTypes = new LinkedHashMap<>();
        }

        return groupTypes;
    }

    public void setGroupTypes(Map<String, YTGroupType> groupTypes) {
        this.groupTypes = groupTypes;
    }

    @NonNull
    public Map<String, YTPolicyType> getPolicyTypes() {
        if (this.policyTypes == null) {
            this.policyTypes = new LinkedHashMap<>();
        }

        return policyTypes;
    }

    public void setPolicyTypes(Map<String, YTPolicyType> policyTypes) {
        this.policyTypes = policyTypes;
    }

    public YTTopologyTemplateDefinition getTopologyTemplate() {
        return topologyTemplate;
    }

    public void setTopologyTemplate(YTTopologyTemplateDefinition topologyTemplate) {
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
        private Map<String, YTRepositoryDefinition> repositories;
        private List<YTMapImportDefinition> imports;
        private Map<String, YTArtifactType> artifactTypes;
        private Map<String, YTDataType> dataTypes;
        private Map<String, YTCapabilityType> capabilityTypes;
        private Map<String, YTInterfaceType> interfaceTypes;
        private Map<String, YTRelationshipType> relationshipTypes;
        private Map<String, YTNodeType> nodeTypes;
        private Map<String, YTGroupType> groupTypes;
        private Map<String, YTPolicyType> policyTypes;
        private YTTopologyTemplateDefinition topologyTemplate;

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

        public Builder setRepositories(Map<String, YTRepositoryDefinition> repositories) {
            this.repositories = repositories;
            return this;
        }

        public Builder setImports(List<YTMapImportDefinition> imports) {
            this.imports = imports;
            return this;
        }

        public Builder setArtifactTypes(Map<String, YTArtifactType> artifactTypes) {
            this.artifactTypes = artifactTypes;
            return this;
        }

        public Builder setDataTypes(Map<String, YTDataType> dataTypes) {
            this.dataTypes = dataTypes;
            return this;
        }

        public Builder setCapabilityTypes(Map<String, YTCapabilityType> capabilityTypes) {
            this.capabilityTypes = capabilityTypes;
            return this;
        }

        public Builder setInterfaceTypes(Map<String, YTInterfaceType> interfaceTypes) {
            this.interfaceTypes = interfaceTypes;
            return this;
        }

        public Builder setRelationshipTypes(Map<String, YTRelationshipType> relationshipTypes) {
            this.relationshipTypes = relationshipTypes;
            return this;
        }

        public Builder setNodeTypes(Map<String, YTNodeType> nodeTypes) {
            this.nodeTypes = nodeTypes;
            return this;
        }

        public Builder setNodeType(String key, YTNodeType nodeType) {
            if (this.nodeTypes == null) {
                this.nodeTypes = new LinkedHashMap<>();
            }
            this.nodeTypes.put(key, nodeType);
            return this;
        }

        public Builder setGroupTypes(Map<String, YTGroupType> groupTypes) {
            this.groupTypes = groupTypes;
            return this;
        }

        public Builder setPolicyTypes(Map<String, YTPolicyType> policyTypes) {
            this.policyTypes = policyTypes;
            return this;
        }

        public Builder setTopologyTemplate(YTTopologyTemplateDefinition topologyTemplate) {
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

        public Builder addRepositories(Map<String, YTRepositoryDefinition> repositories) {
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

        public Builder addRepositories(String name, YTRepositoryDefinition repository) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addRepositories(Collections.singletonMap(name, repository));
        }

        public Builder addImports(List<YTMapImportDefinition> imports) {
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

        public Builder addImports(YTMapImportDefinition importDefinition) {
            if (importDefinition == null | importDefinition.isEmpty()) {
                return this;
            }

            return addImports(Collections.singletonList(importDefinition));
        }

        public Builder addImports(Map<String, YTImportDefinition> imports) {
            if (imports == null || imports.isEmpty()) {
                return this;
            }

            imports.forEach((key, value) -> {
                YTMapImportDefinition tmp = new YTMapImportDefinition();
                tmp.put(key, value);
                addImports(tmp);
            });

            return this;
        }

        public Builder addImports(String name, YTImportDefinition importDefinition) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addImports(Collections.singletonMap(name, importDefinition));
        }

        public Builder addArtifactTypes(Map<String, YTArtifactType> artifactTypes) {
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

        public Builder addArtifactTypes(String name, YTArtifactType artifactType) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addArtifactTypes(Collections.singletonMap(name, artifactType));
        }

        public Builder addDataTypes(Map<String, YTDataType> dataTypes) {
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

        public Builder addDataTypes(String name, YTDataType dataType) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addDataTypes(Collections.singletonMap(name, dataType));
        }

        public Builder addCapabilityTypes(Map<String, YTCapabilityType> capabilityTypes) {
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

        public Builder addCapabilityTypes(String name, YTCapabilityType capabilityType) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addCapabilityTypes(Collections.singletonMap(name, capabilityType));
        }

        public Builder addInterfaceTypes(Map<String, YTInterfaceType> interfaceTypes) {
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

        public Builder addInterfaceTypes(String name, YTInterfaceType tInterfaceType) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addInterfaceTypes(Collections.singletonMap(name, tInterfaceType));
        }

        public Builder addRelationshipTypes(Map<String, YTRelationshipType> relationshipTypes) {
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

        public Builder addRelationshipTypes(String name, YTRelationshipType relationshipType) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addRelationshipTypes(Collections.singletonMap(name, relationshipType));
        }

        public Builder addNodeTypes(Map<String, YTNodeType> nodeTypes) {
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

        public Builder addNodeTypes(String name, YTNodeType nodeType) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addNodeTypes(Collections.singletonMap(name, nodeType));
        }

        public Builder addGroupTypes(Map<String, YTGroupType> groupTypes) {
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

        public Builder addGroupTypes(String name, YTGroupType groupType) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addGroupTypes(Collections.singletonMap(name, groupType));
        }

        public Builder addPolicyTypes(Map<String, YTPolicyType> policyTypes) {
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

        public Builder addPolicyTypes(String name, YTPolicyType policyType) {
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

        public YTServiceTemplate build() {
            return new YTServiceTemplate(this);
        }
    }
}
