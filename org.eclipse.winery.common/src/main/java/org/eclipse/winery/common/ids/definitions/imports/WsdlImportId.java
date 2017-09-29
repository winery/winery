/**
 * Copyright (c) 2017 University of Stuttgart. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 and the Apache License 2.0 which both accompany this
 * distribution, and are available at http://www.eclipse.org/legal/epl-v10.html and
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.eclipse.winery.common.ids.definitions.imports;

import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.XmlId;

public class WsdlImportId extends GenericImportId {

	public static final String WSDL_URI = "http://schemas.xmlsoap.org/wsdl/";

	public WsdlImportId(String ns, String id, boolean encoded) {
		super(ns, id, encoded, WSDL_URI);
	}

	public WsdlImportId(Namespace ns, XmlId id) {
		super(ns, id, WSDL_URI);
	}
}
