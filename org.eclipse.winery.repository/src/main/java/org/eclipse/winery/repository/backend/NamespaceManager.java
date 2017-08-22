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
package org.eclipse.winery.repository.backend;

import java.util.Collection;

import org.eclipse.winery.common.ids.Namespace;

public interface NamespaceManager {

	/**
	 * SIDEFFECT: URI is added to list of known namespaces if it did not exist
	 * before
	 */
	default String getPrefix(Namespace namespace) {
		String ns = namespace.getDecoded();
		return this.getPrefix(ns);
	}

	/**
	 * SIDEFFECT: URI is added to list of known namespaces if it did not exist
	 * before
	 */
	String getPrefix(String namespace);
	
	boolean hasPrefix(String namespace);
	
	void remove(String namespace);

	void setPrefix(String namespace, String prefix);

	Collection<String> getAllPrefixes();
	
	Collection<String> getAllNamespaces();
	
	default void addNamespace(String namespace) {
		this.getPrefix(namespace);
	}

	/**
	 * Removes all namespace mappings
	 */
	void clear();
	
}
