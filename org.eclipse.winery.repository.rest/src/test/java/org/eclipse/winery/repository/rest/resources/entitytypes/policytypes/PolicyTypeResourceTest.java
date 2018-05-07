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

package org.eclipse.winery.repository.rest.resources.entitytypes.policytypes;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;
import org.junit.Test;

public class PolicyTypeResourceTest extends AbstractResourceTest {
    private static final String FOLDERPATH = "http%3A%2F%2Fwinery.opentosca.org%2Ftest%2Fpolicytypes%2Ffruits/european";
    private static final String ENTITY_TYPE = "policytypes/";
    private static final String INSTANCE_XML_PATH = "entitytypes/" + ENTITY_TYPE + "fruits-at-c25aa724201824fce6eddcc7c35a666c6e015880.xml";
    private static final String BAOBAB_JSON_PATH = "entitytypes/" + ENTITY_TYPE + "list-at-a5fd2da6845e9599138b7c20c1fd9d727c1df66f.json";
    private static final String INSTANCE_URL = ENTITY_TYPE + FOLDERPATH;

    @Test
    public void getInstanceXml() throws Exception {
        this.setRevisionTo("origin/fruits");
        this.assertGet(replacePathStringEncoding(INSTANCE_URL), INSTANCE_XML_PATH);
    }

    @Test
    public void getServicetemplate() throws Exception {
        this.setRevisionTo("a5fd2da6845e9599138b7c20c1fd9d727c1df66f");
        this.assertGet(ENTITY_TYPE, BAOBAB_JSON_PATH);
    }

    @Test
    public void getInstancesOfOnePolicyTypeTest() throws Exception {
        this.setRevisionTo("34adf7aba86ff05ce34741bb5c5cb50e468ba7ff");
        this.assertGet("policytypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fpolicytypes%252Ffruits/european/instances/", "entitytypes/policytypes/policyInstances_european.json");
    }

    @Test
    public void getPoliciesGroupedByNamespaceTest() throws Exception {
        this.setRevisionTo("34adf7aba86ff05ce34741bb5c5cb50e468ba7ff");
        this.assertGet("policytypes?grouped=angularSelect", "entitytypes/policytypes/allGroupedByNamespace.json");
    }
}
