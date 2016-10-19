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
package org.eclipse.winery.repository.resources.servicetemplates;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.constants.MimeTypes;
import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTag;
import org.eclipse.winery.model.tosca.TTags;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.backend.filebased.FilebasedRepository;
import org.eclipse.winery.repository.importing.CSARImporter;
import org.eclipse.winery.repository.resources.AbstractComponentInstanceResource;
import org.eclipse.winery.repository.resources.AbstractComponentsResource;
import org.eclipse.winery.repository.resources.entitytemplates.artifacttemplates.ArtifactTemplateResource;
import org.eclipse.winery.repository.resources.entitytemplates.artifacttemplates.ArtifactTemplatesResource;
import org.eclipse.winery.repository.resources.entitytemplates.artifacttemplates.FilesResource;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

public class ServiceTemplatesResource extends AbstractComponentsResource<ServiceTemplateResource> {

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response createFromArtefact(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("file") FormDataBodyPart body,
			@FormDataParam("artefactType") QName artifactType, @FormDataParam("nodeTypes") Set<QName> nodeTypes,
			@FormDataParam("tags") Set<String> tags, @Context UriInfo uriInfo) throws IllegalArgumentException, JAXBException, IOException {

		Collection<ServiceTemplateId> xaasPackages = this.getXaaSPackageTemplates(artifactType);
		Collection<ServiceTemplateId> toRemove = new ArrayList<ServiceTemplateId>();

		// check whether the serviceTemplate contains all the given nodeTypes
		for (ServiceTemplateId serviceTemplate : xaasPackages) {
			if (!this.containsNodeTypes(new ServiceTemplateResource(serviceTemplate).getServiceTemplate(), nodeTypes)
					| !this.containsTags(new ServiceTemplateResource(serviceTemplate).getServiceTemplate(), tags)) {
				toRemove.add(serviceTemplate);
			}
		}

		xaasPackages.removeAll(toRemove);

		if (xaasPackages.size() <= 0) {
			return Response.serverError()
					.entity("No suitable ServiceTemplate found for given artefact and configuration").build();
		}

		// take the first found serviceTemplate
		ServiceTemplateId serviceTemplate = xaasPackages.iterator().next();

		// create new name for the cloned sTemplate
		String newTemplateName = fileDetail.getFileName() + "ServiceTemplate";

		// create artefactTemplate for the uploaded artefact
		ArtifactTemplateId artefactTemplateId = this.createArtefactTemplate(uploadedInputStream, fileDetail,body,
				artifactType, uriInfo);

		// clone serviceTemplate
		ServiceTemplateId serviceTemplateId = this.cloneServiceTemplate(serviceTemplate, newTemplateName, fileDetail.getFileName());

		// inject artefact as DA into cloned ServiceTemplate
		this.injectArtefactTemplateIntoDeploymentArtefact(serviceTemplateId,
				this.getTagValue(new ServiceTemplateResource(serviceTemplate).getServiceTemplate(), "xaasPackageNode"),
				this.getTagValue(new ServiceTemplateResource(serviceTemplate).getServiceTemplate(),
						"xaasPackageDeploymentArtefact"),
				artefactTemplateId);
		
		URI absUri = Utils.getAbsoluteURI(serviceTemplateId);
		// http://localhost:8080/winery/servicetemplates/winery/servicetemplates/http%253A%252F%252Fopentosca.org%252Fservicetemplates/hs_err_pid13228.logServiceTemplate/
		// http://localhost:8080/winery/servicetemplates/winery/servicetemplates/http%253A%252F%252Fopentosca.org%252Fservicetemplates/java0.logServiceTemplate/
		String absUriString = absUri.toString().replace("/winery/servicetemplates", "");
		
		absUri = URI.create(absUriString);
		return Response.created(absUri).build();
	}

	private ArtifactTemplateId createArtefactTemplate(InputStream uploadedInputStream,
			FormDataContentDisposition fileDetail,FormDataBodyPart body, QName artifactType, UriInfo uriInfo) {

		ArtifactTemplatesResource templateResource = new ArtifactTemplatesResource();
		templateResource.onPost("http://opentosca.org/xaaspackager", "xaasPackager_" + fileDetail.getFileName(),
				artifactType.toString());

		ArtifactTemplateId artefactTemplateId = new ArtifactTemplateId("http://opentosca.org/xaaspackager", "xaasPackager_" + fileDetail.getFileName(),
				false);
		
		ArtifactTemplateResource atRes = new ArtifactTemplateResource(artefactTemplateId);
		atRes.getFilesResource().onPost(uploadedInputStream, fileDetail, body, uriInfo);
		
		return artefactTemplateId;
	}

	private boolean injectArtefactTemplateIntoDeploymentArtefact(ServiceTemplateId serviceTemplate,
			String nodeTemplateId, String deploymentArtefactId, ArtifactTemplateId artefactTemplate) {

		ServiceTemplateResource stRes = new ServiceTemplateResource(serviceTemplate);
		stRes.getTopologyTemplateResource().getNodeTemplatesResource().getEntityResource(nodeTemplateId)
				.getDeploymentArtifacts().getEntityResource(deploymentArtefactId).setArtifactTemplate(artefactTemplate);

		return true;
	}

	private ServiceTemplateId cloneServiceTemplate(ServiceTemplateId serviceTemplate, String newName, String artefactName)
			throws JAXBException, IllegalArgumentException, IOException {
		ServiceTemplateId newServiceTemplateId = new ServiceTemplateId(serviceTemplate.getNamespace().getDecoded(),
				newName, false);

		RepositoryFileReference fileRef = new RepositoryFileReference(newServiceTemplateId, "ServiceTemplate.tosca");

		
		Definitions defs = new ServiceTemplateResource(serviceTemplate).getDefinitions();
		
		defs.setId(newName + "Definitions");
		defs.setName(newName + "Definitions generated from Artefact " + artefactName);
		
		TServiceTemplate oldSTModel = null;
		
		for(TExtensibleElements el:  defs.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()){
			if(el instanceof TServiceTemplate){
				oldSTModel = (TServiceTemplate) el;
			}
		}
		
		oldSTModel.setId(newName);
		oldSTModel.setName(newName + " generated from Artefact " + artefactName);
		
		// remove xaaspackager tags
		Collection<TTag> toRemove = new ArrayList<TTag>();
		
		for(TTag tag : oldSTModel.getTags().getTag()){
			switch (tag.getName()) {
			case "xaasPackageNode":
			case "xaasPackageArtefactType":
			case "xaasPackageDeploymentArtefact":
				toRemove.add(tag);
				break;
			default:
				break;
			}
		}
		
		oldSTModel.getTags().getTag().removeAll(toRemove);
		

		JAXBContext context = JAXBContext.newInstance(Definitions.class);
		Marshaller m = context.createMarshaller();
		StringWriter sw = new StringWriter();
		m.marshal(defs, sw);

		String xmlString = sw.toString();

		Repository.INSTANCE.putContentToFile(fileRef, xmlString,
				MediaType.valueOf(MimeTypes.MIMETYPE_TOSCA_DEFINITIONS));

		return newServiceTemplateId;
	}

	private boolean containsTags(TServiceTemplate serviceTemplate, Collection<String> tags) {
		for (String tag : tags) {
			if (tag.contains(":")) {
				String key = tag.split(":")[0];
				String value = tag.split(":")[1];
				if (!this.containsTag(serviceTemplate, key, value)) {
					return false;
				}
			} else {
				if (!this.containsTag(serviceTemplate, tag)) {
					return false;
				}
			}
		}

		return true;
	}

	private boolean containsTag(TServiceTemplate serviceTemplate, String tagKey) {
		String value = this.getTagValue(serviceTemplate, tagKey);
		if (value != null) {
			return true;
		} else {
			return false;
		}
	}

	private boolean containsTag(TServiceTemplate serviceTemplate, String tagKey, String tagValue) {
		String value = this.getTagValue(serviceTemplate, tagKey);

		if (value == null) {
			return false;
		}

		if (!value.equals(tagValue)) {
			return false;
		}

		return true;
	}

	private boolean containsNodeTypes(TServiceTemplate serviceTemplate, Collection<QName> nodeTypes) {
		for (QName nodeType : nodeTypes) {
			if (!this.containsNodeType(serviceTemplate, nodeType)) {
				return false;
			}
		}
		return true;
	}

	private boolean containsNodeType(TServiceTemplate serviceTemplate, QName nodeType) {
		List<TEntityTemplate> templates = serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate();

		for (TEntityTemplate template : templates) {
			if (template instanceof TNodeTemplate) {
				if (((TNodeTemplate) template).getType().equals(nodeType)) {
					return true;
				}
			}
		}

		return false;
	}

	private Collection<ServiceTemplateId> getXaaSPackageTemplates(QName artefactType) {
		Collection<ServiceTemplateId> xaasPackages = new ArrayList<ServiceTemplateId>();
		for (ServiceTemplateId serviceTemplate : this.getXaaSPackageTemplates()) {
			String artefactTypeTagValue = this.getTagValue(
					new ServiceTemplateResource(serviceTemplate).getServiceTemplate(), "xaasPackageArtefactType");
			QName taggedArtefactType = QName.valueOf(artefactTypeTagValue);
			if (taggedArtefactType.equals(artefactType)) {
				xaasPackages.add(serviceTemplate);
			}
		}
		return xaasPackages;
	}

	private String getTagValue(TServiceTemplate serviceTemplate, String tagKey) {
		if (serviceTemplate.getTags() != null) {
			for (TTag tag : serviceTemplate.getTags().getTag()) {
				if (tag.getName().equals(tagKey)) {
					return tag.getValue();
				}
			}
		}
		return null;
	}

	private Collection<ServiceTemplateId> getXaaSPackageTemplates() {
		Collection<AbstractComponentInstanceResource> templates = this.getAll();
		Collection<ServiceTemplateId> xaasPackages = new ArrayList<ServiceTemplateId>();
		for (AbstractComponentInstanceResource resource : templates) {
			if (resource instanceof ServiceTemplateResource) {
				ServiceTemplateResource stRes = (ServiceTemplateResource) resource;

				TTags tags = stRes.getServiceTemplate().getTags();
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
