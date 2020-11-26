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
package org.eclipse.winery.model.tosca;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Class to represent an operations implementation in TOSCA YAML.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OperationImplementation", propOrder = {
    "primary",
    "dependencies",
    "operationHost",
    "timeout",
})
public class TImplementation implements Serializable {

    private String primary;
    private List<String> dependencies;
    private String operationHost;
    private Integer timeout = 0;

    @Deprecated // used for XML deserialization of API request content
    public TImplementation() { }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
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
}
