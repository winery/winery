/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
