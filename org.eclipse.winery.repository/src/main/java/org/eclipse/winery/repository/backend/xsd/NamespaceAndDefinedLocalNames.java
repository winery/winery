/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.backend.xsd;

import java.util.List;

import org.eclipse.winery.common.ids.Namespace;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

public class NamespaceAndDefinedLocalNames {

	private final Namespace namespace;

	private final MutableList<String> definedLocalNames;

	public NamespaceAndDefinedLocalNames(Namespace namespace) {
		this.namespace = namespace;
		this.definedLocalNames = Lists.mutable.empty();
	}

	public Namespace getNamespace() {
		return namespace;
	}

	public List<String> getDefinedLocalNames() {
		return this.definedLocalNames.asUnmodifiable();
	}

	public void addLocalName(String localName) {
		this.definedLocalNames.add(localName);
	}
}
