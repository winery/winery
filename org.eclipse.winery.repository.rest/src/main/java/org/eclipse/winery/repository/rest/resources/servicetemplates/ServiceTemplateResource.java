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
package org.eclipse.winery.repository.rest.resources.servicetemplates;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.eclipse.winery.model.tosca.TInterfaces;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate.Capabilities;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TPlans;
import org.eclipse.winery.model.tosca.TPropertyMapping;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TRequirementRef;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTag;
import org.eclipse.winery.model.tosca.TTags;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.Namespaces;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.extensions.OTParticipant;
import org.eclipse.winery.model.tosca.extensions.kvproperties.PropertyDefinitionKV;
import org.eclipse.winery.model.tosca.extensions.kvproperties.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.NamespaceManager;
import org.eclipse.winery.repository.backend.YamlArtifactsSynchronizer;
import org.eclipse.winery.repository.driverspecificationandinjection.DASpecification;
import org.eclipse.winery.repository.driverspecificationandinjection.DriverInjection;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentInstanceResourceContainingATopology;
import org.eclipse.winery.repository.rest.resources._support.IHasName;
import org.eclipse.winery.repository.rest.resources._support.ResourceResult;
import org.eclipse.winery.repository.rest.resources._support.dataadapter.injectionadapter.InjectorReplaceData;
import org.eclipse.winery.repository.rest.resources._support.dataadapter.injectionadapter.InjectorReplaceOptions;
import org.eclipse.winery.repository.rest.resources.apiData.QNameApiData;
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
            if (topologyTemplate.getNodeTemplates().stream().anyMatch(nt -> nt.getRequirements() != null
                && nt.getRequirements().getRequirement().stream().anyMatch(req -> req.getRelationship() != null))) {
                // filter unused requirements
                // (1) get a list of requirement template ids
                // (2) filter requirement entry on node template if there is relations assigned
                Set<String> usedRelationshipTemplateIds = topologyTemplate.getRelationshipTemplates()
                    .stream().map(HasId::getId).collect(Collectors.toSet());
                topologyTemplate.getNodeTemplates().forEach(node -> {
                    if (node.getRequirements() == null) return;
                    node.getRequirements().getRequirement()
                        .removeIf(r -> !usedRelationshipTemplateIds.contains(r.getRelationship()));
                });
            }
        }
        this.getServiceTemplate().setTopologyTemplate(topologyTemplate);
        this.cullElementReferences();
    }

    private void cullElementReferences() {
        final TTopologyTemplate topology = this.getServiceTemplate().getTopologyTemplate();
        TBoundaryDefinitions boundaryDefs = this.getServiceTemplate().getBoundaryDefinitions();
        if (boundaryDefs == null) {
            return;
        }
        if (boundaryDefs.getProperties() != null
            && boundaryDefs.getProperties().getPropertyMappings() != null) {
            for (Iterator<TPropertyMapping> it = boundaryDefs.getProperties().getPropertyMappings().getPropertyMapping().iterator(); it.hasNext(); ) {
                TPropertyMapping propMapping = it.next();
                HasId targetObject = propMapping.getTargetObjectRef();
                if (!containsTarget(topology, targetObject)) {
                    // cull the property mapping pointing towards a no-longer existing element
                    it.remove();
                }
            }
        }
        if (boundaryDefs.getCapabilities() != null) {
            for (Iterator<TCapabilityRef> it = boundaryDefs.getCapabilities().getCapability().iterator(); it.hasNext(); ) {
                TCapabilityRef ref = it.next();
                TCapability target = ref.getRef();
                if (!containsCapability(topology, target)) {
                    // cull the capability referencing a no longer existing capability in the topology
                    it.remove();
                }
            }
        }
        if (boundaryDefs.getRequirements() != null) {
            for (Iterator<TRequirementRef> it = boundaryDefs.getRequirements().getRequirement().iterator(); it.hasNext(); ) {
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
                && nt.getCapabilities().getCapability().contains(target));
    }

    private static boolean containsRequirement(TTopologyTemplate topology, TRequirement target) {
        return topology.getNodeTemplates().stream()
            .anyMatch(nt -> nt.getRequirements() != null
                && nt.getRequirements().getRequirement().contains(target));
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
        TPlans plans = this.getServiceTemplate().getPlans();
        if (plans == null) {
            plans = new TPlans();
            this.getServiceTemplate().setPlans(plans);
        }
        return new PlansResource(plans.getPlan(), this);
    }

    @Path("selfserviceportal/")
    public SelfServicePortalResource getSelfServicePortalResource() {
        return new SelfServicePortalResource(this, requestRepository);
    }

    @Path("boundarydefinitions/")
    public BoundaryDefinitionsResource getBoundaryDefinitionsResource() {
        TBoundaryDefinitions boundaryDefinitions = this.getServiceTemplate().getBoundaryDefinitions();
        if (boundaryDefinitions == null) {
            boundaryDefinitions = new TBoundaryDefinitions();
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
        Map<String, List<TTopologyTemplate>> hostMatchingOptions;
        Map<String, List<TTopologyTemplate>> connectionMatchingOptions;
        InjectorReplaceOptions injectionReplaceOptions = new InjectorReplaceOptions();

        try {

            Map<TRequirement, String> requirementsAndMatchingBasisCapabilityTypes =
                splitting.getOpenRequirementsAndMatchingBasisCapabilityTypeNames(this.getServiceTemplate().getTopologyTemplate());
            // Output check
            for (TRequirement req : requirementsAndMatchingBasisCapabilityTypes.keySet()) {
                System.out.println("open Requirement: " + req.getId());
                System.out.println("matchingBasisType: " + requirementsAndMatchingBasisCapabilityTypes.get(req));
            }

            if (requirementsAndMatchingBasisCapabilityTypes.containsValue("Container")) {
                hostMatchingOptions = splitting.getHostingMatchingOptionsWithDefaultLabeling(topologyTemplate);
            } else {
                hostMatchingOptions = null;
            }
            if (requirementsAndMatchingBasisCapabilityTypes.containsValue("Endpoint")) {
                connectionMatchingOptions = splitting.getConnectionInjectionOptions(topologyTemplate);
            } else {
                connectionMatchingOptions = null;
            }

            injectionReplaceOptions.setTopologyTemplate(topologyTemplate);
            injectionReplaceOptions.setHostInjectionOptions(hostMatchingOptions);
            injectionReplaceOptions.setConnectionInjectionOptions(connectionMatchingOptions);

            if (hostMatchingOptions == null && connectionMatchingOptions == null) {
                return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("No need for matching").build();
            }
        } catch (SplittingException e) {
            LOGGER.error("Could not match", e);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
        return Response.ok().entity(injectionReplaceOptions).build();
    }

    @POST
    @Path("placeholder/generator")
    @Consumes( {MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    @Produces( {MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response generatePlaceholdersWithCapability() {
        Splitting splitting = new Splitting();
        TTopologyTemplate topologyTemplate = this.getServiceTemplate().getTopologyTemplate();

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
                TNodeType sourceNodeType = repo.getElement(id);

                TInterfaces sourceNodeTypeInterfaces = sourceNodeType.getInterfaces();
                if (sourceNodeTypeInterfaces != null) {
                    for (TInterface tInterface : sourceNodeTypeInterfaces.getInterface()) {
                        // TODO: make this more safe
                        for (TOperation tOperation : tInterface.getOperation()) {
                            TOperation.InputParameters inputParameters = tOperation.getInputParameters();
                            if (inputParameters != null) {
                                for (TParameter inputParameter : inputParameters.getInputParameter()) {
                                    PropertyDefinitionKV inputParamKV = new PropertyDefinitionKV(inputParameter.getName(), inputParameter.getType());
                                    if (sourceNodeType.getWinerysPropertiesDefinition() != null &&
                                        !sourceNodeType.getWinerysPropertiesDefinition().getPropertyDefinitions().contains(inputParamKV)
                                        && !propertyDefinitionKVList.contains(inputParamKV)) {
                                        propertyDefinitionKVList.add(inputParamKV);
                                        placeholderNodeTemplateProperties.put(inputParameter.getName(), "get_input: " + inputParameter.getName());
                                    }
                                }
                            }
                        }
                    }
                }

                List<TRelationshipTemplate> incomingRelationshipTemplates = ModelUtilities.getIncomingRelationshipTemplates(topologyTemplate, nodeTemplateWithOpenReq);
                List<TParameter> inputParameters = splitting.getInputParamListofIncomingRelationshipTemplates(topologyTemplate, incomingRelationshipTemplates);
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

                    PropertyDefinitionKV inputParamKV = new PropertyDefinitionKV(inputParameter.getName(), inputParameter.getType());
                    if (sourceNodeType.getWinerysPropertiesDefinition() != null &&
                        !sourceNodeType.getWinerysPropertiesDefinition().getPropertyDefinitions().contains(inputParamKV)
                        && !propertyDefinitionKVList.contains(inputParamKV)) {
                        propertyDefinitionKVList.add(inputParamKV);
                        placeholderNodeTemplateProperties.put(inputParameter.getName(), "get_input: " + inputParameter.getName());
                    }
                }

                // get required capability type of open requirement
                QName capabilityType = splitting.getRequiredCapabilityTypeQNameOfRequirement(entry.getKey());

                // create new placeholder node type
                TNodeType placeholderNodeType = splitting.createPlaceholderNodeType(nodeTemplateWithOpenReq.getName());
                QName placeholderQName = new QName(placeholderNodeType.getTargetNamespace(), placeholderNodeType.getName());

                WinerysPropertiesDefinition winerysPropertiesDefinition = sourceNodeType.getWinerysPropertiesDefinition();
                // add properties definition
                placeholderNodeType.setProperties(null);
                if (winerysPropertiesDefinition != null) {
                    winerysPropertiesDefinition.setPropertyDefinitions(propertyDefinitionKVList);
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
                TNodeTemplate placeholderNodeTemplate = splitting.createPlaceholderNodeTemplate(topologyTemplate, nodeTemplateWithOpenReq.getName(), placeholderQName);

                // create capability of placeholder node template
                TCapability capa = splitting.createPlaceholderCapability(topologyTemplate, capabilityType);

                ModelUtilities.setPropertiesKV(placeholderNodeTemplate, placeholderNodeTemplateProperties);
                placeholderNodeTemplate.setCapabilities(new Capabilities());
                placeholderNodeTemplate.getCapabilities().getCapability().add(capa);
                for (Map.Entry<QName, String> targetLocation : nodeTemplateWithOpenReq.getOtherAttributes().entrySet()) {
                    placeholderNodeTemplate.getOtherAttributes().put(targetLocation.getKey(), targetLocation.getValue());
                }
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

    @POST
    @Path("injector/replace")
    @Consumes( {MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    @Produces( {MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public Response injectNodeTemplates(InjectorReplaceData injectorReplaceData, @Context UriInfo uriInfo) throws
        Exception {

        if (injectorReplaceData.hostInjections != null) {
            Collection<TTopologyTemplate> hostInjectorTopologyTemplates = injectorReplaceData.hostInjections.values();
            hostInjectorTopologyTemplates.forEach(t -> {
                try {
                    ModelUtilities.patchAnyAttributes(t.getNodeTemplates());
                } catch (IOException e) {
                    LOGGER.error("XML was invalid", e);
                }
            });
        }
        if (injectorReplaceData.connectionInjections != null) {
            Collection<TTopologyTemplate> connectionInjectorTopologyTemplates = injectorReplaceData.connectionInjections.values();
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

        //Test Method findOpenRequirements
        Map<TRequirement, String> requirementsAndMatchingBasisCapabilityTypes =
            splitting.getOpenRequirementsAndMatchingBasisCapabilityTypeNames(this.getServiceTemplate().getTopologyTemplate());
        // Output check
        for (TRequirement req : requirementsAndMatchingBasisCapabilityTypes.keySet()) {
            System.out.println("open Requirement: " + req.getId());
            System.out.println("matchingbasisType: " + requirementsAndMatchingBasisCapabilityTypes.get(req));
        }
        // End Output check

        if (requirementsAndMatchingBasisCapabilityTypes.containsValue("Container")) {
            matchedHostsTopologyTemplate = splitting.injectNodeTemplates(this.getServiceTemplate().getTopologyTemplate(), injectorReplaceData.hostInjections, InjectRemoval.REMOVE_REPLACED_AND_SUCCESSORS);

            if (requirementsAndMatchingBasisCapabilityTypes.containsValue("Endpoint")) {
                matchedConnectedTopologyTemplate = splitting.injectConnectionNodeTemplates(matchedHostsTopologyTemplate, injectorReplaceData.connectionInjections);
            } else {
                matchedConnectedTopologyTemplate = matchedHostsTopologyTemplate;
            }
        } else if (requirementsAndMatchingBasisCapabilityTypes.containsValue("Endpoint")) {
            matchedConnectedTopologyTemplate = splitting.injectConnectionNodeTemplates(this.getServiceTemplate().getTopologyTemplate(), injectorReplaceData.connectionInjections);
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

        this.getServiceTemplate().setTopologyTemplate(daSpecifiedTopology);

        LOGGER.debug("Persisting...");
        RestUtils.persist(this);
        LOGGER.debug("Persisted.");

        //No renaming of the Service Template allowed because of the plans

        URI url = uriInfo.getBaseUri().resolve(RestUtils.getAbsoluteURL(id));
        LOGGER.debug("URI of the old and new service template {}", url.toString());
        return Response.created(url).build();
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
        TTags tagsOfServiceTemplate = this.getServiceTemplate().getTags();
        List<OTParticipant> participants = originTopologyTemplate.getParticipants();

        String participantId = "";
        TTags newTagList = new TTags();
        for (TTag tagOfServiceTemplate : tagsOfServiceTemplate.getTag()) {
            if (tagOfServiceTemplate.getName().equals("participant")) {
                participantId = tagOfServiceTemplate.getValue();
                newTagList.getTag().add(tagOfServiceTemplate);
            } else if (!tagOfServiceTemplate.getName().equals("choreography")) {
                newTagList.getTag().add(tagOfServiceTemplate);
            }
        }
        final String finalParticipantId = participantId;

        List<String> nodeTemplatesWithNewHost = new ArrayList<>();

        for (TNodeTemplate tNodeTemplate : originTopologyTemplate.getNodeTemplates()) {
            //Multiple participants can be annotated on one node template
            Optional<String> nodeOwners = ModelUtilities.getParticipant(tNodeTemplate);
            if (nodeOwners.isPresent() && nodeOwners.get().contains(finalParticipantId)) {
                for (TRelationshipTemplate tRelationshipTemplate : ModelUtilities.getIncomingRelationshipTemplates(originTopologyTemplate, tNodeTemplate)) {
                    nodeTemplatesWithNewHost.add(ModelUtilities.getSourceNodeTemplateOfRelationshipTemplate(originTopologyTemplate, tRelationshipTemplate).getId());
                }
            }
        }

        ServiceTemplateId id = (ServiceTemplateId) this.getId();
        WineryVersion version = VersionUtils.getVersion(id.getXmlId().getDecoded());

        WineryVersion newVersion = new WineryVersion(
            "_substituted_" + version.toString(),
            1,
            0
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

        Map<String, List<TTopologyTemplate>> resultList = splitting.getHostingInjectionOptions(BackendUtils.clone(newServiceTemplate.getTopologyTemplate()));
        for (Map.Entry<String, List<TTopologyTemplate>> entry : resultList.entrySet()) {
            Optional<String> nodeOwners = ModelUtilities.getParticipant(newServiceTemplate.getTopologyTemplate().getNodeTemplate(entry.getKey()));
            if (nodeOwners.isPresent() && nodeOwners.get().contains(finalParticipantId)) {
                if (nodeTemplatesWithNewHost.contains(entry.getKey()) && !resultList.get(entry.getKey()).isEmpty()) {
                    Map<String, TTopologyTemplate> choiceTopologyTemplate = new LinkedHashMap<>();
                    choiceTopologyTemplate.put(entry.getKey(), entry.getValue().get(0));
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

        newTagList.getTag().add(choreoTag);
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
    public List<Response> createParticipantsVersion() throws IOException {
        IRepository repo = RepositoryFactory.getRepository();
        // create list of responses because we create multiple resources at once
        List<Response> listOfResponses = new ArrayList<>();

        LOGGER.debug("Creating new participants version of Service Template {}...", this.getId());

        ServiceTemplateId id = (ServiceTemplateId) this.getId();
        WineryVersion version = VersionUtils.getVersion(id.getXmlId().getDecoded());
        TTopologyTemplate topologyTemplate = this.getTopology();

        TTags tagsOfServiceTemplate = this.getServiceTemplate().getTags();
        List<TTag> tags = tagsOfServiceTemplate.getTag();

        Splitting splitting = new Splitting();
        // iterate over tags of origin service template

        for (OTParticipant participant : topologyTemplate.getParticipants()) {
            // check if tag with partner in service template
            WineryVersion newVersion = new WineryVersion(
                participant.getName() + "-" + version.toString().replace("gdm", "ldm"),
                1,
                0
            );

            List<OTParticipant> newParticipantList = new ArrayList<>();
            newParticipantList.addAll(topologyTemplate.getParticipants());

            // create list of tags to add to service template
            TTags tTagList = new TTags();

            // new tag to define participant of service template
            TTag participantTag = new TTag();
            participantTag.setName("participant");
            participantTag.setValue(participant.getName());
            tTagList.getTag().add(participantTag);

            String choreoValue = splitting.calculateChoreographyTag(this.getServiceTemplate().getTopologyTemplate().getNodeTemplates(), participant.getName());
            TTag choreoTag = new TTag();
            choreoTag.setName("choreography");
            choreoTag.setValue(choreoValue);
            tTagList.getTag().add(choreoTag);
            ServiceTemplateId newId = new ServiceTemplateId(id.getNamespace().getDecoded(),
                VersionUtils.getNameWithoutVersion(id.getXmlId().getDecoded()) + WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR + newVersion.toString(),
                false);

            if (repo.exists(newId)) {
                repo.forceDelete(newId);
            }

            ResourceResult response = RestUtils.duplicate(id, newId);

            if (response.getStatus() == Status.CREATED) {
                response.setUri(null);
                response.setMessage(new QNameApiData(newId));
            }

            TServiceTemplate tempServiceTempl = repo.getElement(newId);
            // reset tags and set tags with respective entry
            tempServiceTempl.setTags(null);
            tempServiceTempl.setTags(tTagList);
            tempServiceTempl.getTopologyTemplate().setParticipants(newParticipantList);

            listOfResponses.add(response.getResponse());
            // set element to propagate changed tags
            repo.setElement(newId, tempServiceTempl);
        }
        return listOfResponses;
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
            0
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
}
