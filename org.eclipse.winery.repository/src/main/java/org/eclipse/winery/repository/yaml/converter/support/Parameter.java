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
package org.eclipse.winery.repository.yaml.converter.support;

import java.nio.file.Path;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;

public class Parameter extends AbstractParameter<Parameter> {
    private final String namespace;
    private Path path;
    // SchemaVisitor
    private QName datatype;
    private Boolean buildSchema;
    private Boolean buildComplexType;

    public Parameter(Path path, String namespace) {
        this.namespace = namespace;
        this.path = path;
        this.buildSchema = true;
        this.buildComplexType = true;
    }

    public Path getPath() {
        return path;
    }

    public Parameter setPath(Path path) {
        this.path = path;
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public QName getDatatype() {
        return this.datatype;
    }

    public Parameter setDatatype(QName datatype) {
        this.datatype = datatype;
        return this;
    }

    public Boolean getBuildComplexType() {
        return buildComplexType;
    }

    public Parameter setBuildComplexType(Boolean buildComplexType) {
        this.buildComplexType = buildComplexType;
        return this;
    }

    public Boolean getBuildSchema() {
        return buildSchema;
    }

    public Parameter setBuildSchema(Boolean buildSchema) {
        this.buildSchema = buildSchema;
        return this;
    }

    @Override
    public Parameter copy() {
        Parameter parameter = new Parameter(this.path, this.namespace);
        parameter.getContext().addAll(this.getContext());
        return parameter.setDatatype(this.datatype).setBuildSchema(this.buildSchema);
    }

    @Override
    public Parameter self() {
        return this;
    }
}
