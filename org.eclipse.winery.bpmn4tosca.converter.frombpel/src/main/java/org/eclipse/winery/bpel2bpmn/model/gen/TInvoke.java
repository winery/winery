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
import java.util.ArrayList;
import java.util.List;


/**
 * XSD Authors: The child element correlations needs to be a Local Element Declaration,
 * because there is another correlations element defined for the non-invoke activities.
 * <p>
 * <p>
 * <p>Java-Klasse für tInvoke complex type.
 * <p>
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;complexType name="tInvoke">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tActivity">
 *       &lt;sequence>
 *         &lt;element name="correlations" type="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tCorrelationsWithPattern" minOccurs="0"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}catch" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}catchAll" minOccurs="0"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}compensationHandler" minOccurs="0"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}toParts" minOccurs="0"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}fromParts" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="partnerLink" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="portType" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="operation" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="inputVariable" type="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}BPELVariableName" />
 *       &lt;attribute name="outputVariable" type="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}BPELVariableName" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tInvoke", propOrder = {
    "correlations",
    "_catch",
    "catchAll",
    "compensationHandler",
    "toParts",
    "fromParts"
})
public class TInvoke
    extends TActivity {

    protected TCorrelationsWithPattern correlations;
    @XmlElement(name = "catch")
    protected List<TCatch> _catch;
    protected TActivityContainer catchAll;
    protected TActivityContainer compensationHandler;
    protected TToParts toParts;
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
    @XmlAttribute(name = "inputVariable")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String inputVariable;
    @XmlAttribute(name = "outputVariable")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String outputVariable;

    /**
     * Ruft den Wert der correlations-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TCorrelationsWithPattern }
     */
    public TCorrelationsWithPattern getCorrelations() {
        return correlations;
    }

    /**
     * Legt den Wert der correlations-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TCorrelationsWithPattern }
     */
    public void setCorrelations(TCorrelationsWithPattern value) {
        this.correlations = value;
    }

    /**
     * Gets the value of the catch property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the catch property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCatch().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TCatch }
     */
    public List<TCatch> getCatch() {
        if (_catch == null) {
            _catch = new ArrayList<TCatch>();
        }
        return this._catch;
    }

    /**
     * Ruft den Wert der catchAll-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TActivityContainer }
     */
    public TActivityContainer getCatchAll() {
        return catchAll;
    }

    /**
     * Legt den Wert der catchAll-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TActivityContainer }
     */
    public void setCatchAll(TActivityContainer value) {
        this.catchAll = value;
    }

    /**
     * Ruft den Wert der compensationHandler-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TActivityContainer }
     */
    public TActivityContainer getCompensationHandler() {
        return compensationHandler;
    }

    /**
     * Legt den Wert der compensationHandler-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TActivityContainer }
     */
    public void setCompensationHandler(TActivityContainer value) {
        this.compensationHandler = value;
    }

    /**
     * Ruft den Wert der toParts-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TToParts }
     */
    public TToParts getToParts() {
        return toParts;
    }

    /**
     * Legt den Wert der toParts-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TToParts }
     */
    public void setToParts(TToParts value) {
        this.toParts = value;
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
     * Ruft den Wert der inputVariable-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getInputVariable() {
        return inputVariable;
    }

    /**
     * Legt den Wert der inputVariable-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setInputVariable(String value) {
        this.inputVariable = value;
    }

    /**
     * Ruft den Wert der outputVariable-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getOutputVariable() {
        return outputVariable;
    }

    /**
     * Legt den Wert der outputVariable-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setOutputVariable(String value) {
        this.outputVariable = value;
    }

}
