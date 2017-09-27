/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */

package org.eclipse.winery.repository.rest.resources;

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

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
