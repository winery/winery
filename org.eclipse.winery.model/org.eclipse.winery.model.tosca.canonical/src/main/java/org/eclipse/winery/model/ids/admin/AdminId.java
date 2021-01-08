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
package org.eclipse.winery.model.ids.admin;

import org.eclipse.winery.model.ids.GenericId;
import org.eclipse.winery.model.ids.XmlId;

/**
 * The Id for the single admin resource holding administrative things such as
 * the prefixes of namespaces
 */
public abstract class AdminId extends GenericId {

    protected AdminId(XmlId xmlId) {
        super(xmlId);
    }

    @Override
    public int compareTo(GenericId o) {
        if (o instanceof AdminId) {
            return this.getXmlId().compareTo(o.getXmlId());
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public GenericId getParent() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AdminId) {
            return this.getXmlId().equals(((AdminId) obj).getXmlId());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.getXmlId().hashCode();
    }

}
