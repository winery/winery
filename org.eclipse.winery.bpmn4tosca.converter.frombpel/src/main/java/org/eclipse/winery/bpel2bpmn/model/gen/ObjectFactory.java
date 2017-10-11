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
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the org.oasis_open.docs.wsbpel._2_0.process.executable package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ToPart_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "toPart");
    private final static QName _OnMessage_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "onMessage");
    private final static QName _Extensions_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "extensions");
    private final static QName _Condition_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "condition");
    private final static QName _Exit_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "exit");
    private final static QName _Sequence_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "sequence");
    private final static QName _RepeatUntil_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "repeatUntil");
    private final static QName _FaultHandlers_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "faultHandlers");
    private final static QName _JoinCondition_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "joinCondition");
    private final static QName _Branches_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "branches");
    private final static QName _Documentation_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "documentation");
    private final static QName _Query_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "query");
    private final static QName _FromParts_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "fromParts");
    private final static QName _Catch_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "catch");
    private final static QName _From_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "from");
    private final static QName _Links_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "links");
    private final static QName _Flow_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "flow");
    private final static QName _If_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "if");
    private final static QName _Reply_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "reply");
    private final static QName _CompensateScope_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "compensateScope");
    private final static QName _Elseif_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "elseif");
    private final static QName _Else_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "else");
    private final static QName _Pick_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "pick");
    private final static QName _CompensationHandler_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "compensationHandler");
    private final static QName _Source_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "source");
    private final static QName _TransitionCondition_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "transitionCondition");
    private final static QName _RepeatEvery_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "repeatEvery");
    private final static QName _Invoke_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "invoke");
    private final static QName _Empty_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "empty");
    private final static QName _While_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "while");
    private final static QName _CorrelationSet_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "correlationSet");
    private final static QName _CatchAll_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "catchAll");
    private final static QName _To_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "to");
    private final static QName _EventHandlers_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "eventHandlers");
    private final static QName _Until_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "until");
    private final static QName _Variable_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "variable");
    private final static QName _Assign_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "assign");
    private final static QName _Compensate_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "compensate");
    private final static QName _FromPart_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "fromPart");
    private final static QName _ExtensionActivity_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "extensionActivity");
    private final static QName _PartnerLink_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "partnerLink");
    private final static QName _MessageExchange_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "messageExchange");
    private final static QName _OnEvent_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "onEvent");
    private final static QName _CompletionCondition_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "completionCondition");
    private final static QName _Throw_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "throw");
    private final static QName _ToParts_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "toParts");
    private final static QName _MessageExchanges_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "messageExchanges");
    private final static QName _Target_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "target");
    private final static QName _TerminationHandler_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "terminationHandler");
    private final static QName _Process_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "process");
    private final static QName _Variables_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "variables");
    private final static QName _FinalCounterValue_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "finalCounterValue");
    private final static QName _Receive_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "receive");
    private final static QName _Rethrow_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "rethrow");
    private final static QName _ForEach_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "forEach");
    private final static QName _Copy_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "copy");
    private final static QName _Validate_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "validate");
    private final static QName _Scope_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "scope");
    private final static QName _StartCounterValue_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "startCounterValue");
    private final static QName _For_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "for");
    private final static QName _Link_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "link");
    private final static QName _Literal_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "literal");
    private final static QName _Targets_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "targets");
    private final static QName _Extension_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "extension");
    private final static QName _Wait_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "wait");
    private final static QName _CorrelationSets_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "correlationSets");
    private final static QName _PartnerLinks_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "partnerLinks");
    private final static QName _ExtensionAssignOperation_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "extensionAssignOperation");
    private final static QName _Import_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "import");
    private final static QName _Sources_QNAME = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "sources");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.oasis_open.docs.wsbpel._2_0.process.executable
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TActivityContainer }
     */
    public TActivityContainer createTActivityContainer() {
        return new TActivityContainer();
    }

    /**
     * Create an instance of {@link TCorrelationSet }
     */
    public TCorrelationSet createTCorrelationSet() {
        return new TCorrelationSet();
    }

    /**
     * Create an instance of {@link TDurationExpr }
     */
    public TDurationExpr createTDurationExpr() {
        return new TDurationExpr();
    }

    /**
     * Create an instance of {@link TInvoke }
     */
    public TInvoke createTInvoke() {
        return new TInvoke();
    }

    /**
     * Create an instance of {@link TSource }
     */
    public TSource createTSource() {
        return new TSource();
    }

    /**
     * Create an instance of {@link TCondition }
     */
    public TCondition createTCondition() {
        return new TCondition();
    }

    /**
     * Create an instance of {@link TWhile }
     */
    public TWhile createTWhile() {
        return new TWhile();
    }

    /**
     * Create an instance of {@link TEmpty }
     */
    public TEmpty createTEmpty() {
        return new TEmpty();
    }

    /**
     * Create an instance of {@link TCompensateScope }
     */
    public TCompensateScope createTCompensateScope() {
        return new TCompensateScope();
    }

    /**
     * Create an instance of {@link TPick }
     */
    public TPick createTPick() {
        return new TPick();
    }

    /**
     * Create an instance of {@link TElseif }
     */
    public TElseif createTElseif() {
        return new TElseif();
    }

    /**
     * Create an instance of {@link TFrom }
     */
    public TFrom createTFrom() {
        return new TFrom();
    }

    /**
     * Create an instance of {@link TLinks }
     */
    public TLinks createTLinks() {
        return new TLinks();
    }

    /**
     * Create an instance of {@link TCatch }
     */
    public TCatch createTCatch() {
        return new TCatch();
    }

    /**
     * Create an instance of {@link TReply }
     */
    public TReply createTReply() {
        return new TReply();
    }

    /**
     * Create an instance of {@link TIf }
     */
    public TIf createTIf() {
        return new TIf();
    }

    /**
     * Create an instance of {@link TFlow }
     */
    public TFlow createTFlow() {
        return new TFlow();
    }

    /**
     * Create an instance of {@link TFromParts }
     */
    public TFromParts createTFromParts() {
        return new TFromParts();
    }

    /**
     * Create an instance of {@link TDocumentation }
     */
    public TDocumentation createTDocumentation() {
        return new TDocumentation();
    }

    /**
     * Create an instance of {@link TQuery }
     */
    public TQuery createTQuery() {
        return new TQuery();
    }

    /**
     * Create an instance of {@link TFaultHandlers }
     */
    public TFaultHandlers createTFaultHandlers() {
        return new TFaultHandlers();
    }

    /**
     * Create an instance of {@link TRepeatUntil }
     */
    public TRepeatUntil createTRepeatUntil() {
        return new TRepeatUntil();
    }

    /**
     * Create an instance of {@link TBranches }
     */
    public TBranches createTBranches() {
        return new TBranches();
    }

    /**
     * Create an instance of {@link TExit }
     */
    public TExit createTExit() {
        return new TExit();
    }

    /**
     * Create an instance of {@link TSequence }
     */
    public TSequence createTSequence() {
        return new TSequence();
    }

    /**
     * Create an instance of {@link TExtensions }
     */
    public TExtensions createTExtensions() {
        return new TExtensions();
    }

    /**
     * Create an instance of {@link TBooleanExpr }
     */
    public TBooleanExpr createTBooleanExpr() {
        return new TBooleanExpr();
    }

    /**
     * Create an instance of {@link TOnMessage }
     */
    public TOnMessage createTOnMessage() {
        return new TOnMessage();
    }

    /**
     * Create an instance of {@link TToPart }
     */
    public TToPart createTToPart() {
        return new TToPart();
    }

    /**
     * Create an instance of {@link TCorrelationSets }
     */
    public TCorrelationSets createTCorrelationSets() {
        return new TCorrelationSets();
    }

    /**
     * Create an instance of {@link TExtension }
     */
    public TExtension createTExtension() {
        return new TExtension();
    }

    /**
     * Create an instance of {@link TWait }
     */
    public TWait createTWait() {
        return new TWait();
    }

    /**
     * Create an instance of {@link TImport }
     */
    public TImport createTImport() {
        return new TImport();
    }

    /**
     * Create an instance of {@link TSources }
     */
    public TSources createTSources() {
        return new TSources();
    }

    /**
     * Create an instance of {@link TExtensionAssignOperation }
     */
    public TExtensionAssignOperation createTExtensionAssignOperation() {
        return new TExtensionAssignOperation();
    }

    /**
     * Create an instance of {@link TPartnerLinks }
     */
    public TPartnerLinks createTPartnerLinks() {
        return new TPartnerLinks();
    }

    /**
     * Create an instance of {@link TLink }
     */
    public TLink createTLink() {
        return new TLink();
    }

    /**
     * Create an instance of {@link TExpression }
     */
    public TExpression createTExpression() {
        return new TExpression();
    }

    /**
     * Create an instance of {@link TTargets }
     */
    public TTargets createTTargets() {
        return new TTargets();
    }

    /**
     * Create an instance of {@link TLiteral }
     */
    public TLiteral createTLiteral() {
        return new TLiteral();
    }

    /**
     * Create an instance of {@link TScope }
     */
    public TScope createTScope() {
        return new TScope();
    }

    /**
     * Create an instance of {@link TCopy }
     */
    public TCopy createTCopy() {
        return new TCopy();
    }

    /**
     * Create an instance of {@link TValidate }
     */
    public TValidate createTValidate() {
        return new TValidate();
    }

    /**
     * Create an instance of {@link TRethrow }
     */
    public TRethrow createTRethrow() {
        return new TRethrow();
    }

    /**
     * Create an instance of {@link TReceive }
     */
    public TReceive createTReceive() {
        return new TReceive();
    }

    /**
     * Create an instance of {@link TProcess }
     */
    public TProcess createTProcess() {
        return new TProcess();
    }

    /**
     * Create an instance of {@link TVariables }
     */
    public TVariables createTVariables() {
        return new TVariables();
    }

    /**
     * Create an instance of {@link TForEach }
     */
    public TForEach createTForEach() {
        return new TForEach();
    }

    /**
     * Create an instance of {@link TMessageExchanges }
     */
    public TMessageExchanges createTMessageExchanges() {
        return new TMessageExchanges();
    }

    /**
     * Create an instance of {@link TTarget }
     */
    public TTarget createTTarget() {
        return new TTarget();
    }

    /**
     * Create an instance of {@link TExtensionActivity }
     */
    public TExtensionActivity createTExtensionActivity() {
        return new TExtensionActivity();
    }

    /**
     * Create an instance of {@link TFromPart }
     */
    public TFromPart createTFromPart() {
        return new TFromPart();
    }

    /**
     * Create an instance of {@link TToParts }
     */
    public TToParts createTToParts() {
        return new TToParts();
    }

    /**
     * Create an instance of {@link TCompletionCondition }
     */
    public TCompletionCondition createTCompletionCondition() {
        return new TCompletionCondition();
    }

    /**
     * Create an instance of {@link TThrow }
     */
    public TThrow createTThrow() {
        return new TThrow();
    }

    /**
     * Create an instance of {@link TPartnerLink }
     */
    public TPartnerLink createTPartnerLink() {
        return new TPartnerLink();
    }

    /**
     * Create an instance of {@link TMessageExchange }
     */
    public TMessageExchange createTMessageExchange() {
        return new TMessageExchange();
    }

    /**
     * Create an instance of {@link TOnEvent }
     */
    public TOnEvent createTOnEvent() {
        return new TOnEvent();
    }

    /**
     * Create an instance of {@link TVariable }
     */
    public TVariable createTVariable() {
        return new TVariable();
    }

    /**
     * Create an instance of {@link TEventHandlers }
     */
    public TEventHandlers createTEventHandlers() {
        return new TEventHandlers();
    }

    /**
     * Create an instance of {@link TDeadlineExpr }
     */
    public TDeadlineExpr createTDeadlineExpr() {
        return new TDeadlineExpr();
    }

    /**
     * Create an instance of {@link TTo }
     */
    public TTo createTTo() {
        return new TTo();
    }

    /**
     * Create an instance of {@link TCompensate }
     */
    public TCompensate createTCompensate() {
        return new TCompensate();
    }

    /**
     * Create an instance of {@link TAssign }
     */
    public TAssign createTAssign() {
        return new TAssign();
    }

    /**
     * Create an instance of {@link TCorrelations }
     */
    public TCorrelations createTCorrelations() {
        return new TCorrelations();
    }

    /**
     * Create an instance of {@link TActivity }
     */
    public TActivity createTActivity() {
        return new TActivity();
    }

    /**
     * Create an instance of {@link TExtensibleElements }
     */
    public TExtensibleElements createTExtensibleElements() {
        return new TExtensibleElements();
    }

    /**
     * Create an instance of {@link TCorrelationWithPattern }
     */
    public TCorrelationWithPattern createTCorrelationWithPattern() {
        return new TCorrelationWithPattern();
    }

    /**
     * Create an instance of {@link TOnAlarmPick }
     */
    public TOnAlarmPick createTOnAlarmPick() {
        return new TOnAlarmPick();
    }

    /**
     * Create an instance of {@link TOnMsgCommon }
     */
    public TOnMsgCommon createTOnMsgCommon() {
        return new TOnMsgCommon();
    }

    /**
     * Create an instance of {@link TOnAlarmEvent }
     */
    public TOnAlarmEvent createTOnAlarmEvent() {
        return new TOnAlarmEvent();
    }

    /**
     * Create an instance of {@link TCorrelationsWithPattern }
     */
    public TCorrelationsWithPattern createTCorrelationsWithPattern() {
        return new TCorrelationsWithPattern();
    }

    /**
     * Create an instance of {@link TCorrelation }
     */
    public TCorrelation createTCorrelation() {
        return new TCorrelation();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TToPart }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "toPart")
    public JAXBElement<TToPart> createToPart(TToPart value) {
        return new JAXBElement<TToPart>(_ToPart_QNAME, TToPart.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TOnMessage }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "onMessage")
    public JAXBElement<TOnMessage> createOnMessage(TOnMessage value) {
        return new JAXBElement<TOnMessage>(_OnMessage_QNAME, TOnMessage.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TExtensions }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "extensions")
    public JAXBElement<TExtensions> createExtensions(TExtensions value) {
        return new JAXBElement<TExtensions>(_Extensions_QNAME, TExtensions.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TBooleanExpr }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "condition")
    public JAXBElement<TBooleanExpr> createCondition(TBooleanExpr value) {
        return new JAXBElement<TBooleanExpr>(_Condition_QNAME, TBooleanExpr.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TExit }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "exit")
    public JAXBElement<TExit> createExit(TExit value) {
        return new JAXBElement<TExit>(_Exit_QNAME, TExit.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TSequence }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "sequence")
    public JAXBElement<TSequence> createSequence(TSequence value) {
        return new JAXBElement<TSequence>(_Sequence_QNAME, TSequence.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TRepeatUntil }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "repeatUntil")
    public JAXBElement<TRepeatUntil> createRepeatUntil(TRepeatUntil value) {
        return new JAXBElement<TRepeatUntil>(_RepeatUntil_QNAME, TRepeatUntil.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TFaultHandlers }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "faultHandlers")
    public JAXBElement<TFaultHandlers> createFaultHandlers(TFaultHandlers value) {
        return new JAXBElement<TFaultHandlers>(_FaultHandlers_QNAME, TFaultHandlers.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TCondition }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "joinCondition")
    public JAXBElement<TCondition> createJoinCondition(TCondition value) {
        return new JAXBElement<TCondition>(_JoinCondition_QNAME, TCondition.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TBranches }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "branches")
    public JAXBElement<TBranches> createBranches(TBranches value) {
        return new JAXBElement<TBranches>(_Branches_QNAME, TBranches.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TDocumentation }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "documentation")
    public JAXBElement<TDocumentation> createDocumentation(TDocumentation value) {
        return new JAXBElement<TDocumentation>(_Documentation_QNAME, TDocumentation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TQuery }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "query")
    public JAXBElement<TQuery> createQuery(TQuery value) {
        return new JAXBElement<TQuery>(_Query_QNAME, TQuery.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TFromParts }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "fromParts")
    public JAXBElement<TFromParts> createFromParts(TFromParts value) {
        return new JAXBElement<TFromParts>(_FromParts_QNAME, TFromParts.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TCatch }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "catch")
    public JAXBElement<TCatch> createCatch(TCatch value) {
        return new JAXBElement<TCatch>(_Catch_QNAME, TCatch.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TFrom }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "from")
    public JAXBElement<TFrom> createFrom(TFrom value) {
        return new JAXBElement<TFrom>(_From_QNAME, TFrom.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TLinks }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "links")
    public JAXBElement<TLinks> createLinks(TLinks value) {
        return new JAXBElement<TLinks>(_Links_QNAME, TLinks.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TFlow }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "flow")
    public JAXBElement<TFlow> createFlow(TFlow value) {
        return new JAXBElement<TFlow>(_Flow_QNAME, TFlow.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TIf }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "if")
    public JAXBElement<TIf> createIf(TIf value) {
        return new JAXBElement<TIf>(_If_QNAME, TIf.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TReply }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "reply")
    public JAXBElement<TReply> createReply(TReply value) {
        return new JAXBElement<TReply>(_Reply_QNAME, TReply.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TCompensateScope }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "compensateScope")
    public JAXBElement<TCompensateScope> createCompensateScope(TCompensateScope value) {
        return new JAXBElement<TCompensateScope>(_CompensateScope_QNAME, TCompensateScope.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TElseif }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "elseif")
    public JAXBElement<TElseif> createElseif(TElseif value) {
        return new JAXBElement<TElseif>(_Elseif_QNAME, TElseif.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TActivityContainer }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "else")
    public JAXBElement<TActivityContainer> createElse(TActivityContainer value) {
        return new JAXBElement<TActivityContainer>(_Else_QNAME, TActivityContainer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TPick }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "pick")
    public JAXBElement<TPick> createPick(TPick value) {
        return new JAXBElement<TPick>(_Pick_QNAME, TPick.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TActivityContainer }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "compensationHandler")
    public JAXBElement<TActivityContainer> createCompensationHandler(TActivityContainer value) {
        return new JAXBElement<TActivityContainer>(_CompensationHandler_QNAME, TActivityContainer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TSource }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "source")
    public JAXBElement<TSource> createSource(TSource value) {
        return new JAXBElement<TSource>(_Source_QNAME, TSource.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TCondition }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "transitionCondition")
    public JAXBElement<TCondition> createTransitionCondition(TCondition value) {
        return new JAXBElement<TCondition>(_TransitionCondition_QNAME, TCondition.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TDurationExpr }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "repeatEvery")
    public JAXBElement<TDurationExpr> createRepeatEvery(TDurationExpr value) {
        return new JAXBElement<TDurationExpr>(_RepeatEvery_QNAME, TDurationExpr.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TInvoke }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "invoke")
    public JAXBElement<TInvoke> createInvoke(TInvoke value) {
        return new JAXBElement<TInvoke>(_Invoke_QNAME, TInvoke.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TEmpty }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "empty")
    public JAXBElement<TEmpty> createEmpty(TEmpty value) {
        return new JAXBElement<TEmpty>(_Empty_QNAME, TEmpty.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TWhile }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "while")
    public JAXBElement<TWhile> createWhile(TWhile value) {
        return new JAXBElement<TWhile>(_While_QNAME, TWhile.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TCorrelationSet }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "correlationSet")
    public JAXBElement<TCorrelationSet> createCorrelationSet(TCorrelationSet value) {
        return new JAXBElement<TCorrelationSet>(_CorrelationSet_QNAME, TCorrelationSet.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TActivityContainer }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "catchAll")
    public JAXBElement<TActivityContainer> createCatchAll(TActivityContainer value) {
        return new JAXBElement<TActivityContainer>(_CatchAll_QNAME, TActivityContainer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TTo }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "to")
    public JAXBElement<TTo> createTo(TTo value) {
        return new JAXBElement<TTo>(_To_QNAME, TTo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TEventHandlers }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "eventHandlers")
    public JAXBElement<TEventHandlers> createEventHandlers(TEventHandlers value) {
        return new JAXBElement<TEventHandlers>(_EventHandlers_QNAME, TEventHandlers.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TDeadlineExpr }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "until")
    public JAXBElement<TDeadlineExpr> createUntil(TDeadlineExpr value) {
        return new JAXBElement<TDeadlineExpr>(_Until_QNAME, TDeadlineExpr.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TVariable }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "variable")
    public JAXBElement<TVariable> createVariable(TVariable value) {
        return new JAXBElement<TVariable>(_Variable_QNAME, TVariable.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TAssign }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "assign")
    public JAXBElement<TAssign> createAssign(TAssign value) {
        return new JAXBElement<TAssign>(_Assign_QNAME, TAssign.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TCompensate }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "compensate")
    public JAXBElement<TCompensate> createCompensate(TCompensate value) {
        return new JAXBElement<TCompensate>(_Compensate_QNAME, TCompensate.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TFromPart }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "fromPart")
    public JAXBElement<TFromPart> createFromPart(TFromPart value) {
        return new JAXBElement<TFromPart>(_FromPart_QNAME, TFromPart.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TExtensionActivity }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "extensionActivity")
    public JAXBElement<TExtensionActivity> createExtensionActivity(TExtensionActivity value) {
        return new JAXBElement<TExtensionActivity>(_ExtensionActivity_QNAME, TExtensionActivity.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TPartnerLink }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "partnerLink")
    public JAXBElement<TPartnerLink> createPartnerLink(TPartnerLink value) {
        return new JAXBElement<TPartnerLink>(_PartnerLink_QNAME, TPartnerLink.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TMessageExchange }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "messageExchange")
    public JAXBElement<TMessageExchange> createMessageExchange(TMessageExchange value) {
        return new JAXBElement<TMessageExchange>(_MessageExchange_QNAME, TMessageExchange.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TOnEvent }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "onEvent")
    public JAXBElement<TOnEvent> createOnEvent(TOnEvent value) {
        return new JAXBElement<TOnEvent>(_OnEvent_QNAME, TOnEvent.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TCompletionCondition }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "completionCondition")
    public JAXBElement<TCompletionCondition> createCompletionCondition(TCompletionCondition value) {
        return new JAXBElement<TCompletionCondition>(_CompletionCondition_QNAME, TCompletionCondition.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TThrow }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "throw")
    public JAXBElement<TThrow> createThrow(TThrow value) {
        return new JAXBElement<TThrow>(_Throw_QNAME, TThrow.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TToParts }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "toParts")
    public JAXBElement<TToParts> createToParts(TToParts value) {
        return new JAXBElement<TToParts>(_ToParts_QNAME, TToParts.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TMessageExchanges }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "messageExchanges")
    public JAXBElement<TMessageExchanges> createMessageExchanges(TMessageExchanges value) {
        return new JAXBElement<TMessageExchanges>(_MessageExchanges_QNAME, TMessageExchanges.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TTarget }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "target")
    public JAXBElement<TTarget> createTarget(TTarget value) {
        return new JAXBElement<TTarget>(_Target_QNAME, TTarget.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TActivityContainer }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "terminationHandler")
    public JAXBElement<TActivityContainer> createTerminationHandler(TActivityContainer value) {
        return new JAXBElement<TActivityContainer>(_TerminationHandler_QNAME, TActivityContainer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TProcess }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "process")
    public JAXBElement<TProcess> createProcess(TProcess value) {
        return new JAXBElement<TProcess>(_Process_QNAME, TProcess.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TVariables }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "variables")
    public JAXBElement<TVariables> createVariables(TVariables value) {
        return new JAXBElement<TVariables>(_Variables_QNAME, TVariables.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TExpression }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "finalCounterValue")
    public JAXBElement<TExpression> createFinalCounterValue(TExpression value) {
        return new JAXBElement<TExpression>(_FinalCounterValue_QNAME, TExpression.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TReceive }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "receive")
    public JAXBElement<TReceive> createReceive(TReceive value) {
        return new JAXBElement<TReceive>(_Receive_QNAME, TReceive.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TRethrow }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "rethrow")
    public JAXBElement<TRethrow> createRethrow(TRethrow value) {
        return new JAXBElement<TRethrow>(_Rethrow_QNAME, TRethrow.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TForEach }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "forEach")
    public JAXBElement<TForEach> createForEach(TForEach value) {
        return new JAXBElement<TForEach>(_ForEach_QNAME, TForEach.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TCopy }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "copy")
    public JAXBElement<TCopy> createCopy(TCopy value) {
        return new JAXBElement<TCopy>(_Copy_QNAME, TCopy.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TValidate }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "validate")
    public JAXBElement<TValidate> createValidate(TValidate value) {
        return new JAXBElement<TValidate>(_Validate_QNAME, TValidate.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TScope }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "scope")
    public JAXBElement<TScope> createScope(TScope value) {
        return new JAXBElement<TScope>(_Scope_QNAME, TScope.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TExpression }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "startCounterValue")
    public JAXBElement<TExpression> createStartCounterValue(TExpression value) {
        return new JAXBElement<TExpression>(_StartCounterValue_QNAME, TExpression.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TDurationExpr }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "for")
    public JAXBElement<TDurationExpr> createFor(TDurationExpr value) {
        return new JAXBElement<TDurationExpr>(_For_QNAME, TDurationExpr.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TLink }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "link")
    public JAXBElement<TLink> createLink(TLink value) {
        return new JAXBElement<TLink>(_Link_QNAME, TLink.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TLiteral }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "literal")
    public JAXBElement<TLiteral> createLiteral(TLiteral value) {
        return new JAXBElement<TLiteral>(_Literal_QNAME, TLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TTargets }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "targets")
    public JAXBElement<TTargets> createTargets(TTargets value) {
        return new JAXBElement<TTargets>(_Targets_QNAME, TTargets.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TExtension }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "extension")
    public JAXBElement<TExtension> createExtension(TExtension value) {
        return new JAXBElement<TExtension>(_Extension_QNAME, TExtension.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TWait }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "wait")
    public JAXBElement<TWait> createWait(TWait value) {
        return new JAXBElement<TWait>(_Wait_QNAME, TWait.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TCorrelationSets }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "correlationSets")
    public JAXBElement<TCorrelationSets> createCorrelationSets(TCorrelationSets value) {
        return new JAXBElement<TCorrelationSets>(_CorrelationSets_QNAME, TCorrelationSets.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TPartnerLinks }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "partnerLinks")
    public JAXBElement<TPartnerLinks> createPartnerLinks(TPartnerLinks value) {
        return new JAXBElement<TPartnerLinks>(_PartnerLinks_QNAME, TPartnerLinks.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TExtensionAssignOperation }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "extensionAssignOperation")
    public JAXBElement<TExtensionAssignOperation> createExtensionAssignOperation(TExtensionAssignOperation value) {
        return new JAXBElement<TExtensionAssignOperation>(_ExtensionAssignOperation_QNAME, TExtensionAssignOperation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TImport }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "import")
    public JAXBElement<TImport> createImport(TImport value) {
        return new JAXBElement<TImport>(_Import_QNAME, TImport.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TSources }{@code >}}
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable", name = "sources")
    public JAXBElement<TSources> createSources(TSources value) {
        return new JAXBElement<TSources>(_Sources_QNAME, TSources.class, null, value);
    }

}
