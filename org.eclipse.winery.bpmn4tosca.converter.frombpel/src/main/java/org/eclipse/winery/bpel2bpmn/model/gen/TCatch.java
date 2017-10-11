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
 * <p>Java-Klasse für tCatch complex type.
 * <p>
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;complexType name="tCatch">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tActivityContainer">
 *       &lt;attribute name="faultName" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="faultVariable" type="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}BPELVariableName" />
 *       &lt;attribute name="faultMessageType" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="faultElement" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tCatch")
public class TCatch
    extends TActivityContainer {

    @XmlAttribute(name = "faultName")
    protected QName faultName;
    @XmlAttribute(name = "faultVariable")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String faultVariable;
    @XmlAttribute(name = "faultMessageType")
    protected QName faultMessageType;
    @XmlAttribute(name = "faultElement")
    protected QName faultElement;

    /**
     * Ruft den Wert der faultName-Eigenschaft ab.
     *
     * @return possible object is
     * {@link QName }
     */
    public QName getFaultName() {
        return faultName;
    }

    /**
     * Legt den Wert der faultName-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link QName }
     */
    public void setFaultName(QName value) {
        this.faultName = value;
    }

    /**
     * Ruft den Wert der faultVariable-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getFaultVariable() {
        return faultVariable;
    }

    /**
     * Legt den Wert der faultVariable-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setFaultVariable(String value) {
        this.faultVariable = value;
    }

    /**
     * Ruft den Wert der faultMessageType-Eigenschaft ab.
     *
     * @return possible object is
     * {@link QName }
     */
    public QName getFaultMessageType() {
        return faultMessageType;
    }

    /**
     * Legt den Wert der faultMessageType-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link QName }
     */
    public void setFaultMessageType(QName value) {
        this.faultMessageType = value;
    }

    /**
     * Ruft den Wert der faultElement-Eigenschaft ab.
     *
     * @return possible object is
     * {@link QName }
     */
    public QName getFaultElement() {
        return faultElement;
    }

    /**
     * Legt den Wert der faultElement-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link QName }
     */
    public void setFaultElement(QName value) {
        this.faultElement = value;
    }

}
