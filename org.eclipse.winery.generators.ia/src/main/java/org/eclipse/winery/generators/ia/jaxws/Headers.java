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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.headers.Header;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.jaxws.context.WrappedMessageContext;
import org.apache.cxf.message.Message;
import org.w3c.dom.Element;

/**
 * Helper class to provide easy access to SOAP headers.
 */
public final class Headers {

    public static Map<String, String> asMap(final WebServiceContext ctx) {
        Map<String, String> headers = new HashMap<>();
        for (Header h : asList(ctx)) {
            Element n = (Element) h.getObject();
            headers.put(n.getLocalName(), n.getTextContent());
        }
        return headers;
    }

    public static List<Header> asList(final WebServiceContext ctx) {
        MessageContext messageContext = ctx.getMessageContext();
        if (messageContext instanceof WrappedMessageContext) {
            Message message = ((WrappedMessageContext) messageContext).getWrappedMessage();
            return CastUtils.cast((List<?>) message.get(Header.HEADER_LIST));
        }
        return new ArrayList<>();
    }
}
