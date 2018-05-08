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
import java.util.Optional;
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
import org.eclipse.winery.common.ids.definitions.ComplianceRuleId;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.common.ids.definitions.HasInheritanceId;
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
import org.eclipse.winery.model.tosca.TAppliesTo;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TCapabilityDefinition;
import org.eclipse.winery.model.tosca.TComplianceRule;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TEntityTypeImplementation;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.model.tosca.TPolicyType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TRequirementDefinition;
import org.eclipse.winery.model.tosca.TRequirementType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.backend.xsd.XsdImportManager;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;
import org.eclipse.winery.repository.exceptions.WineryRepositoryException;

import org.apache.tika.mime.MediaType;

/**
 * Enables access to the winery repository via Ids defined in package {@link org.eclipse.winery.common.ids}
 * <p>
 * In contrast to {@link org.eclipse.winery.repository.backend.IRepository}, this is NOT dependent on a particular
 * storage format for the properties. These two classes exist to make the need for reengineering explicit.
 * <p>
 * This is a first attempt to offer methods via GenericId. It might happen, that methods, where GenericIds make sense,
 * are simply added to "IWineryRepository" instead of being added here.
 * <p>
 * The ultimate goal is to get rid of this class and to have IWineryRepositoryCommon only.
 * <p>
 * Currently, this class is used internally only
 */
public interface IGenericRepository extends IWineryRepositoryCommon {

    /**
     * Flags the given TOSCA element as existing. The respective resource itself creates appropriate data files.
     * <p>
     * Pre-Condition: !exists(id)<br/> Post-Condition: exists(id)
     * <p>
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
     * <p>
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
     * <p>
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
     * @param ref a reference to the file stored in the repository
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
     * Returns all stable components available of the given id type.
     * Components without a version are also included.
     *
     * @param idClass class of the Ids to search for
     * @return empty set if no ids are available
     */
    public <T extends DefinitionsChildId> SortedSet<T> getStableDefinitionsChildIdsOnly(Class<T> idClass);

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
     * <p>
     * The generated Ids are linked as child to the id associated to the given reference
     * <p>
     * Required for
     * - getting plans nested in a service template: plans are nested below the PlansOfOneServiceTemplateId
     * - exporting service templates
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
        return this.getAllDefinitionsChildIds(clazz)
            .stream()
            // The resource may have been freshly initialized due to existence of a directory
            // then it has no node type assigned leading to ntiRes.getType() being null
            // we ignore this error here
            .filter((X id) -> {
                TExtensibleElements element = this.getDefinitions(id).getElement();
                boolean referencesGivenQName = false;

                if (element instanceof HasType) {
                    referencesGivenQName = ((HasType) element).getTypeAsQName().equals(qNameOfTheType);
                }

                if (!referencesGivenQName && element instanceof HasInheritance) {
                    HasType derivedFrom = ((HasInheritance) element).getDerivedFrom();
                    referencesGivenQName = Objects.nonNull(derivedFrom) && derivedFrom.equals(qNameOfTheType);
                }

                if (!referencesGivenQName && element instanceof TRelationshipType) {
                    TRelationshipType.ValidTarget validTarget = ((TRelationshipType) element).getValidTarget();
                    TRelationshipType.ValidSource validSource = ((TRelationshipType) element).getValidSource();

                    referencesGivenQName = Objects.nonNull(validTarget) && validTarget.getTypeRef().equals(qNameOfTheType);
                    referencesGivenQName = !referencesGivenQName && Objects.nonNull(validSource) && validSource.getTypeRef().equals(qNameOfTheType);
                }

                if (!referencesGivenQName && element instanceof TEntityTypeImplementation) {
                    TImplementationArtifacts implementationArtifacts = ((TEntityTypeImplementation) element).getImplementationArtifacts();
                    referencesGivenQName = Objects.nonNull(implementationArtifacts) &&
                        implementationArtifacts.getImplementationArtifact()
                            .stream()
                            .anyMatch(implementationArtifact ->
                                implementationArtifact.getArtifactRef().equals(qNameOfTheType) ||
                                    implementationArtifact.getArtifactType().equals(qNameOfTheType));

                    if (!referencesGivenQName && element instanceof TNodeTypeImplementation) {
                        TDeploymentArtifacts deploymentArtifacts = ((TNodeTypeImplementation) element).getDeploymentArtifacts();
                        referencesGivenQName = Objects.nonNull(deploymentArtifacts) &&
                            deploymentArtifacts.getDeploymentArtifact()
                                .stream()
                                .anyMatch(tDeploymentArtifact ->
                                    tDeploymentArtifact.getArtifactRef().equals(qNameOfTheType) ||
                                        tDeploymentArtifact.getArtifactType().equals(qNameOfTheType));
                    }
                }

                if (!referencesGivenQName && element instanceof TPolicyType) {
                    TAppliesTo appliesTo = ((TPolicyType) element).getAppliesTo();
                    referencesGivenQName = Objects.nonNull(appliesTo) && appliesTo.getNodeTypeReference()
                        .stream()
                        .anyMatch(nodeTypeReference -> nodeTypeReference.getTypeRef().equals(qNameOfTheType));
                }

                if (!referencesGivenQName && element instanceof TNodeType) {
                    TNodeType.RequirementDefinitions requirementDefinitions = ((TNodeType) element).getRequirementDefinitions();
                    referencesGivenQName = Objects.nonNull(requirementDefinitions) &&
                        requirementDefinitions.getRequirementDefinition()
                            .stream()
                            .anyMatch(tRequirementDefinition -> tRequirementDefinition.getRequirementType().equals(qNameOfTheType));

                    if (!referencesGivenQName) {
                        TNodeType.CapabilityDefinitions capabilityDefinitions = ((TNodeType) element).getCapabilityDefinitions();
                        referencesGivenQName = Objects.nonNull(capabilityDefinitions) &&
                            capabilityDefinitions
                                .getCapabilityDefinition()
                                .stream()
                                .anyMatch(tCapabilityDefinition -> tCapabilityDefinition.getCapabilityType().equals(qNameOfTheType));
                    }
                }

                if (!referencesGivenQName && element instanceof TEntityType) {
                    TEntityType.PropertiesDefinition propertiesDefinition = ((TEntityType) element).getPropertiesDefinition();
                    if (Objects.nonNull(propertiesDefinition)) {
                        referencesGivenQName = Objects.nonNull(propertiesDefinition.getElement()) && propertiesDefinition.getElement().equals(qNameOfTheType)
                            || Objects.nonNull(propertiesDefinition.getType()) && propertiesDefinition.getType().equals(qNameOfTheType);
                    }
                }

                if (!referencesGivenQName && element instanceof TServiceTemplate) {
                 /*   TTopologyTemplate topologyTemplate = ((TServiceTemplate) element).getTopologyTemplate();

                    topologyTemplate.getRelationshipTemplates()
                        .stream()
                        .anyMatch(tRelationshipTemplate -> tRelationshipTemplate.)*/
                }

                return referencesGivenQName;
            })
            .collect(Collectors.toList());
    }

    /**
     * Determines the id of the parent in the inheritance hierarchy (if exists).
     *
     * @return the id of the parent class
     */
    default Optional<DefinitionsChildId> getDefinitionsChildIdOfParent(HasInheritanceId id) {
        final HasInheritance element = (HasInheritance) this.getDefinitions(id).getElement();
        final HasType derivedFrom = element.getDerivedFrom();
        QName derivedFromType = null;
        if (derivedFrom != null) {
            derivedFromType = derivedFrom.getTypeAsQName();
        }

        if (derivedFromType == null) {
            return Optional.empty();
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

            return Optional.of(parentId);
        }
    }

    default Collection<DefinitionsChildId> getReferencedDefinitionsChildIds(NodeTypeId id) {
        Collection<NodeTypeImplementationId> allNodeTypeImplementations = this.getAllElementsReferencingGivenType(NodeTypeImplementationId.class, id.getQName());
        Collection<DefinitionsChildId> ids = new HashSet<>(allNodeTypeImplementations);

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
        // ids.addAll(this.getDefinitionsChildIdOfParent(id));

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
        ids.addAll(this.getAllElementsReferencingGivenType(RelationshipTypeImplementationId.class, id.getQName()));

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

                            QName template = pol.getPolicyRef();
                            if (template != null) {
                                PolicyTemplateId policyTemplateId = new PolicyTemplateId(template);
                                ids.add(policyTemplateId);
                            }
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

    default Collection<DefinitionsChildId> getReferencedDefinitionsChildIds(ComplianceRuleId id) {
        // We have to use a HashSet to ensure that no duplicate ids are added
        // E.g., there may be multiple relationship templates having the same type
        Collection<DefinitionsChildId> ids = new HashSet<>();

        TComplianceRule complianceRule = this.getElement(id);

        //TODO to extra method
        //TODO extend to required Structure
        if (complianceRule.getIdentifier() != null) {
            for (TEntityTemplate entityTemplate : complianceRule.getIdentifier().getNodeTemplateOrRelationshipTemplate()) {
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
    
    /**
     * Determines all referencedDefinitionsChildIds
     *
     * @param id The id to start crawling from
     * @return a list referenced DefinitionChildIds
     * @throws IllegalStateException in case an id is passed, which is not handled in the body
     */
    default Collection<DefinitionsChildId> getReferencedDefinitionsChildIds(DefinitionsChildId id) throws RepositoryCorruptException {
        Collection<DefinitionsChildId> referencedDefinitionsChildIds;

        // First of all, handle the concrete types
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
            // Collections.emptyList() cannot be used as we add elements later on in the case of inheritance
            referencedDefinitionsChildIds = new ArrayList();
        } else if (id instanceof ComplianceRuleId) {
            referencedDefinitionsChildIds = this.getReferencedDefinitionsChildIds((ComplianceRuleId) id);
        } else {
            throw new IllegalStateException("Unhandled id class " + id.getClass());
        }

        // Then, handle the super classes, which support inheritance
        // Currently, it is EntityType and EntityTypeImplementation only
        // Since the latter does not exist in the TOSCA MetaModel, we just handle EntityType here
        if (id instanceof HasInheritanceId) {
            Optional<DefinitionsChildId> parentId = this.getDefinitionsChildIdOfParent((HasInheritanceId) id);
            if (parentId.isPresent()) {
                // add the parent id itself. The referenced definitions are included by recursion
                referencedDefinitionsChildIds.add(parentId.get());
            }
        }

        return referencedDefinitionsChildIds;
    }

    default Collection<DefinitionsChildId> getReferencingDefinitionsChildIds(NodeTypeId id) {
        Collection<DefinitionsChildId> ids = new HashSet<>();

        // NodeTypeImplementations
        ids.addAll(this.getAllElementsReferencingGivenType(NodeTypeImplementationId.class, id.getQName()));
        // RelationshipTypes > validSource + validTarget
        ids.addAll(this.getAllElementsReferencingGivenType(RelationshipTypeId.class, id.getQName()));
        // PolicyTypes
        ids.addAll(this.getAllElementsReferencingGivenType(PolicyTypeId.class, id.getQName()));
        // NodeTypes > derivedFrom
        ids.addAll(this.getAllElementsReferencingGivenType(NodeTypeId.class, id.getQName()));

        // ServiceTemplates > NodeTemplates + substitutable?
        // ids.addAll(this.getAllElementsReferencingGivenType(ServiceTemplateId.class), id.getQName());

        return ids;
    }

    default Collection<DefinitionsChildId> getReferencingDefinitionsChildIds(NodeTypeImplementationId id) {
        // NodeTypeImplementations
        return new HashSet<>(this.getAllElementsReferencingGivenType(NodeTypeImplementationId.class, id.getQName()));
    }

    default Collection<DefinitionsChildId> getReferencingDefinitionsChildIds(RelationshipTypeImplementationId id) {
        // RelationshipTypeImplementations
        return new HashSet<>(this.getAllElementsReferencingGivenType(RelationshipTypeImplementationId.class, id.getQName()));
    }

    default Collection<DefinitionsChildId> getReferencingDefinitionsChildIds(RelationshipTypeId id) {
        Collection<DefinitionsChildId> ids = new HashSet<>();

        // RelationshipTypeImplementations
        ids.addAll(this.getAllElementsReferencingGivenType(RelationshipTypeImplementationId.class, id.getQName()));
        // RelationshipTypes
        ids.addAll(this.getAllElementsReferencingGivenType(RequirementTypeId.class, id.getQName()));
        // ServiceTemplates > RelationshipTemplates
        // ids.addAll(this.getAllElementsReferencingGivenType(ServiceTemplateId.class, id.getQName()));

        return ids;
    }

    default Collection<DefinitionsChildId> getReferencingDefinitionsChildIds(RequirementTypeId id) {
        Collection<DefinitionsChildId> ids = new HashSet<>();

        // RelationshipType > validSource
        ids.addAll(this.getAllElementsReferencingGivenType(RelationshipTypeId.class, id.getQName()));
        // RequirementType
        ids.addAll(this.getAllElementsReferencingGivenType(RequirementTypeId.class, id.getQName()));
        // NodeType > RequirementDefinition
        ids.addAll(this.getAllElementsReferencingGivenType(NodeTypeId.class, id.getQName()));
        // ServiceTemplates > NodeTemplates > Requirements
        // ids.addAll(this.getAllElementsReferencingGivenType(ServiceTemplateId.class, id.getQName()));

        return ids;
    }

    default Collection<DefinitionsChildId> getReferencingDefinitionsChildIds(ArtifactTypeId id) {
        Collection<DefinitionsChildId> ids = new HashSet<>();

        // ArtifactTemplates > type
        ids.addAll(this.getAllElementsReferencingGivenType(ArtifactTemplateId.class, id.getQName()));
        // NodeTypeImplementations > DeploymentArtifact + ImplementationArtifact
        ids.addAll(this.getAllElementsReferencingGivenType(NodeTypeImplementationId.class, id.getQName()));
        // RelationshipTypeImplementations > ImplementationArtifact
        ids.addAll(this.getAllElementsReferencingGivenType(RelationshipTypeImplementationId.class, id.getQName()));
        // ArtifactTypes > derivedFrom
        ids.addAll(this.getAllElementsReferencingGivenType(ArtifactTypeId.class, id.getQName()));
        // ServiceTemplates > NodeTemplates > DeploymentArtifacts
        // ids.addAll(this.getAllElementsReferencingGivenType(ServiceTemplateId.class, id.getQName()));

        return ids;
    }

    default Collection<DefinitionsChildId> getReferencingDefinitionsChildIds(ArtifactTemplateId id) {
        Collection<DefinitionsChildId> ids = new HashSet<>();

        // NodeTypeImplementations > 
        ids.addAll(this.getAllElementsReferencingGivenType(NodeTypeImplementationId.class, id.getQName()));
        // RelationshipTypeImplementations
        ids.addAll(this.getAllElementsReferencingGivenType(RelationshipTypeImplementationId.class, id.getQName()));
        // ServiceTemplates > NodeTemplates > DeploymentArtifacts
        // ids.addAll(this.getAllElementsReferencingGivenType(ServiceTemplateId.class, id.getQName()));

        return ids;
    }

    default Collection<DefinitionsChildId> getReferencingDefinitionsChildIds(PolicyTemplateId id) {
        // ServiceTemplates > BoundaryDefinitions > Policies
        return new HashSet<>(this.getAllElementsReferencingGivenType(ServiceTemplateId.class, id.getQName()));
    }

    default Collection<DefinitionsChildId> getReferencingDefinitionsChildIds(PolicyTypeId id) {
        Collection<DefinitionsChildId> ids = new HashSet<>();

        // PolicyTemplates
        ids.addAll(this.getAllElementsReferencingGivenType(PolicyTemplateId.class, id.getQName()));
        // PolicyTypes
        ids.addAll(this.getAllElementsReferencingGivenType(PolicyTypeId.class, id.getQName()));
        // ServiceTemplates > BoundaryDefinitions > Policies
        // ids.addAll(this.getAllElementsReferencingGivenType(ServiceTemplateId.class, id.getQName()));

        return ids;
    }

    default Collection<DefinitionsChildId> getReferencingDefinitionsChildIds(CapabilityTypeId id) {
        Collection<DefinitionsChildId> ids = new HashSet<>();

        // NodeTypes > CapabilityDefinition
        ids.addAll(this.getAllElementsReferencingGivenType(NodeTypeId.class, id.getQName()));
        // RelationshipTypes > validTarget
        ids.addAll(this.getAllElementsReferencingGivenType(RelationshipTypeId.class, id.getQName()));
        // CapabilityTypes
        ids.addAll(this.getAllElementsReferencingGivenType(CapabilityTypeId.class, id.getQName()));
        // ServiceTemplates > NodeTemplates > Capabilities
        // ids.addAll(this.getAllElementsReferencingGivenType(ServiceTemplateId.class, id.getQName()));

        return ids;
    }

    default Collection<DefinitionsChildId> getReferencingDefinitionsChildIds(GenericImportId id) {
        Collection<DefinitionsChildId> ids = new HashSet<>();

        // ArtifactTypes > PropertiesDefinition
        ids.addAll(this.getAllElementsReferencingGivenType(ArtifactTypeId.class, id.getQName()));
        // CapabilityTypes > PropertiesDefinition
        ids.addAll(this.getAllElementsReferencingGivenType(CapabilityTypeId.class, id.getQName()));
        // NodeTypes > PropertiesDefinition
        ids.addAll(this.getAllElementsReferencingGivenType(NodeTypeId.class, id.getQName()));
        // PolicyTypes > PropertiesDefinition
        ids.addAll(this.getAllElementsReferencingGivenType(PolicyTypeId.class, id.getQName()));
        // RelationshipTypes > PropertiesDefinition
        ids.addAll(this.getAllElementsReferencingGivenType(RelationshipTypeId.class, id.getQName()));
        // RequirementTypes > PropertiesDefinition
        ids.addAll(this.getAllElementsReferencingGivenType(RequirementTypeId.class, id.getQName()));

        return ids;
    }

    /**
     * Collects all DefinitionsChildIds of the specified element which have references to it.
     *
     * @param id The DefinitionsChildId to which references should be collected
     */
    default Collection<DefinitionsChildId> getReferencingDefinitionsChildIds(DefinitionsChildId id) throws RepositoryCorruptException {
        Collection<DefinitionsChildId> referencedDefinitionsChildIds;
        if (id instanceof ServiceTemplateId) {
            referencedDefinitionsChildIds = Collections.emptyList();
        } else if (id instanceof NodeTypeId) {
            referencedDefinitionsChildIds = this.getReferencingDefinitionsChildIds((NodeTypeId) id);
        } else if (id instanceof NodeTypeImplementationId) {
            referencedDefinitionsChildIds = this.getReferencingDefinitionsChildIds((NodeTypeImplementationId) id);
        } else if (id instanceof RelationshipTypeId) {
            referencedDefinitionsChildIds = this.getReferencingDefinitionsChildIds((RelationshipTypeId) id);
        } else if (id instanceof RelationshipTypeImplementationId) {
            referencedDefinitionsChildIds = this.getReferencingDefinitionsChildIds((RelationshipTypeImplementationId) id);
        } else if (id instanceof RequirementTypeId) {
            referencedDefinitionsChildIds = this.getReferencingDefinitionsChildIds((RequirementTypeId) id);
        } else if (id instanceof ArtifactTypeId) {
            referencedDefinitionsChildIds = this.getReferencingDefinitionsChildIds((ArtifactTypeId) id);
        } else if (id instanceof ArtifactTemplateId) {
            referencedDefinitionsChildIds = this.getReferencingDefinitionsChildIds((ArtifactTemplateId) id);
        } else if (id instanceof PolicyTemplateId) {
            referencedDefinitionsChildIds = this.getReferencingDefinitionsChildIds((PolicyTemplateId) id);
        } else if (id instanceof PolicyTypeId) {
            referencedDefinitionsChildIds = this.getReferencingDefinitionsChildIds((PolicyTypeId) id);
        } else if (id instanceof CapabilityTypeId) {
            referencedDefinitionsChildIds = this.getReferencingDefinitionsChildIds((CapabilityTypeId) id);
        } else if (id instanceof GenericImportId) {
            referencedDefinitionsChildIds = this.getReferencingDefinitionsChildIds((GenericImportId) id);
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

    default int getReferenceCount(ArtifactTemplateId id) {
        // We do not use a database, therefore, we have to go through all possibilities pointing to the artifact template
        // DAs and IAs point to an artifact template
        // DAs are contained in Node Type Implementations and Node Templates
        // IAs are contained in Node Type Implementations and Relationship Type Implementations

        int count = 0;

        Collection<TDeploymentArtifact> allDAs = new HashSet<>();
        Collection<TImplementationArtifact> allIAs = new HashSet<>();

        // handle Node Type Implementation, which contains DAs and IAs
        SortedSet<NodeTypeImplementationId> nodeTypeImplementations = this.getAllDefinitionsChildIds(NodeTypeImplementationId.class);
        for (NodeTypeImplementationId ntiId : nodeTypeImplementations) {
            final TNodeTypeImplementation nodeTypeImplementation = this.getElement(ntiId);
            TDeploymentArtifacts deploymentArtifacts = nodeTypeImplementation.getDeploymentArtifacts();
            if (deploymentArtifacts != null) {
                allDAs.addAll(deploymentArtifacts.getDeploymentArtifact());
            }
            TImplementationArtifacts implementationArtifacts = nodeTypeImplementation.getImplementationArtifacts();
            if (implementationArtifacts != null) {
                allIAs.addAll(implementationArtifacts.getImplementationArtifact());
            }
        }

        // check all Relationshiptype Implementations for IAs
        SortedSet<RelationshipTypeImplementationId> relationshipTypeImplementations = this.getAllDefinitionsChildIds(RelationshipTypeImplementationId.class);
        for (RelationshipTypeImplementationId rtiId : relationshipTypeImplementations) {
            TImplementationArtifacts implementationArtifacts = this.getElement(rtiId).getImplementationArtifacts();
            if (implementationArtifacts != null) {
                allIAs.addAll(implementationArtifacts.getImplementationArtifact());
            }
        }

        // check all node templates for DAs
        SortedSet<ServiceTemplateId> serviceTemplates = this.getAllDefinitionsChildIds(ServiceTemplateId.class);
        for (ServiceTemplateId sid : serviceTemplates) {
            TTopologyTemplate topologyTemplate = this.getElement(sid).getTopologyTemplate();
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

        QName ourQName = id.getQName();

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
}
