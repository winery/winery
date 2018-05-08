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

import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * <p>Java class for tRelationshipType complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="tRelationshipType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tEntityType">
 *       &lt;sequence>
 *         &lt;element name="InstanceStates" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tTopologyElementInstanceStates"
 * minOccurs="0"/>
 *         &lt;element name="SourceInterfaces" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Interface" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tInterface"
 * maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="TargetInterfaces" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Interface" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tInterface"
 * maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ValidSource" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="typeRef" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ValidTarget" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="typeRef" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tRelationshipType", propOrder = {
    "instanceStates",
    "sourceInterfaces",
    "targetInterfaces",
    "validSource",
    "validTarget"
})
public class TRelationshipType extends TEntityType {
    @XmlElement(name = "InstanceStates")
    protected TTopologyElementInstanceStates instanceStates;
    @XmlElement(name = "SourceInterfaces")
    protected TRelationshipType.SourceInterfaces sourceInterfaces;
    @XmlElement(name = "TargetInterfaces")
    protected TRelationshipType.TargetInterfaces targetInterfaces;
    @XmlElement(name = "ValidSource")
    protected TRelationshipType.ValidSource validSource;
    @XmlElement(name = "ValidTarget")
    protected TRelationshipType.ValidTarget validTarget;

    public TRelationshipType() {
    }

    public TRelationshipType(Builder builder) {
        super(builder);
        this.instanceStates = builder.instanceStates;
        this.sourceInterfaces = builder.sourceInterfaces;
        this.targetInterfaces = builder.targetInterfaces;
        this.validSource = builder.validSource;
        this.validTarget = builder.validTarget;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TRelationshipType)) return false;
        if (!super.equals(o)) return false;
        TRelationshipType that = (TRelationshipType) o;
        return Objects.equals(instanceStates, that.instanceStates) &&
            Objects.equals(sourceInterfaces, that.sourceInterfaces) &&
            Objects.equals(targetInterfaces, that.targetInterfaces) &&
            Objects.equals(validSource, that.validSource) &&
            Objects.equals(validTarget, that.validTarget);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), instanceStates, sourceInterfaces, targetInterfaces, validSource, validTarget);
    }

    /**
     * Gets the value of the instanceStates property.
     *
     * @return possible object is {@link TTopologyElementInstanceStates }
     */
    /*@Nullable*/
    public TTopologyElementInstanceStates getInstanceStates() {
        return instanceStates;
    }

    /**
     * Sets the value of the instanceStates property.
     *
     * @param value allowed object is {@link TTopologyElementInstanceStates }
     */
    public void setInstanceStates(TTopologyElementInstanceStates value) {
        this.instanceStates = value;
    }

    /**
     * Gets the value of the sourceInterfaces property.
     *
     * @return possible object is {@link TRelationshipType.SourceInterfaces }
     */
    /*@Nullable*/
    public TRelationshipType.SourceInterfaces getSourceInterfaces() {
        return sourceInterfaces;
    }

    /**
     * Sets the value of the sourceInterfaces property.
     *
     * @param value allowed object is {@link TRelationshipType.SourceInterfaces }
     */
    public void setSourceInterfaces(TRelationshipType.SourceInterfaces value) {
        this.sourceInterfaces = value;
    }

    /**
     * Gets the value of the targetInterfaces property.
     *
     * @return possible object is {@link TRelationshipType.TargetInterfaces }
     */
    /*@Nullable*/
    public TRelationshipType.TargetInterfaces getTargetInterfaces() {
        return targetInterfaces;
    }

    /**
     * Sets the value of the targetInterfaces property.
     *
     * @param value allowed object is {@link TRelationshipType.TargetInterfaces }
     */
    public void setTargetInterfaces(TRelationshipType.TargetInterfaces value) {
        this.targetInterfaces = value;
    }

    /**
     * Gets the value of the validSource property.
     *
     * @return possible object is {@link TRelationshipType.ValidSource }
     */
    /*@Nullable*/
    public TRelationshipType.ValidSource getValidSource() {
        return validSource;
    }

    /**
     * Sets the value of the validSource property.
     *
     * @param value allowed object is {@link TRelationshipType.ValidSource }
     */
    public void setValidSource(TRelationshipType.ValidSource value) {
        this.validSource = value;
    }

    /**
     * Gets the value of the validTarget property.
     *
     * @return possible object is {@link TRelationshipType.ValidTarget }
     */
    /*@Nullable*/
    public TRelationshipType.ValidTarget getValidTarget() {
        return validTarget;
    }

    /**
     * Sets the value of the validTarget property.
     *
     * @param value allowed object is {@link TRelationshipType.ValidTarget }
     */
    public void setValidTarget(TRelationshipType.ValidTarget value) {
        this.validTarget = value;
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
     *       &lt;sequence>
     *         &lt;element name="Interface" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tInterface"
     * maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "_interface"
    })
    public static class SourceInterfaces {

        @XmlElement(name = "Interface", required = true)
        protected List<TInterface> _interface;

        /**
         * Gets the value of the interface property.
         * <p>
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the interface property.
         * <p>
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getInterface().add(newItem);
         * </pre>
         * <p>
         * <p>
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TInterface }
         */
        @NonNull
        public List<TInterface> getInterface() {
            if (_interface == null) {
                _interface = new ArrayList<TInterface>();
            }
            return this._interface;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SourceInterfaces that = (SourceInterfaces) o;
            return Objects.equals(_interface, that._interface);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_interface);
        }
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
     *       &lt;sequence>
     *         &lt;element name="Interface" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tInterface"
     * maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "_interface"
    })
    public static class TargetInterfaces {

        @XmlElement(name = "Interface", required = true)
        protected List<TInterface> _interface;

        /**
         * Gets the value of the interface property.
         * <p>
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the interface property.
         * <p>
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getInterface().add(newItem);
         * </pre>
         * <p>
         * <p>
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TInterface }
         */
        @NonNull
        public List<TInterface> getInterface() {
            if (_interface == null) {
                _interface = new ArrayList<TInterface>();
            }
            return this._interface;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TargetInterfaces that = (TargetInterfaces) o;
            return Objects.equals(_interface, that._interface);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_interface);
        }
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
     *       &lt;attribute name="typeRef" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class ValidSource {

        @XmlAttribute(name = "typeRef", required = true)
        protected QName typeRef;

        /**
         * Gets the value of the typeRef property.
         *
         * @return possible object is {@link QName }
         */
        @NonNull
        public QName getTypeRef() {
            return typeRef;
        }

        /**
         * Sets the value of the typeRef property.
         *
         * @param value allowed object is {@link QName }
         */
        public void setTypeRef(QName value) {
            this.typeRef = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ValidSource that = (ValidSource) o;
            return Objects.equals(typeRef, that.typeRef);
        }

        @Override
        public int hashCode() {
            return Objects.hash(typeRef);
        }
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
     *       &lt;attribute name="typeRef" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class ValidTarget {

        @XmlAttribute(name = "typeRef", required = true)
        protected QName typeRef;

        /**
         * Gets the value of the typeRef property.
         *
         * @return possible object is {@link QName }
         */
        @NonNull
        public QName getTypeRef() {
            return typeRef;
        }

        /**
         * Sets the value of the typeRef property.
         *
         * @param value allowed object is {@link QName }
         */
        public void setTypeRef(QName value) {
            this.typeRef = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ValidTarget that = (ValidTarget) o;
            return Objects.equals(typeRef, that.typeRef);
        }

        @Override
        public int hashCode() {
            return Objects.hash(typeRef);
        }
    }

    public static class Builder extends TEntityType.Builder<Builder> {
        private TTopologyElementInstanceStates instanceStates;
        private SourceInterfaces sourceInterfaces;
        private TargetInterfaces targetInterfaces;
        private ValidSource validSource;
        private ValidTarget validTarget;

        public Builder(String name) {
            super(name);
        }

        public Builder(TEntityType entityType) {
            super(entityType);
        }

        public Builder setInstanceStates(TTopologyElementInstanceStates instanceStates) {
            this.instanceStates = instanceStates;
            return this;
        }

        public Builder setSourceInterfaces(TRelationshipType.SourceInterfaces sourceInterfaces) {
            this.sourceInterfaces = sourceInterfaces;
            return this;
        }

        public Builder setTargetInterfaces(TRelationshipType.TargetInterfaces targetInterfaces) {
            this.targetInterfaces = targetInterfaces;
            return this;
        }

        public Builder setValidSource(TRelationshipType.ValidSource validSource) {
            this.validSource = validSource;
            return this;
        }

        public Builder setValidSource(QName validSource) {
            if (validSource == null) {
                return this;
            }

            TRelationshipType.ValidSource tmp = new TRelationshipType.ValidSource();
            tmp.setTypeRef(validSource);
            return setValidSource(tmp);
        }

        public Builder setValidTarget(ValidTarget validTarget) {
            this.validTarget = validTarget;
            return this;
        }

        public Builder setValidTarget(QName validTarget) {
            if (validTarget == null) {
                return this;
            }

            TRelationshipType.ValidTarget tmp = new TRelationshipType.ValidTarget();
            tmp.setTypeRef(validTarget);
            return setValidTarget(tmp);
        }

        public Builder addSourceInterfaces(TRelationshipType.SourceInterfaces sourceInterfaces) {
            if (sourceInterfaces == null || sourceInterfaces.getInterface().isEmpty()) {
                return this;
            }

            if (this.sourceInterfaces == null) {
                this.sourceInterfaces = sourceInterfaces;
            } else {
                this.sourceInterfaces.getInterface().addAll(sourceInterfaces.getInterface());
            }
            return this;
        }

        public Builder addSourceInterfaces(List<TInterface> sourceInterfaces) {
            if (sourceInterfaces == null) {
                return this;
            }

            TRelationshipType.SourceInterfaces tmp = new TRelationshipType.SourceInterfaces();
            tmp.getInterface().addAll(sourceInterfaces);
            return addSourceInterfaces(tmp);
        }

        public Builder addSourceInterfaces(TInterface sourceInterfaces) {
            if (sourceInterfaces == null) {
                return this;
            }

            TRelationshipType.SourceInterfaces tmp = new TRelationshipType.SourceInterfaces();
            tmp.getInterface().add(sourceInterfaces);
            return addSourceInterfaces(tmp);
        }

        public Builder addTargetInterfaces(TRelationshipType.TargetInterfaces targetInterfaces) {
            if (targetInterfaces == null || targetInterfaces.getInterface().isEmpty()) {
                return this;
            }

            if (this.targetInterfaces == null) {
                this.targetInterfaces = targetInterfaces;
            } else {
                this.targetInterfaces.getInterface().addAll(targetInterfaces.getInterface());
            }
            return this;
        }

        public Builder addTargetInterfaces(List<TInterface> targetInterfaces) {
            if (targetInterfaces == null) {
                return this;
            }

            TRelationshipType.TargetInterfaces tmp = new TRelationshipType.TargetInterfaces();
            tmp.getInterface().addAll(targetInterfaces);
            return addTargetInterfaces(tmp);
        }

        public Builder addTargetInterfaces(TInterface targetInterfaces) {
            if (targetInterfaces == null) {
                return this;
            }

            TRelationshipType.TargetInterfaces tmp = new TRelationshipType.TargetInterfaces();
            tmp.getInterface().add(targetInterfaces);
            return addTargetInterfaces(tmp);
        }

        @Override
        public Builder self() {
            return this;
        }

        public TRelationshipType build() {
            return new TRelationshipType(this);
        }
    }
}
