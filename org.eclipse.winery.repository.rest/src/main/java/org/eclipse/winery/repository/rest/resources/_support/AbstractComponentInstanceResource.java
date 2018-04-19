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
 ********************************************************************************/
package org.eclipse.winery.repository.rest.resources._support;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ToscaDocumentBuilderFactory;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.constants.MimeTypes;
import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.XmlId;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.common.version.ToscaDiff;
import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.common.version.WineryVersion;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.HasIdInIdOrNameField;
import org.eclipse.winery.model.tosca.TComplianceRule;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTags;
import org.eclipse.winery.repository.JAXBSupport;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.constants.MediaTypes;
import org.eclipse.winery.repository.configuration.Environment;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.apiData.NewVersionApiData;
import org.eclipse.winery.repository.rest.resources.apiData.QNameWithTypeApiData;
import org.eclipse.winery.repository.rest.resources.apiData.RenameApiData;
import org.eclipse.winery.repository.rest.resources.compliancerules.ComplianceRuleResource;
import org.eclipse.winery.repository.rest.resources.documentation.DocumentationResource;
import org.eclipse.winery.repository.rest.resources.entitytypeimplementations.nodetypeimplementations.NodeTypeImplementationResource;
import org.eclipse.winery.repository.rest.resources.entitytypeimplementations.relationshiptypeimplementations.RelationshipTypeImplementationResource;
import org.eclipse.winery.repository.rest.resources.entitytypes.EntityTypeResource;
import org.eclipse.winery.repository.rest.resources.imports.genericimports.GenericImportResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.ServiceTemplateResource;
import org.eclipse.winery.repository.rest.resources.tags.TagsResource;

import com.sun.jersey.api.NotFoundException;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Resource for a component (
 * <ul>
 * <li>ServiceTemplates,</li>
 * <li>EntityTypes,</li>
 * <li>EntityTypeImplementations,</li>
 * <li>EntityTemplates</li>
 * </ul>
 * ). A component is directly nested in a TDefinitions element. See also
 * {@link DefinitionsChildId}
 * <p>
 * Bundles all operations required for all components. e.g., namespace+XMLid, object comparison, import, export, tags
 * <p>
 * Uses a TDefinitions document as storage.
 * <p>
 * Additional setters and getters are added if it comes to Winery's extensions such as the color of a relationship type
 */
public abstract class AbstractComponentInstanceResource implements Comparable<AbstractComponentInstanceResource>, IPersistable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractComponentInstanceResource.class);

    protected final DefinitionsChildId id;

    // shortcut for this.definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().get(0);
    protected TExtensibleElements element = null;

    private final RepositoryFileReference ref;

    // the object representing the data of this resource
    private Definitions definitions = null;

    /**
     * Instantiates the resource. Assumes that the resource should exist (assured by the caller)
     * <p>
     * The caller should <em>not</em> create the resource by other ways. E.g., by instantiating this resource and then
     * adding data.
     */
    public AbstractComponentInstanceResource(DefinitionsChildId id) {
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
    public final XmlId getXmlId() {
        return this.id.getXmlId();
    }

    /**
     * Returns the id associated with this resource
     */
    public final DefinitionsChildId getId() {
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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createNewVersion(NewVersionApiData newVersionApiData, @QueryParam("release") String release, @QueryParam("freeze") String freeze) {
        if (Objects.nonNull(freeze)) {
            return RestUtils.freezeVersion(this.id).getResponse();
        } else if (Objects.nonNull(release)) {
            return RestUtils.releaseVersion(this.id);
        } else {
            String newId = VersionUtils.getNameWithoutVersion(this.id) + WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR + newVersionApiData.version.toString();
            DefinitionsChildId newComponent = BackendUtils.getDefinitionsChildId(this.id.getClass(), this.id.getNamespace().getDecoded(), newId, false);
            return RestUtils.addNewVersion(this.id, newComponent, newVersionApiData.componentsToUpdate);
        }
    }

    @GET
    @Path("id")
    public String getTOSCAId() {
        return this.id.getXmlId().getDecoded();
    }

    @POST
    @Path("localName")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putId(RenameApiData data) {
        DefinitionsChildId newId;
        newId = BackendUtils.getDefinitionsChildId(this.getId().getClass(), this.getId().getNamespace().getDecoded(), data.localname, false);

        if (data.renameAllComponents) {
            return RestUtils.renameAllVersionsOfOneDefinition(this.getId(), newId);
        } else {
            return RestUtils.rename(this.getId(), newId).getResponse();
        }
    }

    @POST
    @Path("namespace")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putNamespace(RenameApiData data) {
        DefinitionsChildId newId = BackendUtils.getDefinitionsChildId(this.getId().getClass(), data.namespace, this.getId().getXmlId().getDecoded(), false);
        return RestUtils.rename(this.getId(), newId).getResponse();
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
     * Returns the definitions of this resource. Includes required imports of other definitions.
     * Also called by the UI
     *
     * @param csar used because plan generator's GET request lands here
     */
    @GET
    @Produces({MimeTypes.MIMETYPE_TOSCA_DEFINITIONS, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    public Response getDefinitionsAsResponse(
        @QueryParam(value = "csar") String csar,
        @QueryParam(value = "yaml") String yaml,
        @Context UriInfo uriInfo
    ) {
        final IRepository repository = RepositoryFactory.getRepository();
        if (!repository.exists(this.id)) {
            return Response.status(Status.NOT_FOUND).build();
        }

        // TODO: It should be possible to specify ?yaml&csar to retrieve a CSAR and ?yaml to retrieve the .yaml representation
        if (yaml != null) {
            if (csar != null) {
                return RestUtils.getYamlCSARofSelectedResource(this);
            } else {
                return RestUtils.getYamlOfSelectedResource(this.getId());
            }
        } else if (csar == null) {
            // we cannot use this.definitions as that definitions is Winery's internal representation of the data and not the full blown definitions (including imports to referenced elements)
            return RestUtils.getDefinitionsOfSelectedResource(this, uriInfo.getBaseUri());
        } else {
            return RestUtils.getCSARofSelectedResource(this);
        }
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response redirectToAngularUi(
        @QueryParam(value = "csar") String csar,
        @QueryParam(value = "yaml") String yaml,
        @Context UriInfo uriInfo) {
        // in case there is an URL requested directly via the browser UI, the accept cannot be put at the link.
        // thus, there is the hack with ?csar and ?yaml
        // the hack is implemented at getDefinitionsAsResponse
        if ((csar != null) || (yaml != null)) {
            return this.getDefinitionsAsResponse(csar, yaml, uriInfo);
        }
        String repositoryUiUrl = Environment.getUrlConfiguration().getRepositoryUiUrl();
        String uri = uriInfo.getAbsolutePath().toString();
        String uiUrl = uriInfo.getAbsolutePath().toString().replaceAll(Environment.getUrlConfiguration().getRepositoryApiUrl(), repositoryUiUrl);
        return Response.temporaryRedirect(URI.create(uiUrl)).build();
    }

    @GET
    @Produces(MimeTypes.MIMETYPE_YAML)
    public Response getElementAsYaml() {
        return RestUtils.getYamlOfSelectedResource(this.getId());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Object getElementAsJson(@QueryParam("versions") @ApiParam("If set, a list of availbale versions is returned.") String versions,
                                   @QueryParam("subComponents") String subComponents,
                                   @QueryParam("compareTo") String compareTo, @QueryParam("asChangeLog") String asChangeLog) {
        final IRepository repository = RepositoryFactory.getRepository();
        if (!repository.exists(this.id)) {
            throw new NotFoundException();
        }
        try {
            if (Objects.nonNull(versions)) {
                return BackendUtils.getAllVersionsOfOneDefinition(this.id);
            } else if (Objects.nonNull(subComponents)) {
                return repository.getReferencedDefinitionsChildIds(this.id)
                    .stream()
                    .map(item -> new QNameWithTypeApiData(
                        item.getXmlId().getDecoded(),
                        item.getNamespace().getDecoded(),
                        item.getGroup())
                    )
                    .collect(Collectors.toList());
            } else if (Objects.nonNull(compareTo)) {
                WineryVersion version = VersionUtils.getVersion(compareTo);
                ToscaDiff compare = BackendUtils.compare(this.id, version);
                if (Objects.nonNull(asChangeLog)) {
                    return compare.getChangeLog();
                } else {
                    return compare;
                }
            } else {
                return BackendUtils.getDefinitionsHavingCorrectImports(repository, this.id);
            }
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
     * <p>
     * The implementors do <em>not</em>have to copy the ns and the id to the appropriate fields.
     * <p>
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
     * Returns the Element belonging to this resource. As Java does not allow overriding returned classes, we expect the
     * caller to either cast right or to use "getXY" defined by each subclass, where XY is the concrete type
     * <p>
     * Shortcut for getDefinitions().getServiceTemplateOrNodeTypeOrNodeTypeImplementation ().get(0);
     *
     * @return TCapabilityType|...
     */
    public TExtensibleElements getElement() {
        return this.element;
    }

    /**
     * Returns an XML representation of the definitions
     * <p>
     * We return the complete definitions to allow the user changes to it, such as adding imports, etc.
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
            DocumentBuilder db = ToscaDocumentBuilderFactory.INSTANCE.getSchemaAwareToscaDocumentBuilder();
            db.setErrorHandler(BackendUtils.getErrorHandler(sb));
            doc = db.parse(requestBodyStream);
            // doc is not null, because the parser parses even if it is not XSD conforming
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

        try {
            BackendUtils.persist(this.getDefinitions(), this.getRepositoryFileReference(), MediaTypes.MEDIATYPE_TOSCA_DEFINITIONS);
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }

        String validationError = sb.toString();
        if (validationError.isEmpty()) {
            return Response.noContent().build();
        } else {
            // ADR-0005: well-formed XML, but non-schema-conforming XML is saved, but triggers warning at the iser
            return Response.ok().entity(validationError).build();
        }
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
        } else if (this.element instanceof TComplianceRule) {
            tags = ((TComplianceRule) this.element).getTags();
            if (tags == null) {
                tags = new TTags();
                ((ComplianceRuleResource) this).getCompliancerule().setTags(tags);
            }
        } else {
            throw new IllegalStateException("tags was called on a resource not supporting tags");
        }

        return new TagsResource(this, tags.getTag());
    }

    @GET
    @Path("LICENSE")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getLicense() {
        RepositoryFileReference ref = new RepositoryFileReference(this.id, Util.URLdecode("LICENSE"));
        return RestUtils.returnRepoPath(ref, null);
    }

    @PUT
    @Path("LICENSE")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response putLicense(String data) {
        RepositoryFileReference ref = new RepositoryFileReference(this.id, Util.URLdecode("LICENSE"));
        return RestUtils.putContentToFile(ref, data, MediaType.TEXT_PLAIN_TYPE);
    }

    @GET
    @Path("README.md")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getReadme() {
        RepositoryFileReference ref = new RepositoryFileReference(this.id, Util.URLdecode("README.md"));
        return RestUtils.returnRepoPath(ref, null);
    }

    @PUT
    @Path("README.md")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response putFile(String data) {
        RepositoryFileReference ref = new RepositoryFileReference(this.id, Util.URLdecode("README.md"));
        return RestUtils.putContentToFile(ref, data, MediaType.TEXT_PLAIN_TYPE);
    }
}

