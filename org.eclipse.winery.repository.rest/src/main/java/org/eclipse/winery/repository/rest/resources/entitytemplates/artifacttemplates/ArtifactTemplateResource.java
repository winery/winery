/********************************************************************************
 * Copyright (c) 2012-2018 Contributors to the Eclipse Foundation
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

import io.swagger.annotations.ApiParam;
import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.constants.MimeTypes;
import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.tosca.HasType;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateFilesDirectoryId;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateSourceDirectoryId;
import org.eclipse.winery.repository.datatypes.ids.elements.DirectoryId;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentInstanceWithReferencesResource;
import org.eclipse.winery.repository.rest.resources._support.IHasName;
import org.eclipse.winery.repository.rest.resources.apiData.ArtifactResourcesApiData;
import org.eclipse.winery.repository.rest.resources.entitytemplates.IEntityTemplateResource;
import org.eclipse.winery.repository.rest.resources.entitytemplates.PropertiesResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Models an Artifact Template with its artifact references
 * <p>
 * The associated files (through tArtifactReference) are stored directly within this resource. The element
 * <ArtifactReference> is generated during export only
 * <p>
 * This class inherits from AbstractComponentInstanceResourceDefinitionsBacked and not from
 * TEntityTemplateResource<TArtifactTemplate>, because ArtifactTemplates are directly available under TDefinitions and
 * we need the generic resource handling
 */

public class ArtifactTemplateResource extends AbstractComponentInstanceWithReferencesResource implements IEntityTemplateResource<TArtifactTemplate>, IHasName {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactTemplateResource.class);

    private final ArtifactTemplateFilesDirectoryId filesDirectoryId;
    private final ArtifactTemplateSourceDirectoryId sourceDirectoryId;

    public ArtifactTemplateResource(ArtifactTemplateId id) {
        super(id);
        this.filesDirectoryId = new ArtifactTemplateFilesDirectoryId(id);
        this.sourceDirectoryId = new ArtifactTemplateSourceDirectoryId(id);
    }

    private TArtifactTemplate getTArtifactTemplate() {
        return (TArtifactTemplate) this.getElement();
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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response copySourceToFilesResource(@ApiParam(value = "if data contains a non-empty array than only the files" +
        " whose names are included are copied ", required = true) ArtifactResourcesApiData data) {
        List<String> artifactList = data.getArtifactNames();
        DirectoryId sourceDir = new ArtifactTemplateSourceDirectoryId((ArtifactTemplateId) this.id);
        FilesResource filesResource = getFilesResource();
        for (RepositoryFileReference ref : RepositoryFactory.getRepository().getContainedFiles(sourceDir)) {
            if (artifactList == null || artifactList.contains(ref.getFileName())) {
                try (InputStream inputStream = RepositoryFactory.getRepository().newInputStream(ref)) {
                    String fileName = ref.getFileName();
                    String subDirectory = ref.getSubDirectory().map(s -> s.toString()).orElse("");
                    filesResource.putFile(fileName, subDirectory, inputStream);
                } catch (IOException e) {
                    LOGGER.debug("The artifact source " + ref.getFileName() + " could not be copied to the files directory.", e);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                }
            }
        }
        return Response.status(Status.CREATED).build();
    }

    @Path("files/")
    public FilesResource getFilesResource() {
        return new FilesResource(this.filesDirectoryId);
    }

    @GET
    @Path("files/zip")
    @Produces(MimeTypes.MIMETYPE_ZIP)
    public Response getFilesDefinitionsAsResponse() {
        return RestUtils.getZippedContents(this.filesDirectoryId);
    }

    @Path("source/")
    public FilesResource getSrcResource() {
        return new FilesResource(this.sourceDirectoryId);
    }

    @GET
    @Path("source/zip")
    @Produces(MimeTypes.MIMETYPE_ZIP)
    public Response getSourceDefinitionsAsResponse() {
        return RestUtils.getZippedContents(this.sourceDirectoryId, this.sourceDirectoryId.getParent().getXmlId().getEncoded() + "-source.zip");
    }

    @Override
    public PropertiesResource getPropertiesResource() {
        return new PropertiesResource(this.getTArtifactTemplate(), this);
    }

    /**
     * TODO: This method should be moved to the probably can be moved up the type hierarchy and somehow be meregd with
     * the functionality to get referenced templates. Currently, it is only required here by the topology modeler.
     *
     * @return the type of the artifact template OR the number of references pointing to this resource
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getReferenceCount(
        @QueryParam("referenceCount") @ApiParam("Determines the number of elements known by the repository which point to this resource.") String referenceCount,
        @QueryParam("type") @ApiParam("Type of the artifact template") String type) {
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
