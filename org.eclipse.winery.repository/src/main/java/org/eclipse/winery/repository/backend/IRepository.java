/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.backend;

import java.util.Date;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.GenericId;

import org.apache.commons.configuration.Configuration;

/**
 * Provides interface to the backend.
 *
 * Currently a file-based backend is implemented. In the future, a git-based or
 * a database-based backend is possible.
 *
 * The properties are managed by org.apache.commons.configuration. In case a new
 * backend is added, the appropriate implementation of
 * org.apache.commons.configuration.AbstrctConfiguration has to be chosen.
 *
 */
public interface IRepository extends IGenericRepository {

	/**
	 * Returns the configuration of the specified id
	 *
	 * If the associated TOSCA element does not exist, an empty configuration is
	 * returned. That means, the associated TOSCA element is created (SIDE
	 * EFFECT)
	 *
	 * The returned configuration ensures that autoSave is activated
	 *
	 * @param id may be a reference to a TOSCAcomponent or to a nested
	 *            TOSCAelement
	 * @return a Configuration, where isAutoSave == true
	 */
	Configuration getConfiguration(GenericId id);

	/**
	 * Enables resources to define additional properties. Currently used for
	 * tags.
	 *
	 * Currently, more a quick hack. A generic TagsManager should be introduced
	 * to enable auto completion of tag names
	 *
	 * If the associated TOSCA element does not exist, an empty configuration is
	 * returned. That means, the associated TOSCA element is created (SIDE
	 * EFFECT)
	 */
	Configuration getConfiguration(RepositoryFileReference ref);

	/**
	 *
	 * @return the last change date of the configuration belonging to the given
	 *         id. NULL if the associated TOSCA element does not exist.
	 */
	Date getConfigurationLastUpdate(GenericId id);

}
