/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.model.tosca.yaml;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.support.Defaults;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tImportDefinition", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.0", propOrder = {
    "file",
    "repository",
    "namespaceUri",
    "namespacePrefix"
})
public class TImportDefinition implements VisitorNode {
    @XmlAttribute(name = "file", required = true)
    private String file;
    private QName repository;
    @XmlAttribute(name = "namespace_uri")
    private String namespaceUri;
    @XmlAttribute(name = "namespace_prefix")
    private String namespacePrefix;

    public TImportDefinition() {

    }

    public TImportDefinition(String file) {
        this.file = file;
    }

    public TImportDefinition(Builder builder) {
        this.setFile(builder.file);
        this.setRepository(builder.repository);
        this.setNamespaceUri(builder.namespaceUri);
        this.setNamespacePrefix(builder.namespacePrefix);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TImportDefinition)) return false;
        TImportDefinition that = (TImportDefinition) o;
        return Objects.equals(getFile(), that.getFile()) &&
            Objects.equals(getRepository(), that.getRepository()) &&
            Objects.equals(getNamespaceUri(), that.getNamespaceUri()) &&
            Objects.equals(getNamespacePrefix(), that.getNamespacePrefix());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFile(), getRepository(), getNamespaceUri(), getNamespacePrefix());
    }

    @NonNull
    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Nullable
    public QName getRepository() {
        return repository;
    }

    public void setRepository(QName repository) {
        this.repository = repository;
    }

    @NonNull
    public String getNamespaceUri() {
        if (namespaceUri == null || namespaceUri.isEmpty()) {
            this.namespaceUri = Defaults.DEFAULT_NS;
        }
        return namespaceUri;
    }

    public void setNamespaceUri(String namespaceUri) {
        this.namespaceUri = namespaceUri;
    }

    @Nullable
    public String getNamespacePrefix() {
        return namespacePrefix;
    }

    public void setNamespacePrefix(String namespacePrefix) {
        this.namespacePrefix = namespacePrefix;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder {
        private final String file;
        private QName repository;
        private String namespaceUri;
        private String namespacePrefix;

        public Builder(String file) {
            this.file = file;
        }

        public Builder setRepository(QName repository) {
            this.repository = repository;
            return this;
        }

        public Builder setNamespaceUri(String namespaceUri) {
            this.namespaceUri = namespaceUri;
            return this;
        }

        public Builder setNamespacePrefix(String namespacePrefix) {
            this.namespacePrefix = namespacePrefix;
            return this;
        }

        public TImportDefinition build() {
            return new TImportDefinition(this);
        }
    }
}
