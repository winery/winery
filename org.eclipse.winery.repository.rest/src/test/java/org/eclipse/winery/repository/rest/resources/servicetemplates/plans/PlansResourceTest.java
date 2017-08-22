/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 * Lukas Harzenetter - initial API and implementation
 */

package org.eclipse.winery.repository.rest.resources.servicetemplates.plans;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.junit.Test;

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

}
