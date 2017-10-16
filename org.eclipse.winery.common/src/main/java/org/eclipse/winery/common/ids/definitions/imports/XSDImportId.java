/*******************************************************************************
 * Copyright (c) 2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.common.ids.definitions.imports;

import javax.xml.XMLConstants;

import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.XmlId;

/**
 * Models an import of type XML Schema Definition
 *
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
