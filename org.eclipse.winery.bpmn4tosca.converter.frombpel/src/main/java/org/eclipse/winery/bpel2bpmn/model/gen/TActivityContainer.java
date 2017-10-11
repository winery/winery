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
 * <p>Java-Klasse für tActivityContainer complex type.
 * <p>
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;complexType name="tActivityContainer">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tExtensibleElements">
 *       &lt;sequence>
 *         &lt;group ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}activity"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tActivityContainer", propOrder = {
    "assign",
    "compensate",
    "compensateScope",
    "empty",
    "exit",
    "extensionActivity",
    "flow",
    "forEach",
    "_if",
    "invoke",
    "pick",
    "receive",
    "repeatUntil",
    "reply",
    "rethrow",
    "scope",
    "sequence",
    "_throw",
    "validate",
    "wait",
    "_while"
})
@XmlSeeAlso({
    TCatch.class
})
public class TActivityContainer
    extends TExtensibleElements {

    protected TAssign assign;
    protected TCompensate compensate;
    protected TCompensateScope compensateScope;
    protected TEmpty empty;
    protected TExit exit;
    protected TExtensionActivity extensionActivity;
    protected TFlow flow;
    protected TForEach forEach;
    @XmlElement(name = "if")
    protected TIf _if;
    protected TInvoke invoke;
    protected TPick pick;
    protected TReceive receive;
    protected TRepeatUntil repeatUntil;
    protected TReply reply;
    protected TRethrow rethrow;
    protected TScope scope;
    protected TSequence sequence;
    @XmlElement(name = "throw")
    protected TThrow _throw;
    protected TValidate validate;
    protected TWait wait;
    @XmlElement(name = "while")
    protected TWhile _while;

    /**
     * Ruft den Wert der assign-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TAssign }
     */
    public TAssign getAssign() {
        return assign;
    }

    /**
     * Legt den Wert der assign-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TAssign }
     */
    public void setAssign(TAssign value) {
        this.assign = value;
    }

    /**
     * Ruft den Wert der compensate-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TCompensate }
     */
    public TCompensate getCompensate() {
        return compensate;
    }

    /**
     * Legt den Wert der compensate-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TCompensate }
     */
    public void setCompensate(TCompensate value) {
        this.compensate = value;
    }

    /**
     * Ruft den Wert der compensateScope-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TCompensateScope }
     */
    public TCompensateScope getCompensateScope() {
        return compensateScope;
    }

    /**
     * Legt den Wert der compensateScope-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TCompensateScope }
     */
    public void setCompensateScope(TCompensateScope value) {
        this.compensateScope = value;
    }

    /**
     * Ruft den Wert der empty-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TEmpty }
     */
    public TEmpty getEmpty() {
        return empty;
    }

    /**
     * Legt den Wert der empty-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TEmpty }
     */
    public void setEmpty(TEmpty value) {
        this.empty = value;
    }

    /**
     * Ruft den Wert der exit-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TExit }
     */
    public TExit getExit() {
        return exit;
    }

    /**
     * Legt den Wert der exit-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TExit }
     */
    public void setExit(TExit value) {
        this.exit = value;
    }

    /**
     * Ruft den Wert der extensionActivity-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TExtensionActivity }
     */
    public TExtensionActivity getExtensionActivity() {
        return extensionActivity;
    }

    /**
     * Legt den Wert der extensionActivity-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TExtensionActivity }
     */
    public void setExtensionActivity(TExtensionActivity value) {
        this.extensionActivity = value;
    }

    /**
     * Ruft den Wert der flow-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TFlow }
     */
    public TFlow getFlow() {
        return flow;
    }

    /**
     * Legt den Wert der flow-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TFlow }
     */
    public void setFlow(TFlow value) {
        this.flow = value;
    }

    /**
     * Ruft den Wert der forEach-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TForEach }
     */
    public TForEach getForEach() {
        return forEach;
    }

    /**
     * Legt den Wert der forEach-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TForEach }
     */
    public void setForEach(TForEach value) {
        this.forEach = value;
    }

    /**
     * Ruft den Wert der if-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TIf }
     */
    public TIf getIf() {
        return _if;
    }

    /**
     * Legt den Wert der if-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TIf }
     */
    public void setIf(TIf value) {
        this._if = value;
    }

    /**
     * Ruft den Wert der invoke-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TInvoke }
     */
    public TInvoke getInvoke() {
        return invoke;
    }

    /**
     * Legt den Wert der invoke-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TInvoke }
     */
    public void setInvoke(TInvoke value) {
        this.invoke = value;
    }

    /**
     * Ruft den Wert der pick-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TPick }
     */
    public TPick getPick() {
        return pick;
    }

    /**
     * Legt den Wert der pick-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TPick }
     */
    public void setPick(TPick value) {
        this.pick = value;
    }

    /**
     * Ruft den Wert der receive-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TReceive }
     */
    public TReceive getReceive() {
        return receive;
    }

    /**
     * Legt den Wert der receive-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TReceive }
     */
    public void setReceive(TReceive value) {
        this.receive = value;
    }

    /**
     * Ruft den Wert der repeatUntil-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TRepeatUntil }
     */
    public TRepeatUntil getRepeatUntil() {
        return repeatUntil;
    }

    /**
     * Legt den Wert der repeatUntil-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TRepeatUntil }
     */
    public void setRepeatUntil(TRepeatUntil value) {
        this.repeatUntil = value;
    }

    /**
     * Ruft den Wert der reply-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TReply }
     */
    public TReply getReply() {
        return reply;
    }

    /**
     * Legt den Wert der reply-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TReply }
     */
    public void setReply(TReply value) {
        this.reply = value;
    }

    /**
     * Ruft den Wert der rethrow-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TRethrow }
     */
    public TRethrow getRethrow() {
        return rethrow;
    }

    /**
     * Legt den Wert der rethrow-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TRethrow }
     */
    public void setRethrow(TRethrow value) {
        this.rethrow = value;
    }

    /**
     * Ruft den Wert der scope-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TScope }
     */
    public TScope getScope() {
        return scope;
    }

    /**
     * Legt den Wert der scope-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TScope }
     */
    public void setScope(TScope value) {
        this.scope = value;
    }

    /**
     * Ruft den Wert der sequence-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TSequence }
     */
    public TSequence getSequence() {
        return sequence;
    }

    /**
     * Legt den Wert der sequence-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TSequence }
     */
    public void setSequence(TSequence value) {
        this.sequence = value;
    }

    /**
     * Ruft den Wert der throw-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TThrow }
     */
    public TThrow getThrow() {
        return _throw;
    }

    /**
     * Legt den Wert der throw-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TThrow }
     */
    public void setThrow(TThrow value) {
        this._throw = value;
    }

    /**
     * Ruft den Wert der validate-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TValidate }
     */
    public TValidate getValidate() {
        return validate;
    }

    /**
     * Legt den Wert der validate-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TValidate }
     */
    public void setValidate(TValidate value) {
        this.validate = value;
    }

    /**
     * Ruft den Wert der wait-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TWait }
     */
    public TWait getWait() {
        return wait;
    }

    /**
     * Legt den Wert der wait-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TWait }
     */
    public void setWait(TWait value) {
        this.wait = value;
    }

    /**
     * Ruft den Wert der while-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TWhile }
     */
    public TWhile getWhile() {
        return _while;
    }

    /**
     * Legt den Wert der while-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TWhile }
     */
    public void setWhile(TWhile value) {
        this._while = value;
    }

}
