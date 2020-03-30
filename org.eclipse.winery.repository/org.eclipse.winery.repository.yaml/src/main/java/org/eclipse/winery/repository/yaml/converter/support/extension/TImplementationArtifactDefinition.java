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
package org.eclipse.winery.repository.yaml.converter.support.extension;

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

        public Builder(QName type, String file) {
            super(type, file);
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
