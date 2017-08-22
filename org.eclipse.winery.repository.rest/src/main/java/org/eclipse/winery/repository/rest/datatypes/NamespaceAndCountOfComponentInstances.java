/*******************************************************************************
 * Copyright (c) 2016 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Nicole Keppler - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.rest.datatypes;

import org.eclipse.winery.common.ids.Namespace;

public class NamespaceAndCountOfComponentInstances {

	private final Namespace namespace;
	private final Integer count;

	public NamespaceAndCountOfComponentInstances(Namespace namespace, Integer count) {
		this.namespace = namespace;
		this.count = count;
	}

	public Namespace getNamespace() {
		return namespace;
	}

	public Integer getCount() {
		return count;
	}

}
