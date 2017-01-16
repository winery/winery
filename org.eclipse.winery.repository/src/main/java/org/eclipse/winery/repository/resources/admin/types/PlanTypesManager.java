/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources.admin.types;

import org.eclipse.winery.repository.datatypes.ids.admin.PlanTypesId;

public class PlanTypesManager extends AbstractTypesManager {

	public final static PlanTypesManager INSTANCE = new PlanTypesManager();


	private PlanTypesManager() {
		super(new PlanTypesId());
		// add data without rendering in the plan types file
		this.addData(org.eclipse.winery.repository.Constants.TOSCA_PLANTYPE_BUILD_PLAN, "Build Plan");
		this.addData(org.eclipse.winery.repository.Constants.TOSCA_PLANTYPE_TERMINATION_PLAN, "Termination Plan");
	}

}
