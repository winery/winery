/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation; moved from Prefs.java
 *******************************************************************************/
package org.eclipse.winery.repository.configuration;

public class OpenToscaConfiguration {

	/**
	 * @return true if the plan generator is available
	 *
	public boolean isPlanBuilderAvailable() {
		// similar implementation as isContainerLocallyAvailable()
		if (this.isPlanBuilderAvailable == null) {
			String planBuilderURI = "http://localhost:1339/planbuilder";
			this.isPlanBuilderAvailable = Utils.isResourceAvailable(planBuilderURI);
		}
		if (!this.isPlanBuilderAvailable) {
			String containerPlanBuilderURI = "http://localhost:1337/containerapi/planbuilder";
			this.isPlanBuilderAvailable = Utils.isResourceAvailable(containerPlanBuilderURI);
		}

		return this.isPlanBuilderAvailable;
	}
    */
	
		/*
	 * @return true iff the OpenTOSCA container is locally available
	public boolean isContainerLocallyAvailable() {
		if (this.isContainerLocallyAvailable == null) {
			// we initialize the variable at the first read
			// The container and Winery are started simultaneously
			// Therefore, the container might not be available if Winery is starting
			// When checking at the first read, chances are high that the container started
			this.isContainerLocallyAvailable = OpenTOSCAContainerConnection.isContainerLocallyAvailable();
		}
		return this.isContainerLocallyAvailable;
	}
	 */



}
