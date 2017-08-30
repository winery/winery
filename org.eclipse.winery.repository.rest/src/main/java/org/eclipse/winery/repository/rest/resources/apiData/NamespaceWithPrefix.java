/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *   - Niko Stadelmaier - initial API and implementation
 *   - Oliver Kopp - cleanup
 *
 *******************************************************************************/

package org.eclipse.winery.repository.rest.resources.apiData;

import java.util.Objects;

import org.eclipse.winery.common.ids.Namespace;

public class NamespaceWithPrefix implements Comparable<NamespaceWithPrefix> {

	public String prefix = "";
	public String namespace = "";

	public NamespaceWithPrefix(Namespace ns, String prefix) {
		Objects.requireNonNull(ns);
		Objects.requireNonNull(prefix);
		this.namespace = ns.getDecoded();
		this.prefix = prefix;
	}

	@Override
	public int compareTo(NamespaceWithPrefix o) {
		return this.namespace.compareTo(o.namespace);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof NamespaceWithPrefix)) return false;
		NamespaceWithPrefix that = (NamespaceWithPrefix) o;
		return Objects.equals(prefix, that.prefix) &&
				Objects.equals(namespace, that.namespace);
	}

	@Override
	public int hashCode() {
		return Objects.hash(prefix, namespace);
	}
}
