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
package org.eclipse.winery.topologymodeler;

import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.winery.common.interfaces.QNameWithName;

public class WineryUtil {

	/**
	 * LocalName is the ID of the element, whereas Name is the speaking name
	 */
	public static class LocalNameNamePair implements Comparable<LocalNameNamePair> {

		String localName;
		String name;


		public LocalNameNamePair(String localName, String name) {
			this.localName = localName;
			this.name = name;
		}

		public String getLocalName() {
			return this.localName;
		}

		public String getName() {
			return this.name;
		}

		/**
		 * Ordering according to name
		 */
		@Override
		public int compareTo(LocalNameNamePair otherPair) {
			return this.name.compareTo(otherPair.name);
		}

		@Override
		public int hashCode() {
			return this.localName.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof LocalNameNamePair) {
				return this.localName.equals(((LocalNameNamePair) o).getLocalName());
			} else {
				return false;
			}
		}
	}

	public static SortedMap<String, SortedSet<LocalNameNamePair>> convertQNameWithNameListToNamespaceToLocalNameNamePairList(List<QNameWithName> list) {
		if (list == null) {
			throw new IllegalArgumentException("list may not be null");
		}
		SortedMap<String, SortedSet<LocalNameNamePair>> res = new TreeMap<>();
		for (QNameWithName qnameWithName : list) {
			SortedSet<LocalNameNamePair> localNameNamePairSet = res.get(qnameWithName.qname.getNamespaceURI());
			if (localNameNamePairSet == null) {
				localNameNamePairSet = new TreeSet<>();
				res.put(qnameWithName.qname.getNamespaceURI(), localNameNamePairSet);
			}
			LocalNameNamePair pair = new LocalNameNamePair(qnameWithName.qname.getLocalPart(), qnameWithName.name);
			localNameNamePairSet.add(pair);
		}
		return res;
	}

}
