/*******************************************************************************
 * Copyright (c) 2013-2017 University of Stuttgart
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial code generation using vhudson-jaxb-ri-2.1-2
 *******************************************************************************/

package org.eclipse.winery.model.tosca;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.eclipse.jdt.annotation.Nullable;
import org.w3c.dom.Element;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tRelationshipTemplate", propOrder = {
        "sourceElement",
        "targetElement",
        "relationshipConstraints"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class TRelationshipTemplate extends TEntityTemplate {

    @XmlElement(name = "SourceElement", required = true)
    // AD: We need to combine source or target due to multi-inheritance
    protected TRelationshipTemplate.SourceOrTargetElement sourceElement;
    @XmlElement(name = "TargetElement", required = true)
    protected TRelationshipTemplate.SourceOrTargetElement targetElement;
    @XmlElement(name = "RelationshipConstraints")
    protected TRelationshipTemplate.RelationshipConstraints relationshipConstraints;
    @XmlAttribute(name = "name")
    protected String name;

    public TRelationshipTemplate() {
        super();
    }

    public TRelationshipTemplate(String id) {
        super(id);
    }

    public TRelationshipTemplate(Builder builder) {
        super(builder);
        this.sourceElement = builder.sourceElement;
        this.targetElement = builder.targetElement;
        this.relationshipConstraints = builder.relationshipConstraints;
        this.name = builder.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TRelationshipTemplate)) return false;
        if (!super.equals(o)) return false;
        TRelationshipTemplate that = (TRelationshipTemplate) o;
        return Objects.equals(sourceElement, that.sourceElement) &&
                Objects.equals(targetElement, that.targetElement) &&
                Objects.equals(relationshipConstraints, that.relationshipConstraints) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sourceElement, targetElement, relationshipConstraints, name);
    }

    /*@NonNull*/
    public TRelationshipTemplate.SourceOrTargetElement getSourceElement() {
        return sourceElement;
    }

    public void setSourceElement(TRelationshipTemplate.SourceOrTargetElement value) {
        this.sourceElement = value;
    }

    public void setSourceNodeTemplate(TNodeTemplate value) {
        SourceOrTargetElement sourceElement = new SourceOrTargetElement();
        sourceElement.setRef(value);
        this.sourceElement = sourceElement;
    }

    public void setTargetNodeTemplate(TNodeTemplate value) {
        SourceOrTargetElement targetElement = new SourceOrTargetElement();
        targetElement.setRef(value);
        this.targetElement = targetElement;
    }

    /*@NonNull*/
    public TRelationshipTemplate.SourceOrTargetElement getTargetElement() {
        return targetElement;
    }

    public void setTargetElement(TRelationshipTemplate.SourceOrTargetElement value) {
        this.targetElement = value;
    }

    /**
     * Gets the value of the relationshipConstraints property.
     *
     * @return possible object is {@link TRelationshipTemplate.RelationshipConstraints }
     */
    /*@Nullable*/
    public TRelationshipTemplate.RelationshipConstraints getRelationshipConstraints() {
        return relationshipConstraints;
    }

    /**
     * Sets the value of the relationshipConstraints property.
     *
     * @param value allowed object is {@link TRelationshipTemplate.RelationshipConstraints }
     */
    public void setRelationshipConstraints(TRelationshipTemplate.RelationshipConstraints value) {
        this.relationshipConstraints = value;
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
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="RelationshipConstraint" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;any processContents='lax' namespace='##other' minOccurs="0"/>
     *                 &lt;/sequence>
     *                 &lt;attribute name="constraintType" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI"
     * />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "relationshipConstraint"
    })
    public static class RelationshipConstraints {

        @XmlElement(name = "RelationshipConstraint", required = true)
        protected List<TRelationshipTemplate.RelationshipConstraints.RelationshipConstraint> relationshipConstraint;

        /**
         * Gets the value of the relationshipConstraint property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the relationshipConstraint property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRelationshipConstraint().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TRelationshipTemplate.RelationshipConstraints.RelationshipConstraint }
         */
        public List<TRelationshipTemplate.RelationshipConstraints.RelationshipConstraint> getRelationshipConstraint() {
            if (relationshipConstraint == null) {
                relationshipConstraint = new ArrayList<TRelationshipTemplate.RelationshipConstraints.RelationshipConstraint>();
            }
            return this.relationshipConstraint;
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
         *         &lt;any processContents='lax' namespace='##other' minOccurs="0"/>
         *       &lt;/sequence>
         *       &lt;attribute name="constraintType" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
                "any"
        })
        public static class RelationshipConstraint {

            @XmlAnyElement(lax = true)
            protected Object any;
            @XmlAttribute(name = "constraintType", required = true)
            @XmlSchemaType(name = "anyURI")
            protected String constraintType;

            /**
             * Gets the value of the any property.
             *
             * @return possible object is {@link Element } {@link Object }
             */
            public Object getAny() {
                return any;
            }

            /**
             * Sets the value of the any property.
             *
             * @param value allowed object is {@link Element } {@link Object }
             */
            public void setAny(Object value) {
                this.any = value;
            }

            /**
             * Gets the value of the constraintType property.
             *
             * @return possible object is {@link String }
             */
            public String getConstraintType() {
                return constraintType;
            }

            /**
             * Sets the value of the constraintType property.
             *
             * @param value allowed object is {@link String }
             */
            public void setConstraintType(String value) {
                this.constraintType = value;
            }
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "RelationshipSourceOrTaget")
    public static class SourceOrTargetElement {

        // We serialize XML and JSON differently. Solution for JSON taken from https://stackoverflow.com/a/17583175/873282
        @XmlAttribute(name = "ref", required = true)
        @XmlIDREF
        @XmlSchemaType(name = "IDREF")
        @JsonIdentityReference(alwaysAsId = true)
        private RelationshipSourceOrTarget ref;

        public RelationshipSourceOrTarget getRef() {
            return ref;
        }

        public void setRef(RelationshipSourceOrTarget value) {
            this.ref = value;
        }
    }

    public static class Builder extends TEntityTemplate.Builder<Builder> {
        private final SourceOrTargetElement sourceElement;
        private final SourceOrTargetElement targetElement;
        private RelationshipConstraints relationshipConstraints;
        private String name;

        public Builder(String id, QName type, TRelationshipTemplate.SourceOrTargetElement sourceElement, TRelationshipTemplate.SourceOrTargetElement targetElement) {
            super(id, type);
            this.sourceElement = sourceElement;
            this.targetElement = targetElement;
        }

        public Builder setRelationshipConstraints(RelationshipConstraints relationshipConstraints) {
            this.relationshipConstraints = relationshipConstraints;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder addRelationshipConstraints(TRelationshipTemplate.RelationshipConstraints relationshipConstraints) {
            if (relationshipConstraints == null || relationshipConstraints.getRelationshipConstraint().isEmpty()) {
                return this;
            }

            if (this.relationshipConstraints == null) {
                this.relationshipConstraints = relationshipConstraints;
            } else {
                this.relationshipConstraints.getRelationshipConstraint().addAll(relationshipConstraints.getRelationshipConstraint());
            }
            return this;
        }

        public Builder addRelationshipConstraints(List<RelationshipConstraints.RelationshipConstraint> relationshipConstraints) {
            if (relationshipConstraints == null) {
                return this;
            }

            TRelationshipTemplate.RelationshipConstraints tmp = new TRelationshipTemplate.RelationshipConstraints();
            tmp.getRelationshipConstraint().addAll(relationshipConstraints);
            return addRelationshipConstraints(tmp);
        }

        public Builder addRelationshipConstraints(RelationshipConstraints.RelationshipConstraint relationshipConstraints) {
            if (relationshipConstraints == null) {
                return this;
            }

            TRelationshipTemplate.RelationshipConstraints tmp = new TRelationshipTemplate.RelationshipConstraints();
            tmp.getRelationshipConstraint().add(relationshipConstraints);
            return addRelationshipConstraints(tmp);
        }

        @Override
        public Builder self() {
            return this;
        }

        public TRelationshipTemplate build() {
            return new TRelationshipTemplate(this);
        }
    }
}
