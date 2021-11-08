/*******************************************************************************
 * Copyright (c) 2013 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.model.ids;

import java.util.Objects;

/**
 * Superclass for all IDs appearing in Winery. These are:
 * <ul>
 * <li>All IDs of elements directly nested in a Definitions element</li>
 * <li>Subelements of those</li>
 * </ul>
 * <p>
 * We assume that DefinitionsChildId is always the root node of nested IDs
 */
public abstract class GenericId implements Comparable<GenericId> {

    private final XmlId xmlId;


    protected GenericId(XmlId xmlId) {
        this.xmlId = Objects.requireNonNull(xmlId);
    }

    /**
     * @return null if (this instanceof DefinitionsChildId). In that case, the
     * element is already the root element
     */
    public abstract GenericId getParent();

    /**
     * @return the XML id of this thing
     */
    public XmlId getXmlId() {
        return this.xmlId;
    }

    @Override
    public String toString() {
        return "GenericId{" +
            "id=" + IdUtil.getEverythingBetweenTheLastDotAndBeforeId(this.getClass()) +
            "xmlId=" + xmlId +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GenericId)) return false;

        GenericId genericId = (GenericId) o;

        return xmlId.equals(genericId.xmlId);
    }

    @Override
    public int hashCode() {
        return xmlId.hashCode();
    }
}
