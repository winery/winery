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

public class PatternRefinementModelResourceTest extends AbstractResourceTest {

    @Test
    @Disabled // TODO
    public void getJsonPRM() throws Exception {
        this.setRevisionTo("origin/plain");
        this.assertGet("patternrefinementmodels/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fconcrete%252Fpatternrefinementmodels/myExample_w1-wip1/",
            "patternrefinementmodels/first_patternrefinementmodel.json");
    }

    @Test
    @Disabled // TODO
    public void getXmlPRM() throws Exception {
        this.setRevisionTo("origin/plain");
        this.assertGet("patternrefinementmodels/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fconcrete%252Fpatternrefinementmodels/myExample_w1-wip1/",
            "patternrefinementmodels/first_patternrefinementmodel.xml");
    }

    @Test
    public void getPrmWithPropertyMapping() throws Exception {
        this.setRevisionTo("origin/plain");
        this.assertGet("patternrefinementmodels/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fpatternrefinementmodels/PropertyMappingsTest_w1-wip1/attributemappings",
            "patternrefinementmodels/prmRelationMappings.json");
    }
}
