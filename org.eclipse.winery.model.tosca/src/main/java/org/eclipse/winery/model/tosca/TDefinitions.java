/*******************************************************************************
 * Copyright (c) 2013-2017 University of Stuttgart
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial code generation using vhudson-jaxb-ri-2.1-2
 *    Christoph Kleine - hashcode, equals, builder pattern, Nullable and NonNull annotations
 *******************************************************************************/

package org.eclipse.winery.model.tosca;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.w3c.dom.Element;


/**
 * <p>Java class for tDefinitions complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tDefinitions">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tExtensibleElements">
 *       &lt;sequence>
 *         &lt;element name="Extensions" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Extension" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tExtension"
 * maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Import" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tImport" maxOccurs="unbounded"
 * minOccurs="0"/>
 *         &lt;element name="Types" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element name="ServiceTemplate" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tServiceTemplate"/>
 *           &lt;element name="NodeType" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tNodeType"/>
 *           &lt;element name="NodeTypeImplementation" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tNodeTypeImplementation"/>
 *           &lt;element name="RelationshipType" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tRelationshipType"/>
 *           &lt;element name="RelationshipTypeImplementation" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tRelationshipTypeImplementation"/>
 *           &lt;element name="RequirementType" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tRequirementType"/>
 *           &lt;element name="CapabilityType" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tCapabilityType"/>
 *           &lt;element name="ArtifactType" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tArtifactType"/>
 *           &lt;element name="ArtifactTemplate" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tArtifactTemplate"/>
 *           &lt;element name="PolicyType" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tPolicyType"/>
 *           &lt;element name="PolicyTemplate" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tPolicyTemplate"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="targetNamespace" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tDefinitions", propOrder = {
        "extensions",
        "_import",
        "types",
        "serviceTemplateOrNodeTypeOrNodeTypeImplementation"
})
@XmlSeeAlso({
        Definitions.class
})
public class TDefinitions extends TExtensibleElements {
    @XmlElement(name = "Extensions")
    protected TDefinitions.Extensions extensions;
    @XmlElement(name = "Import")
    protected List<TImport> _import;
    @XmlElement(name = "Types")
    protected TDefinitions.Types types;
    @XmlElements({
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
            @XmlElement(name = "PolicyType", type = TPolicyType.class)
    })
    protected List<TExtensibleElements> serviceTemplateOrNodeTypeOrNodeTypeImplementation;
    @XmlAttribute(name = "id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
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
        this._import = builder._import;
        this.types = builder.types;
        this.serviceTemplateOrNodeTypeOrNodeTypeImplementation = builder.getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
        this.id = builder.id;
        this.name = builder.name;
        this.targetNamespace = builder.target_namespace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TDefinitions)) return false;
        TDefinitions that = (TDefinitions) o;
        return Objects.equals(extensions, that.extensions) &&
                Objects.equals(_import, that._import) &&
                Objects.equals(types, that.types) &&
                Objects.equals(serviceTemplateOrNodeTypeOrNodeTypeImplementation, that.serviceTemplateOrNodeTypeOrNodeTypeImplementation) &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(targetNamespace, that.targetNamespace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(extensions, _import, types, serviceTemplateOrNodeTypeOrNodeTypeImplementation, id, name, targetNamespace);
    }

    /**
     * Gets the value of the extensions property.
     *
     * @return possible object is {@link TDefinitions.Extensions }
     */
    /*@Nullable*/
    public TDefinitions.Extensions getExtensions() {
        return extensions;
    }

    /**
     * Sets the value of the extensions property.
     *
     * @param value allowed object is {@link TDefinitions.Extensions }
     */
    public void setExtensions(TDefinitions.Extensions value) {
        this.extensions = value;
    }

    /**
     * Gets the value of the import property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the import property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImport().add(newItem);
     * </pre>
     *
     *
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

    /**
     * Gets the value of the types property.
     *
     * @return possible object is {@link TDefinitions.Types }
     */
    /*@Nullable*/
    public TDefinitions.Types getTypes() {
        return types;
    }

    /**
     * Sets the value of the types property.
     *
     * @param value allowed object is {@link TDefinitions.Types }
     */
    public void setTypes(TDefinitions.Types value) {
        this.types = value;
    }

    /**
     * Gets the value of the serviceTemplateOrNodeTypeOrNodeTypeImplementation property.
     *
     * <p> This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you
     * make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE>
     * method for the serviceTemplateOrNodeTypeOrNodeTypeImplementation property.
     *
     * <p> For example, to add a new item, do as follows:
     * <pre>
     *    getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(newItem);
     * </pre>
     *
     *
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

    /**
     * Gets the value of the id property.
     *
     * @return possible object is {@link String }
     */
    @NonNull
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value allowed object is {@link String }
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the targetNamespace property.
     *
     * @return possible object is {@link String }
     */
    @NonNull
    public String getTargetNamespace() {
        return targetNamespace;
    }

    /**
     * Sets the value of the targetNamespace property.
     *
     * @param value allowed object is {@link String }
     */
    public void setTargetNamespace(String value) {
        this.targetNamespace = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Extension" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tExtension"
     * maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "extension"
    })
    public static class Extensions {

        @XmlElement(name = "Extension", required = true)
        protected List<TExtension> extension;

        /**
         * Gets the value of the extension property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the extension property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getExtension().add(newItem);
         * </pre>
         *
         *
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


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "any"
    })
    public static class Types {

        @XmlAnyElement(lax = true)
        protected List<Object> any;

        /**
         * Gets the value of the any property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the any property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAny().add(newItem);
         * </pre>
         *
         *
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

    public static class Builder extends TExtensibleElements.Builder {
        private final String id;
        private final String target_namespace;

        private TDefinitions.Extensions extensions;
        private List<TImport> _import;
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
            this.id = id;
            this.target_namespace = target_namespace;
        }

        public Builder setExtensions(TDefinitions.Extensions extensions) {
            this.extensions = extensions;
            return this;
        }

        public Builder setImport(List<TImport> _import) {
            this._import = _import;
            return this;
        }

        public Builder setTypes(TDefinitions.Types types) {
            this.types = types;
            return this;
        }

        public Builder setServiceTemplates(List<TServiceTemplate> serviceTemplates) {
            this.serviceTemplates = serviceTemplates;
            return this;
        }

        public Builder setNodeTypes(List<TNodeType> nodeTypes) {
            this.nodeTypes = nodeTypes;
            return this;
        }

        public Builder setNodeTypeImplementations(List<TNodeTypeImplementation> nodeTypeImplementations) {
            this.nodeTypeImplementations = nodeTypeImplementations;
            return this;
        }

        public Builder setRelationshipTypes(List<TRelationshipType> relationshipTypes) {
            this.relationshipTypes = relationshipTypes;
            return this;
        }

        public Builder setRelationshipTypeImplementations(List<TRelationshipTypeImplementation> relationshipTypeImplementations) {
            this.relationshipTypeImplementations = relationshipTypeImplementations;
            return this;
        }

        public Builder setRequirementTypes(List<TRequirementType> requirementTypes) {
            this.requirementTypes = requirementTypes;
            return this;
        }

        public Builder setCapabilityTypes(List<TCapabilityType> capabilityTypes) {
            this.capabilityTypes = capabilityTypes;
            return this;
        }

        public Builder setArtifactTypes(List<TArtifactType> artifactTypes) {
            this.artifactTypes = artifactTypes;
            return this;
        }

        public Builder setArtifactTemplates(List<TArtifactTemplate> artifactTemplates) {
            this.artifactTemplates = artifactTemplates;
            return this;
        }

        public Builder setPolicyTypes(List<TPolicyType> policyTypes) {
            this.policyTypes = policyTypes;
            return this;
        }

        public Builder setPolicyTemplate(List<TPolicyTemplate> policyTemplate) {
            this.policyTemplate = policyTemplate;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder addExtensions(TDefinitions.Extensions extensions) {
            if (extensions == null || extensions.getExtension().isEmpty()) {
                return this;
            }

            if (this.extensions == null) {
                this.extensions = extensions;
            } else {
                this.extensions.getExtension().addAll(extensions.getExtension());
            }
            return this;
        }

        public Builder addExtensions(List<TExtension> extensions) {
            if (extensions == null) {
                return this;
            }

            List<TExtension> tmp = new ArrayList<>();
            tmp.addAll(extensions);
            return addExtensions(tmp);
        }

        public Builder addExtensions(TExtension extensions) {
            if (extensions == null) {
                return this;
            }

            List<TExtension> tmp = new ArrayList<>();
            tmp.add(extensions);
            return addExtensions(tmp);
        }

        public Builder addImports(List<TImport> _import) {
            if (_import == null || _import.isEmpty()) {
                return this;
            }

            if (this._import == null) {
                this._import = _import;
            } else {
                this._import.addAll(_import);
            }
            return this;
        }

        public Builder addImports(TImport _import) {
            if (_import == null) {
                return this;
            }

            List<TImport> tmp = new ArrayList<>();
            tmp.add(_import);
            return addImports(tmp);
        }

        public Builder addTypes(TDefinitions.Types types) {
            if (types == null || types.getAny().isEmpty()) {
                return this;
            }

            if (this.types == null) {
                this.types = types;
            } else {
                this.types.getAny().addAll(types.getAny());
            }
            return this;
        }

        public Builder addTypes(List<Object> types) {
            if (types == null) {
                return this;
            }

            TDefinitions.Types tmp = new TDefinitions.Types();
            tmp.getAny().addAll(types);
            return addTypes(tmp);
        }

        public Builder addServiceTemplates(List<TServiceTemplate> serviceTemplates) {
            if (serviceTemplates == null || serviceTemplates.isEmpty()) {
                return this;
            }

            if (this.serviceTemplates == null) {
                this.serviceTemplates = serviceTemplates;
            } else {
                this.serviceTemplates.addAll(serviceTemplates);
            }
            return this;
        }

        public Builder addServiceTemplates(TServiceTemplate serviceTemplate) {
            if (serviceTemplate == null) {
                return this;
            }

            List<TServiceTemplate> tmp = new ArrayList<>();
            tmp.add(serviceTemplate);
            return addServiceTemplates(tmp);
        }

        public Builder addNodeTypes(List<TNodeType> nodeTypes) {
            if (nodeTypes == null || nodeTypes.isEmpty()) {
                return this;
            }

            if (this.nodeTypes == null) {
                this.nodeTypes = nodeTypes;
            } else {
                this.nodeTypes.addAll(nodeTypes);
            }
            return this;
        }

        public Builder addNodeTypes(TNodeType nodeTypes) {
            if (nodeTypes == null) {
                return this;
            }

            List<TNodeType> tmp = new ArrayList<>();
            tmp.add(nodeTypes);
            return addNodeTypes(tmp);
        }

        public Builder addNodeTypeImplementations(List<TNodeTypeImplementation> nodeTypeImplementations) {
            if (nodeTypeImplementations == null || nodeTypeImplementations.isEmpty()) {
                return this;
            }

            if (this.nodeTypeImplementations == null) {
                this.nodeTypeImplementations = nodeTypeImplementations;
            } else {
                this.nodeTypeImplementations.addAll(nodeTypeImplementations);
            }
            return this;
        }

        public Builder addNodeTypeImplementations(TNodeTypeImplementation relationshipTypes) {
            if (relationshipTypes == null) {
                return this;
            }

            List<TNodeTypeImplementation> tmp = new ArrayList<>();
            tmp.add(relationshipTypes);
            return addNodeTypeImplementations(tmp);
        }

        public Builder addRelationshipTypes(List<TRelationshipType> relationshipTypes) {
            if (relationshipTypes == null || relationshipTypes.isEmpty()) {
                return this;
            }

            if (this.relationshipTypes == null) {
                this.relationshipTypes = relationshipTypes;
            } else {
                this.relationshipTypes.addAll(relationshipTypes);
            }
            return this;
        }

        public Builder addRelationshipTypes(TRelationshipType relationshipTypes) {
            if (relationshipTypes == null) {
                return this;
            }

            List<TRelationshipType> tmp = new ArrayList<>();
            tmp.add(relationshipTypes);
            return addRelationshipTypes(tmp);
        }

        public Builder addRelationshipTypeImplementations(List<TRelationshipTypeImplementation> relationshipTypeImplementations) {
            if (relationshipTypeImplementations == null || relationshipTypeImplementations.isEmpty()) {
                return this;
            }

            if (this.relationshipTypeImplementations == null) {
                this.relationshipTypeImplementations = relationshipTypeImplementations;
            } else {
                this.relationshipTypeImplementations.addAll(relationshipTypeImplementations);
            }
            return this;
        }

        public Builder addRelationshipTypeImplementations(TRelationshipTypeImplementation relationshipTypeImplementations) {
            if (relationshipTypeImplementations == null) {
                return this;
            }

            List<TRelationshipTypeImplementation> tmp = new ArrayList<>();
            tmp.add(relationshipTypeImplementations);
            return addRelationshipTypeImplementations(tmp);
        }

        public Builder addRequirementTypes(List<TRequirementType> requirementTypes) {
            if (requirementTypes == null || requirementTypes.isEmpty()) {
                return this;
            }

            if (this.requirementTypes == null) {
                this.requirementTypes = requirementTypes;
            } else {
                this.requirementTypes.addAll(requirementTypes);
            }
            return this;
        }

        public Builder addRequirementTypes(TRequirementType requirementTypes) {
            if (requirementTypes == null) {
                return this;
            }

            List<TRequirementType> tmp = new ArrayList<>();
            tmp.add(requirementTypes);
            return addRequirementTypes(tmp);
        }

        public Builder addCapabilityTypes(List<TCapabilityType> capabilityTypes) {
            if (capabilityTypes == null || capabilityTypes.isEmpty()) {
                return this;
            }

            if (this.capabilityTypes == null) {
                this.capabilityTypes = capabilityTypes;
            } else {
                this.capabilityTypes.addAll(capabilityTypes);
            }
            return this;
        }

        public Builder addCapabilityTypes(TCapabilityType capabilityTypes) {
            if (capabilityTypes == null) {
                return this;
            }

            List<TCapabilityType> tmp = new ArrayList<>();
            tmp.add(capabilityTypes);
            return addCapabilityTypes(tmp);
        }

        public Builder addArtifactTypes(List<TArtifactType> artifactTypes) {
            if (artifactTypes == null || artifactTypes.isEmpty()) {
                return this;
            }

            if (this.artifactTypes == null) {
                this.artifactTypes = artifactTypes;
            } else {
                this.artifactTypes.addAll(artifactTypes);
            }
            return this;
        }

        public Builder addArtifactTypes(TArtifactType artifactTypes) {
            if (artifactTypes == null) {
                return this;
            }

            List<TArtifactType> tmp = new ArrayList<>();
            tmp.add(artifactTypes);
            return addArtifactTypes(tmp);
        }

        public Builder addArtifactTemplates(List<TArtifactTemplate> artifactTemplates) {
            if (artifactTemplates == null || artifactTemplates.isEmpty()) {
                return this;
            }

            if (this.artifactTemplates == null) {
                this.artifactTemplates = artifactTemplates;
            } else {
                this.artifactTemplates.addAll(artifactTemplates);
            }
            return this;
        }

        public Builder addArtifactTemplates(TArtifactTemplate artifactTemplates) {
            if (artifactTemplates == null) {
                return this;
            }

            List<TArtifactTemplate> tmp = new ArrayList<>();
            tmp.add(artifactTemplates);
            return addArtifactTemplates(tmp);
        }

        public Builder addPolicyTypes(List<TPolicyType> policyTypes) {
            if (policyTypes == null || policyTypes.isEmpty()) {
                return this;
            }

            if (this.policyTypes == null) {
                this.policyTypes = policyTypes;
            } else {
                this.policyTypes.addAll(policyTypes);
            }
            return this;
        }

        public Builder addPolicyTypes(TPolicyType policyTypes) {
            if (policyTypes == null) {
                return this;
            }

            List<TPolicyType> tmp = new ArrayList<>();
            tmp.add(policyTypes);
            return addPolicyTypes(tmp);
        }

        public Builder addPolicyTemplates(List<TPolicyTemplate> policyTemplate) {
            if (policyTemplate == null || policyTemplate.isEmpty()) {
                return this;
            }

            if (this.policyTemplate == null) {
                this.policyTemplate = policyTemplate;
            } else {
                this.policyTemplate.addAll(policyTemplate);
            }
            return this;
        }

        public Builder addPolicyTemplates(TPolicyTemplate policyTemplate) {
            if (policyTemplate == null) {
                return this;
            }

            List<TPolicyTemplate> tmp = new ArrayList<>();
            tmp.add(policyTemplate);
            return addPolicyTemplates(tmp);
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
