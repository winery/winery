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
 * XSD Authors: The child element correlations needs to be a Local Element Declaration,
 * because there is another correlations element defined for the invoke activity.
 * <p>
 * <p>
 * <p>Java-Klasse für tReceive complex type.
 * <p>
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;complexType name="tReceive">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tActivity">
 *       &lt;sequence>
 *         &lt;element name="correlations" type="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tCorrelations" minOccurs="0"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}fromParts" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="partnerLink" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="portType" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="operation" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="variable" type="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}BPELVariableName" />
 *       &lt;attribute name="createInstance" type="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tBoolean" default="no" />
 *       &lt;attribute name="messageExchange" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tReceive", propOrder = {
    "correlations",
    "fromParts"
})
public class TReceive
    extends TActivity {

    protected TCorrelations correlations;
    protected TFromParts fromParts;
    @XmlAttribute(name = "partnerLink", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String partnerLink;
    @XmlAttribute(name = "portType")
    protected QName portType;
    @XmlAttribute(name = "operation", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String operation;
    @XmlAttribute(name = "variable")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String variable;
    @XmlAttribute(name = "createInstance")
    protected TBoolean createInstance;
    @XmlAttribute(name = "messageExchange")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String messageExchange;

    /**
     * Ruft den Wert der correlations-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TCorrelations }
     */
    public TCorrelations getCorrelations() {
        return correlations;
    }

    /**
     * Legt den Wert der correlations-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TCorrelations }
     */
    public void setCorrelations(TCorrelations value) {
        this.correlations = value;
    }

    /**
     * Ruft den Wert der fromParts-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TFromParts }
     */
    public TFromParts getFromParts() {
        return fromParts;
    }

    /**
     * Legt den Wert der fromParts-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TFromParts }
     */
    public void setFromParts(TFromParts value) {
        this.fromParts = value;
    }

    /**
     * Ruft den Wert der partnerLink-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPartnerLink() {
        return partnerLink;
    }

    /**
     * Legt den Wert der partnerLink-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPartnerLink(String value) {
        this.partnerLink = value;
    }

    /**
     * Ruft den Wert der portType-Eigenschaft ab.
     *
     * @return possible object is
     * {@link QName }
     */
    public QName getPortType() {
        return portType;
    }

    /**
     * Legt den Wert der portType-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link QName }
     */
    public void setPortType(QName value) {
        this.portType = value;
    }

    /**
     * Ruft den Wert der operation-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Legt den Wert der operation-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setOperation(String value) {
        this.operation = value;
    }

    /**
     * Ruft den Wert der variable-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getVariable() {
        return variable;
    }

    /**
     * Legt den Wert der variable-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setVariable(String value) {
        this.variable = value;
    }

    /**
     * Ruft den Wert der createInstance-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TBoolean }
     */
    public TBoolean getCreateInstance() {
        if (createInstance == null) {
            return TBoolean.NO;
        } else {
            return createInstance;
        }
    }

    /**
     * Legt den Wert der createInstance-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TBoolean }
     */
    public void setCreateInstance(TBoolean value) {
        this.createInstance = value;
    }

    /**
     * Ruft den Wert der messageExchange-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMessageExchange() {
        return messageExchange;
    }

    /**
     * Legt den Wert der messageExchange-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMessageExchange(String value) {
        this.messageExchange = value;
    }

}
