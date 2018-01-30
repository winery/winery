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

import javax.xml.namespace.QName;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.constants.Filename;
import org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes.VisualAppearanceResource;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NodeTypesVisualsApiData {

	public String iconUrl;
	public String imageUrl;
	public String color;
	public QName nodeTypeId;

	public NodeTypesVisualsApiData(VisualAppearanceResource visuals) {
		this.color = visuals.getBorderColor();
		this.nodeTypeId = ((NodeTypeId) visuals.getId().getParent()).getQName();

		RepositoryFileReference iconRef = new RepositoryFileReference(visuals.getId(), Filename.FILENAME_SMALL_ICON);
		if (RepositoryFactory.getRepository().exists(iconRef)) {
			iconUrl = visuals.getAbsoluteURL() + "16x16";
		}

		RepositoryFileReference imageRef = new RepositoryFileReference(visuals.getId(), Filename.FILENAME_BIG_ICON);
		if (RepositoryFactory.getRepository().exists(imageRef)) {
			imageUrl = visuals.getAbsoluteURL() + "50x50";
		}
	}

	public NodeTypesVisualsApiData() {
	}
}
