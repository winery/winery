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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.TTopologyTemplate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.jdt.annotation.NonNull;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "otTestRefinementModel")
public class OTTestRefinementModel extends OTRefinementModel {

    @XmlElement(name = "TestFragment")
    private TTopologyTemplate testFragment;

    @Deprecated // used for XML deserialization of API request content
    public OTTestRefinementModel() { }

    public OTTestRefinementModel(Builder builder) {
        super(builder);
        this.testFragment = builder.testFragment;
    }

    @NonNull
    @JsonIgnore
    @XmlTransient
    public TTopologyTemplate getRefinementTopology() {
        if (testFragment == null) {
            testFragment = new TTopologyTemplate();
        }
        return testFragment;
    }

    public void setRefinementTopology(TTopologyTemplate refinementStructure) {
        this.testFragment = refinementStructure;
    }

    public TTopologyTemplate getTestFragment() {
        return getRefinementTopology();
    }

    public static class Builder extends OTRefinementModel.Builder<Builder> {

        private TTopologyTemplate testFragment;

        public Builder() { }

        public Builder setTestFragment(TTopologyTemplate testFragment) {
            this.testFragment = testFragment;
            return self();
        }

        public OTTestRefinementModel build() {
            return new OTTestRefinementModel(this);
        }

        @Override
        public Builder self() {
            return this;
        }
    }
}
