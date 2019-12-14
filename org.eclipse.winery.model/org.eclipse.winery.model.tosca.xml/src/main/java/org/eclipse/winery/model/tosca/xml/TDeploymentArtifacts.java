/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * <p>Java class for tDeploymentArtifacts complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="tDeploymentArtifacts">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DeploymentArtifact" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tDeploymentArtifact"
 * maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tDeploymentArtifacts", propOrder = {
    "deploymentArtifact"
})
public class TDeploymentArtifacts implements Serializable {

    @XmlElement(name = "DeploymentArtifact", required = true)
    protected List<TDeploymentArtifact> deploymentArtifact;

    public TDeploymentArtifacts() {

    }

    public TDeploymentArtifacts(Builder builder) {
        this.deploymentArtifact = builder.deploymentArtifact;
    }

    /**
     * Gets the value of the deploymentArtifact property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the deploymentArtifact property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDeploymentArtifact().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TDeploymentArtifact }
     */
    @NonNull
    public List<TDeploymentArtifact> getDeploymentArtifact() {
        if (deploymentArtifact == null) {
            deploymentArtifact = new ArrayList<TDeploymentArtifact>();
        }
        return this.deploymentArtifact;
    }

    /**
     * @return deploymentArtifact having the given name. null if not found
     */
    @Nullable
    public TDeploymentArtifact getDeploymentArtifact(String id) {
        Objects.requireNonNull(id);
        return this.getDeploymentArtifact().stream()
            .filter(x -> id.equals(x.getName()))
            .findAny()
            .orElse(null);
    }

    public static class Builder {
        private final List<TDeploymentArtifact> deploymentArtifact;

        public Builder(List<TDeploymentArtifact> deploymentArtifact) {
            this.deploymentArtifact = deploymentArtifact;
        }

        public TDeploymentArtifacts build() {
            return new TDeploymentArtifacts(this);
        }
    }
}
