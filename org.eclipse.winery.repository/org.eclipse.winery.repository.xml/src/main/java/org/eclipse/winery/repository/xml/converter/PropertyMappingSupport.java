/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.xml.converter;

import java.util.LinkedHashMap;

import org.eclipse.winery.model.tosca.xml.TEntityTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class PropertyMappingSupport {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyMappingSupport.class);
    
    public static boolean isKeyValuePropertyDefinition(TEntityTemplate.Properties xmlProps) {
        Object any = xmlProps.getAny();
        if (any == null) {
            return false;
        }
        if (!(any instanceof Element)) {
            return false;
        }
        
        Element el = (Element) any;
        final NodeList xmlChildren = el.getChildNodes();
        if (xmlChildren.getLength() == 0) {
            return false;
        }
        // if any of the child nodes has multiple children it's not KVProps
        for (int i = 0; i < xmlChildren.getLength(); i++) {
            Node item = xmlChildren.item(i);
            if (item instanceof Text || item instanceof Comment) {
                continue;
            }
            if (!(item instanceof Element)) {
                // this is definitively not a KVProp schema conform xml tree
                return false;
            }
            if (item.getChildNodes().getLength() > 1) {
                // more than a single child element at a key, so not KVProp
                return false;
            }
        }
        return true;
    }
    
    public static LinkedHashMap<String, String> convertToKVProperties(TEntityTemplate.Properties propDef) {
        if (!isKeyValuePropertyDefinition(propDef)) {
            throw new IllegalArgumentException("Passed properties are not KeyValue properties and therefore can't be converted as such");
        }
        final LinkedHashMap<String, String> result = new LinkedHashMap<>();
        // accessing like this is safe because 
        // all preconditions are checked with #isKeyValuePropertyDefinition
        final NodeList entries = ((Element)propDef.getAny()).getChildNodes();
        for (int i = 0; i < entries.getLength(); i++) {
            final Node item = entries.item(i);
            if (!(item instanceof Element)) {
                continue;
            }
            final String key = item.getLocalName();
            NodeList entryChildren = item.getChildNodes();
            if (entryChildren.getLength() == 0) {
                result.put(key, "");
            } else if (entryChildren.getLength() == 1) {
                result.put(key, item.getTextContent());
            } else {
                // this shouldn't ever happen, actually...
                LOGGER.error("Precondition violated when converting XML element to KV Property Map");
            }
        }
        return result;
    }

}
