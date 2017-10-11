/*
 * *****************************************************************************
 * Copyright (c) 2015-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Alex Frank - initial API and implementation
 * *****************************************************************************
 *
 */

//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren.
// Generiert: 2017.07.21 um 10:17:40 AM CEST
//


package org.eclipse.winery.bpel2bpmn.model.gen;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * <p>Java-Klasse für tPartnerLink complex type.
 * <p>
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;complexType name="tPartnerLink">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tExtensibleElements">
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="partnerLinkType" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="myRole" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="partnerRole" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="initializePartnerRole" type="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tBoolean" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPartnerLink")
public class TPartnerLink
    extends TExtensibleElements {

    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;
    @XmlAttribute(name = "partnerLinkType", required = true)
    protected QName partnerLinkType;
    @XmlAttribute(name = "myRole")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String myRole;
    @XmlAttribute(name = "partnerRole")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String partnerRole;
    @XmlAttribute(name = "initializePartnerRole")
    protected TBoolean initializePartnerRole;

    /**
     * Ruft den Wert der name-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getName() {
        return name;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Ruft den Wert der partnerLinkType-Eigenschaft ab.
     *
     * @return possible object is
     * {@link QName }
     */
    public QName getPartnerLinkType() {
        return partnerLinkType;
    }

    /**
     * Legt den Wert der partnerLinkType-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link QName }
     */
    public void setPartnerLinkType(QName value) {
        this.partnerLinkType = value;
    }

    /**
     * Ruft den Wert der myRole-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMyRole() {
        return myRole;
    }

    /**
     * Legt den Wert der myRole-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMyRole(String value) {
        this.myRole = value;
    }

    /**
     * Ruft den Wert der partnerRole-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPartnerRole() {
        return partnerRole;
    }

    /**
     * Legt den Wert der partnerRole-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPartnerRole(String value) {
        this.partnerRole = value;
    }

    /**
     * Ruft den Wert der initializePartnerRole-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TBoolean }
     */
    public TBoolean getInitializePartnerRole() {
        return initializePartnerRole;
    }

    /**
     * Legt den Wert der initializePartnerRole-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TBoolean }
     */
    public void setInitializePartnerRole(TBoolean value) {
        this.initializePartnerRole = value;
    }

}
