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
package org.eclipse.winery.repository.rest.resources.servicetemplates.boundarydefinitions.properties;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.junit.jupiter.api.Test;

public class PropertiesResourceTest extends AbstractResourceTest {

    @Test
    public void addProperties() throws Exception {
        this.setRevisionTo("a5fd2da6845e9599138b7c20c1fd9d727c1df66f");
        this.assertPut("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/boundarydefinitions/properties/",
            "entitytypes/servicetemplates/boundarydefinitions/properties/baobab_initial_properties.xml");
        this.assertGet("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/boundarydefinitions/",
            "entitytypes/servicetemplates/boundarydefinitions/properties/baobab_initial_properties_get.json");
    }
}
