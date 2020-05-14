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

import org.eclipse.winery.model.tosca.RelationshipSourceOrTarget;
import org.eclipse.winery.model.tosca.TAppliesTo;
import org.eclipse.winery.model.tosca.TArtifact;
import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TArtifactType;
import org.eclipse.winery.model.tosca.TArtifacts;
import org.eclipse.winery.model.tosca.TBoolean;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TCapabilityDefinition;
import org.eclipse.winery.model.tosca.TCapabilityRef;
import org.eclipse.winery.model.tosca.TCapabilityType;
import org.eclipse.winery.model.tosca.TCondition;
import org.eclipse.winery.model.tosca.TConstraint;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TDocumentation;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TEntityTypeImplementation;
import org.eclipse.winery.model.tosca.TExportedInterface;
import org.eclipse.winery.model.tosca.TExportedOperation;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TExtension;
import org.eclipse.winery.model.tosca.TImplementation;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TImport;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TInterfaceDefinition;
import org.eclipse.winery.model.tosca.TInterfaceType;
import org.eclipse.winery.model.tosca.TInterfaces;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TOperationDefinition;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPlans;
import org.eclipse.winery.model.tosca.TPolicies;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TPolicyType;
import org.eclipse.winery.model.tosca.TPropertyConstraint;
import org.eclipse.winery.model.tosca.TPropertyMapping;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.TRequiredContainerFeature;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TRequirementDefinition;
import org.eclipse.winery.model.tosca.TRequirementRef;
import org.eclipse.winery.model.tosca.TRequirementType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTag;
import org.eclipse.winery.model.tosca.TTopologyElementInstanceStates;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.xml.XmlRepository;

import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToCanonical {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToCanonical.class);
    private final XmlRepository repository;

    public ToCanonical(XmlRepository repository) {
        this.repository = repository;
    }

    public TDefinitions convert(org.eclipse.winery.model.tosca.xml.TDefinitions canonical) {
        return convert(canonical, false);
    }

    /**
     * Converts a canonical TDefinitions collection to XML TDefinitions. 
     */
    public TDefinitions convert(org.eclipse.winery.model.tosca.xml.TDefinitions xml, boolean convertImports) {
        // FIXME need to correctly deal with convertImports flag to create a self-contained Definitions to export as CSAR if it is set.
        TDefinitions.Builder builder = new TDefinitions.Builder(xml.getId(), xml.getTargetNamespace())
            .setImport(convertImports(xml.getImport()))
            .addTypes(convertTypes(xml.getTypes()))
            .setServiceTemplates(convertServiceTemplates(xml.getServiceTemplates()))
            .setNodeTypes(convertNodeTypes(xml.getNodeTypes()))
            .setNodeTypeImplementations(convertNodeTypeImplementations(xml.getNodeTypeImplementations()))
            .setRelationshipTypes(convertRelationshipTypes(xml.getRelationshipTypes()))
            .setRelationshipTypeImplementations(convertRelationshipImplementations(xml.getRelationshipTypeImplementations()))
            .setCapabilityTypes(convertCapabilityTypes(xml.getCapabilityTypes()))
            .setArtifactTypes(convertArtifactTypes(xml.getArtifactTypes()))
            .setArtifactTemplates(convertArtifactTemplates(xml.getArtifactTemplates()))
            .setPolicyTypes(convertPolicyTypes(xml.getPolicyTypes()))
            .setInterfaceTypes(convertInterfaceTypes(xml.getInterfaceTypes()))
            .setName(xml.getName())
            .addRequirementTypes(convertRequirementTypes(xml.getRequirementTypes()));
        fillExtensibleElementsProperties(builder, xml);
        return builder.build();
    }

    private TRelationshipType convert(org.eclipse.winery.model.tosca.xml.TRelationshipType xml) {
        TRelationshipType.Builder builder = new TRelationshipType.Builder(xml.getIdFromIdOrNameField())
            .addSourceInterfaces(convertInterfaces(xml.getSourceInterfaces()))
            .addTargetInterfaces(convertInterfaces(xml.getTargetInterfaces()));
        if (xml.getValidSource() != null) {
            builder.setValidSource(xml.getValidSource().getTypeRef());
        }
        if (xml.getValidTarget() != null) {
            builder.setValidTarget(xml.getValidTarget().getTypeRef());
        }
        return builder.build();
    }

    private List<TInterface> convertInterfaces(org.eclipse.winery.model.tosca.xml.@Nullable TInterfaces xml) {
        if (xml == null) {
            return Collections.emptyList();
        }
        return xml.getInterface().stream().map(this::convertInterface).collect(Collectors.toList());
    }

    private TInterface convertInterface(org.eclipse.winery.model.tosca.xml.TInterface xml) {
        return new TInterface.Builder(xml.getName(), convertOperations(xml.getOperation())).build();
    }

    private List<TOperation> convertOperations(List<org.eclipse.winery.model.tosca.xml.TOperation> xml) {
        return xml.stream().map(this::convert).collect(Collectors.toList());
    }

    private TOperation convert(org.eclipse.winery.model.tosca.xml.TOperation xml) {
        TOperation.Builder builder = new TOperation.Builder(xml.getName());
        if (xml.getInputParameters() != null) {
            builder.addInputParameters(xml.getInputParameters().getInputParameter().stream()
                .map(this::convert).collect(Collectors.toList()));
        }
        if (xml.getOutputParameters() != null) {
            builder.addOutputParameters(xml.getOutputParameters().getOutputParameter().stream()
                .map(this::convert).collect(Collectors.toList()));
        }
        return builder.build();
    }

    private TParameter convert(org.eclipse.winery.model.tosca.xml.TParameter xml) {
        return new TParameter.Builder(xml.getName(), xml.getType(), TBoolean.fromValue(xml.getRequired().value())).build();
    }

    private TRelationshipTypeImplementation convert(org.eclipse.winery.model.tosca.xml.TRelationshipTypeImplementation xml) {
        TRelationshipTypeImplementation.Builder builder = new TRelationshipTypeImplementation.Builder(xml.getName(), xml.getRelationshipType());
        fillEntityTypeImplementationProperties(builder, xml);
        if (xml.getDerivedFrom() != null) {
            TRelationshipTypeImplementation.DerivedFrom derived = new TRelationshipTypeImplementation.DerivedFrom();
            derived.setRelationshipTypeImplementationRef(xml.getDerivedFrom().getRelationshipTypeImplementationRef());
            builder.setDerivedFrom(derived);
        }
        return builder.build();
    }

    private <Builder extends TEntityTypeImplementation.Builder<?>, Value extends org.eclipse.winery.model.tosca.xml.TEntityTypeImplementation>
    void fillEntityTypeImplementationProperties(Builder builder, Value xml) {
        if (xml.getRequiredContainerFeatures() != null) {
            builder.addRequiredContainerFeatures(xml.getRequiredContainerFeatures().getRequiredContainerFeature()
                .stream().map(this::convert).collect(Collectors.toList()));
        }
        if (xml.getTags() != null) {
            builder.addTags(xml.getTags().getTag().stream().map(this::convert).collect(Collectors.toList()));
        }
        if (xml.getImplementationArtifacts() != null) {
            builder.addImplementationArtifacts(xml.getImplementationArtifacts().getImplementationArtifact().stream()
                .map(this::convert).collect(Collectors.toList()));
        }
        builder.setTargetNamespace(xml.getTargetNamespace());
        builder.setAbstract(TBoolean.fromValue(xml.getAbstract().value()));
        builder.setFinal(TBoolean.fromValue(xml.getFinal().value()));
        fillExtensibleElementsProperties(builder, xml);
    }

    private TImplementationArtifacts.ImplementationArtifact convert(org.eclipse.winery.model.tosca.xml.TImplementationArtifacts.ImplementationArtifact xml) {
        return new TImplementationArtifacts.ImplementationArtifact.Builder(xml.getArtifactType())
            .setName(xml.getName())
            .setInterfaceName(xml.getInterfaceName())
            .setOperationName(xml.getOperationName())
            .setArtifactRef(xml.getArtifactRef())
            .build();
    }

    private TTag convert(org.eclipse.winery.model.tosca.xml.TTag xml) {
        return new TTag.Builder().setName(xml.getName()).setValue(xml.getValue()).build();
    }

    private TRequiredContainerFeature convert(org.eclipse.winery.model.tosca.xml.TRequiredContainerFeature xml) {
        TRequiredContainerFeature result = new TRequiredContainerFeature();
        result.setFeature(xml.getFeature());
        return result;
    }

    private TPolicyType convert(org.eclipse.winery.model.tosca.xml.TPolicyType xml) {
        TPolicyType.Builder builder = new TPolicyType.Builder(xml.getName());
        if (xml.getAppliesTo() != null) {
            TAppliesTo appliesTo = new TAppliesTo();
            appliesTo.getNodeTypeReference().addAll(xml.getAppliesTo().getNodeTypeReference().stream()
                .map(c -> {
                    TAppliesTo.NodeTypeReference result = new TAppliesTo.NodeTypeReference();
                    result.setTypeRef(c.getTypeRef());
                    return result;
                })
                .collect(Collectors.toList()));
            builder.setAppliesTo(appliesTo);
        }
        fillEntityTypeProperties(builder, xml);
        return builder.build();
    }

    private <Builder extends TEntityType.Builder, Value extends org.eclipse.winery.model.tosca.xml.TEntityType>
    void fillEntityTypeProperties(Builder builder, Value xml) {
        if (xml.getTags() != null) {
            builder.addTags(xml.getTags().getTag().stream().map(this::convert).collect(Collectors.toList()));
        }
        if (xml.getDerivedFrom() != null) {
            TEntityType.DerivedFrom derived = new TEntityType.DerivedFrom();
            derived.setTypeRef(xml.getDerivedFrom().getTypeRef());
            builder.setDerivedFrom(derived);
        }
        if (xml.getPropertiesDefinition() != null) {
            TEntityType.XmlPropertiesDefinition propertiesDefinition = new TEntityType.XmlPropertiesDefinition();
            propertiesDefinition.setElement(xml.getPropertiesDefinition().getElement());
            propertiesDefinition.setType(xml.getPropertiesDefinition().getType());
            builder.setPropertiesDefinition(propertiesDefinition);
        }
        builder.setAbstract(TBoolean.fromValue(xml.getAbstract().value()));
        builder.setFinal(TBoolean.fromValue(xml.getFinal().value()));
        builder.setTargetNamespace(xml.getTargetNamespace());
        fillExtensibleElementsProperties(builder, xml);
    }

    private <Builder extends TExtensibleElements.Builder, Value extends org.eclipse.winery.model.tosca.xml.TExtensibleElements>
    void fillExtensibleElementsProperties(Builder builder, Value xml) {
        builder.setDocumentation(xml.getDocumentation().stream().map(this::convert).collect(Collectors.toList()));
        builder.setOtherAttributes(xml.getOtherAttributes());
        builder.setAny(xml.getAny());
    }

    private TRequirementType convert(org.eclipse.winery.model.tosca.xml.TRequirementType xml) {
        TRequirementType.Builder builder = new TRequirementType.Builder(xml.getName());
        builder.setRequiredCapabilityType(xml.getRequiredCapabilityType());
        fillEntityTypeProperties(builder, xml);
        return builder.build();
    }

    private TNodeTypeImplementation convert(org.eclipse.winery.model.tosca.xml.TNodeTypeImplementation xml) {
        TNodeTypeImplementation.Builder builder = new TNodeTypeImplementation.Builder(xml.getName(), xml.getNodeType());
        if (xml.getDeploymentArtifacts() != null) {
            TDeploymentArtifacts artifacts = new TDeploymentArtifacts.Builder(xml.getDeploymentArtifacts()
                .getDeploymentArtifact().stream().map(this::convert).collect(Collectors.toList())).build();
            builder.setDeploymentArtifacts(artifacts);
        }
        if (xml.getDerivedFrom() != null) {
            TNodeTypeImplementation.DerivedFrom derived = new TNodeTypeImplementation.DerivedFrom();
            derived.setNodeTypeImplementationRef(xml.getDerivedFrom().getNodeTypeImplementationRef());
            builder.setDerivedFrom(derived);
        }
        fillEntityTypeImplementationProperties(builder, xml);
        return builder.build();
    }

    private TDeploymentArtifact convert(org.eclipse.winery.model.tosca.xml.TDeploymentArtifact xml) {
        TDeploymentArtifact.Builder builder = new TDeploymentArtifact.Builder(xml.getName(), xml.getArtifactType());
        builder.setArtifactRef(xml.getArtifactRef());
        fillExtensibleElementsProperties(builder, xml);
        return builder.build();
    }

    private TNodeType convert(org.eclipse.winery.model.tosca.xml.TNodeType xml) {
        TNodeType.Builder builder = new TNodeType.Builder(xml.getName());
        if (xml.getRequirementDefinitions() != null) {
            TNodeType.RequirementDefinitions reqDefs = new TNodeType.RequirementDefinitions();
            reqDefs.getRequirementDefinition().addAll(xml.getRequirementDefinitions().getRequirementDefinition()
                .stream().map(this::convert).collect(Collectors.toList()));
            builder.setRequirementDefinitions(reqDefs);
        }
        if (xml.getCapabilityDefinitions() != null) {
            TNodeType.CapabilityDefinitions capDefs = new TNodeType.CapabilityDefinitions();
            capDefs.getCapabilityDefinition().addAll(xml.getCapabilityDefinitions().getCapabilityDefinition()
                .stream().map(this::convert).collect(Collectors.toList()));
            builder.setCapabilityDefinitions(capDefs);
        }
        if (xml.getInstanceStates() != null) {
            TTopologyElementInstanceStates instanceStates = new TTopologyElementInstanceStates();
            instanceStates.getInstanceState().addAll(xml.getInstanceStates().getInstanceState().stream()
                .map(c -> {
                    TTopologyElementInstanceStates.InstanceState r = new TTopologyElementInstanceStates.InstanceState();
                    r.setState(c.getState());
                    return r;
                }).collect(Collectors.toList()));
            builder.setInstanceStates(instanceStates);
        }
        if (xml.getInterfaces() != null) {
            TInterfaces ifaces = new TInterfaces();
            ifaces.getInterface().addAll(convertInterfaces(xml.getInterfaces()));
            builder.setInterfaces(ifaces);
        }
        if (xml.getInterfaceDefinitions() != null) {
            builder.setInterfaceDefinitions(xml.getInterfaceDefinitions().stream()
                .map(this::convert).collect(Collectors.toList()));
        }
        if (xml.getArtifacts() != null) {
            TArtifacts artifacts = new TArtifacts();
            artifacts.getArtifact().addAll(xml.getArtifacts().getArtifact().stream()
                .map(this::convert).collect(Collectors.toList()));
            builder.setArtifacts(artifacts);
        }
        fillEntityTypeProperties(builder, xml);
        return builder.build();
    }

    private TInterfaceDefinition convert(org.eclipse.winery.model.tosca.xml.TInterfaceDefinition xml) {
        TInterfaceDefinition definition = new TInterfaceDefinition();
        definition.setName(xml.getName());
        definition.setType(xml.getType());
        definition.setInputs(xml.getInputs());
//        definition.setInputs(canonical.getInputs().stream().map(this::convert).collect(Collectors.toList()));
        definition.setOperations(xml.getOperations().stream().map(this::convert).collect(Collectors.toList()));
        return definition;
    }

    private TOperationDefinition convert(org.eclipse.winery.model.tosca.xml.TOperationDefinition xml) {
        TOperationDefinition definition = new TOperationDefinition();
        definition.setName(xml.getName());
        definition.setDescription(xml.getDescription());
        definition.setInputs(xml.getInputs());
        definition.setOutputs(xml.getOutputs());
        definition.setImplementation(convert(xml.getImplementation()));
        return definition;
    }

    private TImplementation convert(org.eclipse.winery.model.tosca.xml.TImplementation xml) {
        TImplementation definition = new TImplementation();
        definition.setPrimary(xml.getPrimary());
        definition.setDependencies(xml.getDependencies());
        definition.setOperationHost(xml.getOperationHost());
        definition.setTimeout(xml.getTimeout());
        return definition;
    }

    private TArtifact convert(org.eclipse.winery.model.tosca.xml.TArtifact xml) {
        TArtifact.Builder builder = new TArtifact.Builder(xml.getName(), xml.getType());
        builder.setDeployPath(xml.getDeployPath());
        builder.setDescription(xml.getDescription());
        builder.setFile(xml.getFile());
        fillEntityTemplateProperties(builder, xml);
        return builder.build();
    }

    private TRequirementDefinition convert(org.eclipse.winery.model.tosca.xml.TRequirementDefinition xml) {
        // requirementType can be null in the canonical model because YAML mode doesn't use it.
        //  it's required for us, though, so we just assume it's present
        TRequirementDefinition.Builder builder = new TRequirementDefinition.Builder(xml.getName(), xml.getRequirementType());
        if (xml.getConstraints() != null) {
            TRequirementDefinition.Constraints constraints = new TRequirementDefinition.Constraints();
            constraints.getConstraint().addAll(xml.getConstraints().getConstraint().stream()
                .map(this::convert).collect(Collectors.toList()));
            builder.setConstraints(constraints);
        }
        builder.setLowerBound(xml.getLowerBound());
        builder.setUpperBound(xml.getUpperBound());
        // FIXME capability, node and relationship are YAML things, they should not be moved around here
        builder.setCapability(xml.getCapability());
        builder.setNode(xml.getNode());
        builder.setRelationship(xml.getRelationship());
        fillExtensibleElementsProperties(builder, xml);
        return builder.build();
    }

    private TCapabilityDefinition convert(org.eclipse.winery.model.tosca.xml.TCapabilityDefinition xml) {
        TCapabilityDefinition.Builder builder = new TCapabilityDefinition.Builder(xml.getName(), xml.getCapabilityType());
        if (xml.getConstraints() != null) {
            xml.getConstraints().getConstraint()
                .stream().map(this::convert)
                .forEach(builder::addConstraints);
        }
        builder.setLowerBound(xml.getLowerBound());
        builder.setUpperBound(xml.getUpperBound());
        builder.setValidSourceTypes(xml.getValidSourceTypes());
        fillExtensibleElementsProperties(builder, xml);
        return builder.build();
    }

    private TConstraint convert(org.eclipse.winery.model.tosca.xml.TConstraint xml) {
        TConstraint constraint = new TConstraint();
        constraint.setAny(xml.getAny());
        constraint.setConstraintType(xml.getConstraintType());
        return constraint;
    }

    private TCapability convert(org.eclipse.winery.model.tosca.xml.TCapability xml) {
        TCapability.Builder builder = new TCapability.Builder(xml.getId(), xml.getType(), xml.getName());
        fillRelationshipSourceOrTargetProperties(builder, xml);
        return builder.build();
    }

    private <Builder extends RelationshipSourceOrTarget.Builder, Value extends org.eclipse.winery.model.tosca.xml.RelationshipSourceOrTarget>
    void fillRelationshipSourceOrTargetProperties(Builder builder, Value xml) {
        // no specific properties to fill, just traverse the hierarchy
        fillEntityTemplateProperties(builder, xml);
    }

    private <Builder extends TEntityTemplate.Builder, Value extends org.eclipse.winery.model.tosca.xml.TEntityTemplate>
    void fillEntityTemplateProperties(Builder builder, Value xml) {
        if (xml.getProperties() != null) {
            builder.setProperties(convertProperties(xml.getProperties()));
        }
        if (xml.getPropertyConstraints() != null) {
            TEntityTemplate.PropertyConstraints constraints = new TEntityTemplate.PropertyConstraints();
            constraints.getPropertyConstraint().addAll(xml.getPropertyConstraints().getPropertyConstraint().stream()
                .map(this::convert).collect(Collectors.toList()));
            builder.setPropertyConstraints(constraints);
        }
    }

    private TEntityTemplate.Properties convertProperties(org.eclipse.winery.model.tosca.xml.TEntityTemplate.Properties xml) {
        TEntityTemplate.Properties props = new TEntityTemplate.Properties();
        if (xml.getAny() != null) {
            props.setAny(props.getAny());
        } else if (xml.getKVProperties() != null) {
            // XML kvProperties are guaranteed to be <String, String>
            //  the type erasure of that conforms to Map<String, Object> but the compiler doesn't believe that
            //  which is why we hack around that with a cast to the rawtype
            props.setKVProperties((Map)xml.getKVProperties());
        }
        return props;
    }

    private TPropertyConstraint convert(org.eclipse.winery.model.tosca.xml.TPropertyConstraint xml) {
        TPropertyConstraint constraint = new TPropertyConstraint();
        constraint.setAny(xml.getAny());
        constraint.setConstraintType(xml.getConstraintType());
        constraint.setProperty(xml.getProperty());
        return constraint;
    }

    private TInterfaceType convert(org.eclipse.winery.model.tosca.xml.TInterfaceType xml) {
        // FIXME the interface type is a YAML-model thing. It shouldn't be converted to XML like this!
        TInterfaceType.Builder builder = new TInterfaceType.Builder(xml.getName());
        builder.setDescription(xml.getDescription());
        builder.setOperations(xml.getOperations().entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> convert(e.getValue()))));
        fillEntityTypeProperties(builder, xml);
        return builder.build();
    }

    private TExtension convert(org.eclipse.winery.model.tosca.xml.TExtension xml) {
        TExtension.Builder builder = new TExtension.Builder(xml.getNamespace());
        builder.setMustUnderstand(TBoolean.fromValue(xml.getMustUnderstand().value()));
        fillExtensibleElementsProperties(builder, xml);
        return builder.build();
    }

    private TDocumentation convert(org.eclipse.winery.model.tosca.xml.TDocumentation xml) {
        TDocumentation canonical = new TDocumentation();
        canonical.getContent().addAll(xml.getContent());
        canonical.setSource(xml.getSource());
        canonical.setLang(xml.getLang());
        return canonical;
    }

    private TCapabilityType convert(org.eclipse.winery.model.tosca.xml.TCapabilityType xml) {
        TCapabilityType.Builder builder = new TCapabilityType.Builder(xml.getName());
        // FIXME validSourceTypes are apparently a YAML feature (again)
        builder.setValidSourceTypes(xml.getValidNodeTypes());
        fillEntityTypeProperties(builder, xml);
        return builder.build();
    }

    private TArtifactType convert(org.eclipse.winery.model.tosca.xml.TArtifactType xml) {
        TArtifactType.Builder builder = new TArtifactType.Builder(xml.getName());
        builder.setMimeType(xml.getMimeType());
        builder.setFileExtensions(xml.getFileExtensions());
        fillEntityTypeProperties(builder, xml);
        return builder.build();
    }

    private TArtifactTemplate convert(org.eclipse.winery.model.tosca.xml.TArtifactTemplate xml) {
        TArtifactTemplate.Builder builder = new TArtifactTemplate.Builder(xml.getName(), xml.getType());
        if (xml.getArtifactReferences() != null) {
            xml.getArtifactReferences().getArtifactReference().stream()
                .map(this::convert)
                .forEach(builder::addArtifactReferences);
        }
        fillEntityTemplateProperties(builder, xml);
        return builder.build();
    }

    private TArtifactReference convert(org.eclipse.winery.model.tosca.xml.TArtifactReference xml) {
        TArtifactReference canonical = new TArtifactReference();
        // FIXME because all of this is a mess, the includes and excludes are stored as Include and Exclude in the same field
//        xml.getIncludeOrExclude().add(canonical.getIncludeOrExclude());
        canonical.setReference(xml.getReference());
        return canonical;
    }

    private TImport convert(org.eclipse.winery.model.tosca.xml.TImport xml) {
        TImport.Builder builder = new TImport.Builder(xml.getImportType());
        builder.setNamespace(xml.getNamespace());
        builder.setLocation(xml.getLocation());
        fillExtensibleElementsProperties(builder, xml);
        return builder.build();
    }

    private TServiceTemplate convert(org.eclipse.winery.model.tosca.xml.TServiceTemplate xml) {
        TServiceTemplate.Builder builder = new TServiceTemplate.Builder(xml.getId(), convert(xml.getTopologyTemplate()));
        if (xml.getTags() != null) {
            xml.getTags().getTag().stream().map(this::convert).forEach(builder::addTags);
        }
        if (xml.getBoundaryDefinitions() != null) {
            builder.setBoundaryDefinitions(convert(xml.getBoundaryDefinitions()));
        }
        if (xml.getPlans() != null) {
            TPlans plans = new TPlans();
            plans.setTargetNamespace(xml.getPlans().getTargetNamespace());
            plans.getPlan().addAll(xml.getPlans().getPlan().stream().map(this::convert).collect(Collectors.toList()));
        }
        builder.setSubstitutableNodeType(xml.getSubstitutableNodeType());
        fillExtensibleElementsProperties(builder, xml);
        return builder.build();
    }

    private TPlan convert(org.eclipse.winery.model.tosca.xml.TPlan xml) {
        TPlan.Builder builder = new TPlan.Builder(xml.getId(), xml.getPlanType(), xml.getPlanLanguage());
        if (xml.getPrecondition() != null) {
            builder.setPrecondition(convert(xml.getPrecondition()));
        }
        if (xml.getInputParameters() != null) {
            TPlan.InputParameters inputs = new TPlan.InputParameters();
            inputs.getInputParameter().addAll(xml.getInputParameters().getInputParameter().stream().map(this::convert).collect(Collectors.toList()));
            builder.setInputParameters(inputs);
        }
        if (xml.getOutputParameters() != null) {
            TPlan.OutputParameters outputs = new TPlan.OutputParameters();
            outputs.getOutputParameter().addAll(xml.getOutputParameters().getOutputParameter().stream().map(this::convert).collect(Collectors.toList()));
            builder.setOutputParameters(outputs);
        }
        if (xml.getPlanModel() != null) {
            TPlan.PlanModel model = new TPlan.PlanModel();
            model.setAny(xml.getAny());
            builder.setPlanModel(model);
        }
        if (xml.getPlanModelReference() != null) {
            TPlan.PlanModelReference ref = new TPlan.PlanModelReference();
            ref.setReference(xml.getPlanModelReference().getReference());
            builder.setPlanModelReference(ref);
        }
        builder.setName(xml.getName());
        fillExtensibleElementsProperties(builder, xml);
        return builder.build();
    }

    private TCondition convert(org.eclipse.winery.model.tosca.xml.TCondition xml) {
        TCondition xml = new TCondition();
        xml.setExpressionLanguage(xml.getExpressionLanguage());
        xml.getAny().addAll(xml.getAny());
        return xml;
    }

    private TBoundaryDefinitions convert(org.eclipse.winery.model.tosca.xml.TBoundaryDefinitions xml) {
        TBoundaryDefinitions.Builder builder = new TBoundaryDefinitions.Builder();
        if (xml.getProperties() != null) {
            TBoundaryDefinitions.Properties props = new TBoundaryDefinitions.Properties();
            props.setAny(xml.getProperties().getAny());
            if (xml.getProperties().getPropertyMappings() != null) {
                TBoundaryDefinitions.Properties.PropertyMappings mappings = new TBoundaryDefinitions.Properties.PropertyMappings();
                mappings.getPropertyMapping().addAll(xml.getProperties().getPropertyMappings().getPropertyMapping().stream()
                    .map(this::convert).collect(Collectors.toList()));
                props.setPropertyMappings(mappings);
            }
            builder.setProperties(props);
        }
        if (xml.getRequirements() != null) {
            TBoundaryDefinitions.Requirements reqs = new TBoundaryDefinitions.Requirements();
            reqs.getRequirement().addAll(xml.getRequirements().getRequirement().stream()
                .map(this::convert).collect(Collectors.toList()));
            builder.setRequirements(reqs);
        }
        if (xml.getCapabilities() != null) {
            TBoundaryDefinitions.Capabilities caps = new TBoundaryDefinitions.Capabilities();
            caps.getCapability().addAll(xml.getCapabilities().getCapability().stream()
                .map(this::convert).collect(Collectors.toList()));
            builder.setCapabilities(caps);
        }
        if (xml.getPolicies() != null) {
            TPolicies policies = new TPolicies();
            policies.getPolicy().addAll(xml.getPolicies().getPolicy().stream().map(this::convert).collect(Collectors.toList()));
            builder.setPolicies(policies);
        }
        if (xml.getInterfaces() != null) {
            TBoundaryDefinitions.Interfaces ifaces = new TBoundaryDefinitions.Interfaces();
            ifaces.getInterface().addAll(xml.getInterfaces().getInterface().stream().map(this::convert).collect(Collectors.toList()));
            builder.setInterfaces(ifaces);
        }
        return builder.build();
    }

    private TExportedInterface convert(org.eclipse.winery.model.tosca.xml.TExportedInterface xml) {
        TExportedInterface iface = new TExportedInterface();
        iface.setName(xml.getName());
        iface.getOperation().addAll(xml.getOperation().stream().map(this::convert).collect(Collectors.toList()));
        return iface;
    }

    private TExportedOperation convert(org.eclipse.winery.model.tosca.xml.TExportedOperation xml) {
        TExportedOperation xml = new TExportedOperation();
        xml.setName(xml.getName());
        if (xml.getNodeOperation() != null) {
            xml.setNodeOperation(convert(xml.getNodeOperation()));
        }
        if (xml.getRelationshipOperation() != null) {
            xml.setRelationshipOperation(convert(xml.getRelationshipOperation()));
        }
        if (xml.getPlan() != null) {
            TExportedOperation.Plan plan = new TExportedOperation.Plan();
            plan.setPlanRef(xml.getPlan().getPlanRef());
            xml.setPlan(plan);
        }
        return xml;
    }

    private TExportedOperation.RelationshipOperation convert(org.eclipse.winery.model.tosca.xml.TExportedOperation.RelationshipOperation xml) {
        TExportedOperation.RelationshipOperation canonical = new TExportedOperation.RelationshipOperation();
        canonical.setRelationshipRef(xml.getRelationshipRef());
        canonical.setInterfaceName(xml.getInterfaceName());
        canonical.setOperationName(xml.getOperationName());
        return canonical;
    }

    private TExportedOperation.NodeOperation convert(org.eclipse.winery.model.tosca.xml.TExportedOperation.NodeOperation xml) {
        TExportedOperation.NodeOperation canonical = new TExportedOperation.NodeOperation();
        canonical.setNodeRef(xml.getNodeRef());
        canonical.setInterfaceName(xml.getInterfaceName());
        canonical.setOperationName(xml.getOperationName());
        return canonical;
    }

    private TPolicy convert(org.eclipse.winery.model.tosca.xml.TPolicy xml) {
        TPolicy.Builder builder =  new TPolicy.Builder(xml.getPolicyType());
        builder.setName(xml.getName());
        builder.setPolicyRef(xml.getPolicyRef());
        builder.setTargets(xml.getTargets());
        builder.setProperties(convertProperties(xml.getProperties()));
        return builder.build();
    }

    private TCapabilityRef convert(org.eclipse.winery.model.tosca.xml.TCapabilityRef xml) {
        TCapabilityRef canonical = new TCapabilityRef();
        canonical.setName(xml.getName());
        canonical.setRef(convert(xml.getRef()));
        return canonical;
    }

    private TRequirementRef convert(org.eclipse.winery.model.tosca.xml.TRequirementRef xml) {
        TRequirementRef canonical = new TRequirementRef();
        canonical.setName(xml.getName());
        canonical.setRef(convert(xml.getRef()));
        return canonical;
    }

    private TRequirement convert(org.eclipse.winery.model.tosca.xml.TRequirement xml) {
        TRequirement.Builder builder = new TRequirement.Builder(xml.getId(), xml.getName(), xml.getType());
        builder.setCapability(xml.getCapability());
        builder.setRelationship(xml.getRelationship());
        builder.setNode(xml.getNode());
        fillRelationshipSourceOrTargetProperties(builder, xml);
        return builder.build();
    }

    private TPropertyMapping convert(org.eclipse.winery.model.tosca.xml.TPropertyMapping xml) {
        TPropertyMapping canonical = new TPropertyMapping();
        canonical.setServiceTemplatePropertyRef(xml.getServiceTemplatePropertyRef());
        canonical.setTargetPropertyRef(xml.getTargetPropertyRef());
        canonical.setTargetObjectRef(xml.getTargetObjectRef());
        return canonical;
    }

    private TTopologyTemplate convert(org.eclipse.winery.model.tosca.xml.TTopologyTemplate xml) {
        TTopologyTemplate.Builder builder = new TTopologyTemplate.Builder();
        xml.getNodeTemplates().stream().map(this::convert).forEach(builder::addNodeTemplates);
        xml.getRelationshipTemplates().stream().map(this::convert).forEach(builder::addRelationshipTemplate);
        // policies, inputs and outputs from canonical are YAML-only
        fillExtensibleElementsProperties(builder, xml);
        return builder.build();
    }

    private TNodeTemplate convert(org.eclipse.winery.model.tosca.xml.TNodeTemplate xml) {
        TNodeTemplate.Builder builder = new TNodeTemplate.Builder(xml.getId(), xml.getType());
        if (xml.getRequirements() != null) {
            TNodeTemplate.Requirements reqs = new TNodeTemplate.Requirements();
            reqs.getRequirement().addAll(xml.getRequirements().getRequirement().stream()
                .map(this::convert).collect(Collectors.toList()));
            builder.setRequirements(reqs);
        }
        if (xml.getCapabilities() != null) {
            TNodeTemplate.Capabilities caps = new TNodeTemplate.Capabilities();
            caps.getCapability().addAll(xml.getCapabilities().getCapability().stream()
                .map(this::convert).collect(Collectors.toList()));
            builder.setCapabilities(caps);
        }
        if (xml.getPolicies() != null) {
            TPolicies policies = new TPolicies();
            policies.getPolicy().addAll(xml.getPolicies().getPolicy().stream()
                .map(this::convert).collect(Collectors.toList()));
            builder.setPolicies(policies);
        }
        if (xml.getDeploymentArtifacts() != null) {
            TDeploymentArtifacts artifacts = new TDeploymentArtifacts();
            artifacts.getDeploymentArtifact().addAll(xml.getDeploymentArtifacts().getDeploymentArtifact().stream()
                .map(this::convert).collect(Collectors.toList()));
            builder.setDeploymentArtifacts(artifacts);
        }
        builder.setName(xml.getName());
        builder.setMinInstances(xml.getMinInstances());
        builder.setMaxInstances(xml.getMaxInstances());
        builder.setX(xml.getX());
        builder.setY(xml.getY());
        fillRelationshipSourceOrTargetProperties(builder, xml);
        return builder.build();
    }

    private TRelationshipTemplate convert(org.eclipse.winery.model.tosca.xml.TRelationshipTemplate xml) {
        TRelationshipTemplate.Builder builder = new TRelationshipTemplate.Builder(xml.getId(), xml.getType(),
            convert(xml.getSourceElement()), convert(xml.getTargetElement()));
        if (xml.getRelationshipConstraints() != null) {
            TRelationshipTemplate.RelationshipConstraints constraints = new TRelationshipTemplate.RelationshipConstraints();
            constraints.getRelationshipConstraint().addAll(xml.getRelationshipConstraints().getRelationshipConstraint().stream()
                .map(this::convert).collect(Collectors.toList()));
            builder.setRelationshipConstraints(constraints);
        }
        fillEntityTemplateProperties(builder, xml);
        return builder.build();
    }

    private TRelationshipTemplate.RelationshipConstraints.RelationshipConstraint convert(org.eclipse.winery.model.tosca.xml.TRelationshipTemplate.RelationshipConstraints.RelationshipConstraint xml) {
        TRelationshipTemplate.RelationshipConstraints.RelationshipConstraint canonical = new TRelationshipTemplate.RelationshipConstraints.RelationshipConstraint();
        canonical.setAny(xml.getAny());
        canonical.setConstraintType(xml.getConstraintType());
        return canonical;
    }

    private TRelationshipTemplate.SourceOrTargetElement convert(org.eclipse.winery.model.tosca.xml.TRelationshipTemplate.SourceOrTargetElement xml) {
        TRelationshipTemplate.SourceOrTargetElement canonical = new TRelationshipTemplate.SourceOrTargetElement();
        canonical.setRef(convert(xml.getRef()));
        return canonical;
    }

    private RelationshipSourceOrTarget convert(org.eclipse.winery.model.tosca.xml.RelationshipSourceOrTarget xml) {
        // Capability or NodeTemplate or Requirement
        if (xml instanceof org.eclipse.winery.model.tosca.xml.TCapability) {
            return convert((org.eclipse.winery.model.tosca.xml.TCapability) xml);
        }
        if (xml instanceof org.eclipse.winery.model.tosca.xml.TNodeTemplate) {
            return convert((org.eclipse.winery.model.tosca.xml.TNodeTemplate) xml);
        }
        if (xml instanceof org.eclipse.winery.model.tosca.xml.TRequirement) {
            return convert((org.eclipse.winery.model.tosca.xml.TRequirement) xml);
        }
        throw new IllegalStateException(String.format("Tried to convert unknown RelationshipSourceOrTarget implementation %s", xml.getClass().getName()));
    }

    private List<TServiceTemplate> convertServiceTemplates(List<org.eclipse.winery.model.tosca.xml.TServiceTemplate> xml) {
        return xml.stream().map(this::convert).collect(Collectors.toList());
    }

    private List<TRelationshipType> convertRelationshipTypes(List<org.eclipse.winery.model.tosca.xml.TRelationshipType> xml) {
        return xml.stream().map(this::convert).collect(Collectors.toList());
    }

    private List<TRelationshipTypeImplementation> convertRelationshipImplementations(List<org.eclipse.winery.model.tosca.xml.TRelationshipTypeImplementation> xml) {
        return xml.stream().map(this::convert).collect(Collectors.toList());
    }

    private List<TPolicyType> convertPolicyTypes(List<org.eclipse.winery.model.tosca.xml.TPolicyType> xml) {
        return xml.stream().map(this::convert).collect(Collectors.toList());
    }

    private List<TRequirementType> convertRequirementTypes(List<org.eclipse.winery.model.tosca.xml.TRequirementType> xml) {
        return xml.stream().map(this::convert).collect(Collectors.toList());
    }

    private List<TNodeTypeImplementation> convertNodeTypeImplementations(List<org.eclipse.winery.model.tosca.xml.TNodeTypeImplementation> xml) {
        return xml.stream().map(this::convert).collect(Collectors.toList());
    }

    private List<TNodeType> convertNodeTypes(List<org.eclipse.winery.model.tosca.xml.TNodeType> xml) {
        return xml.stream().map(this::convert).collect(Collectors.toList());
    }

    private List<TInterfaceType> convertInterfaceTypes(List<org.eclipse.winery.model.tosca.xml.TInterfaceType> xml) {
        return xml.stream().map(this::convert).collect(Collectors.toList());
    }

    private List<TCapabilityType> convertCapabilityTypes(List<org.eclipse.winery.model.tosca.xml.TCapabilityType> xml) {
        return xml.stream().map(this::convert).collect(Collectors.toList());
    }

    private List<TArtifactType> convertArtifactTypes(List<org.eclipse.winery.model.tosca.xml.TArtifactType> xml) {
        return xml.stream().map(this::convert).collect(Collectors.toList());
    }

    private List<TArtifactTemplate> convertArtifactTemplates(List<org.eclipse.winery.model.tosca.xml.TArtifactTemplate> xml) {
        return xml.stream().map(this::convert).collect(Collectors.toList());
    }

    private List<TImport> convertImports(List<org.eclipse.winery.model.tosca.xml.TImport> xml) {
        return xml.stream().map(this::convert).collect(Collectors.toList());
    }

    private TDefinitions.Types convertTypes(org.eclipse.winery.model.tosca.xml.TDefinitions.@Nullable Types xml) {
        if (xml == null) {
            return new TDefinitions.Types();
        }
        TDefinitions.Types result = new TDefinitions.Types();
        result.getAny().addAll(xml.getAny());
        return result;
    }
}
