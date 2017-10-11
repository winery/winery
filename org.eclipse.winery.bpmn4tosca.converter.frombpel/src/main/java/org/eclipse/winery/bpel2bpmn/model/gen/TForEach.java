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


/**
 * <p>Java-Klasse für tForEach complex type.
 * <p>
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;complexType name="tForEach">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tActivity">
 *       &lt;sequence>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}startCounterValue"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}finalCounterValue"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}completionCondition" minOccurs="0"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}scope"/>
 *       &lt;/sequence>
 *       &lt;attribute name="counterName" use="required" type="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}BPELVariableName" />
 *       &lt;attribute name="parallel" use="required" type="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tBoolean" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tForEach", propOrder = {
    "startCounterValue",
    "finalCounterValue",
    "completionCondition",
    "scope"
})
public class TForEach
    extends TActivity {

    @XmlElement(required = true)
    protected TExpression startCounterValue;
    @XmlElement(required = true)
    protected TExpression finalCounterValue;
    protected TCompletionCondition completionCondition;
    @XmlElement(required = true)
    protected TScope scope;
    @XmlAttribute(name = "counterName", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String counterName;
    @XmlAttribute(name = "parallel", required = true)
    protected TBoolean parallel;

    /**
     * Ruft den Wert der startCounterValue-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TExpression }
     */
    public TExpression getStartCounterValue() {
        return startCounterValue;
    }

    /**
     * Legt den Wert der startCounterValue-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TExpression }
     */
    public void setStartCounterValue(TExpression value) {
        this.startCounterValue = value;
    }

    /**
     * Ruft den Wert der finalCounterValue-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TExpression }
     */
    public TExpression getFinalCounterValue() {
        return finalCounterValue;
    }

    /**
     * Legt den Wert der finalCounterValue-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TExpression }
     */
    public void setFinalCounterValue(TExpression value) {
        this.finalCounterValue = value;
    }

    /**
     * Ruft den Wert der completionCondition-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TCompletionCondition }
     */
    public TCompletionCondition getCompletionCondition() {
        return completionCondition;
    }

    /**
     * Legt den Wert der completionCondition-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TCompletionCondition }
     */
    public void setCompletionCondition(TCompletionCondition value) {
        this.completionCondition = value;
    }

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
     * Ruft den Wert der counterName-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCounterName() {
        return counterName;
    }

    /**
     * Legt den Wert der counterName-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCounterName(String value) {
        this.counterName = value;
    }

    /**
     * Ruft den Wert der parallel-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TBoolean }
     */
    public TBoolean getParallel() {
        return parallel;
    }

    /**
     * Legt den Wert der parallel-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TBoolean }
     */
    public void setParallel(TBoolean value) {
        this.parallel = value;
    }

}
