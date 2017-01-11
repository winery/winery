/*******************************************************************************
 * Copyright (c) 2012-2016 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *     Lukas Harzenetter, Nicole Keppler - forceDelete for Namespaces
 *******************************************************************************/
package org.eclipse.winery.repository.client;

import org.eclipse.winery.common.interfaces.IWineryRepository;

public interface IWineryRepositoryClient extends IWineryRepository {
	
	/**
	 * Adds an URI to the list of known repositories
	 * 
	 * SIDE EFFECT: If currently no primary repository is defined, the given
	 * repository is used as primary repository
	 * 
	 * @param uri the URI of the repository
	 */
	void addRepository(String uri);
	
	/**
	 * Get the currently defined primary repository
	 */
	String getPrimaryRepository();
	
	/**
	 * Sets the primary repository
	 * 
	 * SIDE EFFECT: If the repository is not known as general repository (via
	 * addRepository), the given repository is added to the list of known
	 * repositories
	 * 
	 * @param uri
	 */
	void setPrimaryRepository(String uri);
	
	/**
	 * Checks whether the primary repository is available to be used. Typically,
	 * this method should return "true". In case of network or server failures,
	 * the result is "false". Note that the availability may change over time
	 * and also a repository might become unavailable during querying it.
	 * 
	 * This method also returns "false" if no primary repository is set. For
	 * instance, this is the case of no repository is registered at the client.
	 * 
	 * @return true if the repository is reachable over network, false otherwise
	 */
	boolean primaryRepositoryAvailable();
}
