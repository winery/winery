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
 * <p>Java-Klasse für tFlow complex type.
 * <p>
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;complexType name="tFlow">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tActivity">
 *       &lt;sequence>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}links" minOccurs="0"/>
 *         &lt;group ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}activity" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tFlow", propOrder = {
    "links",
    "activity"
})
public class TFlow
    extends TActivity {

    protected TLinks links;
    @XmlElements({
        @XmlElement(name = "assign", type = TAssign.class),
        @XmlElement(name = "compensate", type = TCompensate.class),
        @XmlElement(name = "compensateScope", type = TCompensateScope.class),
        @XmlElement(name = "empty", type = TEmpty.class),
        @XmlElement(name = "exit", type = TExit.class),
        @XmlElement(name = "extensionActivity", type = TExtensionActivity.class),
        @XmlElement(name = "flow", type = TFlow.class),
        @XmlElement(name = "forEach", type = TForEach.class),
        @XmlElement(name = "if", type = TIf.class),
        @XmlElement(name = "invoke", type = TInvoke.class),
        @XmlElement(name = "pick", type = TPick.class),
        @XmlElement(name = "receive", type = TReceive.class),
        @XmlElement(name = "repeatUntil", type = TRepeatUntil.class),
        @XmlElement(name = "reply", type = TReply.class),
        @XmlElement(name = "rethrow", type = TRethrow.class),
        @XmlElement(name = "scope", type = TScope.class),
        @XmlElement(name = "sequence", type = TSequence.class),
        @XmlElement(name = "throw", type = TThrow.class),
        @XmlElement(name = "validate", type = TValidate.class),
        @XmlElement(name = "wait", type = TWait.class),
        @XmlElement(name = "while", type = TWhile.class)
    })
    protected List<Object> activity;

    /**
     * Ruft den Wert der links-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TLinks }
     */
    public TLinks getLinks() {
        return links;
    }

    /**
     * Legt den Wert der links-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TLinks }
     */
    public void setLinks(TLinks value) {
        this.links = value;
    }

    /**
     * Gets the value of the activity property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the activity property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getActivity().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TAssign }
     * {@link TCompensate }
     * {@link TCompensateScope }
     * {@link TEmpty }
     * {@link TExit }
     * {@link TExtensionActivity }
     * {@link TFlow }
     * {@link TForEach }
     * {@link TIf }
     * {@link TInvoke }
     * {@link TPick }
     * {@link TReceive }
     * {@link TRepeatUntil }
     * {@link TReply }
     * {@link TRethrow }
     * {@link TScope }
     * {@link TSequence }
     * {@link TThrow }
     * {@link TValidate }
     * {@link TWait }
     * {@link TWhile }
     */
    public List<Object> getActivity() {
        if (activity == null) {
            activity = new ArrayList<Object>();
        }
        return this.activity;
    }

}
