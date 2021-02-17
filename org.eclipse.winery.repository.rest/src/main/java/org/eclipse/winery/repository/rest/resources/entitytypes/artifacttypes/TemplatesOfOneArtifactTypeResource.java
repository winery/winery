/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.entitytypes.artifacttypes;

import org.eclipse.winery.model.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.resources.entitytypes.TemplatesOfOneType;

import java.util.Collection;

public class TemplatesOfOneArtifactTypeResource extends TemplatesOfOneType {

    private ArtifactTypeId artifactTypeId;

    /**
     * Resource returns all templates/implementations of the given artifact type
     *
     * @param artifactTypeId the Id of the artifact type
     */
    public TemplatesOfOneArtifactTypeResource(ArtifactTypeId artifactTypeId) {
        this.artifactTypeId = artifactTypeId;
    }

    @Override
    public Collection<ArtifactTemplateId> getAllImplementations() {
        return RepositoryFactory.getRepository().getAllElementsReferencingGivenType(ArtifactTemplateId.class, this.artifactTypeId.getQName());
    }
}
