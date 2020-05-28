/********************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.converter.support.xml;

import java.util.Optional;

import javax.xml.namespace.QName;

import org.eclipse.winery.repository.converter.support.Namespaces;

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
