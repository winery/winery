/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Nicole Keppler - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources.apiData;

import org.eclipse.winery.repository.resources.entitytypes.relationshiptypes.VisualAppearanceResource;

public class RelationshipTypesVisualsApiData {
	public String sourcearrowhead;
	public String targetarrowhead;
	public String dash;
	public String color;
	public String hovercolor;

	public RelationshipTypesVisualsApiData (VisualAppearanceResource visuals) {
		this.sourcearrowhead = visuals.getSourceArrowHead();
		this.targetarrowhead = visuals.getTargetArrowHead();
		this.dash = visuals.getDash();
		this.color = visuals.getColor();
		this.hovercolor = visuals.getHoverColor();
	}

	public RelationshipTypesVisualsApiData() {

	}

	@Override
	public String toString() {
		return "{sourcearrowhead: " + sourcearrowhead + "}";
	}
}
