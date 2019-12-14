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

/**
 * Class to represent an artifact in TOSCA YAML.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tArtifact", propOrder = {
    "description",
    "file",
    "deployPath"
})
public class TArtifact extends TEntityTemplate {

    private String description;
    private String file;
    private String deployPath;

    public TArtifact() {
    }

    public TArtifact(Builder builder) {
        super(builder);
        this.description = builder.description;
        this.file = builder.file;
        this.deployPath = builder.deployPath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getDeployPath() {
        return deployPath;
    }

    public void setDeployPath(String deployPath) {
        this.deployPath = deployPath;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getName() {
        return getId();
    }

    @Override
    public void setName(String value) {
        setId(value);
    }

    public static class Builder extends TEntityTemplate.Builder<TArtifact.Builder> {

        private String description;
        private String file;
        private String deployPath;

        public Builder(String name, QName type) {
            super(name, type);
        }

        public Builder setDescription(String description) {
            this.description = description;
            return self();
        }

        public Builder setFile(String file) {
            this.file = file;
            return self();
        }

        public Builder setDeployPath(String deployPath) {
            this.deployPath = deployPath;
            return self();
        }

        @Override
        public TArtifact.Builder self() {
            return this;
        }
    }
}
