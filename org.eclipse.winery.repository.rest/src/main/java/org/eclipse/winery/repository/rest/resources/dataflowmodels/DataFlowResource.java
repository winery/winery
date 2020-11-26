/*******************************************************************************
 * Copyright (c) 2012-2019 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.dataflowmodels;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TRequirementDefinition;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTag;
import org.eclipse.winery.model.tosca.TTags;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.extensions.kvproperties.PropertyDefinitionKV;
import org.eclipse.winery.model.tosca.extensions.kvproperties.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.apiData.DataFlowModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.eclipse.winery.model.adaptation.placement.Constants.ARTIFACT_TEMPLATE_GROUP;
import static org.eclipse.winery.model.adaptation.placement.Constants.DATA_TRANSFER_TYPE_PULL;
import static org.eclipse.winery.model.adaptation.placement.Constants.RELATIONSHIP_TEMPLATE_TRANSFER_TYPE_PULL;
import static org.eclipse.winery.model.adaptation.placement.Constants.SERVICE_TEMPLATE_GROUP;
import static org.eclipse.winery.model.adaptation.placement.Constants.TAG_NAME_LOCATION;
import static org.eclipse.winery.model.adaptation.placement.Constants.TAG_NAME_PROVIDER;

public class DataFlowResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataFlowResource.class);

    private static int IdCounter = 1;

    @POST
    @Consumes( {MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response parseDataFlowToServiceTemplate(DataFlowModel dataFlowModel) {

        if (Objects.isNull(dataFlowModel)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Passed data flow model is null!").build();
        }

        if (Objects.isNull(dataFlowModel.getId().getNamespaceURI())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Namespace must be defined for the data flow " +
                "model ID!").build();
        }

        IRepository repo = RepositoryFactory.getRepository();
        ServiceTemplateId templateId = new ServiceTemplateId(dataFlowModel.getId());
        if (repo.exists(templateId)) {
            return Response.status(Response.Status.CONFLICT)
                .entity("ServiceTemplate with name of the data flow model already exists!").build();
        }

        TDefinitions definitions = BackendUtils.createWrapperDefinitionsAndInitialEmptyElement(repo, templateId);
        TServiceTemplate serviceTemplate =
            definitions.getServiceTemplates().stream()
                .filter(template -> template.getId().equals(templateId.getQName().getLocalPart()) && template
                    .getTargetNamespace().equals(templateId.getQName().getNamespaceURI())).findFirst().orElse(null);
        if (Objects.isNull(serviceTemplate)) {
            return Response.serverError().entity("Unable to create ServiceTemplate for the given data flow model!")
                .build();
        }

        @SuppressWarnings("deprecated")
        TTopologyTemplate topology = serviceTemplate.getTopologyTemplate();
        if (Objects.isNull(topology)) {
            topology = new TTopologyTemplate();
        }

        // iterate over all filters of the data flow and create corresponding NodeTemplates
        for (DataFlowModel.Filter filter : dataFlowModel.getFilters()) {
            if (Objects.isNull(filter.getType())) {
                return Response.serverError().entity("Type is missing for a filter!").build();
            }

            NodeTypeId nodeTypeId = BackendUtils.getDefinitionsChildId(NodeTypeId.class, filter.getType());
            if (!repo.exists(nodeTypeId)) {
                TNodeType newNodeType = new TNodeType.Builder(nodeTypeId.getQName().getLocalPart()).setTargetNamespace(nodeTypeId.getQName().getNamespaceURI()).build();
                try {
                    BackendUtils.persist(repo, nodeTypeId, newNodeType);
                } catch (IOException e) {
                    return Response.serverError().entity("Unable to create NodeType "
                        + filter.getType() + " which is not contained in the repository!").build();
                }
            }

            topology = handleFilter(topology, nodeTypeId, filter.getId(), filter.getProperties(), filter
                .getArtifacts(), filter.getLocation(), filter.getProvider());
            if (Objects.isNull(topology)) {
                return Response.serverError()
                    .entity("Unable to handle filter with name: " + filter.getId()).build();
            }
        }

        // without available connectsTo RelationshipType the transformation can not be done
        RelationshipTypeId relationTypeId = BackendUtils.getDefinitionsChildId(RelationshipTypeId.class,
            ToscaBaseTypes.connectsToRelationshipType);
        if (!repo.exists(relationTypeId)) {
            return Response.serverError().entity("Unable to parse data flow model without available connectsTo " +
                "RelationshipType!").build();
        }

        // create connectsTo RelationshipTemplates between NodeTemplates corresponding to connected filters        
        for (DataFlowModel.Pipes pipe : dataFlowModel.getPipes()) {

            if (Objects.isNull(pipe.getSource()) || Objects.isNull(pipe.getTarget())) {
                return Response.serverError().entity("Unable to create RelationshipTemplate for pipe with source or " +
                    "target equal to null!").build();
            }

            TNodeTemplate source = topology.getNodeTemplate(pipe.getSource());
            TNodeTemplate target = topology.getNodeTemplate(pipe.getTarget());
            if (Objects.isNull(source) || Objects.isNull(target)) {
                return Response.serverError().entity("Unable to find NodeTemplates for relation with source: "
                    + pipe.getSource() + " and target: " + pipe.getTarget()).build();
            }

            TRelationshipTemplate relationshipTemplate = createRelationshipTemplate(relationTypeId, source, target,
                pipe.getDataTransferType());
            if (Objects.isNull(relationshipTemplate)) {
                return Response.serverError()
                    .entity("Unable to create RelationshipTemplate between " + source.getId() + " and " + target.getId()).build();
            }
            topology.addRelationshipTemplate(relationshipTemplate);
        }

        serviceTemplate.setTopologyTemplate(topology);

        try {
            BackendUtils.persist(repo, templateId, definitions);
            return Response.created(new URI(RestUtils.getAbsoluteURL(templateId))).build();
        } catch (IOException e) {
            return Response.serverError().entity("IOException while persisting ServiceTemplate for data flow model!")
                .build();
        } catch (URISyntaxException e) {
            return Response.serverError().entity("Unable to parse URI for created ServiceTemplate!").build();
        }
    }

    /**
     * Handle the filter with the given information as a data source if the location and provider are specified or as a
     * data processing component otherwise.
     */
    private TTopologyTemplate handleFilter(TTopologyTemplate topology, NodeTypeId nodeTypeId, String templateName,
                                           Map<String, String> properties, List<QName> artifacts, String location,
                                           String provider) {

        if (Objects.nonNull(location) && Objects.nonNull(provider)) {
            return mergeTemplateForDataSource(topology, nodeTypeId, templateName, properties, location, provider);
        } else {
            return createNodeTemplate(topology, nodeTypeId, templateName, properties, artifacts);
        }
    }

    /**
     * Replace a filter corresponding to a running and placed data source by a ServiceTemplate with the same location
     * and provider tag which contains a NodeTemplate of the NodeType specified for the data source filter.
     */
    private TTopologyTemplate mergeTemplateForDataSource(TTopologyTemplate topology, NodeTypeId nodeTypeId,
                                                         String templateName, Map<String, String> properties,
                                                         String location, String provider) {

        // get all ServiceTemplates in the repo
        IRepository repo = RepositoryFactory.getRepository();
        List<ServiceTemplateId> serviceTemplateIds = repo.getAllDefinitionsChildIds().stream()
            .filter(id -> id.getGroup().equals(SERVICE_TEMPLATE_GROUP) && id instanceof ServiceTemplateId)
            .map(id -> (ServiceTemplateId) id).collect(Collectors.toList());

        for (ServiceTemplateId id : serviceTemplateIds) {
            TServiceTemplate serviceTemplate = repo.getElement(id);

            // only ServiceTemplates with location and provider tags are possible substitution candidates            
            if (containsMatchingTags(serviceTemplate, location, provider) && containsMatchingNodeType(serviceTemplate,
                nodeTypeId)) {
                LOGGER.debug("Found suited substitution candidate for filter {}: {}", templateName, id.getQName());

                TTopologyTemplate substitutionTopology = serviceTemplate.getTopologyTemplate();
                TNodeTemplate filterCorrespondingNode = null;
                LinkedHashMap<String, String> filterCorrespondingNodeProperties = null;

                Map<String, String> nameMap = new HashMap<>();

                // insert all NodeTemplates from the substitution candidate
                for (TNodeTemplate node : substitutionTopology.getNodeTemplates()) {

                    LinkedHashMap<String, String> propertyList = new LinkedHashMap<>();
                    if (Objects.nonNull(node.getProperties()) && Objects.nonNull(ModelUtilities.getPropertiesKV(node))) {
                        propertyList = ModelUtilities.getPropertiesKV(node);
                    }

                    if (node.getType().equals(nodeTypeId.getQName())) {
                        // the NodeTemplate from the data flow model must be renamed to the given name
                        nameMap.put(node.getId(), templateName);
                        node.setId(templateName);
                        filterCorrespondingNode = node;
                        filterCorrespondingNodeProperties = propertyList;
                    } else if (Objects.nonNull(topology.getNodeTemplate(node.getId()))) {
                        // all existing names must be changed too
                        while (Objects.nonNull(topology.getNodeTemplate(node.getId() + "-" + IdCounter))) {
                            IdCounter++;
                        }
                        nameMap.put(node.getId(), node.getId() + "-" + IdCounter);
                        node.setId(node.getId() + "-" + IdCounter);
                    }

                    // update properties of the NodeTemplate if they are set at the filter
                    Map<String, String> propertyCopy = new HashMap(properties);
                    for (String propertyName : propertyCopy.keySet()) {
                        if (propertyList.containsKey(propertyName)) {
                            propertyList.put(propertyName, properties.get(propertyName));
                            properties.remove(propertyName);
                        }
                    }

                    // set the state of all components related to the data source to running
                    propertyList.put("State", "Running");
                    ModelUtilities.setPropertiesKV(node, propertyList);

                    topology.addNodeTemplate(node);
                }

                // add all properties that are not defined in the NodeTypes to the node corresponding to the filter
                if (Objects.nonNull(filterCorrespondingNode) && Objects.nonNull(filterCorrespondingNodeProperties)) {
                    LOGGER.debug("{} properties defined without property at a matching type. Adding to filter " +
                        "NodeTemplate!", properties.size());

                    for (String propertyName : properties.keySet()) {
                        filterCorrespondingNodeProperties.put(propertyName, properties.get(propertyName));
                    }
                    ModelUtilities.setPropertiesKV(filterCorrespondingNode, filterCorrespondingNodeProperties);
                }

                // add location and provider attribute to the NodeTemplate
                filterCorrespondingNode.getOtherAttributes().put(ModelUtilities.NODE_TEMPLATE_REGION, location);
                filterCorrespondingNode.getOtherAttributes().put(ModelUtilities.NODE_TEMPLATE_PROVIDER, provider);

                // add all relations from the substitution fragment to the incomplete topology
                for (TRelationshipTemplate relation : substitutionTopology.getRelationshipTemplates()) {
                    // update source id if it was changed
                    if (nameMap.containsKey(relation.getSourceElement().getRef().getId())) {
                        relation.setSourceNodeTemplate(topology.getNodeTemplate(nameMap.get(relation.getSourceElement().getRef().getId())));
                    }

                    // update target id if it was changed
                    if (nameMap.containsKey(relation.getTargetElement().getRef().getId())) {
                        relation.setTargetNodeTemplate(topology.getNodeTemplate(nameMap.get(relation.getTargetElement().getRef().getId())));
                    }

                    // update id if RelationshipTemplate with same id exists
                    if (Objects.nonNull(topology.getRelationshipTemplate(relation.getId()))) {
                        while (Objects.nonNull(topology.getRelationshipTemplate(relation.getId() + "-" + IdCounter))) {
                            IdCounter++;
                        }
                        relation.setId(relation.getId() + "-" + IdCounter);
                    }

                    topology.addRelationshipTemplate(relation);
                }

                return topology;
            }
        }

        // no substitution of data source possible
        return null;
    }

    /**
     * Checks if the ServiceTemplate contains a NodeTemplate with the given NodeType which has no incoming
     * RelationshipTemplates and is therefore suited as a substitution candidate.
     */
    private boolean containsMatchingNodeType(TServiceTemplate serviceTemplate, NodeTypeId nodeTypeId) {
        return serviceTemplate.getTopologyTemplate().getNodeTemplates().stream()
            .anyMatch(nodeTemplate -> nodeTemplate.getType().equals(nodeTypeId.getQName())
                && ModelUtilities.getIncomingRelationshipTemplates(serviceTemplate.getTopologyTemplate(), nodeTemplate).isEmpty());
    }

    /**
     * Check if the ServiceTemplate contains the location and provider tags with matching values.
     */
    private boolean containsMatchingTags(TServiceTemplate serviceTemplate, String location, String provider) {
        TTags tags = serviceTemplate.getTags();
        if (Objects.isNull(tags)) {
            return false;
        }

        int tests = 0;
        for (TTag tag : tags.getTag()) {
            if (tag.getName().equalsIgnoreCase(TAG_NAME_LOCATION) && tag.getValue().equalsIgnoreCase(location)
                || tag.getName().equalsIgnoreCase(TAG_NAME_PROVIDER) && tag.getValue().equalsIgnoreCase(provider)) {
                tests++;
            }
        }
        return tests == 2;
    }

    /**
     * Create a NodeTemplate corresponding to the given filter with the given type, properties and artifacts and add it
     * to the topology of the incomplete deployment model.
     */
    private TTopologyTemplate createNodeTemplate(TTopologyTemplate topology, NodeTypeId nodeTypeId, String templateName,
                                                 Map<String, String> properties, List<QName> artifacts) {

        // get NodeType to access Requirements for the completion and available properties
        IRepository repo = RepositoryFactory.getRepository();
        TNodeType nodeType = repo.getElement(nodeTypeId);
        if (Objects.isNull(nodeType)) {
            return null;
        }

        TNodeTemplate.Builder templateBuilder = new TNodeTemplate.Builder(templateName, nodeTypeId.getQName());

        // add the defined properties to the NodeTemplate
        if (Objects.nonNull(properties)) {
            LinkedHashMap<String, String> propertyList = new LinkedHashMap<>();

            if (Objects.nonNull(nodeType.getWinerysPropertiesDefinition())) {
                // add empty property for NodeType properties to avoid errors due to missing properties
                WinerysPropertiesDefinition def = nodeType.getWinerysPropertiesDefinition();
                for (PropertyDefinitionKV prop : def.getPropertyDefinitions()) {
                    propertyList.put(prop.getKey(), "");
                }
            }

            // add all properties which are defined at the filter
            propertyList.putAll(properties);

            TEntityTemplate.WineryKVProperties nodeProperties = new TEntityTemplate.WineryKVProperties();
            nodeProperties.setKVProperties(propertyList);
            templateBuilder.setProperties(nodeProperties);
        }

        // add all requirements which are defined by the corresponding NodeType
        TNodeType.RequirementDefinitions def = nodeType.getRequirementDefinitions();
        if (Objects.nonNull(def)) {
            for (TRequirementDefinition requirementDef : def.getRequirementDefinition()) {
                String requirementId = templateName + "-" + requirementDef.getName();
                templateBuilder.addRequirements(new TRequirement.Builder(requirementId, requirementDef.getName(),
                    requirementDef.getRequirementType()).build());
            }
        }

        // add the DAs to the NodeTemplate
        if (Objects.nonNull(artifacts) && !artifacts.isEmpty()) {
            LOGGER.debug("{} artifacts specified for filter {}", artifacts.size(), templateName);
            List<TDeploymentArtifact> daList = new ArrayList<>();

            // get the IDs of all available ArtifactTemplates
            List<ArtifactTemplateId> artifactTemplateIds = repo.getAllDefinitionsChildIds().stream()
                .filter(id -> id.getGroup().equals(ARTIFACT_TEMPLATE_GROUP) && id instanceof ArtifactTemplateId)
                .map(id -> (ArtifactTemplateId) id).collect(Collectors.toList());

            for (QName artifactName : artifacts) {
                Optional<ArtifactTemplateId> idOptional = artifactTemplateIds.stream()
                    .filter(id -> id.getQName().equals(artifactName)).findFirst();
                if (idOptional.isPresent()) {
                    ArtifactTemplateId artifactTemplateId = idOptional.get();
                    TArtifactTemplate artifactTemplate = repo.getElement(artifactTemplateId);
                    daList.add(new TDeploymentArtifact.Builder(artifactName.toString(), artifactTemplate.getType())
                        .setArtifactRef(artifactName).build());
                } else {
                    LOGGER.warn("Filter '{}' specifies DA with name '{}' but no such artifact available in repository!",
                        templateName, artifactName);
                }
            }

            TDeploymentArtifacts das = new TDeploymentArtifacts.Builder(daList).build();
            templateBuilder.setDeploymentArtifacts(das);
        }

        topology.addNodeTemplate(templateBuilder.build());
        return topology;
    }

    private TRelationshipTemplate createRelationshipTemplate(RelationshipTypeId relationshipTypeId,
                                                             TNodeTemplate source, TNodeTemplate target,
                                                             String dataTransferType) {

        // get ConnectsTo RelationshipType to access available properties
        IRepository repo = RepositoryFactory.getRepository();
        TRelationshipType relationshipType = repo.getElement(relationshipTypeId);
        if (Objects.isNull(relationshipType)) {
            return null;
        }

        TRelationshipTemplate.SourceOrTargetElement sourceElement = new TRelationshipTemplate.SourceOrTargetElement();
        TRelationshipTemplate.SourceOrTargetElement targetElement = new TRelationshipTemplate.SourceOrTargetElement();
        String relationId = source.getId() + "-connectsTo-" + target.getId();

        // default connectsTo direction if no further information is provided
        sourceElement.setRef(source);
        targetElement.setRef(target);

        TRelationshipTemplate.Builder builder = new TRelationshipTemplate.Builder(relationId, relationshipTypeId.getQName(),
            sourceElement, targetElement);
        builder.setName(relationId);

        // add empty properties to avoid errors due to missing properties
        if (Objects.nonNull(relationshipType.getWinerysPropertiesDefinition())) {
            LinkedHashMap<String, String> propertyList = new LinkedHashMap<>();

            WinerysPropertiesDefinition def = relationshipType.getWinerysPropertiesDefinition();
            for (PropertyDefinitionKV prop : def.getPropertyDefinitions()) {
                propertyList.put(prop.getKey(), "");
            }

            TEntityTemplate.WineryKVProperties relationProperties = new TEntityTemplate.WineryKVProperties();
            relationProperties.setElementName(def.getElementName());
            relationProperties.setNamespace(def.getNamespace());
            relationProperties.setKVProperties(propertyList);
            builder.setProperties(relationProperties);
        }

        TRelationshipTemplate relation = builder.build();

        // evaluate pipe attribute to determine connectsTo direction
        if (Objects.nonNull(dataTransferType) && dataTransferType.equalsIgnoreCase(DATA_TRANSFER_TYPE_PULL)) {
            // change connectsTo direction in case the corresponding target filter is pulling
            relation.setSourceNodeTemplate(target);
            relation.setTargetNodeTemplate(source);
            relation.getOtherAttributes().put(ModelUtilities.RELATIONSHIP_TEMPLATE_TRANSFER_TYPE,
                RELATIONSHIP_TEMPLATE_TRANSFER_TYPE_PULL);
        }
        return relation;
    }
}
