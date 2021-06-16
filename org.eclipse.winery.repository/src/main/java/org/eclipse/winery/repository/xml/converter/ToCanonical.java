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
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.HasId;
import org.eclipse.winery.model.tosca.RelationshipSourceOrTarget;
import org.eclipse.winery.model.tosca.TAppliesTo;
import org.eclipse.winery.model.tosca.TArtifact;
import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TArtifactType;
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
import org.eclipse.winery.model.tosca.TGroupDefinition;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TImport;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TInterfaces;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPlans;
import org.eclipse.winery.model.tosca.TPolicies;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
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
import org.eclipse.winery.model.tosca.extensions.OTAttributeMapping;
import org.eclipse.winery.model.tosca.extensions.OTAttributeMappingType;
import org.eclipse.winery.model.tosca.extensions.OTBehaviorPatternMapping;
import org.eclipse.winery.model.tosca.extensions.OTComplianceRule;
import org.eclipse.winery.model.tosca.extensions.OTDeploymentArtifactMapping;
import org.eclipse.winery.model.tosca.extensions.OTParticipant;
import org.eclipse.winery.model.tosca.extensions.OTPatternRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTPermutationMapping;
import org.eclipse.winery.model.tosca.extensions.OTPrmMapping;
import org.eclipse.winery.model.tosca.extensions.OTRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTRelationDirection;
import org.eclipse.winery.model.tosca.extensions.OTRelationMapping;
import org.eclipse.winery.model.tosca.extensions.OTStayMapping;
import org.eclipse.winery.model.tosca.extensions.OTStringList;
import org.eclipse.winery.model.tosca.extensions.OTTestRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTTopologyFragmentRefinementModel;
import org.eclipse.winery.model.tosca.extensions.kvproperties.OTPropertyKV;
import org.eclipse.winery.model.tosca.extensions.kvproperties.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.xml.XHasId;
import org.eclipse.winery.model.tosca.xml.XRelationshipSourceOrTarget;
import org.eclipse.winery.model.tosca.xml.XTArtifact;
import org.eclipse.winery.model.tosca.xml.XTArtifactReference;
import org.eclipse.winery.model.tosca.xml.XTArtifactTemplate;
import org.eclipse.winery.model.tosca.xml.XTArtifactType;
import org.eclipse.winery.model.tosca.xml.XTBoolean;
import org.eclipse.winery.model.tosca.xml.XTBoundaryDefinitions;
import org.eclipse.winery.model.tosca.xml.XTCapability;
import org.eclipse.winery.model.tosca.xml.XTCapabilityDefinition;
import org.eclipse.winery.model.tosca.xml.XTCapabilityRef;
import org.eclipse.winery.model.tosca.xml.XTCapabilityType;
import org.eclipse.winery.model.tosca.xml.XTCondition;
import org.eclipse.winery.model.tosca.xml.XTConstraint;
import org.eclipse.winery.model.tosca.xml.XTDefinitions;
import org.eclipse.winery.model.tosca.xml.XTDeploymentArtifact;
import org.eclipse.winery.model.tosca.xml.XTDocumentation;
import org.eclipse.winery.model.tosca.xml.XTEntityTemplate;
import org.eclipse.winery.model.tosca.xml.XTEntityType;
import org.eclipse.winery.model.tosca.xml.XTEntityTypeImplementation;
import org.eclipse.winery.model.tosca.xml.XTExportedInterface;
import org.eclipse.winery.model.tosca.xml.XTExportedOperation;
import org.eclipse.winery.model.tosca.xml.XTExtensibleElements;
import org.eclipse.winery.model.tosca.xml.XTExtension;
import org.eclipse.winery.model.tosca.xml.XTImplementationArtifacts;
import org.eclipse.winery.model.tosca.xml.XTImport;
import org.eclipse.winery.model.tosca.xml.XTInterface;
import org.eclipse.winery.model.tosca.xml.XTInterfaces;
import org.eclipse.winery.model.tosca.xml.XTNodeTemplate;
import org.eclipse.winery.model.tosca.xml.XTNodeType;
import org.eclipse.winery.model.tosca.xml.XTNodeTypeImplementation;
import org.eclipse.winery.model.tosca.xml.XTOperation;
import org.eclipse.winery.model.tosca.xml.XTParameter;
import org.eclipse.winery.model.tosca.xml.XTPlan;
import org.eclipse.winery.model.tosca.xml.XTPolicy;
import org.eclipse.winery.model.tosca.xml.XTPolicyTemplate;
import org.eclipse.winery.model.tosca.xml.XTPolicyType;
import org.eclipse.winery.model.tosca.xml.XTPropertyConstraint;
import org.eclipse.winery.model.tosca.xml.XTPropertyMapping;
import org.eclipse.winery.model.tosca.xml.XTRelationshipTemplate;
import org.eclipse.winery.model.tosca.xml.XTRelationshipType;
import org.eclipse.winery.model.tosca.xml.XTRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.xml.XTRequiredContainerFeature;
import org.eclipse.winery.model.tosca.xml.XTRequirement;
import org.eclipse.winery.model.tosca.xml.XTRequirementDefinition;
import org.eclipse.winery.model.tosca.xml.XTRequirementRef;
import org.eclipse.winery.model.tosca.xml.XTRequirementType;
import org.eclipse.winery.model.tosca.xml.XTServiceTemplate;
import org.eclipse.winery.model.tosca.xml.XTTag;
import org.eclipse.winery.model.tosca.xml.XTTopologyTemplate;
import org.eclipse.winery.model.tosca.xml.extensions.XOTAttributeMapping;
import org.eclipse.winery.model.tosca.xml.extensions.XOTBehaviorPatternMapping;
import org.eclipse.winery.model.tosca.xml.extensions.XOTComplianceRule;
import org.eclipse.winery.model.tosca.xml.extensions.XOTDeploymentArtifactMapping;
import org.eclipse.winery.model.tosca.xml.extensions.XOTPatternRefinementModel;
import org.eclipse.winery.model.tosca.xml.extensions.XOTPermutationMapping;
import org.eclipse.winery.model.tosca.xml.extensions.XOTPrmMapping;
import org.eclipse.winery.model.tosca.xml.extensions.XOTPropertyKV;
import org.eclipse.winery.model.tosca.xml.extensions.XOTRefinementModel;
import org.eclipse.winery.model.tosca.xml.extensions.XOTRelationMapping;
import org.eclipse.winery.model.tosca.xml.extensions.XOTStayMapping;
import org.eclipse.winery.model.tosca.xml.extensions.XOTStringList;
import org.eclipse.winery.model.tosca.xml.extensions.XOTTestRefinementModel;
import org.eclipse.winery.model.tosca.xml.extensions.XOTTopologyFragmentRefinementModel;
import org.eclipse.winery.repository.xml.XmlRepository;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("DuplicatedCode")
public class ToCanonical {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToCanonical.class);
    private final XmlRepository repository;

    public ToCanonical(XmlRepository repository) {
        this.repository = repository;
    }

    public TDefinitions convert(XTDefinitions xml) {
        return convert(xml, false);
    }

    /**
     * Converts an XML TDefinitions collection to canonical TDefinitions.
     */
    public TDefinitions convert(XTDefinitions xml, boolean convertImports) {
        // FIXME need to correctly deal with convertImports flag to create a self-contained Definitions to export as CSAR if it is set.
        TDefinitions.Builder builder = new TDefinitions.Builder(xml.getId(), xml.getTargetNamespace())
            .setName(xml.getName())
            .addTypes(convertTypes(xml.getTypes()))
            .setImport(convertList(xml.getImport(), this::convert))
            .setServiceTemplates(convertList(xml.getServiceTemplates(), this::convert))
            .setNodeTypes(convertList(xml.getNodeTypes(), this::convert))
            .setNodeTypeImplementations(convertList(xml.getNodeTypeImplementations(), this::convert))
            .setRelationshipTypes(convertList(xml.getRelationshipTypes(), this::convert))
            .setRelationshipTypeImplementations(convertList(xml.getRelationshipTypeImplementations(), this::convert))
            .setCapabilityTypes(convertList(xml.getCapabilityTypes(), this::convert))
            .setArtifactTypes(convertList(xml.getArtifactTypes(), this::convert))
            .setArtifactTemplates(convertList(xml.getArtifactTemplates(), this::convert))
            .setPolicyTypes(convertList(xml.getPolicyTypes(), this::convert))
            .setPolicyTemplate(convertList(xml.getPolicyTemplates(), this::convert))
            .addRequirementTypes(convertList(xml.getRequirementTypes(), this::convert));
        if (xml.getExtensions() != null) {
            builder.addExtensions(convertList(xml.getExtensions().getExtension(), this::convert));
        }
        // this handles the "conversion" – basically copying of data – required by the disjoint TExtensibleElements
        //  acting as baseclass for all the extensions we support
        builder.setNonStandardElements(convertList(xml.getExtensionDefinitionsChildren(), this::convertNonStandard));
        fillExtensibleElementsProperties(builder, xml);
        return resolveReferences(builder.build());
    }

    @Nullable
    private TEntityTemplate convertEntityTemplate(XTEntityTemplate xml) {
        if (xml == null) {
            return null;
        }
        if (xml instanceof XRelationshipSourceOrTarget) {
            return convert((XRelationshipSourceOrTarget) xml);
        }
        if (xml instanceof XTArtifact) {
            return convert((XTArtifact) xml);
        }
        if (xml instanceof XTArtifactTemplate) {
            return convert((XTArtifactTemplate) xml);
        }
        if (xml instanceof XTPolicyTemplate) {
            return convert((XTPolicyTemplate) xml);
        }
        if (xml instanceof XTRelationshipTemplate) {
            return convert((XTRelationshipTemplate) xml);
        }
        LOGGER.warn("Trying to convert unknown subtype of TEntityTemplate to canonical model {}", xml.getClass());
        return null;
    }

    private TDefinitions resolveReferences(TDefinitions preliminary) {
        preliminary.getServiceTemplates().forEach(st -> {
            TTopologyTemplate topology = st.getTopologyTemplate();
            // if there's no topology, we can't have requirementRefs, capabilityRefs or relationshipTemplates
            if (topology == null) {
                return;
            }
            topology.getRelationshipTemplates()
                .forEach(rt -> {
                    rt.setTargetElement(resolveReference(rt.getTargetElement(), topology));
                    rt.setSourceElement(resolveReference(rt.getSourceElement(), topology));
                });
            // iterate TBoundaryDefinitions for the Capability and Requirement refs
            TBoundaryDefinitions boundaries = st.getBoundaryDefinitions();
            if (boundaries == null) {
                return;
            }
            if (boundaries.getRequirements() != null) {
                boundaries.getRequirements().getRequirement().forEach(req ->
                    req.setRef(resolveRequirement(req.getRef(), topology))
                );
            }
            if (boundaries.getCapabilities() != null) {
                boundaries.getCapabilities().getCapability().forEach(cap ->
                    cap.setRef(resolveCapability(cap.getRef(), topology))
                );
            }
        });

        return preliminary;
    }

    private TRelationshipTemplate.@NonNull SourceOrTargetElement resolveReference(TRelationshipTemplate.@NonNull SourceOrTargetElement incomplete, TTopologyTemplate topology) {
        RelationshipSourceOrTarget ref = incomplete.getRef();
        if (ref instanceof TCapability) {
            incomplete.setRef(resolveCapability((TCapability) ref, topology));
        } else if (ref instanceof TNodeTemplate) {
            incomplete.setRef(topology.getNodeTemplate(ref.getId()));
        } else if (ref instanceof TRequirement) {
            incomplete.setRef(resolveRequirement((TRequirement) ref, topology));
        } else {
            throw new IllegalStateException(String.format("Tried to resolve a SourceOrTargetReference of unknown type %s", ref.getClass()));
        }
        return incomplete;
    }

    private TCapability resolveCapability(TCapability incomplete, TTopologyTemplate topology) {
        return topology.getNodeTemplates().stream()
            .flatMap(nt -> nt.getCapabilities() == null ? Stream.empty()
                : nt.getCapabilities().getCapability().stream())
            .filter(cap -> cap.getId().equals(incomplete.getId()))
            .findFirst()
            .orElseGet(() -> {
                LOGGER.warn("Could not resolve the capability with id {}", incomplete.getId());
                return incomplete;
            });
    }

    private TRequirement resolveRequirement(TRequirement incomplete, TTopologyTemplate topology) {
        return topology.getNodeTemplates().stream()
            .flatMap(nt -> nt.getRequirements() == null ? Stream.empty()
                : nt.getRequirements().getRequirement().stream())
            .filter(req -> req.getId().equals(incomplete.getId()))
            .findFirst()
            .orElseGet(() -> {
                LOGGER.warn("Could not resolve the requirement with id {}", incomplete.getId());
                return incomplete;
            });
    }

    private TRelationshipType convert(XTRelationshipType xml) {
        TRelationshipType.Builder builder = new TRelationshipType.Builder(xml.getIdFromIdOrNameField())
            .addSourceInterfaces(convertInterfaces(xml.getSourceInterfaces()))
            .addTargetInterfaces(convertInterfaces(xml.getTargetInterfaces()))
            .addInterfaces(convertInterfaces(xml.getInterfaces()));
        if (xml.getValidSource() != null) {
            builder.setValidSource(xml.getValidSource().getTypeRef());
        }
        if (xml.getValidTarget() != null) {
            builder.setValidTarget(xml.getValidTarget().getTypeRef());
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
        fillEntityTypeProperties(builder, xml);
        return builder.build();
    }

    private List<TInterface> convertInterfaces(XTInterfaces xml) {
        if (xml == null) {
            return Collections.emptyList();
        }
        return xml.getInterface().stream().map(this::convertInterface).collect(Collectors.toList());
    }

    private TInterface convertInterface(XTInterface xml) {
        return new TInterface.Builder(xml.getName(), convertOperations(xml.getOperation())).build();
    }

    private List<TOperation> convertOperations(List<XTOperation> xml) {
        return xml.stream().map(this::convert).collect(Collectors.toList());
    }

    private TOperation convert(XTOperation xml) {
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

    private TParameter convert(XTParameter xml) {
        return new TParameter.Builder(xml.getName(), xml.getType(), xml.getRequired() == XTBoolean.YES).build();
    }

    private TRelationshipTypeImplementation convert(XTRelationshipTypeImplementation xml) {
        TRelationshipTypeImplementation.Builder builder = new TRelationshipTypeImplementation.Builder(xml.getName(), xml.getRelationshipType());
        fillEntityTypeImplementationProperties(builder, xml);
        if (xml.getDerivedFrom() != null) {
            TRelationshipTypeImplementation.DerivedFrom derived = new TRelationshipTypeImplementation.DerivedFrom();
            derived.setRelationshipTypeImplementationRef(xml.getDerivedFrom().getRelationshipTypeImplementationRef());
            builder.setDerivedFrom(derived);
        }
        return builder.build();
    }

    private <Builder extends TEntityTypeImplementation.Builder<Builder>, Value extends XTEntityTypeImplementation>
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
        builder.setAbstract(xml.getAbstract() == XTBoolean.YES);
        builder.setFinal(xml.getFinal() == XTBoolean.YES);
        fillExtensibleElementsProperties(builder, xml);
    }

    private TImplementationArtifacts.ImplementationArtifact convert(XTImplementationArtifacts.ImplementationArtifact xml) {
        return new TImplementationArtifacts.ImplementationArtifact.Builder(xml.getArtifactType())
            .setName(xml.getName())
            .setInterfaceName(xml.getInterfaceName())
            .setOperationName(xml.getOperationName())
            .setArtifactRef(xml.getArtifactRef())
            .build();
    }

    private TTag convert(XTTag xml) {
        return new TTag.Builder().setName(xml.getName()).setValue(xml.getValue()).build();
    }

    private TRequiredContainerFeature convert(XTRequiredContainerFeature xml) {
        TRequiredContainerFeature result = new TRequiredContainerFeature();
        result.setFeature(xml.getFeature());
        return result;
    }

    private TPolicyType convert(XTPolicyType xml) {
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

    private <Builder extends TEntityType.Builder<Builder>, Value extends XTEntityType>
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
            if (xml.getPropertiesDefinition().getElement() != null) {
                builder.setProperties(new TEntityType.XmlElementDefinition(xml.getPropertiesDefinition().getElement()));
            } else if (xml.getPropertiesDefinition().getType() != null) {
                builder.setProperties(new TEntityType.XmlTypeDefinition(xml.getPropertiesDefinition().getType()));
            } else {
                throw new IllegalStateException("If a PropertiesDefinition is given, either Element or Type must be specified!");
            }
        }
        if (xml.getAny().stream().anyMatch(anyElement -> anyElement instanceof WinerysPropertiesDefinition)) {
            WinerysPropertiesDefinition def = xml.getAny().stream()
                .filter(el -> el instanceof WinerysPropertiesDefinition)
                .map(WinerysPropertiesDefinition.class::cast)
                // get without check is safe here, because at least one element is a WinerysPropertiesDefinition
                .findFirst()
                .orElse(null);
            builder.setProperties(def);
            // remove the element we've recognized as a property to avoid duplicating it in the canonical model
            xml.getAny().remove(def);
        }
        builder.setAbstract(xml.getAbstract() == XTBoolean.YES);
        builder.setFinal(xml.getFinal() == XTBoolean.YES);
        builder.setTargetNamespace(xml.getTargetNamespace());
        fillExtensibleElementsProperties(builder, xml);
    }

    private <Builder extends TExtensibleElements.Builder<Builder>, Value extends XTExtensibleElements>
    void fillExtensibleElementsProperties(Builder builder, Value xml) {
        // because the getters are side-effecting by generating empty collections instead of returning null, 
        // we check for empty lists instead of null
        if (!xml.getDocumentation().isEmpty()) {
            builder.setDocumentation(xml.getDocumentation().stream().map(this::convert).collect(Collectors.toList()));
        }
        if (!xml.getOtherAttributes().isEmpty()) {
            builder.setOtherAttributes(xml.getOtherAttributes());
        }
        if (!xml.getAny().isEmpty()) {
            builder.setAny(xml.getAny());
        }
    }

    private TRequirementType convert(XTRequirementType xml) {
        TRequirementType.Builder builder = new TRequirementType.Builder(xml.getName());
        builder.setRequiredCapabilityType(xml.getRequiredCapabilityType());
        fillEntityTypeProperties(builder, xml);
        return builder.build();
    }

    private TNodeTypeImplementation convert(XTNodeTypeImplementation xml) {
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

    private TDeploymentArtifact convert(XTDeploymentArtifact xml) {
        TDeploymentArtifact.Builder builder = new TDeploymentArtifact.Builder(xml.getName(), xml.getArtifactType());
        builder.setArtifactRef(xml.getArtifactRef());
        fillExtensibleElementsProperties(builder, xml);
        return builder.build();
    }

    private TNodeType convert(XTNodeType xml) {
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
            TInterfaces interfaces = new TInterfaces();
            interfaces.getInterface().addAll(convertInterfaces(xml.getInterfaces()));
            builder.setInterfaces(interfaces);
        }
        fillEntityTypeProperties(builder, xml);
        return builder.build();
    }

    private TArtifact convert(XTArtifact xml) {
        TArtifact.Builder builder = new TArtifact.Builder(xml.getName(), xml.getType());
        builder.setDeployPath(xml.getDeployPath());
        builder.setDescription(xml.getDescription());
        builder.setFile(xml.getFile());
        fillEntityTemplateProperties(builder, xml);
        return builder.build();
    }

    private TRequirementDefinition convert(XTRequirementDefinition xml) {
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

    private TCapabilityDefinition convert(XTCapabilityDefinition xml) {
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

    private TConstraint convert(XTConstraint xml) {
        TConstraint constraint = new TConstraint();
        constraint.setAny(xml.getAny());
        constraint.setConstraintType(xml.getConstraintType());
        return constraint;
    }

    private TCapability convert(XTCapability xml) {
        TCapability.Builder builder = new TCapability.Builder(xml.getId(), xml.getType(), xml.getName());
        fillRelationshipSourceOrTargetProperties(builder, xml);
        return builder.build();
    }

    private <Builder extends RelationshipSourceOrTarget.Builder<Builder>, Value extends XRelationshipSourceOrTarget>
    void fillRelationshipSourceOrTargetProperties(Builder builder, Value xml) {
        // no specific properties to fill, just traverse the hierarchy
        fillEntityTemplateProperties(builder, xml);
    }

    private <Builder extends TEntityTemplate.Builder<Builder>, Value extends XTEntityTemplate>
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
        fillExtensibleElementsProperties(builder, xml);
    }

    private TEntityTemplate.Properties convertProperties(XTEntityTemplate.Properties xml) {
        if (PropertyMappingSupport.isKeyValuePropertyDefinition(xml)) {
            return PropertyMappingSupport.convertToKVProperties(xml);
        } else {
            // assume XML properties here
            TEntityTemplate.XmlProperties props = new TEntityTemplate.XmlProperties();
            props.setAny(xml.getAny());
            return props;
        }
    }

    private TPropertyConstraint convert(XTPropertyConstraint xml) {
        TPropertyConstraint constraint = new TPropertyConstraint();
        constraint.setAny(xml.getAny());
        constraint.setConstraintType(xml.getConstraintType());
        constraint.setProperty(xml.getProperty());
        return constraint;
    }

    private TExtension convert(XTExtension xml) {
        TExtension.Builder builder = new TExtension.Builder(xml.getNamespace());
        builder.setMustUnderstand(xml.getMustUnderstand() == XTBoolean.YES);
        fillExtensibleElementsProperties(builder, xml);
        return builder.build();
    }

    private TDocumentation convert(XTDocumentation xml) {
        TDocumentation canonical = new TDocumentation();
        canonical.getContent().addAll(xml.getContent());
        canonical.setSource(xml.getSource());
        canonical.setLang(xml.getLang());
        return canonical;
    }

    private TCapabilityType convert(XTCapabilityType xml) {
        TCapabilityType.Builder builder = new TCapabilityType.Builder(xml.getName());
        // FIXME validSourceTypes are apparently a YAML feature (again)
        builder.setValidSourceTypes(xml.getValidNodeTypes());
        fillEntityTypeProperties(builder, xml);
        return builder.build();
    }

    private TArtifactType convert(XTArtifactType xml) {
        TArtifactType.Builder builder = new TArtifactType.Builder(xml.getName());
        builder.setMimeType(xml.getMimeType());
        builder.setFileExtensions(xml.getFileExtensions());
        fillEntityTypeProperties(builder, xml);
        return builder.build();
    }

    private TArtifactTemplate convert(XTArtifactTemplate xml) {
        TArtifactTemplate.Builder builder = new TArtifactTemplate.Builder(xml.getId(), xml.getType());
        builder.setName(xml.getName());
        if (xml.getArtifactReferences() != null) {
            xml.getArtifactReferences().getArtifactReference().stream()
                .map(this::convert)
                .forEach(builder::addArtifactReferences);
        }
        fillEntityTemplateProperties(builder, xml);
        return builder.build();
    }

    private TArtifactReference convert(XTArtifactReference xml) {
        TArtifactReference.Builder builder = new TArtifactReference.Builder(xml.getReference());
        xml.getIncludeOrExclude()
            .forEach(iOrE -> {
                if (iOrE instanceof XTArtifactReference.Include) {
                    builder.addInclude(convert((XTArtifactReference.Include) iOrE));
                } else if (iOrE instanceof XTArtifactReference.Exclude) {
                    builder.addExclude(convert((XTArtifactReference.Exclude) iOrE));
                }
            });
        return builder.build();
    }

    private TArtifactReference.Include convert(XTArtifactReference.Include xml) {
        TArtifactReference.Include canonical = new TArtifactReference.Include();
        canonical.setPattern(xml.getPattern());
        return canonical;
    }

    private TArtifactReference.Exclude convert(XTArtifactReference.Exclude xml) {
        TArtifactReference.Exclude canonical = new TArtifactReference.Exclude();
        canonical.setPattern(xml.getPattern());
        return canonical;
    }

    private TImport convert(XTImport xml) {
        TImport.Builder builder = new TImport.Builder(xml.getImportType());
        builder.setNamespace(xml.getNamespace());
        builder.setLocation(xml.getLocation());
        fillExtensibleElementsProperties(builder, xml);
        return builder.build();
    }

    private TServiceTemplate convert(XTServiceTemplate xml) {
        TTopologyTemplate topologyTemplate = convert(xml.getTopologyTemplate());
        TServiceTemplate.Builder builder = new TServiceTemplate.Builder(xml.getId(), topologyTemplate);
        builder.setName(xml.getName());
        builder.setTargetNamespace(xml.getTargetNamespace());
        if (xml.getTags() != null) {
            xml.getTags().getTag().stream()
                .filter(t -> !t.getName().startsWith("group:")) // filter group definitions
                .filter(t -> !t.getName().startsWith("participant:")) // filter participants
                .map(this::convert).forEach(builder::addTags);
        }
        if (xml.getBoundaryDefinitions() != null) {
            builder.setBoundaryDefinitions(convert(xml.getBoundaryDefinitions()));
        }
        if (xml.getPlans() != null) {
            TPlans plans = new TPlans();
            plans.setTargetNamespace(xml.getPlans().getTargetNamespace());
            plans.getPlan().addAll(xml.getPlans().getPlan().stream().map(this::convert).collect(Collectors.toList()));
            builder.setPlans(plans);
        }
        builder.setSubstitutableNodeType(xml.getSubstitutableNodeType());
        fillExtensibleElementsProperties(builder, xml);

        // map group-related tags back to topology template
        if (topologyTemplate != null && xml.getTags() != null) {
            topologyTemplate.setGroups(convertList(xml.getTags().getTag(), this::convertToGroup));
        }

        // handle participant extension
        if (topologyTemplate != null && xml.getTags() != null) {
            topologyTemplate.setParticipants(convertList(xml.getTags().getTag(), this::convertToParticipant));
        }

        return builder.build();
    }

    private TGroupDefinition convertToGroup(XTTag xml) {
        if (xml.getName().startsWith("group:")) {
            String name = xml.getName().split(":")[1];
            String description = xml.getValue();
            return new TGroupDefinition.Builder(name, QName.valueOf("{tosca.groups}Root"))
                .setDescription(description)
                .build();
        }
        return null;
    }

    private OTParticipant convertToParticipant(XTTag xml) {
        if (xml.getName().startsWith("participant:")) {
            String name = xml.getName().split(":")[1];
            String url = xml.getValue();
            return new OTParticipant.Builder().setName(name).setUrl(url).build();
        }
        return null;
    }

    private TPlan convert(XTPlan xml) {
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

    private TCondition convert(XTCondition xml) {
        TCondition canonical = new TCondition();
        canonical.setExpressionLanguage(xml.getExpressionLanguage());
        canonical.getAny().addAll(xml.getAny());
        return canonical;
    }

    private TBoundaryDefinitions convert(XTBoundaryDefinitions xml) {
        TBoundaryDefinitions.Builder builder = new TBoundaryDefinitions.Builder();
        if (xml.getProperties() != null) {
            TBoundaryDefinitions.Properties props = new TBoundaryDefinitions.Properties();
            props.setAny(xml.getProperties().getAny());
            if (xml.getProperties().getPropertyMappings() != null) {
                TBoundaryDefinitions.Properties.PropertyMappings mappings = new TBoundaryDefinitions.Properties.PropertyMappings();
                mappings.getPropertyMapping().addAll(convertList(xml.getProperties().getPropertyMappings().getPropertyMapping(), this::convert));
                props.setPropertyMappings(mappings);
            }
            builder.setProperties(props);
        }
        if (xml.getRequirements() != null) {
            TBoundaryDefinitions.Requirements reqs = new TBoundaryDefinitions.Requirements();
            reqs.getRequirement().addAll(convertList(xml.getRequirements().getRequirement(), this::convert));
            builder.setRequirements(reqs);
        }
        if (xml.getCapabilities() != null) {
            TBoundaryDefinitions.Capabilities caps = new TBoundaryDefinitions.Capabilities();
            caps.getCapability().addAll(convertList(xml.getCapabilities().getCapability(), this::convert));
            builder.setCapabilities(caps);
        }
        if (xml.getPolicies() != null) {
            TPolicies policies = new TPolicies(convertList(xml.getPolicies().getPolicy(), this::convert));
            builder.setPolicies(policies);
        }
        if (xml.getInterfaces() != null) {
            TBoundaryDefinitions.Interfaces interfaces = new TBoundaryDefinitions.Interfaces();
            interfaces.getInterface().addAll(convertList(xml.getInterfaces().getInterface(), this::convert));
            builder.setInterfaces(interfaces);
        }
        if (xml.getPropertyConstraints() != null) {
            TBoundaryDefinitions.PropertyConstraints constraints = new TBoundaryDefinitions.PropertyConstraints();
            constraints.getPropertyConstraint().addAll(convertList(xml.getPropertyConstraints().getPropertyConstraint(), this::convert));
            builder.setPropertyConstraints(constraints);
        }
        return builder.build();
    }

    private TExportedInterface convert(XTExportedInterface xml) {
        return new TExportedInterface(
            xml.getName(),
            convertList(xml.getOperation(), this::convert));
    }

    private TExportedOperation convert(XTExportedOperation xml) {
        TExportedOperation canonical = new TExportedOperation(xml.getName());
        if (xml.getNodeOperation() != null) {
            canonical.setNodeOperation(convert(xml.getNodeOperation()));
        }
        if (xml.getRelationshipOperation() != null) {
            canonical.setRelationshipOperation(convert(xml.getRelationshipOperation()));
        }
        if (xml.getPlan() != null) {
            TExportedOperation.Plan plan = new TExportedOperation.Plan();
            if (xml.getPlan().getPlanRef() instanceof String) {
                plan.setPlanRef(xml.getPlan().getPlanRef());
            } else {
                plan.setPlanRef(convert((XTPlan) xml.getPlan().getPlanRef()));
            }
            canonical.setPlan(plan);
        }
        return canonical;
    }

    private TExportedOperation.RelationshipOperation convert(XTExportedOperation.RelationshipOperation xml) {
        TExportedOperation.RelationshipOperation canonical = new TExportedOperation.RelationshipOperation();
        if (xml.getRelationshipRef() instanceof String) {
            canonical.setRelationshipRef(xml.getRelationshipRef());
        } else {
            canonical.setRelationshipRef(convert((XTRelationshipTemplate) xml.getRelationshipRef()));
        }
        canonical.setInterfaceName(xml.getInterfaceName());
        canonical.setOperationName(xml.getOperationName());
        return canonical;
    }

    private TExportedOperation.NodeOperation convert(XTExportedOperation.NodeOperation xml) {
        TExportedOperation.NodeOperation canonical = new TExportedOperation.NodeOperation();
        if (xml.getNodeRef() instanceof String) {
            canonical.setNodeRef(xml.getNodeRef());
        } else {
            canonical.setNodeRef(convert((XTNodeTemplate) xml.getNodeRef()));
        }
        canonical.setInterfaceName(xml.getInterfaceName());
        canonical.setOperationName(xml.getOperationName());
        return canonical;
    }

    private TPolicy convert(XTPolicy xml) {
        TPolicy.Builder builder = new TPolicy.Builder(xml.getPolicyType());
        builder.setName(xml.getName());
        builder.setPolicyRef(xml.getPolicyRef());
        builder.setTargets(xml.getTargets());
        if (xml.getProperties() != null) {
            builder.setProperties(convertProperties(xml.getProperties()));
        }
        return builder.build();
    }

    private TPolicyTemplate convert(XTPolicyTemplate xml) {
        TPolicyTemplate.Builder builder = new TPolicyTemplate.Builder(xml.getId(), xml.getType());
        builder.setName(xml.getName());
        fillEntityTemplateProperties(builder, xml);
        return builder.build();
    }

    private TCapabilityRef convert(XTCapabilityRef xml) {
        TCapabilityRef canonical = new TCapabilityRef();
        canonical.setName(xml.getName());
        canonical.setRef(convert(xml.getRef()));
        return canonical;
    }

    private TRequirementRef convert(XTRequirementRef xml) {
        TRequirementRef canonical = new TRequirementRef();
        canonical.setName(xml.getName());
        canonical.setRef(convert(xml.getRef()));
        return canonical;
    }

    private TRequirement convert(XTRequirement xml) {
        TRequirement.Builder builder = new TRequirement.Builder(xml.getId(), xml.getName(), xml.getType());
        builder.setCapability(xml.getCapability());
        builder.setRelationship(xml.getRelationship());
        builder.setNode(xml.getNode());
        fillRelationshipSourceOrTargetProperties(builder, xml);
        return builder.build();
    }

    private TPropertyMapping convert(XTPropertyMapping xml) {
        TPropertyMapping canonical = new TPropertyMapping();
        canonical.setServiceTemplatePropertyRef(xml.getServiceTemplatePropertyRef());
        canonical.setTargetPropertyRef(xml.getTargetPropertyRef());
        canonical.setTargetObjectRef(convert(xml.getTargetObjectRef()));
        return canonical;
    }

    @Nullable
    private TTopologyTemplate convert(XTTopologyTemplate xml) {
        if (xml == null) {
            return null;
        }
        TTopologyTemplate.Builder builder = new TTopologyTemplate.Builder();
        builder.addNodeTemplates(convertList(xml.getNodeTemplates(), this::convert));
        builder.addRelationshipTemplates(convertList(xml.getRelationshipTemplates(), this::convert));
        // policies, inputs and outputs from canonical are YAML-only
        fillExtensibleElementsProperties(builder, xml);
        return builder.build();
    }

    private TNodeTemplate convert(XTNodeTemplate xml) {
        TNodeTemplate.Builder builder = new TNodeTemplate.Builder(xml.getId(), xml.getType());
        if (xml.getRequirements() != null) {
            TNodeTemplate.Requirements reqs = new TNodeTemplate.Requirements();
            reqs.getRequirement().addAll(convertList(xml.getRequirements().getRequirement(), this::convert));
            builder.setRequirements(reqs);
        }
        if (xml.getCapabilities() != null) {
            TNodeTemplate.Capabilities caps = new TNodeTemplate.Capabilities();
            caps.getCapability().addAll(convertList(xml.getCapabilities().getCapability(), this::convert));
            builder.setCapabilities(caps);
        }
        if (xml.getPolicies() != null) {
            TPolicies policies = new TPolicies(convertList(xml.getPolicies().getPolicy(), this::convert));
            builder.setPolicies(policies);
        }
        if (xml.getDeploymentArtifacts() != null) {
            TDeploymentArtifacts artifacts = new TDeploymentArtifacts.Builder(
                convertList(xml.getDeploymentArtifacts().getDeploymentArtifact(), this::convert)
            )
                .build();
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

    private TRelationshipTemplate convert(XTRelationshipTemplate xml) {
        TRelationshipTemplate.Builder builder = new TRelationshipTemplate.Builder(xml.getId(), xml.getType(),
            convert(xml.getSourceElement()), convert(xml.getTargetElement()));
        builder.setName(xml.getName());
        if (xml.getRelationshipConstraints() != null) {
            TRelationshipTemplate.RelationshipConstraints constraints = new TRelationshipTemplate.RelationshipConstraints();
            constraints.getRelationshipConstraint().addAll(xml.getRelationshipConstraints().getRelationshipConstraint().stream()
                .map(this::convert).collect(Collectors.toList()));
            builder.setRelationshipConstraints(constraints);
        }
        if (xml.getPolicies() != null) {
            TPolicies policies = new TPolicies(convertList(xml.getPolicies().getPolicy(), this::convert));
            builder.setPolicies(policies);
        }
        fillEntityTemplateProperties(builder, xml);
        return builder.build();
    }

    private TRelationshipTemplate.RelationshipConstraints.RelationshipConstraint convert(XTRelationshipTemplate.RelationshipConstraints.RelationshipConstraint xml) {
        TRelationshipTemplate.RelationshipConstraints.RelationshipConstraint canonical = new TRelationshipTemplate.RelationshipConstraints.RelationshipConstraint();
        canonical.setAny(xml.getAny());
        canonical.setConstraintType(xml.getConstraintType());
        return canonical;
    }

    private TRelationshipTemplate.SourceOrTargetElement convert(XTRelationshipTemplate.SourceOrTargetElement xml) {
        TRelationshipTemplate.SourceOrTargetElement canonical = new TRelationshipTemplate.SourceOrTargetElement();
        canonical.setRef(convert(xml.getRef()));
        return canonical;
    }

    private RelationshipSourceOrTarget convert(XRelationshipSourceOrTarget xml) {
        // Capability or NodeTemplate or Requirement
        if (xml instanceof XTCapability) {
            return convert((XTCapability) xml);
        }
        if (xml instanceof XTNodeTemplate) {
            return convert((XTNodeTemplate) xml);
        }
        if (xml instanceof XTRequirement) {
            return convert((XTRequirement) xml);
        }
        throw new IllegalStateException(String.format("Tried to convert unknown RelationshipSourceOrTarget implementation %s", xml.getClass().getName()));
    }

    private TDefinitions.Types convertTypes(XTDefinitions.@Nullable Types xml) {
        if (xml == null) {
            return new TDefinitions.Types();
        }
        TDefinitions.Types result = new TDefinitions.Types();
        result.getAny().addAll(xml.getAny());
        return result;
    }

    private HasId convert(XHasId xml) {
        if (xml instanceof XTDefinitions) {
            // what in the ever loving fuck am I supposed to do now??
            // this case should never ever come true
            throw new IllegalStateException("Attempted to convert a TDefinitions instance through HasId overload.");
        }
        if (xml instanceof XRelationshipSourceOrTarget) {
            return convert((XRelationshipSourceOrTarget) xml);
        }
        if (xml instanceof XTArtifact) {
            return convert((XTArtifact) xml);
        }
        if (xml instanceof XTArtifactTemplate) {
            return convert((XTArtifactTemplate) xml);
        }
        if (xml instanceof XTPolicyTemplate) {
            return convert((XTPolicyTemplate) xml);
        }
        if (xml instanceof XTRelationshipTemplate) {
            return convert((XTRelationshipTemplate) xml);
        }
        throw new IllegalStateException("Attempted to convert unknown element deriving from HasId with type " + xml.getClass().getName());
    }

    private <R, I> List<R> convertList(@Nullable List<I> xml, Function<I, R> convert) {
        if (xml == null) {
            return Collections.emptyList();
        }
        return xml.stream().map(convert).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private TExtensibleElements convertNonStandard(XTExtensibleElements xml) {
        if (xml instanceof XOTAttributeMapping) {
            return convert((XOTAttributeMapping) xml);
        }
        if (xml instanceof XOTComplianceRule) {
            return convert((XOTComplianceRule) xml);
        }
        if (xml instanceof XOTDeploymentArtifactMapping) {
            return convert((XOTDeploymentArtifactMapping) xml);
        }
        if (xml instanceof XOTPatternRefinementModel) {
            return convert((XOTPatternRefinementModel) xml);
        }
        if (xml instanceof XOTRelationMapping) {
            return convert((XOTRelationMapping) xml);
        }
        if (xml instanceof XOTStayMapping) {
            return convert((XOTStayMapping) xml);
        }
        if (xml instanceof XOTPermutationMapping) {
            return convert((XOTPermutationMapping) xml);
        }
        if (xml instanceof XOTTestRefinementModel) {
            return convert((XOTTestRefinementModel) xml);
        }
        if (xml instanceof XOTTopologyFragmentRefinementModel) {
            return convert((XOTTopologyFragmentRefinementModel) xml);
        }
        if (xml instanceof XOTBehaviorPatternMapping) {
            return convert((XOTBehaviorPatternMapping) xml);
        }
        throw new IllegalStateException("Attempted to convert unknown Extension to the TOSCA-Standard of the type " + xml.getClass().getName() + " to canonical");
    }

    private <Builder extends OTPrmMapping.Builder<Builder>, Value extends XOTPrmMapping> void fillOTPrmMappingProperties(Builder builder, Value value) {
        builder.setDetectorElement(convertEntityTemplate(value.getDetectorElement()));
        builder.setRefinementElement(convertEntityTemplate(value.getRefinementElement()));
        fillExtensibleElementsProperties(builder, value);
    }

    private OTAttributeMapping convert(XOTAttributeMapping xml) {
        OTAttributeMapping.Builder builder = new OTAttributeMapping.Builder(xml.getId());
        builder.setType(OTAttributeMappingType.fromValue(xml.getType().value()));
        builder.setDetectorProperty(xml.getDetectorProperty());
        builder.setRefinementProperty(xml.getRefinementProperty());
        fillOTPrmMappingProperties(builder, xml);
        return builder.build();
    }

    private OTDeploymentArtifactMapping convert(XOTDeploymentArtifactMapping xml) {
        OTDeploymentArtifactMapping.Builder builder = new OTDeploymentArtifactMapping.Builder(xml.getId());
        builder.setArtifactType(xml.getArtifactType());
        fillOTPrmMappingProperties(builder, xml);
        return builder.build();
    }

    private <Builder extends OTTopologyFragmentRefinementModel.RefinementBuilder<Builder>, Value extends XOTTopologyFragmentRefinementModel> void fillOTTopologyFragmentRefinementModelProperties(Builder builder, Value value) {
        builder.setRefinementStructure(convert(value.getRefinementStructure()));
        builder.setDeploymentArtifactMappings(convertList(value.getDeploymentArtifactMappings(), this::convert));
        builder.setStayMappings(convertList(value.getStayMappings(), this::convert));
        builder.setPermutationMappings(convertList(value.getPermutationMappings(), this::convert));
        builder.setPermutationOptions(convertList(value.getPermutationOptions(), this::convert));
        builder.setAttributeMappings(convertList(value.getAttributeMappings(), this::convert));
        builder.setComponentSets(convertList(value.getComponentSets(), this::convert));
        fillOTRefinementModelProperties(builder, value);
    }

    private <Builder extends OTRefinementModel.Builder<Builder>, Value extends XOTRefinementModel> void
    fillOTRefinementModelProperties(Builder builder, Value value) {
        builder.setName(value.getName());
        builder.setDetector(convert(value.getDetector()));
        builder.setTargetNamespace(value.getTargetNamespace());
        builder.setRelationMappings(convertList(value.getRelationMappings(), this::convert));
        builder.setPermutationMappings(convertList(value.getPermutationMappings(), this::convert));
        fillExtensibleElementsProperties(builder, value);
    }

    private OTPatternRefinementModel convert(XOTPatternRefinementModel xml) {
        OTPatternRefinementModel.Builder builder = new OTPatternRefinementModel.Builder();
        builder.setIsPdrm(xml.isPdrm() == XTBoolean.YES);
        builder.setBehaviorPatternMappings(convertList(xml.getBehaviorPatternMappings(), this::convert));
        fillOTTopologyFragmentRefinementModelProperties(builder, xml);
        return builder.build();
    }

    private OTRelationMapping convert(XOTRelationMapping xml) {
        OTRelationMapping.Builder builder = new OTRelationMapping.Builder(xml.getId());
        builder.setDirection(OTRelationDirection.fromValue(xml.getDirection().value()));
        builder.setRelationType(xml.getRelationType());
        builder.setValidSourceOrTarget(xml.getValidSourceOrTarget());
        fillOTPrmMappingProperties(builder, xml);
        return builder.build();
    }

    private OTStayMapping convert(XOTStayMapping xml) {
        OTStayMapping.Builder builder = new OTStayMapping.Builder(xml.getId());
        fillOTPrmMappingProperties(builder, xml);
        return builder.build();
    }

    private OTPermutationMapping convert(XOTPermutationMapping xml) {
        OTPermutationMapping.Builder builder = new OTPermutationMapping.Builder(xml.getId());
        fillOTPrmMappingProperties(builder, xml);
        return builder.build();
    }

    private OTStringList convert(XOTStringList xml) {
        OTStringList.Builder builder = new OTStringList.Builder(xml.getValues());
        return builder.build();
    }

    private OTComplianceRule convert(XOTComplianceRule xml) {
        OTComplianceRule.Builder builder = new OTComplianceRule.Builder(xml.getId());
        builder.setIdentifier(convert(xml.getIdentifier()));
        builder.setName(xml.getName());
        builder.setRequiredStructure(convert(xml.getRequiredStructure()));
        fillExtensibleElementsProperties(builder, xml);
        return builder.build();
    }

    private OTTestRefinementModel convert(XOTTestRefinementModel xml) {
        OTTestRefinementModel.Builder builder = new OTTestRefinementModel.Builder();
        builder.setTestFragment(convert(xml.getTestFragment()));
        fillOTRefinementModelProperties(builder, xml);
        return builder.build();
    }

    private OTTopologyFragmentRefinementModel convert(XOTTopologyFragmentRefinementModel xml) {
        OTTopologyFragmentRefinementModel.Builder builder = new OTTopologyFragmentRefinementModel.Builder();
        fillOTTopologyFragmentRefinementModelProperties(builder, xml);
        return builder.build();
    }

    private OTBehaviorPatternMapping convert(XOTBehaviorPatternMapping xml) {
        OTBehaviorPatternMapping.Builder builder = new OTBehaviorPatternMapping.Builder(xml.getId());
        builder.setBehaviorPattern(xml.getBehaviorPattern());
        builder.setProperty(convert(xml.getProperty()));
        fillOTPrmMappingProperties(builder, xml);
        return builder.build();
    }

    private OTPropertyKV convert(XOTPropertyKV xml) {
        return new OTPropertyKV(xml.getKey(), xml.getValue());
    }
}
