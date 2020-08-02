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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.converter.support.Namespaces;
import org.eclipse.winery.model.tosca.TEntityTemplate;

import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class PropertiesAdapter extends XmlAdapter<Object, TEntityTemplate.Properties> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesAdapter.class);

    private enum PropertyKind {
        XML, YAML, KV
    }

    @Override
    public TEntityTemplate.Properties unmarshal(Object xmlData) throws Exception {
        if (!(xmlData instanceof Element)) {
            throw new IllegalStateException("Cannot deserialize arbitrary XML if no Element is available. Expected Element, got " + xmlData.getClass().getName());
        }
        Element element = (Element)xmlData;
        final PropertyKind kind = determinePropertyKind(element);
        switch (kind) {
            case XML:
                TEntityTemplate.XmlProperties xmlProps = new TEntityTemplate.XmlProperties();
                xmlProps.setAny(element);
                return xmlProps;
            case YAML:
                TEntityTemplate.YamlProperties yamlProperties = new TEntityTemplate.YamlProperties();
                // deserialize into hierarchical map
                LinkedHashMap<String, Object> hierarchy = unmarshallNestedMap(element);
                yamlProperties.setProperties(hierarchy);
                return yamlProperties;
            case KV:
                final String namespace = element.getNamespaceURI();
                final String elementName = element.getLocalName();

                LinkedHashMap<String, String> map = new LinkedHashMap<>();
                NodeList childNodes = element.getChildNodes();
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
        throw new IllegalStateException("Could not determine the correct property kind for unmarshalling");
    }

    private LinkedHashMap<String, Object> unmarshallNestedMap(Element element) {
        LinkedHashMap<String, Object> results = new LinkedHashMap<>();
        NodeList children = element.getChildNodes();
        for (int x = 0, size = children.getLength(); x < size; x++) {
            Node child = children.item(x);
            if (child.getChildNodes().getLength() > 1) {
                results.put(child.getLocalName(), unmarshallNestedMap((Element)child));
            } else if (child.getChildNodes().getLength() == 1) {
                results.put(child.getLocalName(), child.getTextContent());
            } else {
                results.put(child.getLocalName(), "");
            }
        }
        return results;
    }

    private PropertyKind determinePropertyKind(Element element) {
        if (element.getNamespaceURI().equals(Namespaces.TOSCA_YAML_NS)) {
            return PropertyKind.YAML;
        }
        return isKeyValuePropertyDefinition(element) ? PropertyKind.KV : PropertyKind.XML;
    }

    @Override
    public Object marshal(TEntityTemplate.Properties jaxb) throws Exception {
        if (jaxb instanceof TEntityTemplate.WineryKVProperties) {
            return marshallWineryKV((TEntityTemplate.WineryKVProperties) jaxb);
        }
        if (jaxb instanceof TEntityTemplate.XmlProperties) {
            // assume XmlProperties are correctly stored as xml
            Object any = ((TEntityTemplate.XmlProperties) jaxb).getAny();
            if (!(any instanceof Element)) {
                LOGGER.error("XmlProperties did not contain an Xml Element as any. Aborting serialization");
                return null;
            }
            return any;
        }
        if (jaxb instanceof TEntityTemplate.YamlProperties) {
            LinkedHashMap<String, Object> data = ((TEntityTemplate.YamlProperties) jaxb).getProperties();
            return marshallNestedMap(data);
        }
        throw new IllegalStateException("Encountered Unknown Property Subclass during Serialization");
    }

    private Element marshallNestedMap(LinkedHashMap<String, Object> data) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
//            LOGGER.debug(e.getMessage(), e);
            throw new IllegalStateException("Could not instantiate document builder", e);
        }
        Document doc = db.newDocument();

        Element root = doc.createElementNS(Namespaces.TOSCA_YAML_NS, "Properties");
        for (String key : data.keySet()) {
            final Object value = data.get(key);
            if (value instanceof Map) {
                root.appendChild(marshallNestedMap(doc, key, (Map<String, Object>)value));
            } else if (value instanceof String) {
                Element entry = doc.createElement(key);
                entry.appendChild(doc.createTextNode((String) value));
            } else {
                LOGGER.warn("Could not serialize value of type {}. Skipping!", value.getClass().getName());
            }
        }
        return root;
    }

    private Element marshallNestedMap(Document doc, String elementName, Map<String, Object> data) {
        Element container = doc.createElement(elementName);
        for (String key : data.keySet()) {
            final Object value = data.get(key);
            if (value instanceof Map) {
                container.appendChild(marshallNestedMap(doc, key, (Map<String, Object>)value));
            } else if (value instanceof String) {
                Element entry = doc.createElement(key);
                entry.appendChild(doc.createTextNode((String) value));
            } else {
                LOGGER.warn("Could not serialize value of type {}. Skipping!", value.getClass().getName());
            }
        }
        return container;
    }

    private Element marshallWineryKV(TEntityTemplate.WineryKVProperties jaxb) {
        TEntityTemplate.WineryKVProperties wkvProps = jaxb;
        final String namespace = wkvProps.getNamespace();
        String elementName = wkvProps.getElementName();
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
        final LinkedHashMap<String, String> properties = wkvProps.getKVProperties();
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

    public static boolean isKeyValuePropertyDefinition(Element xmlElement) {
        final NodeList xmlChildren = xmlElement.getChildNodes();
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
}
