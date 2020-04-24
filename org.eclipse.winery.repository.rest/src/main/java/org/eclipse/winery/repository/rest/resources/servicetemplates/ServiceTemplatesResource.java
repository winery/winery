/*******************************************************************************
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
package org.eclipse.winery.repository.rest.resources.servicetemplates;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TTag;
import org.eclipse.winery.model.tosca.TTags;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentInstanceResource;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentsWithoutTypeReferenceResource;
import org.eclipse.winery.repository.rest.resources._support.CreateFromArtifactApiData;

import io.swagger.annotations.Api;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Api(tags = "Service Templates")
public class ServiceTemplatesResource extends AbstractComponentsWithoutTypeReferenceResource<ServiceTemplateResource> {

    @GET
    @Path("createfromartifact")
    @Produces(MediaType.APPLICATION_JSON)
    public CreateFromArtifactApiData getCreateFromArtifactData() {
        Set<QName> artifactTypes = new HashSet<QName>();
        Set<QName> infrastructureNodeTypes = new HashSet<QName>();
        Collection<AbstractComponentInstanceResource> templates = this.getAll();

        for (AbstractComponentInstanceResource resource : templates) {
            if (resource instanceof ServiceTemplateResource) {
                ServiceTemplateResource stRes = (ServiceTemplateResource) resource;
                if (stRes.getServiceTemplate().getTags() != null) {
                    int check = 0;
                    QName artifactType = null;
                    for (TTag tag : stRes.getServiceTemplate().getTags().getTag()) {
                        switch (tag.getName()) {
                            case "xaasPackageNode":
                                check++;
                                break;
                            case "xaasPackageArtifactType":
                                check++;
                                artifactType = QName.valueOf(tag.getValue());
                                break;
                            case "xaasPackageDeploymentArtifact":
                                check++;
                                break;
                            case "xaasPackageInfrastructure":
                                // optional tag, hence no check++
                                infrastructureNodeTypes.add(QName.valueOf(tag.getValue()));
                            default:
                                break;
                        }
                    }
                    if (check == 3) {
                        artifactTypes.add(artifactType);
                    }
                }
            }
        }
        return new CreateFromArtifactApiData(artifactTypes, infrastructureNodeTypes);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createFromArtifact(@FormDataParam("file") InputStream uploadedInputStream,
                                       @FormDataParam("file") FormDataContentDisposition fileDetail,
                                       @FormDataParam("file") FormDataBodyPart body, 
                                       @FormDataParam("artifactType") QName artifactType,
                                       @FormDataParam("nodeTypes") List<FormDataBodyPart> nodeTypesList,
                                       @FormDataParam("infrastructureNodeType") QName infrastructureNodeType,
                                       @FormDataParam("tags") List<FormDataBodyPart> sentTagsList,
                                       @Context UriInfo uriInfo) throws IllegalArgumentException, JAXBException, IOException {
        Set<String> sentTags = new HashSet<>();

        if (sentTagsList != null) {

            for (FormDataBodyPart tag : sentTagsList) {
                sentTags.add(tag.getValue());
            }
        }

        Set<String> tags = RestUtils.clean(sentTags);

        Set<QName> nodeTypes = new HashSet<>();

        for (FormDataBodyPart nodetype : nodeTypesList) {
            nodeTypes.add(QName.valueOf(nodetype.getValue()));
        }

        nodeTypes = RestUtils.cleanQNameSet(nodeTypes);

        Collection<ServiceTemplateId> xaasPackages = this.getXaaSPackageTemplates(artifactType);
        Collection<ServiceTemplateId> toRemove = new ArrayList<ServiceTemplateId>();

        // check whether the serviceTemplate contains all the given nodeTypes
        for (ServiceTemplateId serviceTemplate : xaasPackages) {
            if (!RestUtils.containsNodeTypes(new ServiceTemplateResource(serviceTemplate).getServiceTemplate(), nodeTypes) | !RestUtils.containsTags(new ServiceTemplateResource(serviceTemplate).getServiceTemplate(), tags)) {
                toRemove.add(serviceTemplate);
                continue;
            }
            if (infrastructureNodeType != null && !infrastructureNodeType.getLocalPart().equals("undefined")) {
                if (RestUtils.getTagValue(new ServiceTemplateResource(serviceTemplate).getServiceTemplate(), "xaasPackageInfrastructure") == null) {
                    toRemove.add(serviceTemplate);
                } else {
                    String value = RestUtils.getTagValue(new ServiceTemplateResource(serviceTemplate).getServiceTemplate(), "xaasPackageInfrastructure");
                    String localName = value.split("}")[1];
                    String namespace = value.split("}")[0].substring(1);
                    if (!infrastructureNodeType.equals(new QName(namespace, localName))) {
                        toRemove.add(serviceTemplate);
                    }
                }
            }
        }

        xaasPackages.removeAll(toRemove);

        if (xaasPackages.size() <= 0) {
            return Response.serverError().entity("No suitable ServiceTemplate found for given artifact and configuration").build();
        }

        // take the first found serviceTemplate
        ServiceTemplateId serviceTemplate = xaasPackages.iterator().next();

        // create new name for the cloned sTemplate
        String newTemplateName = fileDetail.getFileName() + "ServiceTemplate";

        // create artifactTemplate for the uploaded artifact
        ArtifactTemplateId artifactTemplateId = RestUtils.createArtifactTemplate(uploadedInputStream, fileDetail, body, artifactType, uriInfo);

        // clone serviceTemplate
        ServiceTemplateId serviceTemplateId = RestUtils.cloneServiceTemplate(serviceTemplate, newTemplateName, fileDetail.getFileName());

        if (RestUtils.hasDA(serviceTemplateId, RestUtils.getTagValue(new ServiceTemplateResource(serviceTemplate).getServiceTemplate(), "xaasPackageNode"), RestUtils.getTagValue(new ServiceTemplateResource(serviceTemplate).getServiceTemplate(), "xaasPackageDeploymentArtifact"))) {

            // inject artifact as DA into cloned ServiceTemplate
            BackendUtils.injectArtifactTemplateIntoDeploymentArtifact(serviceTemplateId, RestUtils.getTagValue(new ServiceTemplateResource(serviceTemplate).getServiceTemplate(), "xaasPackageNode"), RestUtils.getTagValue(new ServiceTemplateResource(serviceTemplate).getServiceTemplate(), "xaasPackageDeploymentArtifact"), artifactTemplateId);
        } else {
            return Response.serverError().entity("Tagged DeploymentArtifact could not be found on given specified NodeTemplate").build();
        }

        URI absUri = RestUtils.getAbsoluteURI(serviceTemplateId);
        // http://localhost:8080/winery/servicetemplates/winery/servicetemplates/http%253A%252F%252Fopentosca.org%252Fservicetemplates/hs_err_pid13228.logServiceTemplate/
        // http://localhost:8080/winery/servicetemplates/winery/servicetemplates/http%253A%252F%252Fopentosca.org%252Fservicetemplates/java0.logServiceTemplate/
        String absUriString = absUri.toString().replace("/winery/servicetemplates", "");

        absUri = URI.create(absUriString);
        return Response.created(absUri).build();
    }

    private Collection<ServiceTemplateId> getXaaSPackageTemplates(QName artifactType) {
        Collection<ServiceTemplateId> xaasPackages = new ArrayList<ServiceTemplateId>();
        for (ServiceTemplateId serviceTemplate : this.getXaaSPackageTemplates()) {
            String artifactTypeTagValue = RestUtils.getTagValue(new ServiceTemplateResource(serviceTemplate).getServiceTemplate(), "xaasPackageArtifactType");
            QName taggedArtifactType = QName.valueOf(artifactTypeTagValue);
            if (taggedArtifactType.equals(artifactType)) {
                xaasPackages.add(serviceTemplate);
            }
        }
        return xaasPackages;
    }

    private Collection<ServiceTemplateId> getXaaSPackageTemplates() {
        Collection<AbstractComponentInstanceResource> templates = this.getAll();
        Collection<ServiceTemplateId> xaasPackages = new ArrayList<ServiceTemplateId>();
        for (AbstractComponentInstanceResource resource : templates) {
            if (resource instanceof ServiceTemplateResource) {
                ServiceTemplateResource stRes = (ServiceTemplateResource) resource;

                TTags tags = stRes.getServiceTemplate().getTags();

                if (tags == null) {
                    continue;
                }

                int check = 0;
                for (TTag tag : tags.getTag()) {
                    switch (tag.getName()) {
                        case "xaasPackageNode":
                        case "xaasPackageArtifactType":
                        case "xaasPackageDeploymentArtifact":
                            check++;
                            break;
                        default:
                            break;
                    }
                }
                if (check == 3) {
                    xaasPackages.add((ServiceTemplateId) stRes.getId());
                }
            }
        }
        return xaasPackages;
    }

    @Path("{namespace}/{id}/")
    public ServiceTemplateResource getComponentInstanceResource(@PathParam("namespace") String namespace, @PathParam("id") String id) {
        return this.getComponentInstanceResource(namespace, id, true);
    }
}
