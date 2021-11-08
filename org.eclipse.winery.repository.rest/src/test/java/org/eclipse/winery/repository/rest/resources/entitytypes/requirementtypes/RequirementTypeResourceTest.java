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

package org.eclipse.winery.repository.rest.resources.entitytypes.requirementtypes;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;
import org.junit.jupiter.api.Test;

public class RequirementTypeResourceTest extends AbstractResourceTest {
    private static final String FOLDERPATH = "http%3A%2F%2Fwinery.opentosca.org%2Ftest%2Frequirementtypes%2Ffruits/MaximumWeight";
    private static final String ENTITY_TYPE = "requirementtypes/";
    private static final String INSTANCE_XML_PATH = "entitytypes/" + ENTITY_TYPE + "fruits-at-c25aa724201824fce6eddcc7c35a666c6e015880.xml";
    private static final String BAOBAB_JSON_PATH = "entitytypes/" + ENTITY_TYPE + "list-at-c25aa724201824fce6eddcc7c35a666c6e015880.json";
    private final String INSTANCE_URL = ENTITY_TYPE + FOLDERPATH;

    @Test
    public void getInstanceXml() throws Exception {
        this.setRevisionTo("c25aa724201824fce6eddcc7c35a666c6e015880");
        this.assertGet(replacePathStringEncoding(INSTANCE_URL), INSTANCE_XML_PATH);
    }

    @Test
    public void getServicetemplate() throws Exception {
        this.setRevisionTo("c25aa724201824fce6eddcc7c35a666c6e015880");
        this.assertGet(ENTITY_TYPE, BAOBAB_JSON_PATH);
    }

    @Test
    public void getRequiredCapabilityTypeList() throws Exception {
        this.setRevisionTo("e889d1e0fdde49e23d91a7aaacffa180f57953f5");
        this.assertGet("requirementtypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Frequirementtypes%252Ffruits/MinimumWeight/requiredcapabilitytype/", "entitytypes/requirementtypes/requiredCapabilityTypeList.json");
    }

    @Test
    public void setRequiredCapabilityTypeList() throws Exception {
        this.setRevisionTo("e889d1e0fdde49e23d91a7aaacffa180f57953f5");
        this.assertPutText("requirementtypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Frequirementtypes%252Ffruits/MinimumWeight/requiredcapabilitytype/", "{http://winery.opentosca.org/test/capabilitytypes/fruits}Healthy");
    }

    @Test
    public void deleteRequiredCapabilityTypeList() throws Exception {
        this.setRevisionTo("e889d1e0fdde49e23d91a7aaacffa180f57953f5");
        this.assertDelete("requirementtypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Frequirementtypes%252Ffruits/MinimumWeight/requiredcapabilitytype/");
    }
}
