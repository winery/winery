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
package org.eclipse.winery.repository.datatypes;

public class TypeWithShortName implements Comparable<TypeWithShortName> {

	private final String type;
	// we could have used "URI" as type here but this seems to be unnecessary
	// overhead

	// this is a kind of ID
	private final String shortName;


	public TypeWithShortName(String type, String shortName) {
		this.type = type;
		this.shortName = shortName;
	}

	public String getType() {
		return this.type;
	}

	public String getShortName() {
		return this.shortName;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof TypeWithShortName) {
			return ((TypeWithShortName) o).getType().equals(this.getType());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.getType().hashCode();
	}

	@Override
	public int compareTo(TypeWithShortName o) {
		int c = this.getShortName().compareTo(o.getShortName());
		if (c == 0) {
			// not sure if this will ever happen
			c = this.getType().compareTo(o.getType());
		}
		return c;
	}
}
