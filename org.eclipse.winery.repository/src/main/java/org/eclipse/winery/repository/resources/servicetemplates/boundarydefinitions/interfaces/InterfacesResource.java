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
package org.eclipse.winery.repository.resources.servicetemplates.boundarydefinitions.interfaces;

import java.util.List;

import org.eclipse.winery.model.tosca.TExportedInterface;
import org.eclipse.winery.repository.resources._support.IPersistable;
import org.eclipse.winery.repository.resources._support.collections.withid.EntityWithIdCollectionResource;

import com.sun.jersey.api.view.Viewable;

public class InterfacesResource extends EntityWithIdCollectionResource<ExportedInterfaceResource, TExportedInterface> {

	public InterfacesResource(List<TExportedInterface> list, IPersistable res) {
		super(ExportedInterfaceResource.class, TExportedInterface.class, list, res);
	}

	@Override
	public String getId(TExportedInterface entity) {
		return entity.getName();
	}

	@Override
	public Viewable getHTML() {
		throw new IllegalStateException("No implementation required: boundarydefinitions.jsp contains all required html.");
	}

}
