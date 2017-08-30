/*******************************************************************************
 * Copyright (c) 2014 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *     Lukas Harzenetter - add id to groups
 *******************************************************************************/
package org.eclipse.winery.repository.rest.datatypes.select2;

import java.util.SortedSet;
import java.util.TreeSet;

public class Select2OptGroup implements Comparable<Select2OptGroup> {

	private final String id;
	private final String text;
	private final SortedSet<Select2DataItem> children;


	public Select2OptGroup(String id, String text) {
		this.text = text;
		this.id = id;
		this.children = new TreeSet<>();
	}

	public String getText() {
		return this.text;
	}

	public String getId() {
		return this.id;
	}

	/**
	 * Returns the internal SortedSet for data items.
	 */
	public SortedSet<Select2DataItem> getChildren() {
		return this.children;
	}

	public void addItem(Select2DataItem item) {
		this.children.add(item);
	}

	/**
	 * Quick hack to test Select2OptGroups for equality. Only the text is
	 * tested, not the contained children. This might cause issues later, but
	 * currently not.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Select2OptGroup)) {
			return false;
		}
		return this.text.equals(((Select2OptGroup) o).text);
	}

	/**
	 * Quick hack to compare Select2OptGroups. Only the text is compared, not
	 * the contained children. This might cause issues later, but currently not.
	 */
	@Override
	public int compareTo(Select2OptGroup o) {
		return this.getText().compareTo(o.getText());
	}
}
