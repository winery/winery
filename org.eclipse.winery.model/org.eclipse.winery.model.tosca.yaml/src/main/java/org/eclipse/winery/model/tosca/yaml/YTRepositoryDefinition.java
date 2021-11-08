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

import java.util.Objects;

import org.eclipse.winery.model.tosca.yaml.tosca.datatypes.Credential;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class YTRepositoryDefinition implements VisitorNode {
    private String description;
    private String url;
    private Credential credential;

    protected YTRepositoryDefinition(Builder builder) {
        this.setUrl(builder.url);
        this.setDescription(builder.description);
        this.setCredential(builder.credential);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YTRepositoryDefinition)) return false;
        YTRepositoryDefinition that = (YTRepositoryDefinition) o;
        return Objects.equals(getDescription(), that.getDescription()) &&
            Objects.equals(getUrl(), that.getUrl()) &&
            Objects.equals(getCredential(), that.getCredential());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDescription(), getUrl(), getCredential());
    }

    @Override
    public String toString() {
        return "TRepositoryDefinition{" +
            "description='" + getDescription() + '\'' +
            ", url='" + getUrl() + '\'' +
            ", credential=" + getCredential() +
            '}';
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Nullable
    public Credential getCredential() {
        return credential;
    }

    public void setCredential(Credential credential) {
        this.credential = credential;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder {
        private final String url;
        private String description;
        private Credential credential;

        public Builder(String url) {
            this.url = url;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setCredential(Credential credential) {
            this.credential = credential;
            return this;
        }

        public YTRepositoryDefinition build() {
            return new YTRepositoryDefinition(this);
        }
    }
}
