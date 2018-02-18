/*******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;

public class ToscaDocumentBuilderFactoryTest {

    private DocumentBuilder documentBuilder;
    private StringBuilder errorStringBuilder;

    private static ErrorHandler getErrorHandler(StringBuilder sb) {
        return new ErrorHandler() {

            @Override
            public void warning(SAXParseException exception) throws SAXException {
                // we don't care
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                sb.append("Fatal Error: ");
                sb.append(exception.getMessage());
                sb.append("\n");
            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
                sb.append("Fatal Error: ");
                sb.append(exception.getMessage());
                sb.append("\n");
            }
        };
    }
    
    @Before
    public void init() {
        documentBuilder = ToscaDocumentBuilderFactory.INSTANCE.getSchemaAwareToscaDocumentBuilder();
        errorStringBuilder = new StringBuilder();
        documentBuilder.setErrorHandler(getErrorHandler(errorStringBuilder));
    }

    @Test
    public void apacheWebServerXmlIsValid() throws Exception {
        final Document document = documentBuilder.parse(this.getClass().getResource("apachewebserver.xml").toString());
        Assert.assertEquals("", errorStringBuilder.toString());
    }

    @Test
    public void invalidToscaXmlIsInvalid() throws Exception {
        final Document document = documentBuilder.parse(this.getClass().getResource("invalidTosca.xml").toString());
        Assert.assertNotEquals("", errorStringBuilder.toString());
    }

    @Test
    @Ignore("TODO: Rewrite in JUnit5 to expect org.xml.sax.SAXParseException")
    public void invalidXmlIsInvalid() throws Exception {
        final Document document = documentBuilder.parse(this.getClass().getResource("invalid.xml").toString());
        Assert.assertNotEquals("", errorStringBuilder.toString());
    }
}
