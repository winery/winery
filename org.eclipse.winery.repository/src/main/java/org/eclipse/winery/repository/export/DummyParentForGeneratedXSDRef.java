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
package org.eclipse.winery.repository.export;

import org.eclipse.winery.common.ids.GenericId;
import org.eclipse.winery.common.ids.XmlId;

public class DummyParentForGeneratedXSDRef extends GenericId {

    protected DummyParentForGeneratedXSDRef() {
        super(new XmlId("dummy", false));
    }

    @Override
    public int compareTo(GenericId o) {
        throw new IllegalStateException("Should never be called.");
    }

    @Override
    public GenericId getParent() {
        throw new IllegalStateException("Should never be called.");
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof DummyParentForGeneratedXSDRef);
    }

    @Override
    public int hashCode() {
        return 0;
    }

}
