/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Balzer - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.apiData;

import org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes.VisualAppearanceResource;

public class NodeTypesVisualsApiData {
	public String color;

	public NodeTypesVisualsApiData(VisualAppearanceResource visuals) {
		this.color = visuals.getBorderColor();
	}

	public NodeTypesVisualsApiData () {
	}
 }
