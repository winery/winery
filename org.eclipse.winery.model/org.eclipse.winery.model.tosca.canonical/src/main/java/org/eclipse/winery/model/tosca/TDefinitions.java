/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.extensions.OTComplianceRule;
import org.eclipse.winery.model.tosca.extensions.OTPatternRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTTestRefinementModel;
import org.eclipse.winery.model.tosca.visitor.Visitor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.adr.embedded.ADR;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tDefinitions", propOrder = {
    "extensions",
    "_import",
    "types",
    "serviceTemplateOrNodeTypeOrNodeTypeImplementation"
})
@XmlRootElement(name = "Definitions")
/**
 * This is the canonical model's TDefinitions type. It's a combination of the tDefinitions type from the XML-1.0 standard
 * and the Definitions type from the YAML standard and acts as a superset to both of them.
 *
 * This means instead of the XML-1.0 standard's definition of Definitions having a complex type inheriting from tDefinitions,
 * the Definitions are <b>of type</b> tDefinitions.
 *
 * This could cause issues when deserializing XML-standard conform Definitions as TDefinitions.
 * Therefor all deserialization of user-input that doesn't use winery Definitions as basis needs to be performed by the standard-specific repository implementations
 * to correctly handle the discrepancies between the standards.
 */
public class TDefinitions extends HasId implements HasName, HasTargetNamespace {
    @XmlElement(name = "Extensions")
    protected TDefinitions.Extensions extensions;
    @XmlElement(name = "Import")
    protected List<TImport> _import;
    @XmlElement(name = "Types")
    protected TDefinitions.Types types;
    @XmlElements( {
        @XmlElement(name = "RelationshipType", type = TRelationshipType.class),
        @XmlElement(name = "RelationshipTypeImplementation", type = TRelationshipTypeImplementation.class),
        @XmlElement(name = "ArtifactTemplate", type = TArtifactTemplate.class),
        @XmlElement(name = "PolicyTemplate", type = TPolicyTemplate.class),
        @XmlElement(name = "ServiceTemplate", type = TServiceTemplate.class),
        @XmlElement(name = "ArtifactType", type = TArtifactType.class),
        @XmlElement(name = "CapabilityType", type = TCapabilityType.class),
        @XmlElement(name = "DataType", type = TDataType.class),
        @XmlElement(name = "NodeType", type = TNodeType.class),
        @XmlElement(name = "NodeTypeImplementation", type = TNodeTypeImplementation.class),
        @XmlElement(name = "RequirementType", type = TRequirementType.class),
        @XmlElement(name = "PolicyType", type = TPolicyType.class),
        @XmlElement(name = "ComplianceRule", type = OTComplianceRule.class),
        @XmlElement(name = "PatternRefinementModel", type = OTPatternRefinementModel.class),
        @XmlElement(name = "TestRefinementModel", type = OTTestRefinementModel.class)
    })
    @Deprecated
    // removing this type information is not helpful, because this class does no longer need to conform to the TOSCA XML XSD
    protected List<TExtensibleElements> serviceTemplateOrNodeTypeOrNodeTypeImplementation;

    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "targetNamespace", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String targetNamespace;

    @JsonIgnore
    @XmlTransient
    protected Map<String, QName> importDefinitions = new HashMap<>();

    @Deprecated // used for XML deserialization of API request content
    public TDefinitions() {
    }

    public TDefinitions(Builder builder) {
        super(builder);
        this.extensions = builder.extensions;
        this._import = builder.imports;
        this.types = builder.types;
        this.serviceTemplateOrNodeTypeOrNodeTypeImplementation = builder.getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
        this.name = builder.name;
        this.targetNamespace = builder.target_namespace;
    }

    public Map<String, QName> getImportDefinitions() {
        return importDefinitions;
    }

    public void setImportDefinitions(Map<String, QName> importDefinitions) {
        this.importDefinitions = importDefinitions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TDefinitions)) return false;
        if (!super.equals(o)) return false;
        TDefinitions that = (TDefinitions) o;
        return Objects.equals(extensions, that.extensions) &&
            Objects.equals(_import, that._import) &&
            Objects.equals(types, that.types) &&
            Objects.equals(serviceTemplateOrNodeTypeOrNodeTypeImplementation, that.serviceTemplateOrNodeTypeOrNodeTypeImplementation) &&
            Objects.equals(name, that.name) &&
            Objects.equals(targetNamespace, that.targetNamespace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), extensions, _import, types, serviceTemplateOrNodeTypeOrNodeTypeImplementation, name, targetNamespace);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    /**
     * Convenience method for <code>this.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().get(0)</code>
     */
    @XmlTransient
    @JsonIgnore
    public TExtensibleElements getElement() {
        return this.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().get(0);
    }

    /**
     * Convenience method for clearing the current elements and setting the given one as single element
     */
    public void setElement(TExtensibleElements element) {
        this.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().clear();
        this.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(element);
    }

    public TDefinitions.@Nullable Extensions getExtensions() {
        return extensions;
    }

    public void setExtensions(TDefinitions.Extensions value) {
        this.extensions = value;
    }

    @NonNull
    public List<TImport> getImport() {
        if (_import == null) {
            _import = new ArrayList<TImport>();
        }
        return this._import;
    }

    public TDefinitions.@Nullable Types getTypes() {
        return types;
    }

    public void setTypes(TDefinitions.Types value) {
        this.types = value;
    }

    /**
     * <p> Objects of the following type(s) are allowed in the list {@link TRelationshipType } {@link
     * TRelationshipTypeImplementation } {@link TArtifactTemplate } {@link TPolicyTemplate } {@link TServiceTemplate }
     * {@link TArtifactType } {@link TCapabilityType } {@link TNodeType } {@link TNodeTypeImplementation } {@link
     * TRequirementType } {@link TPolicyType }
     */
    @NonNull
    public List<TExtensibleElements> getServiceTemplateOrNodeTypeOrNodeTypeImplementation() {
        if (serviceTemplateOrNodeTypeOrNodeTypeImplementation == null) {
            serviceTemplateOrNodeTypeOrNodeTypeImplementation = new ArrayList<>();
        }
        return this.serviceTemplateOrNodeTypeOrNodeTypeImplementation;
    }

    @JsonIgnore
    @NonNull
    public List<TRelationshipType> getRelationshipTypes() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof TRelationshipType)
            .map(TRelationshipType.class::cast)
            .collect(Collectors.toList());
    }

    @JsonIgnore
    @NonNull
    public List<TRelationshipTypeImplementation> getRelationshipTypeImplementations() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof TRelationshipTypeImplementation)
            .map(TRelationshipTypeImplementation.class::cast)
            .collect(Collectors.toList());
    }

    @JsonIgnore
    @NonNull
    public List<TArtifactTemplate> getArtifactTemplates() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof TArtifactTemplate)
            .map(TArtifactTemplate.class::cast)
            .collect(Collectors.toList());
    }

    @JsonIgnore
    @NonNull
    public List<TPolicyTemplate> getPolicyTemplates() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof TPolicyTemplate)
            .map(TPolicyTemplate.class::cast)
            .collect(Collectors.toList());
    }

    @JsonIgnore
    @NonNull
    public List<OTPatternRefinementModel> getPatternRefinementModels() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof OTPatternRefinementModel)
            .map(OTPatternRefinementModel.class::cast)
            .collect(Collectors.toList());
    }

    @JsonIgnore
    @NonNull
    public List<OTTestRefinementModel> getTestRefinementModels() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof OTTestRefinementModel)
            .map(OTTestRefinementModel.class::cast)
            .collect(Collectors.toList());
    }

    @JsonIgnore
    @NonNull
    public List<OTComplianceRule> getComplianceRules() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof OTComplianceRule)
            .map(OTComplianceRule.class::cast)
            .collect(Collectors.toList());
    }

    @JsonIgnore
    @NonNull
    public List<TServiceTemplate> getServiceTemplates() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof TServiceTemplate)
            .map(TServiceTemplate.class::cast)
            .collect(Collectors.toList());
    }

    @JsonIgnore
    @NonNull
    public List<TArtifactType> getArtifactTypes() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof TArtifactType)
            .map(TArtifactType.class::cast)
            .collect(Collectors.toList());
    }

    @JsonIgnore
    @NonNull
    public List<TCapabilityType> getCapabilityTypes() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof TCapabilityType)
            .map(TCapabilityType.class::cast)
            .collect(Collectors.toList());
    }

    @JsonIgnore
    @NonNull
    public List<TInterfaceType> getInterfaceTypes() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof TInterfaceType)
            .map(TInterfaceType.class::cast)
            .collect(Collectors.toList());
    }

    @JsonIgnore
    @NonNull
    public List<TNodeType> getNodeTypes() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof TNodeType)
            .map(TNodeType.class::cast)
            .collect(Collectors.toList());
    }

    @JsonIgnore
    @NonNull
    public List<TNodeTypeImplementation> getNodeTypeImplementations() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof TNodeTypeImplementation)
            .map(TNodeTypeImplementation.class::cast)
            .collect(Collectors.toList());
    }

    @JsonIgnore
    @NonNull
    public List<TDataType> getDataTypes() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof TDataType)
            .map(TDataType.class::cast)
            .collect(Collectors.toList());
    }

    @JsonIgnore
    @NonNull
    public List<TRequirementType> getRequirementTypes() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof TRequirementType)
            .map(TRequirementType.class::cast)
            .collect(Collectors.toList());
    }

    @JsonIgnore
    @NonNull
    public List<TPolicyType> getPolicyTypes() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof TPolicyType)
            .map(TPolicyType.class::cast)
            .collect(Collectors.toList());
    }

    @JsonIgnore
    @NonNull
    public List<TGroupType> getGroupTypes() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof TGroupType)
            .map(TGroupType.class::cast)
            .collect(Collectors.toList());
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    @NonNull
    public String getTargetNamespace() {
        return targetNamespace;
    }

    public void setTargetNamespace(String value) {
        this.targetNamespace = value;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "extension"
    })
    public static class Extensions implements Serializable {

        @XmlElement(name = "Extension", required = true)
        protected List<TExtension> extension;

        /**
         * Gets the value of the extension property.
         * <p>
         * <p>
         * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you
         * make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE>
         * method for the extension property.
         * <p>
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getExtension().add(newItem);
         * </pre>
         * <p>
         * <p>
         * <p>
         * Objects of the following type(s) are allowed in the list {@link TExtension }
         */
        @NonNull
        public List<TExtension> getExtension() {
            if (extension == null) {
                extension = new ArrayList<TExtension>();
            }
            return this.extension;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Extensions that = (Extensions) o;
            return Objects.equals(extension, that.extension);
        }

        @Override
        public int hashCode() {
            return Objects.hash(extension);
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "any"
    })
    public static class Types implements Serializable {

        @XmlAnyElement(lax = true)
        protected List<Object> any;

        @NonNull
        public List<Object> getAny() {
            if (any == null) {
                any = new ArrayList<Object>();
            }
            return this.any;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Types types = (Types) o;
            return Objects.equals(any, types.any);
        }

        @Override
        public int hashCode() {
            return Objects.hash(any);
        }
    }

    public static class Builder extends HasId.Builder<Builder> {
        private final String target_namespace;

        private TDefinitions.Extensions extensions;
        private List<TImport> imports;
        private TDefinitions.Types types;
        private List<TServiceTemplate> serviceTemplates;
        private List<TNodeType> nodeTypes;
        private List<TNodeTypeImplementation> nodeTypeImplementations;
        private List<TRelationshipType> relationshipTypes;
        private List<TRelationshipTypeImplementation> relationshipTypeImplementations;
        private List<TRequirementType> requirementTypes;
        private List<TDataType> dataTypes;
        private List<TGroupType> groupTypes;
        private List<TCapabilityType> capabilityTypes;
        private List<TArtifactType> artifactTypes;
        private List<TArtifactTemplate> artifactTemplates;
        private List<TPolicyType> policyTypes;
        private List<TInterfaceType> interfaceTypes;
        private List<TPolicyTemplate> policyTemplate;
        private List<OTPatternRefinementModel> patternRefinementModels;
        private List<OTTestRefinementModel> testRefinementModels;
        private List<TExtensibleElements> nonStandardElements;
        private String name;

        public Builder(String id, String target_namespace) {
            super(id);
            this.target_namespace = target_namespace;
        }

        public Builder setImport(List<TImport> imports) {
            this.imports = imports;
            return self();
        }

        public Builder setTypes(TDefinitions.Types types) {
            this.types = types;
            return self();
        }

        public Builder setServiceTemplates(List<TServiceTemplate> serviceTemplates) {
            this.serviceTemplates = serviceTemplates;
            return self();
        }

        public Builder setNodeTypes(List<TNodeType> nodeTypes) {
            this.nodeTypes = nodeTypes;
            return self();
        }

        public Builder setNodeTypeImplementations(List<TNodeTypeImplementation> nodeTypeImplementations) {
            this.nodeTypeImplementations = nodeTypeImplementations;
            return self();
        }

        public Builder setPatternRefinementModels(List<OTPatternRefinementModel> refinementModels) {
            this.patternRefinementModels = refinementModels;
            return self();
        }

        public Builder setTestRefinementModels(List<OTTestRefinementModel> refinementModels) {
            this.testRefinementModels = refinementModels;
            return self();
        }

        public Builder setNonStandardElements(List<TExtensibleElements> nonStandardElements) {
            this.nonStandardElements = nonStandardElements;
            return self();
        }

        public Builder setRelationshipTypes(List<TRelationshipType> relationshipTypes) {
            this.relationshipTypes = relationshipTypes;
            return self();
        }

        public Builder setRelationshipTypeImplementations(List<TRelationshipTypeImplementation> relationshipTypeImplementations) {
            this.relationshipTypeImplementations = relationshipTypeImplementations;
            return self();
        }

        public Builder setRequirementTypes(List<TRequirementType> requirementTypes) {
            this.requirementTypes = requirementTypes;
            return self();
        }

        public Builder setCapabilityTypes(List<TCapabilityType> capabilityTypes) {
            this.capabilityTypes = capabilityTypes;
            return self();
        }

        public Builder setArtifactTypes(List<TArtifactType> artifactTypes) {
            this.artifactTypes = artifactTypes;
            return self();
        }

        public Builder setArtifactTemplates(List<TArtifactTemplate> artifactTemplates) {
            this.artifactTemplates = artifactTemplates;
            return self();
        }

        public Builder setPolicyTypes(List<TPolicyType> policyTypes) {
            this.policyTypes = policyTypes;
            return self();
        }

        public Builder setInterfaceTypes(List<TInterfaceType> interfaceTypes) {
            this.interfaceTypes = interfaceTypes;
            return self();
        }

        public Builder setPolicyTemplate(List<TPolicyTemplate> policyTemplate) {
            this.policyTemplate = policyTemplate;
            return self();
        }

        public Builder setName(String name) {
            this.name = name;
            return self();
        }

        public Builder addExtensions(TDefinitions.Extensions extensions) {
            if (extensions == null || extensions.getExtension().isEmpty()) {
                return self();
            }

            if (this.extensions == null) {
                this.extensions = extensions;
            } else {
                this.extensions.getExtension().addAll(extensions.getExtension());
            }
            return self();
        }

        public Builder addExtensions(List<TExtension> extensions) {
            if (extensions == null) {
                return self();
            }

            TDefinitions.Extensions container = new TDefinitions.Extensions();
            container.getExtension().addAll(extensions);
            return addExtensions(container);
        }

        public Builder addExtensions(TExtension extensions) {
            if (extensions == null) {
                return self();
            }

            List<TExtension> tmp = new ArrayList<>();
            tmp.add(extensions);
            return addExtensions(tmp);
        }

        public Builder addImports(List<TImport> _import) {
            if (_import == null || _import.isEmpty()) {
                return self();
            }

            if (this.imports == null) {
                this.imports = _import;
            } else {
                this.imports.addAll(_import);
            }
            return self();
        }

        public Builder addImports(TImport _import) {
            if (_import == null) {
                return self();
            }

            List<TImport> tmp = new ArrayList<>();
            tmp.add(_import);
            return addImports(tmp);
        }

        public Builder addTypes(TDefinitions.Types types) {
            if (types == null || types.getAny().isEmpty()) {
                return self();
            }

            if (this.types == null) {
                this.types = types;
            } else {
                this.types.getAny().addAll(types.getAny());
            }
            return self();
        }

        public Builder addTypes(List<Object> types) {
            if (types == null) {
                return self();
            }

            TDefinitions.Types tmp = new TDefinitions.Types();
            tmp.getAny().addAll(types);
            return addTypes(tmp);
        }

        public Builder addServiceTemplates(List<TServiceTemplate> serviceTemplates) {
            if (serviceTemplates == null || serviceTemplates.isEmpty()) {
                return self();
            }

            if (this.serviceTemplates == null) {
                this.serviceTemplates = serviceTemplates;
            } else {
                this.serviceTemplates.addAll(serviceTemplates);
            }
            return self();
        }

        public Builder addServiceTemplates(TServiceTemplate serviceTemplate) {
            if (serviceTemplate == null) {
                return self();
            }

            List<TServiceTemplate> tmp = new ArrayList<>();
            tmp.add(serviceTemplate);
            return addServiceTemplates(tmp);
        }

        public Builder addNodeTypes(List<TNodeType> nodeTypes) {
            if (nodeTypes == null || nodeTypes.isEmpty()) {
                return self();
            }

            if (this.nodeTypes == null) {
                this.nodeTypes = nodeTypes;
            } else {
                this.nodeTypes.addAll(nodeTypes);
            }
            return self();
        }

        public Builder addNodeTypes(TNodeType nodeTypes) {
            if (nodeTypes == null) {
                return self();
            }

            List<TNodeType> tmp = new ArrayList<>();
            tmp.add(nodeTypes);
            return addNodeTypes(tmp);
        }

        public Builder addNodeTypeImplementations(List<TNodeTypeImplementation> nodeTypeImplementations) {
            if (nodeTypeImplementations == null || nodeTypeImplementations.isEmpty()) {
                return self();
            }

            if (this.nodeTypeImplementations == null) {
                this.nodeTypeImplementations = nodeTypeImplementations;
            } else {
                this.nodeTypeImplementations.addAll(nodeTypeImplementations);
            }
            return self();
        }

        public Builder addNodeTypeImplementations(TNodeTypeImplementation relationshipTypes) {
            if (relationshipTypes == null) {
                return self();
            }

            List<TNodeTypeImplementation> tmp = new ArrayList<>();
            tmp.add(relationshipTypes);
            return addNodeTypeImplementations(tmp);
        }

        public Builder addRelationshipTypes(List<TRelationshipType> relationshipTypes) {
            if (relationshipTypes == null || relationshipTypes.isEmpty()) {
                return self();
            }

            if (this.relationshipTypes == null) {
                this.relationshipTypes = relationshipTypes;
            } else {
                this.relationshipTypes.addAll(relationshipTypes);
            }
            return self();
        }

        public Builder addRelationshipTypes(TRelationshipType relationshipTypes) {
            if (relationshipTypes == null) {
                return self();
            }

            List<TRelationshipType> tmp = new ArrayList<>();
            tmp.add(relationshipTypes);
            return addRelationshipTypes(tmp);
        }

        public Builder addRelationshipTypeImplementations(List<TRelationshipTypeImplementation> relationshipTypeImplementations) {
            if (relationshipTypeImplementations == null || relationshipTypeImplementations.isEmpty()) {
                return self();
            }

            if (this.relationshipTypeImplementations == null) {
                this.relationshipTypeImplementations = relationshipTypeImplementations;
            } else {
                this.relationshipTypeImplementations.addAll(relationshipTypeImplementations);
            }
            return self();
        }

        public Builder addRelationshipTypeImplementations(TRelationshipTypeImplementation relationshipTypeImplementations) {
            if (relationshipTypeImplementations == null) {
                return self();
            }

            List<TRelationshipTypeImplementation> tmp = new ArrayList<>();
            tmp.add(relationshipTypeImplementations);
            return addRelationshipTypeImplementations(tmp);
        }

        public Builder addGroupTypes(List<TGroupType> groupTypes) {
            if (groupTypes == null || groupTypes.isEmpty()) {
                return self();
            }
            if (this.groupTypes == null) {
                this.groupTypes = groupTypes;
            } else {
                this.groupTypes.addAll(groupTypes);
            }
            return self();
        }

        public Builder addGroupTypes(TGroupType groupType) {
            if (groupType == null) {
                return self();
            }
            if (groupTypes == null) {
                groupTypes = new ArrayList<>();
            }
            return addGroupTypes(Collections.singletonList(groupType));
        }

        public Builder addRequirementTypes(List<TRequirementType> requirementTypes) {
            if (requirementTypes == null || requirementTypes.isEmpty()) {
                return self();
            }

            if (this.requirementTypes == null) {
                this.requirementTypes = requirementTypes;
            } else {
                this.requirementTypes.addAll(requirementTypes);
            }
            return self();
        }

        public Builder addRequirementTypes(TRequirementType requirementTypes) {
            if (requirementTypes == null) {
                return self();
            }

            List<TRequirementType> tmp = new ArrayList<>();
            tmp.add(requirementTypes);
            return addRequirementTypes(tmp);
        }

        public Builder addDataTypes(List<TDataType> dataTypes) {
            if (dataTypes == null || dataTypes.isEmpty()) {
                return self();
            }

            if (this.dataTypes == null) {
                this.dataTypes = dataTypes;
            } else {
                this.dataTypes.addAll(dataTypes);
            }
            return self();
        }

        public Builder addDataTypes(TDataType dataType) {
            if (dataType == null) {
                return self();
            }
            if (dataTypes == null) {
                dataTypes = new ArrayList<>();
            }
            return addDataTypes(Collections.singletonList(dataType));
        }

        public Builder addCapabilityTypes(List<TCapabilityType> capabilityTypes) {
            if (capabilityTypes == null || capabilityTypes.isEmpty()) {
                return self();
            }

            if (this.capabilityTypes == null) {
                this.capabilityTypes = capabilityTypes;
            } else {
                this.capabilityTypes.addAll(capabilityTypes);
            }
            return self();
        }

        public Builder addCapabilityTypes(TCapabilityType capabilityTypes) {
            if (capabilityTypes == null) {
                return self();
            }

            List<TCapabilityType> tmp = new ArrayList<>();
            tmp.add(capabilityTypes);
            return addCapabilityTypes(tmp);
        }

        public Builder addArtifactTypes(List<TArtifactType> artifactTypes) {
            if (artifactTypes == null || artifactTypes.isEmpty()) {
                return self();
            }

            if (this.artifactTypes == null) {
                this.artifactTypes = artifactTypes;
            } else {
                this.artifactTypes.addAll(artifactTypes);
            }
            return self();
        }

        public Builder addArtifactTypes(TArtifactType artifactTypes) {
            if (artifactTypes == null) {
                return self();
            }

            List<TArtifactType> tmp = new ArrayList<>();
            tmp.add(artifactTypes);
            return addArtifactTypes(tmp);
        }

        public Builder addArtifactTemplates(List<TArtifactTemplate> artifactTemplates) {
            if (artifactTemplates == null || artifactTemplates.isEmpty()) {
                return self();
            }

            if (this.artifactTemplates == null) {
                this.artifactTemplates = artifactTemplates;
            } else {
                this.artifactTemplates.addAll(artifactTemplates);
            }
            return self();
        }

        public Builder addArtifactTemplates(TArtifactTemplate artifactTemplates) {
            if (artifactTemplates == null) {
                return self();
            }

            List<TArtifactTemplate> tmp = new ArrayList<>();
            tmp.add(artifactTemplates);
            return addArtifactTemplates(tmp);
        }

        public Builder addPolicyTypes(List<TPolicyType> policyTypes) {
            if (policyTypes == null || policyTypes.isEmpty()) {
                return self();
            }

            if (this.policyTypes == null) {
                this.policyTypes = policyTypes;
            } else {
                this.policyTypes.addAll(policyTypes);
            }
            return self();
        }

        public Builder addPolicyTypes(TPolicyType policyTypes) {
            if (policyTypes == null) {
                return self();
            }

            List<TPolicyType> tmp = new ArrayList<>();
            tmp.add(policyTypes);
            return addPolicyTypes(tmp);
        }

        public Builder addInterfaceTypes(List<TInterfaceType> interfaceTypes) {
            if (interfaceTypes == null || interfaceTypes.isEmpty()) {
                return self();
            }

            if (this.interfaceTypes == null) {
                this.interfaceTypes = interfaceTypes;
            } else {
                this.interfaceTypes.addAll(interfaceTypes);
            }
            return self();
        }

        public Builder addInterfaceTypes(TInterfaceType interfaceTypes) {
            if (interfaceTypes == null) {
                return self();
            }

            List<TInterfaceType> tmp = new ArrayList<>();
            tmp.add(interfaceTypes);
            return addInterfaceTypes(tmp);
        }

        public Builder addPolicyTemplates(List<TPolicyTemplate> policyTemplate) {
            if (policyTemplate == null || policyTemplate.isEmpty()) {
                return self();
            }

            if (this.policyTemplate == null) {
                this.policyTemplate = policyTemplate;
            } else {
                this.policyTemplate.addAll(policyTemplate);
            }
            return self();
        }

        public Builder addPolicyTemplates(TPolicyTemplate policyTemplate) {
            if (policyTemplate == null) {
                return self();
            }

            List<TPolicyTemplate> tmp = new ArrayList<>();
            tmp.add(policyTemplate);
            return addPolicyTemplates(tmp);
        }

        @ADR(11)
        @Override
        public Builder self() {
            return this;
        }

        public TDefinitions build() {
            return new TDefinitions(this);
        }

        /**
         * @deprecated there is no good reason for the canonical model to conform to the TOSCA xml xsd any longer As
         * such this total removal of type information could be removed for ease of use in the frontend
         */
        @Deprecated
        public List<TExtensibleElements> getServiceTemplateOrNodeTypeOrNodeTypeImplementation() {
            List<TExtensibleElements> tmp = new ArrayList<>();
            Stream.of(
                serviceTemplates,
                nodeTypes,
                nodeTypeImplementations,
                relationshipTypes,
                relationshipTypeImplementations,
                requirementTypes,
                capabilityTypes,
                dataTypes,
                artifactTypes,
                artifactTemplates,
                policyTypes,
                policyTemplate,
                interfaceTypes,
                nonStandardElements
            ).filter(Objects::nonNull)
                .forEach(tmp::addAll);
            return tmp;
        }
    }
}
