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


/**
 * <p>Java-Klasse für tCopy complex type.
 * <p>
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;complexType name="tCopy">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tExtensibleElements">
 *       &lt;sequence>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}from"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}to"/>
 *       &lt;/sequence>
 *       &lt;attribute name="keepSrcElementName" type="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tBoolean" default="no" />
 *       &lt;attribute name="ignoreMissingFromData" type="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tBoolean" default="no" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tCopy", propOrder = {
    "from",
    "to"
})
public class TCopy
    extends TExtensibleElements {

    @XmlElement(required = true)
    protected TFrom from;
    @XmlElement(required = true)
    protected TTo to;
    @XmlAttribute(name = "keepSrcElementName")
    protected TBoolean keepSrcElementName;
    @XmlAttribute(name = "ignoreMissingFromData")
    protected TBoolean ignoreMissingFromData;

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
     * Ruft den Wert der to-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TTo }
     */
    public TTo getTo() {
        return to;
    }

    /**
     * Legt den Wert der to-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TTo }
     */
    public void setTo(TTo value) {
        this.to = value;
    }

    /**
     * Ruft den Wert der keepSrcElementName-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TBoolean }
     */
    public TBoolean getKeepSrcElementName() {
        if (keepSrcElementName == null) {
            return TBoolean.NO;
        } else {
            return keepSrcElementName;
        }
    }

    /**
     * Legt den Wert der keepSrcElementName-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TBoolean }
     */
    public void setKeepSrcElementName(TBoolean value) {
        this.keepSrcElementName = value;
    }

    /**
     * Ruft den Wert der ignoreMissingFromData-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TBoolean }
     */
    public TBoolean getIgnoreMissingFromData() {
        if (ignoreMissingFromData == null) {
            return TBoolean.NO;
        } else {
            return ignoreMissingFromData;
        }
    }

    /**
     * Legt den Wert der ignoreMissingFromData-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TBoolean }
     */
    public void setIgnoreMissingFromData(TBoolean value) {
        this.ignoreMissingFromData = value;
    }

}
