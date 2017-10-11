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
import javax.xml.namespace.QName;


/**
 * <p>Java-Klasse für tOnEvent complex type.
 * <p>
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;complexType name="tOnEvent">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tOnMsgCommon">
 *       &lt;sequence>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}scope"/>
 *       &lt;/sequence>
 *       &lt;attribute name="messageType" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="element" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tOnEvent", propOrder = {
    "scope"
})
public class TOnEvent
    extends TOnMsgCommon {

    @XmlElement(required = true)
    protected TScope scope;
    @XmlAttribute(name = "messageType")
    protected QName messageType;
    @XmlAttribute(name = "element")
    protected QName element;

    /**
     * Ruft den Wert der scope-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TScope }
     */
    public TScope getScope() {
        return scope;
    }

    /**
     * Legt den Wert der scope-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TScope }
     */
    public void setScope(TScope value) {
        this.scope = value;
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
