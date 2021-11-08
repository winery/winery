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
package org.eclipse.winery.model.ids.elements;

import org.eclipse.winery.model.ids.GenericId;
import org.eclipse.winery.model.ids.XmlId;

/**
 * Models an ID of a TOSCA element, which is NOT a DefinitionsChildId
 * <p>
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
