/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *     Kálmán Képes - support for tags
 *******************************************************************************/
package org.eclipse.winery.repository.resources.servicetemplates;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
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
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.resources.AbstractComponentInstanceResource;
import org.eclipse.winery.repository.resources.AbstractComponentsResource;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

public class ServiceTemplatesResource extends AbstractComponentsResource<ServiceTemplateResource> {

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response createFromArtefact(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("file") FormDataBodyPart body, @FormDataParam("artefactType") QName artifactType, @FormDataParam("nodeTypes") Set<QName> nodeTypes, @FormDataParam("infrastructureNodeType") QName infrastructureNodeType, @FormDataParam("tags") Set<String> tags, @Context UriInfo uriInfo) throws IllegalArgumentException, JAXBException, IOException {

		tags = Utils.clean(tags);
		nodeTypes = Utils.cleanQNameSet(nodeTypes);

		Collection<ServiceTemplateId> xaasPackages = this.getXaaSPackageTemplates(artifactType);
		Collection<ServiceTemplateId> toRemove = new ArrayList<ServiceTemplateId>();

		// check whether the serviceTemplate contains all the given nodeTypes
		for (ServiceTemplateId serviceTemplate : xaasPackages) {
			if (!Utils.containsNodeTypes(new ServiceTemplateResource(serviceTemplate).getServiceTemplate(), nodeTypes) | !Utils.containsTags(new ServiceTemplateResource(serviceTemplate).getServiceTemplate(), tags)) {
				toRemove.add(serviceTemplate);
				continue;
			}
			if (infrastructureNodeType != null) {
				if (Utils.getTagValue(new ServiceTemplateResource(serviceTemplate).getServiceTemplate(), "xaasPackageInfrastructure") == null) {
					toRemove.add(serviceTemplate);
					continue;
				} else {
					String value = Utils.getTagValue(new ServiceTemplateResource(serviceTemplate).getServiceTemplate(), "xaasPackageInfrastructure");
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
			return Response.serverError().entity("No suitable ServiceTemplate found for given artefact and configuration").build();
		}

		// take the first found serviceTemplate
		ServiceTemplateId serviceTemplate = xaasPackages.iterator().next();

		// create new name for the cloned sTemplate
		String newTemplateName = fileDetail.getFileName() + "ServiceTemplate";

		// create artefactTemplate for the uploaded artefact
		ArtifactTemplateId artefactTemplateId = Utils.createArtefactTemplate(uploadedInputStream, fileDetail, body, artifactType, uriInfo);

		// clone serviceTemplate
		ServiceTemplateId serviceTemplateId = Utils.cloneServiceTemplate(serviceTemplate, newTemplateName, fileDetail.getFileName());

		if (Utils.hasDA(serviceTemplateId, Utils.getTagValue(new ServiceTemplateResource(serviceTemplate).getServiceTemplate(), "xaasPackageNode"), Utils.getTagValue(new ServiceTemplateResource(serviceTemplate).getServiceTemplate(), "xaasPackageDeploymentArtefact"))) {

			// inject artefact as DA into cloned ServiceTemplate
			Utils.injectArtefactTemplateIntoDeploymentArtefact(serviceTemplateId, Utils.getTagValue(new ServiceTemplateResource(serviceTemplate).getServiceTemplate(), "xaasPackageNode"), Utils.getTagValue(new ServiceTemplateResource(serviceTemplate).getServiceTemplate(), "xaasPackageDeploymentArtefact"), artefactTemplateId);
		} else {
			return Response.serverError().entity("Tagged DeploymentArtefact couldn't be found on given specified NodeTemplate").build();
		}

		URI absUri = Utils.getAbsoluteURI(serviceTemplateId);
		// http://localhost:8080/winery/servicetemplates/winery/servicetemplates/http%253A%252F%252Fopentosca.org%252Fservicetemplates/hs_err_pid13228.logServiceTemplate/
		// http://localhost:8080/winery/servicetemplates/winery/servicetemplates/http%253A%252F%252Fopentosca.org%252Fservicetemplates/java0.logServiceTemplate/
		String absUriString = absUri.toString().replace("/winery/servicetemplates", "");

		absUri = URI.create(absUriString);
		return Response.created(absUri).build();
	}

	private Collection<ServiceTemplateId> getXaaSPackageTemplates(QName artefactType) {
		Collection<ServiceTemplateId> xaasPackages = new ArrayList<ServiceTemplateId>();
		for (ServiceTemplateId serviceTemplate : this.getXaaSPackageTemplates()) {
			String artefactTypeTagValue = Utils.getTagValue(new ServiceTemplateResource(serviceTemplate).getServiceTemplate(), "xaasPackageArtefactType");
			QName taggedArtefactType = QName.valueOf(artefactTypeTagValue);
			if (taggedArtefactType.equals(artefactType)) {
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
					case "xaasPackageArtefactType":
					case "xaasPackageDeploymentArtefact":
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

}
