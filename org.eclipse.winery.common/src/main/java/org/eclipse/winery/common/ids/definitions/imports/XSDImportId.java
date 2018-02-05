/*******************************************************************************
 * Copyright (c) 2013 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.common.ids.definitions.imports;

import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.XmlId;

import javax.xml.XMLConstants;

/**
 * Models an import of type XML Schema Definition
 * <p>
 * Required for a special treatment in {@link org.eclipse.winery.repository.Utils#getAllXSDefinitionsForTypeAheadSelection(short)}
 */
public class XSDImportId extends GenericImportId {

    public XSDImportId(String ns, String id, boolean encoded) {
        super(ns, id, encoded, XMLConstants.W3C_XML_SCHEMA_NS_URI);
    }

    public XSDImportId(Namespace ns, XmlId id) {
        super(ns, id, XMLConstants.W3C_XML_SCHEMA_NS_URI);
    }

    @Override
    public String getGroup() {
        return "XSDImports";
    }
}
