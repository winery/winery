/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.yaml.converter.xml.support;

import java.util.Optional;

import javax.xml.namespace.QName;

import org.eclipse.winery.yaml.common.Namespaces;

public class TypeConverter {
	public static TypeConverter INSTANCE = new TypeConverter();

	public QName convert(QName type) {
		if (type.getNamespaceURI().equals(Namespaces.XML_NS)) {
			switch (type.getLocalPart()) {
				case "string":
					return new QName(Namespaces.YAML_NS, "string", "yaml");
				case "decimal":
				case "float":
					return new QName(Namespaces.YAML_NS, "float", "yaml");
				case "boolean":
					return new QName(Namespaces.YAML_NS, "boolean", "yaml");
				case "date":
					return new QName(Namespaces.YAML_NS, "timestamp", "yaml");
				default:
					return type;
			}
		}
		return type;
	}

	public QName convert(String type) {
		switch (Optional.ofNullable(type).orElse("").toLowerCase()) {
			case "xsd:string":
			case "xs:string":
			case "xsd:anyuri":
			case "xs:anyuri":
				return convert(new QName(Namespaces.XML_NS, "string", "xsd"));
			case "xsd:decimal":
			case "xs:decimal":
				return convert(new QName(Namespaces.XML_NS, "decimal", "xsd"));
			case "xsd:boolean":
			case "xs:boolean":
				return convert(new QName(Namespaces.XML_NS, "boolean", "xsd"));
			case "xsd:date":
			case "xs:date":
				return convert(new QName(Namespaces.XML_NS, "date", "xsd"));
			case "xsd:float":
			case "xs:float":
				return convert(new QName(Namespaces.XML_NS, "float", "xsd"));
			default:
				return new QName(type);
		}
	}
}
