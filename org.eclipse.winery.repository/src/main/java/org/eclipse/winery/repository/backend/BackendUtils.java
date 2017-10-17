/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *     Tino Stadelmaier, Philipp Meyer - rename for id/namespace
 *     Lukas Harzenetter, Nicole Keppler - forceDelete for Namespaces
 *     Karoline Saatkamp - clone of TTopologyTemplates, TNodeTemplate, and TRelationshipTemplates
 *******************************************************************************/
package org.eclipse.winery.repository.backend;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.GenericId;
import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.XmlId;
import org.eclipse.winery.common.ids.admin.AdminId;
import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.common.ids.definitions.CapabilityTypeId;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.common.ids.definitions.EntityTypeId;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.common.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.common.ids.definitions.PolicyTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeImplementationId;
import org.eclipse.winery.common.ids.definitions.RequirementTypeId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.common.ids.definitions.imports.XSDImportId;
import org.eclipse.winery.common.ids.elements.PlanId;
import org.eclipse.winery.common.ids.elements.PlansId;
import org.eclipse.winery.common.ids.elements.ToscaElementId;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.HasIdInIdOrNameField;
import org.eclipse.winery.model.tosca.HasTargetNamespace;
import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TArtifactType;
import org.eclipse.winery.model.tosca.TCapabilityType;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TEntityType.PropertiesDefinition;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TImplementationArtifacts.ImplementationArtifact;
import org.eclipse.winery.model.tosca.TImport;
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
import org.eclipse.winery.model.tosca.TRequirementType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.Namespaces;
import org.eclipse.winery.model.tosca.kvproperties.PropertyDefinitionKV;
import org.eclipse.winery.model.tosca.kvproperties.PropertyDefinitionKVList;
import org.eclipse.winery.model.tosca.kvproperties.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.Constants;
import org.eclipse.winery.repository.GitInfo;
import org.eclipse.winery.repository.JAXBSupport;
import org.eclipse.winery.repository.backend.constants.Filename;
import org.eclipse.winery.repository.backend.constants.MediaTypes;
import org.eclipse.winery.repository.backend.xsd.XsdImportManager;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateDirectoryId;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateFilesDirectoryId;
import org.eclipse.winery.repository.datatypes.ids.elements.VisualAppearanceId;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;
import org.eclipse.winery.repository.export.ToscaExportUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.LSInput;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * Contains generic utility functions for the Backend
 *
 * Contains everything that is useful for our ids etc. Does <em>not</em> contain anything that has to do with resources
 */
public class BackendUtils {

	/**
	 * Shared object to map JSONs
	 */
	public static final ObjectMapper mapper = getObjectMapper();

	private static final XLogger LOGGER = XLoggerFactory.getXLogger(BackendUtils.class);

	private static final MediaType MEDIATYPE_APPLICATION_OCTET_STREAM = MediaType.parse("application/octet-stream");

	private static ObjectMapper getObjectMapper() {
		final ObjectMapper objectMapper = new ObjectMapper();
		// DO NOT ACTIVE the following - JSON is serialized differently and thus, the JAX-B annotations must not be used
		// For instance, "nodeTemplateOrRelationshipTemplate" is a bad thing for JSON as it cannot distinguish whether a child is a node template or a relationship template
		// final JaxbAnnotationModule module = new JaxbAnnotationModule();
		// objectMapper.registerModule(module);
		return objectMapper;
	}

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
	 *
	 * We return the complete definitions to allow the user changes to it, such as adding imports, etc.
	 */
	public static String getDefinitionsAsXMLString(TDefinitions definitions) {
		StringWriter w = new StringWriter();
		Marshaller m = JAXBSupport.createMarshaller(true);
		try {
			m.marshal(definitions, w);
		} catch (JAXBException e) {
			LOGGER.error("Could not marshal definitions", e);
			throw new IllegalStateException(e);
		}
		return w.toString();
	}

	public static String getName(DefinitionsChildId instanceId) throws RepositoryCorruptException {
		IRepository repository = RepositoryFactory.getRepository();
		if (!repository.exists(instanceId)) {
			throw new RepositoryCorruptException("Definitions does not exist for instance");
		}
		TExtensibleElements instanceElement = RepositoryFactory.getRepository().getDefinitions(instanceId).getElement();
		return ModelUtilities.getNameWithIdFallBack(instanceElement);
	}

	/**
	 * Do <em>not</em> use this for creating URLs. Use  {@link Utils#getURLforPathInsideRepo(java.lang.String)} or
	 * {@link Utils#getAbsoluteURL(org.eclipse.winery.common.ids.GenericId) instead.
	 *
	 * @return the path starting from the root element to the current element. Separated by "/", URLencoded, but
	 * <b>not</b> double encoded. With trailing slash if sub-resources can exist
	 * @throws IllegalStateException if id is of an unknown subclass of id
	 */
	private static String getPathInsideRepo(GenericId id) {
		return Util.getPathInsideRepo(id);
	}

	/**
	 * Do <em>not</em> use this for creating URLs. Use {@link Utils#getURLforPathInsideRepo(java.lang.String)} or {@link
	 * Utils#getAbsoluteURL(org.eclipse.winery.common.ids.GenericId) instead.
	 *
	 * @return the path starting from the root element to the current element. Separated by "/", parent URLencoded.
	 * Without trailing slash.
	 */
	public static String getPathInsideRepo(RepositoryFileReference ref) {
		if (ref.getSubDirectory().isPresent()) {
			return BackendUtils.getPathInsideRepo(ref.getParent())
				+ '/' + ref.getSubDirectory().get().toString().replace('\\', '/')
				+ '/' + ref.getFileName();
		}
		return BackendUtils.getPathInsideRepo(ref.getParent()) + ref.getFileName();
	}

	/**
	 * Returns the reference to the definitions XML storing the TOSCA for the given id
	 *
	 * @param id the id to lookup
	 * @return the reference
	 */
	public static RepositoryFileReference getRefOfDefinitions(DefinitionsChildId id) {
		String name = Util.getTypeForComponentId(id.getClass());
		name = name + Constants.SUFFIX_TOSCA_DEFINITIONS;
		return new RepositoryFileReference(id, name);
	}

	/**
	 * @return Singular type name for the given id. E.g., "ServiceTemplateId" gets "ServiceTemplate"
	 */
	public static String getTypeForAdminId(Class<? extends AdminId> idClass) {
		return Util.getEverythingBetweenTheLastDotAndBeforeId(idClass);
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
			name = Util.getTypeForComponentId(((DefinitionsChildId) id).getClass());
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

	public static Collection<QName> getArtifactTemplatesOfReferencedDeploymentArtifacts(TNodeTemplate nodeTemplate) {
		List<QName> l = new ArrayList<>();

		// DAs may be assigned directly to a node template
		Collection<QName> allReferencedArtifactTemplates = BackendUtils.getAllReferencedArtifactTemplates(nodeTemplate.getDeploymentArtifacts());
		l.addAll(allReferencedArtifactTemplates);

		// DAs may be assigned via node type implementations
		QName nodeTypeQName = nodeTemplate.getType();
		Collection<NodeTypeImplementationId> allNodeTypeImplementations = RepositoryFactory.getRepository().getAllElementsReferencingGivenType(NodeTypeImplementationId.class, nodeTypeQName);
		for (NodeTypeImplementationId nodeTypeImplementationId : allNodeTypeImplementations) {
			TDeploymentArtifacts deploymentArtifacts = RepositoryFactory.getRepository().getElement(nodeTypeImplementationId).getDeploymentArtifacts();
			allReferencedArtifactTemplates = BackendUtils.getAllReferencedArtifactTemplates(deploymentArtifacts);
			l.addAll(allReferencedArtifactTemplates);
		}

		return l;
	}

	public static Collection<QName> getArtifactTemplatesOfReferencedImplementationArtifacts(TNodeTemplate nodeTemplate) {
		List<QName> l = new ArrayList<>();

		// IAs may be assigned via node type implementations
		QName nodeTypeQName = nodeTemplate.getType();
		Collection<NodeTypeImplementationId> allNodeTypeImplementations = RepositoryFactory.getRepository().getAllElementsReferencingGivenType(NodeTypeImplementationId.class, nodeTypeQName);
		for (NodeTypeImplementationId nodeTypeImplementationId : allNodeTypeImplementations) {
			TImplementationArtifacts implementationArtifacts = RepositoryFactory.getRepository().getElement(nodeTypeImplementationId).getImplementationArtifacts();
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
	public static Definitions updateWrapperDefinitions(DefinitionsChildId tcId, Definitions defs) {
		// set target namespace
		// an internal namespace is not possible
		//   a) tPolicyTemplate and tArtfactTemplate do NOT support the "targetNamespace" attribute
		//   b) the imports statement would look bad as it always imported the artificial namespace
		defs.setTargetNamespace(tcId.getNamespace().getDecoded());

		// set a unique id to create a valid definitions element
		// we do not use UUID to be more human readable and deterministic (for debugging)
		String prefix = RepositoryFactory.getRepository().getNamespaceManager().getPrefix(tcId.getNamespace());
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
		TTopologyTemplate topologyTemplateClone = new TTopologyTemplate();
		List<TEntityTemplate> entityTemplate = topologyTemplate.getNodeTemplateOrRelationshipTemplate();
		topologyTemplateClone.getNodeTemplateOrRelationshipTemplate().addAll(entityTemplate);
		return topologyTemplateClone;

		//Copy based on http://stackoverflow.com/a/3899882
		/*try {
			JAXBContext sourceJAXBContext = JAXBSupport.context.newInstance(element.getClass());
			JAXBContext targetJAXBContext = JAXBSupport.context.newInstance(element.getClass());
			return (T) targetJAXBContext.createUnmarshaller().unmarshal(new JAXBSource(sourceJAXBContext, element));
		} catch (JAXBException e) {
			logger.error("Cannot clone object", e);
			return null;
		}*/
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
		nodeTemplateClone.setMaxInstances(nodeTemplate.getMaxInstances());
		nodeTemplateClone.setMinInstances(nodeTemplate.getMinInstances());
		nodeTemplateClone.setName(nodeTemplate.getName());
		nodeTemplateClone.setPolicies(nodeTemplate.getPolicies());
		nodeTemplateClone.setRequirements(nodeTemplate.getRequirements());
		nodeTemplateClone.setCapabilities(nodeTemplate.getCapabilities());
		nodeTemplateClone.setProperties(nodeTemplate.getProperties());
		nodeTemplateClone.setPropertyConstraints(nodeTemplate.getPropertyConstraints());

		if (ModelUtilities.getTargetLabel(nodeTemplate).isPresent()) {
			ModelUtilities.setTargetLabel(nodeTemplateClone, ModelUtilities.getTargetLabel(nodeTemplate).get());
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
	public static Definitions createWrapperDefinitions(DefinitionsChildId tcId) {
		Definitions defs = new Definitions();
		return updateWrapperDefinitions(tcId, defs);
	}

	public static Definitions createWrapperDefinitionsAndInitialEmptyElement(IRepository repository, DefinitionsChildId id) {
		final Definitions definitions = createWrapperDefinitions(id);
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
		Document document;
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			LOGGER.error("Could not create document", e);
			return;
		}

		final String namespace = winerysPropertiesDefinition.getNamespace();
		final Element wrapperElement = document.createElementNS(namespace, winerysPropertiesDefinition.getElementName());
		document.appendChild(wrapperElement);

		// we produce the serialization in the same order the XSD would be generated (because of the usage of xsd:sequence)
		for (PropertyDefinitionKV propertyDefinitionKV : winerysPropertiesDefinition.getPropertyDefinitionKVList()) {
			// we always write the element tag as the XSD forces that
			final Element valueElement = document.createElementNS(namespace, propertyDefinitionKV.getKey());
			wrapperElement.appendChild(valueElement);
		}

		TEntityTemplate.Properties properties = new TEntityTemplate.Properties();
		properties.setAny(document.getDocumentElement());
		entityTemplate.setProperties(properties);
	}

	/**
	 * Regenerates wrapper definitions; thus all extensions at the wrapper definitions are lost
	 *
	 * @param id      the id of the definition child to persist
	 * @param element the element of the definition child
	 */
	public static void persist(DefinitionsChildId id, TExtensibleElements element) throws IOException {
		RepositoryFactory.getRepository().setElement(id, element);
	}

	/**
	 * Persists the given definitions
	 *
	 * @param id          the id of the definition child to persist
	 * @param definitions the definitions to persist
	 */
	public static void persist(DefinitionsChildId id, Definitions definitions) throws IOException {
		RepositoryFileReference ref = BackendUtils.getRefOfDefinitions(id);
		BackendUtils.persist(definitions, ref, MediaTypes.MEDIATYPE_TOSCA_DEFINITIONS);
	}

	/**
	 * @throws IOException           if content could not be updated in the repository
	 * @throws IllegalStateException if an JAXBException occurred. This should never happen.
	 */
	public static void persist(Object o, RepositoryFileReference ref, MediaType mediaType) throws IOException {
		// We assume that the object is not too large
		// Otherwise, http://io-tools.googlecode.com/svn/www/easystream/apidocs/index.html should be used
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Marshaller m;
		try {
			m = JAXBSupport.createMarshaller(true);
			m.marshal(o, out);
		} catch (JAXBException e) {
			BackendUtils.LOGGER.error("Could not put content to file", e);
			throw new IllegalStateException(e);
		}
		byte[] data = out.toByteArray();
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		// this may throw an IOException. We propagate this exception.
		RepositoryFactory.getRepository().putContentToFile(ref, in, mediaType);
	}

	/**
	 * @param ref the file to read from
	 */
	public static Optional<XSModel> getXSModel(final RepositoryFileReference ref) {
		Objects.requireNonNull(ref);
		final InputStream is;
		try {
			is = RepositoryFactory.getRepository().newInputStream(ref);
		} catch (IOException e) {
			BackendUtils.LOGGER.debug("Could not create input stream", e);
			return Optional.empty();
		}

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
	}

	/**
	 * Derives Winery's Properties Definition from an existing properties definition
	 *
	 * @param ci     the entity type to try to modify the WPDs
	 * @param errors the list to add errors to
	 */
	public static void deriveWPD(TEntityType ci, List<String> errors) {
		BackendUtils.LOGGER.trace("deriveWPD");
		PropertiesDefinition propertiesDefinition = ci.getPropertiesDefinition();
		QName element = propertiesDefinition.getElement();
		if (element == null) {
			BackendUtils.LOGGER.debug("only works for an element definition, not for types");
		} else {
			BackendUtils.LOGGER.debug("Looking for the definition of {" + element.getNamespaceURI() + "}" + element.getLocalPart());
			// fetch the XSD defining the element
			final XsdImportManager xsdImportManager = RepositoryFactory.getRepository().getXsdImportManager();
			Map<String, RepositoryFileReference> mapFromLocalNameToXSD = xsdImportManager.getMapFromLocalNameToXSD(new Namespace(element.getNamespaceURI(), false), false);
			RepositoryFileReference ref = mapFromLocalNameToXSD.get(element.getLocalPart());
			if (ref == null) {
				String msg = "XSD not found for " + element.getNamespaceURI() + " / " + element.getLocalPart();
				BackendUtils.LOGGER.debug(msg);
				errors.add(msg);
				return;
			}

			final Optional<XSModel> xsModelOptional = BackendUtils.getXSModel(ref);
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
			if (typeDefinition instanceof XSComplexTypeDefinition) {
				XSComplexTypeDefinition cTypeDefinition = (XSComplexTypeDefinition) typeDefinition;
				XSParticle particle = cTypeDefinition.getParticle();
				if (particle == null) {
					BackendUtils.LOGGER.debug("XSD does not follow the requirements put by winery: Complex type does not contain particles");
				} else {
					XSTerm term = particle.getTerm();
					if (term instanceof XSModelGroup) {
						XSModelGroup modelGroup = (XSModelGroup) term;
						if (modelGroup.getCompositor() == XSModelGroup.COMPOSITOR_SEQUENCE) {
							XSObjectList particles = modelGroup.getParticles();
							int len = particles.getLength();
							boolean everyThingIsASimpleType = true;
							PropertyDefinitionKVList list = new PropertyDefinitionKVList();
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
							if (everyThingIsASimpleType) {
								// everything went allright, we can add a WPD
								WinerysPropertiesDefinition wpd = new WinerysPropertiesDefinition();
								wpd.setIsDerivedFromXSD(Boolean.TRUE);
								wpd.setElementName(element.getLocalPart());
								wpd.setNamespace(element.getNamespaceURI());
								wpd.setPropertyDefinitionKVList(list);
								ModelUtilities.replaceWinerysPropertiesDefinition(ci, wpd);
								BackendUtils.LOGGER.debug("Successfully generated WPD");
							} else {
								BackendUtils.LOGGER.debug("XSD does not follow the requirements put by winery: Not all types in the sequence are simple types");
							}
						} else {
							BackendUtils.LOGGER.debug("XSD does not follow the requirements put by winery: Model group is not a sequence");
						}
					} else {
						BackendUtils.LOGGER.debug("XSD does not follow the requirements put by winery: Not a model group");
					}
				}
			} else {
				BackendUtils.LOGGER.debug("XSD does not follow the requirements put by winery: No Complex Type Definition");
			}
		}
	}

	/**
	 * Returns all components available of the given id type
	 *
	 * Similar functionality as {@link IGenericRepository#getAllDefinitionsChildIds(java.lang.Class)}, but it crawls
	 * through the repository
	 *
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
	 *
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
	 *
	 * For instance, the id is put in the "name" field for EntityTypes
	 */
	public static void copyIdToFields(HasIdInIdOrNameField element, DefinitionsChildId id) {
		element.setId(id.getXmlId().getDecoded());
		if (element instanceof HasTargetNamespace) {
			((HasTargetNamespace) element).setTargetNamespace(id.getNamespace().getDecoded());
		}
	}

	/**
	 * @param directoryId ArtifactTemplateDirectoryId of the ArtifactTemplate that should contain a reference to a git
	 *                    repository.
	 * @return The URL and the branch/tag that contains the files for the ArtifactTemplate. null if no git information
	 * is given.
	 */
	public static GitInfo getGitInformation(ArtifactTemplateDirectoryId directoryId) {
		if (!(directoryId.getParent() instanceof ArtifactTemplateId)) {
			return null;
		}
		RepositoryFileReference ref = BackendUtils.getRefOfDefinitions((ArtifactTemplateId) directoryId.getParent());
		try (InputStream is = RepositoryFactory.getRepository().newInputStream(ref)) {
			Unmarshaller u = JAXBSupport.createUnmarshaller();
			Definitions defs = ((Definitions) u.unmarshal(is));
			Map<QName, String> atts = defs.getOtherAttributes();
			String src = atts.get(new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "gitsrc"));
			String branch = atts.get(new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "gitbranch"));
			// ^ is the XOR operator
			if (src == null ^ branch == null) {
				LOGGER.error("Git information not complete, URL or branch missing");
				return null;
			} else if (src == null && branch == null) {
				return null;
			}
			return new GitInfo(src, branch);
		} catch (IOException e) {
			LOGGER.error("Error reading definitions of " + directoryId.getParent() + " at " + ref.getFileName(), e);
		} catch (JAXBException e) {
			LOGGER.error("Error in XML in " + ref.getFileName(), e);
		}
		return null;
	}


	/**
	 * @param directoryId DirectoryID of the TArtifactTemplate that should be returned.
	 * @return The TArtifactTemplate corresponding to the directoryId.
	 */
	public static TArtifactTemplate getTArtifactTemplate(ArtifactTemplateDirectoryId directoryId) {
		RepositoryFileReference ref = BackendUtils.getRefOfDefinitions((ArtifactTemplateId) directoryId.getParent());
		try (InputStream is = RepositoryFactory.getRepository().newInputStream(ref)) {
			Unmarshaller u = JAXBSupport.createUnmarshaller();
			Definitions defs = ((Definitions) u.unmarshal(is));
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

	/**
	 * Tests if a path matches a glob pattern. {@see <a href="https://en.wikipedia.org/wiki/Glob_(programming)">Wikipedia</a>}
	 *
	 * @param glob Glob pattern to test the path against.
	 * @param path Path that should match the glob pattern.
	 * @return Whether the glob and the path result in a match.
	 */
	public static boolean isGlobMatch(String glob, Path path) {
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + glob);
		return matcher.matches(path);
	}

	public static boolean injectArtifactTemplateIntoDeploymentArtifact(ServiceTemplateId serviceTemplate, String nodeTemplateId, String deploymentArtifactId, ArtifactTemplateId artifactTemplate) throws IOException {
		TServiceTemplate element = RepositoryFactory.getRepository().getElement(serviceTemplate);
		element.getTopologyTemplate().getNodeTemplate(nodeTemplateId).getDeploymentArtifacts().getDeploymentArtifact(deploymentArtifactId).setArtifactRef(artifactTemplate.getQName());
		RepositoryFactory.getRepository().setElement(serviceTemplate, element);
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

	public static void synchronizeReferences(ArtifactTemplateId id) throws IOException {
		TArtifactTemplate template = RepositoryFactory.getRepository().getElement(id);

		ArtifactTemplateDirectoryId fileDir = new ArtifactTemplateFilesDirectoryId(id);
		SortedSet<RepositoryFileReference> files = RepositoryFactory.getRepository().getContainedFiles(fileDir);
		if (files.isEmpty()) {
			// clear artifact references
			template.setArtifactReferences(null);
		} else {
			TArtifactTemplate.ArtifactReferences artifactReferences = new TArtifactTemplate.ArtifactReferences();
			template.setArtifactReferences(artifactReferences);
			List<TArtifactReference> artRefList = artifactReferences.getArtifactReference();
			for (RepositoryFileReference ref : files) {
				// determine path
				// path relative from the root of the CSAR is ok (COS01, line 2663)
				// double encoded - see ADR-0003
				String path = Util.getUrlPath(ref);

				// put path into data structure
				// we do not use Include/Exclude as we directly reference a concrete file
				TArtifactReference artRef = new TArtifactReference();
				artRef.setReference(path);
				artRefList.add(artRef);
			}
		}

		BackendUtils.persist(id, template);
	}

	/**
	 * Synchronizes the known plans with the data in the XML. When there is a stored file, but no known entry in the
	 * XML, we guess "BPEL" as language and "build plan" as type.
	 */
	public static void synchronizeReferences(ServiceTemplateId id) throws IOException {
		final IRepository repository = RepositoryFactory.getRepository();
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
			plan.setPlanType(org.eclipse.winery.repository.Constants.TOSCA_PLANTYPE_BUILD_PLAN);
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

		RepositoryFactory.getRepository().setElement(id, serviceTemplate);
	}

	public static String Object2JSON(Object o) {
		String res;
		try {
			res = BackendUtils.mapper.writeValueAsString(o);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
		return res;
	}

	public static String getXMLAsString(Object obj) {
		if (obj instanceof Element) {
			// in case the object is a DOM element, we use the DOM functionality
			return Util.getXMLAsString((Element) obj);
		} else {
			return BackendUtils.getXMLAsString(obj, false);
		}
	}

	public static <T> String getXMLAsString(T obj, boolean includeProcessingInstruction) {
		if (obj == null) {
			return "";
		}
		@SuppressWarnings("unchecked")
		Class<T> clazz = (Class<T>) obj.getClass();
		return BackendUtils.getXMLAsString(clazz, obj, includeProcessingInstruction);
	}

	public static <T> String getXMLAsString(Class<T> clazz, T obj, boolean includeProcessingInstruction) {
		JAXBElement<T> rootElement = Util.getJAXBElement(clazz, obj);
		Marshaller m = JAXBSupport.createMarshaller(includeProcessingInstruction);
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

	public static RepositoryFileReference getRepositoryFileReference(Path rootPath, Path path, ArtifactTemplateDirectoryId artifactTemplateDirectoryId) {
		final Path relativePath = rootPath.relativize(path);
		Path parent = relativePath.getParent();
		if (parent == null) {
			return new RepositoryFileReference(artifactTemplateDirectoryId, path.getFileName().toString());
		} else {
			return new RepositoryFileReference(artifactTemplateDirectoryId, parent, path.getFileName().toString());
		}
	}

	/**
	 * Imports all files in the given directory into the given artifact template directory
	 */
	public static void importDirectory(Path rootPath, IRepository repository, ArtifactTemplateDirectoryId dir) throws IOException {
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

	public static Definitions getDefinitionsHavingCorrectImports(IRepository repository, DefinitionsChildId id) throws Exception {
		// idea: get the XML, parse it, return it
		// the conversion to JSON is made by Jersey automatically
		// TODO: future work: force TOSCAExportUtil to return TDefinitions directly

		// Have to use TOScAExportUtil to have the imports correctly set

		ToscaExportUtil exporter = new ToscaExportUtil();
		// we include everything related
		Map<String, Object> conf = new HashMap<>();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		exporter.exportTOSCA(repository, id, bos, conf);
		String xmlRepresentation = bos.toString(StandardCharsets.UTF_8.toString());
		Unmarshaller u = JAXBSupport.createUnmarshaller();
		return ((Definitions) u.unmarshal(new StringReader(xmlRepresentation)));
	}
}
