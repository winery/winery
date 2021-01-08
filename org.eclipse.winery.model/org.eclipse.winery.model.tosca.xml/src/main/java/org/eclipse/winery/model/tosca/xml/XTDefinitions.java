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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.xml.constants.TOSCA_xml_1_0;
import org.eclipse.winery.model.tosca.xml.visitor.Visitor;

import org.eclipse.winery.model.tosca.xml.extensions.XOTComplianceRule;
import org.eclipse.winery.model.tosca.xml.extensions.XOTTopologyFragmentRefinementModel;
import org.eclipse.winery.model.tosca.xml.extensions.XOTPatternRefinementModel;
import org.eclipse.winery.model.tosca.xml.extensions.XOTTestRefinementModel;

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
@XmlSeeAlso( {
    XDefinitions.class,
})
public class XTDefinitions extends XHasId implements XHasName, XHasTargetNamespace {

    @XmlElement(name = "Extensions")
    protected XTDefinitions.Extensions extensions;
    @XmlElement(name = "Import")
    protected List<XTImport> _import;
    @XmlElement(name = "Types")
    protected XTDefinitions.Types types;
    @XmlElements( {
        @XmlElement(name = "RelationshipType", type = XTRelationshipType.class),
        @XmlElement(name = "RelationshipTypeImplementation", type = XTRelationshipTypeImplementation.class),
        @XmlElement(name = "ArtifactTemplate", type = XTArtifactTemplate.class),
        @XmlElement(name = "ServiceTemplate", type = XTServiceTemplate.class),
        @XmlElement(name = "ArtifactType", type = XTArtifactType.class),
        @XmlElement(name = "CapabilityType", type = XTCapabilityType.class),
        @XmlElement(name = "NodeType", type = XTNodeType.class),
        @XmlElement(name = "NodeTypeImplementation", type = XTNodeTypeImplementation.class),
        @XmlElement(name = "RequirementType", type = XTRequirementType.class),
        @XmlElement(name = "PolicyTemplate", type = XTPolicyTemplate.class),
        @XmlElement(name = "PolicyType", type = XTPolicyType.class),
        @XmlElement(name = "ComplianceRule", type = XOTComplianceRule.class),
        @XmlElement(name = "TopologyFragmentRefinementModel", type = XOTTopologyFragmentRefinementModel.class),
        @XmlElement(name = "PatternRefinementModel", type = XOTPatternRefinementModel.class),
        @XmlElement(name = "TestRefinementModel", type = XOTTestRefinementModel.class)
    })
    protected List<XTExtensibleElements> serviceTemplateOrNodeTypeOrNodeTypeImplementation;

    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "targetNamespace", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String targetNamespace;

    @Deprecated // required for XML deserialization
    public XTDefinitions() { }

    public XTDefinitions(Builder builder) {
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
        if (!(o instanceof XTDefinitions)) return false;
        if (!super.equals(o)) return false;
        XTDefinitions that = (XTDefinitions) o;
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
    public XTExtensibleElements getElement() {
        return this.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().get(0);
    }

    /**
     * Convenience method for clearing the current elements and setting the given one as single element
     */
    public void setElement(XTExtensibleElements element) {
        this.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().clear();
        this.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(element);
    }

    public XTDefinitions.@Nullable Extensions getExtensions() {
        return extensions;
    }

    public void setExtensions(XTDefinitions.Extensions value) {
        this.extensions = value;
    }

    @NonNull
    public List<XTImport> getImport() {
        if (_import == null) {
            _import = new ArrayList<XTImport>();
        }
        return this._import;
    }

    public XTDefinitions.@Nullable Types getTypes() {
        return types;
    }

    public void setTypes(XTDefinitions.Types value) {
        this.types = value;
    }

    /**
     * <p> Objects of the following type(s) are allowed in the list {@link XTRelationshipType } {@link
     * XTRelationshipTypeImplementation } {@link XTArtifactTemplate } {@link XTPolicyTemplate } {@link XTServiceTemplate }
     * {@link XTArtifactType } {@link XTCapabilityType } {@link XTNodeType } {@link XTNodeTypeImplementation } {@link
     * XTRequirementType } {@link XTPolicyType }
     */
    @NonNull
    public List<XTExtensibleElements> getServiceTemplateOrNodeTypeOrNodeTypeImplementation() {
        if (serviceTemplateOrNodeTypeOrNodeTypeImplementation == null) {
            serviceTemplateOrNodeTypeOrNodeTypeImplementation = new ArrayList<XTExtensibleElements>();
        }
        return this.serviceTemplateOrNodeTypeOrNodeTypeImplementation;
    }

    @NonNull
    public List<XTRelationshipType> getRelationshipTypes() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof XTRelationshipType)
            .map(XTRelationshipType.class::cast)
            .collect(Collectors.toList());
    }

    @NonNull
    public List<XTRelationshipTypeImplementation> getRelationshipTypeImplementations() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof XTRelationshipTypeImplementation)
            .map(XTRelationshipTypeImplementation.class::cast)
            .collect(Collectors.toList());
    }

    @NonNull
    public List<XTArtifactTemplate> getArtifactTemplates() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof XTArtifactTemplate)
            .map(XTArtifactTemplate.class::cast)
            .collect(Collectors.toList());
    }

    @NonNull
    public List<XTPolicyTemplate> getPolicyTemplates() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof XTPolicyTemplate)
            .map(XTPolicyTemplate.class::cast)
            .collect(Collectors.toList());
    }

    @NonNull
    public List<XTExtensibleElements> getExtensionDefinitionsChildren() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> Arrays.stream(TOSCA_xml_1_0.DEFINITIONS_ELEMENT_CLASSES)
                .noneMatch(standardType -> standardType.isInstance(x)))
            .collect(Collectors.toList());
    }

    @NonNull
    public List<XTServiceTemplate> getServiceTemplates() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof XTServiceTemplate)
            .map(XTServiceTemplate.class::cast)
            .collect(Collectors.toList());
    }

    @NonNull
    public List<XTArtifactType> getArtifactTypes() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof XTArtifactType)
            .map(XTArtifactType.class::cast)
            .collect(Collectors.toList());
    }

    @NonNull
    public List<XTCapabilityType> getCapabilityTypes() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof XTCapabilityType)
            .map(XTCapabilityType.class::cast)
            .collect(Collectors.toList());
    }

    @NonNull
    public List<XTNodeType> getNodeTypes() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof XTNodeType)
            .map(XTNodeType.class::cast)
            .collect(Collectors.toList());
    }

    @NonNull
    public List<XTNodeTypeImplementation> getNodeTypeImplementations() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof XTNodeTypeImplementation)
            .map(XTNodeTypeImplementation.class::cast)
            .collect(Collectors.toList());
    }

    @NonNull
    public List<XTRequirementType> getRequirementTypes() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof XTRequirementType)
            .map(XTRequirementType.class::cast)
            .collect(Collectors.toList());
    }

    @NonNull
    public List<XTPolicyType> getPolicyTypes() {
        return getServiceTemplateOrNodeTypeOrNodeTypeImplementation().stream()
            .filter(x -> x instanceof XTPolicyType)
            .map(XTPolicyType.class::cast)
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
        protected List<XTExtension> extension;

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
         * Objects of the following type(s) are allowed in the list {@link XTExtension }
         */
        @NonNull
        public List<XTExtension> getExtension() {
            if (extension == null) {
                extension = new ArrayList<XTExtension>();
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

    public static class Builder<T extends Builder<T>> extends XHasId.Builder<T> {
        private final String target_namespace;

        private XTDefinitions.Extensions extensions;
        private List<XTImport> imports;
        private XTDefinitions.Types types;
        private List<XTServiceTemplate> serviceTemplates;
        private List<XTNodeType> nodeTypes;
        private List<XTNodeTypeImplementation> nodeTypeImplementations;
        private List<XTRelationshipType> relationshipTypes;
        private List<XTRelationshipTypeImplementation> relationshipTypeImplementations;
        private List<XTRequirementType> requirementTypes;
        private List<XTCapabilityType> capabilityTypes;
        private List<XTArtifactType> artifactTypes;
        private List<XTArtifactTemplate> artifactTemplates;
        private List<XTPolicyType> policyTypes;
        private List<XTPolicyTemplate> policyTemplate;
        private List<XTExtensibleElements> nonStandardElements;
        private String name;

        public Builder(String id, String target_namespace) {
            super(id);
            this.target_namespace = target_namespace;
        }

        public T setExtensions(XTDefinitions.Extensions extensions) {
            this.extensions = extensions;
            return self();
        }

        public T setImport(List<XTImport> imports) {
            this.imports = imports;
            return self();
        }

        public T setTypes(XTDefinitions.Types types) {
            this.types = types;
            return self();
        }

        public T setServiceTemplates(List<XTServiceTemplate> serviceTemplates) {
            this.serviceTemplates = serviceTemplates;
            return self();
        }

        public T setNodeTypes(List<XTNodeType> nodeTypes) {
            this.nodeTypes = nodeTypes;
            return self();
        }

        public T setNodeTypeImplementations(List<XTNodeTypeImplementation> nodeTypeImplementations) {
            this.nodeTypeImplementations = nodeTypeImplementations;
            return self();
        }

        public T setRelationshipTypes(List<XTRelationshipType> relationshipTypes) {
            this.relationshipTypes = relationshipTypes;
            return self();
        }

        public T setRelationshipTypeImplementations(List<XTRelationshipTypeImplementation> relationshipTypeImplementations) {
            this.relationshipTypeImplementations = relationshipTypeImplementations;
            return self();
        }

        public T setRequirementTypes(List<XTRequirementType> requirementTypes) {
            this.requirementTypes = requirementTypes;
            return self();
        }

        public T addNonStandardElements(List<? extends XTExtensibleElements> nonStandardElements) {
            if (this.nonStandardElements == null) {
                this.nonStandardElements = new ArrayList<>();
            }
            this.nonStandardElements.addAll(nonStandardElements);
            return self();
        }

        public T setCapabilityTypes(List<XTCapabilityType> capabilityTypes) {
            this.capabilityTypes = capabilityTypes;
            return self();
        }

        public T setArtifactTypes(List<XTArtifactType> artifactTypes) {
            this.artifactTypes = artifactTypes;
            return self();
        }

        public T setArtifactTemplates(List<XTArtifactTemplate> artifactTemplates) {
            this.artifactTemplates = artifactTemplates;
            return self();
        }

        public T setPolicyTypes(List<XTPolicyType> policyTypes) {
            this.policyTypes = policyTypes;
            return self();
        }

        public T setPolicyTemplate(List<XTPolicyTemplate> policyTemplate) {
            this.policyTemplate = policyTemplate;
            return self();
        }

        public T setName(String name) {
            this.name = name;
            return self();
        }

        public T addExtensions(XTDefinitions.Extensions extensions) {
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

        public T addExtensions(List<XTExtension> extensions) {
            if (extensions == null) {
                return self();
            }

            List<XTExtension> tmp = new ArrayList<>();
            tmp.addAll(extensions);
            return addExtensions(tmp);
        }

        public T addExtensions(XTExtension extensions) {
            if (extensions == null) {
                return self();
            }

            List<XTExtension> tmp = new ArrayList<>();
            tmp.add(extensions);
            return addExtensions(tmp);
        }

        public T addImports(List<XTImport> _import) {
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

        public T addImports(XTImport _import) {
            if (_import == null) {
                return self();
            }

            List<XTImport> tmp = new ArrayList<>();
            tmp.add(_import);
            return addImports(tmp);
        }

        public T addTypes(XTDefinitions.Types types) {
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

            XTDefinitions.Types tmp = new XTDefinitions.Types();
            tmp.getAny().addAll(types);
            return addTypes(tmp);
        }

        public T addServiceTemplates(List<XTServiceTemplate> serviceTemplates) {
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

        public T addServiceTemplates(XTServiceTemplate serviceTemplate) {
            if (serviceTemplate == null) {
                return self();
            }

            List<XTServiceTemplate> tmp = new ArrayList<>();
            tmp.add(serviceTemplate);
            return addServiceTemplates(tmp);
        }

        public T addNodeTypes(List<XTNodeType> nodeTypes) {
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

        public T addNodeTypes(XTNodeType nodeTypes) {
            if (nodeTypes == null) {
                return self();
            }

            List<XTNodeType> tmp = new ArrayList<>();
            tmp.add(nodeTypes);
            return addNodeTypes(tmp);
        }

        public T addNodeTypeImplementations(List<XTNodeTypeImplementation> nodeTypeImplementations) {
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

        public T addNodeTypeImplementations(XTNodeTypeImplementation relationshipTypes) {
            if (relationshipTypes == null) {
                return self();
            }

            List<XTNodeTypeImplementation> tmp = new ArrayList<>();
            tmp.add(relationshipTypes);
            return addNodeTypeImplementations(tmp);
        }

        public T addRelationshipTypes(List<XTRelationshipType> relationshipTypes) {
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

        public T addRelationshipTypes(XTRelationshipType relationshipTypes) {
            if (relationshipTypes == null) {
                return self();
            }

            List<XTRelationshipType> tmp = new ArrayList<>();
            tmp.add(relationshipTypes);
            return addRelationshipTypes(tmp);
        }

        public T addRelationshipTypeImplementations(List<XTRelationshipTypeImplementation> relationshipTypeImplementations) {
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

        public T addRelationshipTypeImplementations(XTRelationshipTypeImplementation relationshipTypeImplementations) {
            if (relationshipTypeImplementations == null) {
                return self();
            }

            List<XTRelationshipTypeImplementation> tmp = new ArrayList<>();
            tmp.add(relationshipTypeImplementations);
            return addRelationshipTypeImplementations(tmp);
        }

        public T addRequirementTypes(List<XTRequirementType> requirementTypes) {
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

        public T addRequirementTypes(XTRequirementType requirementTypes) {
            if (requirementTypes == null) {
                return self();
            }

            List<XTRequirementType> tmp = new ArrayList<>();
            tmp.add(requirementTypes);
            return addRequirementTypes(tmp);
        }

        public T addCapabilityTypes(List<XTCapabilityType> capabilityTypes) {
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

        public T addCapabilityTypes(XTCapabilityType capabilityTypes) {
            if (capabilityTypes == null) {
                return self();
            }

            List<XTCapabilityType> tmp = new ArrayList<>();
            tmp.add(capabilityTypes);
            return addCapabilityTypes(tmp);
        }

        public T addArtifactTypes(List<XTArtifactType> artifactTypes) {
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

        public T addArtifactTypes(XTArtifactType artifactTypes) {
            if (artifactTypes == null) {
                return self();
            }

            List<XTArtifactType> tmp = new ArrayList<>();
            tmp.add(artifactTypes);
            return addArtifactTypes(tmp);
        }

        public T addArtifactTemplates(List<XTArtifactTemplate> artifactTemplates) {
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

        public T addArtifactTemplates(XTArtifactTemplate artifactTemplates) {
            if (artifactTemplates == null) {
                return self();
            }

            List<XTArtifactTemplate> tmp = new ArrayList<>();
            tmp.add(artifactTemplates);
            return addArtifactTemplates(tmp);
        }

        public T addPolicyTypes(List<XTPolicyType> policyTypes) {
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

        public T addPolicyTypes(XTPolicyType policyTypes) {
            if (policyTypes == null) {
                return self();
            }

            List<XTPolicyType> tmp = new ArrayList<>();
            tmp.add(policyTypes);
            return addPolicyTypes(tmp);
        }

        public T addPolicyTemplates(List<XTPolicyTemplate> policyTemplate) {
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

        public T addPolicyTemplates(XTPolicyTemplate policyTemplate) {
            if (policyTemplate == null) {
                return self();
            }

            List<XTPolicyTemplate> tmp = new ArrayList<>();
            tmp.add(policyTemplate);
            return addPolicyTemplates(tmp);
        }

        @ADR(11)
        @Override
        public T self() {
            return (T) this;
        }

        public XTDefinitions build() {
            return new XTDefinitions(this);
        }

        public List<XTExtensibleElements> getServiceTemplateOrNodeTypeOrNodeTypeImplementation() {
            List<XTExtensibleElements> tmp = new ArrayList<>();
            Stream.of(
                serviceTemplates,
                nodeTypes,
                nodeTypeImplementations,
                relationshipTypes,
                relationshipTypeImplementations,
                requirementTypes,
                capabilityTypes,
                artifactTypes,
                artifactTemplates,
                policyTypes,
                policyTemplate,
                nonStandardElements
            ).filter(Objects::nonNull)
                .forEach(tmp::addAll);
            return tmp;
        }
    }
}
