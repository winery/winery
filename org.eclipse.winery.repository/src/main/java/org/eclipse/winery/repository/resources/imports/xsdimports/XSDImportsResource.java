/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources.imports.xsdimports;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.definitions.imports.XSDImportId;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.resources.AbstractComponentsWithoutTypeReferenceResource;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.restdoc.annotations.RestDoc;

/**
 * Manages all imports of type XML Schema Definition <br />
 * The actual implementation is done in the AbstractComponentsResource
 *
 * FIXME: This class should be generalized to handle ImportId
 */
public class XSDImportsResource extends AbstractComponentsWithoutTypeReferenceResource<XSDImportResource> {

	@Path("{namespace}/")
	@GET
	@RestDoc(methodDescription = "Returns all available local names of defined elements in this namespace")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllElementLocalNames(@PathParam("namespace") String nsString, @QueryParam(value = "elements") String returnElements, @QueryParam(value = "types") String returnTypes) {
		// returnElements is not read as either types or elements may be read
		Set<String> allNCNames = this.getAllElementLocalNamesAsSet(nsString, returnTypes != null);
		try {
			return Utils.mapper.writeValueAsString(allNCNames);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * @param nsString the namesapce as String
	 * @param getTypes true: return ElementTypes, false: return Elements
	 */
	private Set<String> getAllElementLocalNamesAsSet(final String nsString, final boolean getTypes) {
		Set<XSDImportId> importsOfNS = this.getImportsOfNS(nsString);

		// TreeSet enables ordering
		Set<String> allNCNames = new TreeSet<>();

		for (XSDImportId imp : importsOfNS) {
			XSDImportResource res = new XSDImportResource(imp);
			Collection<String> col;
			if (getTypes) {
				col = res.getAllDefinedTypesLocalNames();
			} else {
				col = res.getAllDefinedElementsLocalNames();
			}
			allNCNames.addAll(col);
		}
		return allNCNames;
	}

	/**
	 * Finds out all imports belonging to the given namespace
	 *
	 * @param nsString the namespace to query
	 */
	private Set<XSDImportId> getImportsOfNS(final String nsString) {
		// FIXME: Currently not supported by the repository, therefore, we filter by hand
		Set<XSDImportId> allImports = Repository.INSTANCE.getAllTOSCAComponentIds(XSDImportId.class);
		Namespace ns = new Namespace(nsString, true);
		Set<XSDImportId> importsOfNs = new HashSet<>();
		for (XSDImportId imp : allImports) {
			if (imp.getNamespace().equals(ns)) {
				importsOfNs.add(imp);
			}
		}
		return importsOfNs;
	}

	/**
	 * Returns a mapping from localnames to XSD files, containing the defined
	 * local names for the given namespace
	 */
	public Map<String, RepositoryFileReference> getMapFromLocalNameToXSD(final String nsString, final boolean getTypes) {
		Set<XSDImportId> importsOfNS = this.getImportsOfNS(nsString);
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

}
