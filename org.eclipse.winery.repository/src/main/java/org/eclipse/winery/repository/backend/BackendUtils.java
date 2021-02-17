/*******************************************************************************
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
 *******************************************************************************/
package org.eclipse.winery.repository.backend;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.common.Constants;
import org.eclipse.winery.model.ids.IdNames;
import org.eclipse.winery.model.ids.definitions.DataTypeId;
import org.eclipse.winery.model.ids.extensions.TopologyFragmentRefinementModelId;
import org.eclipse.winery.model.tosca.TDataType;
import org.eclipse.winery.model.version.VersionSupport;
import org.eclipse.winery.repository.backend.xsd.XsdImportManager;
import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.repository.common.Util;
import org.eclipse.winery.model.ids.GenericId;
import org.eclipse.winery.model.ids.IdUtil;
import org.eclipse.winery.model.ids.Namespace;
import org.eclipse.winery.model.ids.XmlId;
import org.eclipse.winery.model.ids.admin.AdminId;
import org.eclipse.winery.model.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.model.ids.definitions.CapabilityTypeId;
import org.eclipse.winery.model.ids.extensions.ComplianceRuleId;
import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.ids.definitions.EntityTypeId;
import org.eclipse.winery.model.ids.definitions.InterfaceTypeId;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.model.ids.extensions.PatternRefinementModelId;
import org.eclipse.winery.model.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.model.ids.definitions.PolicyTypeId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeImplementationId;
import org.eclipse.winery.model.ids.definitions.RequirementTypeId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.ids.extensions.TestRefinementModelId;
import org.eclipse.winery.model.ids.definitions.imports.XSDImportId;
import org.eclipse.winery.model.ids.elements.PlanId;
import org.eclipse.winery.model.ids.elements.PlansId;
import org.eclipse.winery.model.ids.elements.ToscaElementId;
import org.eclipse.winery.model.version.ToscaDiff;
import org.eclipse.winery.common.version.WineryVersion;
import org.eclipse.winery.model.tosca.HasIdInIdOrNameField;
import org.eclipse.winery.model.tosca.HasName;
import org.eclipse.winery.model.tosca.HasTargetNamespace;
import org.eclipse.winery.model.tosca.extensions.OTComplianceRule;
import org.eclipse.winery.model.tosca.extensions.OTPatternRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTTestRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTTopologyFragmentRefinementModel;
import org.eclipse.winery.model.tosca.RelationshipSourceOrTarget;
import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TArtifactType;
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TCapabilityType;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TImplementationArtifacts.ImplementationArtifact;
import org.eclipse.winery.model.tosca.TImport;
import org.eclipse.winery.model.tosca.TInterfaceType;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPlans;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.model.tosca.TPolicyType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TRequirementType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.Namespaces;
import org.eclipse.winery.model.tosca.extensions.kvproperties.PropertyDefinitionKV;
import org.eclipse.winery.model.tosca.extensions.kvproperties.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.GitInfo;
import org.eclipse.winery.repository.JAXBSupport;
import org.eclipse.winery.repository.backend.constants.Filename;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateFilesDirectoryId;
import org.eclipse.winery.repository.datatypes.ids.elements.DirectoryId;
import org.eclipse.winery.repository.datatypes.ids.elements.GenericDirectoryId;
import org.eclipse.winery.repository.datatypes.ids.elements.VisualAppearanceId;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;
import org.eclipse.winery.repository.export.ToscaExportUtil;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.xs.XSImplementationImpl;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.LSInput;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * Contains generic utility functions for the Backend
 * <p>
 * Contains everything that is useful for our ids etc. Does <em>not</em> contain anything that has to do with resources
 */
public class BackendUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackendUtils.class);

    private static final MediaType MEDIATYPE_APPLICATION_OCTET_STREAM = MediaType.parse("application/octet-stream");

    /**
     * @return true if given fileDate is newer then the modified date (or modified is null)
     */
    public static boolean isFileNewerThanModifiedDate(long millis, String modified) {
        if (modified == null) {
            return true;
        }

        Date modifiedDate = null;

        assert (Locale.getDefault() == Locale.ENGLISH);
        try {
            modifiedDate = DateUtils.parseDate(modified, org.eclipse.winery.repository.DateUtils.DEFAULT_PATTERNS);
        } catch (ParseException e) {
            BackendUtils.LOGGER.error(e.getMessage(), e);
        }

        if (modifiedDate != null) {
            // modifiedDate does not carry milliseconds, but fileDate does
            // therefore we have to do a range-based comparison
            if ((millis - modifiedDate.getTime()) < DateUtils.MILLIS_PER_SECOND) {
                return false;
            }
        }

        return true;
    }

    public static <T extends DefinitionsChildId> T getDefinitionsChildId(Class<T> idClass, String qnameStr) {
        QName qname = QName.valueOf(qnameStr);
        return BackendUtils.getDefinitionsChildId(idClass, qname.getNamespaceURI(), qname.getLocalPart(), false);
    }

    public static <T extends DefinitionsChildId> T getDefinitionsChildId(Class<T> idClass, QName qname) {
        // we got two implementation possibilities: one is to directly use the
        // QName constructor,
        // the other is to use a namespace, localname, urlencoded constructor
        // we opt for the latter one, which forces the latter constructor to
        // exist at all ids
        return BackendUtils.getDefinitionsChildId(idClass, qname.getNamespaceURI(), qname.getLocalPart(), false);
    }

    public static <T extends DefinitionsChildId> T getDefinitionsChildId(Class<T> idClass, String namespace, String id, boolean URLencoded) {
        Constructor<T> constructor;
        try {
            constructor = idClass.getConstructor(String.class, String.class, boolean.class);
        } catch (NoSuchMethodException | SecurityException e) {
            BackendUtils.LOGGER.error("Could not get constructor for id " + idClass.getName(), e);
            throw new IllegalStateException(e);
        }
        T tcId;
        try {
            tcId = constructor.newInstance(namespace, id, URLencoded);
        } catch (InstantiationException | IllegalAccessException
            | IllegalArgumentException | InvocationTargetException e) {
            BackendUtils.LOGGER.error("Could not create id instance", e);
            throw new IllegalStateException(e);
        }
        return tcId;
    }

    /**
     * @param id the id to determine the namespace of the parent for
     * @return the namespace of the first DefinitionsChildId found in the ID hierarchy
     */
    public static Namespace getNamespace(ToscaElementId id) {
        GenericId parent = id.getParent();
        while (!(parent instanceof DefinitionsChildId)) {
            parent = parent.getParent();
        }
        return ((DefinitionsChildId) parent).getNamespace();
    }

    /**
     * Returns an XML representation of the definitions
     * <p>
     * We return the complete definitions to allow the user changes to it, such as adding imports, etc.
     */
    public static String getDefinitionsAsXMLString(TDefinitions definitions, IRepository repository) {
        StringWriter w = new StringWriter();
        Marshaller m = JAXBSupport.createMarshaller(true, repository.getNamespaceManager().asPrefixMapper());
        try {
            m.marshal(definitions, w);
        } catch (JAXBException e) {
            LOGGER.error("Could not marshal definitions", e);
            throw new IllegalStateException(e);
        }
        return w.toString();
    }

    public static String getName(DefinitionsChildId instanceId, IRepository repo) throws RepositoryCorruptException {
        if (!repo.exists(instanceId)) {
            throw new RepositoryCorruptException("Definitions does not exist for instance");
        }
        TExtensibleElements instanceElement = repo.getDefinitions(instanceId).getElement();
        return ModelUtilities.getNameWithIdFallBack(instanceElement);
    }

    /**
     * Do <em>not</em> use this for creating URLs. Use {@link Util#getUrlPath(java.lang.String)} or
     * RestUtils#getAbsoluteURL(org.eclipse.winery.common.ids.GenericId instead.
     *
     * @return the path starting from the root element to the current element. Separated by "/", URLencoded, but
     * <b>not</b> double encoded. With trailing slash if sub-resources can exist
     * @throws IllegalStateException if id is of an unknown subclass of id
     */
    public static String getPathInsideRepo(GenericId id) {
        return Util.getPathInsideRepo(id);
    }

    /**
     * Do <em>not</em> use this for creating URLs. Use {@link Util#getUrlPath(java.lang.String)} or
     * RestUtils#getAbsoluteURL(org.eclipse.winery.common.ids.GenericId) instead.
     *
     * @return the path starting from the root element to the current element. Separated by "/", parent URLencoded.
     * Without trailing slash.
     */
    public static String getPathInsideRepo(RepositoryFileReference ref) {
        return BackendUtils.getPathInsideRepo(ref.getParent()) + getFilenameAndSubDirectory(ref);
    }

    /**
     * Returns the filename with its containing subdirectory. If the file doesn't lie in a sub directory, it only
     * returns the filename.
     *
     * @return the filename and the file's potential subdirectory.
     */
    public static String getFilenameAndSubDirectory(RepositoryFileReference ref) {
        if (ref.getSubDirectory().isPresent()) {
            return ref.getSubDirectory().get().toString().replace('\\', '/')
                + '/' + ref.getFileName();
        } else {
            return ref.getFileName();
        }
    }

    public static DefinitionsChildId getIdForRef(RepositoryFileReference ref) {
        GenericId stored = ref.getParent();
        if (stored instanceof DefinitionsChildId) {
            return (DefinitionsChildId) stored;
        }
        return null;
    }

    /**
     * Returns the reference to the definitions XML storing the TOSCA for the given id
     *
     * @param id the id to lookup
     * @return the reference
     */
    public static RepositoryFileReference getRefOfDefinitions(DefinitionsChildId id) {
        return new RepositoryFileReference(id, getFileNameOfDefinitions(id));
    }

    public static String getFileNameOfDefinitions(DefinitionsChildId id) {
        return getFileNameOfDefinitions(id.getClass());
    }

    public static <T extends DefinitionsChildId> String getFileNameOfDefinitions(Class<T> id) {
        String name = IdUtil.getTypeForComponentId(id);
        name = name + Constants.SUFFIX_TOSCA_DEFINITIONS;
        return name;
    }

    /**
     * @return Singular type name for the given id. E.g., "ServiceTemplateId" gets "ServiceTemplate"
     */
    public static String getTypeForAdminId(Class<? extends AdminId> idClass) {
        return IdUtil.getEverythingBetweenTheLastDotAndBeforeId(idClass);
    }

    /**
     * Returns the reference to the properties file storing the TOSCA information for the given id
     *
     * @param id the id to lookup
     * @return the reference
     */
    public static RepositoryFileReference getRefOfConfiguration(GenericId id) {
        String name;
        // Hack to determine file name
        if (id instanceof DefinitionsChildId) {
            name = IdUtil.getTypeForComponentId(((DefinitionsChildId) id).getClass());
            name = name + Constants.SUFFIX_PROPERTIES;
        } else if (id instanceof AdminId) {
            name = BackendUtils.getTypeForAdminId(((AdminId) id).getClass());
            name = name + Constants.SUFFIX_PROPERTIES;
        } else {
            assert (id instanceof ToscaElementId);
            ToscaElementId tId = (ToscaElementId) id;
            if (tId instanceof PlansId) {
                name = Filename.FILENAME_PROPERTIES_PLANCONTAINER;
            } else if (tId instanceof VisualAppearanceId) {
                // quick hack for special name here
                name = Filename.FILENAME_PROPERTIES_VISUALAPPEARANCE;
            } else {
                name = Util.getTypeForElementId(tId.getClass()) + Constants.SUFFIX_PROPERTIES;
            }
        }

        return new RepositoryFileReference(id, name);
    }

    public static RepositoryFileReference getRefOfJsonConfiguration(GenericId id) {
        String name = "";

        if (id instanceof AdminId) {
            name = BackendUtils.getTypeForAdminId(((AdminId) id).getClass());
            name += Constants.SUFFIX_JSON;
        }

        return new RepositoryFileReference(id, name);
    }

    /**
     * Returns a list of the topology template nested in the given service template
     */
    public static List<TNodeTemplate> getAllNestedNodeTemplates(TServiceTemplate serviceTemplate) {
        List<TNodeTemplate> l = new ArrayList<>();
        TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();
        if (topologyTemplate == null) {
            return Collections.emptyList();
        }
        for (TEntityTemplate t : topologyTemplate.getNodeTemplateOrRelationshipTemplate()) {
            if (t instanceof TNodeTemplate) {
                l.add((TNodeTemplate) t);
            }
        }
        return l;
    }

    @NonNull
    private static Collection<QName> getAllReferencedArtifactTemplates(TDeploymentArtifacts tDeploymentArtifacts) {
        if (tDeploymentArtifacts == null) {
            return Collections.emptyList();
        }
        List<TDeploymentArtifact> deploymentArtifacts = tDeploymentArtifacts.getDeploymentArtifact();
        if (deploymentArtifacts == null) {
            return Collections.emptyList();
        }
        Collection<QName> res = new ArrayList<>();
        for (TDeploymentArtifact da : deploymentArtifacts) {
            QName artifactRef = da.getArtifactRef();
            if (artifactRef != null) {
                res.add(artifactRef);
            }
        }
        return res;
    }

    private static Collection<QName> getAllReferencedArtifactTemplates(TImplementationArtifacts tImplementationArtifacts) {
        if (tImplementationArtifacts == null) {
            return Collections.emptyList();
        }
        List<ImplementationArtifact> implementationArtifacts = tImplementationArtifacts.getImplementationArtifact();
        if (implementationArtifacts == null) {
            return Collections.emptyList();
        }
        Collection<QName> res = new ArrayList<>();
        for (ImplementationArtifact ia : implementationArtifacts) {
            QName artifactRef = ia.getArtifactRef();
            if (artifactRef != null) {
                res.add(artifactRef);
            }
        }
        return res;
    }

    public static Collection<QName> getArtifactTemplatesOfReferencedDeploymentArtifacts(TNodeTemplate nodeTemplate, IRepository repo) {
        List<QName> l = new ArrayList<>();

        // DAs may be assigned directly to a node template
        Collection<QName> allReferencedArtifactTemplates = BackendUtils.getAllReferencedArtifactTemplates(nodeTemplate.getDeploymentArtifacts());
        l.addAll(allReferencedArtifactTemplates);

        // DAs may be assigned via node type implementations
        QName nodeTypeQName = nodeTemplate.getType();
        Collection<NodeTypeImplementationId> allNodeTypeImplementations = repo.getAllElementsReferencingGivenType(NodeTypeImplementationId.class, nodeTypeQName);
        for (NodeTypeImplementationId nodeTypeImplementationId : allNodeTypeImplementations) {
            TDeploymentArtifacts deploymentArtifacts = repo.getElement(nodeTypeImplementationId).getDeploymentArtifacts();
            allReferencedArtifactTemplates = BackendUtils.getAllReferencedArtifactTemplates(deploymentArtifacts);
            l.addAll(allReferencedArtifactTemplates);
        }

        return l;
    }

    public static Collection<QName> getArtifactTemplatesOfReferencedImplementationArtifacts(TNodeTemplate nodeTemplate, IRepository repo) {
        List<QName> l = new ArrayList<>();

        // IAs may be assigned via node type implementations
        QName nodeTypeQName = nodeTemplate.getType();
        Collection<NodeTypeImplementationId> allNodeTypeImplementations = repo.getAllElementsReferencingGivenType(NodeTypeImplementationId.class, nodeTypeQName);
        for (NodeTypeImplementationId nodeTypeImplementationId : allNodeTypeImplementations) {
            TImplementationArtifacts implementationArtifacts = repo.getElement(nodeTypeImplementationId).getImplementationArtifacts();
            Collection<QName> allReferencedArtifactTemplates = BackendUtils.getAllReferencedArtifactTemplates(implementationArtifacts);
            l.addAll(allReferencedArtifactTemplates);
        }

        return l;
    }

    /**
     * Creates a new TDefintions element wrapping a definition child. The namespace of the tosca component is used as
     * namespace and {@code winery-defs-for-} concatenated with the (unique) ns prefix and idOfContainedElement is used
     * as id
     *
     * @param tcId the id of the element the wrapper is used for
     * @param defs the definitions to update
     * @return a definitions element prepared for wrapping a definition child
     */
    public static TDefinitions updateWrapperDefinitions(DefinitionsChildId tcId, TDefinitions defs, IRepository repo) {
        // set target namespace
        // an internal namespace is not possible
        //   a) tPolicyTemplate and tArtfactTemplate do NOT support the "targetNamespace" attribute
        //   b) the imports statement would look bad as it always imported the artificial namespace
        defs.setTargetNamespace(tcId.getNamespace().getDecoded());

        // set a unique id to create a valid definitions element
        // we do not use UUID to be more human readable and deterministic (for debugging)
        String prefix = repo.getNamespaceManager().getPrefix(tcId.getNamespace());
        String elId = tcId.getXmlId().getDecoded();
        String id = "winery-defs-for_" + prefix + "-" + elId;
        defs.setId(id);
        return defs;
    }

    /**
     * @param topologyTemplate which should be cloned
     * @return Copy od topologyTemplate
     */
    public static TTopologyTemplate clone(TTopologyTemplate topologyTemplate) {
        @SuppressWarnings("deprecated")
        TTopologyTemplate topologyTemplateClone = new TTopologyTemplate();
        List<TEntityTemplate> entityTemplate = topologyTemplate.getNodeTemplateOrRelationshipTemplate();
        topologyTemplateClone.getNodeTemplateOrRelationshipTemplate().addAll(entityTemplate);
        return topologyTemplateClone;
    }

    /**
     * @param nodeTemplate which should be cloned
     * @return copy of nodeTemplate
     */
    public static TNodeTemplate clone(TNodeTemplate nodeTemplate) {
        TNodeTemplate nodeTemplateClone = new TNodeTemplate();

        nodeTemplateClone.setType(nodeTemplate.getType());
        nodeTemplateClone.setId(nodeTemplate.getId());
        nodeTemplateClone.setDeploymentArtifacts(nodeTemplate.getDeploymentArtifacts());
        // returns 1 if null -> !original.equals(cloned)
        nodeTemplateClone.setMaxInstances(nodeTemplate.getMaxInstances());
        nodeTemplateClone.setMinInstances(nodeTemplate.getMinInstances());
        nodeTemplateClone.setName(nodeTemplate.getName());
        nodeTemplateClone.setPolicies(nodeTemplate.getPolicies());
        nodeTemplateClone.setRequirements(nodeTemplate.getRequirements());
        nodeTemplateClone.setCapabilities(nodeTemplate.getCapabilities());
        nodeTemplateClone.setProperties(nodeTemplate.getProperties());
        nodeTemplateClone.setPropertyConstraints(nodeTemplate.getPropertyConstraints());
        if (Objects.nonNull(nodeTemplate.getX())) {
            nodeTemplateClone.setX(nodeTemplate.getX());
        }
        if (Objects.nonNull(nodeTemplate.getY())) {
            nodeTemplateClone.setY(nodeTemplate.getY());
        }

        if (ModelUtilities.getTargetLabel(nodeTemplate).isPresent()) {
            ModelUtilities.setTargetLabel(nodeTemplateClone, ModelUtilities.getTargetLabel(nodeTemplate).get());
        }

        String region = nodeTemplate.getOtherAttributes().get(ModelUtilities.NODE_TEMPLATE_REGION);
        if (Objects.nonNull(region)) {
            nodeTemplateClone.getOtherAttributes().put(ModelUtilities.NODE_TEMPLATE_REGION, region);
        }

        String provider = nodeTemplate.getOtherAttributes().get(ModelUtilities.NODE_TEMPLATE_PROVIDER);
        if (Objects.nonNull(provider)) {
            nodeTemplateClone.getOtherAttributes().put(ModelUtilities.NODE_TEMPLATE_PROVIDER, provider);
        }

        return nodeTemplateClone;
    }

    /**
     * @param relationshipTemplate which should be cloned
     * @return copy of relationshipTemplate
     */
    public static TRelationshipTemplate clone(TRelationshipTemplate relationshipTemplate) {
        TRelationshipTemplate relationshipTemplateClone = new TRelationshipTemplate();
        relationshipTemplateClone.setSourceElement(relationshipTemplate.getSourceElement());
        relationshipTemplateClone.setType(relationshipTemplate.getType());
        relationshipTemplateClone.setPropertyConstraints(relationshipTemplate.getPropertyConstraints());
        relationshipTemplateClone.setTargetElement(relationshipTemplate.getTargetElement());
        relationshipTemplateClone.setId(relationshipTemplate.getId());
        relationshipTemplateClone.setProperties(relationshipTemplate.getProperties());
        relationshipTemplateClone.setName(relationshipTemplate.getName());
        relationshipTemplateClone.setRelationshipConstraints(relationshipTemplate.getRelationshipConstraints());

        String transferType =
            relationshipTemplate.getOtherAttributes().get(ModelUtilities.RELATIONSHIP_TEMPLATE_TRANSFER_TYPE);
        if (Objects.nonNull(transferType)) {
            relationshipTemplateClone.getOtherAttributes().put(ModelUtilities.RELATIONSHIP_TEMPLATE_TRANSFER_TYPE, transferType);
        }

        return relationshipTemplateClone;
    }

    /*
     * Creates a new TDefintions element wrapping a definition child.
     * The namespace of the definition child is used as namespace and
     * {@code winery-defs-for-} concatenated with the (unique) ns prefix and
     * idOfContainedElement is used as id
     *
     * @param tcId the id of the element the wrapper is used for
     * @return a definitions element prepared for wrapping a definition child instance
     */
    public static TDefinitions createWrapperDefinitions(DefinitionsChildId tcId, IRepository repo) {
        TDefinitions defs = new TDefinitions();
        return updateWrapperDefinitions(tcId, defs, repo);
    }

    public static TDefinitions createWrapperDefinitionsAndInitialEmptyElement(IRepository repository, DefinitionsChildId id) {
        final TDefinitions definitions = createWrapperDefinitions(id, repository);
        HasIdInIdOrNameField element;
        if (id instanceof RelationshipTypeImplementationId) {
            element = new TRelationshipTypeImplementation();
        } else if (id instanceof NodeTypeImplementationId) {
            element = new TNodeTypeImplementation();
        } else if (id instanceof RequirementTypeId) {
            element = new TRequirementType();
        } else if (id instanceof NodeTypeId) {
            element = new TNodeType();
        } else if (id instanceof RelationshipTypeId) {
            element = new TRelationshipType();
        } else if (id instanceof CapabilityTypeId) {
            element = new TCapabilityType();
        } else if (id instanceof DataTypeId) { 
            element = new TDataType();
        } else if (id instanceof ArtifactTypeId) {
            element = new TArtifactType();
        } else if (id instanceof PolicyTypeId) {
            element = new TPolicyType();
        } else if (id instanceof PolicyTemplateId) {
            element = new TPolicyTemplate();
        } else if (id instanceof ServiceTemplateId) {
            element = new TServiceTemplate();
        } else if (id instanceof ArtifactTemplateId) {
            element = new TArtifactTemplate();
        } else if (id instanceof ComplianceRuleId) {
            element = new OTComplianceRule(new OTComplianceRule.Builder(id.getXmlId().getDecoded()));
        } else if (id instanceof PatternRefinementModelId) {
            element = new OTPatternRefinementModel(new OTPatternRefinementModel.Builder());
        } else if (id instanceof TopologyFragmentRefinementModelId) {
            element = new OTTopologyFragmentRefinementModel(new OTPatternRefinementModel.Builder());
        } else if (id instanceof TestRefinementModelId) {
            element = new OTTestRefinementModel(new OTTestRefinementModel.Builder());
        } else if (id instanceof InterfaceTypeId) {
            element = new TInterfaceType();
        } else if (id instanceof XSDImportId) {
            // TImport has no id; thus directly generating it without setting an id
            TImport tImport = new TImport();
            definitions.setElement(tImport);
            return definitions;
        } else {
            throw new IllegalStateException("Unhandled id branch. Could happen for XSDImportId");
        }
        copyIdToFields(element, id);
        definitions.setElement((TExtensibleElements) element);
        return definitions;
    }

    /**
     * Properties need to be initialized in the case of K/V Properties
     *
     * @param repository     The repository to work on
     * @param entityTemplate the entity template to update
     */
    public static void initializeProperties(IRepository repository, TEntityTemplate entityTemplate) {
        Objects.requireNonNull(repository);
        Objects.requireNonNull(entityTemplate);

        Objects.requireNonNull(entityTemplate.getType());

        final TEntityType entityType = repository.getTypeForTemplate(entityTemplate);
        final WinerysPropertiesDefinition winerysPropertiesDefinition = entityType.getWinerysPropertiesDefinition();
        if (winerysPropertiesDefinition == null) {
            return;
        }
        final LinkedHashMap<String, String> emptyKVProperties = new LinkedHashMap<>();
        for (PropertyDefinitionKV definitionKV : winerysPropertiesDefinition.getPropertyDefinitions()) {
            emptyKVProperties.put(definitionKV.getKey(), "");
        }
        TEntityTemplate.WineryKVProperties properties = new TEntityTemplate.WineryKVProperties();
        properties.setNamespace(winerysPropertiesDefinition.getNamespace());
        properties.setElementName(winerysPropertiesDefinition.getElementName());
        properties.setKVProperties(emptyKVProperties);
        entityTemplate.setProperties(properties);
    }

    /**
     * Regenerates wrapper definitions; thus all extensions at the wrapper definitions are lost
     *
     * @param id      the id of the definition child to persist
     * @param element the element of the definition child
     */
    public static void persist(IRepository repository, DefinitionsChildId id, TExtensibleElements element) throws IOException {
        repository.setElement(id, element);
    }

    /**
     * Persists the given definitions
     *  @param id          the id of the definition child to persist
     * @param definitions the definitions to persist
     */
    public static void persist(IRepository repository, DefinitionsChildId id, TDefinitions definitions) throws IOException {
        repository.putDefinition(id, definitions);
    }

    public static void persist(IRepository repository, RepositoryFileReference ref, TDefinitions definitions) throws IOException {
        repository.putDefinition(ref, definitions);
    }

    // todo this should not depend on JAXB !

    /**
     * @deprecated Instead use {@link IRepository#putDefinition(DefinitionsChildId, TDefinitions)} or {@link IRepository#putContentToFile(RepositoryFileReference, InputStream, MediaType)}
     * @throws IOException           if content could not be updated in the repository
     * @throws IllegalStateException if an JAXBException occurred. This should never happen.
     */
    @Deprecated
    public static void persist(Object o, RepositoryFileReference ref, MediaType mediaType, IRepository repo) throws IOException {
        // We assume that the object is not too large
        // Otherwise, http://io-tools.googlecode.com/svn/www/easystream/apidocs/index.html should be used
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Marshaller m = JAXBSupport.createMarshaller(true, repo.getNamespaceManager().asPrefixMapper());
            m.marshal(o, out);
            byte[] data = out.toByteArray();
            try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
                // String xml = IOUtils.toString(in);
                // this may throw an IOException. We propagate this exception.
                repo.putContentToFile(ref, in, mediaType);
            }
        } catch (JAXBException e) {
            BackendUtils.LOGGER.error("Could not put content to file", e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * @param ref the file to read from
     * @param repo
     */
    public static Optional<XSModel> getXSModel(final RepositoryFileReference ref, IRepository repo) {
        Objects.requireNonNull(ref);
        try (final InputStream is = repo.newInputStream(ref)) {
            // we rely on xerces to parse the XSD
            // idea based on http://stackoverflow.com/a/5165177/873282
            XSImplementation impl = new XSImplementationImpl();
            XSLoader schemaLoader = impl.createXSLoader(null);

            // minimal LSInput implementation sufficient for XSLoader in Oracle's JRE7
            LSInput input = new LSInput() {

                @Override
                public void setSystemId(String systemId) {
                }

                @Override
                public void setStringData(String stringData) {
                }

                @Override
                public void setPublicId(String publicId) {
                }

                @Override
                public void setEncoding(String encoding) {
                }

                @Override
                public void setCharacterStream(Reader characterStream) {
                }

                @Override
                public void setCertifiedText(boolean certifiedText) {
                }

                @Override
                public void setByteStream(InputStream byteStream) {
                }

                @Override
                public void setBaseURI(String baseURI) {
                }

                @Override
                public String getSystemId() {
                    return null;
                }

                @Override
                public String getStringData() {
                    return null;
                }

                @Override
                public String getPublicId() {
                    return BackendUtils.getPathInsideRepo(ref);
                }

                @Override
                public String getEncoding() {
                    return "UTF-8";
                }

                @Override
                public Reader getCharacterStream() {
                    try {
                        return new InputStreamReader(is, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        System.out.println("exeption");
                        throw new IllegalStateException("UTF-8 is unkown", e);
                    }
                }

                @Override
                public boolean getCertifiedText() {
                    return false;
                }

                @Override
                public InputStream getByteStream() {
                    return null;
                }

                @Override
                public String getBaseURI() {
                    return null;
                }
            };
            return Optional.ofNullable(schemaLoader.load(input));
        } catch (IOException e) {
            BackendUtils.LOGGER.debug("Could not create input stream", e);
            return Optional.empty();
        }
    }

    /**
     * Derives Winery's Properties Definition from an existing properties definition
     *  @param ci     the entity type to try to modify the WPDs
     * @param errors the list to add errors to
     * @param repository
     */
    // FIXME this is specifically for xml backends and therefore broken under the new canonical model
    public static void deriveWPD(TEntityType ci, List<String> errors, IRepository repository) {
        BackendUtils.LOGGER.trace("deriveWPD");
        TEntityType.PropertiesDefinition propertiesDefinition = ci.getProperties();
        if (propertiesDefinition == null) {
            // if there's no properties definition, there's nothing to derive because we're in YAML mode
            return;
        }
        if (!(propertiesDefinition instanceof TEntityType.XmlElementDefinition)) {
            BackendUtils.LOGGER.debug("only works for an element definition, not for types");
            return;
        }
        final QName element = ((TEntityType.XmlElementDefinition) propertiesDefinition).getElement();
        BackendUtils.LOGGER.debug("Looking for the definition of {" + element.getNamespaceURI() + "}" + element.getLocalPart());
        // fetch the XSD defining the element
        final XsdImportManager xsdImportManager = repository.getXsdImportManager();
        Map<String, RepositoryFileReference> mapFromLocalNameToXSD = xsdImportManager.getMapFromLocalNameToXSD(new Namespace(element.getNamespaceURI(), false), false);
        RepositoryFileReference ref = mapFromLocalNameToXSD.get(element.getLocalPart());
        if (ref == null) {
            String msg = "XSD not found for " + element.getNamespaceURI() + " / " + element.getLocalPart();
            BackendUtils.LOGGER.debug(msg);
            errors.add(msg);
            return;
        }

        final Optional<XSModel> xsModelOptional = BackendUtils.getXSModel(ref, repository);
        if (!xsModelOptional.isPresent()) {
            LOGGER.error("no XSModel found");
        }
        XSModel xsModel = xsModelOptional.get();
        XSElementDeclaration elementDeclaration = xsModel.getElementDeclaration(element.getLocalPart(), element.getNamespaceURI());
        if (elementDeclaration == null) {
            String msg = "XSD model claimed to contain declaration for {" + element.getNamespaceURI() + "}" + element.getLocalPart() + ", but it did not.";
            BackendUtils.LOGGER.debug(msg);
            errors.add(msg);
            return;
        }

        // go through the XSD definition and
        XSTypeDefinition typeDefinition = elementDeclaration.getTypeDefinition();
        if (!(typeDefinition instanceof XSComplexTypeDefinition)) {
            BackendUtils.LOGGER.debug("XSD does not follow the requirements put by winery: No Complex Type Definition");
            return;
        }
        XSComplexTypeDefinition cTypeDefinition = (XSComplexTypeDefinition) typeDefinition;
        XSParticle particle = cTypeDefinition.getParticle();
        if (particle == null) {
            BackendUtils.LOGGER.debug("XSD does not follow the requirements put by winery: Complex type does not contain particles");
            return;
        }
        XSTerm term = particle.getTerm();
        if (!(term instanceof XSModelGroup)) {
            BackendUtils.LOGGER.debug("XSD does not follow the requirements put by winery: Not a model group");
            return;
        }
        XSModelGroup modelGroup = (XSModelGroup) term;
        if (modelGroup.getCompositor() != XSModelGroup.COMPOSITOR_SEQUENCE) {
            BackendUtils.LOGGER.debug("XSD does not follow the requirements put by winery: Model group is not a sequence");
            return;
        }
        XSObjectList particles = modelGroup.getParticles();
        int len = particles.getLength();
        boolean everyThingIsASimpleType = true;
        List<PropertyDefinitionKV> list = new ArrayList<>();
        if (len != 0) {
            for (int i = 0; i < len; i++) {
                XSParticle innerParticle = (XSParticle) particles.item(i);
                XSTerm innerTerm = innerParticle.getTerm();
                if (innerTerm instanceof XSElementDeclaration) {
                    XSElementDeclaration innerElementDeclaration = (XSElementDeclaration) innerTerm;
                    String name = innerElementDeclaration.getName();
                    XSTypeDefinition innerTypeDefinition = innerElementDeclaration.getTypeDefinition();
                    if (innerTypeDefinition instanceof XSSimpleType) {
                        XSSimpleType xsSimpleType = (XSSimpleType) innerTypeDefinition;
                        String typeNS = xsSimpleType.getNamespace();
                        String typeName = xsSimpleType.getName();
                        if (typeNS.equals(XMLConstants.W3C_XML_SCHEMA_NS_URI)) {
                            PropertyDefinitionKV def = new PropertyDefinitionKV();
                            def.setKey(name);
                            // convention at WPD: use "xsd" as prefix for XML Schema Definition
                            def.setType("xsd:" + typeName);
                            list.add(def);
                        } else {
                            everyThingIsASimpleType = false;
                            break;
                        }
                    } else {
                        everyThingIsASimpleType = false;
                        break;
                    }
                } else {
                    everyThingIsASimpleType = false;
                    break;
                }
            }
        }
        if (!everyThingIsASimpleType) {
            BackendUtils.LOGGER.debug("XSD does not follow the requirements put by winery: Not all types in the sequence are simple types");
            return;
        }
        // everything went alright, we can add a WPD
        WinerysPropertiesDefinition wpd = new WinerysPropertiesDefinition();
        wpd.setIsDerivedFromXSD(Boolean.TRUE);
        wpd.setElementName(element.getLocalPart());
        wpd.setNamespace(element.getNamespaceURI());
        wpd.setPropertyDefinitions(list);
        ModelUtilities.replaceWinerysPropertiesDefinition(ci, wpd);
        BackendUtils.LOGGER.debug("Successfully generated WPD");
    }

    /**
     * Returns all components available of the given id type
     * <p>
     * Similar functionality as {@link IRepository#getAllDefinitionsChildIds(java.lang.Class)}, but it crawls through
     * the repository
     * <p>
     * This method is required as we do not use a database.
     *
     * @param idClass class of the Ids to search for
     * @return empty set if no ids are available
     */
    public <T extends ToscaElementId> SortedSet<T> getAllTOSCAElementIds(Class<T> idClass) {
        throw new IllegalStateException("Not yet implemented");

		/*
		 Implementation idea:
		   * switch of instance of idClass
		   * nodetemplate / relationshiptemplate -> fetch all service templates -> crawl through topology -> add all to res
		   * req/cap do as above, but inspect nodetemplate
		   * (other special handlings; check spec where each type can be linked from)
		 */
    }

    /**
     * Converts the given collection of definition children Ids to a collection of QNames by using the getQName()
     * method.
     * <p>
     * This is required for QNameChooser.tag
     */
    public static Collection<QName> convertDefinitionsChildIdCollectionToQNameCollection(Collection<? extends DefinitionsChildId> col) {
        Collection<QName> res = new ArrayList<>();
        for (DefinitionsChildId id : col) {
            res.add(id.getQName());
        }
        return res;
    }

    /**
     * Detect the mime type of the stream. The stream is marked at the beginning and reset at the end
     *
     * @param bis the stream
     * @param fn  the fileName of the file belonging to the stream
     */
    public static MediaType getMimeType(BufferedInputStream bis, String fn) throws IOException {
        AutoDetectParser parser = new AutoDetectParser();
        Detector detector = parser.getDetector();
        Metadata md = new Metadata();
        md.add(Metadata.RESOURCE_NAME_KEY, fn);
        final MediaType mediaType = detector.detect(bis, md);
        return mediaType;
    }

    /**
     * Fixes the mediaType if it is too vague (such as application/octet-stream)
     *
     * @return a more fitting MediaType or the original one if it is appropriate enough
     */
    public static MediaType getFixedMimeType(BufferedInputStream is, String fileName, MediaType mediaType) {
        if (mediaType.equals(MEDIATYPE_APPLICATION_OCTET_STREAM)) {
            // currently, we fix application/octet-stream only

            // TODO: instead of using apache tika, we could hve a user-configured map storing
            //  * media type
            //  * file extension

            try {
                return BackendUtils.getMimeType(is, fileName);
            } catch (Exception e) {
                BackendUtils.LOGGER.debug("Could not determine mimetype for " + fileName, e);
                // just keep the old one
                return mediaType;
            }
        } else {
            return mediaType;
        }
    }

    /**
     * Copies the given id resource to the appropriate fields in the element.
     * <p>
     * For instance, the id is put in the "name" field for EntityTypes
     */
    public static void copyIdToFields(HasIdInIdOrNameField element, DefinitionsChildId id) {
        element.setId(id.getXmlId().getDecoded());
        if (element instanceof HasTargetNamespace) {
            ((HasTargetNamespace) element).setTargetNamespace(id.getNamespace().getDecoded());
        }
        // Required for creating a new version in order to also update the name
        if (element instanceof HasName) {
            ((HasName) element).setName(id.getXmlId().getDecoded());
        }
    }

    /**
     * @param directoryId DirectoryId of the ArtifactTemplate that should contain a reference to a git repository.
     * @return The URL and the branch/tag that contains the files for the ArtifactTemplate. null if no git information
     * is given.
     */
    public static GitInfo getGitInformation(DirectoryId directoryId, IRepository repo) {
        if (!(directoryId.getParent() instanceof ArtifactTemplateId)) {
            return null;
        }
        RepositoryFileReference ref = BackendUtils.getRefOfDefinitions((ArtifactTemplateId) directoryId.getParent());
        try {
            TDefinitions defs = repo.definitionsFromRef(ref);
            Map<QName, String> atts = defs.getOtherAttributes();
            String src = atts.get(new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "gitsrc"));
            String branch = atts.get(new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "gitbranch"));
            if (src == null && branch == null) {
                return null;
            }
            if (src == null || branch == null) {
                LOGGER.error("Git information not complete, URL or branch missing");
                return null;
            }
            return new GitInfo(src, branch);
        } catch (IOException e) {
            LOGGER.error("Error reading definitions of " + directoryId.getParent() + " at " + ref.getFileName(), e);
        }
        return null;
    }

    /**
     * @param directoryId DirectoryID of the TArtifactTemplate that should be returned.
     * @return The TArtifactTemplate corresponding to the directoryId.
     */
    public static TArtifactTemplate getTArtifactTemplate(DirectoryId directoryId, IRepository repo) {
        RepositoryFileReference ref = BackendUtils.getRefOfDefinitions((ArtifactTemplateId) directoryId.getParent());
        try (InputStream is = repo.newInputStream(ref)) {
            Unmarshaller u = JAXBSupport.createUnmarshaller();
            TDefinitions defs = ((TDefinitions) u.unmarshal(is));
            for (TExtensibleElements elem : defs.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
                if (elem instanceof TArtifactTemplate) {
                    return (TArtifactTemplate) elem;
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error reading definitions of " + directoryId.getParent() + " at " + ref.getFileName(), e);
        } catch (JAXBException e) {
            LOGGER.error("Error in XML in " + ref.getFileName(), e);
        }
        return null;
    }

    public static DirectoryId getYamlArtifactsDirectoryOfNodeTemplate(ServiceTemplateId serviceTemplateId, String nodeTemplateId) {
        DirectoryId serviceTemplateYamlArtifactsDir =
            new GenericDirectoryId(serviceTemplateId, IdNames.FILES_DIRECTORY);
        return new GenericDirectoryId(serviceTemplateYamlArtifactsDir, nodeTemplateId);
    }

    public static DirectoryId getYamlArtifactDirectoryOfNodeTemplate(ServiceTemplateId serviceTemplateId,
                                                                     String nodeTemplateId, String yamlArtifactId) {
        DirectoryId nodeTemplateYamlArtifactsDir = getYamlArtifactsDirectoryOfNodeTemplate(serviceTemplateId, nodeTemplateId);
        return new GenericDirectoryId(nodeTemplateYamlArtifactsDir, yamlArtifactId);
    }

    /**
     * Tests if a path matches a glob pattern. @see <a href="https://en.wikipedia.org/wiki/Glob_(programming)">Wikipedia</a>
     *
     * @param glob Glob pattern to test the path against.
     * @param path Path that should match the glob pattern.
     * @return Whether the glob and the path result in a match.
     */
    public static boolean isGlobMatch(String glob, Path path) {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + glob);
        return matcher.matches(path);
    }

    public static boolean injectArtifactTemplateIntoDeploymentArtifact(ServiceTemplateId serviceTemplate, String nodeTemplateId, String deploymentArtifactId, ArtifactTemplateId artifactTemplate, IRepository repo) throws IOException {
        TServiceTemplate element = repo.getElement(serviceTemplate);
        element.getTopologyTemplate().getNodeTemplate(nodeTemplateId).getDeploymentArtifacts().getDeploymentArtifact(deploymentArtifactId).setArtifactRef(artifactTemplate.getQName());
        repo.setElement(serviceTemplate, element);
        return true;
    }

    /**
     * @param tcId                    The element type id to get the location for
     * @param uri                     uri to use if in XML export mode, null if in CSAR export mode
     * @param wrapperElementLocalName the local name of the wrapper element
     */
    public static String getImportLocationForWinerysPropertiesDefinitionXSD(EntityTypeId tcId, URI uri, String wrapperElementLocalName) {
        String loc = Util.getPathInsideRepo(tcId);
        loc = loc + "propertiesdefinition/";
        loc = Util.getUrlPath(loc);
        if (uri == null) {
            loc = loc + wrapperElementLocalName + ".xsd";
            // for the import later, we need "../" in front
            loc = "../" + loc;
        } else {
            loc = uri + loc + "xsd";
        }
        return loc;
    }

    /**
     * Synchronizes the list of files of the given artifact template with the list of files contained in the given
     * repository. The repository is updated after synchronization.
     * <p>
     * This was intended if a user manually added files in the "files" directory and expected winery to correctly export
     * a CSAR
     *
     * @param repository The repository to search for the files
     * @param id         the id of the artifact template
     * @return The synchronized artifact template. Used for testing only, because mockito cannot mock static methods
     * (https://github.com/mockito/mockito/issues/1013).
     */
    public static TArtifactTemplate synchronizeReferences(IRepository repository, ArtifactTemplateId id) throws IOException {
        TArtifactTemplate template = repository.getElement(id);
        List<TArtifactReference> toRemove = new ArrayList<>();
        List<RepositoryFileReference> toAdd = new ArrayList<>();
        TArtifactTemplate.ArtifactReferences artifactReferences = template.getArtifactReferences();
        DirectoryId fileDir = new ArtifactTemplateFilesDirectoryId(id);
        SortedSet<RepositoryFileReference> files = repository.getContainedFiles(fileDir);

        if (artifactReferences == null) {
            artifactReferences = new TArtifactTemplate.ArtifactReferences();
            template.setArtifactReferences(artifactReferences);
        }

        List<TArtifactReference> artRefList = artifactReferences.getArtifactReference();
        determineChanges(artRefList, files, toRemove, toAdd);

        if (toAdd.size() > 0 || toRemove.size() > 0) {
            // apply removal list
            toRemove.forEach(artRefList::remove);

            // apply addition list
            artRefList.addAll(toAdd.stream().map(fileRef -> {
                String path = Util.getUrlPath(fileRef);

                // put path into data structure
                // we do not use Include/Exclude as we directly reference a concrete file
                TArtifactReference artRef = new TArtifactReference();
                artRef.setReference(path);

                return artRef;
            }).collect(Collectors.toList()));

            // finally, persist only if something changed
            BackendUtils.persist(repository, id, template);
        }

        return template;
    }

    /**
     * determines the difference between the list of artifact references (derived from the template) and the actual
     * files stored on disk
     *
     * @param artRefList  the list of artifact references derived from the corresponding artifact template
     * @param filesOnDisk the list of files actually stored on disk
     * @param toRemove    the items to remove from the artifact list (output)
     * @param toAdd       the items to add to the artifact list (output)
     */
    private static void determineChanges(List<TArtifactReference> artRefList, SortedSet<RepositoryFileReference> filesOnDisk, List<TArtifactReference> toRemove, List<RepositoryFileReference> toAdd) {
        // first find references to remove
        for (TArtifactReference reference : artRefList) {
            try {
                String urlString = reference.getReference();

                // we leave out the absolute paths
                if (!(new URI(urlString)).isAbsolute()) {
                    if (filesOnDisk.stream().noneMatch(file -> Util.getUrlPath(file).equals(urlString))) {
                        // we remove references not pointing to a file
                        toRemove.add(reference);
                    }
                }
            } catch (URISyntaxException e) {
                // we remove malformed references
                toRemove.add(reference);
            }
        }

        // second find references to add
        for (RepositoryFileReference file : filesOnDisk) {
            if (artRefList.stream().noneMatch(ref -> ref.getReference().equals(Util.getUrlPath(file)))) {
                toAdd.add(file);
            }
        }
    }

    /**
     * Synchronizes the known plans with the data in the XML. When there is a stored file, but no known entry in the
     * XML, we guess "BPEL" as language and "buildProvenanceSmartContract plan" as type.
     */
    public static void synchronizeReferences(ServiceTemplateId id, IRepository repository) throws IOException {
        final TServiceTemplate serviceTemplate = repository.getElement(id);
        // locally stored plans
        TPlans plans = serviceTemplate.getPlans();

        // plans stored in the repository
        PlansId plansContainerId = new PlansId(id);
        SortedSet<PlanId> nestedPlans = repository.getNestedIds(plansContainerId, PlanId.class);

        Set<PlanId> plansToAdd = new HashSet<>();
        plansToAdd.addAll(nestedPlans);

        if (nestedPlans.isEmpty()) {
            if (plans == null) {
                // data on the file system equals the data -> no plans
                return;
            } else {
                //noinspection StatementWithEmptyBody
                // we have to check for equality later
            }
        }

        if (plans == null) {
            plans = new TPlans();
            serviceTemplate.setPlans(plans);
        }

        for (Iterator<TPlan> iterator = plans.getPlan().iterator(); iterator.hasNext(); ) {
            TPlan plan = iterator.next();
            if (plan.getPlanModel() != null) {
                // in case, a plan is directly contained in a Model element, we do not need to do anything
                continue;
            }
            TPlan.PlanModelReference planModelReference;
            if ((planModelReference = plan.getPlanModelReference()) != null) {
                String ref = planModelReference.getReference();
                if ((ref == null) || ref.startsWith("../")) {
                    // references to local plans start with "../"
                    // special case (due to errors in the importer): empty PlanModelReference field
                    if (plan.getId() == null) {
                        // invalid plan entry: no id.
                        // we remove the entry
                        iterator.remove();
                        continue;
                    }
                    PlanId planId = new PlanId(plansContainerId, new XmlId(plan.getId(), false));
                    if (nestedPlans.contains(planId)) {
                        // everything allright
                        // we do NOT need to add the plan on the HDD to the XML
                        plansToAdd.remove(planId);
                    } else {
                        // no local storage for the plan, we remove it from the XML
                        iterator.remove();
                    }
                }
            }
        }

        // add all plans locally stored, but not contained in the XML, as plan element to the plans of the service template.
        List<TPlan> thePlans = plans.getPlan();
        for (PlanId planId : plansToAdd) {
            SortedSet<RepositoryFileReference> files = repository.getContainedFiles(planId);
            if (files.size() != 1) {
                throw new IllegalStateException("Currently, only one file per plan is supported.");
            }
            RepositoryFileReference ref = files.iterator().next();

            TPlan plan = new TPlan();
            plan.setId(planId.getXmlId().getDecoded());
            plan.setName(planId.getXmlId().getDecoded());
            plan.setPlanType(Constants.TOSCA_PLANTYPE_BUILD_PLAN);
            plan.setPlanLanguage(Namespaces.URI_BPEL20_EXECUTABLE);

            // create a PlanModelReferenceElement pointing to that file
            String path = Util.getUrlPath(ref);
            // path is relative from the definitions element
            path = "../" + path;
            TPlan.PlanModelReference pref = new TPlan.PlanModelReference();
            pref.setReference(path);

            plan.setPlanModelReference(pref);
            thePlans.add(plan);
        }

        if (serviceTemplate.getPlans().getPlan().isEmpty()) {
            serviceTemplate.setPlans(null);
        }

        repository.setElement(id, serviceTemplate);
    }

    public static String getXMLAsString(Object obj, IRepository repository) {
        if (obj instanceof Element) {
            // in case the object is a DOM element, we use the DOM functionality
            return Util.getXMLAsString((Element) obj);
        } else {
            return BackendUtils.getXMLAsString(obj, false, repository);
        }
    }

    public static <T> String getXMLAsString(T obj, boolean includeProcessingInstruction, IRepository repository) {
        if (obj == null) {
            return "";
        }
        @SuppressWarnings("unchecked")
        Class<? super T> clazz = (Class<T>) obj.getClass();
        return BackendUtils.getXMLAsString(clazz, obj, includeProcessingInstruction, repository);
    }

    public static <T> String getXMLAsString(Class<T> clazz, T obj, boolean includeProcessingInstruction, IRepository repository) {
        JAXBElement<T> rootElement = Util.getJAXBElement(clazz, obj);
        Marshaller m = JAXBSupport.createMarshaller(includeProcessingInstruction, repository.getNamespaceManager().asPrefixMapper());
        StringWriter w = new StringWriter();
        try {
            m.marshal(rootElement, w);
        } catch (JAXBException e) {
            BackendUtils.LOGGER.error("Could not put content to string", e);
            throw new IllegalStateException(e);
        }
        return w.toString();
    }

    public static ErrorHandler getErrorHandler(StringBuilder sb) {
        return new ErrorHandler() {

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
        };
    }

    public static RepositoryFileReference getRepositoryFileReference(Path rootPath, Path path, DirectoryId directoryId) {
        final Path relativePath = rootPath.relativize(path);
        Path parent = relativePath.getParent();
        if (parent == null) {
            return new RepositoryFileReference(directoryId, path.getFileName().toString());
        } else {
            return new RepositoryFileReference(directoryId, parent, path.getFileName().toString());
        }
    }

    /**
     * Imports all files in the given directory into the given artifact template directory
     */
    public static void importDirectory(Path rootPath, IRepository repository, DirectoryId dir) throws IOException {
        Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relFile = rootPath.relativize(file);
                RepositoryFileReference repositoryFileReference;
                final Path subDirs = relFile.getParent();
                if (subDirs == null) {
                    repositoryFileReference = new RepositoryFileReference(dir, file.getFileName().toString());
                } else {
                    repositoryFileReference = new RepositoryFileReference(dir, subDirs, file.getFileName().toString());
                }

                try (InputStream is = Files.newInputStream(file);
                     BufferedInputStream bis = new BufferedInputStream(is)) {
                    final MediaType mimeType = BackendUtils.getMimeType(bis, file.getFileName().toString());
                    repository.putContentToFile(repositoryFileReference, bis, mimeType);
                }
                return CONTINUE;
            }
        });
    }

    /**
     * Uses a ToscaExportUtil object to create a TDefinitions object that has imports resolved to the point of being exportable as a CSAR.
     */
    public static TDefinitions getDefinitionsHavingCorrectImports(IRepository repository, DefinitionsChildId id) throws IOException, RepositoryCorruptException {
        ToscaExportUtil exporter = new ToscaExportUtil();
        return exporter.getExportableDefinitions(repository, id);
    }

    public static void mergeTopologyTemplateAinTopologyTemplateB(ServiceTemplateId serviceTemplateIdA, ServiceTemplateId serviceTemplateIdB, IRepository repository) throws IOException {
        Objects.requireNonNull(serviceTemplateIdA);
        Objects.requireNonNull(serviceTemplateIdB);
        
        TTopologyTemplate topologyTemplateA = repository.getElement(serviceTemplateIdA).getTopologyTemplate();
        TServiceTemplate serviceTemplateB = repository.getElement(serviceTemplateIdB);
        TTopologyTemplate topologyTemplateB = serviceTemplateB.getTopologyTemplate();

        if (topologyTemplateB != null) {
            mergeTopologyTemplateAinTopologyTemplateB(topologyTemplateA, topologyTemplateB);
        } else {
            serviceTemplateB.setTopologyTemplate(topologyTemplateA);
        }

        repository.setElement(serviceTemplateIdB, serviceTemplateB);
    }

    /**
     * Merges two Topology Templates and returns the mapping between the topology elements from the original Topology
     * Template and their respective clones inside the merged topology.
     *
     * @param topologyTemplateA the topology to merged into <code>topologyTemplateB</code>
     * @param topologyTemplateB the target topology in which <dode>topologyTemplateA</dode> should be merged in
     * @return A mapping between the ids in the <code>topologyTemplateA</code> and their corresponding ids in
     * <code>topologyTemplateB</code>
     */
    public static Map<String, String> mergeTopologyTemplateAinTopologyTemplateB(TTopologyTemplate topologyTemplateA, TTopologyTemplate topologyTemplateB) {
        return mergeTopologyTemplateAinTopologyTemplateB(topologyTemplateA, topologyTemplateB, null);
    }

    /**
     * Merges two Topology Templates and returns the mapping between the topology elements from the original Topology
     * Template and their respective clones inside the merged topology. Hereby, the staying elements must not be
     * merged.
     *
     * @param topologyTemplateA the topology to merged into <code>topologyTemplateB</code>
     * @param topologyTemplateB the target topology in which <dode>topologyTemplateA</dode> should be merged in
     * @param stayingElements   the TEntityTemplates that must not be merged from A to B.
     * @return A mapping between the ids in the <code>topologyTemplateA</code> and their corresponding ids in
     * <code>topologyTemplateB</code>
     */
    public static Map<String, String> mergeTopologyTemplateAinTopologyTemplateB(TTopologyTemplate topologyTemplateA, TTopologyTemplate topologyTemplateB, List<TEntityTemplate> stayingElements) {
        Objects.requireNonNull(topologyTemplateA);
        Objects.requireNonNull(topologyTemplateB);

        @SuppressWarnings("deprecated")
        TTopologyTemplate topologyTemplateToBeMerged = new TTopologyTemplate();
        Map<String, String> idMapping = new HashMap<>();

        Optional<Integer> shiftLeft = topologyTemplateB.getNodeTemplateOrRelationshipTemplate().stream()
            .filter(x -> x instanceof TNodeTemplate)
            .map(x -> (TNodeTemplate) x)
            .max(Comparator.comparingInt(n -> ModelUtilities.getLeft(n).orElse(0)))
            .map(n -> ModelUtilities.getLeft(n).orElse(0));

        if (Objects.nonNull(stayingElements)) {
            topologyTemplateA.getNodeTemplateOrRelationshipTemplate()
                .forEach(entity -> {
                    if (!stayingElements.contains(entity)) {
                        if (entity instanceof TNodeTemplate) {
                            topologyTemplateToBeMerged.addNodeTemplate((TNodeTemplate) entity);
                        } else if (entity instanceof TRelationshipTemplate) {
                            topologyTemplateToBeMerged.addRelationshipTemplate((TRelationshipTemplate) entity);
                        }
                    }
                });
        } else {
            topologyTemplateToBeMerged.getNodeTemplateOrRelationshipTemplate()
                .addAll(topologyTemplateA.getNodeTemplateOrRelationshipTemplate());
        }

        if (shiftLeft.isPresent()) {
            ModelUtilities.collectIdsOfExistingTopologyElements(topologyTemplateB, idMapping);

            // patch ids of reqs change them if required
            topologyTemplateToBeMerged.getNodeTemplates().stream()
                .filter(nt -> nt.getRequirements() != null)
                .forEach(nt -> nt.getRequirements().getRequirement().forEach(oldReq -> {
                    TRequirement req = SerializationUtils.clone(oldReq);
                    ModelUtilities.generateNewIdOfTemplate(req, idMapping);

                    topologyTemplateToBeMerged.getRelationshipTemplates().stream()
                        .filter(rt -> rt.getSourceElement().getRef() instanceof TRequirement)
                        .forEach(rt -> {
                            TRequirement sourceElement = (TRequirement) rt.getSourceElement().getRef();
                            if (sourceElement.getId().equalsIgnoreCase(oldReq.getId())) {
                                sourceElement.setId(req.getId());
                            }
                        });
                }));

            // patch ids of caps change them if required
            topologyTemplateToBeMerged.getNodeTemplates().stream()
                .filter(nt -> nt.getCapabilities() != null)
                .forEach(nt -> nt.getCapabilities().getCapability().forEach(oldCap -> {
                    TCapability cap = SerializationUtils.clone(oldCap);
                    ModelUtilities.generateNewIdOfTemplate(cap, idMapping);

                    topologyTemplateToBeMerged.getRelationshipTemplates().stream()
                        .filter(rt -> rt.getTargetElement().getRef() instanceof TCapability)
                        .forEach(rt -> {
                            TCapability targetElement = (TCapability) rt.getTargetElement().getRef();
                            if (targetElement.getId().equalsIgnoreCase(oldCap.getId())) {
                                targetElement.setId(cap.getId());
                            }
                        });
                }));

            ArrayList<TRelationshipTemplate> newRelationships = new ArrayList<>();

            // patch the ids of templates and add them
            topologyTemplateToBeMerged.getNodeTemplateOrRelationshipTemplate()
                .forEach(element -> {
                    TEntityTemplate rtOrNt = SerializationUtils.clone(element);
                    ModelUtilities.generateNewIdOfTemplate(rtOrNt, idMapping);

                    if (rtOrNt instanceof TNodeTemplate) {
                        int newLeft = ModelUtilities.getLeft((TNodeTemplate) rtOrNt).orElse(0) + shiftLeft.get();
                        ((TNodeTemplate) rtOrNt).setX(Integer.toString(newLeft));
                    } else if (rtOrNt instanceof TRelationshipTemplate) {
                        newRelationships.add((TRelationshipTemplate) rtOrNt);
                    }

                    topologyTemplateB.getNodeTemplateOrRelationshipTemplate().add(rtOrNt);
                });

            // update references to the new elements
            newRelationships.forEach(rel -> {
                RelationshipSourceOrTarget source = rel.getSourceElement().getRef();
                RelationshipSourceOrTarget target = rel.getTargetElement().getRef();

                if (source instanceof TNodeTemplate && (stayingElements == null
                    || stayingElements.stream().noneMatch(element -> element.getId().equals(source.getId())))) {
                    TNodeTemplate newSource = topologyTemplateB.getNodeTemplate(idMapping.get(source.getId()));
                    rel.setSourceNodeTemplate(newSource);
                }

                if (target instanceof TNodeTemplate && (stayingElements == null
                    || stayingElements.stream().noneMatch(element -> element.getId().equals(target.getId())))) {
                    TNodeTemplate newTarget = topologyTemplateB.getNodeTemplate(idMapping.get(target.getId()));
                    rel.setTargetNodeTemplate(newTarget);
                }
            });
        } else {
            topologyTemplateB.getNodeTemplateOrRelationshipTemplate()
                .addAll(topologyTemplateToBeMerged.getNodeTemplateOrRelationshipTemplate());
        }

        return idMapping;
    }

    public static TTopologyTemplate updateVersionOfNodeTemplate(TTopologyTemplate topologyTemplate, String nodeTemplateId, String newComponentType) {
        topologyTemplate.getNodeTemplateOrRelationshipTemplate().stream()
            .filter(template -> template.getId().equals(nodeTemplateId))
            .findFirst()
            .ifPresent(template -> template.setType(newComponentType));
        return topologyTemplate;
    }

    public static ToscaDiff compare(DefinitionsChildId id, WineryVersion versionToCompareTo, IRepository repository) {
        DefinitionsChildId versionToCompare = VersionSupport.getDefinitionInTheGivenVersion(id, versionToCompareTo);

        TExtensibleElements workingVersion = repository.getDefinitions(id).getElement();
        TExtensibleElements baseVersion = repository.getDefinitions(versionToCompare).getElement();
        return VersionSupport.calculateDifferences(baseVersion, workingVersion);
    }

    public static void commit(DefinitionsChildId componentToCommit, String commitMessagePrefix, IRepository repository) throws GitAPIException {
        if (repository instanceof GitBasedRepository) {
            GitBasedRepository gitRepo = (GitBasedRepository) repository;
            List<String> filePatternsToCommit = new ArrayList<>();

            if (gitRepo.hasChangesInFile(BackendUtils.getRefOfDefinitions(componentToCommit))) {
                /*WineryVersion predecessor = BackendUtils.getPredecessor(componentToCommit);
                ToscaDiff diff = BackendUtils.compare(componentToCommit, predecessor);
                String changeLog = diff.getChangeLog();
                // get changelog.md and append changeLog*/

                filePatternsToCommit.add(Util.getPathInsideRepo(componentToCommit));

                gitRepo.addCommit(filePatternsToCommit.toArray(new String[filePatternsToCommit.size()]), commitMessagePrefix + " " + componentToCommit.getQName());
            }
        } else {
            throw new RuntimeException("Repository does not support git!");
        }
    }
}
