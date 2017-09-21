/*******************************************************************************
 * Copyright (c) 2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.common.ids.elements;

import org.eclipse.winery.common.ids.GenericId;
import org.eclipse.winery.common.ids.XmlId;

/**
 * Models an ID of a TOSCA element, which is NOT a DefinitionsChildId
 *
 * It has a parent and an xmlId
 */
public abstract class ToscaElementId extends GenericId {

	private final GenericId parent;


	public ToscaElementId(GenericId parent, XmlId xmlId) {
		super(xmlId);
		this.parent = parent;
	}

	@Override
	public GenericId getParent() {
		return this.parent;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ToscaElementId) {
			ToscaElementId otherId = (ToscaElementId) obj;
			// the XML id has to be equal and the parents have to be equal
			return (otherId.getXmlId().equals(this.getXmlId())) && (otherId.getParent().equals(this.getParent()));
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(GenericId o1) {
		if (o1 instanceof ToscaElementId) {
			ToscaElementId o = (ToscaElementId) o1;
			if (this.getParent().equals(o.getParent())) {
				return this.getXmlId().compareTo(o.getXmlId());
			} else {
				return this.getParent().compareTo(o.getParent());
			}
		} else {
			// comparing TOSCAcomponentIDs with non-TOSCAcomponentIDs is not
			// possible
			throw new IllegalStateException();
		}
	}

	@Override
	public int hashCode() {
		return this.getParent().hashCode() ^ this.getXmlId().hashCode();
	}

	@Override
	public String toString() {
		String res;
		res = this.getClass().toString() + " / " + this.getXmlId().getDecoded();
		res += "\n";
		res += "parent: " + this.getParent().toString();
		return res;
	}
}
