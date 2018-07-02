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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import java.util.List;
import java.util.Objects;


/**
 * <p>Java class for tNodeTypeImplementation complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="tNodeTypeImplementation">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tExtensibleElements">
 *       &lt;sequence>
 *         &lt;element name="Tags" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tTags" minOccurs="0"/>
 *         &lt;element name="DerivedFrom" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="nodeTypeImplementationRef" use="required" type="{http://www.w3.org/2001/XMLSchema}QName"
 * />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="RequiredContainerFeatures" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tRequiredContainerFeatures"
 * minOccurs="0"/>
 *         &lt;element name="ImplementationArtifacts" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tImplementationArtifacts"
 * minOccurs="0"/>
 *         &lt;element name="DeploymentArtifacts" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tDeploymentArtifacts"
 * minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="targetNamespace" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="nodeType" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="abstract" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tBoolean" default="no" />
 *       &lt;attribute name="final" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tBoolean" default="no" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
// by using @XmlTransient at TEntityTypeImplementation, this orders *all* elements, even if IntelliJ marks them in red
// see https://stackoverflow.com/a/6790388/873282
@XmlType(name = "tNodeTypeImplementation", propOrder = {
    "tags",
    "derivedFrom",
    "requiredContainerFeatures",
    "implementationArtifacts",
    "deploymentArtifacts"
})
public class TNodeTypeImplementation extends TEntityTypeImplementation {

    @XmlElement(name = "DeploymentArtifacts")
    protected TDeploymentArtifacts deploymentArtifacts;

    @XmlElement(name = "DerivedFrom")
    protected TNodeTypeImplementation.DerivedFrom derivedFrom;

    public TNodeTypeImplementation() {
        super();
    }

    public TNodeTypeImplementation(Builder builder) {
        super(builder);
        this.derivedFrom = builder.derivedFrom;
        this.deploymentArtifacts = builder.deploymentArtifacts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TNodeTypeImplementation)) return false;
        if (!super.equals(o)) return false;
        TNodeTypeImplementation that = (TNodeTypeImplementation) o;
        return Objects.equals(tags, that.tags) &&
            Objects.equals(derivedFrom, that.derivedFrom) &&
            Objects.equals(requiredContainerFeatures, that.requiredContainerFeatures) &&
            Objects.equals(implementationArtifacts, that.implementationArtifacts) &&
            Objects.equals(deploymentArtifacts, that.deploymentArtifacts) &&
            _abstract == that._abstract &&
            _final == that._final;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tags, derivedFrom, requiredContainerFeatures, implementationArtifacts, deploymentArtifacts, _abstract, _final);
    }

    @Nullable
    public TDeploymentArtifacts getDeploymentArtifacts() {
        return deploymentArtifacts;
    }

    public void setDeploymentArtifacts(TDeploymentArtifacts value) {
        this.deploymentArtifacts = value;
    }

    public TNodeTypeImplementation.DerivedFrom getDerivedFrom() {
        return derivedFrom;
    }

    public void setDerivedFrom(HasType value) {
        this.derivedFrom = (TNodeTypeImplementation.DerivedFrom) value;
    }

    /**
     * Returns the implemented type. We have to use different namings here as the XML Schema does not have
     * TEntityTypeImplementation
     */
    @XmlAttribute(name = "nodeType", required = true)
    @NonNull
    public QName getNodeType() {
        return this.implementedType;
    }

    public void setNodeType(QName value) {
        this.implementedType = value;
    }

    /**
     * <p>Java class for anonymous complex type.
     * <p>
     * <p>The following schema fragment specifies the expected content contained within this class.
     * <p>
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="nodeTypeImplementationRef" use="required" type="{http://www.w3.org/2001/XMLSchema}QName"
     * />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DerivedFrom implements HasType {

        @XmlAttribute(name = "nodeTypeImplementationRef", required = true)
        protected QName nodeTypeImplementationRef;

        /**
         * Gets the value of the nodeTypeImplementationRef property.
         *
         * @return possible object is {@link QName }
         */
        @NonNull
        public QName getNodeTypeImplementationRef() {
            return nodeTypeImplementationRef;
        }

        /**
         * Sets the value of the nodeTypeImplementationRef property.
         *
         * @param value allowed object is {@link QName }
         */
        public void setNodeTypeImplementationRef(QName value) {
            this.nodeTypeImplementationRef = value;
        }

        public QName getType() {
            return this.getNodeTypeImplementationRef();
        }

        @Override
        public void setType(QName type) {
            this.setNodeTypeImplementationRef(type);
        }

        @Override
        public QName getTypeAsQName() {
            return this.getType();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DerivedFrom that = (DerivedFrom) o;
            return Objects.equals(nodeTypeImplementationRef, that.nodeTypeImplementationRef);
        }

        @Override
        public int hashCode() {
            return Objects.hash(nodeTypeImplementationRef);
        }
    }

    public static class Builder extends TEntityTypeImplementation.Builder<Builder> {
        private TTags tags;
        private DerivedFrom derivedFrom;
        private TRequiredContainerFeatures requiredContainerFeatures;
        private TImplementationArtifacts implementationArtifacts;
        private TDeploymentArtifacts deploymentArtifacts;
        private TBoolean _abstract;
        private TBoolean _final;

        public Builder(TExtensibleElements extensibleElements, String name, QName implementedNodeType) {
            super(extensibleElements, name, implementedNodeType);
        }

        public Builder(String name, QName implementedNodeType) {
            super(name, implementedNodeType);
        }

        public Builder setTags(TTags tags) {
            this.tags = tags;
            return this;
        }

        public Builder setDerivedFrom(TNodeTypeImplementation.DerivedFrom derivedFrom) {
            this.derivedFrom = derivedFrom;
            return this;
        }

        public Builder setRequiredContainerFeatures(TRequiredContainerFeatures requiredContainerFeatures) {
            this.requiredContainerFeatures = requiredContainerFeatures;
            return this;
        }

        public Builder setImplementationArtifacts(TImplementationArtifacts implementationArtifacts) {
            this.implementationArtifacts = implementationArtifacts;
            return this;
        }

        public Builder setDeploymentArtifacts(TDeploymentArtifacts deploymentArtifacts) {
            this.deploymentArtifacts = deploymentArtifacts;
            return this;
        }

        public Builder setAbstract(TBoolean _abstract) {
            this._abstract = _abstract;
            return this;
        }

        public Builder setFinal(TBoolean _final) {
            this._final = _final;
            return this;
        }

        public Builder addRequiredContainerFeatures(TRequiredContainerFeatures requiredContainerFeatures) {
            if (requiredContainerFeatures == null || requiredContainerFeatures.getRequiredContainerFeature().isEmpty()) {
                return this;
            }

            if (this.requiredContainerFeatures == null) {
                this.requiredContainerFeatures = requiredContainerFeatures;
            } else {
                this.requiredContainerFeatures.getRequiredContainerFeature().addAll(requiredContainerFeatures.getRequiredContainerFeature());
            }
            return this;
        }

        public Builder addRequiredContainerFeatures(List<TRequiredContainerFeature> requiredContainerFeatures) {
            if (requiredContainerFeatures == null) {
                return this;
            }

            TRequiredContainerFeatures tmp = new TRequiredContainerFeatures();
            tmp.getRequiredContainerFeature().addAll(requiredContainerFeatures);
            return addRequiredContainerFeatures(tmp);
        }

        public Builder addRequiredContainerFeatures(TRequiredContainerFeature requiredContainerFeatures) {
            if (requiredContainerFeatures == null) {
                return this;
            }

            TRequiredContainerFeatures tmp = new TRequiredContainerFeatures();
            tmp.getRequiredContainerFeature().add(requiredContainerFeatures);
            return addRequiredContainerFeatures(tmp);
        }

        public Builder addImplementationArtifacts(TImplementationArtifacts implementationArtifacts) {
            if (implementationArtifacts == null || implementationArtifacts.getImplementationArtifact().isEmpty()) {
                return this;
            }

            if (this.implementationArtifacts == null) {
                this.implementationArtifacts = implementationArtifacts;
            } else {
                this.implementationArtifacts.getImplementationArtifact().addAll(implementationArtifacts.getImplementationArtifact());
            }
            return this;
        }

        public Builder addImplementationArtifacts(List<TImplementationArtifacts.ImplementationArtifact> implementationArtifacts) {
            if (implementationArtifacts == null) {
                return this;
            }

            TImplementationArtifacts tmp = new TImplementationArtifacts();
            tmp.getImplementationArtifact().addAll(implementationArtifacts);
            return addImplementationArtifacts(tmp);
        }

        public Builder addImplementationArtifacts(TImplementationArtifacts.ImplementationArtifact implementationArtifacts) {
            if (implementationArtifacts == null) {
                return this;
            }

            TImplementationArtifacts tmp = new TImplementationArtifacts();
            tmp.getImplementationArtifact().add(implementationArtifacts);
            return addImplementationArtifacts(tmp);
        }

        public Builder addDeploymentArtifacts(TDeploymentArtifacts deploymentArtifacts) {
            if (deploymentArtifacts == null || deploymentArtifacts.getDeploymentArtifact().isEmpty()) {
                return this;
            }

            if (this.deploymentArtifacts == null) {
                this.deploymentArtifacts = deploymentArtifacts;
            } else {
                this.deploymentArtifacts.getDeploymentArtifact().addAll(deploymentArtifacts.getDeploymentArtifact());
            }
            return this;
        }

        public Builder addDeploymentArtifacts(List<TDeploymentArtifact> deploymentArtifacts) {
            if (deploymentArtifacts == null) {
                return this;
            }

            TDeploymentArtifacts tmp = new TDeploymentArtifacts();
            tmp.getDeploymentArtifact().addAll(deploymentArtifacts);
            return addDeploymentArtifacts(tmp);
        }

        public Builder addDeploymentArtifacts(TDeploymentArtifact deploymentArtifacts) {
            if (deploymentArtifacts == null) {
                return this;
            }

            TDeploymentArtifacts tmp = new TDeploymentArtifacts();
            tmp.getDeploymentArtifact().add(deploymentArtifacts);
            return addDeploymentArtifacts(tmp);
        }

        @Override
        public Builder self() {
            return this;
        }

        public TNodeTypeImplementation build() {
            return new TNodeTypeImplementation(this);
        }
    }
}
