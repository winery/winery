/*******************************************************************************
 * Copyright (c) 2014-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.servicetemplates.boundarydefinitions.interfaces;

import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.eclipse.winery.model.tosca.TExportedOperation;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.repository.rest.resources._support.collections.withid.EntityWithIdCollectionResource;

public class ExportedOperationsResource extends EntityWithIdCollectionResource<ExportedOperationResource, TExportedOperation> {

	public ExportedOperationsResource(List<TExportedOperation> list, IPersistable res) {
		super(ExportedOperationResource.class, TExportedOperation.class, list, res);
	}

	@Override
	public String getId(TExportedOperation entity) {
		return entity.getName();
	}

	@Override
	@Path("{id}/")
	public ExportedOperationResource getEntityResource(@PathParam("id") String id) {
		return this.getEntityResourceFromEncodedId(id);
	}
}
