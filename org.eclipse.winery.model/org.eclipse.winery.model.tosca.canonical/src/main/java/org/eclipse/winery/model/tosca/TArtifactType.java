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

package org.eclipse.winery.model.tosca;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tArtifactType")
public class TArtifactType extends TEntityType {
    // the following two fields are added to support YAML mode
    @XmlAttribute(name = "mimetype", required = false)
    private String mimeType;
    @XmlAttribute(name = "fileextensions", required = false)
    private List<String> fileExtensions;
    
    public TArtifactType() {
    }

    public TArtifactType(Builder builder) {
        super(builder);
        this.mimeType = builder.mimeType;
        this.fileExtensions = builder.fileExtensions;
    }

    @Nullable
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(@Nullable String mimeType) {
        this.mimeType = mimeType;
    }

    @Nullable
    public List<String> getFileExtensions() {
        return fileExtensions;
    }

    public void setFileExtensions(@Nullable List<String> fileExtensions) {
        this.fileExtensions = fileExtensions;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static class Builder extends TEntityType.Builder<Builder> {
        private String mimeType;
        private List<String> fileExtensions;

        public Builder(String name) {
            super(name);
        }

        public Builder(TEntityType entityType) {
            super(entityType);
        }

        public Builder setMimeType(String mimeType) {
            this.mimeType = mimeType;
            return self();
        }

        public Builder setFileExtensions(List<String> fileExtensions) {
            this.fileExtensions = fileExtensions;
            return self();
        }

        @Override
        public Builder self() {
            return this;
        }

        public TArtifactType build() {
            return new TArtifactType(this);
        }
    }
}
