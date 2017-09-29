/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *     Tino Stadelmaier, Philipp Meyer - rename for id/namespace
 *     Philipp Meyer - support for src directory
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.entitytemplates.artifacttemplates;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.common.constants.MimeTypes;
import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.tosca.HasType;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateDirectoryId;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateFilesDirectoryId;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateSourceDirectoryId;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.AbstractComponentInstanceWithReferencesResource;
import org.eclipse.winery.repository.rest.resources.IHasName;
import org.eclipse.winery.repository.rest.resources.entitytemplates.IEntityTemplateResource;
import org.eclipse.winery.repository.rest.resources.entitytemplates.PropertiesResource;

/**
 * Models an Artifact Template with its artifact references
 *
 * The associated files (through tArtifactReference) are stored directly within this resource. The element
 * <ArtifactReference> is generated during export only
 *
 * This class inherits from AbstractComponentInstanceResourceDefinitionsBacked and not from
 * TEntityTemplateResource<TArtifactTemplate>, because ArtifactTemplates are directly available under TDefinitions and
 * we need the generic resource handling
 */

public class ArtifactTemplateResource extends AbstractComponentInstanceWithReferencesResource implements IEntityTemplateResource<TArtifactTemplate>, IHasName {


	public ArtifactTemplateResource(ArtifactTemplateId id) {
		super(id);
	}

	private TArtifactTemplate getTArtifactTemplate() {
		return (TArtifactTemplate) this.element;
	}

	@Override
	public String getName() {
		String name = this.getTArtifactTemplate().getName();
		if (name == null) {
			return this.getTArtifactTemplate().getId();
		} else {
			return name;
		}
	}

	@Override
	public Response setName(String name) {
		this.getTArtifactTemplate().setName(name);
		return RestUtils.persist(this);
	}

	@Override
	protected TExtensibleElements createNewElement() {
		return new TArtifactTemplate();
	}

	public void synchronizeReferences() throws IOException {
		BackendUtils.synchronizeReferences((ArtifactTemplateId) this.id);
	}

	@Path("files/")
	public FilesResource getFilesResource() {
		ArtifactTemplateDirectoryId fileDir = new ArtifactTemplateFilesDirectoryId((ArtifactTemplateId) this.id);
		return new FilesResource(fileDir);
	}

	@GET
	@Path("files/zip")
	@Produces(MimeTypes.MIMETYPE_ZIP)
	public Response getFilesDefinitionsAsResponse() {
		ArtifactTemplateDirectoryId fileDir = new ArtifactTemplateFilesDirectoryId((ArtifactTemplateId) this.id);
		return RestUtils.getZippedContents(fileDir);
	}

	@Path("source/")
	public FilesResource getSrcResource() {
		ArtifactTemplateDirectoryId fileDir = new ArtifactTemplateSourceDirectoryId((ArtifactTemplateId) this.id);
		return new FilesResource(fileDir);
	}

	@GET
	@Path("source/zip")
	@Produces(MimeTypes.MIMETYPE_ZIP)
	public Response getSourceDefinitionsAsResponse() {
		ArtifactTemplateDirectoryId fileDir = new ArtifactTemplateSourceDirectoryId((ArtifactTemplateId) this.id);
		return RestUtils.getZippedContents(fileDir, fileDir.getParent().getXmlId().getEncoded() + "-source.zip");
	}

	@Override
	public PropertiesResource getPropertiesResource() {
		return new PropertiesResource(this.getTArtifactTemplate(), this);
	}

	/**
	 * Query parameter {@code type}:<br /> Returns the type of the artifact template
	 *
	 * Query parameter {@code referenceCount}:<br /> Determines the number of elements known by the repository which
	 * point to this resource. This method probably can be moved up the type hierarchy. Currently, it is only required
	 * here by the topology modeler.
	 *
	 * @return the type of the artifact template OR the number of references pointing to this resource
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response getReferenceCount(@QueryParam("referenceCount") String referenceCount, @QueryParam("type") String type) {
		if (referenceCount != null) {
			String res = Integer.toString(RepositoryFactory.getRepository().getReferenceCount((ArtifactTemplateId) this.id));
			return Response.ok().entity(res).build();
		} else if (type != null) {
			String res = ((HasType) this.getElement()).getTypeAsQName().toString();
			return Response.ok().entity(res).build();
		} else {
			// we enforce the query parameter to be extensible to other queries
			return Response.status(Status.BAD_REQUEST).entity("You have to pass the query parameter referenceCount or type").build();
		}
	}

	/* not yet implemented */
	/*
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getReferenes(@QueryParam("references") String references) {
		if (references== null) {
			// we enforce the query parameter to be extensible to other queries
			return Response.status(Status.BAD_REQUEST).entity("You have to pass the query parameter references").build();
		}

		String res = Integer.toString(this.getReferenceCount());
		return Response.ok().entity(res).build();
	}
	*/
}
