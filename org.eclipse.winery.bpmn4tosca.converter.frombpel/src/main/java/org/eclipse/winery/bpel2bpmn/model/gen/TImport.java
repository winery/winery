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
 * <p>Java-Klasse für tImport complex type.
 * <p>
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;complexType name="tImport">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tExtensibleElements">
 *       &lt;attribute name="namespace" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="location" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="importType" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tImport")
public class TImport
    extends TExtensibleElements {

    @XmlAttribute(name = "namespace")
    @XmlSchemaType(name = "anyURI")
    protected String namespace;
    @XmlAttribute(name = "location")
    @XmlSchemaType(name = "anyURI")
    protected String location;
    @XmlAttribute(name = "importType", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String importType;

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
     * Ruft den Wert der location-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getLocation() {
        return location;
    }

    /**
     * Legt den Wert der location-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setLocation(String value) {
        this.location = value;
    }

    /**
     * Ruft den Wert der importType-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getImportType() {
        return importType;
    }

    /**
     * Legt den Wert der importType-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setImportType(String value) {
        this.importType = value;
    }

}
