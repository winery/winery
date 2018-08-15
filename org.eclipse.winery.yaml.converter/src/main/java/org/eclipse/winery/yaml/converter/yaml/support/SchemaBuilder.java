/********************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.yaml.converter.yaml.support;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.yaml.TPropertyDefinition;
import org.eclipse.winery.yaml.common.Namespaces;

import org.eclipse.jdt.annotation.NonNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SchemaBuilder {
    private String namespace;
    private boolean notFinished = true;

    private Document document;
    private Element schema;
    private List<Element> elements;
    private List<Element> complexTypes;
    private List<Element> imports;
    private Map<String, String> namespaces;

    private int uniqueNumber;

    public SchemaBuilder(String namespace) {
        reset(namespace);
    }

    public SchemaBuilder reset(String namespace) {
        this.namespace = namespace;

        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        schema = document.createElementNS(Namespaces.XML_NS, "schema");
        schema.setAttribute("attributeFormDefault", "unqualified");
        schema.setAttribute("elementFormDefault", "qualified");
        schema.setAttribute("targetNamespace", this.namespace);

        this.elements = new ArrayList<>();
        this.complexTypes = new ArrayList<>();
        this.imports = new ArrayList<>();
        this.namespaces = new LinkedHashMap<>();

        this.uniqueNumber = 1;

        return this;
    }

    public String getNamespace() {
        return this.namespace;
    }

    @NonNull
    private List<Element> getElements() {
        if (this.elements == null) {
            this.elements = new ArrayList<>();
        }
        return elements;
    }

    public SchemaBuilder setElements(List<Element> elements) {
        assert notFinished;
        this.elements = elements;
        return this;
    }

    public SchemaBuilder addElements(List<Element> elements) {
        assert notFinished;
        if (elements == null) {
            return this;
        }

        if (this.elements == null) {
            this.elements = elements;
        } else {
            this.elements.addAll(elements);
        }
        return this;
    }

    public SchemaBuilder addElements(Element element) {
        assert notFinished;
        if (element == null) {
            return this;
        }

        List<Element> tmp = new ArrayList<>();
        tmp.add(element);
        return addElements(tmp);
    }

    public SchemaBuilder addElements(String key, TPropertyDefinition propertyDefinition) {
        assert notFinished;
        Element element = document.createElement("element");
        element.setAttribute("name", key);
        QName type = TypeConverter.INSTANCE.convert(propertyDefinition.getType());
        String prefix = type.getPrefix();
        if (prefix == null || prefix.isEmpty()) {
            prefix = "pfx" + this.uniqueNumber++;
        }
        if (!this.namespace.equals(type.getNamespaceURI())) {
            this.namespaces.put(prefix, type.getNamespaceURI());
            element.setAttribute("type", prefix + ":" + type.getLocalPart());
        } else {
            element.setAttribute("type", "pfx0:" + type.getLocalPart());
        }
        if (propertyDefinition.getRequired() != null && !propertyDefinition.getRequired()) {
            element.setAttribute("minOccurs", "0");
        }
        // TODO default value
        this.addElements(element);
        return this;
    }

    public SchemaBuilder setImports(List<Element> imports) {
        assert notFinished;
        this.imports = imports;
        return this;
    }

    public SchemaBuilder addImports(List<Element> imports) {
        assert notFinished;
        if (imports == null) {
            return this;
        }

        this.imports.addAll(imports);

        return this;
    }

    public SchemaBuilder addImports(Element _import) {
        assert notFinished;
        if (_import == null) {
            return this;
        }

        List<Element> tmp = new ArrayList<>();
        tmp.add(_import);
        return addImports(tmp);
    }

    public SchemaBuilder addImports(String namespace, String location) {
        assert notFinished;
        Element importDefinition = document.createElement("import");
        importDefinition.setAttribute("namespace", namespace);
        importDefinition.setAttribute("schemaLocation", location);

        return addImports(importDefinition);
    }

    public SchemaBuilder buildComplexType(String name, Boolean wrapped) {
        assert notFinished;
        Element complexType = document.createElement("complexType");
        if (wrapped) {
            Element element = document.createElement("element");
            element.setAttribute("name", name);
            this.complexTypes.add(element);
            element.appendChild(complexType);
        } else {
            complexType.setAttribute("name", name);
            this.complexTypes.add(complexType);
        }

        Element sequence = document.createElement("sequence");
        sequence.setAttribute("xmlns:pfx0", namespace);
        sequence.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
        complexType.appendChild(sequence);

        this.getElements().forEach(sequence::appendChild);
        this.elements.clear();
        return this;
    }

    public SchemaBuilder finish() {
        if (notFinished) {
            for (Map.Entry<String, String> entry : namespaces.entrySet()) {
                schema.setAttribute("xmlns:" + entry.getKey(), entry.getValue());
            }

            for (Element _import : imports) {
                schema.appendChild(_import);
            }

            for (Element entry : complexTypes) {
                schema.appendChild(entry);
            }

            document.appendChild(schema);
            notFinished = false;
        }
        return this;
    }

    public Document build() {
        if (notFinished) finish();
        return document;
    }
}

