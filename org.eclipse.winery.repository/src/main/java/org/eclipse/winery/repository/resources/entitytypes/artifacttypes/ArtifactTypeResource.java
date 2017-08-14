/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources.entitytypes.artifacttypes;

import java.util.SortedSet;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.model.tosca.Namespaces;
import org.eclipse.winery.model.tosca.TArtifactType;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.datatypes.select2.Select2OptGroup;
import org.eclipse.winery.repository.resources.EntityTypeResource;

public class ArtifactTypeResource extends EntityTypeResource {

	private final QName qnameFileExtension = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "fileExtension");

	public ArtifactTypeResource(ArtifactTypeId id) {
		super(id);
	}

	/**
	 * @return the file extension associated with this artifact type. May be null
	 */
	@GET
	@Path("/fileextension")
	public String getAssociatedFileExtension() {
		return this.getDefinitions().getOtherAttributes().get(this.qnameFileExtension);
	}

	@PUT
	@Path("/fileextension")
	public Response setAssociatedFileExtension(String fileExtension) {
		this.getDefinitions().getOtherAttributes().put(this.qnameFileExtension, fileExtension);
		return BackendUtils.persist(this);
	}

	@Override
	protected TExtensibleElements createNewElement() {
		return new TArtifactType();
	}

	@Override
	public SortedSet<Select2OptGroup> getListOfAllInstances() {
		return this.getListOfAllInstances(ArtifactTemplateId.class);
	}
}
