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

package org.eclipse.winery.repository.rest.resources.servicetemplates.plans;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;
import org.junit.jupiter.api.Test;

public class PlansResourceTest extends AbstractResourceTest {

    @Test
    public void getFirstPlan() throws Exception {
        this.setRevisionTo("ae8dfe4e192cc9942df055f9aa075f1cd7445584");
        this.assertGet("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/plans/", "entitytypes/servicetemplates/plans/boabab_initial_plans.json");
    }

    @Test
    public void addPlanWithBpmn4ToscaLanguage() throws Exception {
        this.setRevisionTo("a5fd2da6845e9599138b7c20c1fd9d727c1df66f");
        this.assertPost("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/plans/", "entitytypes/servicetemplates/plans/baobab_add_initial_plan.json");
        this.assertGetSize("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/plans/", 1);
    }

    @Test
    public void deletePlan() throws Exception {
        this.setRevisionTo("ae8dfe4e192cc9942df055f9aa075f1cd7445584");
        this.assertDelete("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/plans/harvest_Plan/");
        this.assertGetSize("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/plans", 0);
    }

    @Test
    public void updatePlan() throws Exception {
        this.setRevisionTo("ae8dfe4e192cc9942df055f9aa075f1cd7445584");
        this.assertPut("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/plans/harvest_Plan/", "entitytypes/servicetemplates/plans/baobab_update_plan.json");
        this.assertGet("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/plans/", "entitytypes/servicetemplates/plans/baobab_updated_plans.json");
    }

    @Test
    public void addFile() throws Exception {
        this.setRevisionTo("3465576f5b46079bb26f5c8e93663424440421a0");
        this.assertUploadBinary("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/plans/harvest_Plan/file", "servicetemplates/plan.zip");
    }
}
