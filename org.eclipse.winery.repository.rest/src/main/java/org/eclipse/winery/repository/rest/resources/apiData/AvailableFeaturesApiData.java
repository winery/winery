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

package org.eclipse.winery.repository.rest.resources.apiData;

import java.util.List;

import javax.xml.namespace.QName;

public class AvailableFeaturesApiData {

    private String nodeTemplateId;
    private List<Features> features;

    public AvailableFeaturesApiData() {
    }

    public AvailableFeaturesApiData(String nodeTemplateId, List<Features> features) {
        this.nodeTemplateId = nodeTemplateId;
        this.features = features;
    }

    public String getNodeTemplateId() {
        return nodeTemplateId;
    }

    public void setNodeTemplateId(String nodeTemplateId) {
        this.nodeTemplateId = nodeTemplateId;
    }

    public List<Features> getFeatures() {
        return features;
    }

    public void setFeatures(List<Features> features) {
        this.features = features;
    }

    public static class Features {

        private QName type;
        private String featureName;

        public Features() {
        }

        public Features(QName type, String featureName) {
            this.type = type;
            this.featureName = featureName;
        }

        public QName getType() {
            return type;
        }

        public void setType(QName type) {
            this.type = type;
        }

        public String getFeatureName() {
            return featureName;
        }

        public void setFeatureName(String featureName) {
            this.featureName = featureName;
        }
    }
}
