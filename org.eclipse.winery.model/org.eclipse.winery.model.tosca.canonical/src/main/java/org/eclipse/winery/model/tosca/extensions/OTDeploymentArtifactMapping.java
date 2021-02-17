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

package org.eclipse.winery.model.tosca.extensions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "otDeploymentArtifactMapping")
public class OTDeploymentArtifactMapping extends OTPrmMapping {

    @XmlAttribute(name = "artifactType", required = true)
    private QName artifactType;

    @Deprecated // used for XML deserialization of API request content
    public OTDeploymentArtifactMapping() { }

    public OTDeploymentArtifactMapping(Builder builder) {
        super(builder);
        this.artifactType = builder.artifactType;
    }

    public QName getArtifactType() {
        return artifactType;
    }

    public void setArtifactType(QName artifactType) {
        this.artifactType = artifactType;
    }

    public static class Builder extends OTPrmMapping.Builder<Builder> {

        private QName artifactType;

        public Builder() {
            super();
        }

        public Builder(String id) {
            super(id);
        }

        public Builder setArtifactType(QName artifactType) {
            this.artifactType = artifactType;
            return self();
        }

        @Override
        public Builder self() {
            return this;
        }

        public OTDeploymentArtifactMapping build() {
            return new OTDeploymentArtifactMapping(this);
        }
    }
}
