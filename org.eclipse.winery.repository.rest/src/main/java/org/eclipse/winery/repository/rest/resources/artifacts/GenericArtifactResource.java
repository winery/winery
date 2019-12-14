/*******************************************************************************
 * Copyright (c) 2012-2015 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.artifacts;

import org.eclipse.winery.model.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.repository.rest.resources._support.collections.IIdDetermination;
import org.eclipse.winery.repository.rest.resources._support.collections.withid.EntityWithIdResource;

import java.util.List;

/**
 * Currently no common things for deployment artifacts and implementation
 * artifacts as the data model also has no common ancestor (besides
 * TExensibleElement)
 */
public abstract class GenericArtifactResource<ArtifactT> extends EntityWithIdResource<ArtifactT> {

    public GenericArtifactResource(IIdDetermination<ArtifactT> idDetermination, ArtifactT o, int idx, List<ArtifactT> list, IPersistable res) {
        super(idDetermination, o, idx, list, res);
    }

    public abstract void setArtifactType(ArtifactTypeId artifactTypeId);

    public abstract void setArtifactTemplate(ArtifactTemplateId artifactTemplateId);

    /**
     * required by artifacts.jsp
     */
    public abstract ArtifactT getA();

}
