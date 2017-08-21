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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.definitions.imports.XSDImportId;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.constants.MediaTypes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;

public class RepositoryBasedXsdImportManager implements XsdImportManager {

	/**
	 * Finds out all imports belonging to the given namespace
	 */
	private Set<XSDImportId> getImportsOfNS(final Namespace namespace) {
		Objects.requireNonNull(namespace);
		
		// implemented using a straight-forward solution: get ALL XSD definitions and filter out the matching ones
		
		Set<XSDImportId> allImports = RepositoryFactory.getRepository().getAllTOSCAComponentIds(XSDImportId.class);
		return allImports.stream().filter(imp -> imp.getNamespace().equals(namespace)).collect(Collectors.toSet());
	}

	// we need "unchecked", because of the parsing of the cache
	@SuppressWarnings("unchecked")
	private List<String> getAllDefinedLocalNames(final XSDImportId id, final boolean getTypes) {
		short type = getTypes ? XSConstants.TYPE_DEFINITION : XSConstants.ELEMENT_DECLARATION;
		RepositoryFileReference ref = this.getXSDFileReference();
		if (ref == null) {
			return Collections.emptySet();
		}
		Date lastUpdate = RepositoryFactory.getRepository().getLastUpdate(ref);

		String cacheFileName = "definedLocalNames " + Integer.toString(type) + ".cache";
		RepositoryFileReference cacheRef = new RepositoryFileReference(this.id, cacheFileName);
		boolean cacheNeedsUpdate = true;
		if (RepositoryFactory.getRepository().exists(cacheRef)) {
			Date lastUpdateCache = RepositoryFactory.getRepository().getLastUpdate(cacheRef);
			if (lastUpdate.compareTo(lastUpdateCache) <= 0) {
				cacheNeedsUpdate = false;
			}
		}

		List<String> result;
		if (cacheNeedsUpdate) {

			XSModel model = this.getXSModel();
			if (model == null) {
				return Collections.emptySet();
			}
			XSNamedMap components = model.getComponents(type);
			//@SuppressWarnings("unchecked")
			int len = components.getLength();
			result = new ArrayList<>(len);
			for (int i = 0; i < len; i++) {
				XSObject item = components.item(i);
				// if queried for TYPE_DEFINITION, then XSD base types (such as IDREF) are also returned
				// We want to return only types defined in the namespace of this resource
				if (item.getNamespace().equals(this.id.getNamespace().getDecoded())) {
					result.add(item.getName());
				}
			}

			String cacheContent = null;
			try {
				cacheContent = BackendUtils.mapper.writeValueAsString(result);
			} catch (JsonProcessingException e) {
				XSDImportResource.LOGGER.error("Could not generate cache content", e);
			}
			try {
				RepositoryFactory.getRepository().putContentToFile(cacheRef, cacheContent, MediaTypes.MEDIATYPE_APPLICATION_JSON);
			} catch (IOException e) {
				XSDImportResource.LOGGER.error("Could not update cache", e);
			}
		} else {
			// read content from cache
			// cache should contain most recent information
			try (InputStream is = RepositoryFactory.getRepository().newInputStream(cacheRef)) {
				result = BackendUtils.mapper.readValue(is, java.util.List.class);
			} catch (IOException e) {
				XSDImportResource.LOGGER.error("Could not read from cache", e);
				result = Collections.emptyList();
			}
		}
		return result;
	}


	@Override
	public List<String> getAllDeclaredElementLocalNames(final Namespace namespace, final boolean getTypes) {
		return this.getImportsOfNS(namespace)
				.stream()
				.flatMap(xsdImportId -> this.getAllDeclaredElementLocalNames(namespace, getTypes).stream())
				.sorted()
				.collect(Collectors.toList());
	}
	
	@Override
	public Map<String, RepositoryFileReference> getMapFromLocalNameToXSD(final Namespace namespace, final boolean getTypes) {
		Set<XSDImportId> importsOfNS = this.getImportsOfNS(namespace);
		Map<String, RepositoryFileReference> result = new HashMap<>();
		for (XSDImportId imp : importsOfNS) {
			
			XSDImportResource res = new XSDImportResource(imp);
			Collection<String> col;
			if (getTypes) {
				col = res.getAllDefinedTypesLocalNames();
			} else {
				col = res.getAllDefinedElementsLocalNames();
			}
			RepositoryFileReference ref = res.getXSDFileReference();
			for (String localName : col) {
				result.put(localName, ref);
			}
		}
		return result;
	}

	private Collection<String> getAllDefinedLocalNames(short type) {
		
	}

	/**
	 * @param type as XSConstants - either XSConstants.ELEMENT_DECLARATION or XSConstants.TYPE_DEFINITION
	 */
	private List<NamespaceAndDefinedLocalNames> getAllXsdDefinitions(short type) {
		SortedSet<XSDImportId> allImports = RepositoryFactory.getRepository().getAllTOSCAComponentIds(XSDImportId.class);

		allImports.stream().flatMap()


		Map<Namespace, Collection<String>> data = new HashMap<>();

		for (XSDImportId id : allImports) {
			XSDImportResource resource = new XSDImportResource(id);
			Collection<String> allLocalNames = resource.getAllDefinedLocalNames(type);

			Collection<String> list;
			if ((list = data.get(id.getNamespace())) == null) {
				// list does not yet exist
				list = new ArrayList<>();
				data.put(id.getNamespace(), list);
			}
			list.addAll(allLocalNames);
		}

		ArrayNode rootNode = RestUtils.mapper.createArrayNode();

		// ensure ordering in JSON object
		Collection<Namespace> allns = new TreeSet<>();
		allns.addAll(data.keySet());

		for (Namespace ns : allns) {
			Collection<String> localNames = data.get(ns);
			if (!localNames.isEmpty()) {
				ObjectNode groupEntry = RestUtils.mapper.createObjectNode();
				rootNode.add(groupEntry);
				groupEntry.put("id", ns.getEncoded());
				groupEntry.put("text", ns.getDecoded());
				ArrayNode children = RestUtils.mapper.createArrayNode();
				groupEntry.put("children", children);
				Collection<String> sortedLocalNames = new TreeSet<>();
				sortedLocalNames.addAll(localNames);
				for (String localName : sortedLocalNames) {
					String value = "{" + ns.getDecoded() + "}" + localName;
					//noinspection UnnecessaryLocalVariable
					String text = localName;
					ObjectNode o = RestUtils.mapper.createObjectNode();
					o.put("text", text);
					o.put("id", value);
					children.add(o);
				}
			}
		}

		return rootNode;
	}

	@Override
	public List<NamespaceAndDefinedLocalNames> getAllDeclaredElementsLocalNames() {
		return this.getAllXsdDefinitions(XSConstants.ELEMENT_DECLARATION);
	}

	@Override
	public List<NamespaceAndDefinedLocalNames> getAllDefinedTypesLocalNames() {
		return this.getAllXsdDefinitions(XSConstants.TYPE_DEFINITION);
	}
}
