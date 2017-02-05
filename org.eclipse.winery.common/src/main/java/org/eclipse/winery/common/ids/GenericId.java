/*******************************************************************************
 * Copyright (c) 2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.common.ids;

import org.eclipse.winery.common.Util;

/**
 * Superclass for all IDs appearing in Winery. These are:
 * <ul>
 * <li>All IDs of elements directly nested in a Definitions element</li>
 * <li>Subelements of those</li>
 * </ul>
 *
 * We assume that TOSCAcomponentId is always the root node of nested IDs
 *
 */
public abstract class GenericId implements Comparable<GenericId> {

	private final XMLId xmlId;


	protected GenericId(XMLId xmlId) {
		this.xmlId = xmlId;
	}

	/**
	 * @return null if (this instanceof TOSCAcomponentId). In that case, the
	 *         element is already the root element
	 */
	public abstract GenericId getParent();

	/**
	 * @return the XML id of this thing
	 */
	public XMLId getXmlId() {
		return this.xmlId;
	}

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract int hashCode();

	@Override
	public String toString() {
		String idName = Util.getEverythingBetweenTheLastDotAndBeforeId(this.getClass());
		return idName + " / " + this.getXmlId().toString();
	}
}
