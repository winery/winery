/*******************************************************************************
 * Copyright (c) 2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.rest.runtimeintegration;

import org.eclipse.winery.repository.rest.RestUtils;

public class OpenTOSCAContainerConnection {

	/**
	 * Determines whether the OpenTOSCA container is locally available
	 *
	 * We currently check for localhost only as Winery currently does not allow
	 * for configuring an URL for the container
	 */
	public static boolean isContainerLocallyAvailable() {
		// the string determining the location of the OpenTOSCA container admin resource
		String adminPath = "http://localhost:8080/admin/";
		return RestUtils.isResourceAvailable(adminPath);
	}

}
