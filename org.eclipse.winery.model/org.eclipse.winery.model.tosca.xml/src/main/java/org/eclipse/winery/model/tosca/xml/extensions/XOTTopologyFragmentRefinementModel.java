/*******************************************************************************
 * Copyright (c) 2018-2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.tosca.xml.extensions;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.xml.XTTopologyTemplate;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "otTopologyFragmentRefinementModel")
@XmlSeeAlso( {
    XOTPatternRefinementModel.class
})
public class XOTTopologyFragmentRefinementModel extends XOTRefinementModel {

    @XmlElement(name = "RefinementStructure")
    protected XTTopologyTemplate refinementStructure;

    @XmlElementWrapper(name = "AttributeMappings")
    @XmlElement(name = "AttributeMapping")
    protected List<XOTAttributeMapping> attributeMappings;

    @XmlElementWrapper(name = "StayMappings")
    @XmlElement(name = "StayMapping")
    protected List<XOTStayMapping> stayMappings;

    @XmlElementWrapper(name = "DeploymentArtifactMappings")
    @XmlElement(name = "DeploymentArtifactMapping")
    protected List<XOTDeploymentArtifactMapping> deploymentArtifactMappings;

    @XmlElementWrapper(name = "PermutationOptions")
    @XmlElement(name = "PermutationOption")
    protected List<XOTStringList> permutationOptions;

    @XmlElementWrapper(name = "ComponentSets")
    @XmlElement(name = "ComponentSet")
    protected List<XOTStringList> componentSets;

    @Deprecated // required for XML deserialization
    public XOTTopologyFragmentRefinementModel() { }

    public XOTTopologyFragmentRefinementModel(Builder builder) {
        super(builder);
        this.refinementStructure = builder.refinementStructure;
        this.attributeMappings = builder.attributeMappings;
        this.stayMappings = builder.stayMappings;
        this.deploymentArtifactMappings = builder.deploymentArtifactMappings;
        this.permutationOptions = builder.permutationOptions;
    }

    @NonNull
    @XmlTransient
    public XTTopologyTemplate getRefinementTopology() {
        if (refinementStructure == null) {
            refinementStructure = new XTTopologyTemplate();
        }
        return refinementStructure;
    }

    public XTTopologyTemplate getRefinementStructure() {
        return getRefinementTopology();
    }

    public void setRefinementTopology(XTTopologyTemplate refinementStructure) {
        this.refinementStructure = refinementStructure;
    }

    @Nullable
    public List<XOTAttributeMapping> getAttributeMappings() {
        return attributeMappings;
    }

    public void setAttributeMappings(List<XOTAttributeMapping> attributeMappings) {
        this.attributeMappings = attributeMappings;
    }

    @Nullable
    public List<XOTStayMapping> getStayMappings() {
        return stayMappings;
    }

    public void setStayMappings(List<XOTStayMapping> stayMappings) {
        this.stayMappings = stayMappings;
    }

    @Nullable
    public List<XOTDeploymentArtifactMapping> getDeploymentArtifactMappings() {
        return deploymentArtifactMappings;
    }

    public void setDeploymentArtifactMappings(List<XOTDeploymentArtifactMapping> deploymentArtifactMappings) {
        this.deploymentArtifactMappings = deploymentArtifactMappings;
    }

    public List<XOTStringList> getPermutationOptions() {
        return permutationOptions;
    }

    public void setPermutationOptions(List<XOTStringList> permutationOptions) {
        this.permutationOptions = permutationOptions;
    }

    public List<XOTStringList> getComponentSets() {
        return componentSets;
    }

    public void setComponentSets(List<XOTStringList> componentSets) {
        this.componentSets = componentSets;
    }

    public static class Builder extends XOTRefinementModel.Builder<Builder> {

        private XTTopologyTemplate refinementStructure;
        private List<XOTAttributeMapping> attributeMappings;
        private List<XOTStayMapping> stayMappings;
        private List<XOTDeploymentArtifactMapping> deploymentArtifactMappings;
        private List<XOTStringList> permutationOptions;

        public Builder() {
            super();
        }

        public Builder setRefinementStructure(XTTopologyTemplate refinementStructure) {
            this.refinementStructure = refinementStructure;
            return self();
        }

        public Builder setAttributeMappings(List<XOTAttributeMapping> attributeMappings) {
            this.attributeMappings = attributeMappings;
            return self();
        }

        public Builder setStayMappings(List<XOTStayMapping> stayMappings) {
            this.stayMappings = stayMappings;
            return self();
        }

        public Builder setDeploymentArtifactMappings(List<XOTDeploymentArtifactMapping> deploymentArtifactMappings) {
            this.deploymentArtifactMappings = deploymentArtifactMappings;
            return self();
        }

        public Builder setPermutationOptions(List<XOTStringList> permutationOptions) {
            this.permutationOptions = permutationOptions;
            return self();
        }

        public XOTTopologyFragmentRefinementModel build() {
            return new XOTTopologyFragmentRefinementModel(this);
        }

        @Override
        public Builder self() {
            return this;
        }
    }
}
