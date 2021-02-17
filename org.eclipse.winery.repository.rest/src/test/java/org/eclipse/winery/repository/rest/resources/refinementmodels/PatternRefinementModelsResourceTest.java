/********************************************************************************
 * Copyright (c) 2018-2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources.refinementmodels;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class PatternRefinementModelsResourceTest extends AbstractResourceTest {

    @Test
    @Disabled // TODO
    public void createPatternRefinementModel() throws Exception {
        this.setRevisionTo("337119ea2e694e70b994bcb3d97295856c0ab0f6");
        this.assertPost("patternrefinementmodels/", "patternrefinementmodels/create_patternrefinementmodel.json");
        this.assertGet("patternrefinementmodels/http%253A%252F%252Fplain.winery.opentosca.org%252Ftest%252Fpatternrefinementmodels/MyCoolPRM_w1-wip1/",
            "patternrefinementmodels/initial_patternrefinementmodel.json");
    }

    @Test
    @Disabled // TODO
    public void createPRM() throws Exception {
        this.setRevisionTo("3de3025c9b96ec639b3c2a45eea8bf19fe017b56");
        this.assertPost("patternrefinementmodels/", "patternrefinementmodels/create_patternrefinementmodel.json");
        this.assertGet("patternrefinementmodels/http%253A%252F%252Fplain.winery.opentosca.org%252Ftest%252Fpatternrefinementmodels/MyCoolPRM_w1-wip1/",
            "patternrefinementmodels/created_patternrefinementmodel.xml");
    }
}
