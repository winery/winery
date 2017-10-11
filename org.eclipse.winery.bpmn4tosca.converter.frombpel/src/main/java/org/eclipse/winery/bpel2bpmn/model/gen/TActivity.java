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
 * <p>Java-Klasse für tActivity complex type.
 * <p>
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;complexType name="tActivity">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tExtensibleElements">
 *       &lt;sequence>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}targets" minOccurs="0"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}sources" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="suppressJoinFailure" type="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tBoolean" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tActivity", propOrder = {
    "targets",
    "sources"
})
@XmlSeeAlso({
    TInvoke.class,
    TWhile.class,
    TEmpty.class,
    TCompensateScope.class,
    TPick.class,
    TReply.class,
    TIf.class,
    TFlow.class,
    TRepeatUntil.class,
    TExit.class,
    TSequence.class,
    TWait.class,
    TScope.class,
    TValidate.class,
    TRethrow.class,
    TReceive.class,
    TForEach.class,
    TThrow.class,
    TCompensate.class,
    TAssign.class
})
public class TActivity
    extends TExtensibleElements {

    protected TTargets targets;
    protected TSources sources;
    @XmlAttribute(name = "name")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;
    @XmlAttribute(name = "suppressJoinFailure")
    protected TBoolean suppressJoinFailure;

    /**
     * Ruft den Wert der targets-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TTargets }
     */
    public TTargets getTargets() {
        return targets;
    }

    /**
     * Legt den Wert der targets-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TTargets }
     */
    public void setTargets(TTargets value) {
        this.targets = value;
    }

    /**
     * Ruft den Wert der sources-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TSources }
     */
    public TSources getSources() {
        return sources;
    }

    /**
     * Legt den Wert der sources-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TSources }
     */
    public void setSources(TSources value) {
        this.sources = value;
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
     * Ruft den Wert der suppressJoinFailure-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TBoolean }
     */
    public TBoolean getSuppressJoinFailure() {
        return suppressJoinFailure;
    }

    /**
     * Legt den Wert der suppressJoinFailure-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TBoolean }
     */
    public void setSuppressJoinFailure(TBoolean value) {
        this.suppressJoinFailure = value;
    }

}
