/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Kálmán Képes - initial API and implementation and/or initial documentation
 *     Oliver Kopp - adapted to new storage model and to TOSCA v1.0
 *******************************************************************************/
package org.eclipse.winery.repository.export;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.ModelUtilities;
import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.constants.QNames;
import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.common.ids.definitions.CapabilityTypeId;
import org.eclipse.winery.common.ids.definitions.EntityTypeId;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.common.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.common.ids.definitions.PolicyTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeImplementationId;
import org.eclipse.winery.common.ids.definitions.RequirementTypeId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.common.ids.definitions.TopologyGraphElementEntityTypeId;
import org.eclipse.winery.common.ids.definitions.imports.GenericImportId;
import org.eclipse.winery.common.ids.elements.PlanId;
import org.eclipse.winery.common.ids.elements.PlansId;
import org.eclipse.winery.common.propertydefinitionkv.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions.Policies;
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TCapabilityDefinition;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TEntityType.PropertiesDefinition;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TImport;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate.Capabilities;
import org.eclipse.winery.model.tosca.TNodeTemplate.Requirements;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeType.CapabilityDefinitions;
import org.eclipse.winery.model.tosca.TNodeType.RequirementDefinitions;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipType.ValidSource;
import org.eclipse.winery.model.tosca.TRelationshipType.ValidTarget;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TRequirementDefinition;
import org.eclipse.winery.repository.JAXBSupport;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.backend.constants.Filename;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateDirectoryId;
import org.eclipse.winery.repository.datatypes.ids.elements.VisualAppearanceId;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;
import org.eclipse.winery.repository.resources.AbstractComponentInstanceResource;
import org.eclipse.winery.repository.resources.AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal;
import org.eclipse.winery.repository.resources.AbstractComponentsResource;
import org.eclipse.winery.repository.resources.EntityTypeResource;
import org.eclipse.winery.repository.resources.entitytemplates.artifacttemplates.ArtifactTemplateResource;
import org.eclipse.winery.repository.resources.entitytemplates.policytemplates.PolicyTemplateResource;
import org.eclipse.winery.repository.resources.entitytypeimplementations.nodetypeimplementations.NodeTypeImplementationResource;
import org.eclipse.winery.repository.resources.entitytypeimplementations.relationshiptypeimplementations.RelationshipTypeImplementationResource;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.NodeTypeResource;
import org.eclipse.winery.repository.resources.entitytypes.relationshiptypes.RelationshipTypeResource;
import org.eclipse.winery.repository.resources.entitytypes.requirementtypes.RequirementTypeResource;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.w3c.dom.Document;

public class TOSCAExportUtil {

	private static final XLogger LOGGER = XLoggerFactory.getXLogger(TOSCAExportUtil.class);

	/*
	 * these two are GLOBAL VARIABLES leading to the fact that this class has to
	 * be constructed for each export
	 */

	// collects the references to be put in the CSAR and the assigned path in
	// the CSAR MANIFEST
	// this allows to use other paths in the CSAR than on the local storage
	private Map<RepositoryFileReference, String> referencesToPathInCSARMap = null;

	/**
	 * Currently a very simple approach to configure the export
	 */
	private Map<String, Object> exportConfiguration;

	public enum ExportProperties {
		INCLUDEXYCOORDINATES, REPOSITORY_URI
	}


	/**
	 * Writes the <em>complete</em> tosca xml into the given outputstream
	 *
	 * @param id the id of the TOSCA component instance to export
	 * @param out outputstream to write to
	 * @param exportConfiguration the configuration map for the export.
	 * @return a collection of TOSCAcomponentIds referenced by the given
	 *         component
	 */
	public Collection<TOSCAComponentId> exportTOSCA(TOSCAComponentId id, OutputStream out, Map<String, Object> exportConfiguration) throws IOException, JAXBException, RepositoryCorruptException {
		this.exportConfiguration = exportConfiguration;
		this.initializeExport();
		return this.writeDefinitionsElement(id, out);
	}

	private void initializeExport() {
		this.setDefaultExportConfiguration();
		// quick hack to avoid NPE
		if (this.referencesToPathInCSARMap == null) {
			this.referencesToPathInCSARMap = new HashMap<>();
		}
	}

	/**
	 * Quick hack to set defaults. Typically, a configuration builder or similar
	 * is used
	 */
	private void setDefaultExportConfiguration() {
		this.checkConfig(ExportProperties.INCLUDEXYCOORDINATES, Boolean.FALSE);
	}

	private void checkConfig(ExportProperties propKey, Boolean bo) {
		if (!this.exportConfiguration.containsKey(propKey.toString())) {
			this.exportConfiguration.put(propKey.toString(), bo);
		}
	}

	/**
	 * Writes the <em>complete</em> TOSCA XML into the given outputstream.
	 * Additionally, a the artifactMap is filled to enable the CSAR exporter to
	 * create necessary entries in TOSCA-Meta and to add them to the CSAR itself
	 *
	 * @param id the component instance to export
	 * @param out outputstream to write to
	 * @param exportConfiguration Configures the exporter
	 * @param referencesToPathInCSARMap collects the references to export. It is
	 *            updated during the export
	 * @return a collection of TOSCAcomponentIds referenced by the given
	 *         component
	 */
	protected Collection<TOSCAComponentId> exportTOSCA(TOSCAComponentId id, OutputStream out, Map<RepositoryFileReference, String> referencesToPathInCSARMap, Map<String, Object> exportConfiguration) throws IOException, JAXBException, RepositoryCorruptException {
		this.referencesToPathInCSARMap = referencesToPathInCSARMap;
		return this.exportTOSCA(id, out, exportConfiguration);
	}

	/**
	 * Called when the entry resource is definitions backed
	 */
	private void writeDefinitionsElement(Definitions entryDefinitions, OutputStream out) throws JAXBException {
		Marshaller m = JAXBSupport.createMarshaller(true);
		m.marshal(entryDefinitions, out);
	}

	/**
	 * Writes the Definitions belonging to the given TOSCA component to the
	 * outputstream
	 *
	 * @return a collection of TOSCAcomponentIds referenced by the given
	 *         component
	 *
	 * @throws RepositoryCorruptException if tcId does not exist
	 */
	private Collection<TOSCAComponentId> writeDefinitionsElement(TOSCAComponentId tcId, OutputStream out) throws JAXBException, RepositoryCorruptException {
		if (!Repository.INSTANCE.exists(tcId)) {
			String error = "Component instance " + tcId.toString() + " does not exist.";
			TOSCAExportUtil.LOGGER.error(error);
			throw new RepositoryCorruptException(error);
		}

		AbstractComponentInstanceResource res = AbstractComponentsResource.getComponentInstaceResource(tcId);
		Definitions entryDefinitions = res.getDefinitions();

		// BEGIN: Definitions modification
		// the "imports" collection contains the imports of Definitions, not of other definitions
		// the other definitions are stored in entryDefinitions.getImport()
		// we modify the internal definitions object directly. It is not written back to the storage. Therefore, we do not need to clone it

		// the imports (pointing to not-definitions (xsd, wsdl, ...)) already have a correct relative URL. (quick hack)
		URI uri = (URI) this.exportConfiguration.get(TOSCAExportUtil.ExportProperties.REPOSITORY_URI.toString());
		if (uri != null) {
			// we are in the plain-XML mode, the URLs of the imports have to be adjusted
			for (TImport i : entryDefinitions.getImport()) {
				String loc = i.getLocation();
				assert (loc.startsWith("../"));
				loc = loc.substring(3);
				loc = uri + loc;
				// now the location is an absolute URL
				i.setLocation(loc);
			}
		}

		// files of imports have to be added to the CSAR, too
		for (TImport i : entryDefinitions.getImport()) {
			String loc = i.getLocation();
			if (Util.isRelativeURI(loc)) {
				// locally stored, add to CSAR
				GenericImportId iid = new GenericImportId(i);
				String fileName = Util.getLastURIPart(loc);
				fileName = Util.URLdecode(fileName);
				RepositoryFileReference ref = new RepositoryFileReference(iid, fileName);
				this.putRefAsReferencedItemInCSAR(ref);
			}
		}

		// adjust imports: add imports of definitions to it
		Collection<TOSCAComponentId> referencedTOSCAComponentIds = this.getReferencedTOSCAComponentIds(tcId);
		Collection<TImport> imports = new ArrayList<>();
		for (TOSCAComponentId id : referencedTOSCAComponentIds) {
			this.addToImports(id, imports);
		}
		entryDefinitions.getImport().addAll(imports);

		if (res.getElement() instanceof TEntityType) {
			// we have an entity type with a possible properties definition
			EntityTypeResource entityTypeRes = (EntityTypeResource) res;
			WinerysPropertiesDefinition wpd = ModelUtilities.getWinerysPropertiesDefinition(entityTypeRes.getEntityType());
			if (wpd != null) {
				if (wpd.getIsDerivedFromXSD() == null) {
					// Write WPD only to file if it exists and is NOT derived from an XSD (which may happen during import)

					String wrapperElementNamespace = wpd.getNamespace();
					String wrapperElementLocalName = wpd.getElementName();

					// BEGIN: add import and put into CSAR

					TImport imp = new TImport();
					entryDefinitions.getImport().add(imp);

					// fill known import values
					imp.setImportType(XMLConstants.W3C_XML_SCHEMA_NS_URI);
					imp.setNamespace(wrapperElementNamespace);
					// add "winerysPropertiesDefinition" flag to import tag to support
					Map<QName, String> otherAttributes = imp.getOtherAttributes();
					otherAttributes.put(QNames.QNAME_WINERYS_PROPERTIES_DEFINITION_ATTRIBUTE, "true");

					// Determine location
					String loc = BackendUtils.getImportLocationForWinerysPropertiesDefinitionXSD((EntityTypeId) tcId, uri, wrapperElementLocalName);
					if (uri == null) {
						TOSCAExportUtil.LOGGER.trace("CSAR Export mode. Putting XSD into CSAR");
						// CSAR Export mode
						// XSD has to be put into the CSAR
						Document document = ModelUtilities.getWinerysPropertiesDefinitionXSDAsDocument(wpd);

						// loc in import is URLencoded, loc on filesystem isn't
						String locInCSAR = Util.URLdecode(loc);
						// furthermore, the path has to start from the root of the CSAR; currently, it starts from Definitions/
						locInCSAR = locInCSAR.substring(3);
						TOSCAExportUtil.LOGGER.trace("Location in CSAR: {}", locInCSAR);
						this.referencesToPathInCSARMap.put(new DummyRepositoryFileReferenceForGeneratedXSD(document), locInCSAR);
					}
					imp.setLocation(loc);

					// END: add import and put into CSAR

					// BEGIN: generate TOSCA conforming PropertiesDefinition

					TEntityType entityType = entityTypeRes.getEntityType();
					PropertiesDefinition propertiesDefinition = new PropertiesDefinition();
					propertiesDefinition.setType(new QName(wrapperElementNamespace, wrapperElementLocalName));
					entityType.setPropertiesDefinition(propertiesDefinition);

					// END: generate TOSCA conforming PropertiesDefinition
				} else {
					//noinspection StatementWithEmptyBody
					// otherwise WPD exists, but is derived from XSD
					// we DO NOT have to remove the winery properties definition from the output to allow "debugging" of the CSAR
				}
			}
		}

		// END: Definitions modification

		this.writeDefinitionsElement(entryDefinitions, out);

		return referencedTOSCAComponentIds;
	}

	private Collection<TOSCAComponentId> getReferencedTOSCAComponentIds(EntityTypeId id) {
		return this.getReferencedTOSCAComponentIdOfParentForAnAbstractComponentsWithTypeReferenceResource(id);
	}

	/**
	 * There is now equivalent id class for
	 * AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal,
	 * therefore we take the super type and hope that the caller knows what he
	 * does.
	 */
	private Collection<TOSCAComponentId> getReferencedTOSCAComponentIdOfParentForAnAbstractComponentsWithTypeReferenceResource(TOSCAComponentId id) {
		AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal res = (AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal) AbstractComponentsResource.getComponentInstaceResource(id);
		String derivedFrom = res.getInheritanceManagement().getDerivedFrom();
		if (StringUtils.isEmpty(derivedFrom)) {
			return Collections.emptySet();
		} else {
			// Instantiate an id with the same class as the current id
			TOSCAComponentId parentId;
			QName qname = QName.valueOf(derivedFrom);

			Constructor<? extends TOSCAComponentId> constructor;
			try {
				constructor = id.getClass().getConstructor(QName.class);
			} catch (NoSuchMethodException | SecurityException e1) {
				throw new IllegalStateException("Could get constructor to instantiate parent id", e1);
			}
			try {
				parentId = constructor.newInstance(qname);
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				throw new IllegalStateException("Could not instantiate id for parent", e);
			}

			Collection<TOSCAComponentId> result = new ArrayList<>(1);
			result.add(parentId);
			return result;
		}
	}

	/**
	 * This method is intended to be used by exportTOSCA. However,
	 * org.eclipse.winery.repository.client requires an XML representation of a
	 * component instances without a surrounding definitions element.
	 *
	 * We name this method differently to prevent wrong calling due to
	 * inheritance
	 *
	 * @param id the id to search its children for referenced elements
	 */
	private Collection<TOSCAComponentId> getReferencedTOSCAComponentIds(TOSCAComponentId id) throws RepositoryCorruptException {
		Collection<TOSCAComponentId> referencedTOSCAComponentIds;

		// first of all, handle the concrete elements
		if (id instanceof ServiceTemplateId) {
			referencedTOSCAComponentIds = this.prepareForExport((ServiceTemplateId) id);
		} else if (id instanceof NodeTypeId) {
			referencedTOSCAComponentIds = this.getReferencedTOSCAComponentIds((NodeTypeId) id);
		} else if (id instanceof NodeTypeImplementationId) {
			referencedTOSCAComponentIds = this.getReferencedTOSCAComponentIds((NodeTypeImplementationId) id);
		} else if (id instanceof RelationshipTypeId) {
			referencedTOSCAComponentIds = this.getReferencedTOSCAComponentIds((RelationshipTypeId) id);
		} else if (id instanceof RelationshipTypeImplementationId) {
			referencedTOSCAComponentIds = this.getReferencedTOSCAComponentIds((RelationshipTypeImplementationId) id);
		} else if (id instanceof RequirementTypeId) {
			referencedTOSCAComponentIds = this.getReferencedTOSCAComponentIds((RequirementTypeId) id);
		} else if (id instanceof CapabilityTypeId) {
			referencedTOSCAComponentIds = this.getReferencedTOSCAComponentIds((CapabilityTypeId) id);
		} else if (id instanceof ArtifactTypeId) {
			referencedTOSCAComponentIds = this.getReferencedTOSCAComponentIds((ArtifactTypeId) id);
		} else if (id instanceof ArtifactTemplateId) {
			referencedTOSCAComponentIds = this.prepareForExport((ArtifactTemplateId) id);
		} else if (id instanceof PolicyTypeId) {
			referencedTOSCAComponentIds = this.getReferencedTOSCAComponentIds((PolicyTypeId) id);
		} else if (id instanceof PolicyTemplateId) {
			referencedTOSCAComponentIds = this.getReferencedTOSCAComponentIds((PolicyTemplateId) id);
		} else if (id instanceof GenericImportId) {
			// in case of imports, there are no other ids referenced
			referencedTOSCAComponentIds = Collections.emptyList();
		} else {
			throw new IllegalStateException("Unhandled id class " + id.getClass());
		}

		// Then, handle the super classes, which support inheritance
		// Currently, it is EntityType and EntityTypeImplementation only
		// Since the latter does not exist in the TOSCA MetaModel, we just handle EntityType here
		if (id instanceof EntityTypeId) {
			Collection<TOSCAComponentId> additionalRefs = this.getReferencedTOSCAComponentIds((EntityTypeId) id);
			// the original referenceTOSCAComponentIds could be unmodifiable
			// we just create a new one...
			referencedTOSCAComponentIds = new ArrayList<>(referencedTOSCAComponentIds);
			// ...and add the new reference
			referencedTOSCAComponentIds.addAll(additionalRefs);
		}

		return referencedTOSCAComponentIds;
	}

	/**
	 * Adds the given id as import to the given imports collection
	 */
	private void addToImports(TOSCAComponentId id, Collection<TImport> imports) {
		TImport imp = new TImport();
		imp.setImportType(org.eclipse.winery.common.constants.Namespaces.TOSCA_NAMESPACE);
		imp.setNamespace(id.getNamespace().getDecoded());
		URI uri = (URI) this.exportConfiguration.get(TOSCAExportUtil.ExportProperties.REPOSITORY_URI.toString());
		if (uri == null) {
			// self-contained mode
			// all Definitions are contained in "Definitions" directory, therefore, we provide the filename only
			// references are resolved relatively from a definitions element (COS01, line 425)
			String fn = CSARExporter.getDefinitionsFileName(id);
			fn = Util.URLencode(fn);
			imp.setLocation(fn);
		} else {
			String path = Utils.getURLforPathInsideRepo(BackendUtils.getPathInsideRepo(id));
			path = path + "?definitions";
			URI absoluteURI = uri.resolve(path);
			imp.setLocation(absoluteURI.toString());
		}
		imports.add(imp);

		// FIXME: Currently the depended elements (such as the artifact templates linked to a node type implementation) are gathered by the corresponding "addXY" method.
		// Reason: the corresponding TDefinitions element is *not* updated if a related element is added/removed.
		// That means: The imports are not changed.
		// The current issue is that TOSCA allows imports of Definitions only and the repository has the concrete elements as main structure
		// Although during save the import can be updated (by fetching the associated resource and get the definitions of it),
		// The concrete definitions cannot be determined without
		//  a) having a complete index of all definitions in the repository
		//  b) crawling through the *complete* repository
		// Possibly the current solution, just lazily adding all dependent elements is the better solution.
	}

	private Collection<TOSCAComponentId> getReferencedTOSCAComponentIds(NodeTypeImplementationId id) {
		// We have to use a HashSet to ensure that no duplicate ids are added
		// There may be multiple DAs/IAs referencing the same type
		Collection<TOSCAComponentId> ids = new HashSet<>();

		NodeTypeImplementationResource res = new NodeTypeImplementationResource(id);

		// DAs
		TDeploymentArtifacts deploymentArtifacts = res.getNTI().getDeploymentArtifacts();
		if (deploymentArtifacts != null) {
			for (TDeploymentArtifact da : deploymentArtifacts.getDeploymentArtifact()) {
				QName qname;
				if ((qname = da.getArtifactRef()) != null) {
					ids.add(new ArtifactTemplateId(qname));
				}
				ids.add(new ArtifactTypeId(da.getArtifactType()));
			}
		}

		// IAs
		TImplementationArtifacts implementationArtifacts = res.getNTI().getImplementationArtifacts();
		if (implementationArtifacts != null) {
			for (TImplementationArtifact ia : implementationArtifacts.getImplementationArtifact()) {
				QName qname;
				if ((qname = ia.getArtifactRef()) != null) {
					ids.add(new ArtifactTemplateId(qname));
				}
				ids.add(new ArtifactTypeId(ia.getArtifactType()));
			}
		}

		// inheritance
		ids.addAll(this.getReferencedTOSCAComponentIdOfParentForAnAbstractComponentsWithTypeReferenceResource(id));

		return ids;
	}

	private Collection<TOSCAComponentId> getReferencedTOSCAComponentIds(RelationshipTypeImplementationId id) {
		// We have to use a HashSet to ensure that no duplicate ids are added
		// There may be multiple IAs referencing the same type
		Collection<TOSCAComponentId> ids = new HashSet<>();

		RelationshipTypeImplementationResource res = new RelationshipTypeImplementationResource(id);

		// IAs
		for (TImplementationArtifact ia : res.getRTI().getImplementationArtifacts().getImplementationArtifact()) {
			QName qname;
			if ((qname = ia.getArtifactRef()) != null) {
				ids.add(new ArtifactTemplateId(qname));
			}
			ids.add(new ArtifactTypeId(ia.getArtifactType()));
		}

		// inheritance
		ids.addAll(this.getReferencedTOSCAComponentIdOfParentForAnAbstractComponentsWithTypeReferenceResource(id));

		return ids;
	}

	private Collection<TOSCAComponentId> getReferencedTOSCAComponentIds(RequirementTypeId id) {
		Collection<TOSCAComponentId> ids = new ArrayList<>(1);

		RequirementTypeResource res = new RequirementTypeResource(id);
		QName requiredCapabilityType = res.getRequirementType().getRequiredCapabilityType();
		if (requiredCapabilityType != null) {
			CapabilityTypeId capId = new CapabilityTypeId(requiredCapabilityType);
			ids.add(capId);
		}
		return ids;
	}

	private Collection<TOSCAComponentId> getReferencedTOSCAComponentIds(CapabilityTypeId id) {
		return Collections.emptyList();
	}

	private Collection<TOSCAComponentId> getReferencedTOSCAComponentIds(PolicyTypeId id) {
		return Collections.emptyList();
	}

	private Collection<TOSCAComponentId> getReferencedTOSCAComponentIds(PolicyTemplateId id) {
		Collection<TOSCAComponentId> ids = new ArrayList<>();
		PolicyTemplateResource res = new PolicyTemplateResource(id);
		ids.add(new PolicyTypeId(res.getType()));
		return ids;
	}

	/**
	 * Synchronizes the plan model references and returns the referenced TOSCA
	 * Component Ids.
	 */
	private Collection<TOSCAComponentId> prepareForExport(ServiceTemplateId id) {
		// We have to use a HashSet to ensure that no duplicate ids are added
		// E.g., there may be multiple relationship templates having the same type
		Collection<TOSCAComponentId> ids = new HashSet<>();
		ServiceTemplateResource res = new ServiceTemplateResource(id);

		// ensure that the plans stored locally are the same ones as stored in the definitions
		res.synchronizeReferences();

		// add all plans as reference in the CSAR
		// the data model is consistent with the repository
		// we crawl through the repository to as putRefAsReferencedItemInCSAR expects a repository file reference
		PlansId plansContainerId = new PlansId(id);
		SortedSet<PlanId> nestedPlans = Repository.INSTANCE.getNestedIds(plansContainerId, PlanId.class);
		for (PlanId planId : nestedPlans) {
			SortedSet<RepositoryFileReference> containedFiles = Repository.INSTANCE.getContainedFiles(planId);
			// even if we currently support only one file in the directory, we just add everything
			for (RepositoryFileReference ref : containedFiles) {
				this.putRefAsReferencedItemInCSAR(ref);
			}
		}

		// add included things to export queue

		TBoundaryDefinitions boundaryDefs;
		if ((boundaryDefs = res.getServiceTemplate().getBoundaryDefinitions()) != null) {
			Policies policies = boundaryDefs.getPolicies();
			if (policies != null) {
				for (TPolicy policy : policies.getPolicy()) {
					PolicyTypeId policyTypeId = new PolicyTypeId(policy.getPolicyType());
					ids.add(policyTypeId);
					PolicyTemplateId policyTemplateId = new PolicyTemplateId(policy.getPolicyRef());
					ids.add(policyTemplateId);
				}
			}

			// reqs and caps don't have to be exported here as they are references to existing reqs/caps (of nested node templates)
		}

		if (res.getServiceTemplate().getTopologyTemplate() != null) {
			for (TEntityTemplate entityTemplate : res.getServiceTemplate().getTopologyTemplate().getNodeTemplateOrRelationshipTemplate()) {
				QName qname = entityTemplate.getType();
				if (entityTemplate instanceof TNodeTemplate) {
					ids.add(new NodeTypeId(qname));
					TNodeTemplate n = (TNodeTemplate) entityTemplate;

					// crawl through deployment artifacts
					TDeploymentArtifacts deploymentArtifacts = n.getDeploymentArtifacts();
					if (deploymentArtifacts != null) {
						List<TDeploymentArtifact> das = deploymentArtifacts.getDeploymentArtifact();
						for (TDeploymentArtifact da : das) {
							ids.add(new ArtifactTypeId(da.getArtifactType()));
							if ((qname = da.getArtifactRef()) != null) {
								ids.add(new ArtifactTemplateId(qname));
							}
						}
					}

					// crawl through reqs/caps
					Requirements requirements = n.getRequirements();
					if (requirements != null) {
						for (TRequirement req : requirements.getRequirement()) {
							QName type = req.getType();
							RequirementTypeId rtId = new RequirementTypeId(type);
							ids.add(rtId);
						}
					}
					Capabilities capabilities = n.getCapabilities();
					if (capabilities != null) {
						for (TCapability cap : capabilities.getCapability()) {
							QName type = cap.getType();
							CapabilityTypeId ctId = new CapabilityTypeId(type);
							ids.add(ctId);
						}
					}

					// crawl through policies
					org.eclipse.winery.model.tosca.TNodeTemplate.Policies policies = n.getPolicies();
					if (policies != null) {
						for (TPolicy pol : policies.getPolicy()) {
							QName type = pol.getPolicyType();
							PolicyTypeId ctId = new PolicyTypeId(type);
							ids.add(ctId);
						}
					}
				} else {
					assert (entityTemplate instanceof TRelationshipTemplate);
					ids.add(new RelationshipTypeId(qname));
				}
			}
		}

		return ids;
	}

	private Collection<TOSCAComponentId> getReferencedTOSCAComponentIds(ArtifactTypeId id) {
		// no recursive crawling needed
		return Collections.emptyList();
	}

	/**
	 * Determines the referenced TOSCA Component Ids and also updates the
	 * references in the Artifact Template
	 *
	 * @return a collection of referenced TOCSA Component Ids
	 */
	private Collection<TOSCAComponentId> prepareForExport(ArtifactTemplateId id) throws RepositoryCorruptException {
		Collection<TOSCAComponentId> ids = new ArrayList<>();

		ArtifactTemplateResource res = new ArtifactTemplateResource(id);

		// "Export" type
		QName type = res.getType();
		if (type == null) {
			throw new RepositoryCorruptException("Type is null for " + id.toString());
		}
		ids.add(new ArtifactTypeId(type));

		// Export files

		// This method is called BEFORE the concrete definitions element is written.
		// Therefore, we adapt the content of the attached files to the really existing files
		res.synchronizeReferences();

		ArtifactTemplateDirectoryId fileDir = new ArtifactTemplateDirectoryId(id);
		SortedSet<RepositoryFileReference> files = Repository.INSTANCE.getContainedFiles(fileDir);
		for (RepositoryFileReference ref : files) {
			// Even if writing a TOSCA only (!this.writingCSAR),
			// we put the virtual path in the TOSCA
			// Reason: Winery is mostly used as a service and local storage
			// reference to not make sense
			// The old implementation had absolutePath.toUri().toString();
			// there, but this does not work when using a cloud blob store.

			this.putRefAsReferencedItemInCSAR(ref);
		}

		return ids;
	}

	/**
	 * Puts the given reference as item in the CSAR
	 *
	 * Thereby, it uses the global variable referencesToPathInCSARMap
	 */
	private void putRefAsReferencedItemInCSAR(RepositoryFileReference ref) {
		// Determine path
		String path = BackendUtils.getPathInsideRepo(ref);

		// put mapping reference to path into global map
		// the path is the same as put in "synchronizeReferences"
		this.referencesToPathInCSARMap.put(ref, path);
	}

	private Collection<TOSCAComponentId> getReferencedTOSCAComponentIds(RelationshipTypeId id) {
		Collection<TOSCAComponentId> ids = new ArrayList<>();

		// add all implementations
		Collection<RelationshipTypeImplementationId> allTypeImplementations = BackendUtils.getAllElementsRelatedWithATypeAttribute(RelationshipTypeImplementationId.class, id.getQName());
		for (RelationshipTypeImplementationId ntiId : allTypeImplementations) {
			ids.add(ntiId);
		}

		RelationshipTypeResource res = new RelationshipTypeResource(id);
		TRelationshipType relationshipType = (TRelationshipType) res.getElement();

		ValidSource validSource = relationshipType.getValidSource();
		if (validSource != null) {
			QName typeRef = validSource.getTypeRef();
			// can be a node type or a requirement type

			// similar code as for valid target (difference: req/cap)
			NodeTypeId ntId = new NodeTypeId(typeRef);
			if (Repository.INSTANCE.exists(ntId)) {
				ids.add(ntId);
			} else {
				RequirementTypeId rtId = new RequirementTypeId(typeRef);
				ids.add(rtId);
			}
		}

		ValidTarget validTarget = relationshipType.getValidTarget();
		if (validTarget != null) {
			QName typeRef = validTarget.getTypeRef();
			// can be a node type or a capability type

			// similar code as for valid target (difference: req/cap)
			NodeTypeId ntId = new NodeTypeId(typeRef);
			if (Repository.INSTANCE.exists(ntId)) {
				ids.add(ntId);
			} else {
				CapabilityTypeId capId = new CapabilityTypeId(typeRef);
				ids.add(capId);
			}
		}

		this.addVisualAppearanceToCSAR(id);

		return ids;
	}

	private Collection<TOSCAComponentId> getReferencedTOSCAComponentIds(NodeTypeId id) {
		Collection<TOSCAComponentId> ids = new ArrayList<>();
		Collection<NodeTypeImplementationId> allNodeTypeImplementations = BackendUtils.getAllElementsRelatedWithATypeAttribute(NodeTypeImplementationId.class, id.getQName());
		for (NodeTypeImplementationId ntiId : allNodeTypeImplementations) {
			ids.add(ntiId);
		}

		NodeTypeResource res = new NodeTypeResource(id);
		TNodeType nodeType = (TNodeType) res.getElement();

		// add all referenced requirement types
		RequirementDefinitions reqDefsContainer = nodeType.getRequirementDefinitions();
		if (reqDefsContainer != null) {
			List<TRequirementDefinition> reqDefs = reqDefsContainer.getRequirementDefinition();
			for (TRequirementDefinition reqDef : reqDefs) {
				RequirementTypeId reqTypeId = new RequirementTypeId(reqDef.getRequirementType());
				ids.add(reqTypeId);
			}
		}

		// add all referenced capability types
		CapabilityDefinitions capDefsContainer = nodeType.getCapabilityDefinitions();
		if (capDefsContainer != null) {
			List<TCapabilityDefinition> capDefs = capDefsContainer.getCapabilityDefinition();
			for (TCapabilityDefinition capDef : capDefs) {
				CapabilityTypeId capTypeId = new CapabilityTypeId(capDef.getCapabilityType());
				ids.add(capTypeId);
			}
		}

		this.addVisualAppearanceToCSAR(id);

		return ids;
	}

	private void addVisualAppearanceToCSAR(TopologyGraphElementEntityTypeId id) {
		VisualAppearanceId visId = new VisualAppearanceId(id);
		if (Repository.INSTANCE.exists(visId)) {
			// we do NOT check for the id, but simply check for bigIcon.png (only exists in NodeType) and smallIcon.png (exists in NodeType and RelationshipType)

			RepositoryFileReference ref = new RepositoryFileReference(visId, Filename.FILENAME_BIG_ICON);
			if (Repository.INSTANCE.exists(ref)) {
				this.referencesToPathInCSARMap.put(ref, BackendUtils.getPathInsideRepo(ref));
			}

			ref = new RepositoryFileReference(visId, Filename.FILENAME_SMALL_ICON);
			if (Repository.INSTANCE.exists(ref)) {
				this.referencesToPathInCSARMap.put(ref, BackendUtils.getPathInsideRepo(ref));
			}
		}
	}

}
