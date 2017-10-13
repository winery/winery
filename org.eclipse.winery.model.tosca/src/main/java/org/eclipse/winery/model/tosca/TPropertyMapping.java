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

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import org.eclipse.jdt.annotation.NonNull;


/**
 * <p>Java class for tPropertyMapping complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tPropertyMapping">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="serviceTemplatePropertyRef" use="required" type="{http://www.w3.org/2001/XMLSchema}string"
 * />
 *       &lt;attribute name="targetObjectRef" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *       &lt;attribute name="targetPropertyRef" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPropertyMapping")
public class TPropertyMapping {

    @JsonIdentityReference(alwaysAsId = true)
    @XmlAttribute(name = "serviceTemplatePropertyRef", required = true)
    protected String serviceTemplatePropertyRef;

    @JsonIdentityReference(alwaysAsId = true)
    @XmlAttribute(name = "targetObjectRef", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object targetObjectRef;

    @XmlAttribute(name = "targetPropertyRef", required = true)
    protected String targetPropertyRef;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TPropertyMapping)) return false;
        TPropertyMapping that = (TPropertyMapping) o;
        return Objects.equals(serviceTemplatePropertyRef, that.serviceTemplatePropertyRef) &&
                Objects.equals(targetObjectRef, that.targetObjectRef) &&
                Objects.equals(targetPropertyRef, that.targetPropertyRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceTemplatePropertyRef, targetObjectRef, targetPropertyRef);
    }

    /**
     * Gets the value of the serviceTemplatePropertyRef property.
     *
     * @return possible object is {@link String }
     */
    @NonNull
    public String getServiceTemplatePropertyRef() {
        return serviceTemplatePropertyRef;
    }

    /**
     * Sets the value of the serviceTemplatePropertyRef property.
     *
     * @param value allowed object is {@link String }
     */
    public void setServiceTemplatePropertyRef(String value) {
        this.serviceTemplatePropertyRef = value;
    }

    /**
     * Gets the value of the targetObjectRef property.
     *
     * @return possible object is {@link Object }
     */
    @NonNull
    public Object getTargetObjectRef() {
        return targetObjectRef;
    }

    /**
     * Sets the value of the targetObjectRef property.
     *
     * @param value allowed object is {@link Object }
     */
    public void setTargetObjectRef(Object value) {
        this.targetObjectRef = value;
    }

    /**
     * Gets the value of the targetPropertyRef property.
     *
     * @return possible object is {@link String }
     */
    @NonNull
    public String getTargetPropertyRef() {
        return targetPropertyRef;
    }

    /**
     * Sets the value of the targetPropertyRef property.
     *
     * @param value allowed object is {@link String }
     */
    public void setTargetPropertyRef(String value) {
        this.targetPropertyRef = value;
    }
}
