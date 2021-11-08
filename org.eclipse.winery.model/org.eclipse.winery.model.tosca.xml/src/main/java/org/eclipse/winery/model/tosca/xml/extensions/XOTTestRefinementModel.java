/*******************************************************************************
 * Copyright (c) 2019-2020 Contributors to the Eclipse Foundation
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.xml.XTTopologyTemplate;

import org.eclipse.jdt.annotation.NonNull;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "otTestRefinementModel")
public class XOTTestRefinementModel extends XOTRefinementModel {

    private XTTopologyTemplate testFragment;

    @Deprecated // required for XML deserialization
    public XOTTestRefinementModel() { }

    public XOTTestRefinementModel(Builder builder) {
        super(builder);
        this.testFragment = builder.testFragment;
    }

    @NonNull
    @XmlTransient
    public XTTopologyTemplate getRefinementTopology() {
        if (testFragment == null) {
            testFragment = new XTTopologyTemplate();
        }
        return testFragment;
    }

    public void setRefinementTopology(XTTopologyTemplate refinementStructure) {
        this.testFragment = refinementStructure;
    }

    @XmlElement(name = "TestFragment")
    public XTTopologyTemplate getTestFragment() {
        return getRefinementTopology();
    }

    public static class Builder extends XOTRefinementModel.Builder<Builder> {

        private XTTopologyTemplate testFragment;

        public Builder() { }

        public Builder setTestFragment(XTTopologyTemplate testFragment) {
            this.testFragment = testFragment;
            return self();
        }

        public XOTTestRefinementModel build() {
            return new XOTTestRefinementModel(this);
        }

        @Override
        public Builder self() {
            return this;
        }
    }
}
