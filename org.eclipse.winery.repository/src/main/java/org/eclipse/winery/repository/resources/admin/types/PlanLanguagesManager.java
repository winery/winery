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

import org.eclipse.winery.model.tosca.Namespaces;
import org.eclipse.winery.repository.datatypes.ids.admin.PlanLanguagesId;

public class PlanLanguagesManager extends AbstractTypesManager {

	public final static PlanLanguagesManager INSTANCE = new PlanLanguagesManager();


	private PlanLanguagesManager() {
		super(new PlanLanguagesId());
		// add data without rendering in the plan languages file
		this.addData(Namespaces.URI_BPEL20_EXECUTABLE, "BPEL 2.0 (executable)");
		this.addData(Namespaces.URI_BPMN20_MODEL, "BPMN 2.0");
		this.addData(Namespaces.URI_BPMN4TOSCA_20, "BPMN4TOSCA 2.0");
	}

}
