/*******************************************************************************
 * Copyright (c) 2021-2023 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.edmm;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.configuration.Environments;
import org.eclipse.winery.common.constants.MimeTypes;
import org.eclipse.winery.edmm.EdmmManager;
import org.eclipse.winery.edmm.TransformationManager;
import org.eclipse.winery.edmm.model.EdmmType;
import org.eclipse.winery.edmm.plugins.PluginManager;
import org.eclipse.winery.model.ids.EncodingUtil;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.apiData.GenerateArtifactApiData;
import org.eclipse.winery.repository.rest.resources.apiData.QNameWithTypeApiData;
import org.eclipse.winery.repository.rest.resources.entitytemplates.artifacttemplates.ArtifactTemplateResource;
import org.eclipse.winery.repository.rest.resources.entitytemplates.artifacttemplates.ArtifactTemplatesResource;
import org.eclipse.winery.repository.rest.resources.entitytypeimplementations.nodetypeimplementations.NodeTypeImplementationResource;
import org.eclipse.winery.repository.rest.resources.entitytypeimplementations.nodetypeimplementations.NodeTypeImplementationsResource;
import org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes.NodeTypeResource;
import org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes.NodeTypesResource;

import io.github.edmm.core.DeploymentTechnology;
import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.support.GraphNormalizer;
import io.github.edmm.core.plugin.PluginService;
import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.PluginSupportResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * This class manages the API for the EDMM functionalities like the Decision Support System and the Transformation
 * Framework
 */
public class EdmmResource {

    private static final String NAMESPACE = "https://edmm.uni-stuttgart.de/";
    private static final String NODE_TYPES = NAMESPACE + "nodetypes";
    private static final String ARTIFACT_TEMPLATES = NAMESPACE + "artifacttemplates";
    private static final String ARTIFACT_TYPES = NAMESPACE + "artifacttypes";
    private static final String NODE_TYPE_IMPLEMENTATIONS = NAMESPACE + "nodetypeimplementations";
    private static final String LIFECYCLE_NAME = "http://opentosca.org/interfaces/lifecycle";
    private static final String[] LIFECYCLE = {"create", "configure", "start", "stop", "delete"};

    private final TTopologyTemplate element;

    public EdmmResource(TServiceTemplate element) {
        this(element.getTopologyTemplate());
    }

    public EdmmResource(TTopologyTemplate element) {
        this.element = element;
    }

    @GET
    @Path("export")
    @Produces()
    public Response exportEdmm(@QueryParam(value = "edmmUseAbsolutePaths") String edmmUseAbsolutePaths) {
        return RestUtils.getEdmmModel(this.element, edmmUseAbsolutePaths != null);
    }

    @GET
    @Path("supportedTechnologies")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DeploymentTechnology> getPlugins() {
        return PluginManager.getInstance().getPluginsList().stream()
            .map(TransformationPlugin::getDeploymentTechnology)
            .collect(Collectors.toList());
    }

    @GET
    @Path("check-model-support")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkModelSupport() {

        EntityGraph graph = RestUtils.getEdmmEntityGraph(this.element, true);
        GraphNormalizer.normalize(graph);

        PluginService pluginService = PluginManager.getInstance()
            .getPluginService();
        // getting the model from the graph
        DeploymentModel model = new DeploymentModel(UUID.randomUUID().toString(), graph);
        List<PluginSupportResult> result = pluginService.checkModelSupport(model);

        return Response.ok()
            .type(MediaType.APPLICATION_JSON)
            .entity(result)
            .build();
    }

    @GET
    @Path("transform")
    @Produces(MimeTypes.MIMETYPE_ZIP)
    public Response transform(@QueryParam(value = "target") String target) {

        EntityGraph graph = RestUtils.getEdmmEntityGraph(this.element, false);
        GraphNormalizer.normalize(graph);

        String wineryRepository = Environments.getInstance()
            .getRepositoryConfig()
            .getRepositoryRoot();
        String filename;
        byte[] response;

        try {
            // the transform command applies the transformation towards the specific target deployment files
            // and return a zip of them
            TransformationManager transformationManager = new TransformationManager();
            File zipFile = transformationManager.transform(graph, target, wineryRepository);
            filename = zipFile.getName();
            response = FileUtils.readFileToByteArray(zipFile);
        } catch (Exception e) {
            // we send back a Server Error
            String message = String.format(
                "<html><body>" +
                    "<div> %s </div>" +
                    "<div> Probably something is missing in the service template XML or something went wrong on the server </div>" +
                    "</body></html>",
                e
            );
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.TEXT_HTML)
                .entity(message)
                .build();
        }

        return Response.ok()
            .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
            .type(MimeTypes.MIMETYPE_ZIP)
            .entity(response)
            .build();
    }

    @GET
    @Path("one-to-one-map")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOneToOneMap() {
        IRepository repository = RepositoryFactory.getRepository();
        EdmmManager edmmManager = EdmmManager.forRepository(repository);
        Map<QName, EdmmType> oneToOneMap = edmmManager.getOneToOneMap();

        Map<String, String> reverseOneToOneMap = new HashMap<>();
        for (Map.Entry<QName, EdmmType> entry : oneToOneMap.entrySet()) {
            EdmmType edmmType = entry.getValue();
            reverseOneToOneMap.put(edmmType.getValue(), entry.getKey().getLocalPart());
        }

        return Response.ok()
            .type(MediaType.APPLICATION_JSON)
            .entity(reverseOneToOneMap)
            .build();
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("create-placeholders-scripts")
    public Response createPlaceholders(String componentType, @Context UriInfo uriInfo, @Context HttpServletResponse response) throws Exception {

        // adding the interface to the component node type
        NodeTypeResource nodeTypeResource = new NodeTypesResource().getComponentInstanceResource(EncodingUtil.URLencode(NODE_TYPES), componentType);
        // the interfaces will be overridden
        nodeTypeResource.getInterfaces()
            .onPost(buildInterfacesForNodeType(LIFECYCLE_NAME, LIFECYCLE));

        for (String operation : LIFECYCLE) {
            // adding a new artifact template
            final String artifactLocalName = componentType + "-" + StringUtils.capitalize(operation);
            ArtifactTemplatesResource artifactTemplatesResource = new ArtifactTemplatesResource();
            QNameWithTypeApiData qNameWithTypeApiData = new QNameWithTypeApiData(
                artifactLocalName,
                ARTIFACT_TEMPLATES,
                "{" + ARTIFACT_TYPES + "}Script"
            );
            artifactTemplatesResource.onJsonPost(qNameWithTypeApiData);

            // adding the placeholder file
            ArtifactTemplateResource artifactTemplateResource = artifactTemplatesResource.getComponentInstanceResource(EncodingUtil.URLencode(ARTIFACT_TEMPLATES), artifactLocalName);
            boolean fileExists = artifactTemplateResource.getFilesResource()
                .getAllFileMetas()
                .stream()
                .anyMatch(fileMeta -> fileMeta.getName().equals(operation + ".sh"));
            // if the file exists it will be overridden, but we do not want that.
            if (!fileExists) {
                artifactTemplateResource.getFilesResource()
                    .putContentToFile(operation + ".sh", getPlaceholderFile(operation), "text/x-sh");

                artifactTemplateResource.synchronizeReferences();
            }

            // creating a new node type implementation
            String nodeTypeImplementationLocalName = componentType + "-IA";

            NodeTypeImplementationsResource nodeTypeImplementationsResource = new NodeTypeImplementationsResource();
            qNameWithTypeApiData = new QNameWithTypeApiData(
                nodeTypeImplementationLocalName,
                NODE_TYPE_IMPLEMENTATIONS,
                "{" + NODE_TYPES + "}" + componentType
            );
            nodeTypeImplementationsResource.onJsonPost(qNameWithTypeApiData);

            // linking the artifact template to the node type implementation
            NodeTypeImplementationResource nodeTypeImplementationResource = nodeTypeImplementationsResource
                .getComponentInstanceResource(EncodingUtil.URLencode(NODE_TYPE_IMPLEMENTATIONS), nodeTypeImplementationLocalName);

            GenerateArtifactApiData artifactApiData = new GenerateArtifactApiData();
            artifactApiData.artifactName = operation;
            artifactApiData.artifactTemplate = "{" + ARTIFACT_TEMPLATES + "}" + artifactLocalName;
            artifactApiData.interfaceName = LIFECYCLE_NAME;
            artifactApiData.operationName = operation;

            boolean implementationResourceExists = nodeTypeImplementationResource.getImplementationArtifacts()
                .getAllArtifactResources()
                .stream()
                .anyMatch(artifactResource -> {
                    TImplementationArtifact implementationArtifact = artifactResource.getImplementationArtifact();

                    return operation.equals(implementationArtifact.getOperationName()) &&
                        LIFECYCLE_NAME.equals(implementationArtifact.getInterfaceName()) &&
                        implementationArtifact.getArtifactRef() != null &&
                        artifactApiData.artifactTemplate.equals(implementationArtifact.getArtifactRef().toString());
                });
            if (!implementationResourceExists) {
                nodeTypeImplementationResource.getImplementationArtifacts().generateArtifact(artifactApiData, uriInfo, response);
            }
        }
        return Response.status(Response.Status.CREATED).build();
    }

    private List<TInterface> buildInterfacesForNodeType(String interfaceName, String[] operations) {
        List<TOperation> tOperations = new ArrayList<>();

        for (String op : operations) {
            TOperation tOperation = new TOperation.Builder(op).build();
            tOperations.add(tOperation);
        }

        TInterface lifecycleInterface = new TInterface.Builder(interfaceName, tOperations).build();
        List<TInterface> interfaces = new ArrayList<>();
        interfaces.add(lifecycleInterface);
        return interfaces;
    }

    private InputStream getPlaceholderFile(String operation) {
        return new ByteArrayInputStream("#!/bin/bash".getBytes(StandardCharsets.UTF_8));
    }
}
