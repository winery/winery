/*******************************************************************************
 * Copyright (c) 2012-2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.entitytemplates.artifacttemplates;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.eclipse.winery.repository.rest.resources._support.AbstractComponentsWithTypeReferenceResource;

import io.swagger.annotations.Api;

/**
 * This class does NOT inherit from TEntityTemplatesResource<ArtifactTemplate>
 * as these templates are directly nested in a TDefinitionsElement
 */
@Api(tags = "Artifact Templates")
public class ArtifactTemplatesResource extends AbstractComponentsWithTypeReferenceResource<ArtifactTemplateResource> {
	@Path("{namespace}/{id}/")
	public ArtifactTemplateResource getComponentInstaceResource(@PathParam("namespace") String namespace, @PathParam("id") String id) {
		return this.getComponentInstaceResource(namespace, id, true);
	}
}
