/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.tosca.xml.extensions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.xml.visitor.Visitor;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "otPermutationMapping")
public class XOTPermutationMapping extends XOTPrmMapping {

    @Deprecated // required for XML deserialization
    public XOTPermutationMapping() { }

    public XOTPermutationMapping(Builder builder) {
        super(builder);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static class Builder extends XOTPrmMapping.Builder<Builder> {

        public Builder(String id) {
            super(id);
        }

        public XOTPermutationMapping build() {
            return new XOTPermutationMapping(this);
        }

        @Override
        public Builder self() {
            return this;
        }
    }
}
