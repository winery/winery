/*******************************************************************************
 * Copyright (c) 2013-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.common;

import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Class to produce DocumentBuilders with a pre-loaded TOSCA XSD.
 *
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
		URL resource = this.getClass().getResource("/TOSCA-v1.0.xsd");
		try {
			// takes a few seconds to load
			schema = schemaFactory.newSchema(resource);
			this.schemaAwareFactory.setSchema(schema);
		} catch (SAXException e) {
			// TODO: load xml.xsd in offline mode
			ToscaDocumentBuilderFactory.LOGGER.error("Schema could not be initialized", e);
			ToscaDocumentBuilderFactory.LOGGER.debug("We continue nevertheless to enable offline usage");
		}

		this.plainFactory = DocumentBuilderFactory.newInstance();
		this.plainFactory.setNamespaceAware(true);
		this.plainFactory.setValidating(false);
	}

	public DocumentBuilder getSchemaAwareToscaDocumentBuilder() {
		DocumentBuilder db;
		try {
			db = this.schemaAwareFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException("document builder could not be initialized", e);
		}
		return db;
	}

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
