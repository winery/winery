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
package org.eclipse.winery.repository.backend.xsd;

import java.util.List;
import java.util.Map;

import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.model.ids.Namespace;

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
