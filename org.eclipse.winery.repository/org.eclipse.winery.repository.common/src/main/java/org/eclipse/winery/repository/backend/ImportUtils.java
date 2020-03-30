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
package org.eclipse.winery.repository.backend;

import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.model.ids.definitions.imports.GenericImportId;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;

public class ImportUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportUtils.class);

    /**
     * SIDE EFFECT: persists the import when something is changed
     */
    public static Optional<TImport> getTheImport(IRepository repository, GenericImportId id) {
        Objects.requireNonNull(id);

        TImport theImport;
        boolean needsPersistence = false;

        final Definitions definitions = repository.getDefinitions(id);

        if (!repository.exists(id)) {
            return Optional.empty();
        }

        if (definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().isEmpty()) {
            // definitions exist

            // we have to manually assign our import right
            theImport = definitions.getImport().get(0);
        } else {
            // someone created a new import

            // store it locally
            theImport = (TImport) definitions.getElement();

            // undo the side effect of adding it at the wrong place at TDefinitions
            definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().clear();

            // add import at the right place
            definitions.getImport().add(theImport);

            // Super class has persisted the definitions
            // We have to persist the new variant
            needsPersistence = true;
        }

        if (theImport.getLocation() == null) {
            // invalid import -- try to synchronize with storage

            SortedSet<RepositoryFileReference> containedFiles = repository.getContainedFiles(id);
            // there is also a .definitions contained
            // we are only interested in the non-.definitions
            for (RepositoryFileReference ref : containedFiles) {
                if (!ref.getFileName().endsWith(".definitions")) {
                    // associated file found
                    // set the filename of the import to the found xsd
                    // TODO: no more validity checks are done currently. In the case of XSD: targetNamespace matches, not more than one xsd
                    theImport.setLocation(ref.getFileName());
                    needsPersistence = true;
                    break;
                }
            }
        }

        if (needsPersistence) {
            try {
                BackendUtils.persist(repository, id, definitions);
            } catch (IOException e) {
                LOGGER.error("Could not persist changes", e);
            }
        }

        return Optional.of(theImport);
    }

    public static Optional<String> getLocation(IRepository repository, GenericImportId id) {
        return getTheImport(repository, id).map(TImport::getLocation).filter(Objects::nonNull);
    }
}
