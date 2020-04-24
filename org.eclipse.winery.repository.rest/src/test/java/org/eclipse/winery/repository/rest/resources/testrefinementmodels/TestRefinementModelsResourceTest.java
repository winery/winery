/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources.testrefinementmodels;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.junit.jupiter.api.Test;

public class TestRefinementModelsResourceTest extends AbstractResourceTest {

    @Test
    public void createPatternRefinementModel() throws Exception {
        this.setRevisionTo("337119ea2e694e70b994bcb3d97295856c0ab0f6");
        this.assertPost("testrefinementmodels/", "testrefinementmodels/create_testrefinementmodel.json");
        this.assertGet("testrefinementmodels/http%253A%252F%252Fplain.winery.opentosca.org%252Ftest%252Ftestrefinementmodels/MyCoolTRM_w1-wip1/",
            "testrefinementmodels/initial_testrefinementmodel.json");
    }
}
