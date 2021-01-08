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

import org.eclipse.winery.model.tosca.constants.Namespaces;
import org.eclipse.winery.model.tosca.xml.XTEntityTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class PropertyMappingSupport {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyMappingSupport.class);
    
    public static boolean isKeyValuePropertyDefinition(XTEntityTemplate.Properties xmlProps) {
        if (xmlProps == null) {
            return false;
        }
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
    
    public static org.eclipse.winery.model.tosca.TEntityTemplate.WineryKVProperties convertToKVProperties(XTEntityTemplate.Properties propDef) {
        if (!isKeyValuePropertyDefinition(propDef)) {
            throw new IllegalArgumentException("Passed properties are not KeyValue properties and therefore can't be converted as such");
        }
        final LinkedHashMap<String, String> properties = new LinkedHashMap<>();
        // ensured by #isKeyValuePropertyDefinition
        final Element element = (Element) propDef.getAny();
        assert (element != null);
        
        final String namespace = element.getNamespaceURI();
        final String elementName = element.getLocalName();
        final NodeList entries = element.getChildNodes();
        for (int i = 0; i < entries.getLength(); i++) {
            final Node item = entries.item(i);
            if (!(item instanceof Element)) {
                continue;
            }
            final String key = item.getLocalName();
            NodeList entryChildren = item.getChildNodes();
            if (entryChildren.getLength() == 0) {
                properties.put(key, "");
            } else if (entryChildren.getLength() == 1) {
                properties.put(key, item.getTextContent());
            } else {
                // this shouldn't ever happen, actually...
                LOGGER.error("Precondition violated when converting XML element to KV Property Map");
            }
        }
        org.eclipse.winery.model.tosca.TEntityTemplate.WineryKVProperties result = new org.eclipse.winery.model.tosca.TEntityTemplate.WineryKVProperties();
        result.setKVProperties(properties);
        // remove defaults required for serialization
        result.setElementName(elementName.equals("Properties") ? null : elementName);
        result.setNamespace(namespace == null || namespace.equals(Namespaces.EXAMPLE_NAMESPACE_URI) ? null : namespace);
        return result;
    }

}
