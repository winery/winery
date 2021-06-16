/*******************************************************************************
 * Copyright (c) 2020-2021 Contributors to the Eclipse Foundation
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.Namespaces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "otTopologyFragmentRefinementModel", namespace = Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE)
public class OTTopologyFragmentRefinementModel extends OTRefinementModel {

    @XmlElement(name = "RefinementStructure")
    protected TTopologyTemplate refinementStructure;

    @XmlElementWrapper(name = "AttributeMappings")
    @XmlElement(name = "AttributeMapping")
    protected List<OTAttributeMapping> attributeMappings;

    @XmlElementWrapper(name = "StayMappings")
    @XmlElement(name = "StayMapping")
    protected List<OTStayMapping> stayMappings;

    @XmlElementWrapper(name = "DeploymentArtifactMappings")
    @XmlElement(name = "DeploymentArtifactMapping")
    protected List<OTDeploymentArtifactMapping> deploymentArtifactMappings;

    @XmlElementWrapper(name = "PermutationOptions")
    @XmlElement(name = "PermutationOption")
    protected List<OTStringList> permutationOptions;

    @XmlElementWrapper(name = "ComponentSets")
    @XmlElement(name = "ComponentSet")
    protected List<OTStringList> componentSets;

    @Deprecated // used for XML deserialization of API request content
    public OTTopologyFragmentRefinementModel() {
    }

    public OTTopologyFragmentRefinementModel(RefinementBuilder<? extends RefinementBuilder<?>> builder) {
        super(builder);
        this.refinementStructure = builder.refinementStructure;
        this.attributeMappings = builder.attributeMappings;
        this.stayMappings = builder.stayMappings;
        this.deploymentArtifactMappings = builder.deploymentArtifactMappings;
        this.permutationOptions = builder.permutationOptions;
        this.permutationMappings = builder.permutationMappings;
        this.componentSets = builder.componentSets;
    }

    @NonNull
    @JsonIgnore
    @XmlTransient
    public TTopologyTemplate getRefinementTopology() {
        if (refinementStructure == null) {
            refinementStructure = new TTopologyTemplate();
        }
        return refinementStructure;
    }

    public TTopologyTemplate getRefinementStructure() {
        return getRefinementTopology();
    }

    public void setRefinementTopology(TTopologyTemplate refinementStructure) {
        this.refinementStructure = refinementStructure;
    }

    /**
     * remove all mappings so that delete works when modeling graphically
     */
    @Override
    public void resetAllMappings() {
        setDeploymentArtifactMappings(new ArrayList<>());
        setPermutationMappings(new ArrayList<>());
        setRelationMappings(new ArrayList<>());
        setAttributeMappings(new ArrayList<>());
        setStayMappings(new ArrayList<>());
    }

    @Nullable
    public List<OTAttributeMapping> getAttributeMappings() {
        return attributeMappings;
    }

    public void setAttributeMappings(List<OTAttributeMapping> attributeMappings) {
        this.attributeMappings = attributeMappings;
    }

    @Nullable
    public List<OTStayMapping> getStayMappings() {
        return stayMappings;
    }

    public void setStayMappings(List<OTStayMapping> stayMappings) {
        this.stayMappings = stayMappings;
    }

    @Nullable
    public List<OTDeploymentArtifactMapping> getDeploymentArtifactMappings() {
        return deploymentArtifactMappings;
    }

    public void setDeploymentArtifactMappings(List<OTDeploymentArtifactMapping> deploymentArtifactMappings) {
        this.deploymentArtifactMappings = deploymentArtifactMappings;
    }

    public List<OTStringList> getPermutationOptions() {
        return permutationOptions;
    }

    public void setPermutationOptions(List<OTStringList> permutationOptions) {
        this.permutationOptions = permutationOptions;
    }

    public List<OTStringList> getComponentSets() {
        return componentSets;
    }

    public void setComponentSets(List<OTStringList> componentSets) {
        this.componentSets = componentSets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OTTopologyFragmentRefinementModel that = (OTTopologyFragmentRefinementModel) o;
        return Objects.equals(refinementStructure, that.refinementStructure)
            && Objects.equals(attributeMappings, that.attributeMappings)
            && Objects.equals(stayMappings, that.stayMappings)
            && Objects.equals(deploymentArtifactMappings, that.deploymentArtifactMappings)
            && Objects.equals(permutationOptions, that.permutationOptions)
            && Objects.equals(componentSets, that.componentSets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), refinementStructure, attributeMappings, stayMappings,
            deploymentArtifactMappings, permutationOptions, componentSets);
    }

    public abstract static class RefinementBuilder<T extends OTRefinementModel.Builder<T>> extends OTRefinementModel.Builder<T> {

        private List<OTPermutationMapping> permutationMappings;
        private List<OTStringList> permutationOptions;
        private TTopologyTemplate refinementStructure;
        private List<OTAttributeMapping> attributeMappings;
        private List<OTStayMapping> stayMappings;
        private List<OTDeploymentArtifactMapping> deploymentArtifactMappings;
        private List<OTStringList> componentSets;

        public RefinementBuilder() {
        }

        public T setRefinementStructure(TTopologyTemplate refinementStructure) {
            this.refinementStructure = refinementStructure;
            return self();
        }

        public T setAttributeMappings(List<OTAttributeMapping> attributeMappings) {
            this.attributeMappings = attributeMappings;
            return self();
        }

        public T setStayMappings(List<OTStayMapping> stayMappings) {
            this.stayMappings = stayMappings;
            return self();
        }

        public T setDeploymentArtifactMappings(List<OTDeploymentArtifactMapping> deploymentArtifactMappings) {
            this.deploymentArtifactMappings = deploymentArtifactMappings;
            return self();
        }

        public T setPermutationOptions(List<OTStringList> permutationOptions) {
            this.permutationOptions = permutationOptions;
            return self();
        }

        public T setPermutationMappings(List<OTPermutationMapping> permutationMappings) {
            this.permutationMappings = permutationMappings;
            return self();
        }

        public T setComponentSets(List<OTStringList> componentSets) {
            this.componentSets = componentSets;
            return self();
        }
    }

    public static class Builder extends RefinementBuilder<Builder> {

        public Builder() {
        }

        public OTTopologyFragmentRefinementModel build() {
            return new OTTopologyFragmentRefinementModel(this);
        }

        @Override
        public Builder self() {
            return this;
        }
    }
}
