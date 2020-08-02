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

package org.eclipse.winery.model.jaxbsupport.map;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TEntityTemplate;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class KvPropertiesAdapter extends XmlAdapter<Element, TEntityTemplate.WineryKVProperties> {
    @Override
    public TEntityTemplate.WineryKVProperties unmarshal(Element propertiesElement) throws Exception {
        final String namespace = propertiesElement.getNamespaceURI();
        final String elementName = propertiesElement.getLocalName();
        
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        NodeList childNodes = propertiesElement.getChildNodes();
        for (int x = 0, size = childNodes.getLength(); x < size; x++) {
            Node childNode = childNodes.item(x);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                map.put(childNode.getLocalName(), childNode.getTextContent());
            }
        }
        TEntityTemplate.WineryKVProperties result = new TEntityTemplate.WineryKVProperties();
        result.setNamespace(namespace);
        result.setElementName(elementName);
        result.setKVProperties(map);
        return result;
    }

    @Override
    public Element marshal(TEntityTemplate.WineryKVProperties props) throws Exception {
        final String namespace = props.getNamespace();
        String elementName = props.getElementName();
        if (elementName.equals("")) {
            elementName = "KVProperties";
        }
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
//            LOGGER.debug(e.getMessage(), e);
            throw new IllegalStateException("Could not instantiate document builder", e);
        }
        Document doc = db.newDocument();

        Element root = doc.createElementNS(namespace, elementName);
        doc.appendChild(root);

        // No wpd - so this is not possible:
        // we produce the serialization in the same order the XSD would be generated (because of the usage of xsd:sequence)
        // for (PropertyDefinitionKV prop : wpd.getPropertyDefinitionKVList()) {
        final LinkedHashMap<String, String> properties = props.getKVProperties();
        for (String key : properties.keySet()) {
            Element element = doc.createElementNS(namespace, key);
            root.appendChild(element);
            String value = properties.get(key);
            if (value != null) {
                Text text = doc.createTextNode(value);
                element.appendChild(text);
            }
        }
        return root;
    }
}
