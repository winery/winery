/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.yaml.converter.yaml.visitors.support;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;

public class Parameter extends AbstractParameter<Parameter> {
	private final String namespace;
	private String path;
	// SchemaVisitor
	private QName datatype;
	private Boolean buildSchema;
	private Boolean buildComplexType;

	public Parameter(String path, String namespace) {
		this.namespace = namespace;
		this.path = path;
		this.buildSchema = true;
		this.buildComplexType = true;
	}

	public String getPath() {
		return path;
	}

	public Parameter setPath(String path) {
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
