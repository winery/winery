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
// Generiert: 2017.07.24 um 11:12:17 PM CEST
//


package org.eclipse.winery.bpel2bpmn.model.gen.si;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für invokeResponse complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="invokeResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MessageID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="Params" type="{http://siserver.org/schema}ParamsMap" minOccurs="0"/>
 *           &lt;element name="Doc" type="{http://siserver.org/schema}Doc" minOccurs="0"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "invokeResponse", propOrder = {
    "messageID",
    "params",
    "doc"
})
public class InvokeResponse {

    @XmlElement(name = "MessageID")
    protected String messageID;
    @XmlElement(name = "Params")
    protected ParamsMap params;
    @XmlElement(name = "Doc")
    protected Doc doc;

    /**
     * Ruft den Wert der messageID-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMessageID() {
        return messageID;
    }

    /**
     * Legt den Wert der messageID-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMessageID(String value) {
        this.messageID = value;
    }

    /**
     * Ruft den Wert der params-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link ParamsMap }
     *
     */
    public ParamsMap getParams() {
        return params;
    }

    /**
     * Legt den Wert der params-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link ParamsMap }
     *
     */
    public void setParams(ParamsMap value) {
        this.params = value;
    }

    /**
     * Ruft den Wert der doc-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Doc }
     *
     */
    public Doc getDoc() {
        return doc;
    }

    /**
     * Legt den Wert der doc-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Doc }
     *
     */
    public void setDoc(Doc value) {
        this.doc = value;
    }

}
