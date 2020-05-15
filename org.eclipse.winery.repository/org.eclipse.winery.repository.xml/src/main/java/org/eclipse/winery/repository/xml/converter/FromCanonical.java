/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.xml.converter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.winery.model.tosca.xml.RelationshipSourceOrTarget;
import org.eclipse.winery.model.tosca.xml.TArtifact;
import org.eclipse.winery.model.tosca.xml.TArtifactReference;
import org.eclipse.winery.model.tosca.xml.TArtifacts;
import org.eclipse.winery.model.tosca.xml.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.xml.TCapability;
import org.eclipse.winery.model.tosca.xml.TCapabilityDefinition;
import org.eclipse.winery.model.tosca.xml.TCapabilityRef;
import org.eclipse.winery.model.tosca.xml.TCondition;
import org.eclipse.winery.model.tosca.xml.TConstraint;
import org.eclipse.winery.model.tosca.xml.TEntityTemplate;
import org.eclipse.winery.model.tosca.xml.TExportedInterface;
import org.eclipse.winery.model.tosca.xml.TExportedOperation;
import org.eclipse.winery.model.tosca.xml.TImplementation;
import org.eclipse.winery.model.tosca.xml.TInterfaceDefinition;
import org.eclipse.winery.model.tosca.xml.TInterfaces;
import org.eclipse.winery.model.tosca.xml.TNodeTemplate;
import org.eclipse.winery.model.tosca.xml.TOperationDefinition;
import org.eclipse.winery.model.tosca.xml.TPlan;
import org.eclipse.winery.model.tosca.xml.TPlans;
import org.eclipse.winery.model.tosca.xml.TPolicies;
import org.eclipse.winery.model.tosca.xml.TPolicy;
import org.eclipse.winery.model.tosca.xml.TPropertyConstraint;
import org.eclipse.winery.model.tosca.xml.TPropertyMapping;
import org.eclipse.winery.model.tosca.xml.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.xml.TRequirement;
import org.eclipse.winery.model.tosca.xml.TRequirementDefinition;
import org.eclipse.winery.model.tosca.xml.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.xml.TAppliesTo;
import org.eclipse.winery.model.tosca.xml.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.xml.TEntityType;
import org.eclipse.winery.model.tosca.xml.TExtensibleElements;
import org.eclipse.winery.model.tosca.xml.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.xml.TEntityTypeImplementation;
import org.eclipse.winery.model.tosca.xml.TRequirementRef;
import org.eclipse.winery.model.tosca.xml.TTag;
import org.eclipse.winery.model.tosca.xml.TRequiredContainerFeature;
import org.eclipse.winery.model.tosca.xml.TBoolean;
import org.eclipse.winery.model.tosca.xml.TParameter;
import org.eclipse.winery.model.tosca.xml.TExtension;
import org.eclipse.winery.model.tosca.xml.Definitions;
import org.eclipse.winery.model.tosca.xml.TArtifactTemplate;
import org.eclipse.winery.model.tosca.xml.TArtifactType;
import org.eclipse.winery.model.tosca.xml.TCapabilityType;
import org.eclipse.winery.model.tosca.xml.TDefinitions;
import org.eclipse.winery.model.tosca.xml.TDocumentation;
import org.eclipse.winery.model.tosca.xml.TImport;
import org.eclipse.winery.model.tosca.xml.TInterface;
import org.eclipse.winery.model.tosca.xml.TInterfaceType;
import org.eclipse.winery.model.tosca.xml.TNodeType;
import org.eclipse.winery.model.tosca.xml.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.xml.TOperation;
import org.eclipse.winery.model.tosca.xml.TPolicyType;
import org.eclipse.winery.model.tosca.xml.TRelationshipType;
import org.eclipse.winery.model.tosca.xml.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.xml.TRequirementType;
import org.eclipse.winery.model.tosca.xml.TServiceTemplate;
import org.eclipse.winery.model.tosca.xml.TTopologyElementInstanceStates;
import org.eclipse.winery.model.tosca.xml.TTopologyTemplate;
import org.eclipse.winery.repository.xml.XmlRepository;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NonNullByDefault
public class FromCanonical {

    private static final Logger LOGGER = LoggerFactory.getLogger(FromCanonical.class);
    
    private final XmlRepository repository;
    private List<TImport> rollingImportStorage;

    public FromCanonical(XmlRepository repository) {
        this.repository = repository;
    }
    
    public TDefinitions convert(org.eclipse.winery.model.tosca.TDefinitions canonical) {
        return convert(canonical, false);
    }

    /**
     * Converts a canonical TDefinitions collection to XML TDefinitions. 
     */
    public TDefinitions convert(org.eclipse.winery.model.tosca.TDefinitions canonical, boolean convertImports) {
        // FIXME need to correctly deal with convertImports flag to create a self-contained Definitions to export as CSAR if it is set.
        Definitions.Builder builder = new Definitions.Builder(canonical.getId(), canonical.getTargetNamespace())
            .setImport(convertImports(canonical.getImport()))
            .addTypes(convertTypes(canonical.getTypes()))
            .setServiceTemplates(convertServiceTemplates(canonical.getServiceTemplates()))
            .setNodeTypes(convertNodeTypes(canonical.getNodeTypes()))
            .setNodeTypeImplementations(convertNodeTypeImplementations(canonical.getNodeTypeImplementations()))
            .setRelationshipTypes(convertRelationshipTypes(canonical.getRelationshipTypes()))
            .setRelationshipTypeImplementations(convertRelationshipImplementations(canonical.getRelationshipTypeImplementations()))
            .setCapabilityTypes(convertCapabilityTypes(canonical.getCapabilityTypes()))
            .setArtifactTypes(convertArtifactTypes(canonical.getArtifactTypes()))
            .setArtifactTemplates(convertArtifactTemplates(canonical.getArtifactTemplates()))
            .setPolicyTypes(convertPolicyTypes(canonical.getPolicyTypes()))
            .setInterfaceTypes(convertInterfaceTypes(canonical.getInterfaceTypes()))
            .setName(canonical.getName())
            .addImports(this.rollingImportStorage)
            .addRequirementTypes(convertRequirementTypes(canonical.getRequirementTypes()));
        fillExtensibleElementsProperties(builder, canonical);
        return builder.build();
    }

    private TRelationshipType convert(org.eclipse.winery.model.tosca.TRelationshipType canonical) {
        TRelationshipType.Builder builder = new TRelationshipType.Builder(canonical.getIdFromIdOrNameField())
            .addSourceInterfaces(convertInterfaces(canonical.getSourceInterfaces()))
            .addTargetInterfaces(convertInterfaces(canonical.getTargetInterfaces()));
        if (canonical.getValidSource() != null) {
            builder.setValidSource(canonical.getValidSource().getTypeRef());
        }
        if (canonical.getValidTarget() != null) {
            builder.setValidTarget(canonical.getValidTarget().getTypeRef());
        }
        if (canonical.getInterfaceDefinitions() != null) {
            builder.setInterfaceDefinitions(canonical.getInterfaceDefinitions().stream()
                .map(this::convert).collect(Collectors.toList()));
        }
        return builder.build();
    }

    private List<TInterface> convertInterfaces(org.eclipse.winery.model.tosca.@Nullable TInterfaces interfaces) {
        if (interfaces == null) {
            return Collections.emptyList();
        }
        return interfaces.getInterface().stream().map(this::convertInterface).collect(Collectors.toList());
    }
    
    private TInterface convertInterface(org.eclipse.winery.model.tosca.TInterface canonical) {
        return new TInterface.Builder(canonical.getName(), convertOperations(canonical.getOperation())).build();
    }

    private List<TOperation> convertOperations(List<org.eclipse.winery.model.tosca.TOperation> operation) {
        return operation.stream().map(this::convert).collect(Collectors.toList());
    }

    private TOperation convert(org.eclipse.winery.model.tosca.TOperation canonical) {
        TOperation.Builder builder = new TOperation.Builder(canonical.getName());
        if (canonical.getInputParameters() != null) {
            builder.addInputParameters(canonical.getInputParameters().getInputParameter().stream()
                .map(this::convert).collect(Collectors.toList()));
        }
        if (canonical.getOutputParameters() != null) {
            builder.addOutputParameters(canonical.getOutputParameters().getOutputParameter().stream()
                .map(this::convert).collect(Collectors.toList()));
        }
        return builder.build();
    }

    private TParameter convert(org.eclipse.winery.model.tosca.TParameter canonical) {
        return new TParameter.Builder(canonical.getName(), canonical.getType(), TBoolean.fromValue(canonical.getRequired().value())).build();
    }

    private TRelationshipTypeImplementation convert(org.eclipse.winery.model.tosca.TRelationshipTypeImplementation canonical) {
        TRelationshipTypeImplementation.Builder builder = new TRelationshipTypeImplementation.Builder(canonical.getName(), canonical.getRelationshipType());
        fillEntityTypeImplementationProperties(builder, canonical);
        if (canonical.getDerivedFrom() != null) {
            TRelationshipTypeImplementation.DerivedFrom derived = new TRelationshipTypeImplementation.DerivedFrom();
            derived.setRelationshipTypeImplementationRef(canonical.getDerivedFrom().getRelationshipTypeImplementationRef());
            builder.setDerivedFrom(derived);
        }
        return builder.build();
    }
    
    private <Builder extends TEntityTypeImplementation.Builder<?>, Value extends org.eclipse.winery.model.tosca.TEntityTypeImplementation> 
        void fillEntityTypeImplementationProperties(Builder builder, Value canonical) {
        if (canonical.getRequiredContainerFeatures() != null) {
            builder.addRequiredContainerFeatures(canonical.getRequiredContainerFeatures().getRequiredContainerFeature()
                .stream().map(this::convert).collect(Collectors.toList()));
        }
        if (canonical.getTags() != null) {
            builder.addTags(canonical.getTags().getTag().stream().map(this::convert).collect(Collectors.toList()));
        }
        if (canonical.getImplementationArtifacts() != null) {
            builder.addImplementationArtifacts(canonical.getImplementationArtifacts().getImplementationArtifact().stream()
                .map(this::convert).collect(Collectors.toList()));
        }
        builder.setTargetNamespace(canonical.getTargetNamespace());
        builder.setAbstract(TBoolean.fromValue(canonical.getAbstract().value()));
        builder.setFinal(TBoolean.fromValue(canonical.getFinal().value()));
        fillExtensibleElementsProperties(builder, canonical);
    }

    private TImplementationArtifacts.ImplementationArtifact convert(org.eclipse.winery.model.tosca.TImplementationArtifacts.ImplementationArtifact canonical) {
        return new TImplementationArtifacts.ImplementationArtifact.Builder(canonical.getArtifactType())
            .setName(canonical.getName())
            .setInterfaceName(canonical.getInterfaceName())
            .setOperationName(canonical.getOperationName())
            .setArtifactRef(canonical.getArtifactRef())
            .build();
    }

    private TTag convert(org.eclipse.winery.model.tosca.TTag canonical) {
        return new TTag.Builder().setName(canonical.getName()).setValue(canonical.getValue()).build();
    }

    private TRequiredContainerFeature convert(org.eclipse.winery.model.tosca.TRequiredContainerFeature canonical) {
        TRequiredContainerFeature result = new TRequiredContainerFeature();
        result.setFeature(canonical.getFeature());
        return result;
    }

    private TPolicyType convert(org.eclipse.winery.model.tosca.TPolicyType canonical) {
        TPolicyType.Builder builder = new TPolicyType.Builder(canonical.getName());
        if (canonical.getAppliesTo() != null) {
            TAppliesTo appliesTo = new TAppliesTo();
            appliesTo.getNodeTypeReference().addAll(canonical.getAppliesTo().getNodeTypeReference().stream()
                .map(c -> { 
                    TAppliesTo.NodeTypeReference result = new TAppliesTo.NodeTypeReference();
                    result.setTypeRef(c.getTypeRef());
                    return result;
                })
                .collect(Collectors.toList()));
            builder.setAppliesTo(appliesTo);
        }
        fillEntityTypeProperties(builder, canonical);
        return builder.build();
    }

    private <Builder extends TEntityType.Builder, Value extends org.eclipse.winery.model.tosca.TEntityType> 
        void fillEntityTypeProperties(Builder builder, Value canonical) {
        if (canonical.getTags() != null) {
            builder.addTags(canonical.getTags().getTag().stream().map(this::convert).collect(Collectors.toList()));
        }
        if (canonical.getDerivedFrom() != null) {
            TEntityType.DerivedFrom derived = new TEntityType.DerivedFrom();
            derived.setTypeRef(canonical.getDerivedFrom().getTypeRef());
            builder.setDerivedFrom(derived);
        }
        if (canonical.getPropertiesDefinition() != null) {
            TEntityType.PropertiesDefinition propertiesDefinition = new TEntityType.PropertiesDefinition();
            propertiesDefinition.setElement(canonical.getPropertiesDefinition().getElement());
            propertiesDefinition.setType(canonical.getPropertiesDefinition().getType());
            builder.setPropertiesDefinition(propertiesDefinition);
        }
        builder.setAbstract(TBoolean.fromValue(canonical.getAbstract().value()));
        builder.setFinal(TBoolean.fromValue(canonical.getFinal().value()));
        builder.setTargetNamespace(canonical.getTargetNamespace());
        fillExtensibleElementsProperties(builder, canonical);
    }
    
    private <Builder extends TExtensibleElements.Builder, Value extends org.eclipse.winery.model.tosca.TExtensibleElements>
        void fillExtensibleElementsProperties(Builder builder, Value canonical) {
        builder.setDocumentation(canonical.getDocumentation().stream().map(this::convert).collect(Collectors.toList()));
        builder.setOtherAttributes(canonical.getOtherAttributes());
        builder.setAny(canonical.getAny());
    }

    private TRequirementType convert(org.eclipse.winery.model.tosca.TRequirementType canonical) {
        TRequirementType.Builder builder = new TRequirementType.Builder(canonical.getName());
        builder.setRequiredCapabilityType(canonical.getRequiredCapabilityType());
        fillEntityTypeProperties(builder, canonical);
        return builder.build();
    }

    private TNodeTypeImplementation convert(org.eclipse.winery.model.tosca.TNodeTypeImplementation canonical) {
        TNodeTypeImplementation.Builder builder = new TNodeTypeImplementation.Builder(canonical.getName(), canonical.getNodeType());
        if (canonical.getDeploymentArtifacts() != null) {
            TDeploymentArtifacts artifacts = new TDeploymentArtifacts.Builder(canonical.getDeploymentArtifacts()
                .getDeploymentArtifact().stream().map(this::convert).collect(Collectors.toList())).build();
            builder.setDeploymentArtifacts(artifacts);
        }
        if (canonical.getDerivedFrom() != null) {
            TNodeTypeImplementation.DerivedFrom derived = new TNodeTypeImplementation.DerivedFrom();
            derived.setNodeTypeImplementationRef(canonical.getDerivedFrom().getNodeTypeImplementationRef());
            builder.setDerivedFrom(derived);
        }
        fillEntityTypeImplementationProperties(builder, canonical);
        return builder.build();
    }

    private TDeploymentArtifact convert(org.eclipse.winery.model.tosca.TDeploymentArtifact canonical) {
        TDeploymentArtifact.Builder builder = new TDeploymentArtifact.Builder(canonical.getName(), canonical.getArtifactType());
        builder.setArtifactRef(canonical.getArtifactRef());
        fillExtensibleElementsProperties(builder, canonical);
        return builder.build();
    }

    private TNodeType convert(org.eclipse.winery.model.tosca.TNodeType canonical) {
        TNodeType.Builder builder = new TNodeType.Builder(canonical.getName());
        if (canonical.getRequirementDefinitions() != null) {
            TNodeType.RequirementDefinitions reqDefs = new TNodeType.RequirementDefinitions();
            reqDefs.getRequirementDefinition().addAll(canonical.getRequirementDefinitions().getRequirementDefinition()
                .stream().map(this::convert).collect(Collectors.toList()));
            builder.setRequirementDefinitions(reqDefs);
        }
        if (canonical.getCapabilityDefinitions() != null) {
            TNodeType.CapabilityDefinitions capDefs = new TNodeType.CapabilityDefinitions();
            capDefs.getCapabilityDefinition().addAll(canonical.getCapabilityDefinitions().getCapabilityDefinition()
                .stream().map(this::convert).collect(Collectors.toList()));
            builder.setCapabilityDefinitions(capDefs);
        }
        if (canonical.getInstanceStates() != null) {
            TTopologyElementInstanceStates instanceStates = new TTopologyElementInstanceStates();
            instanceStates.getInstanceState().addAll(canonical.getInstanceStates().getInstanceState().stream()
                .map(c -> {
                    TTopologyElementInstanceStates.InstanceState r = new TTopologyElementInstanceStates.InstanceState();
                    r.setState(c.getState());
                    return r;
                }).collect(Collectors.toList()));
            builder.setInstanceStates(instanceStates);
        }
        if (canonical.getInterfaces() != null) {
            TInterfaces ifaces = new TInterfaces();
            ifaces.getInterface().addAll(convertInterfaces(canonical.getInterfaces()));
            builder.setInterfaces(ifaces);
        }
        if (canonical.getInterfaceDefinitions() != null) {
            builder.setInterfaceDefinitions(canonical.getInterfaceDefinitions().stream()
                .map(this::convert).collect(Collectors.toList()));
        }
        if (canonical.getArtifacts() != null) {
            TArtifacts artifacts = new TArtifacts();
            artifacts.getArtifact().addAll(canonical.getArtifacts().getArtifact().stream()
                .map(this::convert).collect(Collectors.toList()));
            builder.setArtifacts(artifacts);
        }
        fillEntityTypeProperties(builder, canonical);
        return builder.build();
    }
    
    private TInterfaceDefinition convert(org.eclipse.winery.model.tosca.TInterfaceDefinition canonical) {
        TInterfaceDefinition definition = new TInterfaceDefinition();
        definition.setName(canonical.getName());
        definition.setType(canonical.getType());
        definition.setInputs(canonical.getInputs());
//        definition.setInputs(canonical.getInputs().stream().map(this::convert).collect(Collectors.toList()));
        definition.setOperations(canonical.getOperations().stream().map(this::convert).collect(Collectors.toList()));
        return definition;
    }
    
    private TOperationDefinition convert(org.eclipse.winery.model.tosca.TOperationDefinition canonical) {
        TOperationDefinition definition = new TOperationDefinition();
        definition.setName(canonical.getName());
        definition.setDescription(canonical.getDescription());
        definition.setInputs(canonical.getInputs());
        definition.setOutputs(canonical.getOutputs());
        definition.setImplementation(convert(canonical.getImplementation()));
        return definition;
    }
    
    private TImplementation convert(org.eclipse.winery.model.tosca.TImplementation canonical) {
        TImplementation definition = new TImplementation();
        definition.setPrimary(canonical.getPrimary());
        definition.setDependencies(canonical.getDependencies());
        definition.setOperationHost(canonical.getOperationHost());
        definition.setTimeout(canonical.getTimeout());
        return definition;
    }
    
    private TArtifact convert(org.eclipse.winery.model.tosca.TArtifact canonical) {
        TArtifact.Builder builder = new TArtifact.Builder(canonical.getName(), canonical.getType());
        builder.setDeployPath(canonical.getDeployPath());
        builder.setDescription(canonical.getDescription());
        builder.setFile(canonical.getFile());
        fillEntityTemplateProperties(builder, canonical);
        return builder.build();
    }

    private TRequirementDefinition convert(org.eclipse.winery.model.tosca.TRequirementDefinition canonical) {
        // requirementType can be null in the canonical model because YAML mode doesn't use it.
        //  it's required for us, though, so we just assume it's present
        TRequirementDefinition.Builder builder = new TRequirementDefinition.Builder(canonical.getName(), canonical.getRequirementType());
        if (canonical.getConstraints() != null) {
            TRequirementDefinition.Constraints constraints = new TRequirementDefinition.Constraints();
            constraints.getConstraint().addAll(canonical.getConstraints().getConstraint().stream()
                .map(this::convert).collect(Collectors.toList()));
            builder.setConstraints(constraints);
        }
        builder.setLowerBound(canonical.getLowerBound());
        builder.setUpperBound(canonical.getUpperBound());
        // FIXME capability, node and relationship are YAML things, they should not be moved around here
        builder.setCapability(canonical.getCapability());
        builder.setNode(canonical.getNode());
        builder.setRelationship(canonical.getRelationship());
        fillExtensibleElementsProperties(builder, canonical);
        return builder.build();
    }
    
    private TCapabilityDefinition convert(org.eclipse.winery.model.tosca.TCapabilityDefinition canonical) {
        TCapabilityDefinition.Builder builder = new TCapabilityDefinition.Builder(canonical.getName(), canonical.getCapabilityType());
        if (canonical.getConstraints() != null) {
            canonical.getConstraints().getConstraint()
                .stream().map(this::convert)
                .forEach(builder::addConstraints);
        }
        builder.setLowerBound(canonical.getLowerBound());
        builder.setUpperBound(canonical.getUpperBound());
        builder.setValidSourceTypes(canonical.getValidSourceTypes());
        fillExtensibleElementsProperties(builder, canonical);
        return builder.build();
    }
    
    private TConstraint convert(org.eclipse.winery.model.tosca.TConstraint canonical) {
        TConstraint constraint = new TConstraint();
        constraint.setAny(canonical.getAny());
        constraint.setConstraintType(canonical.getConstraintType());
        return constraint;
    }
    
    private TCapability convert(org.eclipse.winery.model.tosca.TCapability canonical) {
        TCapability.Builder builder = new TCapability.Builder(canonical.getId(), canonical.getType(), canonical.getName());
        fillRelationshipSourceOrTargetProperties(builder, canonical);
        return builder.build();
    }

    private <Builder extends RelationshipSourceOrTarget.Builder, Value extends org.eclipse.winery.model.tosca.RelationshipSourceOrTarget>
        void fillRelationshipSourceOrTargetProperties(Builder builder, Value canonical) {
        // no specific properties to fill, just traverse the hierarchy
        fillEntityTemplateProperties(builder, canonical);
    }
    
    private <Builder extends TEntityTemplate.Builder, Value extends org.eclipse.winery.model.tosca.TEntityTemplate>
        void fillEntityTemplateProperties(Builder builder, Value canonical) {
        if (canonical.getProperties() != null) {
            builder.setProperties(convertProperties(canonical.getProperties()));
        }
        if (canonical.getPropertyConstraints() != null) {
            TEntityTemplate.PropertyConstraints constraints = new TEntityTemplate.PropertyConstraints();
            constraints.getPropertyConstraint().addAll(canonical.getPropertyConstraints().getPropertyConstraint().stream()
                .map(this::convert).collect(Collectors.toList()));
            builder.setPropertyConstraints(constraints);
        }
    }
    
    private TEntityTemplate.Properties convertProperties(org.eclipse.winery.model.tosca.TEntityTemplate.Properties canonical) {
        TEntityTemplate.Properties props = new TEntityTemplate.Properties();
        if (canonical instanceof org.eclipse.winery.model.tosca.TEntityTemplate.XmlProperties) {
            props.setAny(((org.eclipse.winery.model.tosca.TEntityTemplate.XmlProperties) canonical).getAny());
        }
        else if (canonical instanceof org.eclipse.winery.model.tosca.TEntityTemplate.WineryKVProperties) {
            // FIXME this shouldn't be part of the xml model's definition
            //  instead use PropertyMappingSupport
            props.setKVProperties(((org.eclipse.winery.model.tosca.TEntityTemplate.WineryKVProperties) canonical).getKvProperties());
        }
        else if (canonical instanceof org.eclipse.winery.model.tosca.TEntityTemplate.YamlProperties) {
            // this is the messy case of converting from YAML to XML
            LOGGER.warn("KVProperties with complex entries are not supported for XML serialization");
        }
        return props;
    }

    private TPropertyConstraint convert(org.eclipse.winery.model.tosca.TPropertyConstraint canonical) {
        TPropertyConstraint constraint = new TPropertyConstraint();
        constraint.setAny(canonical.getAny());
        constraint.setConstraintType(canonical.getConstraintType());
        constraint.setProperty(canonical.getProperty());
        return constraint;
    }

    private TInterfaceType convert(org.eclipse.winery.model.tosca.TInterfaceType canonical) {
        // FIXME the interface type is a YAML-model thing. It shouldn't be converted to XML like this!
        TInterfaceType.Builder builder = new TInterfaceType.Builder(canonical.getName());
        builder.setDescription(canonical.getDescription());
        builder.setOperations(canonical.getOperations().entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> convert(e.getValue()))));
        fillEntityTypeProperties(builder, canonical);
        return builder.build();
    }

    private TExtension convert(org.eclipse.winery.model.tosca.TExtension canonical) {
        TExtension.Builder builder = new TExtension.Builder(canonical.getNamespace());
        builder.setMustUnderstand(TBoolean.fromValue(canonical.getMustUnderstand().value()));
        fillExtensibleElementsProperties(builder, canonical);
        return builder.build();
    }

    private TDocumentation convert(org.eclipse.winery.model.tosca.TDocumentation canonical) {
        TDocumentation xml = new TDocumentation();
        xml.getContent().addAll(canonical.getContent());
        xml.setSource(canonical.getSource());
        xml.setLang(canonical.getLang());
        return xml;
    }

    private TCapabilityType convert(org.eclipse.winery.model.tosca.TCapabilityType canonical) {
        TCapabilityType.Builder builder = new TCapabilityType.Builder(canonical.getName());
        // FIXME validSourceTypes are apparently a YAML feature (again)
        builder.setValidSourceTypes(canonical.getValidNodeTypes());
        fillEntityTypeProperties(builder, canonical);
        return builder.build();
    }

    private TArtifactType convert(org.eclipse.winery.model.tosca.TArtifactType canonical) {
        TArtifactType.Builder builder = new TArtifactType.Builder(canonical.getName());
        builder.setMimeType(canonical.getMimeType());
        builder.setFileExtensions(canonical.getFileExtensions());
        fillEntityTypeProperties(builder, canonical);
        return builder.build();
    }

    private TArtifactTemplate convert(org.eclipse.winery.model.tosca.TArtifactTemplate canonical) {
        TArtifactTemplate.Builder builder = new TArtifactTemplate.Builder(canonical.getName(), canonical.getType());
        if (canonical.getArtifactReferences() != null) {
            canonical.getArtifactReferences().getArtifactReference().stream()
                .map(this::convert)
                .forEach(builder::addArtifactReferences);
        }
        fillEntityTemplateProperties(builder, canonical);
        return builder.build();
    }

    private TArtifactReference convert(org.eclipse.winery.model.tosca.TArtifactReference canonical) {
        TArtifactReference xml = new TArtifactReference();
        // FIXME because all of this is a mess, the includes and excludes are stored as Include and Exclude in the same field
//        xml.getIncludeOrExclude().add(canonical.getIncludeOrExclude());
        xml.setReference(canonical.getReference());
        return xml;
    }
    
    private TImport convert(org.eclipse.winery.model.tosca.TImport canonical) {
        TImport.Builder builder = new TImport.Builder(canonical.getImportType());
        builder.setNamespace(canonical.getNamespace());
        builder.setLocation(canonical.getLocation());
        fillExtensibleElementsProperties(builder, canonical);
        return builder.build();
    }
    
    private TServiceTemplate convert(org.eclipse.winery.model.tosca.TServiceTemplate canonical) {
        TServiceTemplate.Builder builder = new TServiceTemplate.Builder(canonical.getId(), convert(canonical.getTopologyTemplate()));
        if (canonical.getTags() != null) {
            canonical.getTags().getTag().stream().map(this::convert).forEach(builder::addTags);
        }
        if (canonical.getBoundaryDefinitions() != null) {
            builder.setBoundaryDefinitions(convert(canonical.getBoundaryDefinitions()));
        }
        if (canonical.getPlans() != null) {
            TPlans plans = new TPlans();
            plans.setTargetNamespace(canonical.getPlans().getTargetNamespace());
            plans.getPlan().addAll(canonical.getPlans().getPlan().stream().map(this::convert).collect(Collectors.toList()));
        }
        builder.setSubstitutableNodeType(canonical.getSubstitutableNodeType());
        fillExtensibleElementsProperties(builder, canonical);
        return builder.build();
    }
    
    private TPlan convert(org.eclipse.winery.model.tosca.TPlan canonical) {
        TPlan.Builder builder = new TPlan.Builder(canonical.getId(), canonical.getPlanType(), canonical.getPlanLanguage());
        if (canonical.getPrecondition() != null) {
            builder.setPrecondition(convert(canonical.getPrecondition()));
        }
        if (canonical.getInputParameters() != null) {
            TPlan.InputParameters inputs = new TPlan.InputParameters();
            inputs.getInputParameter().addAll(canonical.getInputParameters().getInputParameter().stream().map(this::convert).collect(Collectors.toList()));
            builder.setInputParameters(inputs);
        }
        if (canonical.getOutputParameters() != null) {
            TPlan.OutputParameters outputs = new TPlan.OutputParameters();
            outputs.getOutputParameter().addAll(canonical.getOutputParameters().getOutputParameter().stream().map(this::convert).collect(Collectors.toList()));
            builder.setOutputParameters(outputs);
        }
        if (canonical.getPlanModel() != null) {
            TPlan.PlanModel model = new TPlan.PlanModel();
            model.setAny(canonical.getAny());
            builder.setPlanModel(model);
        }
        if (canonical.getPlanModelReference() != null) {
            TPlan.PlanModelReference ref = new TPlan.PlanModelReference();
            ref.setReference(canonical.getPlanModelReference().getReference());
            builder.setPlanModelReference(ref);
        }
        builder.setName(canonical.getName());
        fillExtensibleElementsProperties(builder, canonical);
        return builder.build();
    }
    
    private TCondition convert(org.eclipse.winery.model.tosca.TCondition canonical) {
        TCondition xml = new TCondition();
        xml.setExpressionLanguage(canonical.getExpressionLanguage());
        xml.getAny().addAll(canonical.getAny());
        return xml;
    }
    
    private TBoundaryDefinitions convert(org.eclipse.winery.model.tosca.TBoundaryDefinitions canonical) {
        TBoundaryDefinitions.Builder builder = new TBoundaryDefinitions.Builder();
        if (canonical.getProperties() != null) {
            TBoundaryDefinitions.Properties props = new TBoundaryDefinitions.Properties();
            props.setAny(canonical.getProperties().getAny());
            if (canonical.getProperties().getPropertyMappings() != null) {
                TBoundaryDefinitions.Properties.PropertyMappings mappings = new TBoundaryDefinitions.Properties.PropertyMappings();
                mappings.getPropertyMapping().addAll(canonical.getProperties().getPropertyMappings().getPropertyMapping().stream()
                    .map(this::convert).collect(Collectors.toList()));
                props.setPropertyMappings(mappings);
            }
            builder.setProperties(props);
        }
        if (canonical.getRequirements() != null) {
            TBoundaryDefinitions.Requirements reqs = new TBoundaryDefinitions.Requirements();
            reqs.getRequirement().addAll(canonical.getRequirements().getRequirement().stream()
                .map(this::convert).collect(Collectors.toList()));
            builder.setRequirements(reqs);
        }
        if (canonical.getCapabilities() != null) {
            TBoundaryDefinitions.Capabilities caps = new TBoundaryDefinitions.Capabilities();
            caps.getCapability().addAll(canonical.getCapabilities().getCapability().stream()
                .map(this::convert).collect(Collectors.toList()));
            builder.setCapabilities(caps);
        }
        if (canonical.getPolicies() != null) {
            TPolicies policies = new TPolicies();
            policies.getPolicy().addAll(canonical.getPolicies().getPolicy().stream().map(this::convert).collect(Collectors.toList()));
            builder.setPolicies(policies);
        }
        if (canonical.getInterfaces() != null) {
            TBoundaryDefinitions.Interfaces ifaces = new TBoundaryDefinitions.Interfaces();
            ifaces.getInterface().addAll(canonical.getInterfaces().getInterface().stream().map(this::convert).collect(Collectors.toList()));
            builder.setInterfaces(ifaces);
        }
        return builder.build();
    }
    
    private TExportedInterface convert(org.eclipse.winery.model.tosca.TExportedInterface canonical) {
        TExportedInterface iface = new TExportedInterface();
        iface.setName(canonical.getName());
        iface.getOperation().addAll(canonical.getOperation().stream().map(this::convert).collect(Collectors.toList()));
        return iface;
    }
    
    private TExportedOperation convert(org.eclipse.winery.model.tosca.TExportedOperation canonical) {
        TExportedOperation xml = new TExportedOperation();
        xml.setName(canonical.getName());
        if (canonical.getNodeOperation() != null) {
            xml.setNodeOperation(convert(canonical.getNodeOperation()));
        }
        if (canonical.getRelationshipOperation() != null) {
            xml.setRelationshipOperation(convert(canonical.getRelationshipOperation()));
        }
        if (canonical.getPlan() != null) {
            TExportedOperation.Plan plan = new TExportedOperation.Plan();
            plan.setPlanRef(canonical.getPlan().getPlanRef());
            xml.setPlan(plan);
        }
        return xml;
    }
    
    private TExportedOperation.RelationshipOperation convert(org.eclipse.winery.model.tosca.TExportedOperation.RelationshipOperation canonical) {
        TExportedOperation.RelationshipOperation xml = new TExportedOperation.RelationshipOperation();
        xml.setRelationshipRef(canonical.getRelationshipRef());
        xml.setInterfaceName(canonical.getInterfaceName());
        xml.setOperationName(canonical.getOperationName());
        return xml;
    }
    
    private TExportedOperation.NodeOperation convert(org.eclipse.winery.model.tosca.TExportedOperation.NodeOperation canonical) {
        TExportedOperation.NodeOperation xml = new TExportedOperation.NodeOperation();
        xml.setNodeRef(canonical.getNodeRef());
        xml.setInterfaceName(canonical.getInterfaceName());
        xml.setOperationName(canonical.getOperationName());
        return xml;
    }
    
    private TPolicy convert(org.eclipse.winery.model.tosca.TPolicy canonical) {
        TPolicy.Builder builder =  new TPolicy.Builder(canonical.getPolicyType());
        builder.setName(canonical.getName());
        builder.setPolicyRef(canonical.getPolicyRef());
        builder.setTargets(canonical.getTargets());
        builder.setProperties(convertProperties(canonical.getProperties()));
        return builder.build();
    }
    
    private TCapabilityRef convert(org.eclipse.winery.model.tosca.TCapabilityRef canonical) {
        TCapabilityRef xml = new TCapabilityRef();
        xml.setName(canonical.getName());
        xml.setRef(convert(canonical.getRef()));
        return xml;
    }
    
    private TRequirementRef convert(org.eclipse.winery.model.tosca.TRequirementRef canonical) {
        TRequirementRef xml = new TRequirementRef();
        xml.setName(canonical.getName());
        xml.setRef(convert(canonical.getRef()));
        return xml;
    }
    
    private TRequirement convert(org.eclipse.winery.model.tosca.TRequirement canonical) {
        TRequirement.Builder builder = new TRequirement.Builder(canonical.getId(), canonical.getName(), canonical.getType());
        builder.setCapability(canonical.getCapability());
        builder.setRelationship(canonical.getRelationship());
        builder.setNode(canonical.getNode());
        fillRelationshipSourceOrTargetProperties(builder, canonical);
        return builder.build();
    }
    
    private TPropertyMapping convert(org.eclipse.winery.model.tosca.TPropertyMapping canonical) {
        TPropertyMapping xml = new TPropertyMapping();
        xml.setServiceTemplatePropertyRef(canonical.getServiceTemplatePropertyRef());
        xml.setTargetPropertyRef(canonical.getTargetPropertyRef());
        xml.setTargetObjectRef(canonical.getTargetObjectRef());
        return xml;
    }
    
    private TTopologyTemplate convert(org.eclipse.winery.model.tosca.TTopologyTemplate canonical) {
        TTopologyTemplate.Builder builder = new TTopologyTemplate.Builder();
        canonical.getNodeTemplates().stream().map(this::convert).forEach(builder::addNodeTemplates);
        canonical.getRelationshipTemplates().stream().map(this::convert).forEach(builder::addRelationshipTemplate);
        // policies, inputs and outputs from canonical are YAML-only
        fillExtensibleElementsProperties(builder, canonical);
        return builder.build();
    }
    
    private TNodeTemplate convert(org.eclipse.winery.model.tosca.TNodeTemplate canonical) {
        TNodeTemplate.Builder builder = new TNodeTemplate.Builder(canonical.getId(), canonical.getType());
        if (canonical.getRequirements() != null) {
            TNodeTemplate.Requirements reqs = new TNodeTemplate.Requirements();
            reqs.getRequirement().addAll(canonical.getRequirements().getRequirement().stream()
                .map(this::convert).collect(Collectors.toList()));
            builder.setRequirements(reqs);
        }
        if (canonical.getCapabilities() != null) {
            TNodeTemplate.Capabilities caps = new TNodeTemplate.Capabilities();
            caps.getCapability().addAll(canonical.getCapabilities().getCapability().stream()
                .map(this::convert).collect(Collectors.toList()));
            builder.setCapabilities(caps);
        }
        if (canonical.getPolicies() != null) {
            TPolicies policies = new TPolicies();
            policies.getPolicy().addAll(canonical.getPolicies().getPolicy().stream()
                .map(this::convert).collect(Collectors.toList()));
            builder.setPolicies(policies);
        }
        if (canonical.getDeploymentArtifacts() != null) {
            TDeploymentArtifacts artifacts = new TDeploymentArtifacts();
            artifacts.getDeploymentArtifact().addAll(canonical.getDeploymentArtifacts().getDeploymentArtifact().stream()
                .map(this::convert).collect(Collectors.toList()));
            builder.setDeploymentArtifacts(artifacts);
        }
        builder.setName(canonical.getName());
        builder.setMinInstances(canonical.getMinInstances());
        builder.setMaxInstances(canonical.getMaxInstances());
        builder.setX(canonical.getX());
        builder.setY(canonical.getY());
        fillRelationshipSourceOrTargetProperties(builder, canonical);
        return builder.build();
    }
    
    private TRelationshipTemplate convert(org.eclipse.winery.model.tosca.TRelationshipTemplate canonical) {
        TRelationshipTemplate.Builder builder = new TRelationshipTemplate.Builder(canonical.getId(), canonical.getType(),
            convert(canonical.getSourceElement()), convert(canonical.getTargetElement()));
        if (canonical.getRelationshipConstraints() != null) {
            TRelationshipTemplate.RelationshipConstraints constraints = new TRelationshipTemplate.RelationshipConstraints();
            constraints.getRelationshipConstraint().addAll(canonical.getRelationshipConstraints().getRelationshipConstraint().stream()
                .map(this::convert).collect(Collectors.toList()));
            builder.setRelationshipConstraints(constraints);
        }
        fillEntityTemplateProperties(builder, canonical);
        return builder.build();
    }
    
    private TRelationshipTemplate.RelationshipConstraints.RelationshipConstraint convert(org.eclipse.winery.model.tosca.TRelationshipTemplate.RelationshipConstraints.RelationshipConstraint canonical) {
        TRelationshipTemplate.RelationshipConstraints.RelationshipConstraint xml = new TRelationshipTemplate.RelationshipConstraints.RelationshipConstraint();
        xml.setAny(canonical.getAny());
        xml.setConstraintType(canonical.getConstraintType());
        return xml;
    }
    
    private TRelationshipTemplate.SourceOrTargetElement convert(org.eclipse.winery.model.tosca.TRelationshipTemplate.SourceOrTargetElement canonical) {
        TRelationshipTemplate.SourceOrTargetElement xml = new TRelationshipTemplate.SourceOrTargetElement();
        xml.setRef(convert(canonical.getRef()));
        return xml;
    }
    
    private RelationshipSourceOrTarget convert(org.eclipse.winery.model.tosca.RelationshipSourceOrTarget canonical) {
        // Capability or NodeTemplate or Requirement
        if (canonical instanceof org.eclipse.winery.model.tosca.TCapability) {
            return convert((org.eclipse.winery.model.tosca.TCapability) canonical);
        }
        if (canonical instanceof org.eclipse.winery.model.tosca.TNodeTemplate) {
            return convert((org.eclipse.winery.model.tosca.TNodeTemplate) canonical);
        }
        if (canonical instanceof org.eclipse.winery.model.tosca.TRequirement) {
            return convert((org.eclipse.winery.model.tosca.TRequirement) canonical);
        }
        throw new IllegalStateException(String.format("Tried to convert unknown RelationshipSourceOrTarget implementation %s", canonical.getClass().getName()));
    }

    private List<TServiceTemplate> convertServiceTemplates(List<org.eclipse.winery.model.tosca.TServiceTemplate> canonical) {
        return canonical.stream().map(this::convert).collect(Collectors.toList());
    }

    private List<TRelationshipType> convertRelationshipTypes(List<org.eclipse.winery.model.tosca.TRelationshipType> canonical) {
        return canonical.stream().map(this::convert).collect(Collectors.toList());
    }

    private List<TRelationshipTypeImplementation> convertRelationshipImplementations(List<org.eclipse.winery.model.tosca.TRelationshipTypeImplementation> canonical) {
        return canonical.stream().map(this::convert).collect(Collectors.toList());
    }

    private List<TPolicyType> convertPolicyTypes(List<org.eclipse.winery.model.tosca.TPolicyType> canonical) {
        return canonical.stream().map(this::convert).collect(Collectors.toList());
    }

    private List<TRequirementType> convertRequirementTypes(List<org.eclipse.winery.model.tosca.TRequirementType> canonical) {
        return canonical.stream().map(this::convert).collect(Collectors.toList());
    }

    private List<TNodeTypeImplementation> convertNodeTypeImplementations(List<org.eclipse.winery.model.tosca.TNodeTypeImplementation> canonical) {
        return canonical.stream().map(this::convert).collect(Collectors.toList());
    }

    private List<TNodeType> convertNodeTypes(List<org.eclipse.winery.model.tosca.TNodeType> canonical) {
        return canonical.stream().map(this::convert).collect(Collectors.toList());
    }

    private List<TInterfaceType> convertInterfaceTypes(List<org.eclipse.winery.model.tosca.TInterfaceType> canonical) {
        return canonical.stream().map(this::convert).collect(Collectors.toList());
    }

    private TDefinitions.Extensions convertExtensions(org.eclipse.winery.model.tosca.TDefinitions.Extensions canonical) {
        TDefinitions.Extensions result = new TDefinitions.Extensions();
        result.getExtension().addAll(canonical.getExtension().stream().map(this::convert).collect(Collectors.toList()));
        return result;
    }

    private List<TDocumentation> convertDocumentation(List<org.eclipse.winery.model.tosca.TDocumentation> canonical) {
        return canonical.stream().map(this::convert).collect(Collectors.toList());
    }

    private List<TCapabilityType> convertCapabilityTypes(List<org.eclipse.winery.model.tosca.TCapabilityType> canonical) {
        return canonical.stream().map(this::convert).collect(Collectors.toList());
    }

    private List<TArtifactType> convertArtifactTypes(List<org.eclipse.winery.model.tosca.TArtifactType> canonical) {
        return canonical.stream().map(this::convert).collect(Collectors.toList());
    }

    private List<TArtifactTemplate> convertArtifactTemplates(List<org.eclipse.winery.model.tosca.TArtifactTemplate> canonical) {
        return canonical.stream().map(this::convert).collect(Collectors.toList());
    }

    private List<TImport> convertImports(List<org.eclipse.winery.model.tosca.TImport> canonical) {
        return canonical.stream().map(this::convert).collect(Collectors.toList());
    }

    private TDefinitions.Types convertTypes(org.eclipse.winery.model.tosca.TDefinitions.@Nullable Types canonical) {
        if (canonical == null) {
            return new TDefinitions.Types();
        }
        TDefinitions.Types result = new TDefinitions.Types();
        result.getAny().addAll(canonical.getAny());
        return result;
    }
}
