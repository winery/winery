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
package org.eclipse.winery.repository.datatypes.ids.elements;

import org.eclipse.winery.common.ids.XmlId;
import org.eclipse.winery.common.ids.definitions.TopologyGraphElementEntityTypeId;
import org.eclipse.winery.common.ids.elements.ToscaElementId;

/**
 * ID for a pseudo-TOSCA-Element holding the data for the visual appearance
 * (e.g., icons for node types)
 */
public class VisualAppearanceId extends ToscaElementId {

	public VisualAppearanceId(TopologyGraphElementEntityTypeId parent) {
		super(parent, new XmlId("appearance", true));
	}

}
