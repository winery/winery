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
 * <p>Java-Klasse für tCorrelation complex type.
 * <p>
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;complexType name="tCorrelation">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tExtensibleElements">
 *       &lt;attribute name="set" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="initiate" type="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tInitiate" default="no" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tCorrelation")
@XmlSeeAlso({
    TCorrelationWithPattern.class
})
public class TCorrelation
    extends TExtensibleElements {

    @XmlAttribute(name = "set", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String set;
    @XmlAttribute(name = "initiate")
    protected TInitiate initiate;

    /**
     * Ruft den Wert der set-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSet() {
        return set;
    }

    /**
     * Legt den Wert der set-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSet(String value) {
        this.set = value;
    }

    /**
     * Ruft den Wert der initiate-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TInitiate }
     */
    public TInitiate getInitiate() {
        if (initiate == null) {
            return TInitiate.NO;
        } else {
            return initiate;
        }
    }

    /**
     * Legt den Wert der initiate-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TInitiate }
     */
    public void setInitiate(TInitiate value) {
        this.initiate = value;
    }

}
