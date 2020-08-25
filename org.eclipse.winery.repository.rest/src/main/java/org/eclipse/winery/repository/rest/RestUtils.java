/********************************************************************************
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
 ********************************************************************************/
package org.eclipse.winery.repository.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.nio.file.attribute.FileTime;
import java.security.AccessControlException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.Constants;
import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.edmm.EdmmManager;
import org.eclipse.winery.edmm.model.EdmmConverter;
import org.eclipse.winery.edmm.model.EdmmType;
import org.eclipse.winery.repository.backend.WineryVersionUtils;
import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.repository.common.Util;
import org.eclipse.winery.common.configuration.Environments;
import org.eclipse.winery.common.configuration.UiConfigurationObject;
import org.eclipse.winery.common.constants.MimeTypes;
import org.eclipse.winery.model.ids.GenericId;
import org.eclipse.winery.model.ids.Namespace;
import org.eclipse.winery.model.ids.XmlId;
import org.eclipse.winery.model.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.model.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeImplementationId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.ids.elements.ToscaElementId;
import org.eclipse.winery.common.version.WineryVersion;
import org.eclipse.winery.model.selfservice.Application;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.HasType;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TConstraint;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTag;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.NamespaceManager;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.constants.MediaTypes;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;
import org.eclipse.winery.repository.backend.xsd.NamespaceAndDefinedLocalNames;
import org.eclipse.winery.repository.export.CsarExportOptions;
import org.eclipse.winery.repository.export.CsarExporter;
import org.eclipse.winery.repository.export.ToscaExportUtil;
import org.eclipse.winery.repository.rest.datatypes.ComponentId;
import org.eclipse.winery.repository.yaml.export.YamlExporter;
import org.eclipse.winery.repository.rest.datatypes.LocalNameForAngular;
import org.eclipse.winery.repository.rest.datatypes.NamespaceAndDefinedLocalNamesForAngular;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentInstanceResource;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentsResource;
import org.eclipse.winery.repository.rest.resources._support.IHasName;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.repository.rest.resources._support.ResourceResult;
import org.eclipse.winery.repository.rest.resources.apiData.QNameApiData;
import org.eclipse.winery.repository.rest.resources.apiData.QNameWithTypeApiData;
import org.eclipse.winery.repository.rest.resources.apiData.converter.QNameConverter;
import org.eclipse.winery.repository.rest.resources.entitytemplates.artifacttemplates.ArtifactTemplateResource;
import org.eclipse.winery.repository.rest.resources.entitytemplates.artifacttemplates.ArtifactTemplatesResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.ServiceTemplateResource;

import io.github.edmm.core.parser.EntityGraph;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains utility functionality concerning with everything that is <em>not</em> related only to the repository, but
 * more. For instance, resource functionality. Utility functionality for the repository is contained at {@link
 * BackendUtils}
 */
public class RestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestUtils.class);

    // RegExp inspired by http://stackoverflow.com/a/5396246/873282
    // NameStartChar without ":"
    // stackoverflow: -dfff, standard: d7fff
    private static final String RANGE_NCNAMESTARTCHAR = "A-Z_a-z\\u00C0\\u00D6\\u00D8-\\u00F6\\u00F8-\\u02ff\\u0370-\\u037d" + "\\u037f-\\u1fff\\u200c\\u200d\\u2070-\\u218f\\u2c00-\\u2fef\\u3001-\\ud7ff" + "\\uf900-\\ufdcf\\ufdf0-\\ufffd\\x10000-\\xEFFFF";
    private static final String REGEX_NCNAMESTARTCHAR = "[" + RestUtils.RANGE_NCNAMESTARTCHAR + "]";

    private static final String RANGE_NCNAMECHAR = RestUtils.RANGE_NCNAMESTARTCHAR + "\\-\\.0-9\\u00b7\\u0300-\\u036f\\u203f-\\u2040";
    private static final String REGEX_INVALIDNCNAMESCHAR = "[^" + RestUtils.RANGE_NCNAMECHAR + "]";

    static {
        if (Locale.getDefault() != Locale.ENGLISH) {
            try {
                // needed for {@link
                // returnRepoPath(File, String)}
                Locale.setDefault(Locale.ENGLISH);
            } catch (AccessControlException e) {
                // Happens at Google App Engine
                LOGGER.error("Could not switch locale to English", e);
            }
        }
    }

    /**
     * Creates a (valid) XML ID (NCName) based on the passed name
     * <p>
     * Valid NCNames: http://www.w3.org/TR/REC-xml-names/#NT-NCName / http://www.w3.org/TR/xml/#NT-Name
     * http://www.w3.org/TR/xml/#NT-Name
     */
    public static XmlId createXMLid(String name) {
        return new XmlId(RestUtils.createXmlIdAsString(name), false);
    }

    /**
     * Creates a (valid) XML ID (NCName) based on the passed name
     * <p>
     * Valid NCNames: http://www.w3.org/TR/REC-xml-names/#NT-NCName / http://www.w3.org/TR/xml/#NT-Name
     * http://www.w3.org/TR/xml/#NT-Name
     * <p>
     * TODO: this method seems to be equal to {@link Util#makeNCName(java.lang.String)}. The methods should be merged
     * into one.
     */
    public static String createXmlIdAsString(String name) {
        String id = name;
        if (!id.substring(0, 1).matches(RestUtils.REGEX_NCNAMESTARTCHAR)) {
            id = "-".concat(id);
        }
        // id starts with a valid character

        // before we wipe out all invalid characters, we do a readable
        // replacement for appropriate characters
        id = id.replace(' ', '-');

        // keep length of ID, only wipe out invalid characters
        // alternative: replace invalid characters by URLencoded version. As the
        // ID is visible only in the URL, this quick hack should be OK
        // ID is visible only in the URL, this quick hack should be OK
        id = id.replaceAll(RestUtils.REGEX_INVALIDNCNAMESCHAR, "-");

        return id;
    }

    /**
     * Returns the plain XML for the selected resource
     */
    public static Response getDefinitionsOfSelectedResource(final AbstractComponentInstanceResource resource, final URI uri) {
        final ToscaExportUtil exporter = new ToscaExportUtil();
        StreamingOutput so = output -> {
            Map<String, Object> conf = new HashMap<>();
            conf.put(ToscaExportUtil.ExportProperties.REPOSITORY_URI.toString(), uri);
            try {
                exporter.writeTOSCA(RepositoryFactory.getRepository(), resource.getId(), conf, output);
            } catch (Exception e) {
                throw new WebApplicationException(e);
            }
            output.close();
        };
        /*
         * this code is for offering a download action // Browser offers save as
         * // .tosca is more or less needed for debugging, only a CSAR makes
         * sense. // Therefore, we want to have the xml opened in the browser.
         * StringBuilder sb = new StringBuilder();
         * sb.append("attachment;filename=\"");
         * sb.append(resource.getXmlId().getEncoded()); sb.append(" - ");
         * sb.append(resource.getNamespace().getEncoded()); sb.append(".xml");
         * sb.append("\""); return Response.ok().header("Content-Disposition",
         * sb
         * .toString()).type(MediaType.APPLICATION_XML_TYPE).entity(so).buildProvenanceSmartContract();
         */
        return Response.ok().type(MediaType.APPLICATION_XML).entity(so).build();
    }

    /**
     * @param options the set of options that are applicable for exporting a csar
     */
    public static Response getCsarOfSelectedResource(final AbstractComponentInstanceResource resource, CsarExportOptions options) {
        LocalDateTime start = LocalDateTime.now();
        final CsarExporter exporter = new CsarExporter(RepositoryFactory.getRepository());
        Map<String, Object> exportConfiguration = new HashMap<>();

        StreamingOutput so = output -> {
            try {
                // check which options are chosen
                if (options.isAddToProvenance()) {
                    // We wait for the accountability layer to confirm the transaction
                    String result = exporter.writeCsarAndSaveManifestInProvenanceLayer(resource.getId(), output)
                        .get();
                    LOGGER.debug("Stored state in accountability layer in transaction " + result);
                    LOGGER.debug("CSAR export (provenance) lasted {}", Duration.between(LocalDateTime.now(), start).toString());
                } else {
                    exporter.writeCsar(resource.getId(), output, exportConfiguration);
                    LOGGER.debug("CSAR export lasted {}", Duration.between(LocalDateTime.now(), start).toString());
                }
            } catch (Exception e) {
                LOGGER.error("Error while exporting CSAR", e);
                throw new WebApplicationException(e);
            }
        };
        String contentDisposition = String.format("attachment;filename=\"%s%s\"",
            resource.getXmlId().getEncoded(),
            Constants.SUFFIX_CSAR);

        return Response.ok()
            .header("Content-Disposition", contentDisposition)
            .type(MimeTypes.MIMETYPE_ZIP)
            .entity(so)
            .build();
    }

    public static Response getYamlOfSelectedResource(DefinitionsChildId id) {
        // TODO
//        final Converter converter = new Converter();
//        try {
//            // MimeTypes.MIMETYPE_YAML not chosen, because firefox always demands download
//            return Response.ok(converter.convertDefinitionsChildToYaml(id)).type(MediaType.TEXT_PLAIN_TYPE).build();
//        } catch (MultiException e) {
//            throw new WebApplicationException(e);
//        }
        return Response.noContent().build();
    }

    public static Response getYamlCSARofSelectedResource(final AbstractComponentInstanceResource resource) {
        LocalDateTime start = LocalDateTime.now();
        final YamlExporter exporter = new YamlExporter(RepositoryFactory.getRepository());
        Map<String, Object> exportConfiguration = new HashMap<>();

        StreamingOutput so = output -> {
            try {
                exporter.writeCsar(resource.getId(), output, exportConfiguration);
                LOGGER.debug("CSAR export lasted {}", Duration.between(LocalDateTime.now(), start).toString());
            } catch (Exception e) {
                LOGGER.error("Error while exporting CSAR", e);
                throw new WebApplicationException(e);
            }
        };
        String contentDisposition = String.format("attachment;filename=\"%s%s\"",
            resource.getXmlId().getEncoded(),
            Constants.SUFFIX_CSAR);

        return Response.ok()
            .header("Content-Disposition", contentDisposition)
            .type(MimeTypes.MIMETYPE_ZIP)
            .entity(so)
            .build();
    }

    public static Response getEdmmModel(TServiceTemplate element, boolean useAbsolutPaths) {
        IRepository repository = RepositoryFactory.getRepository();

        Map<QName, TNodeType> nodeTypes = repository.getQNameToElementMapping(NodeTypeId.class);
        Map<QName, TRelationshipType> relationshipTypes = repository.getQNameToElementMapping(RelationshipTypeId.class);
        Map<QName, TNodeTypeImplementation> nodeTypeImplementations = repository.getQNameToElementMapping(NodeTypeImplementationId.class);
        Map<QName, TRelationshipTypeImplementation> relationshipTypeImplementations = repository.getQNameToElementMapping(RelationshipTypeImplementationId.class);
        Map<QName, TArtifactTemplate> artifactTemplates = repository.getQNameToElementMapping(ArtifactTemplateId.class);
        EdmmManager edmmManager = EdmmManager.forRepository(repository);
        Map<QName, EdmmType> oneToOneMappings = edmmManager.getOneToOneMap();
        Map<QName, EdmmType> typeMappings = edmmManager.getTypeMap();

        if (nodeTypes.isEmpty()) {
            throw new IllegalStateException("No Node Types defined!");
        } else if (relationshipTypes.isEmpty()) {
            throw new IllegalStateException("No Relationship Types defined!");
        }

        EdmmConverter edmmConverter = new EdmmConverter(nodeTypes, relationshipTypes, nodeTypeImplementations, relationshipTypeImplementations,
            artifactTemplates, typeMappings, oneToOneMappings, useAbsolutPaths);
        EntityGraph transform = edmmConverter.transform(element);
        StringWriter stringWriter = new StringWriter();
        transform.generateYamlOutput(stringWriter);

        return Response.ok()
            .type(MimeTypes.MIMETYPE_YAML)
            .entity(stringWriter.toString())
            .build();
    }

    /**
     * Zips the folder reference given by the id. As filename the parent id is used.
     *
     * @param id the id of the folder
     */
    public static Response getZippedContents(final GenericId id) {
        final String name = id.getParent().getXmlId().getEncoded();
        return getZippedContents(id, name + Constants.SUFFIX_ZIP);
    }

    /**
     * Zips the folder reference given by the id. As filename the parent id is used.
     *
     * @param id   the id of the folder
     * @param name the name of the result zip (including.zip)
     */
    public static Response getZippedContents(final GenericId id, String name) {
        StreamingOutput so = output -> {
            try {
                RepositoryFactory.getRepository().getZippedContents(id, output);
            } catch (Exception e) {
                throw new WebApplicationException(e);
            }
        };
        String sb = "attachment;filename=\"" +
            name +
            "\"";
        return Response.ok().header("Content-Disposition", sb).type(MimeTypes.MIMETYPE_ZIP).entity(so).build();
    }

    /**
     * @return Singular type name for given AbstractComponentsResource. E.g, "ServiceTemplatesResource" gets
     * "ServiceTemplate"
     */
    public static String getTypeForComponentContainer(Class<? extends AbstractComponentsResource> containerClass) {
        String res = containerClass.getName();
        // Everything between the last "." and before "sResource" is the Type
        int dotIndex = res.lastIndexOf('.');
        assert (dotIndex >= 0);
        return res.substring(dotIndex + 1, res.length() - "sResource".length());
    }

    /**
     * Returns a class object for ids of components nested in the given AbstractComponentsResource
     */
    public static Class<? extends DefinitionsChildId> getComponentIdClassForComponentContainer(Class<? extends AbstractComponentsResource> containerClass) {
        // the name of the id class is the type + "Id"
        String idClassName = RestUtils.getTypeForComponentContainer(containerClass) + "Id";

        return Util.getComponentIdClass(idClassName);
    }

    /**
     * @return the absolute path for the given id
     */
    public static String getAbsoluteURL(GenericId id) {
        return Environments.getInstance().getUiConfig().getEndpoints().get(UiConfigurationObject.apiUrlKey) + "/" + Util.getUrlPath(id);
    }

    /**
     * @return the absolute path for the given id
     */
    public static String getAbsoluteURL(RepositoryFileReference ref) {
        return Environments.getInstance().getUiConfig().getEndpoints().get(UiConfigurationObject.apiUrlKey) + "/" + Util.getUrlPath(ref);
    }

    public static URI getAbsoluteURI(GenericId id) {
        return URI.create((RestUtils.getAbsoluteURL(id)));
    }

    /**
     * Returns the middle part of the package name or the JSP location
     *
     * @param type      the type
     * @param separator the separator to be used, "." or "/"
     * @return string which can be used "in the middle" of a package or of a path to a JSP
     */
    public static String getIntermediateLocationStringForType(String type, String separator) {
        String location;
        if (type.contains("ServiceTemplate")) {
            location = "servicetemplates";
        } else if (type.contains("ComplianceRule")) {
            location = "compliancerules";
        } else if (type.contains("RefinementModel")) {
            location = "refinementmodels";
        } else if (type.contains("DataType")) {
            // FIXME Intermediate location, may be subject to change!
            location = "yaml";
        } else {
            if (type.contains("TypeImplementation")) {
                location = "entitytypeimplementations";
            } else if (type.contains("Type")) {
                location = "entitytypes";
            } else if (type.contains("Import")) {
                location = "imports";
            } else {
                assert (type.contains("Template"));
                location = "entitytemplates";
            }
            // location now is the super pkg, we have to add a pkg of the type
            location = location + separator + type.toLowerCase() + "s";
        }
        return location;
    }

    /**
     * Converts the given object to XML.
     * <p>
     * Used in cases the given element is not annotated with @XmlRoot
     * <p>
     * We cannot use {@literal Class<? extends TExtensibleElements>} as, for instance, {@link TConstraint} does not
     * inherit from {@link TExtensibleElements}
     *  @param clazz the Class of the passed object, required if obj is null
     * @param obj   the object to serialize
     * @param repository
     */
    public static <T> Response getXML(Class<T> clazz, T obj, IRepository repository) {
        // see commit ab4b5c547619c058990 for an implementation using getJAXBElement,
        // which can be directly passed as entity
        // the issue is that we want to have a *formatted* XML
        // Therefore, we serialize "by hand".
        String xml = BackendUtils.getXMLAsString(clazz, obj, false, repository);

        return Response.ok().type(MediaType.TEXT_XML).entity(xml).build();
    }

    public static Response getResponseForException(Exception e) {
        String msg;
        if (e.getCause() != null) {
            msg = e.getCause().getMessage();
        } else {
            msg = e.getMessage();
        }
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(msg).build();
    }

    public static boolean isSuccessFulResponse(Response res) {
        return Status.fromStatusCode(res.getStatus()).getFamily().equals(Family.SUCCESSFUL);
    }

    /**
     * Checks whether a given resource (with absolute URL!) is available with a HEAD request on it.
     */
    public static boolean isResourceAvailable(String path) {
        Response response = ClientBuilder.newClient()
            .target(path)
            .request()
            .head();

        return response.getStatusInfo().getFamily().equals(Family.SUCCESSFUL);
    }

    public static Set<String> clean(Set<String> set) {
        Set<String> newSet = new HashSet<>();

        for (String setItem : set) {
            if (setItem != null && !setItem.trim().isEmpty() && !setItem.equals("null")) {
                newSet.add(setItem);
            }
        }

        return newSet;
    }

    public static Set<QName> cleanQNameSet(Set<QName> set) {
        Set<QName> newSet = new HashSet<>();

        for (QName setItem : set) {
            if (setItem != null && !setItem.getLocalPart().equals("null")) {
                newSet.add(setItem);
            }
        }
        return newSet;
    }

    public static ServiceTemplateId cloneServiceTemplate(ServiceTemplateId serviceTemplate, String newName, String artifactName) throws JAXBException, IllegalArgumentException, IOException {

        ServiceTemplateId newServiceTemplateId = new ServiceTemplateId(serviceTemplate.getNamespace().getDecoded(), newName, false);

        RepositoryFileReference fileRef = new RepositoryFileReference(newServiceTemplateId, "ServiceTemplate.tosca");

        TDefinitions defs = new ServiceTemplateResource(serviceTemplate).getDefinitions();

        defs.setId(newName + "Definitions");
        defs.setName(newName + "Definitions generated from Artifact " + artifactName);

        TServiceTemplate oldSTModel = null;

        for (TExtensibleElements el : defs.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
            if (el instanceof TServiceTemplate) {
                oldSTModel = (TServiceTemplate) el;
            }
        }

        if (oldSTModel != null) {
            oldSTModel.setId(newName);
            oldSTModel.setName(newName + " generated from Artifact " + artifactName);

            // remove xaaspackager tags
            Collection<TTag> toRemove = new ArrayList<>();

            for (TTag tag : oldSTModel.getTags().getTag()) {
                switch (tag.getName()) {
                    case "xaasPackageNode":
                    case "xaasPackageArtifactType":
                    case "xaasPackageDeploymentArtifact":
                        toRemove.add(tag);
                        break;
                    default:
                        break;
                }
            }

            oldSTModel.getTags().getTag().removeAll(toRemove);
        }

        JAXBContext context = JAXBContext.newInstance(TDefinitions.class);
        Marshaller m = context.createMarshaller();
        StringWriter sw = new StringWriter();
        m.marshal(defs, sw);

        String xmlString = sw.toString();

        RepositoryFactory.getRepository().putContentToFile(fileRef, xmlString, MediaTypes.MEDIATYPE_TOSCA_DEFINITIONS);

        return newServiceTemplateId;
    }

    public static boolean containsNodeType(TServiceTemplate serviceTemplate, QName nodeType) {
        List<TEntityTemplate> templates = serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate();

        return templates.stream().filter(template -> template instanceof TNodeTemplate).anyMatch(template -> template.getType().equals(nodeType));
    }

    public static boolean containsNodeTypes(TServiceTemplate serviceTemplate, Collection<QName> nodeTypes) {
        return nodeTypes.stream().allMatch(nodeType -> RestUtils.containsNodeType(serviceTemplate, nodeType));
    }

    public static boolean containsTag(TServiceTemplate serviceTemplate, String tagKey) {
        return RestUtils.getTagValue(serviceTemplate, tagKey) != null;
    }

    public static boolean containsTag(TServiceTemplate serviceTemplate, String tagKey, String tagValue) {
        String value = RestUtils.getTagValue(serviceTemplate, tagKey);
        return value != null && value.equals(tagValue);
    }

    public static boolean containsTags(TServiceTemplate serviceTemplate, Collection<String> tags) {
        for (String tag : tags) {
            if (tag.contains(":")) {
                String key = tag.split(":")[0];
                String value = tag.split(":")[1];
                if (!RestUtils.containsTag(serviceTemplate, key, value)) {
                    return false;
                }
            } else {
                if (!RestUtils.containsTag(serviceTemplate, tag)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static ArtifactTemplateId createArtifactTemplate(InputStream uploadedInputStream, FormDataContentDisposition fileDetail, FormDataBodyPart body, QName artifactType, UriInfo uriInfo) {

        ArtifactTemplatesResource templateResource = new ArtifactTemplatesResource();
        QNameWithTypeApiData qNameApiData = new QNameWithTypeApiData();
        qNameApiData.localname = "xaasPackager_" + fileDetail.getFileName();
        qNameApiData.namespace = "http://opentosca.org/xaaspackager";
        qNameApiData.type = artifactType.toString();
        templateResource.onJsonPost(qNameApiData);

        ArtifactTemplateId artifactTemplateId = new ArtifactTemplateId("http://opentosca.org/xaaspackager", "xaasPackager_" + fileDetail.getFileName(), false);

        ArtifactTemplateResource atRes = new ArtifactTemplateResource(artifactTemplateId);
        atRes.getFilesResource().onPost(uploadedInputStream, fileDetail, body, uriInfo);

        return artifactTemplateId;
    }

    public static String getTagValue(TServiceTemplate serviceTemplate, String tagKey) {
        if (serviceTemplate.getTags() != null) {
            for (TTag tag : serviceTemplate.getTags().getTag()) {
                if (tag.getName().equals(tagKey)) {
                    return tag.getValue();
                }
            }
        }
        return null;
    }

    public static boolean hasDA(ServiceTemplateId serviceTemplate, String nodeTemplateId, String deploymentArtifactId) {
        ServiceTemplateResource stRes = new ServiceTemplateResource(serviceTemplate);
        try {
            stRes.getTopologyTemplateResource().getNodeTemplatesResource().getEntityResource(nodeTemplateId).getDeploymentArtifacts().getEntityResource(deploymentArtifactId);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Persists the resource and returns appropriate response
     */
    public static Response persist(IPersistable res) {
        return persistWithResponseBuilder(res).build();
    }

    /**
     * Persists the given object
     */
    public static Response persist(Application application, RepositoryFileReference data_xml_ref, String mimeType) {
        try {
            BackendUtils.persist(application, data_xml_ref, org.apache.tika.mime.MediaType.parse(mimeType), RepositoryFactory.getRepository());
        } catch (IOException e) {
            LOGGER.debug("Could not persist resource", e);
            throw new WebApplicationException(e);
        }
        return Response.noContent().build();
    }

    public static Response.ResponseBuilder persistWithResponseBuilder(IPersistable res) {
        try {
            NamespaceManager namespaceManager = RepositoryFactory.getRepository().getNamespaceManager();
            namespaceManager.addPermanentNamespace(res.getDefinitions().getTargetNamespace());
            BackendUtils.persist(RepositoryFactory.getRepository(), res.getRepositoryFileReference(), res.getDefinitions());
        } catch (IOException e) {
            LOGGER.debug("Could not persist resource", e);
            throw new WebApplicationException(e);
        }
        return Response.noContent();
    }

    public static ResourceResult rename(DefinitionsChildId oldId, DefinitionsChildId newId) {
        ResourceResult result = new ResourceResult();
        IRepository repo = RepositoryFactory.getRepository();
        WineryVersion version = oldId.getVersion();
        DefinitionsChildId id = newId;

        if (version.toString().length() > 0) {
            // ensure that the version isn't changed by the user
            String componentName = newId.getNameWithoutVersion() + WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR + version.toString();
            id = BackendUtils.getDefinitionsChildId(oldId.getClass(), newId.getNamespace().getDecoded(), componentName, false);
        }

        // If a definition was not committed yet, it is renamed, otherwise duplicate the definition.
        if (repo instanceof GitBasedRepository && ((GitBasedRepository) repo).hasChangesInFile(BackendUtils.getRefOfDefinitions(oldId))) {
            try {
                repo.rename(oldId, id);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
                result.setStatus(Status.INTERNAL_SERVER_ERROR);
                result.setMessage(e.getMessage());
                return result;
            }
        } else {
            result = duplicate(oldId, id);
            if (result.isSuccess()) {
                result = freezeVersion(id);
            }
        }

        URI uri = RestUtils.getAbsoluteURI(id);
        result.setUri(uri);
        result.setStatus(Status.CREATED);

        return result;
    }

    public static Response renameAllVersionsOfOneDefinition(DefinitionsChildId oldId, DefinitionsChildId newId) {
        SortedSet<? extends DefinitionsChildId> definitions = WineryVersionUtils.getOtherVersionDefinitionsFromDefinition(oldId, RepositoryFactory.getRepository());
        Response finalResponse = null;

        for (DefinitionsChildId definition : definitions) {
            ResourceResult response = rename(definition, newId);
            if (response.getStatus() == Status.INTERNAL_SERVER_ERROR) {
                return response.getResponse();
            }
            if (Objects.isNull(finalResponse) || definition.equals(oldId)) {
                finalResponse = response.getResponse();
            }
        }

        return finalResponse;
    }

    /**
     * Deletes the whole namespace in the component
     */
    public static Response delete(Class<? extends DefinitionsChildId> definitionsChildIdClazz, String namespaceStr) {
        Namespace namespace = new Namespace(namespaceStr, true);
        try {
            RepositoryFactory.getRepository().forceDelete(definitionsChildIdClazz, namespace);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
        return Response.noContent().build();
    }

    /**
     * Deletes given file/dir and returns appropriate response code
     */
    public static Response delete(GenericId id) {
        if (!RepositoryFactory.getRepository().exists(id)) {
            return Response.status(Status.NOT_FOUND).build();
        }
        try {
            RepositoryFactory.getRepository().forceDelete(id);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
        return Response.noContent().build();
    }

    /**
     * Deletes given file and returns appropriate response code
     */
    public static Response delete(RepositoryFileReference ref) {
        if (!RepositoryFactory.getRepository().exists(ref)) {
            return Response.status(Status.NOT_FOUND).build();
        }
        try {
            RepositoryFactory.getRepository().forceDelete(ref);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
        return Response.noContent().build();
    }

    /**
     * Generates given TOSCA element and returns appropriate response code <br  />
     * <p>
     * In the case of an existing resource, the other possible return code is 302. This code has no Status constant,
     * therefore we use Status.CONFLICT, which is also possible.
     *
     * @return <ul> <li> <ul> <li>Status.CREATED (201) if the resource has been created,</li> <li>Status.CONFLICT if the
     * resource already exists,</li> <li>Status.INTERNAL_SERVER_ERROR (500) if something went wrong</li> </ul> </li>
     * <li>URI: the absolute URI of the newly created resource</li> </ul>
     */
    public static ResourceResult create(GenericId id, String name) {
        ResourceResult res = new ResourceResult();
        if (RepositoryFactory.getRepository().exists(id)) {
            // res.setStatus(302);
            res.setStatus(Status.CONFLICT);
        } else {
            if (RepositoryFactory.getRepository().flagAsExisting(id)) {
                res.setStatus(Status.CREATED);
                // @formatter:off
                // This method is a generic method
                // We cannot return an "absolute" URL as the URL is always
                // relative to the caller
                // Does not work: String path = Environment.getUrlConfiguration().getRepositoryApiUrl()
                // + "/" +
                // Utils.getUrlPathForPathInsideRepo(id.getPathInsideRepo());
                // We distinguish between two cases: DefinitionsChildId and
                // TOSCAelementId
                // @formatter:on
                String path;
                if (id instanceof DefinitionsChildId) {
                    // here, we return namespace + id, as it is only possible to
                    // post on the definition child*s* resource to create an
                    // instance of a definition child
                    DefinitionsChildId tcId = (DefinitionsChildId) id;
                    path = tcId.getNamespace().getEncoded() + "/" + tcId.getXmlId().getEncoded() + "/";
                    // in case the resource additionally supports a name attribute, we set the original name
                    if ((tcId instanceof ServiceTemplateId) || (tcId instanceof ArtifactTemplateId) || (tcId instanceof PolicyTemplateId)) {
                        // these three types have an additional name (instead of a pure id)
                        // we store the name
                        IHasName resource = (IHasName) AbstractComponentsResource.getComponentInstanceResource(tcId);
                        resource.setName(name);
                    }
                } else {
                    assert (id instanceof ToscaElementId);
                    // We just return the id as we assume that only the parent
                    // of this id may create sub elements
                    path = id.getXmlId().getEncoded() + "/";
                }
                // we have to encode it twice to get correct URIs
                path = Util.getUrlPath(path);
                URI uri = URI.create(path);
                res.setUri(uri);
                res.setId(id);
            } else {
                res.setStatus(Status.INTERNAL_SERVER_ERROR);
            }
        }
        return res;
    }

    /**
     * Sends the file if modified and "not modified" if not modified future work may put each file with a unique id in a
     * separate folder in tomcat * use that static URL for each file * if file is modified, URL of file changes * ->
     * client always fetches correct file
     * <p>
     * additionally "Vary: Accept" header is added (enables caching of the response)
     * <p>
     * method header for calling method public <code>Response getXY(@HeaderParam("If-Modified-Since") String modified)
     * {...}</code>
     *
     * @param ref      references the file to be send
     * @param modified - HeaderField "If-Modified-Since" - may be "null"
     * @return Response to be sent to the client
     */
    public static Response returnRepoPath(RepositoryFileReference ref, String modified) {
        return RestUtils.returnRefAsResponseBuilder(ref, modified).build();
    }

    /**
     * This is not repository specific, but we leave it close to the only caller
     * <p>
     * If the passed ref is newer than the modified date (or the modified date is null), an OK response with an
     * inputstream pointing to the path is returned
     */
    private static Response.ResponseBuilder returnRefAsResponseBuilder(RepositoryFileReference ref, String modified) {
        if (!RepositoryFactory.getRepository().exists(ref)) {
            return Response.status(Status.NOT_FOUND);
        }

        FileTime lastModified;
        try {
            lastModified = RepositoryFactory.getRepository().getLastModifiedTime(ref);
        } catch (IOException e1) {
            LOGGER.debug("Could not get lastModifiedTime", e1);
            return Response.serverError();
        }

        // do we really need to send the file or can send "not modified"?
        if (!BackendUtils.isFileNewerThanModifiedDate(lastModified.toMillis(), modified)) {
            return Response.status(Status.NOT_MODIFIED);
        }

        Response.ResponseBuilder res;
        try {
            res = Response.ok(RepositoryFactory.getRepository().newInputStream(ref));
        } catch (IOException e) {
            LOGGER.debug("Could not open input stream", e);
            return Response.serverError();
        }
        res = res.lastModified(new Date(lastModified.toMillis()));
        // vary:accept header is always set to be safe
        res = res.header(HttpHeaders.VARY, HttpHeaders.ACCEPT);
        // determine and set MIME content type
        try {
            res = res.header(HttpHeaders.CONTENT_TYPE, RepositoryFactory.getRepository().getMimeType(ref));
        } catch (IOException e) {
            LOGGER.debug("Could not determine mime type", e);
            return Response.serverError();
        }
        // set filename
        ContentDisposition contentDisposition = ContentDisposition.type("attachment")
            .fileName(ref.getFileName())
            .modificationDate(new Date(lastModified.toMillis()))
            .build();
        res.header("Content-Disposition", contentDisposition);
        res.header("Cache-Control", "max-age=0");
        return res;
    }

    /**
     * Writes data to file. Replaces the file's content with the given content. The file does not need to exist
     *
     * @param ref     Reference to the File to write to (overwrite)
     * @param content the data to write
     * @return a JAX-RS Response containing the result. NOCONTENT if successful, InternalSeverError otherwise
     */
    public static Response putContentToFile(RepositoryFileReference ref, String content, MediaType mediaType) {
        try {
            RepositoryFactory.getRepository().putContentToFile(ref, content, org.apache.tika.mime.MediaType.parse(mediaType.toString()));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
        return Response.noContent().build();
    }

    public static Response putContentToFile(RepositoryFileReference ref, InputStream inputStream, org.apache.tika.mime.MediaType mediaType) {
        try {
            RepositoryFactory.getRepository().putContentToFile(ref, inputStream, mediaType);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
        return Response.noContent().build();
    }

    public static Response putContentToFile(RepositoryFileReference ref, InputStream inputStream, MediaType mediaType) {
        return putContentToFile(ref, inputStream, org.apache.tika.mime.MediaType.parse(mediaType.toString()));
    }

    /**
     * Updates the color if the color is not yet existent
     *
     * @param name            the name of the component. Used as basis for a generated color
     * @param qname           the QName of the color attribute
     * @param otherAttributes the plain "XML" attributes. They are used to check
     */
    public static String getColor(String name, QName qname, Map<QName, String> otherAttributes) {
        String colorStr = otherAttributes.get(qname);
        if (colorStr == null) {
            colorStr = ModelUtilities.getColor(name);
        }
        return colorStr;
    }

    /**
     * This is a quick helper method. The code should be refactored to use HasType on the element directly instead of
     * going through the resource. The method was implemented, because it is not that easy to get the id of the element
     * belonging to a resource.
     *
     * @param res a resource, where the associated element has a type. Example: EntityTypeImplementationResource
     * @return the QName of the associated type
     */
    public static QName getType(IPersistable res) {
        DefinitionsChildId id = (DefinitionsChildId) res.getRepositoryFileReference().getParent();
        final HasType element = (HasType) RepositoryFactory.getRepository().getDefinitions(id).getElement();
        return element.getTypeAsQName();
    }

    public static List<NamespaceAndDefinedLocalNamesForAngular> convert(List<NamespaceAndDefinedLocalNames> list) {
        return list.stream().map(namespaceAndDefinedLocalNames -> {
            List<LocalNameForAngular> names = namespaceAndDefinedLocalNames.getDefinedLocalNames()
                .stream().map(localName -> {
                    final String id = "{" + namespaceAndDefinedLocalNames.getNamespace().getDecoded() + "}" + localName;
                    return new LocalNameForAngular(id, localName);
                }).collect(Collectors.toList());
            return new NamespaceAndDefinedLocalNamesForAngular(
                namespaceAndDefinedLocalNames.getNamespace(), names);
        }).collect(Collectors.toList());
    }

    public static ResourceResult duplicate(DefinitionsChildId oldComponent, DefinitionsChildId newComponent) {
        ResourceResult result = create(newComponent, newComponent.getXmlId().getDecoded());

        if (result.isSuccess()) {
            // After successful creation of the new component, copy all files from old version to new version
            IRepository repository = RepositoryFactory.getRepository();
            try {
                repository.duplicate(oldComponent, newComponent);
            } catch (IOException e) {
                result.setStatus(Status.INTERNAL_SERVER_ERROR);
            }
        }

        return result;
    }

    public static Response addNewVersion(DefinitionsChildId oldId, DefinitionsChildId newComponent, List<QNameWithTypeApiData> componentsToUpdate) {
        ResourceResult creationResult = RestUtils.duplicate(oldId, newComponent);
        return creationResult.getResponse();
    }

    public static Response releaseVersion(DefinitionsChildId releasableComponent) {
        ResourceResult result = new ResourceResult();
        final IRepository repository = RepositoryFactory.getRepository();
        WineryVersion version = WineryVersionUtils.getCurrentVersionWithAllFlags(releasableComponent, repository);

        if (version.isReleasable()) {
            if (repository instanceof GitBasedRepository) {
                try {
                    freezeVersion(releasableComponent);

                    version.setWorkInProgressVersion(0);
                    String newId = releasableComponent.getNameWithoutVersion() + WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR + version.toString();
                    DefinitionsChildId newComponent = BackendUtils.getDefinitionsChildId(releasableComponent.getClass(), releasableComponent.getNamespace().getDecoded(), newId, false);
                    result = duplicate(releasableComponent, newComponent);

                    BackendUtils.commit(newComponent, "Release", repository);
                } catch (GitAPIException e) {
                    result.setStatus(Status.INTERNAL_SERVER_ERROR);
                }
            } else {
                result.setStatus(Status.INTERNAL_SERVER_ERROR);
            }
        } else {
            result.setStatus(Status.BAD_REQUEST);
        }

        return result.getResponse();
    }

    public static ResourceResult freezeVersion(DefinitionsChildId componentToCommit) {
        ResourceResult result = new ResourceResult();

        try {
            BackendUtils.commit(componentToCommit, "Freeze", RepositoryFactory.getRepository());
            result.setStatus(Status.OK);
        } catch (Exception e) {
            LOGGER.error("Error freezing component", e);
            result.setStatus(Status.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    public static <X extends DefinitionsChildId> List<QNameApiData> getAllElementsReferencingGivenType(Class<X> clazz, QName qNameOfTheType) {
        final QNameConverter adapter = new QNameConverter();
        return RepositoryFactory.getRepository()
            .getAllElementsReferencingGivenType(clazz, qNameOfTheType)
            .stream()
            .map(id -> adapter.marshal(id.getQName()))
            .collect(Collectors.toList());
    }

    public static List<ComponentId> getListOfIds(Set<? extends DefinitionsChildId> allDefinitionsChildIds,
                                                 boolean includeFullDefinitions, boolean includeVersions) {
        return allDefinitionsChildIds.stream()
            .sorted()
            .map(id -> {
                String name = id.getXmlId().getDecoded();
                TDefinitions definitions = null;
                WineryVersion version = null;
                if (Util.instanceSupportsNameAttribute(id.getClass())) {
                    TExtensibleElements element = RepositoryFactory.getRepository().getElement(id);
                    if (element instanceof IHasName) {
                        name = ((IHasName) element).getName();
                    }
                }
                if (includeFullDefinitions) {
                    definitions = getFullComponentData(id);
                }
                if (includeVersions) {
                    version = VersionUtils.getVersion(id.getXmlId().getDecoded());
                }
                return new ComponentId(id.getXmlId().getDecoded(), name, id.getNamespace().getDecoded(), id.getQName(), definitions, version);
            })
            .collect(Collectors.toList());
    }

    public static TDefinitions getFullComponentData(DefinitionsChildId id) {
        try {
            return BackendUtils.getDefinitionsHavingCorrectImports(RepositoryFactory.getRepository(), id);
        } catch (Exception e) {
            RestUtils.LOGGER.error(e.getMessage(), e);
        }

        return null;
    }
}
