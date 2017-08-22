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
package org.eclipse.winery.repository.rest.datatypes;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.winery.common.ids.Namespace;

public class NamespaceAndDefinedLocalNamesForAngular {
	
	private final String id;
	private final String text;
	private final List<LocalNameForAngular> localNamesForAngular;

	public NamespaceAndDefinedLocalNamesForAngular(final Namespace namespace, final List<String> localNamesForAngular) {
		Objects.requireNonNull(namespace);
		Objects.requireNonNull(localNamesForAngular);
		this.id = namespace.getEncoded();
		this.text = namespace.getDecoded();
		this.localNamesForAngular = localNamesForAngular.stream().map(localName -> {
			final String id = "{" + namespace.getDecoded() + "}" + localName;
			final String value = localName;
			return new LocalNameForAngular(id, value);
		}).collect(Collectors.toList());
	}

	public String getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	/**
	 * "children" property is used at the UI
	 */
	public List<LocalNameForAngular> getChildren() {
		return localNamesForAngular;
	}
}
