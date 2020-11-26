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
package org.eclipse.winery.repository.rest.resources.artifacts;

import java.io.IOException;
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
import java.util.Optional;
import java.util.SortedSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.repository.common.Util;
import org.eclipse.winery.model.ids.EncodingUtil;
import org.eclipse.winery.model.ids.Namespace;
import org.eclipse.winery.model.ids.XmlId;
import org.eclipse.winery.model.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.ids.definitions.EntityTypeId;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.generators.ia.Generator;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TArtifactType;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TImplementationArtifacts.ImplementationArtifact;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.constants.QNames;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.FileUtils;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateSourceDirectoryId;
import org.eclipse.winery.repository.datatypes.ids.elements.DirectoryId;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentInstanceResource;
import org.eclipse.winery.repository.rest.resources._support.INodeTemplateResourceOrNodeTypeImplementationResourceOrRelationshipTypeImplementationResource;
import org.eclipse.winery.repository.rest.resources._support.collections.withid.EntityWithIdCollectionResource;
import org.eclipse.winery.repository.rest.resources.apiData.GenerateArtifactApiData;
import org.eclipse.winery.repository.rest.resources.entitytemplates.artifacttemplates.ArtifactTemplateResource;
import org.eclipse.winery.repository.rest.resources.entitytypeimplementations.nodetypeimplementations.NodeTypeImplementationResource;
import org.eclipse.winery.repository.rest.resources.entitytypeimplementations.nodetypeimplementations.NodeTypeImplementationsResource;
import org.eclipse.winery.repository.rest.resources.entitytypeimplementations.relationshiptypeimplementations.RelationshipTypeImplementationResource;
import org.eclipse.winery.repository.rest.resources.entitytypeimplementations.relationshiptypeimplementations.RelationshipTypeImplementationsResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.topologytemplates.NodeTemplateResource;

import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;
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

    public GenericArtifactsResource(Class<ArtifactResource> entityResourceTClazz, Class<ArtifactT> entityTClazz,
                                    List<ArtifactT> list, INodeTemplateResourceOrNodeTypeImplementationResourceOrRelationshipTypeImplementationResource res) {
        super(entityResourceTClazz, entityTClazz, list, GenericArtifactsResource.getAbstractComponentInstanceResource(res));
        this.resWithNamespace = res;
    }

    /**
     * @return TImplementationArtifact | TDeploymentArtifact (XML) | URL of generated IA zip (in case of autoGenerateIA)
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Creates a new implementation/deployment artifact. If an implementation artifact with the same name already exists, it is <em>overridden</em>.")
    @SuppressWarnings("unchecked")
    public Response generateArtifact(GenerateArtifactApiData apiData, @Context UriInfo uriInfo) {
        // we assume that the parent ComponentInstance container exists

        final IRepository repository = RepositoryFactory.getRepository();

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
                LOGGER.debug("Invalid content", e);
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
                artifactTemplateId = BackendUtils.getDefinitionsChildId(ArtifactTemplateId.class, artifactTemplateQName);
            }
            if (StringUtils.isEmpty(apiData.artifactType)) {
                // derive the type from the artifact template
                if (artifactTemplateId == null) {
                    return Response.status(Status.NOT_ACCEPTABLE).entity("No artifactTemplate and no artifactType provided. Deriving the artifactType is not possible.").build();
                }
                @NonNull final QName type = repository.getElement(artifactTemplateId).getType();
                artifactTypeId = BackendUtils.getDefinitionsChildId(ArtifactTypeId.class, type);
            } else {
                // artifactTypeStr is directly given, use that
                artifactTypeId = BackendUtils.getDefinitionsChildId(ArtifactTypeId.class, apiData.artifactType);
            }
        } else {
            // do the artifact template auto creation magic

            if (StringUtils.isEmpty(apiData.artifactType)) {
                return Response.status(Status.BAD_REQUEST).entity("Artifact template auto creation requested, but no artifact type supplied.").build();
            }

            artifactTypeId = BackendUtils.getDefinitionsChildId(ArtifactTypeId.class, apiData.artifactType);
            // ensure that given type exists
            if (!repository.exists(artifactTypeId)) {
                LOGGER.debug("Artifact type {} is created", apiData.artifactType);
                final TArtifactType element = repository.getElement(artifactTypeId);
                try {
                    repository.setElement(artifactTypeId, element);
                } catch (IOException e) {
                    throw new WebApplicationException(e);
                }
            }

            if (StringUtils.isEmpty(apiData.artifactTemplateName) || StringUtils.isEmpty(apiData.artifactTemplateNamespace)) {
                // no explicit name provided
                // we use the artifactNameStr as prefix for the
                // artifact template name

                // we create a new artifact template in the namespace of the parent
                // element
                Namespace namespace = this.resWithNamespace.getNamespace();

                artifactTemplateId = new ArtifactTemplateId(namespace, new XmlId(apiData.artifactName + "artifactTemplate", false));
            } else {
                QName artifactTemplateQName = new QName(apiData.artifactTemplateNamespace, apiData.artifactTemplateName);
                artifactTemplateId = new ArtifactTemplateId(artifactTemplateQName);
            }

            // even if artifactTemplate does not exist, it is loaded
            final TArtifactTemplate artifactTemplate = repository.getElement(artifactTemplateId);
            artifactTemplate.setType(artifactTypeId.getQName());
            try {
                repository.setElement(artifactTemplateId, artifactTemplate);
            } catch (IOException e) {
                throw new WebApplicationException(e);
            }
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

        // TODO: Check for error, and in case one found return it
        RestUtils.persist(super.res);

        if (StringUtils.isEmpty(apiData.autoGenerateIA)) {
            // No IA generation
            return Response.created(URI.create(EncodingUtil.URLencode(apiData.artifactName))).entity(resultingArtifact).build();
        } else {
            // after everything was created, we fire up the artifact generation
            return this.generateImplementationArtifact(apiData.interfaceName, apiData.javaPackage, uriInfo, artifactTemplateId);
        }
    }

    /**
     * Generates a unique and valid name to be used for the generated maven project name, java project name, class name,
     * port type name.
     */
    private String generateName(EntityTypeId typeId, String interfaceName) {
        String name = Util.namespaceToJavaPackage(typeId.getNamespace().getDecoded());
        name += Util.FORBIDDEN_CHARACTER_REPLACEMENT;

        // Winery already ensures that this is a valid NCName
        // getName() returns the id of the nodeType: A nodeType carries the "id" attribute only (and no name attribute)
        name += VersionUtils.getNameWithoutVersion(typeId.getXmlId().getDecoded());

        // Two separators to distinguish node type and interface part
        name += Util.FORBIDDEN_CHARACTER_REPLACEMENT;
        name += Util.FORBIDDEN_CHARACTER_REPLACEMENT;
        name += Util.namespaceToJavaPackage(interfaceName);

        // In addition we must replace '.', because Java class names must not
        // contain dots, but for Winery they are fine.
        return name.replace(".", Util.FORBIDDEN_CHARACTER_REPLACEMENT);
    }

    /**
     * Generates the implementation artifact using the implementation artifact generator. Also sets the properties
     * according to the requirements of OpenTOSCA.
     */
    private Response generateImplementationArtifact(String interfaceName, String javaPackage, UriInfo uriInfo, ArtifactTemplateId artifactTemplateId) {

        assert (this instanceof ImplementationArtifactsResource);
        IRepository repository = RepositoryFactory.getRepository();

        QName type = RestUtils.getType(this.res);
        EntityTypeId typeId = getTypeId(type).orElseThrow(IllegalStateException::new);
        TInterface i = findInterface(typeId, interfaceName).orElseThrow(IllegalStateException::new);

        Path workingDir;
        try {
            workingDir = Files.createTempDirectory("winery");
        } catch (IOException e2) {
            LOGGER.debug("Could not create temporary directory", e2);
            return Response.serverError().entity("Could not create temporary directory").build();
        }

        URI artifactTemplateFilesUri = uriInfo.getBaseUri().resolve(RestUtils.getAbsoluteURL(artifactTemplateId)).resolve("files");
        URL artifactTemplateFilesUrl;
        try {
            artifactTemplateFilesUrl = artifactTemplateFilesUri.toURL();
        } catch (MalformedURLException e2) {
            LOGGER.debug("Could not convert URI to URL", e2);
            return Response.serverError().entity("Could not convert URI to URL").build();
        }

        String name = this.generateName(typeId, interfaceName);
        Generator gen = new Generator(i, javaPackage, artifactTemplateFilesUrl, name, workingDir.toFile());
        Path targetPath;
        try {
            targetPath = gen.generateProject();
        } catch (Exception e) {
            LOGGER.debug("IA generator failed", e);
            return Response.serverError().entity("IA generator failed").build();
        }

        DirectoryId fileDir = new ArtifactTemplateSourceDirectoryId(artifactTemplateId);
        try {
            BackendUtils.importDirectory(targetPath, repository, fileDir);
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }

        // clean up
        FileUtils.forceDelete(workingDir);

        this.storeProperties(artifactTemplateId, typeId, name);

        URI url = uriInfo.getBaseUri().resolve(Util.getUrlPath(artifactTemplateId));
        return Response.created(url).build();
    }

    private Optional<EntityTypeId> getTypeId(QName type) {
        if (this.res instanceof NodeTypeImplementationResource
            || this.res instanceof NodeTypeImplementationsResource) {
            return Optional.of(new NodeTypeId(type));
        } else if (this.res instanceof RelationshipTypeImplementationResource
            || this.res instanceof RelationshipTypeImplementationsResource) {
            return Optional.of(new RelationshipTypeId(type));
        }
        return Optional.empty();
    }

    private Optional<TInterface> findInterface(EntityTypeId id, String interfaceName) {
        TInterface i;
        List<TInterface> interfaces = new ArrayList<>();
        IRepository repository = RepositoryFactory.getRepository();
        if (this.res instanceof NodeTypeImplementationResource
            || this.res instanceof NodeTypeImplementationsResource) {
            TNodeType nodeType = repository.getElement((NodeTypeId) id);
            if (nodeType.getInterfaces() != null) {
                interfaces.addAll(nodeType.getInterfaces().getInterface());
            }
        } else if (this.res instanceof RelationshipTypeImplementationResource
            || this.res instanceof RelationshipTypeImplementationsResource) {
            TRelationshipType relationshipType = repository.getElement((RelationshipTypeId) id);
            if (relationshipType.getSourceInterfaces() != null) {
                interfaces.addAll(relationshipType.getSourceInterfaces().getInterface());
            }
            if (relationshipType.getTargetInterfaces() != null) {
                interfaces.addAll(relationshipType.getTargetInterfaces().getInterface());
            }
            if (relationshipType.getInterfaces() != null) {
                interfaces.addAll(relationshipType.getInterfaces().getInterface());
            }
        }
        Iterator<TInterface> it = interfaces.iterator();
        do {
            i = it.next();
            if (i.getName().equals(interfaceName)) {
                return Optional.of(i);
            }
        } while (it.hasNext());
        return Optional.empty();
    }

    private void storeProperties(ArtifactTemplateId artifactTemplateId, DefinitionsChildId typeId, String name) {
        // We generate the properties by hand instead of using JAX-B as using JAX-B causes issues at org.eclipse.winery.common.ModelUtilities.getPropertiesKV(TEntityTemplate):
        // getAny() does not always return "w3c.dom.element" anymore

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            LOGGER.error(e.getMessage(), e);
            return;
        }
        Document doc = builder.newDocument();
        String namespace = QNames.QNAME_ARTIFACT_TYPE_WAR.getNamespaceURI();
        Element root = doc.createElementNS(namespace, "WSProperties");
        doc.appendChild(root);

        Element element = doc.createElementNS(namespace, "ServiceEndpoint");
        Text text = doc.createTextNode("/services/" + name + "Port");
        element.appendChild(text);
        root.appendChild(element);

        element = doc.createElementNS(namespace, "PortType");
        text = doc.createTextNode("{" + typeId.getNamespace().getDecoded() + "}" + name);
        element.appendChild(text);
        root.appendChild(element);

        element = doc.createElementNS(namespace, "InvocationType");
        text = doc.createTextNode("SOAP/HTTP");
        element.appendChild(text);
        root.appendChild(element);

        TEntityTemplate.XmlProperties properties = new TEntityTemplate.XmlProperties();
        properties.setAny(root);

        final IRepository repository = RepositoryFactory.getRepository();
        final TArtifactTemplate artifactTemplate = repository.getElement(artifactTemplateId);
        artifactTemplate.setProperties(properties);
        try {
            repository.setElement(artifactTemplateId, artifactTemplate);
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }
    }

    /**
     * Required for artifacts.jsp
     *
     * @return list of known artifact types.
     */
    public List<QName> getAllArtifactTypes() {
        SortedSet<ArtifactTypeId> allArtifactTypes = RepositoryFactory.getRepository().getAllDefinitionsChildIds(ArtifactTypeId.class);
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
     * Required by artifact.jsp to decide whether to display "Deployment Artifact" or "Implementation Artifact"
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
     * For saving resources, an AbstractComponentInstanceResource is required. DAs may be attached to a node template,
     * which is not an AbstractComponentInstanceResource, but its grandparent resource ServiceTemplate is
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
