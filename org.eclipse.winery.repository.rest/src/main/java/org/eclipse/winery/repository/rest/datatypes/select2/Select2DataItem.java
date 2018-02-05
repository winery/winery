/********************************************************************************
 * Copyright (c) 2014 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.datatypes.select2;

/**
 * Models a data item for select2. In case optgroups have to be returned, use
 * this element in a TreeMap
 */
public class Select2DataItem implements Comparable<Select2DataItem> {

	public String id;
	public String text;

	public Select2DataItem() {

	}

	public Select2DataItem(String id, String text) {
		this.id = id;
		this.text = text;
	}

	public String getId() {
		return this.id;
	}

	public String getText() {
		return this.text;
	}

	/**
	 * Sort order is based on text
	 */
	@Override
	public int compareTo(Select2DataItem o) {
		return this.getText().compareTo(o.getText());
	}

	/**
	 * Equality is checked at id level
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Select2DataItem)) {
			return false;
		}
		return this.getId().equals(((Select2DataItem) o).getId());
	}
}
