/*******************************************************************************
 * Copyright (c) 2012-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.servicetemplates.topologytemplates;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.IdNames;
import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.common.version.WineryVersion;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.constants.Namespaces;
import org.eclipse.winery.model.tosca.constants.OpenToscaBaseTypes;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.datatypes.ids.elements.DirectoryId;
import org.eclipse.winery.repository.datatypes.ids.elements.GenericDirectoryId;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.GenericFileResource;
import org.eclipse.winery.repository.rest.resources._support.INodeTemplateResourceOrNodeTypeImplementationResource;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.repository.rest.resources._support.collections.IIdDetermination;
import org.eclipse.winery.repository.rest.resources.apiData.QNameWithTypeApiData;
import org.eclipse.winery.repository.rest.resources.artifacts.DeploymentArtifactsResource;
import org.eclipse.winery.repository.rest.resources.entitytemplates.TEntityTemplateResource;
import org.eclipse.winery.repository.rest.resources.entitytemplates.artifacttemplates.ArtifactTemplateResource;
import org.eclipse.winery.repository.rest.resources.entitytemplates.artifacttemplates.ArtifactTemplatesResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.ServiceTemplateResource;

import io.swagger.annotations.ApiOperation;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeTemplateResource extends TEntityTemplateResource<TNodeTemplate> implements INodeTemplateResourceOrNodeTypeImplementationResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeTemplateResource.class);

    private final QName qnameX = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "x");
    private final QName qnameY = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "y");
    private final TNodeTemplate nodeTemplate;

    public NodeTemplateResource(IIdDetermination<TNodeTemplate> idDetermination, TNodeTemplate o, int idx, List<TNodeTemplate> list, IPersistable res) {
        super(idDetermination, o, idx, list, res);
        this.nodeTemplate = o;
    }

    @Path("deploymentartifacts/")
    public DeploymentArtifactsResource getDeploymentArtifacts() {
        return new DeploymentArtifactsResource(this.o, this);
    }

    @Path("yamlartifacts/{artifactId}")
    public GenericFileResource postYamlArtifactFile(@PathParam("artifactId") String id) {
        DirectoryId serviceTemplateYamlArtifactsDir =
            new GenericDirectoryId(getServiceTemplateResource().getId(), IdNames.FILES_DIRECTORY);
        DirectoryId nodeTemplateYamlArtifactsDir =
            new GenericDirectoryId(serviceTemplateYamlArtifactsDir, nodeTemplate.getId());
        DirectoryId yamlArtifactFilesDirectoryId =
            new GenericDirectoryId(nodeTemplateYamlArtifactsDir, id);

        return new GenericFileResource(yamlArtifactFilesDirectoryId);
    }

    // The following methods are currently *not* used by the topology modeler. The modeler is using the repository client to interact with the repository

    @GET
    @Path("minInstances")
    public String getMinInstances() {
        return Integer.toString(this.o.getMinInstances());
    }

    @PUT
    @Path("minInstances")
    public Response setMinInstances(@FormParam(value = "minInstances") String minInstances) {
        int min = Integer.parseInt(minInstances);
        this.o.setMinInstances(min);
        return RestUtils.persist(this.res);
    }

    @GET
    @Path("maxInstances")
    public String getMaxInstances() {
        return this.o.getMaxInstances();
    }

    @PUT
    @Path("maxInstances")
    public Response setMaxInstances(@FormParam(value = "maxInstances") String maxInstances) {
        // TODO: check for valid integer | "unbound"
        this.o.setMaxInstances(maxInstances);
        return RestUtils.persist(this.res);
    }


    /* * *
     * The visual appearance
     *
     * We do not use a subresource "visualappearance" here to avoid generation of more objects
     * * */

    @Path("x")
    @GET
    @ApiOperation(value = "@return the x coordinate of the node template")
    public String getX() {
        Map<QName, String> otherAttributes = this.o.getOtherAttributes();
        return otherAttributes.get(this.qnameX);
    }

    @Path("x")
    @PUT
    public Response setX(String x) {
        this.o.getOtherAttributes().put(this.qnameX, x);
        return RestUtils.persist(this.res);
    }

    @Path("y")
    @GET
    @ApiOperation(value = "@return the y coordinate of the node template")
    public String getY() {
        Map<QName, String> otherAttributes = this.o.getOtherAttributes();
        return otherAttributes.get(this.qnameY);
    }

    @Path("y")
    @PUT
    public Response setY(String y) {
        this.o.getOtherAttributes().put(this.qnameY, y);
        return RestUtils.persist(this.res);
    }

    @Override
    public Namespace getNamespace() {
        // TODO Auto-generated method stub
        throw new IllegalStateException("Not yet implemented.");
    }

    @POST
    @Path("state")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createStateElement(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file")
        FormDataContentDisposition fileDetail, @FormDataParam("file") FormDataBodyPart body, @Context UriInfo uriInfo) {
        LOGGER.debug("Received state artifact for Node Template {} with ID {}", this.nodeTemplate.getName(), this.nodeTemplate.getId());
        LOGGER.debug("Artifact file name is {} and is {} bytes big.", fileDetail.getFileName(), fileDetail.getSize());

        // ensure that the artifact type exists.
        IRepository repo = RepositoryFactory.getRepository();
        repo.getElement(new ArtifactTypeId(OpenToscaBaseTypes.stateArtifactType));

        // create DA
        Optional<TDeploymentArtifact> stateDeploymentArtifact = this.getDeploymentArtifacts().getDeploymentArtifacts().stream()
            .filter(artifact -> artifact.getArtifactType().equals(OpenToscaBaseTypes.stateArtifactType))
            .findFirst();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");

        TDeploymentArtifact deploymentArtifact = new TDeploymentArtifact();
        deploymentArtifact.setArtifactType(OpenToscaBaseTypes.stateArtifactType);
        deploymentArtifact.setName("state");

        String componentVersion = dateFormat.format(new Date());
        ArtifactTemplateId newArtifactTemplateId = new ArtifactTemplateId(
            "http://opentosca.org/artifacttemplates",
            this.getServiceTemplateResource().getServiceTemplate().getName() + "-" + this.nodeTemplate.getId() + "-State"
                + WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR + componentVersion
                + WineryVersion.WINERY_VERSION_SEPARATOR + WineryVersion.WINERY_VERSION_PREFIX + "1"
            , false
        );
        LOGGER.debug("Created Artifact Template of Type \"State\" called {}", newArtifactTemplateId.getQName());

        // if there is already a state artifact, update the file
        if (stateDeploymentArtifact.isPresent()) {
            LOGGER.debug("Updating the state DA of the Node Template...");
            deploymentArtifact = stateDeploymentArtifact.get();

            // create new ArtifactTemplate version
            ArtifactTemplateId oldArtifactTemplateId = new ArtifactTemplateId(deploymentArtifact.getArtifactRef());
            List<WineryVersion> versions = BackendUtils.getAllVersionsOfOneDefinition(oldArtifactTemplateId);
            WineryVersion newWineryVersion = VersionUtils.getNewWineryVersion(versions);
            newWineryVersion.setWorkInProgressVersion(0);
            newWineryVersion.setComponentVersion(componentVersion);

            newArtifactTemplateId = (ArtifactTemplateId) VersionUtils.getDefinitionInTheGivenVersion(
                oldArtifactTemplateId,
                newWineryVersion
            );
        } else {
            LOGGER.debug("Creating the state DA of the Node Template...");
            TDeploymentArtifacts list = this.nodeTemplate.getDeploymentArtifacts();
            if (Objects.isNull(list)) {
                list = new TDeploymentArtifacts();
                this.nodeTemplate.setDeploymentArtifacts(list);
            }

            list.getDeploymentArtifact().add(deploymentArtifact);
        }

        new ArtifactTemplatesResource()
            .onJsonPost(new QNameWithTypeApiData(
                newArtifactTemplateId.getQName().getLocalPart(),
                newArtifactTemplateId.getQName().getNamespaceURI(),
                OpenToscaBaseTypes.stateArtifactType.toString()
            ));

        LOGGER.debug("Attaching the new Artifact...");
        deploymentArtifact.setArtifactRef(newArtifactTemplateId.getQName());

        Response response = new ArtifactTemplateResource(newArtifactTemplateId)
            .getFilesResource()
            .onPost(uploadedInputStream, fileDetail, body, uriInfo, this.nodeTemplate.getId() + ".state");

        if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
            LOGGER.debug("Could not create artifact file! Response was {}", response);
            return response;
        }

        LOGGER.debug("Persisting now...");
        return RestUtils.persist(this.res);
    }

    /**
     * Required for persistence after a change of the deployment artifact. Required by DeploymentArtifactResource to be
     * able to persist
     *
     * @return the service template this node template belongs to
     */
    public ServiceTemplateResource getServiceTemplateResource() {
        return (ServiceTemplateResource) this.res;
    }

    /**
     * required for topology modeler to check for existence of a node template at the server
     *
     * @return empty response
     */
    @HEAD
    public Response getHEAD() {
        return Response.noContent().build();
    }
}
