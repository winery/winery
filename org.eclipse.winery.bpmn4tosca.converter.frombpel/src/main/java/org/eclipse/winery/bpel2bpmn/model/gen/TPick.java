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
import java.util.ArrayList;
import java.util.List;


/**
 * XSD Authors: The child element onAlarm needs to be a Local Element Declaration,
 * because there is another onAlarm element defined for event handlers.
 * <p>
 * <p>
 * <p>Java-Klasse für tPick complex type.
 * <p>
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;complexType name="tPick">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tActivity">
 *       &lt;sequence>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}onMessage" maxOccurs="unbounded"/>
 *         &lt;element name="onAlarm" type="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tOnAlarmPick" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="createInstance" type="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tBoolean" default="no" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPick", propOrder = {
    "onMessage",
    "onAlarm"
})
public class TPick
    extends TActivity {

    @XmlElement(required = true)
    protected List<TOnMessage> onMessage;
    protected List<TOnAlarmPick> onAlarm;
    @XmlAttribute(name = "createInstance")
    protected TBoolean createInstance;

    /**
     * Gets the value of the onMessage property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the onMessage property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOnMessage().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TOnMessage }
     */
    public List<TOnMessage> getOnMessage() {
        if (onMessage == null) {
            onMessage = new ArrayList<TOnMessage>();
        }
        return this.onMessage;
    }

    /**
     * Gets the value of the onAlarm property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the onAlarm property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOnAlarm().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TOnAlarmPick }
     */
    public List<TOnAlarmPick> getOnAlarm() {
        if (onAlarm == null) {
            onAlarm = new ArrayList<TOnAlarmPick>();
        }
        return this.onAlarm;
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

}
