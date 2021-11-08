/********************************************************************************
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

package org.eclipse.winery.repository.rest.resources.placementmodels;

import java.lang.reflect.Field;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;
import org.eclipse.winery.repository.splitting.Splitting;

import org.junit.jupiter.api.Test;

public class PlacementModelsResourceTest extends AbstractResourceTest {

    @Test
    public void createDataFlowBasedServiceTemplate1() throws Exception {
        this.setRevisionTo("1f24de8867bf3df5d26b932abf4526c625d8502f");
        this.assertPost("dataflowmodels/", "placement/Placement_Test_DataFlow1.xml");
        this.assertGet("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fplacement%252Fservicetemplates/Placement_Test_DataFlow/xml/",
            "placement/Placement_Test_ServiceTemplate1.xml");
    }

    @Test
    public void placementAndCompletionOfServiceTemplate1() throws Exception {
        this.setRevisionTo("1f24de8867bf3df5d26b932abf4526c625d8502f");
        this.assertPost("dataflowmodels/", "placement/Placement_Test_DataFlow1.xml");

        resetIDCounter();

        this.assertPost("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fplacement%252Fservicetemplates/Placement_Test_DataFlow/topologytemplate/applyplacement");
        this.assertGet("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fplacement%252Fservicetemplates/Placement_Test_DataFlow/xml/",
            "placement/Placement_Test_ServiceTemplate_Completed1.xml");
    }

    @Test
    public void createDataFlowBasedServiceTemplate2() throws Exception {
        this.setRevisionTo("1f24de8867bf3df5d26b932abf4526c625d8502f");
        this.assertPost("dataflowmodels/", "placement/Placement_Test_DataFlow2.xml");
        this.assertGet("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fplacement%252Fservicetemplates/Placement_Test_DataFlow/xml/",
            "placement/Placement_Test_ServiceTemplate2.xml");
    }

    @Test
    public void placementAndCompletionOfServiceTemplate2() throws Exception {
        this.setRevisionTo("1f24de8867bf3df5d26b932abf4526c625d8502f");
        this.assertPost("dataflowmodels/", "placement/Placement_Test_DataFlow2.xml");

        resetIDCounter();

        this.assertPost("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fplacement%252Fservicetemplates/Placement_Test_DataFlow/topologytemplate/applyplacement");
        this.assertGet("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fplacement%252Fservicetemplates/Placement_Test_DataFlow/xml/",
            "placement/Placement_Test_ServiceTemplate_Completed2.xml");
    }

    private void resetIDCounter() throws NoSuchFieldException, IllegalAccessException {
        Field field = Splitting.class.getDeclaredField("newRelationshipIdCounter");
        field.setAccessible(true);
        field.set(null, 100);
        field = Splitting.class.getDeclaredField("IdCounter");
        field.setAccessible(true);
        field.set(null, 1);
    }
}
