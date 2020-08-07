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
package org.eclipse.winery.model.tosca.extensions;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.TTopologyTemplate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "otPatternRefinementModel")
public class OTPatternRefinementModel extends OTTopologyFragmentRefinementModel {

    @Deprecated
    public OTPatternRefinementModel() { }

    public OTPatternRefinementModel(Builder builder) {
        super(builder);
    }

    public static class Builder extends OTTopologyFragmentRefinementModel.Builder {
        public Builder() { }

        public OTPatternRefinementModel build() {
            return new OTPatternRefinementModel(this);
        }

        @Override
        public Builder self() {
            return this;
        }
    }
}