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
package org.eclipse.winery.repository.rest.resources.servicetemplates.selfserviceportal;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;
import org.junit.jupiter.api.Test;

public class XMLTest extends AbstractResourceTest {

    @Test
    public void getXML() throws Exception {
        this.setRevisionTo("3fe0df76e98d46ead68295920e5d1cf1354bdea1");
        this.assertGet("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/selfserviceportal/xml", "entitytypes/servicetemplates/selfserviceportal/getXML.xml");
    }

    @Test
    public void putXML() throws Exception {
        this.setRevisionTo("3fe0df76e98d46ead68295920e5d1cf1354bdea1");
        this.assertPut("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/selfserviceportal/", "entitytypes/servicetemplates/selfserviceportal/putXML.xml");
        this.assertGet("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/selfserviceportal/xml", "entitytypes/servicetemplates/selfserviceportal/getAfterPut.xml");
    }
}
