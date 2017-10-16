/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.yaml.converter.yaml.support.extension;

import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.TArtifactDefinition;

public class TImplementationArtifactDefinition extends TArtifactDefinition {
	private String interfaceName;
	private String operationName;

	public TImplementationArtifactDefinition() {

	}

	public TImplementationArtifactDefinition(Builder builder) {
		super(builder);
		this.interfaceName = builder.interfaceName;
		this.operationName = builder.operationName;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public static class Builder extends TArtifactDefinition.Builder {
		private String interfaceName;
		private String operationName;

		public Builder(QName type, List<String> files) {
			super(type, files);
		}

		public Builder(TArtifactDefinition artifactDefinition) {
			super(artifactDefinition);
		}

		public Builder setInterfaceName(String interfaceName) {
			this.interfaceName = interfaceName;
			return this;
		}

		public Builder setOperationName(String operationName) {
			this.operationName = operationName;
			return this;
		}

		public TImplementationArtifactDefinition build() {
			return new TImplementationArtifactDefinition(this);
		}
	}
}
