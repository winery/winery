/*******************************************************************************
 * Copyright (c) 2012-2018 Contributors to the Eclipse Foundation
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

import java.util.Date;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.GenericId;

import org.apache.commons.configuration.Configuration;

/**
 * Provides interface to the backend.
 * <p>
 * Currently a file-based backend is implemented. In the future, a git-based or a database-based backend is possible.
 * <p>
 * The properties are managed by org.apache.commons.configuration. In case a new backend is added, the appropriate
 * implementation of {@link org.apache.commons.configuration.AbstractConfiguration} has to be chosen.
 */
public interface IRepository extends IGenericRepository {

    /**
     * Returns the configuration of the specified id
     * <p>
     * If the associated TOSCA element does not exist, an empty configuration is returned. That means, the associated
     * TOSCA element is created (SIDE EFFECT)
     * <p>
     * The returned configuration ensures that autoSave is activated
     *
     * @param id may be a reference to a TOSCAcomponent or to a nested TOSCAelement
     * @return a Configuration, where isAutoSave == true
     */
    Configuration getConfiguration(GenericId id);

    /**
     * Enables resources to define additional properties. Currently used for tags.
     * <p>
     * Currently, more a quick hack. A generic TagsManager should be introduced to enable auto completion of tag names
     * <p>
     * If the associated TOSCA element does not exist, an empty configuration is returned. That means, the associated
     * TOSCA element is created (SIDE EFFECT)
     */
    Configuration getConfiguration(RepositoryFileReference ref);

    /**
     * @return the last change date of the configuration belonging to the given id. NULL if the associated TOSCA element
     * does not exist.
     */
    Date getConfigurationLastUpdate(GenericId id);
}
