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
package org.eclipse.winery.repository.resources.admin;

import javax.ws.rs.Path;

import org.eclipse.winery.repository.resources.admin.types.ConstraintTypesManager;
import org.eclipse.winery.repository.resources.admin.types.PlanLanguagesManager;
import org.eclipse.winery.repository.resources.admin.types.PlanTypesManager;

public class AdminTopResource {

	@Path("namespaces/")
	public NamespacesResource getNamespacesResource() {
		return NamespacesResource.getInstance();
	}

	@Path("repository/")
	public RepositoryAdminResource getRepositoryAdminResource() {
		return new RepositoryAdminResource();
	}

	@Path("planlanguages/")
	public PlanLanguagesManager getPlanLanguagesResource() {
		return PlanLanguagesManager.INSTANCE;
	}

	@Path("plantypes/")
	public PlanTypesManager getPlanTypesResource() {
		return PlanTypesManager.INSTANCE;
	}

	@Path("constrainttypes/")
	public ConstraintTypesManager getConstraintTypesManager() {
		return ConstraintTypesManager.INSTANCE;
	}
}
