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

package org.eclipse.winery.repository.rest.resources;

import org.junit.jupiter.api.Test;

public class InheritanceResourceTest extends AbstractResourceTest {

    @Test
    public void addInheritanceToNodeType() throws Exception {
        this.setRevisionTo("2fd9edf31bc0d7a1118fa19eb7050922d0653cb0");
        this.assertPut("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/grape/inheritance/", "entitytypes/nodetypes/grape_add_inheritance.json");
        this.assertGet("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/grape/inheritance/", "entitytypes/nodetypes/grape_add_inheritance.json");
        // Also assure that the XML is still valid
        this.assertGet("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/grape/", "entitytypes/nodetypes/grape_inheritance.xml");
    }

    @Test
    public void xmlStillValidAfterAddingDerivedFromNoneInheritanceToOneNodeType() throws Exception {
        this.setRevisionTo("2fd9edf31bc0d7a1118fa19eb7050922d0653cb0");
        this.assertPut("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/grape/inheritance/", "entitytypes/nodetypes/grape_add_none_inheritance.json");
        this.assertGet("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/grape/", "entitytypes/nodetypes/grape_inheritance_none.xml");
    }

    @Test
    public void addInheritanceToPolicyType() throws Exception {
        this.setRevisionTo("96a908b37fd3ee190d6371eff4112455eb0097fb");
        this.assertPut("policytypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fpolicytypes%252Ffruits/european/inheritance/", "entitytypes/policytypes/european_add_inheritance.json");
        this.assertGet("policytypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fpolicytypes%252Ffruits/european/inheritance/", "entitytypes/policytypes/european_add_inheritance.json");
        // Also assure that the XML is still valid
        this.assertGet("policytypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fpolicytypes%252Ffruits/european/", "entitytypes/policytypes/european_inheritance.xml");
    }

    @Test
    public void xmlStillValidAfterAddingDerivedFromNoneInheritanceToOnePolicyType() throws Exception {
        this.setRevisionTo("96a908b37fd3ee190d6371eff4112455eb0097fb");
        this.assertPut("policytypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fpolicytypes%252Ffruits/european/inheritance/", "entitytypes/policytypes/organic_add_none_inheritance.json");
        this.assertGet("policytypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fpolicytypes%252Ffruits/european/", "entitytypes/policytypes/organic_inheritance_none.xml");
    }
}
