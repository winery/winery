/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *     Philipp Meyer - support for source directory
 *******************************************************************************/
package org.eclipse.winery.repository.datatypes.ids.elements;

import org.eclipse.winery.common.ids.GenericId;
import org.eclipse.winery.common.ids.XmlId;
import org.eclipse.winery.common.ids.elements.ToscaElementId;

public abstract class DirectoryId extends ToscaElementId {

	public DirectoryId(GenericId parent, String directoryId) {
		super(parent, new XmlId(directoryId, false));
	}

	public DirectoryId(GenericId parent, String directoryId, boolean URLencoded) {
		super(parent, new XmlId(directoryId, URLencoded));
	}
}
