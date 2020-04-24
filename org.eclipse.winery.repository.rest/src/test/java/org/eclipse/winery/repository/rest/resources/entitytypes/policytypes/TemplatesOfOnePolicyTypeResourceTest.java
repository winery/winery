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
import org.junit.jupiter.api.Test;

public class TemplatesOfOnePolicyTypeResourceTest extends AbstractResourceTest {

    @Test
    public void getTemplatesOfPolicyType() throws Exception {
        this.setRevisionTo("88e5ccd6c35aeffdebc19c8dda9cd76f432538f8");
        this.assertGet("policytypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fpolicytypes%252Ffruits/european/templates/", "entitytypes/policytypes/templates_of_european_policytype.json");
    }
}
