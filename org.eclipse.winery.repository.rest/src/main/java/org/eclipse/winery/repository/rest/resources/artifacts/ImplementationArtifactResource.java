/*******************************************************************************
 * Copyright (c) 2012-2013 Contributors to the Eclipse Foundation
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
import org.eclipse.winery.model.tosca.TDeploymentOrImplementationArtifact;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.repository.rest.resources._support.collections.IIdDetermination;

import java.util.List;
import java.util.Objects;

public class ImplementationArtifactResource extends GenericArtifactResource<TImplementationArtifact> {

    private TImplementationArtifact a;


    public ImplementationArtifactResource(String artifactId, List<TImplementationArtifact> implementationArtifacts, IPersistable res) {
        this(ImplementationArtifactResource.getTImplementationArtifact(artifactId, implementationArtifacts), implementationArtifacts, res);
    }

    public ImplementationArtifactResource(IIdDetermination<TImplementationArtifact> idDetermination, TImplementationArtifact o, int idx, List<TImplementationArtifact> list, IPersistable res) {
        super(idDetermination, o, idx, list, res);
        this.a = o;
    }

    public ImplementationArtifactResource(TImplementationArtifact a, List<TImplementationArtifact> implementationArtifacts, IPersistable res) {
        this(TDeploymentOrImplementationArtifact::getName, a, implementationArtifacts.indexOf(a), implementationArtifacts, res);
    }

    /**
     * Converts the given artifactId to an ImplementArtifact.
     * <p>
     * <em>SIDE EFFECT</em> Adds it to the implementationArtifacts list if it
     * does not yet exist.
     */
    private static TImplementationArtifact getTImplementationArtifact(String artifactId, List<TImplementationArtifact> implementationArtifacts) {
        Objects.requireNonNull(artifactId);
        Objects.requireNonNull(implementationArtifacts);
        for (TImplementationArtifact ia : implementationArtifacts) {
            // ia.getName() might be null as TOSCA COS01 does not forsee a name on the implementation artifact
            // Therefore, we begin the test with "artifactId"
            if (artifactId.equals(ia.getName())) {
                return ia;
            }
        }
        // IA does not exist in list
        TImplementationArtifact ia = new TImplementationArtifact();
        ia.setName(artifactId);
        implementationArtifacts.add(ia);
        return ia;
    }

    public TImplementationArtifact getImplementationArtifact() {
        return this.a;
    }

    @Override
    public void setArtifactType(ArtifactTypeId artifactTypeId) {
        this.getImplementationArtifact().setArtifactType(artifactTypeId.getQName());
        RestUtils.persist(this.res);
    }

    @Override
    public void setArtifactTemplate(ArtifactTemplateId artifactTemplateId) {
        this.getImplementationArtifact().setArtifactRef(artifactTemplateId.getQName());
        RestUtils.persist(this.res);
    }

    @Override
    public TImplementationArtifact getA() {
        return this.a;
    }

}
