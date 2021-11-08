/*******************************************************************************
 * Copyright (c) 2012-2013 Contributors to the Eclipse Foundation
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

public class FilesTest extends AbstractResourceTest {

    @Test
    public void addFileTest() throws Exception {
        this.setRevisionTo("770b3b6b86620fac4c8ff1947e84e872e9dcd970");
        this.assertNoContentPost("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fservicetemplates/ServiceTemplateWithFourPolicies/selfserviceportal/files/text.txt",
            "servicetemplates/selfserviceportal/addFirstFile.json");
        this.assertGet("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fservicetemplates/ServiceTemplateWithFourPolicies/selfserviceportal/files",
            "servicetemplates/selfserviceportal/firstFileInList.json");
    }

    @Test
    public void getFileTest() throws Exception {
        this.setRevisionTo("d2fe6f518d732d9f93bcd8e4053c089a4eab4c96");
        this.assertGet("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fservicetemplates/ServiceTemplateWithOneFileInSelfServicePortal/selfserviceportal/files",
            "servicetemplates/selfserviceportal/oneFileInList.json");
    }

    @Test
    public void deleteFileTest() throws Exception {
        this.setRevisionTo("d2fe6f518d732d9f93bcd8e4053c089a4eab4c96");
        this.assertDelete("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fservicetemplates/ServiceTemplateWithOneFileInSelfServicePortal/selfserviceportal/files/myFirstFile.txt?path=");
    }
}
