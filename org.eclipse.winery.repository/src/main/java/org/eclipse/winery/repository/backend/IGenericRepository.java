/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation, more helper methods
 *     Lukas Harzentter - get namespaces for specific component
 *     Tino Stadelmaier - code cleaning
 *     Philipp Meyer - support for source directory
 *******************************************************************************/
package org.eclipse.winery.repository.backend;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.GenericId;
import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.common.ids.definitions.CapabilityTypeId;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.common.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.common.ids.definitions.PolicyTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeImplementationId;
import org.eclipse.winery.common.ids.definitions.RequirementTypeId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.common.ids.definitions.imports.GenericImportId;
import org.eclipse.winery.common.ids.elements.ToscaElementId;
import org.eclipse.winery.common.interfaces.IWineryRepositoryCommon;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.HasInheritance;
import org.eclipse.winery.model.tosca.HasType;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TCapabilityDefinition;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TRequirementDefinition;
import org.eclipse.winery.model.tosca.TRequirementType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.backend.xsd.XsdImportManager;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;
import org.eclipse.winery.repository.exceptions.WineryRepositoryException;

import org.apache.tika.mime.MediaType;

/**
 * Enables access to the winery repository via Ids defined in package {@link org.eclipse.winery.common.ids}
 *
 * In contrast to {@link org.eclipse.winery.repository.backend.IRepository}, this is NOT dependent on a particular
 * storage format for the properties. These two classes exist to make the need for reengineering explicit.
 *
 * This is a first attempt to offer methods via GenericId. It might happen, that methods, where GenericIds make sense,
 * are simply added to "IWineryRepository" instead of being added here.
 *
 * The ultimate goal is to get rid of this class and to have IWineryRepositoryCommon only.
 *
 * Currently, this class is used internally only
 */
public interface IGenericRepository extends IWineryRepositoryCommon {

	/**
	 * Flags the given TOSCA element as existing. The respective resource itself creates appropriate data files.
	 *
	 * Pre-Condition: !exists(id)<br/> Post-Condition: exists(id)
	 *
	 * Typically, the given TOSCA element is created if a configuration is asked for
	 */
	boolean flagAsExisting(GenericId id);

	/**
	 * Checks whether the associated TOSCA element exists
	 *
	 * @param id the id to check
	 * @return true iff the TOSCA element belonging to the given ID exists
	 */
	boolean exists(GenericId id);

	/**
	 * Deletes the referenced object from the repository
	 */
	void forceDelete(RepositoryFileReference ref) throws IOException;

	/**
	 * @param ref reference to check
	 * @return true if the file associated with the given reference exists
	 */
	boolean exists(RepositoryFileReference ref);

	/**
	 * Puts the given content to the given file. Replaces existing content.
	 *
	 * If the parent of the reference does not exist, it is created.
	 *
	 * @param ref       the reference to the file. Must not be null.
	 * @param content   the content to put into the file. Must not be null.
	 * @param mediaType the media type of the file. Must not be null.
	 * @throws IOException if something goes wrong
	 */
	void putContentToFile(RepositoryFileReference ref, String content, MediaType mediaType) throws IOException;

	/**
	 * Puts the given content to the given file. Replaces existing content.
	 *
	 * If the parent of the reference does not exist, it is created.
	 *
	 * @param ref         the reference to the file
	 * @param inputStream the content to put into the file
	 * @throws IOException if something goes wrong
	 */
	void putContentToFile(RepositoryFileReference ref, InputStream inputStream, MediaType mediaType) throws IOException;

	/**
	 * Creates an opened inputStream of the contents referenced by ref. The stream has to be closed by the caller.
	 *
	 * @param ref the reference to the file
	 * @return an input stream
	 * @throws IOException if something goes wrong
	 */
	InputStream newInputStream(RepositoryFileReference ref) throws IOException;

	/**
	 * Creates a stream of a ZIP file containing all files contained in the given id
	 *
	 * @param id  the id whose children should be zipped
	 * @param out the output stream to write to
	 */
	void getZippedContents(final GenericId id, OutputStream out) throws WineryRepositoryException;


	/**
	 * Returns the size of the file referenced by ref
	 *
	 * @param ref a refernce to the file stored in the repository
	 * @return the size in bytes
	 * @throws IOException if something goes wrong
	 */
	long getSize(RepositoryFileReference ref) throws IOException;

	/**
	 * Returns the last modification time of the entry.
	 *
	 * @param ref the reference to the file
	 * @return the time of the last modification
	 * @throws IOException if something goes wrong
	 */
	FileTime getLastModifiedTime(RepositoryFileReference ref) throws IOException;

	/**
	 * Returns the mimetype belonging to the reference.
	 *
	 * @param ref the reference to the file
	 * @return the mimetype as string
	 * @throws IOException           if something goes wrong
	 * @throws IllegalStateException if an internal error occurs, which is not an IOException
	 */
	String getMimeType(RepositoryFileReference ref) throws IOException;

	/**
	 * @return the last change date of the file belonging to the given reference. NULL if the associated file does not
	 * exist.
	 */
	Date getLastUpdate(RepositoryFileReference ref);

	/**
	 * Returns all components available of the given id type
	 *
	 * @param idClass class of the Ids to search for
	 * @return empty set if no ids are available
	 */
	<T extends DefinitionsChildId> SortedSet<T> getAllDefinitionsChildIds(Class<T> idClass);

	/**
	 * Returns all component instances existing in the repository
	 *
	 * @return empty set if no ids are available
	 */
	default SortedSet<DefinitionsChildId> getAllDefinitionsChildIds() {
		return DefinitionsChildId.ALL_TOSCA_COMPONENT_ID_CLASSES
			.stream()
			.flatMap(idClass -> this.getAllDefinitionsChildIds(idClass).stream())
			.collect(Collectors.toCollection(() -> new TreeSet<>()));
	}


	/**
	 * Returns the set of <em>all</em> ids nested in the given reference
	 *
	 * The generated Ids are linked as child to the id associated to the given reference
	 *
	 * Required for getting plans nested in a service template: plans are nested below the PlansOfOneServiceTemplateId
	 *
	 * @param ref     a reference to the TOSCA element to be checked. The path belonging to this element is checked.
	 * @param idClass the class of the Id
	 * @return the set of Ids nested in the given reference. Empty set if there are no or the reference itself does not
	 * exist.
	 */
	<T extends ToscaElementId> SortedSet<T> getNestedIds(GenericId ref, Class<T> idClass);

	/**
	 * Returns the set of files nested in the given reference
	 */
	SortedSet<RepositoryFileReference> getContainedFiles(GenericId id);

	/**
	 * Returns all namespaces used by all known definition children
	 */
	Collection<Namespace> getUsedNamespaces();

	/**
	 * Returns all namespaces specific for a given definition child
	 *
	 * @param clazz the definition child id class which namespaces' should be returned.
	 */
	Collection<Namespace> getComponentsNamespaces(Class<? extends DefinitionsChildId> clazz);

	/**
	 * @param clazz          the id class of the entities to discover
	 * @param qNameOfTheType the QName of the type, where all DefinitionsChildIds, where the associated element points
	 *                       to the type
	 */
	default <X extends DefinitionsChildId> Collection<X> getAllElementsReferencingGivenType(Class<X> clazz, QName qNameOfTheType) {
		Objects.requireNonNull(clazz);
		Objects.requireNonNull(qNameOfTheType);

		// we do not use any database system,
		// therefore we have to crawl through each node type implementation by ourselves
		return RepositoryFactory.getRepository().getAllDefinitionsChildIds(clazz)
			.stream()
			// The resource may have been freshly initialized due to existence of a directory
			// then it has no node type assigned leading to ntiRes.getType() being null
			// we ignore this error here
			.filter(id -> ((HasType) this.getDefinitions(id).getElement()).getTypeAsQName().equals(qNameOfTheType))
			.collect(Collectors.toList());
	}

	/**
	 * This is a helper method. Since we currently rely on default implementations, we have to expose this helper method
	 * in the interface.
	 *
	 * There is now the equivalent id class for AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal,
	 * therefore we take the super type and hope that the caller knows what he does.
	 */
	default Collection<DefinitionsChildId> getReferencedDefinitionsChildIdOfParentForAnAbstractComponentsWithTypeReferenceResource(DefinitionsChildId id) {
		final HasInheritance element = (HasInheritance) this.getDefinitions(id).getElement();
		final HasType derivedFrom = element.getDerivedFrom();
		QName derivedFromType = null;
		if (derivedFrom != null) {
			derivedFromType = derivedFrom.getTypeAsQName();
		}

		if (derivedFromType == null) {
			return Collections.emptySet();
		} else {
			// Instantiate an id with the same class as the current id
			DefinitionsChildId parentId;

			Constructor<? extends DefinitionsChildId> constructor;
			try {
				constructor = id.getClass().getConstructor(QName.class);
			} catch (NoSuchMethodException | SecurityException e1) {
				throw new IllegalStateException("Could get constructor to instantiate parent id", e1);
			}
			try {
				parentId = constructor.newInstance(derivedFromType);
			} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
				throw new IllegalStateException("Could not instantiate id for parent", e);
			}

			Collection<DefinitionsChildId> result = new ArrayList<>(1);
			result.add(parentId);
			return result;
		}
	}

	default Collection<DefinitionsChildId> getReferencedDefinitionsChildIds(NodeTypeId id) {
		Collection<DefinitionsChildId> ids = new ArrayList<>();
		Collection<NodeTypeImplementationId> allNodeTypeImplementations = this.getAllElementsReferencingGivenType(NodeTypeImplementationId.class, id.getQName());
		for (NodeTypeImplementationId ntiId : allNodeTypeImplementations) {
			ids.add(ntiId);
		}

		final TNodeType nodeType = this.getElement(id);

		// add all referenced requirement types
		TNodeType.RequirementDefinitions reqDefsContainer = nodeType.getRequirementDefinitions();
		if (reqDefsContainer != null) {
			List<TRequirementDefinition> reqDefs = reqDefsContainer.getRequirementDefinition();
			for (TRequirementDefinition reqDef : reqDefs) {
				RequirementTypeId reqTypeId = new RequirementTypeId(reqDef.getRequirementType());
				ids.add(reqTypeId);
			}
		}

		// add all referenced capability types
		TNodeType.CapabilityDefinitions capDefsContainer = nodeType.getCapabilityDefinitions();
		if (capDefsContainer != null) {
			List<TCapabilityDefinition> capDefs = capDefsContainer.getCapabilityDefinition();
			for (TCapabilityDefinition capDef : capDefs) {
				CapabilityTypeId capTypeId = new CapabilityTypeId(capDef.getCapabilityType());
				ids.add(capTypeId);
			}
		}

		return ids;
	}

	default Collection<DefinitionsChildId> getReferencedDefinitionsChildIds(NodeTypeImplementationId id) {
		// We have to use a HashSet to ensure that no duplicate ids are added
		// There may be multiple DAs/IAs referencing the same type
		Collection<DefinitionsChildId> ids = new HashSet<>();

		final TNodeTypeImplementation element = this.getElement(id);

		// DAs
		TDeploymentArtifacts deploymentArtifacts = element.getDeploymentArtifacts();
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
		return this.getReferencedTOSCAComponentImplementationArtifactIds(ids, element.getImplementationArtifacts(), id);
	}

	default Collection<DefinitionsChildId> getReferencedDefinitionsChildIds(RelationshipTypeImplementationId id) {
		// We have to use a HashSet to ensure that no duplicate ids are added
		// There may be multiple IAs referencing the same type
		Collection<DefinitionsChildId> ids = new HashSet<>();

		final TRelationshipTypeImplementation element = this.getElement(id);

		// IAs
		return this.getReferencedTOSCAComponentImplementationArtifactIds(ids, element.getImplementationArtifacts(), id);
	}

	default Collection<DefinitionsChildId> getReferencedDefinitionsChildIds(ArtifactTypeId id) {
		// no recursive crawling needed
		return Collections.emptyList();
	}

	/**
	 * Helper method
	 *
	 * @param ids                     the list of ids to add the new ids to
	 * @param implementationArtifacts the implementation artifacts belonging to the given id
	 * @param id                      the id to handle
	 */
	default Collection<DefinitionsChildId> getReferencedTOSCAComponentImplementationArtifactIds(Collection<DefinitionsChildId> ids, TImplementationArtifacts implementationArtifacts, DefinitionsChildId id) {
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
		// ids.addAll(this.getReferencedDefinitionsChildIdOfParentForAnAbstractComponentsWithTypeReferenceResource(id));

		return ids;
	}

	default Collection<DefinitionsChildId> getReferencedDefinitionsChildIds(RequirementTypeId id) {
		Collection<DefinitionsChildId> ids = new ArrayList<>(1);

		final TRequirementType element = this.getElement(id);

		QName requiredCapabilityType = element.getRequiredCapabilityType();
		if (requiredCapabilityType != null) {
			CapabilityTypeId capId = new CapabilityTypeId(requiredCapabilityType);
			ids.add(capId);
		}
		return ids;
	}

	default Collection<DefinitionsChildId> getReferencedDefinitionsChildIds(PolicyTemplateId id) {
		Collection<DefinitionsChildId> ids = new ArrayList<>();

		final TPolicyTemplate element = this.getElement(id);
		ids.add(new PolicyTypeId(element.getType()));
		return ids;
	}

	default Collection<DefinitionsChildId> getReferencedDefinitionsChildIds(RelationshipTypeId id) {
		Collection<DefinitionsChildId> ids = new ArrayList<>();

		// add all implementations
		Collection<RelationshipTypeImplementationId> allTypeImplementations = this.getAllElementsReferencingGivenType(RelationshipTypeImplementationId.class, id.getQName());
		for (RelationshipTypeImplementationId ntiId : allTypeImplementations) {
			ids.add(ntiId);
		}

		final TRelationshipType relationshipType = this.getElement(id);

		TRelationshipType.ValidSource validSource = relationshipType.getValidSource();
		if (validSource != null) {
			QName typeRef = validSource.getTypeRef();
			// can be a node type or a requirement type

			// similar code as for valid target (difference: req/cap)
			NodeTypeId ntId = new NodeTypeId(typeRef);
			if (this.exists(ntId)) {
				ids.add(ntId);
			} else {
				RequirementTypeId rtId = new RequirementTypeId(typeRef);
				ids.add(rtId);
			}
		}

		TRelationshipType.ValidTarget validTarget = relationshipType.getValidTarget();
		if (validTarget != null) {
			QName typeRef = validTarget.getTypeRef();
			// can be a node type or a capability type

			// similar code as for valid target (difference: req/cap)
			NodeTypeId ntId = new NodeTypeId(typeRef);
			if (this.exists(ntId)) {
				ids.add(ntId);
			} else {
				CapabilityTypeId capId = new CapabilityTypeId(typeRef);
				ids.add(capId);
			}
		}

		return ids;
	}


	/**
	 * Determines the referenced definition children Ids. Does NOT return the included files.
	 *
	 * @return a collection of referenced definition child Ids
	 */
	default Collection<DefinitionsChildId> getReferencedDefinitionsChildIds(ArtifactTemplateId id) throws RepositoryCorruptException {
		Collection<DefinitionsChildId> ids = new ArrayList<>();

		final TArtifactTemplate artifactTemplate = this.getElement(id);

		// "Export" type
		QName type = artifactTemplate.getType();
		if (type == null) {
			throw new RepositoryCorruptException("Type is null for " + id.toReadableString());
		} else {
			ids.add(new ArtifactTypeId(type));
		}

		return ids;
	}

	default Collection<DefinitionsChildId> getReferencedDefinitionsChildIds(ServiceTemplateId id) {
		// We have to use a HashSet to ensure that no duplicate ids are added
		// E.g., there may be multiple relationship templates having the same type
		Collection<DefinitionsChildId> ids = new HashSet<>();

		TServiceTemplate serviceTemplate = this.getElement(id);

		// add included things to export queue

		TBoundaryDefinitions boundaryDefs;
		if ((boundaryDefs = serviceTemplate.getBoundaryDefinitions()) != null) {
			TBoundaryDefinitions.Policies policies = boundaryDefs.getPolicies();
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

		if (serviceTemplate.getTopologyTemplate() != null) {
			for (TEntityTemplate entityTemplate : serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate()) {
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
					TNodeTemplate.Requirements requirements = n.getRequirements();
					if (requirements != null) {
						for (TRequirement req : requirements.getRequirement()) {
							QName type = req.getType();
							RequirementTypeId rtId = new RequirementTypeId(type);
							ids.add(rtId);
						}
					}
					TNodeTemplate.Capabilities capabilities = n.getCapabilities();
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

	default Collection<DefinitionsChildId> getReferencedDefinitionsChildIds(DefinitionsChildId id) throws RepositoryCorruptException {
		Collection<DefinitionsChildId> referencedDefinitionsChildIds;
		if (id instanceof ServiceTemplateId) {
			referencedDefinitionsChildIds = this.getReferencedDefinitionsChildIds((ServiceTemplateId) id);
		} else if (id instanceof NodeTypeId) {
			referencedDefinitionsChildIds = this.getReferencedDefinitionsChildIds((NodeTypeId) id);
		} else if (id instanceof NodeTypeImplementationId) {
			referencedDefinitionsChildIds = this.getReferencedDefinitionsChildIds((NodeTypeImplementationId) id);
		} else if (id instanceof RelationshipTypeId) {
			referencedDefinitionsChildIds = this.getReferencedDefinitionsChildIds((RelationshipTypeId) id);
		} else if (id instanceof RelationshipTypeImplementationId) {
			referencedDefinitionsChildIds = this.getReferencedDefinitionsChildIds((RelationshipTypeImplementationId) id);
		} else if (id instanceof RequirementTypeId) {
			referencedDefinitionsChildIds = this.getReferencedDefinitionsChildIds((RequirementTypeId) id);
		} else if (id instanceof ArtifactTypeId) {
			referencedDefinitionsChildIds = this.getReferencedDefinitionsChildIds((ArtifactTypeId) id);
		} else if (id instanceof ArtifactTemplateId) {
			referencedDefinitionsChildIds = this.getReferencedDefinitionsChildIds((ArtifactTemplateId) id);
		} else if (id instanceof PolicyTemplateId) {
			referencedDefinitionsChildIds = this.getReferencedDefinitionsChildIds((PolicyTemplateId) id);
		} else if (id instanceof GenericImportId || id instanceof PolicyTypeId || id instanceof CapabilityTypeId) {
			// in case of imports, policy types, and capability types, there are no other ids referenced
			referencedDefinitionsChildIds = Collections.emptyList();
		} else {
			throw new IllegalStateException("Unhandled id class " + id.getClass());
		}
		return referencedDefinitionsChildIds;
	}

	NamespaceManager getNamespaceManager();

	XsdImportManager getXsdImportManager();

	/**
	 * Updates the element belonging to the given DefinitionsChildId Regenerates wrapper definitions; thus all
	 * extensions at the wrapper definitions are lost
	 *
	 * @param id      the DefinitionsChildId to update
	 * @param element the element to set
	 * @throws IOException if persisting went wrong
	 */
	default void setElement(DefinitionsChildId id, TExtensibleElements element) throws IOException {
		// default implementation on the server side
		// the client side has to use the REST method
		Definitions definitions = BackendUtils.createWrapperDefinitions(id);
		definitions.setElement(element);
		BackendUtils.persist(id, definitions);
	}
}
