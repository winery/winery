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

import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

/**
 * Part of Operation Definition
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tImplementation", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.3", propOrder = {
    "primaryArtifactName",
    "dependencyArtifactNames",
    "operationHost",
    "timeout",
})
public class TImplementation implements VisitorNode {

    @XmlAttribute(name = "primary", required = true)
    private String primaryArtifactName;
    private List<String> dependencyArtifactNames;
    private String operationHost;
    private Integer timeout;

    public TImplementation() {
    }

    public TImplementation(String primaryArtifactName) {
        this.primaryArtifactName = primaryArtifactName;
    }

    public String getPrimaryArtifactName() {
        return primaryArtifactName;
    }

    public void setPrimaryArtifactName(String primaryArtifactName) {
        this.primaryArtifactName = primaryArtifactName;
    }

    public List<String> getDependencyArtifactNames() {
        return dependencyArtifactNames;
    }

    public void setDependencyArtifactNames(List<String> dependencyArtifactNames) {
        this.dependencyArtifactNames = dependencyArtifactNames;
    }

    public String getOperationHost() {
        return operationHost;
    }

    public void setOperationHost(String operationHost) {
        this.operationHost = operationHost;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TImplementation that = (TImplementation) o;
        return Objects.equals(primaryArtifactName, that.primaryArtifactName) &&
            Objects.equals(dependencyArtifactNames, that.dependencyArtifactNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(primaryArtifactName, dependencyArtifactNames);
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }
}
