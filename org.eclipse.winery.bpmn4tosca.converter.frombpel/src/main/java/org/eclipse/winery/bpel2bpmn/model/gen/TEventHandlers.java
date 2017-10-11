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
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * XSD Authors: The child element onAlarm needs to be a Local Element Declaration,
 * because there is another onAlarm element defined for the pick activity.
 * <p>
 * <p>
 * <p>Java-Klasse für tEventHandlers complex type.
 * <p>
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;complexType name="tEventHandlers">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tExtensibleElements">
 *       &lt;sequence>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}onEvent" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="onAlarm" type="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tOnAlarmEvent" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tEventHandlers", propOrder = {
    "onEvent",
    "onAlarm"
})
public class TEventHandlers
    extends TExtensibleElements {

    protected List<TOnEvent> onEvent;
    protected List<TOnAlarmEvent> onAlarm;

    /**
     * Gets the value of the onEvent property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the onEvent property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOnEvent().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TOnEvent }
     */
    public List<TOnEvent> getOnEvent() {
        if (onEvent == null) {
            onEvent = new ArrayList<TOnEvent>();
        }
        return this.onEvent;
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
     * {@link TOnAlarmEvent }
     */
    public List<TOnAlarmEvent> getOnAlarm() {
        if (onAlarm == null) {
            onAlarm = new ArrayList<TOnAlarmEvent>();
        }
        return this.onAlarm;
    }

}
