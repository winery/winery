/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
package org.eclipse.winery.repository.rest.resources.imports.xsdimports;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.junit.Test;

public class XSDImportResourceTest extends AbstractResourceTest {

	@Test
	public void getAllDeclaredElementsLocalNames() throws Exception {
		this.setRevisionTo("b827889c9a3e2e2af338190a66f6e61ab284fbf1");
		this.assertGet("imports/http%253A%252F%252Fwww.w3.org%252F2001%252FXMLSchema/https%253A%252F%252Fwww.w3schools.com%252Fxml%252Fschema_example.asp/ShipOrderProperties/alldeclaredelementslocalnames", "imports/w3c_schema_example_all_declared_elements_local_names.json");
	}

	@Test
	public void getAllDefinedTypesLocalNames() throws Exception {
		this.setRevisionTo("b827889c9a3e2e2af338190a66f6e61ab284fbf1");
		this.assertGet("imports/http%253A%252F%252Fwww.w3.org%252F2001%252FXMLSchema/https%253A%252F%252Fwww.w3schools.com%252Fxml%252Fschema_example.asp/ShipOrderProperties/alldefinedtypeslocalnames", "imports/w3c_schema_example_all_defined_types_local_names.json");
	}

}
