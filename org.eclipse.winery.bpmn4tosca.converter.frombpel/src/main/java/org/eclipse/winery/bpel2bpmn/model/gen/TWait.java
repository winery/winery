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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für tWait complex type.
 * <p>
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;complexType name="tWait">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tActivity">
 *       &lt;choice>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}for"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}until"/>
 *       &lt;/choice>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tWait", propOrder = {
    "_for",
    "until"
})
public class TWait
    extends TActivity {

    @XmlElement(name = "for")
    protected TDurationExpr _for;
    protected TDeadlineExpr until;

    /**
     * Ruft den Wert der for-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TDurationExpr }
     */
    public TDurationExpr getFor() {
        return _for;
    }

    /**
     * Legt den Wert der for-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TDurationExpr }
     */
    public void setFor(TDurationExpr value) {
        this._for = value;
    }

    /**
     * Ruft den Wert der until-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TDeadlineExpr }
     */
    public TDeadlineExpr getUntil() {
        return until;
    }

    /**
     * Legt den Wert der until-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TDeadlineExpr }
     */
    public void setUntil(TDeadlineExpr value) {
        this.until = value;
    }

}
