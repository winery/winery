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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java-Klasse für tOnAlarmEvent complex type.
 * <p>
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;complexType name="tOnAlarmEvent">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tExtensibleElements">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;sequence>
 *             &lt;group ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}forOrUntilGroup"/>
 *             &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}repeatEvery" minOccurs="0"/>
 *           &lt;/sequence>
 *           &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}repeatEvery"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}scope"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tOnAlarmEvent", propOrder = {
    "rest"
})
public class TOnAlarmEvent
    extends TExtensibleElements {

    @XmlElementRefs({
        @XmlElementRef(name = "until", namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "for", namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "repeatEvery", namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "scope", namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", type = JAXBElement.class, required = false)
    })
    protected List<JAXBElement<?>> rest;

    /**
     * Ruft das restliche Contentmodell ab.
     * <p>
     * <p>
     * Sie rufen diese "catch-all"-Eigenschaft aus folgendem Grund ab:
     * Der Feldname "RepeatEvery" wird von zwei verschiedenen Teilen eines Schemas verwendet. Siehe:
     * Zeile 395 von file:/home/alex/Projects/uni-projects/enpro/winery/org.eclipse.winery.bpmn4tosca.converter.frombpel/src/main/resources/ws-bpel_executable.xsd
     * Zeile 393 von file:/home/alex/Projects/uni-projects/enpro/winery/org.eclipse.winery.bpmn4tosca.converter.frombpel/src/main/resources/ws-bpel_executable.xsd
     * <p>
     * Um diese Eigenschaft zu entfernen, wenden Sie eine Eigenschaftenanpassung für eine
     * der beiden folgenden Deklarationen an, um deren Namen zu ändern:
     * Gets the value of the rest property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rest property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRest().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link TDeadlineExpr }{@code >}
     * {@link JAXBElement }{@code <}{@link TDurationExpr }{@code >}
     * {@link JAXBElement }{@code <}{@link TScope }{@code >}
     * {@link JAXBElement }{@code <}{@link TDurationExpr }{@code >}
     */
    public List<JAXBElement<?>> getRest() {
        if (rest == null) {
            rest = new ArrayList<JAXBElement<?>>();
        }
        return this.rest;
    }

}
