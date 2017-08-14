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
 *     Tino Stadelmaier, Philipp Meyer - rename for id/namespace
 *     Philipp Meyer - support for src directory
 *******************************************************************************/
package org.eclipse.winery.repository.resources.entitytemplates.artifacttemplates;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.constants.MimeTypes;
import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.common.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeImplementationId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TArtifactTemplate.ArtifactReferences;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateDirectoryId;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateFilesDirectoryId;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateSrcDirectoryId;
import org.eclipse.winery.repository.resources.AbstractComponentInstanceWithReferencesResource;
import org.eclipse.winery.repository.resources.AbstractComponentsResource;
import org.eclipse.winery.repository.resources.IHasName;
import org.eclipse.winery.repository.resources.entitytemplates.IEntityTemplateResource;
import org.eclipse.winery.repository.resources.entitytemplates.PropertiesResource;
import org.eclipse.winery.repository.resources.entitytemplates.TEntityTemplateResource;
import org.eclipse.winery.repository.resources.entitytypeimplementations.nodetypeimplementations.NodeTypeImplementationResource;
import org.eclipse.winery.repository.resources.entitytypeimplementations.relationshiptypeimplementations.RelationshipTypeImplementationResource;
import org.eclipse.winery.repository.resources.entitytypes.artifacttypes.ArtifactTypeResource;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource;

/**
 * Models an Artifact Template with its artifact references
 *
 * The associated files (through tArtifactReference) are stored directly within
 * this resource. The element <ArtifactReference> is generated during export
 * only
 *
 * This class inherits from AbstractComponentInstanceResourceDefinitionsBacked
 * and not from TEntityTemplateResource<TArtifactTemplate>, because
 * ArtifactTemplates are directly available under TDefinitions and we need the
 * generic resource handling
 */

public class ArtifactTemplateResource extends AbstractComponentInstanceWithReferencesResource implements IEntityTemplateResource<TArtifactTemplate>, IHasName {

	private final TEntityTemplateResource<TArtifactTemplate> entityTemplateResource;


	public ArtifactTemplateResource(ArtifactTemplateId id) {
		super(id);
		// we provide the minimum requirements for the resource
		this.entityTemplateResource = new TEntityTemplateResource<>(null, this.getTArtifactTemplate(), 0, null, this);
	}

	/**
	 * @return null if no artifact type resource is defined
	 */
	public ArtifactTypeResource getAritfactTypeResource() {
		ArtifactTypeId atId = new ArtifactTypeId(this.getTArtifactTemplate().getType());
		return new ArtifactTypeResource(atId);
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
		return BackendUtils.persist(this);
	}

	@Override
	protected TExtensibleElements createNewElement() {
		return new TArtifactTemplate();
	}

	@Override
	public void copyIdToFields(TOSCAComponentId id) {
		this.getTArtifactTemplate().setId(id.getXmlId().getDecoded());
		// Namespace cannot be set as the namespace is contained in TDefinitions only
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeReferences() {
		TArtifactTemplate template = this.getTArtifactTemplate();

		ArtifactTemplateDirectoryId fileDir = new ArtifactTemplateFilesDirectoryId((ArtifactTemplateId) this.id);
		SortedSet<RepositoryFileReference> files = Repository.INSTANCE.getContainedFiles(fileDir);
		if (files.isEmpty()) {
			// clear artifact references
			template.setArtifactReferences(null);
		} else {
			ArtifactReferences artifactReferences = new ArtifactReferences();
			template.setArtifactReferences(artifactReferences);
			List<TArtifactReference> artRefList = artifactReferences.getArtifactReference();
			for (RepositoryFileReference ref : files) {
				// determine path
				// path relative from the root of the CSAR is ok (COS01, line 2663)
				String path = Utils.getURLforPathInsideRepo(BackendUtils.getPathInsideRepo(ref));

				// put path into data structure
				// we do not use Inlude/Exclude as we directly reference a concrete file
				TArtifactReference artRef = new TArtifactReference();
				artRef.setReference(path);
				artRefList.add(artRef);
			}
		}
	}

	@Path("files/")
	public FilesResource getFilesResource() {
		ArtifactTemplateDirectoryId fileDir = new ArtifactTemplateFilesDirectoryId((ArtifactTemplateId) this.id);
		return new FilesResource(fileDir);
	}

	@Path("source/")
	public FilesResource getSrcResource() {
		ArtifactTemplateDirectoryId fileDir = new ArtifactTemplateSrcDirectoryId((ArtifactTemplateId) this.id);
		return new FilesResource(fileDir);
	}

	@GET
	@Path("source/zip")
	@Produces(MimeTypes.MIMETYPE_ZIP)
	public Response getDefinitionsAsResponse() {
		ArtifactTemplateDirectoryId fileDir = new ArtifactTemplateSrcDirectoryId((ArtifactTemplateId) this.id);
		return Utils.getZippedContents(fileDir);
	}


	/***********************************************************************
	 * "inheritance" from TEntityTemplateResource<TArtifactTemplate> *
	 *
	 * Offering all methods of TEntityTemplateResource<TArtifactTemplate> and
	 * forwarding it to our private instance of it
	 */

	@Override
	public QName getType() {
		return this.entityTemplateResource.getType();
	}

	@Override
	public Response setType(QName type) {
		this.entityTemplateResource.setType(type);
		return BackendUtils.persist(this);
	}

	@Override
	public Response setType(String typeStr) {
		this.entityTemplateResource.setType(typeStr);
		return BackendUtils.persist(this);
	}

	@Override
	public PropertiesResource getPropertiesResource() {
		return new PropertiesResource(this.getTArtifactTemplate(), this);
	}

	int getReferenceCount() {
		// We do not use a database, therefore, we have to go through all possibilities pointing to the artifact template
		// DAs and IAs point to an artifact template
		// DAs are contained in Node Type Implementations and Node Templates
		// IAs are contained in Node Type Implementations and Relationship Type Implementations

		int count = 0;

		Collection<TDeploymentArtifact> allDAs = new HashSet<>();
		Collection<TImplementationArtifact> allIAs = new HashSet<>();

		// handle Node Type Implementation, which contains DAs and IAs
		SortedSet<NodeTypeImplementationId> nodeTypeImplementations = Repository.INSTANCE.getAllTOSCAComponentIds(NodeTypeImplementationId.class);
		for (NodeTypeImplementationId ntiId : nodeTypeImplementations) {
			NodeTypeImplementationResource ntiRes = (NodeTypeImplementationResource) AbstractComponentsResource.getComponentInstaceResource(ntiId);
			TDeploymentArtifacts deploymentArtifacts = ntiRes.getNTI().getDeploymentArtifacts();
			if (deploymentArtifacts != null) {
				allDAs.addAll(deploymentArtifacts.getDeploymentArtifact());
			}
			TImplementationArtifacts implementationArtifacts = ntiRes.getNTI().getImplementationArtifacts();
			if (implementationArtifacts != null) {
				allIAs.addAll(implementationArtifacts.getImplementationArtifact());
			}
		}

		// check all Relationshiptype Implementations for IAs
		SortedSet<RelationshipTypeImplementationId> relationshipTypeImplementations = Repository.INSTANCE.getAllTOSCAComponentIds(RelationshipTypeImplementationId.class);
		for (RelationshipTypeImplementationId rtiId : relationshipTypeImplementations) {
			RelationshipTypeImplementationResource rtiRes = (RelationshipTypeImplementationResource) AbstractComponentsResource.getComponentInstaceResource(rtiId);
			TImplementationArtifacts implementationArtifacts = rtiRes.getRTI().getImplementationArtifacts();
			if (implementationArtifacts != null) {
				allIAs.addAll(implementationArtifacts.getImplementationArtifact());
			}
		}

		// check all node templates for DAs
		SortedSet<ServiceTemplateId> serviceTemplates = Repository.INSTANCE.getAllTOSCAComponentIds(ServiceTemplateId.class);
		for (ServiceTemplateId sid : serviceTemplates) {
			ServiceTemplateResource serviceTemplateRes = (ServiceTemplateResource) AbstractComponentsResource.getComponentInstaceResource(sid);
			TTopologyTemplate topologyTemplate = serviceTemplateRes.getServiceTemplate().getTopologyTemplate();
			if (topologyTemplate != null) {
				List<TEntityTemplate> nodeTemplateOrRelationshipTemplate = topologyTemplate.getNodeTemplateOrRelationshipTemplate();
				for (TEntityTemplate template : nodeTemplateOrRelationshipTemplate) {
					if (template instanceof TNodeTemplate) {
						TNodeTemplate nodeTemplate = (TNodeTemplate) template;
						TDeploymentArtifacts deploymentArtifacts = nodeTemplate.getDeploymentArtifacts();
						if (deploymentArtifacts != null) {
							allDAs.addAll(deploymentArtifacts.getDeploymentArtifact());
						}
					}
				}
			}
		}

		// now we have all DAs and IAs

		QName ourQName = this.getQName();

		// check DAs for artifact templates
		for (TDeploymentArtifact da : allDAs) {
			QName artifactRef = da.getArtifactRef();
			if (ourQName.equals(artifactRef)) {
				count++;
			}
		}

		// check IAs for artifact templates
		for (TImplementationArtifact ia : allIAs) {
			QName artifactRef = ia.getArtifactRef();
			if (ourQName.equals(artifactRef)) {
				count++;
			}
		}

		return count;
	}

	/**
	 * Query parameter {@code type}:<br />
	 * Returns the type of the artifact template
	 *
	 * Query parameter {@code referenceCount}:<br />
	 * Determines the number of elements known by the repository which point to
	 * this resource. This method probably can be moved up the type hierarchy.
	 * Currently, it is only required here by the topology modeler.
	 *
	 * @return the type of the artifact template OR the number of references
	 *         pointing to this resource
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response getRefereneCount(@QueryParam("referenceCount") String referenceCount, @QueryParam("type") String type) {
		if (referenceCount != null) {
			String res = Integer.toString(this.getReferenceCount());
			return Response.ok().entity(res).build();
		} else if (type != null) {
			String res = this.getType().toString();
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
