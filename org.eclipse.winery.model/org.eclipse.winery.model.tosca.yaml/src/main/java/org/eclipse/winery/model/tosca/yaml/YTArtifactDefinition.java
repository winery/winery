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
package org.eclipse.winery.model.tosca.yaml;

import java.util.Map;
import java.util.Objects;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.support.Annotations;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class YTArtifactDefinition implements VisitorNode {
    private QName type;
    private String repository;
    private String description;
    private String deployPath;

    @Annotations.StandardExtension
    private String file;
    @Annotations.StandardExtension
    private Map<String, YTPropertyAssignment> properties;

    protected YTArtifactDefinition(Builder builder) {
        this.setType(builder.type);
        this.setFile(builder.file);
        this.setRepository(builder.repository);
        this.setDescription(builder.description);
        this.setDeployPath(builder.deployPath);
        this.setProperties(builder.properties);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YTArtifactDefinition)) return false;
        YTArtifactDefinition that = (YTArtifactDefinition) o;
        return Objects.equals(getType(), that.getType()) &&
            Objects.equals(getRepository(), that.getRepository()) &&
            Objects.equals(getDescription(), that.getDescription()) &&
            Objects.equals(getDeployPath(), that.getDeployPath()) &&
            Objects.equals(getFile(), that.getFile()) &&
            Objects.equals(getProperties(), that.getProperties());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getRepository(), getDescription(), getDeployPath(), getFile(), getProperties());
    }

    @Override
    public String toString() {
        return "TArtifactDefinition{" +
            "type=" + getType() +
            ", repository='" + getRepository() + '\'' +
            ", description='" + getDescription() + '\'' +
            ", deployPath='" + getDeployPath() + '\'' +
            ", file=" + getFile() +
            ", properties=" + getProperties() +
            '}';
    }

    @NonNull
    public QName getType() {
        return type;
    }

    public void setType(QName type) {
        this.type = type;
    }

    public String getFile() {
        return this.file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Nullable
    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Nullable
    public String getDeployPath() {
        return deployPath;
    }

    public void setDeployPath(String deployPath) {
        this.deployPath = deployPath;
    }

    public Map<String, YTPropertyAssignment> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, YTPropertyAssignment> properties) {
        this.properties = properties;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder {
        private final QName type;
        private final String file;

        private String repository;
        private String description;
        private String deployPath;
        private Map<String, YTPropertyAssignment> properties;

        public Builder(QName type, String file) {
            this.type = type;
            this.file = file;
        }

        public Builder(YTArtifactDefinition artifactDefinition) {
            this.type = artifactDefinition.getType();
            this.file = artifactDefinition.getFile();
            this.repository = artifactDefinition.getRepository();
            this.description = artifactDefinition.getDescription();
            this.deployPath = artifactDefinition.getDeployPath();
            this.properties = artifactDefinition.getProperties();
        }

        public Builder setRepository(String repository) {
            this.repository = repository;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setDeployPath(String deployPath) {
            this.deployPath = deployPath;
            return this;
        }

        public Builder setProperties(Map<String, YTPropertyAssignment> properties) {
            this.properties = properties;
            return this;
        }

        public YTArtifactDefinition build() {
            return new YTArtifactDefinition(this);
        }
    }
}
