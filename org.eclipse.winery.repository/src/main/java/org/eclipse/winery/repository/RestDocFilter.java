/*******************************************************************************
 * Copyright (c) 2014 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/

package org.eclipse.winery.repository;

import org.eclipse.winery.repository.resources.MainResource;
import org.restdoc.jersey.server.RestDocFeature;

public class RestDocFilter extends RestDocFeature {

	@Override
	protected Class<?>[] getClasses() {
		return new Class<?>[]{MainResource.class};
	}

}
