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
// Generiert: 2017.07.24 um 11:12:17 PM CEST
//


package org.eclipse.winery.bpel2bpmn.model.gen.si;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the org.siserver.schema package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _InvokePlan_QNAME = new QName("http://siserver.org/schema", "invokePlan");
    private final static QName _InvokeOperationAsync_QNAME = new QName("http://siserver.org/schema", "invokeOperationAsync");
    private final static QName _InvokeOperationSync_QNAME = new QName("http://siserver.org/schema", "invokeOperationSync");
    private final static QName _InvokeResponse_QNAME = new QName("http://siserver.org/schema", "invokeResponse");
    private final static QName _InvokeOperation_QNAME = new QName("http://siserver.org/schema", "invokeOperation");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.siserver.schema
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link InvokePlan }
     *
     */
    public InvokePlan createInvokePlan() {
        return new InvokePlan();
    }

    /**
     * Create an instance of {@link InvokeOperationAsync }
     *
     */
    public InvokeOperationAsync createInvokeOperationAsync() {
        return new InvokeOperationAsync();
    }

    /**
     * Create an instance of {@link InvokeOperationSync }
     *
     */
    public InvokeOperationSync createInvokeOperationSync() {
        return new InvokeOperationSync();
    }

    /**
     * Create an instance of {@link InvokeResponse }
     *
     */
    public InvokeResponse createInvokeResponse() {
        return new InvokeResponse();
    }

    /**
     * Create an instance of {@link ParamsMap }
     *
     */
    public ParamsMap createParamsMap() {
        return new ParamsMap();
    }

    /**
     * Create an instance of {@link Doc }
     *
     */
    public Doc createDoc() {
        return new Doc();
    }

    /**
     * Create an instance of {@link ParamsMapItemType }
     *
     */
    public ParamsMapItemType createParamsMapItemType() {
        return new ParamsMapItemType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvokePlan }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://siserver.org/schema", name = "invokePlan")
    public JAXBElement<InvokePlan> createInvokePlan(InvokePlan value) {
        return new JAXBElement<InvokePlan>(_InvokePlan_QNAME, InvokePlan.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvokeOperationAsync }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://siserver.org/schema", name = "invokeOperationAsync")
    public JAXBElement<InvokeOperationAsync> createInvokeOperationAsync(InvokeOperationAsync value) {
        return new JAXBElement<InvokeOperationAsync>(_InvokeOperationAsync_QNAME, InvokeOperationAsync.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvokeOperationSync }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://siserver.org/schema", name = "invokeOperationSync")
    public JAXBElement<InvokeOperationSync> createInvokeOperationSync(InvokeOperationSync value) {
        return new JAXBElement<InvokeOperationSync>(_InvokeOperationSync_QNAME, InvokeOperationSync.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvokeResponse }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://siserver.org/schema", name = "invokeResponse")
    public JAXBElement<InvokeResponse> createInvokeResponse(InvokeResponse value) {
        return new JAXBElement<InvokeResponse>(_InvokeResponse_QNAME, InvokeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvokeOperationAsync }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://siserver.org/schema", name = "invokeOperation")
    public JAXBElement<InvokeOperationAsync> createInvokeOperation(InvokeOperationAsync value) {
        return new JAXBElement<InvokeOperationAsync>(_InvokeOperation_QNAME, InvokeOperationAsync.class, null, value);
    }

}
