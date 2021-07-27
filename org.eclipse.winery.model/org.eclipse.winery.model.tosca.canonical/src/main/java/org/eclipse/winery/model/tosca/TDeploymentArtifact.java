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

package org.eclipse.winery.model.tosca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tDeploymentArtifact")
public class TDeploymentArtifact extends TDeploymentOrImplementationArtifact {

    @Deprecated // used for XML deserialization of API request content
    public TDeploymentArtifact() {
    }

    public TDeploymentArtifact(Builder builder) {
        super(builder);
    }

    public void setArtifactRef(@Nullable QName value) {
        this.artifactRef = value;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static class Builder extends TDeploymentOrImplementationArtifact.Builder<Builder> {

        public Builder(String name, QName artifactType) {
            super(artifactType);
            this.name = name;
        }

        @Override
        public Builder self() {
            return this;
        }

        public TDeploymentArtifact build() {
            return new TDeploymentArtifact(this);
        }
    }
}
