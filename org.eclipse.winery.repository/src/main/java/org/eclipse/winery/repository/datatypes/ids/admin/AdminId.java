/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.datatypes.ids.admin;

import org.eclipse.winery.common.ids.GenericId;
import org.eclipse.winery.common.ids.XMLId;

/**
 * The Id for the single admin resource holding administrative things such as
 * the prefixes of namespaces
 */
public abstract class AdminId extends GenericId {

	protected AdminId(XMLId xmlId) {
		super(xmlId);
	}

	@Override
	public int compareTo(GenericId o) {
		if (o instanceof AdminId) {
			return this.getXmlId().compareTo(o.getXmlId());
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public GenericId getParent() {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AdminId) {
			return this.getXmlId().equals(((AdminId) obj).getXmlId());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.getXmlId().hashCode();
	}

}
