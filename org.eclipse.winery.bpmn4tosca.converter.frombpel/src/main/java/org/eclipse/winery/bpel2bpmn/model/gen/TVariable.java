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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * <p>Java-Klasse für tVariable complex type.
 * <p>
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;complexType name="tVariable">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tExtensibleElements">
 *       &lt;sequence>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}from" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}BPELVariableName" />
 *       &lt;attribute name="messageType" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="element" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tVariable", propOrder = {
    "from"
})
public class TVariable
    extends TExtensibleElements {

    protected TFrom from;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String name;
    @XmlAttribute(name = "messageType")
    protected QName messageType;
    @XmlAttribute(name = "type")
    protected QName type;
    @XmlAttribute(name = "element")
    protected QName element;

    /**
     * Ruft den Wert der from-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TFrom }
     */
    public TFrom getFrom() {
        return from;
    }

    /**
     * Legt den Wert der from-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TFrom }
     */
    public void setFrom(TFrom value) {
        this.from = value;
    }

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
     * Ruft den Wert der messageType-Eigenschaft ab.
     *
     * @return possible object is
     * {@link QName }
     */
    public QName getMessageType() {
        return messageType;
    }

    /**
     * Legt den Wert der messageType-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link QName }
     */
    public void setMessageType(QName value) {
        this.messageType = value;
    }

    /**
     * Ruft den Wert der type-Eigenschaft ab.
     *
     * @return possible object is
     * {@link QName }
     */
    public QName getType() {
        return type;
    }

    /**
     * Legt den Wert der type-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link QName }
     */
    public void setType(QName value) {
        this.type = value;
    }

    /**
     * Ruft den Wert der element-Eigenschaft ab.
     *
     * @return possible object is
     * {@link QName }
     */
    public QName getElement() {
        return element;
    }

    /**
     * Legt den Wert der element-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link QName }
     */
    public void setElement(QName value) {
        this.element = value;
    }

}
