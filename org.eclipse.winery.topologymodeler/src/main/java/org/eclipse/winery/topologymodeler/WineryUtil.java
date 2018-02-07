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
package org.eclipse.winery.topologymodeler;

import org.eclipse.winery.common.interfaces.QNameWithName;

import java.util.*;

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
