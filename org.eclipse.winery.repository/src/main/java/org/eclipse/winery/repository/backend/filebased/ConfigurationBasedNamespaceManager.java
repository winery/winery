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
package org.eclipse.winery.repository.backend.filebased;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import org.eclipse.winery.model.tosca.constants.Namespaces;
import org.eclipse.winery.repository.backend.NamespaceManager;

import org.apache.commons.configuration.Configuration;

public class ConfigurationBasedNamespaceManager implements NamespaceManager {

	private Configuration configuration;

	public ConfigurationBasedNamespaceManager(Configuration configuration) {
		this.configuration = configuration;

		// globally set prefixes
		// if that behavior is not desired, the code has to be moved to "generatePrefix" which checks for existence, ...
		this.configuration.setProperty("http://www.w3.org/2001/XMLSchema", "xsd");
		this.configuration.setProperty("http://www.w3.org/XML/1998/namespace", "xmlns");
		this.configuration.setProperty(Namespaces.TOSCA_NAMESPACE, "tosca");
		this.configuration.setProperty(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "winery");
	}

	@Override
	public String getPrefix(String namespace) {
		Objects.requireNonNull(namespace);
		String prefix = configuration.getString(namespace);
		if (prefix == null) {
			prefix = this.generatePrefix(namespace);
			this.configuration.setProperty(namespace, prefix);
		}
		return prefix;
	}

	@Override
	public boolean hasPrefix(String namespace) {
		return this.configuration.containsKey(namespace);
	}

	@Override
	public void remove(String namespace) {
		this.configuration.clearProperty(namespace);
	}

	@Override
	public void setPrefix(String namespace, String prefix) {
		if (!this.getAllPrefixes().contains(prefix)) {
			this.configuration.setProperty(namespace, prefix);
		}
	}

	/**
	 * Tries to generate a prefix based on the last part of the URL
	 */
	private String generatePrefixProposal(String namespace, int round) {
		Objects.requireNonNull(namespace);
		String[] split = namespace.split("/");
		if (split.length == 0) {
			return String.format("ns%d", round);
		} else {
			String result;
			result = split[split.length - 1].replaceAll("[^A-Za-z]+", "");
			if (result.isEmpty()) {
				return String.format("ns%d", round);
			} else {
				if (round == 0) {
					return result;
				} else {
					return String.format("%s%d", result, round);
				}
			}
		}
	}

	private String generatePrefix(String namespace) {
		Objects.requireNonNull(namespace);
		String prefix;
		Collection<String> allPrefixes = this.getAllPrefixes();

		int round = 0;
		do {
			prefix = generatePrefixProposal(namespace, round);
			round++;
		} while (allPrefixes.contains(prefix));
		return prefix;
	}

	public Collection<String> getAllPrefixes() {
		Iterator<String> keys = this.configuration.getKeys();
		Set<String> res = new HashSet<>();
		while (keys.hasNext()) {
			String key = keys.next();
			String prefix = this.configuration.getString(key);
			res.add(prefix);
		}
		return res;
	}

	@Override
	public Collection<String> getAllNamespaces() {
		Iterator<String> keys = this.configuration.getKeys();
		Set<String> res = new HashSet<>();
		while (keys.hasNext()) {
			res.add(keys.next());
		}		
		return res;
	}

	@Override
	public void clear() {
		this.configuration.clear();
	}
}
