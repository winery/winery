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

package org.eclipse.winery.repository.rest.resources.apiData;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.extensions.OTDeploymentArtifactMapping;
import org.eclipse.winery.model.tosca.TEntityTemplate;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PrmDeploymentArtifactMappingApiData extends AbstractPrmMappingElement {

    public QName artifactType;

    public PrmDeploymentArtifactMappingApiData() {
    }

    @JsonIgnore
    public OTDeploymentArtifactMapping createDeploymentArtifactMapping(TEntityTemplate detectorNodeTemplate, TEntityTemplate refinementNodeTemplate) {
        OTDeploymentArtifactMapping artifactMapping = new OTDeploymentArtifactMapping();
        artifactMapping.setId(this.id);
        artifactMapping.setDetectorNode(detectorNodeTemplate);
        artifactMapping.setArtifactType(this.artifactType);
        artifactMapping.setRefinementNode(refinementNodeTemplate);

        return artifactMapping;
    }
}
