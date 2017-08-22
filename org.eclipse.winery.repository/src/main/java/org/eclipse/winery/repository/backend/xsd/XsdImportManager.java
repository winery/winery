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
import java.util.Map;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.Namespace;

public interface XsdImportManager {

	List<NamespaceAndDefinedLocalNames> getAllDeclaredElementsLocalNames();

	List<NamespaceAndDefinedLocalNames> getAllDefinedTypesLocalNames();

	/**
	 * Returns a mapping from localnames to XSD files, containing the defined local names for the given namespace
	 *
	 * @param namespace the namespace to search for
	 * @param getTypes  true: get types, false: get elements
	 */
	Map<String, RepositoryFileReference> getMapFromLocalNameToXSD(Namespace namespace, final boolean getTypes);

	/**
	 * Determines the declared local names - either XSD elements or XSD types
	 *
	 * @param namespace the namespace to search for
	 * @param getTypes  true: get types, false: get elements
	 */
	List<String> getAllDefinedLocalNames(Namespace namespace, final boolean getTypes);
}
