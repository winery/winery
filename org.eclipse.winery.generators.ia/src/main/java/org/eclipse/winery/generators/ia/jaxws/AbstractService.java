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

package org.eclipse.winery.generators.ia.jaxws;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;

import org.eclipse.winery.highlevelrestapi.HighLevelRestApi;

import org.apache.cxf.headers.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

/**
 * Abstract class providing common functionality for JAX-WS web services.
 */
public abstract class AbstractService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractService.class);

    @Resource
    protected WebServiceContext ctx;

    protected void sendResponse(HashMap<String, String> returnParameters) {

        // Extract headers from message
        List<Header> headers = Headers.asList(ctx);

        // Find ReplyTo and MessageID SOAP Header
        String replyTo = null;
        String messageID = null;
        for (Header iter : headers) {
            Object headerObject = iter.getObject();
            // Unmarshall to org.w3c.dom.Node
            if (headerObject instanceof Node) {
                Node node = (Node) headerObject;
                String localPart = iter.getName().getLocalPart();
                String content = node.getTextContent();
                // Extract ReplyTo Header value
                if ("ReplyTo".equals(localPart)) {
                    replyTo = content;
                }
                // Extract MessageID Header value
                if ("MessageID".equals(localPart)) {
                    messageID = content;
                }
            }
        }

        // Create asynchronous SOAP Response Message
        StringBuilder builder = new StringBuilder();

        builder.append("<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:sch='http://siserver.org/schema'>");
        builder.append("   <soapenv:Header/>");
        builder.append("   <soapenv:Body>");
        builder.append("      <sch:invokeResponse>");
        builder.append("         <sch:MessageID>" + messageID + "</sch:MessageID>");

        // Insert return parameters into asynchronous SOAP Response Message
        for (Entry<String, String> paramIter : returnParameters.entrySet()) {
            String key = paramIter.getKey();
            String value = paramIter.getValue();
            builder.append("         <" + key + ">" + value + "</" + key + ">");
        }

        builder.append("      </sch:invokeResponse>");
        builder.append("	</soapenv:Body>");
        builder.append("</soapenv:Envelope>");

        // Send SOAP Response Message back to requester
        if (replyTo == null) {
            logger.warn("No 'ReplyTo' header found! Therefore, print reply message:\n{}", builder.toString());
        } else {
            HighLevelRestApi.Post(replyTo, builder.toString(), "");
        }
    }
}
