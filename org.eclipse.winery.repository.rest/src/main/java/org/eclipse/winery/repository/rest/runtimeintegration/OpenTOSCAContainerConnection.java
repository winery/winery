/********************************************************************************
 * Copyright (c) 2013 Contributors to the Eclipse Foundation
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
