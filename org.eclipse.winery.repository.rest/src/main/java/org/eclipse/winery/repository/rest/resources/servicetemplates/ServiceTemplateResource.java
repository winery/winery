/********************************************************************************
 * Copyright (c) 2012-2022 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.servicetemplates;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.configuration.Environments;
import org.eclipse.winery.common.configuration.RepositoryConfigurationObject;
import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.common.version.WineryVersion;
import org.eclipse.winery.compliance.checking.ServiceTemplateCheckingResult;
import org.eclipse.winery.compliance.checking.ServiceTemplateComplianceRuleRuleChecker;
import org.eclipse.winery.edmm.EdmmUtils;
import org.eclipse.winery.model.adaptation.substitution.Substitution;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.threatmodeling.ThreatAssessment;
import org.eclipse.winery.model.threatmodeling.ThreatModeling;
import org.eclipse.winery.model.tosca.HasId;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TCapabilityRef;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPropertyMapping;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TRequirementRef;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTag;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.Namespaces;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.extensions.OTParticipant;
import org.eclipse.winery.model.tosca.extensions.kvproperties.PropertyDefinitionKV;
import org.eclipse.winery.model.tosca.extensions.kvproperties.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.model.version.VersionSupport;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.NamespaceManager;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.YamlArtifactsSynchronizer;
import org.eclipse.winery.repository.driverspecificationandinjection.DASpecification;
import org.eclipse.winery.repository.driverspecificationandinjection.DriverInjection;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentInstanceResourceContainingATopology;
import org.eclipse.winery.repository.rest.resources._support.IHasName;
import org.eclipse.winery.repository.rest.resources._support.ResourceResult;
import org.eclipse.winery.repository.rest.resources._support.dataadapter.injectionadapter.InjectionSelectionData;
import org.eclipse.winery.repository.rest.resources._support.dataadapter.injectionadapter.InjectorReplaceOptions;
import org.eclipse.winery.repository.rest.resources._support.dataadapter.injectionadapter.NodeInjectionOptions;
import org.eclipse.winery.repository.rest.resources.apiData.QNameApiData;
import org.eclipse.winery.repository.rest.resources.edmm.EdmmResource;
import org.eclipse.winery.repository.rest.resources.researchObject.ResearchObjectResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.boundarydefinitions.BoundaryDefinitionsResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.plans.PlansResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.selfserviceportal.SelfServicePortalResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.topologytemplates.TopologyTemplateResource;
import org.eclipse.winery.repository.splitting.InjectRemoval;
import org.eclipse.winery.repository.splitting.Splitting;
import org.eclipse.winery.repository.splitting.SplittingException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.bouncycastle.math.raw.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceTemplateResource extends AbstractComponentInstanceResourceContainingATopology implements IHasName {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceTemplateResource.class);

    private static final QName QNAME_LOCATION = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "location");
    private static final QName QNAME_PARTICIPANT = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "participant");

    public ServiceTemplateResource(ServiceTemplateId id) {
        super(id);
    }

    public TServiceTemplate getServiceTemplate() {
        return (TServiceTemplate) this.getElement();
    }

    @Override
    public TTopologyTemplate getTopology() {
        return getServiceTemplate().getTopologyTemplate();
    }

    @Override
    public void setTopology(TTopologyTemplate topologyTemplate, String type) {
        // if we are in yaml mode, replacing the topology can result in yaml artifacts having to be deleted.
        if (Environments.getInstance().getRepositoryConfig().getProvider() == RepositoryConfigurationObject.RepositoryProvider.YAML) {
            try {
                YamlArtifactsSynchronizer synchronizer = new YamlArtifactsSynchronizer
                    .Builder(RepositoryFactory.getRepository())
                    .setOriginalTemplate(this.getServiceTemplate().getTopologyTemplate())
                    .setNewTemplate(topologyTemplate)
                    .setServiceTemplateId((ServiceTemplateId) this.getId())
                    .build();
                synchronizer.synchronizeNodeTemplates();
                synchronizer.synchronizeRelationshipTemplates();
            } catch (IOException e) {
                LOGGER.error("Failed to delete yaml artifact files from disk. Reason {}", e.getMessage());
            }
            if (topologyTemplate.getNodeTemplates().stream()
                .filter(nt -> nt.getRequirements() != null)
                .anyMatch(nt -> nt.getRequirements().stream().anyMatch(req -> req.getRelationship() != null))) {
                // filter unused requirements
                // (1) get a list of requirement template ids
                // (2) filter requirement entry on node template if there is relations assigned
                Set<String> usedRelationshipTemplateIds = topologyTemplate.getRelationshipTemplates()
                    .stream().map(HasId::getId).collect(Collectors.toSet());
                topologyTemplate.getNodeTemplates().stream()
                    .filter(node -> node.getRequirements() != null)
                    .forEach(node -> node.getRequirements()
                        .removeIf(r -> !usedRelationshipTemplateIds.contains(r.getRelationship()))
                    );
            }
        }
        this.getServiceTemplate().setTopologyTemplate(topologyTemplate);
        this.cullElementReferences();
    }

    private void cullElementReferences() {
        final TTopologyTemplate topology = this.getServiceTemplate().getTopologyTemplate();
        TBoundaryDefinitions boundaryDefs = this.getServiceTemplate().getBoundaryDefinitions();
        if (topology == null || boundaryDefs == null) {
            return;
        }
        if (boundaryDefs.getProperties() != null
            && boundaryDefs.getProperties().getPropertyMappings() != null) {
            for (Iterator<TPropertyMapping> it = boundaryDefs.getProperties().getPropertyMappings().iterator(); it.hasNext(); ) {
                TPropertyMapping propMapping = it.next();
                HasId targetObject = propMapping.getTargetObjectRef();
                if (!containsTarget(topology, targetObject)) {
                    // cull the property mapping pointing towards a no-longer existing element
                    it.remove();
                }
            }
        }
        if (boundaryDefs.getCapabilities() != null) {
            for (Iterator<TCapabilityRef> it = boundaryDefs.getCapabilities().iterator(); it.hasNext(); ) {
                TCapabilityRef ref = it.next();
                TCapability target = ref.getRef();
                if (!containsCapability(topology, target)) {
                    // cull the capability referencing a no longer existing capability in the topology
                    it.remove();
                }
            }
        }
        if (boundaryDefs.getRequirements() != null) {
            for (Iterator<TRequirementRef> it = boundaryDefs.getRequirements().iterator(); it.hasNext(); ) {
                TRequirementRef ref = it.next();
                TRequirement target = ref.getRef();
                if (!containsRequirement(topology, target)) {
                    // cull the requirement referencing a no longer existing requirement in the topology
                    it.remove();
                }
            }
        }
    }

    private static boolean containsTarget(TTopologyTemplate topology, HasId target) {
        return topology.getNodeTemplate(target.getId()) != null
            || topology.getRelationshipTemplate(target.getId()) != null;
    }

    private static boolean containsCapability(TTopologyTemplate topology, TCapability target) {
        return topology.getNodeTemplates().stream()
            .anyMatch(nt -> nt.getCapabilities() != null
                && nt.getCapabilities().contains(target));
    }

    private static boolean containsRequirement(TTopologyTemplate topology, TRequirement target) {
        return topology.getNodeTemplates().stream()
            .anyMatch(nt -> nt.getRequirements().contains(target));
    }

    /**
     * sub-resources
     **/
    @Path("topologytemplate/")
    @SuppressWarnings("deprecated")
    public TopologyTemplateResource getTopologyTemplateResource() {
        if (this.getServiceTemplate().getTopologyTemplate() == null) {
            // the main service template resource exists
            // default topology template: empty template
            // This eases the JSPs etc. and is valid as a non-existant topology template is equal to an empty one
            this.getServiceTemplate().setTopologyTemplate(new TTopologyTemplate());
        }
        return new TopologyTemplateResource(this, this.getServiceTemplate().getTopologyTemplate(), null);
    }

    @Path("plans/")
    public PlansResource getPlansResource() {
        List<TPlan> plans = this.getServiceTemplate().getPlans();
        if (plans == null) {
            plans = new ArrayList<>();
            this.getServiceTemplate().setPlans(plans);
        }
        return new PlansResource(plans, this);
    }

    @Path("selfserviceportal/")
    public SelfServicePortalResource getSelfServicePortalResource() {
        return new SelfServicePortalResource(this, requestRepository);
    }

    @Path("researchobject/")
    public ResearchObjectResource getResearchObjectResource() {
        return new ResearchObjectResource(this);
    }

    @Path("boundarydefinitions/")
    public BoundaryDefinitionsResource getBoundaryDefinitionsResource() {
        TBoundaryDefinitions boundaryDefinitions = this.getServiceTemplate().getBoundaryDefinitions();
        if (boundaryDefinitions == null) {
            boundaryDefinitions = new TBoundaryDefinitions.Builder().build();
            this.getServiceTemplate().setBoundaryDefinitions(boundaryDefinitions);
        }
        return new BoundaryDefinitionsResource(this, boundaryDefinitions);
    }

    @Override
    public String getName() {
        String name = this.getServiceTemplate().getName();
        if (name == null) {
            // place default
            name = this.getId().getXmlId().getDecoded();
        }
        return name;
    }

    @Override
    public Response setName(String name) {
        this.getServiceTemplate().setName(name);
        return RestUtils.persist(this);
    }

    @GET
    @ApiOperation(value = "Returns the associated node type, which can be substituted by this service template.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, response = QName.class, message = "QName of the form {namespace}localName")
    })
    @Path("substitutableNodeType")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getSubstitutableNodeTypeAsResponse() {
        QName qname = this.getServiceTemplate().getSubstitutableNodeType();
        if (qname == null) {
            return Response.status(Status.NOT_FOUND).build();
        } else {
            return Response.ok(qname.toString()).build();
        }
    }

    /**
     * @return null if there is no substitutable node type
     */
    public QName getSubstitutableNodeType() {
        return this.getServiceTemplate().getSubstitutableNodeType();
    }

    @DELETE
    @ApiOperation(value = "Removes the association to substitutable node type")
    @Path("substitutableNodeType")
    public Response deleteSubstitutableNodeType() {
        this.getServiceTemplate().setSubstitutableNodeType(null);
        RestUtils.persist(this);
        return Response.noContent().build();
    }

    @GET
    @Path("injector/options")
    @Produces( {MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response getInjectorOptions() {
        Splitting splitting = new Splitting();
        TTopologyTemplate topologyTemplate = this.getServiceTemplate().getTopologyTemplate();
        Map<String, List<TServiceTemplate>> hostMatchingOptions = new HashMap<>();
        Map<String, List<TServiceTemplate>> connectionMatchingOptions = new HashMap<>();
        InjectorReplaceOptions injectorReplaceOptions = new InjectorReplaceOptions();

        try {
            if (splitting.checkValidTopology(this.getServiceTemplate())) {
                hostMatchingOptions = splitting.getHostingMatchingOptionsWithDefaultLabeling(this.getServiceTemplate());
            } else {
                Map<TRequirement, String> requirementsAndMatchingBasisCapabilityTypes =
                    splitting.getOpenRequirementsAndMatchingBasisCapabilityTypeNames(this.getServiceTemplate());
                // Output check
                if (requirementsAndMatchingBasisCapabilityTypes != null) {
                    for (TRequirement req : requirementsAndMatchingBasisCapabilityTypes.keySet()) {
                        System.out.println("open Requirement: " + req.getId());
                        System.out.println("matchingBasisType: " + requirementsAndMatchingBasisCapabilityTypes.get(req));
                    }

                    if (requirementsAndMatchingBasisCapabilityTypes.containsValue("Container")) {
                        hostMatchingOptions = splitting.getHostingMatchingOptionsWithDefaultLabeling(this.getServiceTemplate());
                    } else {
                        hostMatchingOptions = null;
                    }
                    if (requirementsAndMatchingBasisCapabilityTypes.containsValue("Endpoint")) {
                        connectionMatchingOptions = splitting.getConnectionInjectionOptions(this.getServiceTemplate());
                    } else {
                        connectionMatchingOptions = null;
                    }
                }
            }

            List<NodeInjectionOptions> hostInjectionOptions = new ArrayList<>();
            if (hostMatchingOptions != null) {
                hostMatchingOptions.forEach((key, value) -> {
                    NodeInjectionOptions options = new NodeInjectionOptions();
                    options.setNodeID(key);
                    value
                        .forEach(st -> {
                            QName stQName = new QName(st.getTargetNamespace(), st.getId());
                            options.addInjectionOption(stQName);
                        });
                    hostInjectionOptions.add(options);
                });
            }
            List<NodeInjectionOptions> connectionInjectionOptions = new ArrayList<>();
            if (connectionMatchingOptions != null) {
                connectionMatchingOptions.forEach((key, value) -> {
                    NodeInjectionOptions options = new NodeInjectionOptions();
                    options.setNodeID(key);
                    value.forEach(st -> {
                        QName stQName = new QName(st.getTargetNamespace(), st.getId());
                        options.addInjectionOption(stQName);
                    });
                    connectionInjectionOptions.add(options);
                });
            }
            injectorReplaceOptions.setHostInjectionOptions(hostInjectionOptions);
            injectorReplaceOptions.setConnectionInjectionOptions(connectionInjectionOptions);

            if (hostMatchingOptions == null && connectionMatchingOptions == null) {
                return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("No need for matching").build();
            }
        } catch (SplittingException e) {
            LOGGER.error("Could not match", e);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
        return Response.ok().entity(injectorReplaceOptions).build();
    }

    @POST
    @Path("placeholder/generator")
    @Consumes( {MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    @Produces( {MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response generatePlaceholdersWithCapability() {
        Splitting splitting = new Splitting();
        TTopologyTemplate topologyTemplate = this.getServiceTemplate().getTopologyTemplate();
        if (topologyTemplate == null) {
            return Response.notModified().build();
        }

        try {
            // get all open requirements and the respective node templates with open requirements
            Map<TRequirement, TNodeTemplate> requirementsAndItsNodeTemplates =
                splitting.getOpenRequirementsAndItsNodeTemplate(topologyTemplate);
            IRepository repo = RepositoryFactory.getRepository();

            // iterate over all open requirements
            for (Map.Entry<TRequirement, TNodeTemplate> entry : requirementsAndItsNodeTemplates.entrySet()) {
                List<PropertyDefinitionKV> propertyDefinitionKVList = new ArrayList<>();
                LinkedHashMap<String, String> placeholderNodeTemplateProperties = new LinkedHashMap<>();
                // current node template with open requirements
                TNodeTemplate nodeTemplateWithOpenReq = entry.getValue();
                // get type of node template with open requirements
                NodeTypeId id = new NodeTypeId(nodeTemplateWithOpenReq.getType());
                TNodeType elementNodeType = repo.getElement(id);

                List<TInterface> allInterfaces = elementNodeType.getInterfaces();
                if (allInterfaces != null && allInterfaces.isEmpty()) {
                    List<TInterface> sourceNodeTypeInterfaces = allInterfaces.stream().filter(tInterface -> !tInterface.getIdFromIdOrNameField().contains("connect")).collect(Collectors.toList());
                    if (sourceNodeTypeInterfaces != null) {
                        for (TInterface tInterface : sourceNodeTypeInterfaces) {
                            // TODO: make this more safe
                            for (TOperation tOperation : tInterface.getOperations()) {
                                List<TParameter> inputParameters = tOperation.getInputParameters();
                                if (inputParameters != null) {
                                    for (TParameter inputParameter : inputParameters) {
                                        generateInputParameters(propertyDefinitionKVList, placeholderNodeTemplateProperties, elementNodeType, inputParameter);
                                    }
                                }
                            }
                        }
                    }
                }

                List<TRelationshipTemplate> incomingRelationshipTemplates = ModelUtilities.getIncomingRelationshipTemplates(topologyTemplate, nodeTemplateWithOpenReq);
                for (TRelationshipTemplate incomingRelation : incomingRelationshipTemplates) {
                    List<TParameter> inputParameters = splitting.getInputParamListofIncomingRelationshipTemplate(topologyTemplate, incomingRelation);
                    for (TParameter inputParameter : inputParameters) {
                        String prefixTARGET = "TARGET_";
                        String prefixSOURCE = "SOURCE_";
                        String inputParamName = inputParameter.getName();
                        if (inputParamName.contains(prefixTARGET)) {
                            inputParamName = inputParamName.replaceAll(prefixTARGET, "");
                        }
                        if (inputParamName.contains(prefixSOURCE)) {
                            inputParamName = inputParamName.replaceAll(prefixSOURCE, "");
                        }
                        inputParameter.setName(inputParamName);
                        
                        TNodeTemplate relationSourceTemplate = ModelUtilities.getSourceNodeTemplateOfRelationshipTemplate(topologyTemplate, incomingRelation);
                        Map<String, String> relationSourceProperties = new HashMap<>();
                        if (relationSourceTemplate.getProperties() != null) {
                            relationSourceProperties.putAll(ModelUtilities.getPropertiesKV(relationSourceTemplate));
                        }
                        if(incomingRelation.getProperties()!= null) {
                            relationSourceProperties.putAll(ModelUtilities.getPropertiesKV(incomingRelation));
                        }
                        TNodeTemplate relationTargetTemplate = ModelUtilities.getTargetNodeTemplateOfRelationshipTemplate(topologyTemplate, incomingRelation);
                        Map<String, String> relationTargetTProperties = ModelUtilities.getPropertiesKV(relationTargetTemplate);
                        generateInputParametersForIncomingRelations(propertyDefinitionKVList, placeholderNodeTemplateProperties, relationSourceProperties, relationTargetTProperties, inputParameter);
                    }
                }

                // get required capability type of open requirement
                QName capabilityType = splitting.getRequiredCapabilityTypeQNameOfRequirement(entry.getKey());

                // create new placeholder node type
                TNodeType placeholderNodeType = splitting.createPlaceholderNodeType(nodeTemplateWithOpenReq.getName());
                QName placeholderQName = new QName(placeholderNodeType.getTargetNamespace(), placeholderNodeType.getName());

                WinerysPropertiesDefinition winerysPropertiesDefinition = elementNodeType.getWinerysPropertiesDefinition();
                if (Objects.isNull(winerysPropertiesDefinition)) {
                    winerysPropertiesDefinition = new WinerysPropertiesDefinition();
                }
                // add properties definition
                winerysPropertiesDefinition.setPropertyDefinitions(propertyDefinitionKVList);
                if (!winerysPropertiesDefinition.getPropertyDefinitions().isEmpty()) {

                    placeholderNodeType.setProperties(null);
                    placeholderNodeType.setProperties(winerysPropertiesDefinition);
                    String namespace = placeholderNodeType.getWinerysPropertiesDefinition().getNamespace();
                    NamespaceManager namespaceManager = RepositoryFactory.getRepository().getNamespaceManager();
                    if (!namespaceManager.hasPermanentProperties(namespace)) {
                        namespaceManager.addPermanentNamespace(namespace);
                    }
                }

                NodeTypeId placeholderId = new NodeTypeId(placeholderQName);
                // check if placeholder node type exists
                if (repo.exists(placeholderId)) {
                    // delete and create new
                    RestUtils.delete(placeholderId);
                }
                repo.setElement(placeholderId, placeholderNodeType);

                // create placeholder node template
                TNodeTemplate placeholderNodeTemplate = splitting.createPlaceholderNodeTemplate(topologyTemplate, nodeTemplateWithOpenReq, placeholderQName);

                // create capability of placeholder node template
                TCapability capa = splitting.createPlaceholderCapability(topologyTemplate, capabilityType);

                if (!placeholderNodeTemplateProperties.isEmpty()) {
                    ModelUtilities.setPropertiesKV(placeholderNodeTemplate, placeholderNodeTemplateProperties);
                }

                if (placeholderNodeTemplate.getCapabilities() == null) {
                    placeholderNodeTemplate.setCapabilities(new ArrayList<>());
                }
                placeholderNodeTemplate.getCapabilities().add(capa);

                for (Map.Entry<QName, String> targetLocation : nodeTemplateWithOpenReq.getOtherAttributes().entrySet()) {
                    placeholderNodeTemplate.getOtherAttributes().put(targetLocation.getKey(), targetLocation.getValue());
                }

                //Set new coordinates
                int y = Integer.parseInt(placeholderNodeTemplate.getY()) + 200;
                placeholderNodeTemplate.setY(Integer.toString(y));
                placeholderNodeTemplate.setX(placeholderNodeTemplate.getX());
                // add placeholder to node template and connect with source node template with open requirements
                topologyTemplate.addNodeTemplate(placeholderNodeTemplate);
                ModelUtilities.createRelationshipTemplateAndAddToTopology(nodeTemplateWithOpenReq, placeholderNodeTemplate, ToscaBaseTypes.hostedOnRelationshipType, topologyTemplate);
            }
            LOGGER.debug("PERSISTING");
            RestUtils.persist(this);
            LOGGER.debug("PERSISTED");
            String responseId = this.getServiceTemplate().getId();
            return Response.ok().entity(responseId).build();
        } catch (
            Exception e) {
            LOGGER.error("Could not fetch requirements and capabilities", e);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    private void generateInputParameters(List<PropertyDefinitionKV> propertyDefinitionKVList, LinkedHashMap<String, String> placeholderNodeTemplateProperties, TNodeType sourceNodeType, TParameter inputParameter) {
        PropertyDefinitionKV inputParamKV = new PropertyDefinitionKV(inputParameter.getName(), inputParameter.getType());
        if (sourceNodeType.getWinerysPropertiesDefinition() != null &&
            !sourceNodeType.getWinerysPropertiesDefinition().getPropertyDefinitions().stream().anyMatch(p -> p.getKey().equals(inputParameter.getName()))
            && !propertyDefinitionKVList.contains(inputParamKV)) {
            propertyDefinitionKVList.add(inputParamKV);
            placeholderNodeTemplateProperties.put(inputParameter.getName(), "get_input: " + inputParameter.getName());
        } else if (sourceNodeType.getWinerysPropertiesDefinition() == null && !propertyDefinitionKVList.contains(inputParamKV)) {
            propertyDefinitionKVList.add(inputParamKV);
            placeholderNodeTemplateProperties.put(inputParameter.getName(), "get_input: " + inputParameter.getName());
        }
    }

    private void generateInputParametersForIncomingRelations(List<PropertyDefinitionKV> propertyDefinitionKVList, LinkedHashMap<String, String> placeholderNodeTemplateProperties, 
                                                             Map<String, String> sourceTemplateProperties,
                                                             Map<String, String> targetTemplateProperties, TParameter inputParameter) {
        PropertyDefinitionKV inputParamKV = new PropertyDefinitionKV(inputParameter.getName(), inputParameter.getType());
        if (!propertyDefinitionKVList.contains(inputParamKV) && ((sourceTemplateProperties != null &&
            !sourceTemplateProperties.keySet().stream().anyMatch(p -> p.equals(inputParameter.getName()))) ||
            sourceTemplateProperties == null)) {
            if ((targetTemplateProperties != null &&
                !targetTemplateProperties.keySet().stream().anyMatch(p -> p.equals(inputParameter.getName()))) ||
                targetTemplateProperties == null) {
                propertyDefinitionKVList.add(inputParamKV);
                placeholderNodeTemplateProperties.put(inputParameter.getName(), "get_input: " + inputParameter.getName());
            }
        }
    }

    @POST
    @Path("injector/replace")
    @Consumes( {MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    @Produces( {MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response injectNodeTemplates(InjectionSelectionData injectionSelectionData, @Context UriInfo uriInfo) throws
        Exception {

        Map<String, TTopologyTemplate> hostInjectionSelections = new HashMap<>();
        Map<String, TTopologyTemplate> connectionInjectionSelections = new HashMap<>();

        if (injectionSelectionData.hostInjections != null) {
            hostInjectionSelections = injectionSelectionData.hostInjections.stream()
                .map(entry -> {
                    ServiceTemplateId id = new ServiceTemplateId(entry.getInjection());
                    TServiceTemplate serviceTemplate = RepositoryFactory.getRepository().getElement(id);
                    Map.Entry<String, TTopologyTemplate> newEntry = new AbstractMap.SimpleEntry(entry.getNodeID(), serviceTemplate.getTopologyTemplate());
                    return newEntry;
                }).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

            Collection<TTopologyTemplate> hostInjectorTopologyTemplates = hostInjectionSelections.values();
            hostInjectorTopologyTemplates.forEach(t -> {
                try {
                    ModelUtilities.patchAnyAttributes(t.getNodeTemplates());
                } catch (IOException e) {
                    LOGGER.error("XML was invalid", e);
                }
            });
        }
        if (injectionSelectionData.connectionInjections != null) {
            connectionInjectionSelections = injectionSelectionData.connectionInjections.stream()
                .map(entry -> {
                    ServiceTemplateId id = new ServiceTemplateId(entry.getInjection());
                    TServiceTemplate serviceTemplate = RepositoryFactory.getRepository().getElement(id);
                    Map.Entry<String, TTopologyTemplate> newEntry = new AbstractMap.SimpleEntry(entry.getNodeID(), serviceTemplate.getTopologyTemplate());
                    return newEntry;
                }).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
            Collection<TTopologyTemplate> connectionInjectorTopologyTemplates = connectionInjectionSelections.values();
            connectionInjectorTopologyTemplates.forEach(t -> {
                try {
                    ModelUtilities.patchAnyAttributes(t.getNodeTemplates());
                } catch (IOException e) {
                    LOGGER.error("XML was invalid", e);
                }
            });
        }

        Splitting splitting = new Splitting();
        TTopologyTemplate matchedHostsTopologyTemplate;
        TTopologyTemplate matchedConnectedTopologyTemplate;

        if (hostInjectionSelections != null && !hostInjectionSelections.isEmpty()) {
            matchedHostsTopologyTemplate = splitting.injectNodeTemplates(this.getServiceTemplate().getTopologyTemplate(), hostInjectionSelections, InjectRemoval.REMOVE_REPLACED_AND_SUCCESSORS);

            if (connectionInjectionSelections != null && !connectionInjectionSelections.isEmpty()) {
                matchedConnectedTopologyTemplate = splitting.injectConnectionNodeTemplates(matchedHostsTopologyTemplate, connectionInjectionSelections);
            } else {
                matchedConnectedTopologyTemplate = matchedHostsTopologyTemplate;
            }
        } else if (connectionInjectionSelections != null && !connectionInjectionSelections.isEmpty()) {
            matchedConnectedTopologyTemplate = splitting.injectConnectionNodeTemplates(this.getServiceTemplate().getTopologyTemplate(), connectionInjectionSelections);
        } else {
            throw new SplittingException("No open Requirements which can be matched");
        }

        TTopologyTemplate daSpecifiedTopology = matchedConnectedTopologyTemplate;

        //Start additional functionality Driver Injection
        if (!DASpecification.getNodeTemplatesWithAbstractDAs(matchedConnectedTopologyTemplate).isEmpty() &&
            DASpecification.getNodeTemplatesWithAbstractDAs(matchedConnectedTopologyTemplate) != null) {
            daSpecifiedTopology = DriverInjection.injectDriver(matchedConnectedTopologyTemplate);
        }
        //End additional functionality Driver Injection

        ServiceTemplateId matchedServiceTemplateId = new ServiceTemplateId(
            id.getNamespace().getDecoded(),
            VersionSupport.getNewComponentVersionId(id, "matched"),
            false);
        IRepository repository = RepositoryFactory.getRepository();
        repository.forceDelete(matchedServiceTemplateId);
        repository.flagAsExisting(matchedServiceTemplateId);
        TServiceTemplate matchedServiceTemplate = new TServiceTemplate();
        matchedServiceTemplate.setName(matchedServiceTemplateId.getXmlId().getDecoded());
        matchedServiceTemplate.setId(matchedServiceTemplate.getName());
        matchedServiceTemplate.setTargetNamespace(id.getNamespace().getDecoded());
        matchedServiceTemplate.setTopologyTemplate(daSpecifiedTopology);
        matchedServiceTemplate.setTags(this.getServiceTemplate().getTags());
        LOGGER.debug("Persisting...");
        repository.setElement(matchedServiceTemplateId, matchedServiceTemplate);
        LOGGER.debug("Persisted.");

        //No renaming of the Service Template allowed because of the plans

        URI url = uriInfo.getBaseUri().resolve(RestUtils.getAbsoluteURL(id));
        ResourceResult result = new ResourceResult();
        result.setStatus(Response.Status.CREATED);
        result.setMessage(new QNameApiData(matchedServiceTemplateId));

        return result.getResponse();
    }

    @Path("constraintchecking")
    @Produces(MediaType.APPLICATION_XML)
    @POST
    public Response complianceChecking(@Context UriInfo uriInfo) throws JAXBException {
        ServiceTemplateComplianceRuleRuleChecker checker = new ServiceTemplateComplianceRuleRuleChecker(this.getServiceTemplate());
        ServiceTemplateCheckingResult serviceTemplateCheckingResult = checker.checkComplianceRules();
        return Response.ok().entity(serviceTemplateCheckingResult.toXMLString()).build();
    }

    @Path("substitute")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public ServiceTemplateId substitute() {
        Substitution substitution = new Substitution();
        return substitution.substituteTopologyOfServiceTemplate((ServiceTemplateId) this.id);
    }

    @Path("placeholdersubstitution")
    @POST()
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createPlaceholderSubstituteVersion() throws IOException, SplittingException {
        TTopologyTemplate originTopologyTemplate = this.getServiceTemplate().getTopologyTemplate();
        if (originTopologyTemplate == null) {
            return Response.notModified().build();
        }

        List<TTag> tagsOfServiceTemplate = this.getServiceTemplate().getTags();
        List<OTParticipant> participants = originTopologyTemplate.getParticipants();

        String participantId = "";
        List<TTag> newTagList = new ArrayList<>();
        for (TTag tagOfServiceTemplate : tagsOfServiceTemplate) {
            if (tagOfServiceTemplate.getName().equals("participant")) {
                participantId = tagOfServiceTemplate.getValue();
                newTagList.add(tagOfServiceTemplate);
            } else if (!tagOfServiceTemplate.getName().equals("choreography")) {
                newTagList.add(tagOfServiceTemplate);
            }
        }
        final String finalParticipantId = participantId;

        List<String> nodeTemplatesWithNewHost = new ArrayList<>();

        for (TNodeTemplate tNodeTemplate : originTopologyTemplate.getNodeTemplates()) {
            //Multiple participants can be annotated on one node template
            Optional<String> nodeOwners = ModelUtilities.getParticipant(tNodeTemplate);
            if (nodeOwners.isPresent() && nodeOwners.get().contains(finalParticipantId)) {
                for (TRelationshipTemplate tRelationshipTemplate :
                    ModelUtilities.getIncomingRelationshipTemplates(originTopologyTemplate, tNodeTemplate)) {
                    nodeTemplatesWithNewHost.add(
                        ModelUtilities.getSourceNodeTemplateOfRelationshipTemplate(originTopologyTemplate, tRelationshipTemplate)
                            .getId()
                    );
                }
            }
        }

        ServiceTemplateId id = (ServiceTemplateId) this.getId();
        WineryVersion version = VersionUtils.getVersion(id.getXmlId().getDecoded());

        WineryVersion newVersion = new WineryVersion(
            "_substituted_" + version.toString(),
            1,
            1
        );

        ServiceTemplateId newId = new ServiceTemplateId(id.getNamespace().getDecoded(),
            VersionUtils.getNameWithoutVersion(id.getXmlId().getDecoded()) + WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR + newVersion.toString(),
            false);

        IRepository repo = RepositoryFactory.getRepository();
        if (repo.exists(newId)) {
            repo.forceDelete(newId);
        }

        ResourceResult response = RestUtils.duplicate(id, newId);

        TServiceTemplate newServiceTemplate = repo.getElement(newId);
        newServiceTemplate.setTopologyTemplate(BackendUtils.clone(originTopologyTemplate));
        newServiceTemplate.getTopologyTemplate().setParticipants(participants);

        Splitting splitting = new Splitting();

        Map<String, List<TServiceTemplate>> resultList = splitting.getHostingInjectionOptions(BackendUtils.clone(newServiceTemplate));
        for (Map.Entry<String, List<TServiceTemplate>> entry : resultList.entrySet()) {
            Optional<String> nodeOwners = ModelUtilities.getParticipant(newServiceTemplate.getTopologyTemplate().getNodeTemplate(entry.getKey()));
            if (nodeOwners.isPresent() && nodeOwners.get().contains(finalParticipantId)) {
                if (nodeTemplatesWithNewHost.contains(entry.getKey()) && !resultList.get(entry.getKey()).isEmpty()) {
                    Map<String, TTopologyTemplate> choiceTopologyTemplate = new LinkedHashMap<>();
                    choiceTopologyTemplate.put(entry.getKey(), entry.getValue().get(0).getTopologyTemplate());
                    splitting.injectNodeTemplates(newServiceTemplate.getTopologyTemplate(), choiceTopologyTemplate, InjectRemoval.REMOVE_REPLACED);
                    for (TNodeTemplate injectNodeTemplate : choiceTopologyTemplate.get(entry.getKey()).getNodeTemplates()) {
                        injectNodeTemplate.getOtherAttributes().put(QNAME_PARTICIPANT, finalParticipantId);
                    }
                }
            }
        }

        String choreoValue = splitting.calculateChoreographyTag(newServiceTemplate.getTopologyTemplate().getNodeTemplates(), participantId);

        TTag choreoTag = new TTag();
        choreoTag.setName("choreography");
        choreoTag.setValue(choreoValue);

        newTagList.add(choreoTag);
        newServiceTemplate.setTags(newTagList);

        repo.setElement(newId, newServiceTemplate);

        if (response.getStatus() == Status.CREATED) {
            response.setUri(null);
            response.setMessage(new QNameApiData(newId));
        }

        return response.getResponse();
    }

    @POST()
    @Path("createparticipantsversion")
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createParticipantsVersion() {
        IRepository repo = RepositoryFactory.getRepository();
        ResourceResult result = new ResourceResult();
        // create list of responses because we create multiple resources at once
        List<Response> listOfResponses = new ArrayList<>();

        LOGGER.debug("Creating new participants version of Service Template {}...", this.getId());

        ServiceTemplateId id = (ServiceTemplateId) this.getId();
        WineryVersion version = VersionUtils.getVersion(id.getXmlId().getDecoded());
        TTopologyTemplate topologyTemplate = this.getTopology();

        List<TTag> tags = new ArrayList<>();

        //Try Driver Injection
        TTopologyTemplate daSpecifiedTopology = this.getTopology();
        if (!DASpecification.getNodeTemplatesWithAbstractDAs(topologyTemplate).isEmpty() &&
            DASpecification.getNodeTemplatesWithAbstractDAs(topologyTemplate) != null) {
            try {
                daSpecifiedTopology = DriverInjection.injectDriver(topologyTemplate);
                this.getServiceTemplate().setTopologyTemplate(daSpecifiedTopology);
                topologyTemplate = this.getTopology();
                RestUtils.persist(this);
            } catch (Exception e) {
                e.printStackTrace();
                result.setStatus(Status.BAD_REQUEST);
                result.setMessage(e.getMessage());
                return result.getResponse();
            }
        }
        //End Driver Injection

        Splitting splitting = new Splitting();
        // iterate over tags of origin service template

        if (topologyTemplate.getParticipants() != null) {
            for (OTParticipant participant : topologyTemplate.getParticipants()) {
                // check if tag with partner in service template
                WineryVersion newVersion = new WineryVersion(
                    participant.getName() + "-" + version.toString().replace("gdm", "ldm"),
                    1,
                    1
                );

                List<OTParticipant> newParticipantList = new ArrayList<>(topologyTemplate.getParticipants());

                // new tag to define participant of service template
                tags.add(
                    new TTag.Builder("participant", participant.getName())
                        .build()
                );

                String choreoValue = splitting.calculateChoreographyTag(this.getServiceTemplate().getTopologyTemplate().getNodeTemplates(), participant.getName());
                tags.add(
                    new TTag.Builder("choreography", choreoValue)
                        .build()
                );

                ServiceTemplateId newId = new ServiceTemplateId(id.getNamespace().getDecoded(),
                    VersionUtils.getNameWithoutVersion(id.getXmlId().getDecoded()) + WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR + newVersion.toString(),
                    false);

                if (repo.exists(newId)) {
                    try {
                        repo.forceDelete(newId);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                ResourceResult response = RestUtils.duplicate(id, newId);

                if (response.getStatus() == Status.CREATED) {
                    response.setUri(null);
                    response.setMessage(new QNameApiData(newId));
                }

                TServiceTemplate tempServiceTempl = repo.getElement(newId);
                tempServiceTempl.setTags(tags);
                tempServiceTempl.getTopologyTemplate().setParticipants(newParticipantList);

                listOfResponses.add(response.getResponse());
                // set element to propagate changed tags
                try {
                    repo.setElement(newId, tempServiceTempl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                tags.clear();
            }
        }
        result.setStatus(Status.CREATED);
        result.setMessage(listOfResponses);
        return result.getResponse();
    }

    @POST()
    @Path("createplaceholderversion")
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createNewPlaceholderVersion() throws IOException {
        LOGGER.debug("Creating new placeholder version of Service Template {}...", this.getId());
        ServiceTemplateId id = (ServiceTemplateId) this.getId();
        WineryVersion version = VersionUtils.getVersion(id.getXmlId().getDecoded());

        WineryVersion newVersion = new WineryVersion(
            "gdm-" + version.toString(),
            1,
            1
        );

        IRepository repository = RepositoryFactory.getRepository();

        ServiceTemplateId newId = new ServiceTemplateId(id.getNamespace().getDecoded(),
            VersionUtils.getNameWithoutVersion(id.getXmlId().getDecoded()) + WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR + newVersion.toString(),
            false);

        if (repository.exists(newId)) {
            repository.forceDelete(newId);
        }

        ResourceResult response = RestUtils.duplicate(id, newId);

        if (response.getStatus() == Status.CREATED) {
            response.setUri(null);
            response.setMessage(new QNameApiData(newId));
        }

        LOGGER.debug("Created Service Template {}", newId.getQName());

        return response.getResponse();
    }

    @POST()
    @Path("createnewstatefulversion")
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createNewStatefulVersion() {
        LOGGER.debug("Creating new stateful version of Service Template {}...", this.getId());
        ServiceTemplateId id = (ServiceTemplateId) this.getId();
        WineryVersion version = id.getVersion();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
        WineryVersion newVersion = new WineryVersion(
            "stateful-" + version.toString() + "-" + dateFormat.format(new Date()),
            1,
            0
        );

        ServiceTemplateId newId = new ServiceTemplateId(id.getNamespace().getDecoded(),
            id.getNameWithoutVersion() + WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR + newVersion.toString(),
            false);
        ResourceResult response = RestUtils.duplicate(id, newId);

        if (response.getStatus() == Status.CREATED) {
            response.setUri(null);
            response.setMessage(new QNameApiData(newId));
        }

        LOGGER.debug("Created Service Template {}", newId.getQName());

        return response.getResponse();
    }

    @POST()
    @Path("createlivemodelingversion")
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createLiveModelingVersion() {
        LOGGER.debug("Creating live modeling version of Service Template {}...", this.getId().getQName());
        ServiceTemplateId id = (ServiceTemplateId) this.getId();
        WineryVersion version = VersionUtils.getVersion(id.getQName().toString());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
        WineryVersion newVersion = new WineryVersion(
            version.toString() + "-live-" + dateFormat.format(new Date()),
            1,
            0
        );

        String newComponentVersionId = VersionSupport.getNewComponentVersionId(id, "live-" + dateFormat.format(new Date()));

        ServiceTemplateId newId = new ServiceTemplateId(id.getNamespace().getDecoded(),
            newComponentVersionId,
            false);
        ResourceResult response = RestUtils.duplicate(id, newId);

        if (response.getStatus() == Status.CREATED) {
            response.setUri(null);
            response.setMessage(new QNameApiData(newId));
        }

        LOGGER.debug("Created Service Template {}", newId.getQName());

        return response.getResponse();
    }

    @Path("threatmodeling")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public ThreatAssessment threatModeling() {
        ThreatModeling threatModeling = new ThreatModeling((ServiceTemplateId) this.id);
        return threatModeling.getServiceTemplateThreats();
    }

    @Override
    protected TExtensibleElements createNewElement() {
        return new TServiceTemplate();
    }

    @Override
    public void synchronizeReferences() throws IOException {
        BackendUtils.synchronizeReferences((ServiceTemplateId) this.id, RepositoryFactory.getRepository());
    }

    @Path("parameters")
    @SuppressWarnings("deprecated")
    public ParameterResource getParameterResource() {
        if (this.getServiceTemplate().getTopologyTemplate() == null) {
            this.getServiceTemplate().setTopologyTemplate(new TTopologyTemplate());
        }
        return new ParameterResource(this, this.getServiceTemplate().getTopologyTemplate());
    }

    @Path("toscalight")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Map<String, Object> getToscaLightCompatibility() {
        return EdmmUtils.checkToscaLightCompatibility(this.getServiceTemplate());
    }

    @Path("edmm")
    public EdmmResource edmmResource() {
        return new EdmmResource(this.getServiceTemplate());
    }
}
