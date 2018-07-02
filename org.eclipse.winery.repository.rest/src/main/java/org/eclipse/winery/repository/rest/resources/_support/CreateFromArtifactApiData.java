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

package org.eclipse.winery.repository.rest.resources._support;

import javax.xml.namespace.QName;
import java.util.HashSet;
import java.util.Set;

public class CreateFromArtifactApiData {
    private Set<QName> artifactTypes = new HashSet<QName>();
    private Set<QName> infrastructureNodeTypes = new HashSet<QName>();

    public CreateFromArtifactApiData(Set<QName> artifactTypes, Set<QName> infrastructureNodeTypes) {
        this.artifactTypes = artifactTypes;
        this.infrastructureNodeTypes = infrastructureNodeTypes;

    }

    public Set<QName> getArtifactTypes() {
        return artifactTypes;
    }

    public void setArtifactTypes(Set<QName> artifactTypes) {
        this.artifactTypes = artifactTypes;
    }

    public Set<QName> getInfrastructureNodeTypes() {
        return infrastructureNodeTypes;
    }

    public void setInfrastructureNodeTypes(Set<QName> infrastructureNodeTypes) {
        this.infrastructureNodeTypes = infrastructureNodeTypes;
    }
}
