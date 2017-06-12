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
package org.eclipse.winery.repository.resources.artifacts;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.constants.Namespaces;
import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.XMLId;
import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.generators.ia.Generator;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TEntityTemplate.Properties;
import org.eclipse.winery.model.tosca.TImplementationArtifacts.ImplementationArtifact;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.backend.ResourceCreationResult;
import org.eclipse.winery.repository.backend.filebased.FileUtils;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateDirectoryId;
import org.eclipse.winery.repository.resources.AbstractComponentInstanceResource;
import org.eclipse.winery.repository.resources.AbstractComponentsResource;
import org.eclipse.winery.repository.resources.IHasTypeReference;
import org.eclipse.winery.repository.resources.INodeTemplateResourceOrNodeTypeImplementationResourceOrRelationshipTypeImplementationResource;
import org.eclipse.winery.repository.resources._support.collections.withid.EntityWithIdCollectionResource;
import org.eclipse.winery.repository.resources.apiData.GenerateArtifactApiData;
import org.eclipse.winery.repository.resources.entitytemplates.PropertiesResource;
import org.eclipse.winery.repository.resources.entitytemplates.artifacttemplates.ArtifactTemplateResource;
import org.eclipse.winery.repository.resources.entitytypeimplementations.EntityTypeImplementationResource;
import org.eclipse.winery.repository.resources.entitytypeimplementations.nodetypeimplementations.NodeTypeImplementationResource;
import org.eclipse.winery.repository.resources.entitytypeimplementations.relationshiptypeimplementations.RelationshipTypeImplementationResource;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.NodeTypeResource;
import org.eclipse.winery.repository.resources.servicetemplates.topologytemplates.NodeTemplateResource;

import org.apache.commons.lang3.StringUtils;
import org.restdoc.annotations.RestDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

/**
 * Resource handling both deployment and implementation artifacts
 */
public abstract class GenericArtifactsResource<ArtifactResource extends GenericArtifactResource<ArtifactT>, ArtifactT> extends EntityWithIdCollectionResource<ArtifactResource, ArtifactT> {

	private static final Logger LOGGER = LoggerFactory.getLogger(GenericArtifactsResource.class);

	protected final INodeTemplateResourceOrNodeTypeImplementationResourceOrRelationshipTypeImplementationResource resWithNamespace;


	public GenericArtifactsResource(Class<ArtifactResource> entityResourceTClazz, Class<ArtifactT> entityTClazz, List<ArtifactT> list, INodeTemplateResourceOrNodeTypeImplementationResourceOrRelationshipTypeImplementationResource res) {
		super(entityResourceTClazz, entityTClazz, list, GenericArtifactsResource.getAbstractComponentInstanceResource(res));
		this.resWithNamespace = res;
	}

	// @formatter:off

	/**
	 * @return TImplementationArtifact | TDeploymentArtifact (XML) | URL of generated IA zip (in case of autoGenerateIA)
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@RestDoc(methodDescription = "Creates a new implementation/deployment artifact. " +
			"If an implementation artifact with the same name already exists, it is <em>overridden</em>.")
	@SuppressWarnings("unchecked")
	public Response onPost(GenerateArtifactApiData apiData, @Context UriInfo uriInfo) {
		// we assume that the parent ComponentInstance container exists

		// @formatter:on

		if (StringUtils.isEmpty(apiData.artifactName)) {
			return Response.status(Status.BAD_REQUEST).entity("Empty artifactName").build();
		}
		if (StringUtils.isEmpty(apiData.artifactType)) {
			if (StringUtils.isEmpty(apiData.artifactTemplateName) || StringUtils.isEmpty(apiData.artifactTemplateNamespace)) {
				if (StringUtils.isEmpty(apiData.artifactTemplate)) {
					return Response.status(Status.BAD_REQUEST).entity("No artifact type given and no template given. Cannot guess artifact type").build();
				}
			}
		}

		if (!StringUtils.isEmpty(apiData.autoGenerateIA)) {
			if (StringUtils.isEmpty(apiData.javaPackage)) {
				return Response.status(Status.BAD_REQUEST).entity("no java package name supplied for IA auto generation.").build();
			}
			if (StringUtils.isEmpty(apiData.interfaceName)) {
				return Response.status(Status.BAD_REQUEST).entity("no interface name supplied for IA auto generation.").build();
			}
		}

		// convert second calling form to first calling form
		if (!StringUtils.isEmpty(apiData.artifactTemplate)) {
			QName qname = QName.valueOf(apiData.artifactTemplate);
			apiData.artifactTemplateName = qname.getLocalPart();
			apiData.artifactTemplateNamespace = qname.getNamespaceURI();
		}

		Document doc = null;

		// check artifact specific content for validity
		// if invalid, abort and do not create anything
		if (!StringUtils.isEmpty(apiData.artifactSpecificContent)) {
			try {
				DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				InputSource is = new InputSource();
				StringReader sr = new StringReader(apiData.artifactSpecificContent);
				is.setCharacterStream(sr);
				doc = db.parse(is);
			} catch (Exception e) {
				// FIXME: currently we allow a single element only. However, the content should be internally wrapped by an (arbitrary) XML element as the content will be nested in the artifact element, too
				GenericArtifactsResource.LOGGER.debug("Invalid content", e);
				return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
			}
		}

		// determine artifactTemplate and artifactType

		ArtifactTypeId artifactTypeId;
		ArtifactTemplateId artifactTemplateId = null;
		ArtifactTemplateResource artifactTemplateResource = null;

		boolean doAutoCreateArtifactTemplate = !(StringUtils.isEmpty(apiData.autoCreateArtifactTemplate) || apiData.autoCreateArtifactTemplate.equalsIgnoreCase("no") || apiData.autoCreateArtifactTemplate.equalsIgnoreCase("false"));
		if (!doAutoCreateArtifactTemplate) {
			// no auto creation
			if (!StringUtils.isEmpty(apiData.artifactTemplateName) && !StringUtils.isEmpty(apiData.artifactTemplateNamespace)) {
				QName artifactTemplateQName = new QName(apiData.artifactTemplateNamespace, apiData.artifactTemplateName);
				artifactTemplateId = BackendUtils.getTOSCAcomponentId(ArtifactTemplateId.class, artifactTemplateQName);
			}
			if (StringUtils.isEmpty(apiData.artifactType)) {
				// derive the type from the artifact template
				if (artifactTemplateId == null) {
					return Response.status(Status.NOT_ACCEPTABLE).entity("No artifactTemplate and no artifactType provided. Deriving the artifactType is not possible.").build();
				}
				artifactTemplateResource = new ArtifactTemplateResource(artifactTemplateId);
				artifactTypeId = BackendUtils.getTOSCAcomponentId(ArtifactTypeId.class, artifactTemplateResource.getType());
			} else {
				// artifactTypeStr is directly given, use that
				artifactTypeId = BackendUtils.getTOSCAcomponentId(ArtifactTypeId.class, apiData.artifactType);
			}
		} else {
			// do the artifact template auto creation magic

			if (StringUtils.isEmpty(apiData.artifactType)) {
				return Response.status(Status.BAD_REQUEST).entity("Artifact template auto creation requested, but no artifact type supplied.").build();
			}

			// we assume that the type points to a valid artifact type
			artifactTypeId = BackendUtils.getTOSCAcomponentId(ArtifactTypeId.class, apiData.artifactType);

			if (StringUtils.isEmpty(apiData.artifactTemplateName) || StringUtils.isEmpty(apiData.artifactTemplateNamespace)) {
				// no explicit name provided
				// we use the artifactNameStr as prefix for the
				// artifact template name

				// we create a new artifact template in the namespace of the parent
				// element
				Namespace namespace = this.resWithNamespace.getNamespace();

				artifactTemplateId = new ArtifactTemplateId(namespace, new XMLId(apiData.artifactName + "artifactTemplate", false));
			} else {
				QName artifactTemplateQName = new QName(apiData.artifactTemplateNamespace, apiData.artifactTemplateName);
				artifactTemplateId = new ArtifactTemplateId(artifactTemplateQName);
			}
			ResourceCreationResult creationResult = BackendUtils.create(artifactTemplateId);
			if (!creationResult.isSuccess()) {
				// something went wrong. skip
				return creationResult.getResponse();
			}

			// associate the type to the created artifact template
			artifactTemplateResource = new ArtifactTemplateResource(artifactTemplateId);
			// set the type. The resource is automatically persisted inside
			artifactTemplateResource.setType(apiData.artifactType);
		}

		// variable artifactTypeId is set
		// variable artifactTemplateId is not null if artifactTemplate has been generated

		// we have to generate the DA/IA resource now
		// Doing it here instead of doing it at the subclasses is dirty on the
		// one hand, but quicker to implement on the other hand

		// Create the artifact itself

		ArtifactT resultingArtifact;

		if (this instanceof ImplementationArtifactsResource) {
			ImplementationArtifact a = new ImplementationArtifact();
			// Winery internal id is the name of the artifact:
			// store the name
			a.setName(apiData.artifactName);
			a.setInterfaceName(apiData.interfaceName);
			a.setOperationName(apiData.operationName);
			assert (artifactTypeId != null);
			a.setArtifactType(artifactTypeId.getQName());
			if (artifactTemplateId != null) {
				a.setArtifactRef(artifactTemplateId.getQName());
			}
			if (doc != null) {
				// the content has been checked for validity at the beginning of the method.
				// If this point in the code is reached, the XML has been parsed into doc
				// just copy over the dom node. Hopefully, that works...
				a.getAny().add(doc.getDocumentElement());
			}

			this.list.add((ArtifactT) a);
			resultingArtifact = (ArtifactT) a;
		} else {
			// for comments see other branch

			TDeploymentArtifact a = new TDeploymentArtifact();
			a.setName(apiData.artifactName);
			assert (artifactTypeId != null);
			a.setArtifactType(artifactTypeId.getQName());
			if (artifactTemplateId != null) {
				a.setArtifactRef(artifactTemplateId.getQName());
			}
			if (doc != null) {
				a.getAny().add(doc.getDocumentElement());
			}

			this.list.add((ArtifactT) a);
			resultingArtifact = (ArtifactT) a;
		}

		Response persistResponse = BackendUtils.persist(super.res);
		// TODO: check for error and in case one found return that

		if (StringUtils.isEmpty(apiData.autoGenerateIA)) {
			// no IA generation
			// we include an XML for the data table

//			String implOrDeplArtifactXML = Utils.getXMLAsString(resultingArtifact);

			return Response.created(Utils.createURI(Util.URLencode(apiData.artifactName))).entity(resultingArtifact).build();
		} else {
			// after everything was created, we fire up the artifact generation
			return this.generateImplementationArtifact(apiData.interfaceName, apiData.javaPackage, uriInfo, artifactTemplateId, artifactTemplateResource);
		}
	}

	/**
	 * Generates a unique and valid name to be used for the generated maven
	 * project name, java project name, class name, port type name.
	 */
	private String generateName(NodeTypeId nodeTypeId, String interfaceName) {
		String name = Util.namespaceToJavaPackage(nodeTypeId.getNamespace().getDecoded());
		name += Util.FORBIDDEN_CHARACTER_REPLACEMENT;

		// Winery already ensures that this is a valid NCName
		// getName() returns the id of the nodeType: A nodeType carries the "id" attribute only (and no name attribute)
		name += nodeTypeId.getXmlId().getDecoded();

		// Two separators to distinguish node type and interface part
		name += Util.FORBIDDEN_CHARACTER_REPLACEMENT;
		name += Util.FORBIDDEN_CHARACTER_REPLACEMENT;
		name += Util.namespaceToJavaPackage(interfaceName);

		// In addition we must replace '.', because Java class names must not
		// contain dots, but for Winery they are fine.
		return name.replace(".", Util.FORBIDDEN_CHARACTER_REPLACEMENT);
	}

	/**
	 * Generates the implementation artifact using the implementation artifact
	 * generator. Also sets the proeprties according to the requirements of
	 * OpenTOSCA.
	 *
	 * @param artifactTemplateResource the resource associated with the artifactTempalteId. If null, the object is
	 *                                 created in this method
	 */
	private Response generateImplementationArtifact(String interfaceNameStr, String javapackage, UriInfo uriInfo, ArtifactTemplateId artifactTemplateId, ArtifactTemplateResource artifactTemplateResource) {
		TInterface iface;

		assert (this instanceof ImplementationArtifactsResource);
		IHasTypeReference typeRes = (EntityTypeImplementationResource) this.res;
		QName type = typeRes.getType();
		TOSCAComponentId typeId;
		TNodeType nodeType;
		if (typeRes instanceof NodeTypeImplementationResource) {
			// TODO: refactor: This is more a model/repo utilities thing than something which should happen here...

			typeId = new NodeTypeId(type);
			NodeTypeResource ntRes = (NodeTypeResource) AbstractComponentsResource.getComponentInstaceResource(typeId);

			// required for IA Generation
			nodeType = ntRes.getNodeType();

			List<TInterface> interfaces = nodeType.getInterfaces().getInterface();
			Iterator<TInterface> it = interfaces.iterator();
			do {
				iface = it.next();
				if (iface.getName().equals(interfaceNameStr)) {
					break;
				}
			} while (it.hasNext());
			// iface now contains the right interface
		} else {
			assert (typeRes instanceof RelationshipTypeImplementationResource);
			return Response.serverError().entity("IA creation for relation ship type implementations not yet possible").build();
		}

		Path workingDir;
		try {
			workingDir = Files.createTempDirectory("winery");
		} catch (IOException e2) {
			GenericArtifactsResource.LOGGER.debug("Could not create temporary directory", e2);
			return Response.serverError().entity("Could not create temporary directory").build();
		}

		URI artifactTemplateFilesUri = uriInfo.getBaseUri().resolve(Utils.getAbsoluteURL(artifactTemplateId)).resolve("files/");
		URL artifactTemplateFilesUrl;
		try {
			artifactTemplateFilesUrl = artifactTemplateFilesUri.toURL();
		} catch (MalformedURLException e2) {
			GenericArtifactsResource.LOGGER.debug("Could not convert URI to URL", e2);
			return Response.serverError().entity("Could not convert URI to URL").build();
		}

		String name = this.generateName((NodeTypeId) typeId, interfaceNameStr);
		Generator gen = new Generator(iface, javapackage, artifactTemplateFilesUrl, name, workingDir.toFile());
		File zipFile = gen.generateProject();
		if (zipFile == null) {
			return Response.serverError().entity("IA generator failed").build();
		}

		// store it
		// TODO: refactor: this is more a RepositoryUtils thing than a special thing here; see also importFile at CSARImporter

		ArtifactTemplateDirectoryId fileDir = new ArtifactTemplateDirectoryId(artifactTemplateId);
		RepositoryFileReference fref = new RepositoryFileReference(fileDir, zipFile.getName());
		try (InputStream is = Files.newInputStream(zipFile.toPath());
			 BufferedInputStream bis = new BufferedInputStream(is)) {
			String mediaType = Utils.getMimeType(bis, zipFile.getName());
			// TODO: do the catch thing as in CSARImporter

			Repository.INSTANCE.putContentToFile(fref, bis, MediaType.valueOf(mediaType));
		} catch (IOException e1) {
			throw new IllegalStateException("Could not import generated files", e1);
		}

		// cleanup dir
		FileUtils.forceDelete(workingDir);

		// store the properties in the artifact template
		if (artifactTemplateResource == null) {
			artifactTemplateResource = (ArtifactTemplateResource) AbstractComponentsResource.getComponentInstaceResource(artifactTemplateId);
		}
		this.storeProperties(artifactTemplateResource, typeId, name);

		URI url = uriInfo.getBaseUri().resolve(Utils.getAbsoluteURL(fref));
		return Response.created(url).build();
	}

	private void storeProperties(ArtifactTemplateResource artifactTemplateResource, TOSCAComponentId typeId, String name) {
		// We generate the properties by hand instead of using JAX-B as using JAX-B causes issues at org.eclipse.winery.common.ModelUtilities.getPropertiesKV(TEntityTemplate):
		// getAny() does not always return "w3c.dom.element" anymore

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			GenericArtifactsResource.LOGGER.error(e.getMessage(), e);
			return;
		}
		Document doc = builder.newDocument();
		Element root = doc.createElementNS(Namespaces.OPENTOSCA_WAR_TYPE, "WSProperties");
		doc.appendChild(root);

		Element element = doc.createElementNS(Namespaces.OPENTOSCA_WAR_TYPE, "ServiceEndpoint");
		Text text = doc.createTextNode("/services/" + name + "Port");
		element.appendChild(text);
		root.appendChild(element);

		element = doc.createElementNS(Namespaces.OPENTOSCA_WAR_TYPE, "PortType");
		text = doc.createTextNode("{" + typeId.getNamespace().getDecoded() + "}" + name);
		element.appendChild(text);
		root.appendChild(element);

		element = doc.createElementNS(Namespaces.OPENTOSCA_WAR_TYPE, "InvocationType");
		text = doc.createTextNode("SOAP/HTTP");
		element.appendChild(text);
		root.appendChild(element);

		Properties properties = new Properties();
		properties.setAny(root);
		PropertiesResource propertiesResource = artifactTemplateResource.getPropertiesResource();
		propertiesResource.setProperties(properties);
	}

	/**
	 * Required for artifacts.jsp
	 *
	 * @return list of known artifact types.
	 */
	public List<QName> getAllArtifactTypes() {
		SortedSet<ArtifactTypeId> allArtifactTypes = Repository.INSTANCE.getAllTOSCAComponentIds(ArtifactTypeId.class);
		List<QName> res = new ArrayList<>(allArtifactTypes.size());
		for (ArtifactTypeId id : allArtifactTypes) {
			res.add(id.getQName());
		}
		return res;
	}

	/**
	 * Required for artifacts.jsp
	 *
	 * @return list of all contained artifacts.
	 */
	public abstract Collection<ArtifactResource> getAllArtifactResources();

	/**
	 * Required by artifact.jsp to decide whether to display
	 * "Deployment Artifact" or "Implementation Artifact"
	 */
	public boolean getIsDeploymentArtifacts() {
		return (this instanceof DeploymentArtifactsResource);
	}

	/**
	 * required by artifacts.jsp
	 */
	public String getNamespace() {
		return this.resWithNamespace.getNamespace().getDecoded();
	}

	/**
	 * For saving resources, an AbstractComponentInstanceResource is required.
	 * DAs may be attached to a node template, which is not an
	 * AbstractComponentInstanceResource, but its grandparent resource
	 * ServiceTemplate is
	 *
	 * @param res the resource to determine the the AbstractComponentInstanceResource for
	 * @return the AbstractComponentInstanceResource where the given res is contained in
	 */
	public static AbstractComponentInstanceResource getAbstractComponentInstanceResource(INodeTemplateResourceOrNodeTypeImplementationResourceOrRelationshipTypeImplementationResource res) {
		final AbstractComponentInstanceResource r;
		if (res instanceof NodeTemplateResource) {
			r = ((NodeTemplateResource) res).getServiceTemplateResource();
		} else {
			// quick hack: the resource has to be an abstract component instance
			r = (AbstractComponentInstanceResource) res;
		}
		return r;
	}
}
