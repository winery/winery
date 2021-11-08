/*******************************************************************************
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

package org.eclipse.winery.repository.rest.resources.entitytemplates.policytemplates;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.junit.jupiter.api.Test;

public class PolicyTemplateResourceTest extends AbstractResourceTest {
    
    @Test
    public void getVisualAppearanceData() throws Exception {
        this.setRevisionTo("origin/plain");
        this.assertGet("policytemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fpolicytemplates/PolicyTemplateWithoutProperties/appearance",
            "entitytemplates/policytemplates/visualappearance.json");
    }
    
    @Test
    public void getIcon() throws Exception {
        this.setRevisionTo("4656a0abe19b8720c28273461c84d2ddd09ef868");
        this.assertGet("policytemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fpolicytemplates/PolicyWithIcon_w1-wip1/appearance",
            "entitytemplates/policytemplates/visualappearance_icon.json");
    }
}
