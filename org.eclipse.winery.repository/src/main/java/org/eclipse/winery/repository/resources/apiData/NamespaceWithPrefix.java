/*******************************************************************************
 * Copyright (c) 2012-2016 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier - initial API and implementation
 *
 *******************************************************************************/

package org.eclipse.winery.repository.resources.apiData;

import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.repository.resources.admin.NamespacesResource;

public class NamespaceWithPrefix implements Comparable<NamespaceWithPrefix> {

	public String prefix = "";
	public String namespace = "";

	public NamespaceWithPrefix() {
	}

	public NamespaceWithPrefix(Namespace ns) {
		this.namespace = ns.getDecoded();
		this.prefix = NamespacesResource.getPrefix(ns);
	}

	@Override
	public int compareTo(NamespaceWithPrefix o) {
		return this.namespace.compareTo(o.namespace);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final NamespaceWithPrefix other = (NamespaceWithPrefix) obj;
		if (this.prefix == null ? other.prefix != null : !this.prefix.equals(other.prefix)) {
			return false;
		}
		if (this.namespace == null ? other.namespace != null : !this.namespace.equals(other.namespace)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 59 * hash + (this.namespace != null ? this.namespace.hashCode() : 0);
		hash = 59 * hash + (this.prefix != null ? this.prefix.hashCode() : 0);
		return hash;
	}
}
