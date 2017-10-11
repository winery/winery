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
 * <p>Java-Klasse für tAssign complex type.
 * <p>
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;complexType name="tAssign">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tActivity">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}copy"/>
 *           &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}extensionAssignOperation"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="validate" type="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tBoolean" default="no" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tAssign", propOrder = {
    "copyOrExtensionAssignOperation"
})
public class TAssign
    extends TActivity {

    @XmlElements({
        @XmlElement(name = "copy", type = TCopy.class),
        @XmlElement(name = "extensionAssignOperation", type = TExtensionAssignOperation.class)
    })
    protected List<TExtensibleElements> copyOrExtensionAssignOperation;
    @XmlAttribute(name = "validate")
    protected TBoolean validate;

    @Override
    public String toString() {
        return "TAssign{" +
            "copyOrExtensionAssignOperation=" + copyOrExtensionAssignOperation +
            ", validate=" + validate +
            '}';
    }

    /**
     * Gets the value of the copyOrExtensionAssignOperation property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the copyOrExtensionAssignOperation property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCopyOrExtensionAssignOperation().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TCopy }
     * {@link TExtensionAssignOperation }
     */
    public List<TExtensibleElements> getCopyOrExtensionAssignOperation() {
        if (copyOrExtensionAssignOperation == null) {
            copyOrExtensionAssignOperation = new ArrayList<TExtensibleElements>();
        }
        return this.copyOrExtensionAssignOperation;
    }

    /**
     * Ruft den Wert der validate-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TBoolean }
     */
    public TBoolean getValidate() {
        if (validate == null) {
            return TBoolean.NO;
        } else {
            return validate;
        }
    }

    /**
     * Legt den Wert der validate-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TBoolean }
     */
    public void setValidate(TBoolean value) {
        this.validate = value;
    }

}
