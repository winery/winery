/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.model.tosca.xml;

import io.github.adr.embedded.ADR;

import javax.xml.namespace.QName;

public abstract class XRelationshipSourceOrTarget extends XTEntityTemplate {

    @Deprecated // required for XML deserialization
    public XRelationshipSourceOrTarget() {
        super();
    }

    public XRelationshipSourceOrTarget(String id) {
        super(id);
    }

    public XRelationshipSourceOrTarget(Builder builder) {
        super(builder);
    }

    public abstract String getFakeJacksonType();

    @ADR(11)
    public abstract static class Builder<T extends Builder<T>> extends XTEntityTemplate.Builder<T> {
        public Builder(String id, QName type) {
            super(id, type);
        }

        public Builder(XTEntityTemplate entityTemplate) {
            super(entityTemplate);
        }
    }
}
