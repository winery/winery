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

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Multimaps;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.definitions.imports.XSDImportId;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.ImportUtils;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.constants.MediaTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class RepositoryBasedXsdImportManager implements XsdImportManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackendUtils.class);

    /**
     * Finds out all imports belonging to the given namespace
     */
    private Set<XSDImportId> getImportsOfNamespace(final Namespace namespace) {
        Objects.requireNonNull(namespace);

        // implemented using a straight-forward solution: get ALL XSD definitions and filter out the matching ones

        Set<XSDImportId> allImports = RepositoryFactory.getRepository().getAllDefinitionsChildIds(XSDImportId.class);
        return allImports.stream().filter(imp -> imp.getNamespace().equals(namespace)).collect(Collectors.toSet());
    }

    private Optional<RepositoryFileReference> getXsdFileReference(final XSDImportId id) {
        final Optional<String> location = ImportUtils.getLocation(id);
        return location.map(l -> new RepositoryFileReference(id, l));
    }

    // we need "unchecked", because of the parsing of the cache
    @SuppressWarnings("unchecked")
    private List<String> getAllDefinedLocalNames(final XSDImportId id, final boolean getTypes) {
        Objects.requireNonNull(id);

        Optional<RepositoryFileReference> ref = this.getXsdFileReference(id);
        if (!ref.isPresent()) {
            return Collections.emptyList();
        }

        short type = getTypes ? XSConstants.TYPE_DEFINITION : XSConstants.ELEMENT_DECLARATION;
        Date lastUpdate = RepositoryFactory.getRepository().getLastUpdate(ref.get());

        @NonNull final String cacheFileName = "definedLocalNames " + Integer.toString(type) + ".cache";
        @NonNull final RepositoryFileReference cacheRef = new RepositoryFileReference(id, cacheFileName);
        boolean cacheNeedsUpdate = true;
        if (RepositoryFactory.getRepository().exists(cacheRef)) {
            Date lastUpdateCache = RepositoryFactory.getRepository().getLastUpdate(cacheRef);
            if (lastUpdate.compareTo(lastUpdateCache) <= 0) {
                cacheNeedsUpdate = false;
            }
        }

        List<String> result;
        if (cacheNeedsUpdate) {
            final Optional<XSModel> model = BackendUtils.getXSModel(ref.get());
            if (!model.isPresent()) {
                return Collections.emptyList();
            }
            XSNamedMap components = model.get().getComponents(type);
            //@SuppressWarnings("unchecked")
            int len = components.getLength();
            result = new ArrayList<>(len);
            for (int i = 0; i < len; i++) {
                XSObject item = components.item(i);
                // if queried for TYPE_DEFINITION, then XSD base types (such as IDREF) are also returned
                // We want to return only types defined in the namespace of this resource
                if (id.getNamespace().getDecoded().equals(item.getNamespace())) {
                    result.add(item.getName());
                }
            }

            String cacheContent = null;
            try {
                cacheContent = BackendUtils.mapper.writeValueAsString(result);
            } catch (JsonProcessingException e) {
                LOGGER.error("Could not generate cache content", e);
            }
            try {
                RepositoryFactory.getRepository().putContentToFile(cacheRef, cacheContent, MediaTypes.MEDIATYPE_APPLICATION_JSON);
            } catch (IOException e) {
                LOGGER.error("Could not update cache", e);
            }
        } else {
            // read content from cache
            // cache should contain most recent information
            try (InputStream is = RepositoryFactory.getRepository().newInputStream(cacheRef)) {
                result = BackendUtils.mapper.readValue(is, java.util.List.class);
            } catch (IOException e) {
                LOGGER.error("Could not read from cache", e);
                result = Collections.emptyList();
            }
        }
        return result;
    }

    @Override
    public List<String> getAllDefinedLocalNames(final Namespace namespace, final boolean getTypes) {
        return this.getImportsOfNamespace(namespace)
            .stream()
            .flatMap(xsdImportId -> this.getAllDefinedLocalNames(xsdImportId, getTypes).stream())
            .sorted()
            .collect(Collectors.toList());
    }

    @Override
    public Map<String, RepositoryFileReference> getMapFromLocalNameToXSD(final Namespace namespace, final boolean getTypes) {
        Set<XSDImportId> importsOfNamespace = this.getImportsOfNamespace(namespace);
        Map<String, RepositoryFileReference> result = new HashMap<>();
        for (XSDImportId imp : importsOfNamespace) {
            final List<String> allDefinedLocalNames = this.getAllDefinedLocalNames(namespace, getTypes);
            Optional<RepositoryFileReference> ref = getXsdFileReference(imp);
            if (!ref.isPresent()) {
                LOGGER.error("Ref is not defined");
            } else {
                for (String localName : allDefinedLocalNames) {
                    result.put(localName, ref.get());
                }
            }
        }
        return result;
    }

    /**
     * @param getType true: XSConstants.TYPE_DEFINITION; false: XSConstants.ELEMENT_DECLARATION
     */
    private List<NamespaceAndDefinedLocalNames> getAllXsdDefinitions(boolean getType) {
        MutableMultimap<Namespace, String> data = Multimaps.mutable.list.empty();

        SortedSet<XSDImportId> allImports = RepositoryFactory.getRepository().getAllDefinitionsChildIds(XSDImportId.class);

        for (XSDImportId id : allImports) {
            final List<String> allDefinedLocalNames = getAllDefinedLocalNames(id, getType);
            data.putAll(id.getNamespace(), allDefinedLocalNames);
        }

        List<NamespaceAndDefinedLocalNames> result = Lists.mutable.empty();
        data.forEachKeyMultiValues((namespace, strings) -> {
            final NamespaceAndDefinedLocalNames namespaceAndDefinedLocalNames = new NamespaceAndDefinedLocalNames(namespace);
            strings.forEach(localName -> namespaceAndDefinedLocalNames.addLocalName(localName));
            result.add(namespaceAndDefinedLocalNames);
        });

        return result;
    }

    @Override
    public List<NamespaceAndDefinedLocalNames> getAllDeclaredElementsLocalNames() {
        return this.getAllXsdDefinitions(false);
    }

    @Override
    public List<NamespaceAndDefinedLocalNames> getAllDefinedTypesLocalNames() {
        return this.getAllXsdDefinitions(true);
    }
}
