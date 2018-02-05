/*******************************************************************************
 * Copyright (c) 2012-2013 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/
package org.eclipse.winery.repository.rest.datatypes;

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
