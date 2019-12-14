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

package org.eclipse.winery.model.tosca.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Artifacts", propOrder = {
    "artifact"
})
public class TArtifacts {

    @XmlElement(name = "Artifact", required = true)
    List<TArtifact> artifact;

    public TArtifacts() {

    }

    public TArtifacts(List<TArtifact> artifacts) {
        this.artifact = artifacts;
    }

    public List<TArtifact> getArtifact() {
        if (artifact == null) {
            artifact = new ArrayList<>();
        }
        return artifact;
    }

    public void setArtifact(List<TArtifact> artifact) {
        this.artifact = artifact;
    }

    public void addArtifact(TArtifact artifact) {
        if (this.artifact == null) {
            this.artifact = new ArrayList<>();
        }
        this.artifact.add(artifact);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TArtifacts that = (TArtifacts) o;
        return artifact.equals(that.artifact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artifact);
    }
}
