/********************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.model.tosca.extensions;

import java.util.Objects;

import org.eclipse.winery.model.tosca.HasId;
import org.eclipse.winery.model.tosca.visitor.Visitor;

public class OTParticipant extends HasId {

    private String name;
    private String url;

    public OTParticipant() {
    }

    protected OTParticipant(Builder builder) {
        super(builder);
        this.name = builder.name;
        this.url = builder.url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OTParticipant that = (OTParticipant) o;
        return Objects.equals(name, that.name) &&
            Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, url);
    }

    public static class Builder extends HasId.Builder<Builder> {

        private String name;
        private String url;

        public Builder() {
            super();
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        @Override
        public OTParticipant.Builder self() {
            return this;
        }

        public OTParticipant build() {
            return new OTParticipant(this);
        }
    }
}
