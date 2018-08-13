/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources.patternrefinementmodels;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.junit.Test;

public class PatternRefinementModelResourceTest extends AbstractResourceTest {

    @Test
    public void getWholePRM() throws Exception {
        this.setRevisionTo("3cf34e2b569b5e811ffb2920aa172ebebbafc670");
        this.assertGet("patternrefinementmodels/http%253A%252F%252Fplain.winery.opentosca.org%252Ftest%252Fpatternrefinementmodels/pmr_1.0.0-w1-wip1/",
            "patternrefinementmodels/first_patternrefinementmodel.json");
    }
}
