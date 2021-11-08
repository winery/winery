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

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import io.github.adr.embedded.ADR;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tImplementationArtifact")
public class TImplementationArtifact extends TDeploymentOrImplementationArtifact {

    @XmlAttribute(name = "interfaceName")
    @XmlSchemaType(name = "anyURI")
    protected String interfaceName;
    @XmlAttribute(name = "operationName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String operationName;

    @Deprecated // used for XML deserialization of API request content
    public TImplementationArtifact() {
    }

    public TImplementationArtifact(TImplementationArtifact.Builder builder) {
        super(builder);
        this.interfaceName = builder.interfaceName;
        this.operationName = builder.operationName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TImplementationArtifact)) return false;
        if (!super.equals(o)) return false;
        TImplementationArtifact that = (TImplementationArtifact) o;
        return Objects.equals(name, that.name) &&
            Objects.equals(interfaceName, that.interfaceName) &&
            Objects.equals(operationName, that.operationName) &&
            Objects.equals(artifactType, that.artifactType) &&
            Objects.equals(artifactRef, that.artifactRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, interfaceName, operationName, artifactType, artifactRef);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(@Nullable String value) {
        this.interfaceName = value;
    }

    @Nullable
    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(@Nullable String value) {
        this.operationName = value;
    }

    @NonNull
    public QName getArtifactType() {
        return artifactType;
    }

    public void setArtifactType(@NonNull QName value) {
        Objects.requireNonNull(value);
        this.artifactType = value;
    }

    @Nullable
    public QName getArtifactRef() {
        return artifactRef;
    }

    public void setArtifactRef(@Nullable QName value) {
        this.artifactRef = value;
    }

    public static class Builder extends TDeploymentOrImplementationArtifact.Builder<Builder> {

        private String interfaceName;
        private String operationName;

        public Builder(QName artifactType) {
            super(artifactType);
        }

        public Builder setName(String name) {
            this.name = name;
            return self();
        }

        public Builder setInterfaceName(String interfaceName) {
            this.interfaceName = interfaceName;
            return self();
        }

        public Builder setOperationName(String operationName) {
            this.operationName = operationName;
            return self();
        }

        @ADR(11)
        @Override
        public Builder self() {
            return this;
        }

        public TImplementationArtifact build() {
            return new TImplementationArtifact(this);
        }
    }
}
