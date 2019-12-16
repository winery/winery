/*******************************************************************************
 * Copyright (c) 2012-2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.admin.types;

import org.eclipse.winery.common.Constants;
import org.eclipse.winery.common.ids.admin.PlanTypesId;

public class PlanTypesManager extends AbstractTypesManager {

    public final static PlanTypesManager INSTANCE = new PlanTypesManager();

    private PlanTypesManager() {
        super(new PlanTypesId());
        // add data without rendering in the plan types file
        this.addData(Constants.TOSCA_PLANTYPE_BUILD_PLAN, "Build Plan");
        this.addData(Constants.TOSCA_PLANTYPE_TERMINATION_PLAN, "Termination Plan");
        this.addData(Constants.TOSCA_PLANTYPE_MANAGEMENT_PLAN, "Management Plan");
    }
}
