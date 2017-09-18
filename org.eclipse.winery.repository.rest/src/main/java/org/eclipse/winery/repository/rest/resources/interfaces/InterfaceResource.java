/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.interfaces;

import java.util.List;

import javax.ws.rs.Path;

import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.repository.rest.resources._support.collections.IIdDetermination;
import org.eclipse.winery.repository.rest.resources._support.collections.withid.EntityWithIdResource;

public class InterfaceResource extends EntityWithIdResource<TInterface> {

	private final TInterface iface;


	public InterfaceResource(IIdDetermination<TInterface> idDetermination, TInterface o, int idx, List<TInterface> list, IPersistable res) {
		super(idDetermination, o, idx, list, res);
		this.iface = o;
	}

	/**
	 * required by artifacts.jsp
	 */
	public String getName() {
		return this.iface.getName();
	}

	@Path("operations/")
	public OperationsResource getOperationsResouce() {
		List<TOperation> list = this.o.getOperation();
		return new OperationsResource(list, this.res);
	}
}
