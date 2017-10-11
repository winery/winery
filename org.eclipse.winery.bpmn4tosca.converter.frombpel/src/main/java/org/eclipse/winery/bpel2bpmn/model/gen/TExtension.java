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
 * <p>Java-Klasse für tExtension complex type.
 * <p>
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;complexType name="tExtension">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tExtensibleElements">
 *       &lt;attribute name="namespace" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="mustUnderstand" use="required" type="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tBoolean" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tExtension")
public class TExtension
    extends TExtensibleElements {

    @XmlAttribute(name = "namespace", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String namespace;
    @XmlAttribute(name = "mustUnderstand", required = true)
    protected TBoolean mustUnderstand;

    /**
     * Ruft den Wert der namespace-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Legt den Wert der namespace-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setNamespace(String value) {
        this.namespace = value;
    }

    /**
     * Ruft den Wert der mustUnderstand-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TBoolean }
     */
    public TBoolean getMustUnderstand() {
        return mustUnderstand;
    }

    /**
     * Legt den Wert der mustUnderstand-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TBoolean }
     */
    public void setMustUnderstand(TBoolean value) {
        this.mustUnderstand = value;
    }

}
