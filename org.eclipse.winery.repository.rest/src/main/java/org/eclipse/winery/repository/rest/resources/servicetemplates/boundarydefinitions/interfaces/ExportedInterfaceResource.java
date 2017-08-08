/*******************************************************************************
 * Copyright (c) 2014 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.servicetemplates.boundarydefinitions.interfaces;

import java.util.List;

import javax.ws.rs.Path;

import org.eclipse.winery.model.tosca.TExportedInterface;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.repository.rest.resources._support.collections.IIdDetermination;
import org.eclipse.winery.repository.rest.resources._support.collections.withid.EntityWithIdResource;

public class ExportedInterfaceResource extends EntityWithIdResource<TExportedInterface> {

	public ExportedInterfaceResource(IIdDetermination<TExportedInterface> idDetermination, TExportedInterface o, int idx, List<TExportedInterface> list, IPersistable res) {
		super(idDetermination, o, idx, list, res);
	}

	@Path("exportedoperations/")
	public ExportedOperationsResource getExportedOperationsResource() {
		return new ExportedOperationsResource(this.o.getOperation(), this.res);
	}

}
