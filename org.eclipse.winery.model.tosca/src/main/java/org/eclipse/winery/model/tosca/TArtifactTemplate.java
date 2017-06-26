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
 *******************************************************************************/

package org.eclipse.winery.model.tosca;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tArtifactTemplate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tArtifactTemplate">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tEntityTemplate">
 *       &lt;sequence>
 *         &lt;element name="ArtifactReferences" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ArtifactReference" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tArtifactReference" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tArtifactTemplate", propOrder = {
    "artifactReferences"
})
public class TArtifactTemplate
    extends TEntityTemplate
{

    @XmlElement(name = "ArtifactReferences")
    protected TArtifactTemplate.ArtifactReferences artifactReferences;
    @XmlAttribute(name = "name")
    protected String name;

    /**
     * Gets the value of the artifactReferences property.
     * 
     * @return
     *     possible object is
     *     {@link TArtifactTemplate.ArtifactReferences }
     *     
     */
    public TArtifactTemplate.ArtifactReferences getArtifactReferences() {
        return artifactReferences;
    }

    /**
     * Sets the value of the artifactReferences property.
     * 
     * @param value
     *     allowed object is
     *     {@link TArtifactTemplate.ArtifactReferences }
     *     
     */
    public void setArtifactReferences(TArtifactTemplate.ArtifactReferences value) {
        this.artifactReferences = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
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
     *         &lt;element name="ArtifactReference" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tArtifactReference" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "artifactReference"
    })
    public static class ArtifactReferences {

        @XmlElement(name = "ArtifactReference", required = true)
        protected List<TArtifactReference> artifactReference;

        /**
         * Gets the value of the artifactReference property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the artifactReference property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getArtifactReference().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TArtifactReference }
         * 
         * 
         */
        public List<TArtifactReference> getArtifactReference() {
            if (artifactReference == null) {
                artifactReference = new ArrayList<TArtifactReference>();
            }
            return this.artifactReference;
        }

    }

}
