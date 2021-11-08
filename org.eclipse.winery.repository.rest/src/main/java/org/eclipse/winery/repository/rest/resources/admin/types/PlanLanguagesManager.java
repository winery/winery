/*******************************************************************************
 * Copyright (c) 2012-2013 Contributors to the Eclipse Foundation
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

import org.eclipse.winery.model.ids.admin.PlanLanguagesId;
import org.eclipse.winery.model.tosca.constants.Namespaces;

public class PlanLanguagesManager extends AbstractTypesManager {

    public final static PlanLanguagesManager INSTANCE = new PlanLanguagesManager();


    private PlanLanguagesManager() {
        super(new PlanLanguagesId());
        // add data without rendering in the plan languages file
        this.addData(Namespaces.URI_BPEL20_EXECUTABLE, "BPEL 2.0");
        this.addData(Namespaces.URI_BPMN20_MODEL, "BPMN 2.0");
        this.addData(Namespaces.URI_BPMN4TOSCA_20, "BPMN4TOSCA 2.0");
    }
}
