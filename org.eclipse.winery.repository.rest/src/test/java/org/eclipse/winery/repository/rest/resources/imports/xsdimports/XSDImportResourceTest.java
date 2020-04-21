/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.imports.xsdimports;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;
import org.junit.jupiter.api.Test;

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
