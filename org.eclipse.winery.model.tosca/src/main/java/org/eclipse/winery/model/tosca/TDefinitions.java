/*******************************************************************************
 * Copyright (c) 2013-2018 Contributors to the Eclipse Foundation
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.adr.embedded.ADR;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tDefinitions", propOrder = {
    "extensions",
    "_import",
    "types",
    "serviceTemplateOrNodeTypeOrNodeTypeImplementation"
})
@XmlSeeAlso( {
    Definitions.class
})
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
        @XmlElement(name = "NodeType", type = TNodeType.class),
        @XmlElement(name = "NodeTypeImplementation", type = TNodeTypeImplementation.class),
        @XmlElement(name = "RequirementType", type = TRequirementType.class),
        @XmlElement(name = "PolicyType", type = TPolicyType.class),
        @XmlElement(name = "Compliancerule", type = TComplianceRule.class)
    })
    protected List<TExtensibleElements> serviceTemplateOrNodeTypeOrNodeTypeImplementation;

    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "targetNamespace", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String targetNamespace;

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

    /*@Nullable*/
    public TDefinitions.Extensions getExtensions() {
        return extensions;
    }

    public void setExtensions(TDefinitions.Extensions value) {
        this.extensions = value;
    }

    /**
     * Gets the value of the import property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the import property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImport().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TImport }
     */
    @NonNull
    public List<TImport> getImport() {
        if (_import == null) {
            _import = new ArrayList<TImport>();
        }
        return this._import;
    }

    /*@Nullable*/
    public TDefinitions.Types getTypes() {
        return types;
    }

    public void setTypes(TDefinitions.Types value) {
        this.types = value;
    }

    /**
     * Gets the value of the serviceTemplateOrNodeTypeOrNodeTypeImplementation property.
     * <p>
     * <p> This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you
     * make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE>
     * method for the serviceTemplateOrNodeTypeOrNodeTypeImplementation property.
     * <p>
     * <p> For example, to add a new item, do as follows:
     * <pre>
     *    getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p> Objects of the following type(s) are allowed in the list {@link TRelationshipType } {@link
     * TRelationshipTypeImplementation } {@link TArtifactTemplate } {@link TPolicyTemplate } {@link TServiceTemplate }
     * {@link TArtifactType } {@link TCapabilityType } {@link TNodeType } {@link TNodeTypeImplementation } {@link
     * TRequirementType } {@link TPolicyType }
     */
    @NonNull
    public List<TExtensibleElements> getServiceTemplateOrNodeTypeOrNodeTypeImplementation() {
        if (serviceTemplateOrNodeTypeOrNodeTypeImplementation == null) {
            serviceTemplateOrNodeTypeOrNodeTypeImplementation = new ArrayList<TExtensibleElements>();
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
    public static class Extensions {

        @XmlElement(name = "Extension", required = true)
        protected List<TExtension> extension;

        /**
         * Gets the value of the extension property.
         * <p>
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the extension property.
         * <p>
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getExtension().add(newItem);
         * </pre>
         * <p>
         * <p>
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TExtension }
         */
        @NonNull
        public List<TExtension> getExtension() {
            if (extension == null) {
                extension = new ArrayList<TExtension>();
            }
            return this.extension;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "any"
    })
    public static class Types {

        @XmlAnyElement(lax = true)
        protected List<Object> any;

        /**
         * Gets the value of the any property.
         * <p>
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the any property.
         * <p>
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAny().add(newItem);
         * </pre>
         * <p>
         * <p>
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Element }
         * {@link Object }
         */
        @NonNull
        public List<Object> getAny() {
            if (any == null) {
                any = new ArrayList<Object>();
            }
            return this.any;
        }
    }

    public static class Builder<T extends Builder<T>> extends HasId.Builder<T> {
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
        private List<TCapabilityType> capabilityTypes;
        private List<TArtifactType> artifactTypes;
        private List<TArtifactTemplate> artifactTemplates;
        private List<TPolicyType> policyTypes;
        private List<TPolicyTemplate> policyTemplate;
        private String name;

        public Builder(String id, String target_namespace) {
            super(id);
            this.target_namespace = target_namespace;
        }

        public Builder setExtensions(TDefinitions.Extensions extensions) {
            this.extensions = extensions;
            return self();
        }

        public T setImport(List<TImport> imports) {
            this.imports = imports;
            return self();
        }

        public T setTypes(TDefinitions.Types types) {
            this.types = types;
            return self();
        }

        public T setServiceTemplates(List<TServiceTemplate> serviceTemplates) {
            this.serviceTemplates = serviceTemplates;
            return self();
        }

        public T setNodeTypes(List<TNodeType> nodeTypes) {
            this.nodeTypes = nodeTypes;
            return self();
        }

        public T setNodeTypeImplementations(List<TNodeTypeImplementation> nodeTypeImplementations) {
            this.nodeTypeImplementations = nodeTypeImplementations;
            return self();
        }

        public T setRelationshipTypes(List<TRelationshipType> relationshipTypes) {
            this.relationshipTypes = relationshipTypes;
            return self();
        }

        public T setRelationshipTypeImplementations(List<TRelationshipTypeImplementation> relationshipTypeImplementations) {
            this.relationshipTypeImplementations = relationshipTypeImplementations;
            return self();
        }

        public T setRequirementTypes(List<TRequirementType> requirementTypes) {
            this.requirementTypes = requirementTypes;
            return self();
        }

        public T setCapabilityTypes(List<TCapabilityType> capabilityTypes) {
            this.capabilityTypes = capabilityTypes;
            return self();
        }

        public T setArtifactTypes(List<TArtifactType> artifactTypes) {
            this.artifactTypes = artifactTypes;
            return self();
        }

        public T setArtifactTemplates(List<TArtifactTemplate> artifactTemplates) {
            this.artifactTemplates = artifactTemplates;
            return self();
        }

        public T setPolicyTypes(List<TPolicyType> policyTypes) {
            this.policyTypes = policyTypes;
            return self();
        }

        public T setPolicyTemplate(List<TPolicyTemplate> policyTemplate) {
            this.policyTemplate = policyTemplate;
            return self();
        }

        public T setName(String name) {
            this.name = name;
            return self();
        }

        public T addExtensions(TDefinitions.Extensions extensions) {
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

        public T addExtensions(List<TExtension> extensions) {
            if (extensions == null) {
                return self();
            }

            List<TExtension> tmp = new ArrayList<>();
            tmp.addAll(extensions);
            return addExtensions(tmp);
        }

        public T addExtensions(TExtension extensions) {
            if (extensions == null) {
                return self();
            }

            List<TExtension> tmp = new ArrayList<>();
            tmp.add(extensions);
            return addExtensions(tmp);
        }

        public T addImports(List<TImport> _import) {
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

        public T addImports(TImport _import) {
            if (_import == null) {
                return self();
            }

            List<TImport> tmp = new ArrayList<>();
            tmp.add(_import);
            return addImports(tmp);
        }

        public T addTypes(TDefinitions.Types types) {
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

        public T addTypes(List<Object> types) {
            if (types == null) {
                return self();
            }

            TDefinitions.Types tmp = new TDefinitions.Types();
            tmp.getAny().addAll(types);
            return addTypes(tmp);
        }

        public T addServiceTemplates(List<TServiceTemplate> serviceTemplates) {
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

        public T addServiceTemplates(TServiceTemplate serviceTemplate) {
            if (serviceTemplate == null) {
                return self();
            }

            List<TServiceTemplate> tmp = new ArrayList<>();
            tmp.add(serviceTemplate);
            return addServiceTemplates(tmp);
        }

        public T addNodeTypes(List<TNodeType> nodeTypes) {
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

        public T addNodeTypes(TNodeType nodeTypes) {
            if (nodeTypes == null) {
                return self();
            }

            List<TNodeType> tmp = new ArrayList<>();
            tmp.add(nodeTypes);
            return addNodeTypes(tmp);
        }

        public T addNodeTypeImplementations(List<TNodeTypeImplementation> nodeTypeImplementations) {
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

        public T addNodeTypeImplementations(TNodeTypeImplementation relationshipTypes) {
            if (relationshipTypes == null) {
                return self();
            }

            List<TNodeTypeImplementation> tmp = new ArrayList<>();
            tmp.add(relationshipTypes);
            return addNodeTypeImplementations(tmp);
        }

        public T addRelationshipTypes(List<TRelationshipType> relationshipTypes) {
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

        public T addRelationshipTypes(TRelationshipType relationshipTypes) {
            if (relationshipTypes == null) {
                return self();
            }

            List<TRelationshipType> tmp = new ArrayList<>();
            tmp.add(relationshipTypes);
            return addRelationshipTypes(tmp);
        }

        public T addRelationshipTypeImplementations(List<TRelationshipTypeImplementation> relationshipTypeImplementations) {
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

        public T addRelationshipTypeImplementations(TRelationshipTypeImplementation relationshipTypeImplementations) {
            if (relationshipTypeImplementations == null) {
                return self();
            }

            List<TRelationshipTypeImplementation> tmp = new ArrayList<>();
            tmp.add(relationshipTypeImplementations);
            return addRelationshipTypeImplementations(tmp);
        }

        public T addRequirementTypes(List<TRequirementType> requirementTypes) {
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

        public T addRequirementTypes(TRequirementType requirementTypes) {
            if (requirementTypes == null) {
                return self();
            }

            List<TRequirementType> tmp = new ArrayList<>();
            tmp.add(requirementTypes);
            return addRequirementTypes(tmp);
        }

        public T addCapabilityTypes(List<TCapabilityType> capabilityTypes) {
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

        public T addCapabilityTypes(TCapabilityType capabilityTypes) {
            if (capabilityTypes == null) {
                return self();
            }

            List<TCapabilityType> tmp = new ArrayList<>();
            tmp.add(capabilityTypes);
            return addCapabilityTypes(tmp);
        }

        public T addArtifactTypes(List<TArtifactType> artifactTypes) {
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

        public T addArtifactTypes(TArtifactType artifactTypes) {
            if (artifactTypes == null) {
                return self();
            }

            List<TArtifactType> tmp = new ArrayList<>();
            tmp.add(artifactTypes);
            return addArtifactTypes(tmp);
        }

        public T addArtifactTemplates(List<TArtifactTemplate> artifactTemplates) {
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

        public T addArtifactTemplates(TArtifactTemplate artifactTemplates) {
            if (artifactTemplates == null) {
                return self();
            }

            List<TArtifactTemplate> tmp = new ArrayList<>();
            tmp.add(artifactTemplates);
            return addArtifactTemplates(tmp);
        }

        public T addPolicyTypes(List<TPolicyType> policyTypes) {
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

        public T addPolicyTypes(TPolicyType policyTypes) {
            if (policyTypes == null) {
                return self();
            }

            List<TPolicyType> tmp = new ArrayList<>();
            tmp.add(policyTypes);
            return addPolicyTypes(tmp);
        }

        public T addPolicyTemplates(List<TPolicyTemplate> policyTemplate) {
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

        public T addPolicyTemplates(TPolicyTemplate policyTemplate) {
            if (policyTemplate == null) {
                return self();
            }

            List<TPolicyTemplate> tmp = new ArrayList<>();
            tmp.add(policyTemplate);
            return addPolicyTemplates(tmp);
        }

        @ADR(11)
        @Override
        public T self() {
            return (T) this;
        }

        public TDefinitions build() {
            return new TDefinitions(this);
        }

        public List<TExtensibleElements> getServiceTemplateOrNodeTypeOrNodeTypeImplementation() {
            List<TExtensibleElements> tmp = new ArrayList<>();

            Optional.ofNullable(serviceTemplates).ifPresent(tmp::addAll);
            Optional.ofNullable(nodeTypes).ifPresent(tmp::addAll);
            Optional.ofNullable(nodeTypeImplementations).ifPresent(tmp::addAll);
            Optional.ofNullable(relationshipTypes).ifPresent(tmp::addAll);
            Optional.ofNullable(relationshipTypeImplementations).ifPresent(tmp::addAll);
            Optional.ofNullable(requirementTypes).ifPresent(tmp::addAll);
            Optional.ofNullable(capabilityTypes).ifPresent(tmp::addAll);
            Optional.ofNullable(artifactTypes).ifPresent(tmp::addAll);
            Optional.ofNullable(artifactTemplates).ifPresent(tmp::addAll);
            Optional.ofNullable(policyTypes).ifPresent(tmp::addAll);
            Optional.ofNullable(policyTemplate).ifPresent(tmp::addAll);
            return tmp;
        }
    }
}
