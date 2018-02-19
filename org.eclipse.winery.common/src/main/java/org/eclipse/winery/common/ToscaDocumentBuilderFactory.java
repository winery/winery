/*******************************************************************************
 * Copyright (c) 2013-2017 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/
package org.eclipse.winery.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.net.URL;

/**
 * Class to produce DocumentBuilders with a pre-loaded TOSCA XSD.
 * <p>
 * In a separate class as TOSCA XSD loading takes a few seconds
 */
public class ToscaDocumentBuilderFactory {

    public static final ToscaDocumentBuilderFactory INSTANCE = new ToscaDocumentBuilderFactory();

    private static final Logger LOGGER = LoggerFactory.getLogger(ToscaDocumentBuilderFactory.class);

    private final DocumentBuilderFactory schemaAwareFactory;
    private final DocumentBuilderFactory plainFactory;


    public ToscaDocumentBuilderFactory() {
        this.schemaAwareFactory = DocumentBuilderFactory.newInstance();
        this.schemaAwareFactory.setNamespaceAware(true);
        // we do not need DTD validation
        this.schemaAwareFactory.setValidating(false);

        // we do XSD validation
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema;
        URL xmlXsdUrl = this.getClass().getResource("/xml.xsd");
        URL toscaV10XsdUrl = this.getClass().getResource("/TOSCA-v1.0.xsd");
        try {
            // takes a few seconds to load
            // we have xml.xsd locally, which should enable offline validation
            schema = schemaFactory.newSchema(new Source[]{
                new StreamSource(xmlXsdUrl.toString()),
                new StreamSource(toscaV10XsdUrl.toString())
            });
            this.schemaAwareFactory.setSchema(schema);
        } catch (SAXException e) {
            // This should never happen. If it happens, then xml.xsd might not be available online.
            LOGGER.error("Schema could not be initialized", e);
            LOGGER.debug("We continue nevertheless to enable offline usage");
        }

        this.plainFactory = DocumentBuilderFactory.newInstance();
        this.plainFactory.setNamespaceAware(true);
        this.plainFactory.setValidating(false);
    }

    /**
     * @throws IllegalStateException in case the document builder could not be created
     */
    public DocumentBuilder getSchemaAwareToscaDocumentBuilder() {
        DocumentBuilder db;
        try {
            db = this.schemaAwareFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("document builder could not be initialized", e);
        }
        return db;
    }

    /**
     * @throws IllegalStateException in case the document builder could not be created
     */
    public DocumentBuilder getPlainToscaDocumentBuilder() {
        DocumentBuilder db;
        try {
            db = this.schemaAwareFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("document builder could not be initialized", e);
        }
        return db;
    }
}
