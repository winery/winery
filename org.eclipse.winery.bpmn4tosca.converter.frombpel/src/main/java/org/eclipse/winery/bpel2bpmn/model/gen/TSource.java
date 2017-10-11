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
 * <p>Java-Klasse für tSource complex type.
 * <p>
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;complexType name="tSource">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tExtensibleElements">
 *       &lt;sequence>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}transitionCondition" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="linkName" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tSource", propOrder = {
    "transitionCondition"
})
public class TSource
    extends TExtensibleElements {

    protected TCondition transitionCondition;
    @XmlAttribute(name = "linkName", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String linkName;

    /**
     * Ruft den Wert der transitionCondition-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TCondition }
     */
    public TCondition getTransitionCondition() {
        return transitionCondition;
    }

    /**
     * Legt den Wert der transitionCondition-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TCondition }
     */
    public void setTransitionCondition(TCondition value) {
        this.transitionCondition = value;
    }

    /**
     * Ruft den Wert der linkName-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getLinkName() {
        return linkName;
    }

    /**
     * Legt den Wert der linkName-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setLinkName(String value) {
        this.linkName = value;
    }

}
