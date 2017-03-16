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
 *     Tino Stadelmaier, Philipp Meyer - rename for id/namespace
 *******************************************************************************/
package org.eclipse.winery.repository.resources.imports.genericimports;

import java.util.SortedSet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.common.ids.definitions.imports.GenericImportId;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TImport;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.resources.AbstractComponentInstanceResource;

public class GenericImportResource extends AbstractComponentInstanceResource {

	// The import belonging to this resource
	protected final TImport theImport;


	public GenericImportResource(GenericImportId id) {
		super(id);

		boolean needsPersistence = false;

		if (this.getDefinitions().getServiceTemplateOrNodeTypeOrNodeTypeImplementation().isEmpty()) {
			// super class loaded an existing definitions

			// we have to manually assign our import right
			this.theImport = this.getDefinitions().getImport().get(0);

			// element is not assigned as there are no service templates/...
			// we assign the value to be sure that no NPEs occur
			this.element = this.theImport;
		} else {
			// super class created a new import

			// store it locally
			this.theImport = (TImport) this.element;

			// undo the side effect of adding it at the wrong place at TDefinitions
			this.getDefinitions().getServiceTemplateOrNodeTypeOrNodeTypeImplementation().clear();

			// add import at the right place
			this.getDefinitions().getImport().add(this.theImport);

			// Super class has persisted the definitions
			// We have to persist the new variant
			needsPersistence = true;
		}

		if (this.theImport.getLocation() == null) {
			// invalid import -- try to synchronize with storage

			SortedSet<RepositoryFileReference> containedFiles = Repository.INSTANCE.getContainedFiles(id);
			// there is also a .definitions contained
			// we are only interested in the non-.definitions
			for (RepositoryFileReference ref : containedFiles) {
				if (!ref.getFileName().endsWith(".definitions")) {
					// associated file found
					// set the filename of the import to the found xsd
					// TODO: no more validity checks are done currently. In the case of XSD: targetNamespace matches, not more than one xsd
					this.theImport.setLocation(ref.getFileName());
					needsPersistence = true;
					break;
				}
			}
		}

		if (needsPersistence) {
			BackendUtils.persist(this);
		}
	}

	@Override
	protected TExtensibleElements createNewElement() {
		throw new IllegalStateException("This should not never happen.");
	}

	@Override
	public void copyIdToFields(TOSCAComponentId id) {
		// this.theImport cannot be used as this method is called by the super constructor
		((TImport) this.element).setNamespace(id.getNamespace().getDecoded());
	}

	@GET
	@Path("{filename}")
	public Response getFile(@PathParam("filename") String fileName) {
		fileName = Util.URLdecode(fileName);
		String location;
		if ((location = this.getLocation()) == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		if (!location.equals(fileName)) {
			return Response.status(Status.NOT_FOUND).build();
		}
		RepositoryFileReference ref = new RepositoryFileReference(this.id, location);
		return BackendUtils.returnRepoPath(ref, null);

	}

	public String getLocation() {
		return this.theImport.getLocation();
	}

	/**
	 * @return a name suitable for componentnaming.jspf
	 */
	public String getName() {
		if (this.getLocation() == null) {
			return this.id.getXmlId().getDecoded();
		} else {
			return this.getLocation();
		}
	}

}
