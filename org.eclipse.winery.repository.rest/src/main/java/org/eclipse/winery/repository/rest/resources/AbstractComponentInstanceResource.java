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
 *     Tino Stadelmaier, Philipp Meyer - rename for id and namespace
 *     Nicole Keppler - support for JSON response
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.TOSCADocumentBuilderFactory;
import org.eclipse.winery.common.constants.MimeTypes;
import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.XMLId;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.HasIdInIdOrNameField;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TImport;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTags;
import org.eclipse.winery.repository.JAXBSupport;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.export.TOSCAExportUtil;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.repository.rest.resources.documentation.DocumentationResource;
import org.eclipse.winery.repository.rest.resources.entitytypeimplementations.nodetypeimplementations.NodeTypeImplementationResource;
import org.eclipse.winery.repository.rest.resources.entitytypeimplementations.relationshiptypeimplementations.RelationshipTypeImplementationResource;
import org.eclipse.winery.repository.rest.resources.imports.genericimports.GenericImportResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.ServiceTemplateResource;
import org.eclipse.winery.repository.rest.resources.tags.TagsResource;

import com.sun.jersey.api.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Resource for a component (
 * <ul>
 * <li>ServiceTemplates,</li>
 * <li>EntityTypes,</li>
 * <li>EntityTypeImplementations,</li>
 * <li>EntityTemplates</li>
 * </ul>
 * ). A component is directly nested in a TDefinitions element. See also
 * {@link org.eclipse.winery.common.ids.definitions.TOSCAComponentId}
 *
 * Bundles all operations required for all components. e.g., namespace+XMLid,
 * object comparison, import, export, tags
 *
 * Uses a TDefinitions document as storage.
 *
 * Additional setters and getters are added if it comes to Winery's extensions
 * such as the color of a relationship type
 */
public abstract class AbstractComponentInstanceResource implements Comparable<AbstractComponentInstanceResource>, IPersistable {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractComponentInstanceResource.class);

	protected final TOSCAComponentId id;

	// shortcut for this.definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().get(0);
	protected TExtensibleElements element = null;

	private final RepositoryFileReference ref;

	// the object representing the data of this resource
	private Definitions definitions = null;

	/**
	 * Instantiates the resource. Assumes that the resource should exist
	 * (assured by the caller)
	 *
	 * The caller should <em>not</em> create the resource by other ways. E.g.,
	 * by instantiating this resource and then adding data.
	 */
	public AbstractComponentInstanceResource(TOSCAComponentId id) {
		this.id = id;

		// the resource itself exists
		if (!RepositoryFactory.getRepository().exists(id)) {
			throw new IllegalStateException(String.format("The resource %s does not exist", id));
		}

		// the data file might not exist
		this.ref = BackendUtils.getRefOfDefinitions(id);
		if (RepositoryFactory.getRepository().exists(this.ref)) {
			this.load();
		} else {
			this.createNew();
		}
	}

	/**
	 * Convenience method for getId().getNamespace()
	 */
	public final Namespace getNamespace() {
		return this.id.getNamespace();
	}

	/**
	 * Convenience method for getId().getXmlId()
	 */
	public final XMLId getXmlId() {
		return this.id.getXmlId();
	}

	/**
	 * Convenience method for getId().getQName();
	 *
	 * @return the QName associated with this resource
	 */
	@SuppressWarnings("WeakerAccess")
	public final QName getQName() {
		return this.getId().getQName();
	}

	/**
	 * Returns the id associated with this resource
	 */
	public final TOSCAComponentId getId() {
		return this.id;
	}

	/**
	 * called from AbstractComponentResource
	 */
	@DELETE
	public final Response onDelete() {
		return RestUtils.delete(this.id);
	}

	@Override
	public final int compareTo(AbstractComponentInstanceResource o) {
		return this.id.compareTo(o.id);
	}

	@Override
	public final boolean equals(Object o) {
		if (o instanceof String) {
			throw new IllegalStateException();
		} else if (o instanceof AbstractComponentInstanceResource) {
			if (o.getClass().equals(this.getClass())) {
				// only compare if the two objects are from the same class
				return ((AbstractComponentInstanceResource) o).getId().equals(this.getId());
			} else {
				throw new IllegalStateException();
			}
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public final int hashCode() {
		return this.getId().hashCode();
	}

	@GET
	@Path("id")
	public String getTOSCAId() {
		return this.id.getXmlId().getDecoded();
	}

	@POST
	@Path("id")
	public Response putId(@FormParam("id") String id, @FormParam("namespace") String namespace) {
		TOSCAComponentId newId;
		if (namespace == null) {
			newId = BackendUtils.getTOSCAcomponentId(this.getId().getClass(), this.getId().getNamespace().getDecoded(), id, false);
		} else {
			newId = BackendUtils.getTOSCAcomponentId(this.getId().getClass(), namespace, this.getId().getXmlId().toString(), false);
		}
		return RestUtils.rename(this.getId(), newId);
	}

	@POST
	@Path("namespace")
	public Response putNamespace(@FormParam("ns") String namespace) {
		TOSCAComponentId newId = BackendUtils.getTOSCAcomponentId(this.getId().getClass(), namespace, this.getId().getXmlId().getDecoded(), false);
		return RestUtils.rename(this.getId(), newId);
	}

	@GET
	@Produces(MimeTypes.MIMETYPE_ZIP)
	public final Response getCSAR() {
		if (!RepositoryFactory.getRepository().exists(this.id)) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return RestUtils.getCSARofSelectedResource(this);
	}

	/**
	 * Returns the definitions of this resource. Includes required imports of
	 * other definitions
	 *
	 * @param csar used because plan generator's GET request lands here
	 */
	@GET
	@Produces({MimeTypes.MIMETYPE_TOSCA_DEFINITIONS, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
	public Response getDefinitionsAsResponse(
			@QueryParam(value = "csar") String csar,
			@Context UriInfo uriInfo
	) {
		if (!RepositoryFactory.getRepository().exists(this.id)) {
			return Response.status(Status.NOT_FOUND).build();
		}

		if (csar != null) {
			return RestUtils.getCSARofSelectedResource(this);
		}

		// we cannot use this.definitions as that definitions is Winery's interal representation of the data and not the full blown definitions (including imports to referenced elements)

		StreamingOutput so = new StreamingOutput() {

			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				TOSCAExportUtil exporter = new TOSCAExportUtil();
				// we include everything related
				Map<String, Object> conf = new HashMap<>();
				conf.put(TOSCAExportUtil.ExportProperties.REPOSITORY_URI.toString(), uriInfo.getBaseUri());
				try {
					exporter.exportTOSCA(AbstractComponentInstanceResource.this.id, output, conf);
				} catch (Exception e) {
					throw new WebApplicationException(e);
				}
			}
		};
		return Response.ok().entity(so).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public TDefinitions getDefinitionsAsJson() {
		if (!RepositoryFactory.getRepository().exists(this.id)) {
			throw new NotFoundException();
		}

		// idea: get the XML, parse it, return it
		// the conversion to JSON is made by Jersey automatically
		// future work: force TOSCAExportUtil to return TDefinitions directly

		TOSCAExportUtil exporter = new TOSCAExportUtil();
		// we include everything related
		Map<String, Object> conf = new HashMap<>();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			exporter.exportTOSCA(AbstractComponentInstanceResource.this.id, bos, conf);
			String xmlRepresentation = bos.toString(StandardCharsets.UTF_8.toString());
			Unmarshaller u = JAXBSupport.createUnmarshaller();
			return (Definitions) u.unmarshal(new StringReader(xmlRepresentation));
		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}

	/**
	 * @throws IllegalStateException if an IOException occurred. We opted not to propagate the IOException directly as
	 *                               this exception occurs seldom and is a not an exception to be treated by all callers
	 *                               in the prototype.
	 */
	private void load() {
		// makes use of the side effect that a wrapper definitions is created
		this.definitions = RepositoryFactory.getRepository().getDefinitions(this.id);

		try {
			this.element = this.definitions.getElement();
		} catch (IndexOutOfBoundsException e) {
			if (this instanceof GenericImportResource) {
				//noinspection StatementWithEmptyBody
				// everything allright:
				// ImportResource is a quick hack using 99% of the functionality offered here
				// As only 1% has to be "quick hacked", we do that instead of a clean design
				// Clean design: Introduce a class between this and AbstractComponentInstanceResource, where this class and ImportResource inhertis from
				// A clean design introducing a super class AbstractDefinitionsBackedResource does not work, as we currently also support PropertiesBackedResources and such a super class would required multi-inheritance
			} else {
				throw new IllegalStateException("Wrong storage format: No ServiceTemplateOrNodeTypeOrNodeTypeImplementation found.");
			}
		}
	}

	/**
	 * Creates a new instance of the object represented by this resource
	 */
	private void createNew() {
		this.definitions = BackendUtils.createWrapperDefinitions(this.getId());

		// create empty element
		this.element = this.createNewElement();

		// add the element to the definitions
		this.definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(this.element);

		// copy ns + id
		BackendUtils.copyIdToFields((HasIdInIdOrNameField) this.element, this.getId());

		// ensure that the definitions is persisted. Ensures that export works.
		RestUtils.persist(this);
	}

	/**
	 * Creates an empty instance of an Element.
	 *
	 * The implementors do <em>not</em>have to copy the ns and the id to the
	 * appropriate fields.
	 *
	 * we have two implementation possibilities:
	 * <ul>
	 * <li>a) each subclass implements this method and returns the appropriate
	 * object</li>
	 * <li>b) we use java reflection to invoke the right constructor as done in
	 * the resources</li>
	 * </ul>
	 * We opted for a) to increase readability of the code
	 */
	protected abstract TExtensibleElements createNewElement();

	/**
	 * Returns the Element belonging to this resource. As Java does not allow
	 * overriding returned classes, we expect the caller to either cast right or
	 * to use "getXY" defined by each subclass, where XY is the concrete type
	 *
	 * Shortcut for
	 * getDefinitions().getServiceTemplateOrNodeTypeOrNodeTypeImplementation
	 * ().get(0);
	 *
	 * @return TCapabilityType|...
	 */
	public TExtensibleElements getElement() {
		return this.element;
	}

	/**
	 * @return the reference to the internal list of imports. Can be changed if some imports are required or should be
	 * removed
	 * @throws IllegalStateException if definitions was not loaded or not initialized
	 */
	protected List<TImport> getImport() {
		if (this.definitions == null) {
			throw new IllegalStateException("Trying to access uninitalized definitions object");
		}
		return this.definitions.getImport();
	}

	/**
	 * Returns an XML representation of the definitions
	 *
	 * We return the complete definitions to allow the user changes to it, such
	 * as adding imports, etc.
	 */
	public String getDefinitionsAsXMLString() {
		return BackendUtils.getDefinitionsAsXMLString(this.getDefinitions());
	}

	/**
	 * @return the reference to the internal Definitions object
	 */
	public Definitions getDefinitions() {
		return this.definitions;
	}

	public RepositoryFileReference getRepositoryFileReference() {
		return this.ref;
	}

	@PUT
	@Consumes({MimeTypes.MIMETYPE_TOSCA_DEFINITIONS, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
	public Response updateDefinitions(InputStream requestBodyStream) {
		Unmarshaller u;
		Definitions defs;
		Document doc;
		final StringBuilder sb = new StringBuilder();
		try {
			DocumentBuilder db = TOSCADocumentBuilderFactory.INSTANCE.getTOSCADocumentBuilder();
			db.setErrorHandler(new ErrorHandler() {

				@Override
				public void warning(SAXParseException exception) throws SAXException {
					// we don't care
				}

				@Override
				public void fatalError(SAXParseException exception) throws SAXException {
					sb.append("Fatal Error: ");
					sb.append(exception.getMessage());
					sb.append("\n");
				}

				@Override
				public void error(SAXParseException exception) throws SAXException {
					sb.append("Fatal Error: ");
					sb.append(exception.getMessage());
					sb.append("\n");
				}
			});
			doc = db.parse(requestBodyStream);
			if (sb.length() > 0) {
				// some error happened
				// doc is not null, because the parser parses even if it is not XSD conforming
				LOGGER.debug("some error happened: {}", sb.toString());
				return Response.status(Status.BAD_REQUEST).entity(sb.toString()).build();
			}
		} catch (SAXException | IOException e) {
			AbstractComponentInstanceResource.LOGGER.debug("Could not parse XML", e);
			return RestUtils.getResponseForException(e);
		}
		try {
			u = JAXBSupport.createUnmarshaller();
			defs = (Definitions) u.unmarshal(doc);
		} catch (JAXBException e) {
			AbstractComponentInstanceResource.LOGGER.debug("Could not unmarshal from request body stream", e);
			return RestUtils.getResponseForException(e);
		}

		// initial validity check

		// we allow changing the target namespace and the id
		// This allows for inserting arbitrary definitions XML
		//		if (!this.definitions.getTargetNamespace().equals(this.id.getNamespace().getDecoded())) {
		//			return Response.status(Status.BAD_REQUEST).entity("Changing of the namespace is not supported").build();
		//		}
		//		this.definitions.setTargetNamespace(this.id.getNamespace().getDecoded());

		// TODO: check the provided definitions for validity

		TExtensibleElements tExtensibleElements = defs.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().get(0);
		if (!tExtensibleElements.getClass().equals(this.createNewElement().getClass())) {
			return Response.status(Status.BAD_REQUEST).entity("First type in Definitions is not matching the type modeled by this resource").build();
		}

		this.definitions = defs;

		// replace existing element by retrieved data
		this.element = this.definitions.getElement();

		// ensure that ids did not change
		// TODO: future work: raise error if user changed id or namespace
		BackendUtils.copyIdToFields((HasIdInIdOrNameField) element, this.getId());

		return RestUtils.persist(this);
	}

	@GET
	@Path("xml/")
	@Produces(MediaType.APPLICATION_XML)
	public Response getXMLasString() {
		return Response.ok().entity(this.getDefinitionsAsXMLString()).build();
	}

	@Path("documentation/")
	public DocumentationResource getDocumentationsResource() {
		return new DocumentationResource(this, this.getElement().getDocumentation());
	}

	@Path("tags/")
	public final TagsResource getTags() {
		TTags tags = null;
		if (this.element instanceof TServiceTemplate) {
			tags = ((TServiceTemplate) this.element).getTags();
			if (tags == null) {
				tags = new TTags();
				((ServiceTemplateResource) this).getServiceTemplate().setTags(tags);
			}
		} else if (this.element instanceof TEntityType) {
			tags = ((TEntityType) this.element).getTags();
			if (tags == null) {
				tags = new TTags();
				((EntityTypeResource) this).getEntityType().setTags(tags);
			}
		} else if (this.element instanceof TNodeTypeImplementation) {
			tags = ((TNodeTypeImplementation) this.element).getTags();
			if (tags == null) {
				tags = new TTags();
				((NodeTypeImplementationResource) this).getNTI().setTags(tags);
			}
		} else if (this.element instanceof TRelationshipTypeImplementation) {
			tags = ((TRelationshipTypeImplementation) this.element).getTags();
			if (tags == null) {
				tags = new TTags();
				((RelationshipTypeImplementationResource) this).getRTI().setTags(tags);
			}
		} else {
			throw new IllegalStateException("tags was called on a resource not supporting tags");
		}

		return new TagsResource(this, tags.getTag());
	}
}
