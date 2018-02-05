/********************************************************************************
 * Copyright (c) 2014 Contributors to the Eclipse Foundation
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
