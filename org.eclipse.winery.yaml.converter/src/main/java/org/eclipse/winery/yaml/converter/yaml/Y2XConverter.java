/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.yaml.converter.yaml;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.Util;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TAppliesTo;
import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TArtifactType;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TCapabilityDefinition;
import org.eclipse.winery.model.tosca.TCapabilityType;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TImport;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.model.tosca.TPolicyType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TRequirementDefinition;
import org.eclipse.winery.model.tosca.TRequirementType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTag;
import org.eclipse.winery.model.tosca.TTags;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.yaml.TArtifactDefinition;
import org.eclipse.winery.model.tosca.yaml.TAttributeDefinition;
import org.eclipse.winery.model.tosca.yaml.TImplementation;
import org.eclipse.winery.model.tosca.yaml.TImportDefinition;
import org.eclipse.winery.model.tosca.yaml.TInterfaceDefinition;
import org.eclipse.winery.model.tosca.yaml.TInterfaceType;
import org.eclipse.winery.model.tosca.yaml.TOperationDefinition;
import org.eclipse.winery.model.tosca.yaml.TPolicyDefinition;
import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;
import org.eclipse.winery.model.tosca.yaml.TPropertyAssignmentOrDefinition;
import org.eclipse.winery.model.tosca.yaml.TPropertyDefinition;
import org.eclipse.winery.model.tosca.yaml.TRequirementAssignment;
import org.eclipse.winery.model.tosca.yaml.TTopologyTemplateDefinition;
import org.eclipse.winery.model.tosca.yaml.support.Metadata;
import org.eclipse.winery.model.tosca.yaml.support.TMapRequirementAssignment;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.yaml.common.Defaults;
import org.eclipse.winery.yaml.common.Namespaces;
import org.eclipse.winery.yaml.common.exception.MultiException;
import org.eclipse.winery.yaml.common.reader.yaml.Reader;
import org.eclipse.winery.yaml.common.writer.WriterUtils;
import org.eclipse.winery.yaml.converter.Converter;
import org.eclipse.winery.yaml.converter.yaml.support.AssignmentBuilder;
import org.eclipse.winery.yaml.converter.yaml.support.TypeConverter;
import org.eclipse.winery.yaml.converter.yaml.support.extension.TImplementationArtifactDefinition;
import org.eclipse.winery.yaml.converter.yaml.visitors.ReferenceVisitor;
import org.eclipse.winery.yaml.converter.yaml.visitors.SchemaVisitor;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Y2XConverter {
	public final static Logger LOGGER = LoggerFactory.getLogger(Y2XConverter.class);
	private final IRepository repository;
	private String namespace;
	private String path;
	private String outPath;
	private List<TNodeTypeImplementation> nodeTypeImplementations;
	private Map<String, TArtifactTemplate> artifactTemplates;
	private List<TPolicyTemplate> policyTemplates;
	private List<TRequirementType> requirementTypes;
	private List<TImport> imports;
	private Map<QName, TInterfaceType> interfaceTypes;
	private Map<String, List<TPolicy>> policies;
	private Map<String, Map.Entry<String, String>> relationshipSTMap;
	private Map<String, TNodeTemplate> nodeTemplateMap;
	private AssignmentBuilder assignmentBuilder;
	private ReferenceVisitor referenceVisitor;

	public Y2XConverter(IRepository repository) {
		this.repository = repository;
	}

	private void reset() {
		this.nodeTypeImplementations = new ArrayList<>();
		this.artifactTemplates = new LinkedHashMap<>();
		this.policyTemplates = new ArrayList<>();
		this.requirementTypes = new ArrayList<>();
		this.imports = new ArrayList<>();

		this.policies = new LinkedHashMap<>();
		this.relationshipSTMap = new LinkedHashMap<>();
		this.nodeTemplateMap = new LinkedHashMap<>();

		this.interfaceTypes = new LinkedHashMap<>();
	}

	/**
	 * Processes knowledge from TServiceTemplate needed to construct XML result
	 */
	private void init(org.eclipse.winery.model.tosca.yaml.TServiceTemplate node) {
		// no interface type for xml -> interface type information inserted into interface definitions
		convert(node.getInterfaceTypes());
		SchemaVisitor schemaVisitor = new SchemaVisitor();
		schemaVisitor.visit(node, path, outPath, namespace);
		this.assignmentBuilder = new AssignmentBuilder(schemaVisitor.getPropertyDefinition());
	}

	/**
	 * Converts TOSCA YAML ServiceTemplates to TOSCA XML Definitions
	 *
	 * @return TOSCA XML Definitions
	 */
	@NonNull
	public Definitions convert(org.eclipse.winery.model.tosca.yaml.TServiceTemplate node, String id, String target_namespace, String path, String outPath) {
		if (node == null) return new Definitions();
		LOGGER.debug("Converting TServiceTemplate");

		// Reset
		this.reset();
		this.referenceVisitor = new ReferenceVisitor(node, target_namespace, path);

		this.namespace = target_namespace;
		this.path = path;
		this.outPath = outPath;

		init(node);

		Definitions definitions = new Definitions.Builder(id + "_Definitions", target_namespace)
			.setImport(convert(node.getImports()))
			.addTypes(convert(node.getDataTypes()))
			.addTypes(convert(node.getGroupTypes()))
			.addServiceTemplates(convertServiceTemplate(node, id, target_namespace))
			.addNodeTypes(convert(node.getNodeTypes()))
			.addNodeTypeImplementations(this.nodeTypeImplementations)
			.addRelationshipTypes(convert(node.getRelationshipTypes()))
			.addCapabilityTypes(convert(node.getCapabilityTypes()))
			.addArtifactTypes(convert(node.getArtifactTypes()))
			.addArtifactTemplates(this.artifactTemplates.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList()))
			.addPolicyTypes(convert(node.getPolicyTypes()))
			.setName(id)
			.addImports(this.imports)
			.addRequirementTypes(this.requirementTypes)
			.addPolicyTemplates(this.policyTemplates)
			.build();
		WriterUtils.storeDefinitions(definitions, true, Paths.get(path));
		return definitions;
	}

	/**
	 * Converts TOSCA YAML ServiceTemplates to TOSCA XML ServiceTemplates
	 *
	 * @param node TOSCA YAML ServiceTemplate
	 * @return TOSCA XML ServiceTemplate
	 */
	@Nullable
	private TServiceTemplate convertServiceTemplate(org.eclipse.winery.model.tosca.yaml.TServiceTemplate node, String id, String targetNamespace) {
		if (node == null || node.getTopologyTemplate() == null) return null;

		return new TServiceTemplate.Builder(id, convert(node.getTopologyTemplate()))
			.addDocumentation(node.getDescription())
			.setBoundaryDefinitions(
				new TBoundaryDefinitions.Builder()
					.addPolicies(this.policies.get("boundary")).build()
			)
			.setName(id)
			.setTargetNamespace(targetNamespace)
			.build();
	}

	/**
	 * Converts TPropertyDefinition and TAttributeDefinition to an xml schema
	 *
	 * @return TOSCA XML PropertyDefinition with referencing the schema of the Properties
	 */
	private TEntityType.PropertiesDefinition convertPropertyDefinition(String name) {
		this.imports.add(
			new TImport.Builder(Namespaces.XML_NS)
				.setNamespace(this.namespace)
				.setLocation("types" + File.separator + name + ".xsd").build()
		);

		TEntityType.PropertiesDefinition propertiesDefinition = new TEntityType.PropertiesDefinition();
		propertiesDefinition.setElement(new QName(name));
		return propertiesDefinition;
	}

	/**
	 * Converts TOSCA YAML EntityTypes to TOSCA XML EntityTypes
	 *
	 * Additional element version added to tag.
	 * Missing elements abstract, final will not be set.
	 * Missing element targetNamespace is searched in metadata
	 *
	 * @param node TOSCA YAML EntityType
	 * @return TOSCA XML EntityType
	 */
	private <T extends TEntityType.Builder<T>> T convert(org.eclipse.winery.model.tosca.yaml.TEntityType node, T builder) {
		builder.addDocumentation(node.getDescription())
			.setDerivedFrom(node.getDerivedFrom())
			.addTags(convertMetadata(node.getMetadata()))
			.setTargetNamespace(node.getMetadata().get("targetNamespace"));

		if (node.getVersion() != null) {
			TTag tag = new TTag();
			tag.setName("version");
			tag.setValue(node.getVersion().getVersion());
			builder.addTags(tag);
		}

		if (!node.getProperties().isEmpty()) {
			builder.setPropertiesDefinition(convertPropertyDefinition(builder.build().getIdFromIdOrNameField() + "_Properties"));
		}

		return builder;
	}

	/**
	 * Converts TOSCA YAML metadata to TOSCA XML Tags
	 *
	 * @param metadata map of strings
	 * @return TOSCA XML Tags
	 */
	@NonNull
	private TTags convertMetadata(Metadata metadata) {
		return new TTags.Builder()
			.addTag(
				metadata.entrySet().stream()
					.filter(Objects::nonNull)
					.map(entry -> new TTag.Builder().setName(entry.getKey()).setValue(entry.getValue()).build())
					.filter(Objects::nonNull)
					.collect(Collectors.toList())
			)
			.build();
	}

	/**
	 * Converts TOSCA YAML ArtifactTypes to TOSCA XML ArtifactTypes.
	 * Both objects have a super type EntityType.
	 * Additional elements mime_type and file_ext from TOSCA YAML are moved to tags in TOSCA XML
	 *
	 * @param node the YAML ArtifactType
	 * @return TOSCA XML ArtifactType
	 */
	private TArtifactType convert(org.eclipse.winery.model.tosca.yaml.TArtifactType node, String id) {
		if (node == null) return null;
		TArtifactType.Builder builder = new TArtifactType.Builder(id);
		convert(node, builder);
		if (node.getFileExt() != null) {
			builder.addTags("file_ext", "[" + node.getFileExt().stream().map(Object::toString)
				.collect(Collectors.joining(",")) + "]");
		}
		if (node.getMimeType() != null) {
			builder.addTags("mime_type", node.getMimeType());
		}
		return builder.build();
	}

	/**
	 * Converts a TOSCA YAML ArtifactDefinition to a TOSCA XML ArtifactTemplate
	 *
	 * @param node TOSCA YAML ArtifactDefinition
	 * @return TOSCA XML ArtifactTemplate
	 */
	@NonNull
	private TArtifactTemplate convert(TArtifactDefinition node, String id) {
		return new TArtifactTemplate.Builder(id, node.getType())
			.addArtifactReferences(node.getFiles().stream()
				.filter(Objects::nonNull)
				// TODO change filepath
				.map(file -> new TArtifactReference.Builder(file).build())
				.collect(Collectors.toList())
			)
			.setProperties(convertPropertyAssignments(node.getProperties(), getPropertyTypeName(node.getType())))
			.build();
	}

	/**
	 * Converts TOSCA YAML ArtifactDefinitions to TOSCA XML DeploymentArtifacts
	 *
	 * @param artifactDefinitionMap map of TOSCA YAML ArtifactDefinitions
	 * @return TOSCA XML DeploymentArtifacts
	 */
	private TDeploymentArtifacts convertDeploymentArtifacts(@NonNull Map<String, TArtifactDefinition> artifactDefinitionMap) {
		if (artifactDefinitionMap.isEmpty()) return null;
		return new TDeploymentArtifacts.Builder(artifactDefinitionMap.entrySet().stream()
			.filter(Objects::nonNull)
			.map(entry -> {
				TArtifactTemplate artifactTemplate = convert(entry.getValue(), entry.getKey());
				this.artifactTemplates.put(artifactTemplate.getId(), artifactTemplate);
				return new TDeploymentArtifact.Builder(entry.getKey(), entry.getValue().getType())
					.setArtifactRef(new QName(artifactTemplate.getId()))
					.build();
			})
			.collect(Collectors.toList()))
			.build();
	}

	/**
	 * Converts TOSCA YAML ArtifactDefinitions to TOSCA XML ImplementationArtifacts
	 *
	 * @param artifactDefinitionMap map of TOSCA YAML ArtifactDefinitions
	 * @return TOSCA XML ImplementationArtifacts
	 */
	private TImplementationArtifacts convertImplementationArtifact(@NonNull Map<String, TArtifactDefinition> artifactDefinitionMap) {
		if (artifactDefinitionMap.isEmpty()) return null;
		return new TImplementationArtifacts.Builder(artifactDefinitionMap.entrySet().stream()
			.filter(entry -> Objects.nonNull(entry) && Objects.nonNull(entry.getValue()))
			.map(entry -> {
				TArtifactTemplate artifactTemplate = convert(entry.getValue(), entry.getKey());
				this.artifactTemplates.put(artifactTemplate.getId(), artifactTemplate);
				return new TImplementationArtifacts.ImplementationArtifact.Builder(entry.getValue().getType())
					.setName(entry.getKey())
					.setArtifactRef(new QName(artifactTemplate.getId()))
					.setInterfaceName(convertInterfaceName(entry.getValue()))
					.setOperationName(convertOperationName(entry.getValue()))
					.build();
			})
			.collect(Collectors.toList()))
			.build();
	}

	@Nullable
	public String convertInterfaceName(@NonNull TArtifactDefinition node) {
		if (node instanceof TImplementationArtifactDefinition)
			return ((TImplementationArtifactDefinition) node).getInterfaceName();
		return null;
	}

	@Nullable
	public String convertOperationName(@NonNull TArtifactDefinition node) {
		if (node instanceof TImplementationArtifactDefinition)
			return ((TImplementationArtifactDefinition) node).getOperationName();
		return null;
	}

	/**
	 * Inserts operation output definitions defined in attributes
	 * "{ get_operation_output: [ SELF, interfaceName, operationName, propertyName ] }"
	 * into interfaceDefinitions
	 */
	private Map<String, TInterfaceDefinition> refactor(Map<String, TInterfaceDefinition> map, org.eclipse.winery.model.tosca.yaml.TNodeType node) {
		if (Objects.isNull(map) || map.isEmpty() || node.getAttributes().isEmpty()) return map;

		// Extract Outputs from Attributes and attach them to the Operations (if possible)
		// Template: attribute.default: { get_operation_output: [ SELF, interfaceName, operationName, propertyName ] }
		for (Map.Entry<String, TAttributeDefinition> entry : node.getAttributes().entrySet()) {
			TAttributeDefinition attr = entry.getValue();
			if (attr.getDefault() != null && attr.getDefault() instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Object> aDefault = (Map<String, Object>) attr.getDefault();
				if (aDefault != null && aDefault.containsKey("get_operation_output")) {
					@SuppressWarnings("unchecked")
					List<String> values = (List<String>) aDefault.get("get_operation_output");
					if (values.size() == 4 &&
						values.get(0).equals("SELF") &&
						map.containsKey(values.get(1)) &&
						map.get(values.get(1)).getOperations().containsKey(values.get(2)) &&
						!map.get(values.get(1)).getOperations().get(values.get(2)).getOutputs().containsKey(values.get(3))
						) {
						TPropertyDefinition.Builder pBuilder = new TPropertyDefinition.Builder(attr.getType());
						map.get(values.get(1)).getOperations().get(values.get(2)).getOutputs().put(values.get(3), pBuilder.build());
					}
				}
			}
		}

		return map;
	}

	private Map<String, TArtifactDefinition> refactorDeploymentArtifacts(Map<String, TArtifactDefinition> map) {
		return map.entrySet().stream()
			// Filter for deployment artifacts
			.filter(entry -> Objects.nonNull(entry.getValue())
				&& !referenceVisitor.getTypes(entry.getValue().getType(), "TArtifactType").getNames().contains(Defaults.IMPLEMENTATION_ARTIFACTS))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	private Map<String, TArtifactDefinition> refactorImplementationArtifacts(Map<String, TArtifactDefinition> map, org.eclipse.winery.model.tosca.yaml.TNodeType node) {
		Map<String, TArtifactDefinition> implementationArtifacts = new LinkedHashMap<>(map.entrySet().stream()
			// Filter for deployment artifacts
			.filter(entry -> Objects.nonNull(entry.getValue())
				&& !referenceVisitor.getTypes(entry.getValue().getType(), "TArtifactType").getNames().contains(Defaults.DEPLOYMENT_ARTIFACTS))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

		// Convert Interface.Operations Artifacts to ArtifactDefinition
		for (Map.Entry<String, TInterfaceDefinition> entry : node.getInterfaces().entrySet()) {
			entry.getValue().getOperations()
				.entrySet().stream()
				.filter(operation -> operation.getValue() != null && operation.getValue().getImplementation() != null)
				.forEach(operation -> {
					String interfaceName = entry.getKey();
					String operationName = operation.getKey();
					TImplementation implementation = operation.getValue().getImplementation();
					List<QName> list = implementation.getDependencies();
					if (implementation.getPrimary() != null) {
						list.add(implementation.getPrimary());
					}
					for (QName artifactQName : list) {
						String artifactName = artifactQName.getLocalPart();
						if (implementationArtifacts.containsKey(artifactName)) {
							TImplementationArtifactDefinition.Builder iABuilder = new TImplementationArtifactDefinition.Builder(implementationArtifacts.get(artifactName));
							TArtifactDefinition old = implementationArtifacts.get(artifactName);
							// TODO write Test!!!! (see Restrictions section in Artifacts.md
							// Check if implementation artifact is already defined for other interfaces
							if (!(old instanceof TImplementationArtifactDefinition)
								|| ((TImplementationArtifactDefinition) old).getInterfaceName() == null
								|| ((TImplementationArtifactDefinition) old).getInterfaceName().equals(interfaceName)) {
								iABuilder.setInterfaceName(interfaceName);
								// Check if ArtifactDefinition is used in more than one operation implementation 
								if (old instanceof TImplementationArtifactDefinition
									&& ((TImplementationArtifactDefinition) old).getInterfaceName().equals(interfaceName)
									&& !(((TImplementationArtifactDefinition) old).getOperationName().equals(operationName))) {
									iABuilder.setOperationName(null);
								} else {
									iABuilder.setOperationName(operationName);
								}
							} else {
								// if interface is not ImplementationArtifactDefinition
								// or interface not set
								// or interface already defined
								iABuilder.setInterfaceName(null);
							}
							iABuilder.setOperationName(operationName);

							implementationArtifacts.put(artifactName, iABuilder.build());
						}
					}
				});
		}

		return implementationArtifacts;
	}

	/**
	 * Converts TOSCA YAML NodeTypes to TOSCA XML NodeTypes
	 *
	 * @param node TOSCA YAML NodeType
	 * @return TOSCA XML NodeType
	 */
	private TNodeType convert(org.eclipse.winery.model.tosca.yaml.TNodeType node, String id) {
		if (Objects.isNull(node)) return null;
		TNodeType.Builder builder = convert(node, new TNodeType.Builder(id))
			.addRequirementDefinitions(convert(node.getRequirements()))
			.addCapabilityDefinitions(convert(node.getCapabilities()))
			.addInterfaces(convert(refactor(node.getInterfaces(), node)));
		convertNodeTypeImplementation(
			refactorImplementationArtifacts(node.getArtifacts(), node),
			refactorDeploymentArtifacts(node.getArtifacts()),
			id
		);
		return builder.build();
	}

	/**
	 * Converts TOSCA YAML NodeTemplates to TOSCA XML NodeTemplates
	 * Additional TOSCA YAML element metadata is put into TOSCA XML documentation element
	 * Additional TOSCA YAML elements directives and copy are not converted
	 *
	 * @param node TOSCA YAML NodeTemplate
	 * @return TOSCA XML NodeTemplate
	 */
	private TNodeTemplate convert(org.eclipse.winery.model.tosca.yaml.TNodeTemplate node, String id) {
		if (Objects.isNull(node)) return null;
		TNodeTemplate.Builder builder = new TNodeTemplate.Builder(id, node.getType())
			.addDocumentation(node.getDescription())
			.addDocumentation(node.getMetadata())
			.setName(id)
			.setProperties(convertPropertyAssignments(node.getProperties(), getPropertyTypeName(node.getType())))
			.addRequirements(convert(id, node.getRequirements()))
			.addCapabilities(convert(node.getCapabilities()))
			.setDeploymentArtifacts(convertDeploymentArtifacts(node.getArtifacts()));
		TNodeTemplate nodeTemplate = builder.build();
		this.nodeTemplateMap.put(id, nodeTemplate);
		return nodeTemplate;
	}

	private List<TRequirement> convert(String id, List<TMapRequirementAssignment> list) {
		return list.stream()
			.flatMap(map -> map.entrySet().stream())
			.filter(Objects::nonNull)
			.map(entry -> {
				// Find candidates for relationship templates with the names entry.getKey() 
				// and source and target elements (id, ..getNode())
				if (Objects.nonNull(entry.getValue())
					&& Objects.nonNull(entry.getValue().getNode())
					&& Objects.isNull(entry.getValue().getRelationship())
					&& Objects.isNull(entry.getValue().getCapability())
					) {
					int num = 0;
					String name = entry.getKey();
					while (this.relationshipSTMap.containsKey(name)) {
						name = entry.getKey() + num++;
					}
					this.relationshipSTMap.put(
						name,
						new LinkedHashMap.SimpleEntry<>(
							id,
							entry.getValue().getNode().getLocalPart()
						)
					);
				}
				return convert(entry.getValue(), entry.getKey());
			})
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	/**
	 * Constructs the the name of the PropertyType for a given type
	 */
	private QName getPropertyTypeName(QName type) {
		return new QName(type.getNamespaceURI(), type.getLocalPart() + "_Properties");
	}

	/**
	 * Converts TOSCA YAML RequirementDefinition to TOSCA XML RequirementDefinition
	 *
	 * @param node TOSCA YAML RequirementDefinition
	 * @return TOSCA XML RequirementDefinition
	 */
	private TRequirementDefinition convert(org.eclipse.winery.model.tosca.yaml.TRequirementDefinition node, String id) {
		if (Objects.isNull(node)) return null;
		// TOSCA YAML does not have RequirementTypes:
		// * construct TOSCA XML RequirementType from TOSCA YAML Requirement Definition	
		return new TRequirementDefinition.Builder(id,
			convertRequirementDefinition(
				node,
				getRequirementTypeName(node.getCapability(), id)
			))
			.setLowerBound(node.getLowerBound())
			.setUpperBound(node.getUpperBound())
			.build();
	}

	/**
	 * Convert TOSCA YAML RequirementDefinition to TOSCA XML RequirementType
	 *
	 * @param node TOSCA YAML RequirementDefinition
	 * @param id   with name of the TRequirementType
	 * @return QName of the TOSCA XML RequirementType
	 */
	private QName convertRequirementDefinition(org.eclipse.winery.model.tosca.yaml.TRequirementDefinition node, String id) {
		if (node == null) return null;
		String namespace = Optional.ofNullable(node.getCapability()).map(QName::getNamespaceURI).orElse(this.namespace);
		TRequirementType result = new TRequirementType.Builder(id)
			.setRequiredCapabilityType(node.getCapability())
			.setTargetNamespace(namespace)
			.build();
		requirementTypes.add(result);
		return new QName(namespace, result.getName());
	}

	private String getRequirementTypeName(QName capability, String id) {
		if (Objects.isNull(capability)) return id.concat("Type");
		return "Req".concat(capability.getLocalPart());
	}

	/**
	 * Converts TOSCA YAML RequirementAssignments to TOSCA XML Requirements
	 * Additional TOSCA YAML elements node_filter and occurrences are not converted
	 *
	 * @param node TOSCA YAML RequirementAssignments
	 * @return return List of TOSCA XML Requirements
	 */
	private TRequirement convert(TRequirementAssignment node, String id) {
		if (Objects.isNull(node)) return null;
		// Skip requirement if it only the field node is set
		if (Objects.nonNull(node.getNode())
			&& Objects.isNull(node.getCapability())
			&& Objects.isNull(node.getNodeFilter())
			&& node.getOccurrences().isEmpty()
			&& Objects.isNull(node.getRelationship())) return null;

		return new TRequirement.Builder(id, new QName(
			Optional.ofNullable(node.getCapability()).map(QName::getNamespaceURI).orElse(this.namespace),
			getRequirementTypeName(node.getCapability(), id)
		))
			.build();
	}

	/**
	 * Converts TOSCA YAML CapabilityTypes to TOSCA XML CapabilityTypes
	 *
	 * @param node TOSCA YAML CapabilityType
	 * @return TOSCA XML CapabilityType
	 */
	private TCapabilityType convert(org.eclipse.winery.model.tosca.yaml.TCapabilityType node, String id) {
		if (Objects.isNull(node)) return null;
		return convert(node, new TCapabilityType.Builder(id))
			.addTags(convertValidSourceTypes(node.getValidSourceTypes()))
			.build();
	}

	private TTag convertValidSourceTypes(@NonNull List<QName> node) {
		if (node.isEmpty()) return null;
		return new TTag.Builder()
			.setName("valid_source_types")
			.setValue("[" + node.stream().map(QName::toString).collect(Collectors.joining(",")) + "]")
			.build();
	}

	/**
	 * Converts TOSCA YAML CapabilityDefinitions to TOSCA XML CapabilityDefinitions
	 * Additional TOSCA YAML elements properties, attributes and valid_source_types are not converted
	 *
	 * @param node TOSCA YAML CapabilityDefinition
	 * @return TOSCA XML CapabilityDefinition
	 */
	private TCapabilityDefinition convert(org.eclipse.winery.model.tosca.yaml.TCapabilityDefinition node, String id) {
		if (Objects.isNull(node)) return null;
		return new TCapabilityDefinition.Builder(id, node.getType())
			.addDocumentation(node.getDescription())
			.setLowerBound(node.getLowerBound())
			.setUpperBound(node.getUpperBound())
			.build();
	}

	/**
	 * Converts TOSCA YAML InterfaceDefinitions to TOSCA XML Interface
	 * Additional TOSCA YAML element input with PropertyAssignment or PropertyDefinition is not converted
	 *
	 * @param node TOSCA YAML InterfaceDefinition
	 * @return TOSCA XML Interface
	 */
	private TInterface convert(TInterfaceDefinition node, String id) {
		List<TOperation> operation = new ArrayList<>();
		if (this.interfaceTypes.containsKey(node.getType())) {
			operation.addAll(convert(this.interfaceTypes.get(node.getType()).getOperations()));
		}

		operation.addAll(convert(node.getOperations()));

		TInterface.Builder builder = new TInterface.Builder(id, operation);

		return builder.build();
	}

	/**
	 * Convert TOSCA YAML TopologyTemplatesDefinition to TOSCA XML TopologyTemplates
	 * Additional TOSCA YAML elements inputs, outputs, groups, policies,
	 * substitution_mappings and workflows are not converted
	 *
	 * @param node TOSCA YAML TopologyTemplateDefinition
	 * @return TOSCA XML TopologyTemplate
	 */
	private TTopologyTemplate convert(TTopologyTemplateDefinition node) {
		if (node == null) {
			return null;
		}

		TTopologyTemplate.Builder builder = new TTopologyTemplate.Builder();
		builder.addDocumentation(node.getDescription());

		builder.setNodeTemplates(convert(node.getNodeTemplates()));
		builder.setRelationshipTemplates(convert(node.getRelationshipTemplates()));
		convert(node.getPolicies());

		return builder.build();
	}

	/**
	 * Converts TOSCA YAML RelationshipTypes to TOSCA XML RelationshipTypes
	 * Additional element valid_target_types (specifying Capability Types) is not converted
	 *
	 * @param node TOSCA YAML RelationshipType
	 * @return TOSCA XML RelationshipType
	 */
	private TRelationshipType convert(org.eclipse.winery.model.tosca.yaml.TRelationshipType node, String id) {
		if (Objects.isNull(node)) return null;
		return convert(node, new TRelationshipType.Builder(id))
			// TODO source or target interface?
			.addSourceInterfaces(convert(node.getInterfaces()))
			.build();
	}

	/**
	 * Converts TOSCA YAML RelationshipTemplate to TOSCA XML RelationshipTemplate
	 * Additional TOSCA YAML element interfaces is not converted
	 *
	 * @param node TOSCA YAML RelationshipTemplate
	 * @return TOSCA XML RelationshipTemplate
	 */
	private List<TRelationshipTemplate> convert(org.eclipse.winery.model.tosca.yaml.TRelationshipTemplate node, String id) {
		if (node == null) {
			return null;
		}
		List<TRelationshipTemplate> relationshipTemplates = new ArrayList<>();

		int num = 0;
		String idName = id;
		while (this.relationshipSTMap.containsKey(idName)) {
			TRelationshipTemplate.SourceOrTargetElement sourceElement = new TRelationshipTemplate.SourceOrTargetElement();
			sourceElement.setRef(this.nodeTemplateMap.get(this.relationshipSTMap.get(idName).getKey()));
			TRelationshipTemplate.SourceOrTargetElement targetElement = new TRelationshipTemplate.SourceOrTargetElement();
			targetElement.setRef(this.nodeTemplateMap.get(this.relationshipSTMap.get(idName).getValue()));
			relationshipTemplates.add(
				new TRelationshipTemplate.Builder(idName, node.getType(), sourceElement, targetElement).build()
			);
			idName = id + num++;
		}

		return relationshipTemplates;
	}

	/**
	 * Converts TOSCA YAML PolicyTypes to TOSCA XML  PolicyTypes
	 * Additional TOSCA YAML element triggers is not converted
	 *
	 * @param node TOSCA YAML PolicyType
	 * @return TOSCA XML PolicyType
	 */
	private TPolicyType convert(org.eclipse.winery.model.tosca.yaml.TPolicyType node, String id) {
		if (node == null) {
			return null;
		}

		TPolicyType.Builder builder = new TPolicyType.Builder(id);
		convert(node, builder);
		builder.setAppliesTo(convertTargets(node.getTargets()));

		return builder.build();
	}

	/**
	 * Converts TOSCA YAML PolicyDefinitions to TOSCA XML Policy and PolicyTemplate
	 * Additional TOSCA YAML element trigger is not converted
	 *
	 * @param node TOSCA YAML PolicyDefinition
	 */
	private void convert(TPolicyDefinition node, String id) {
		if (node == null) {
			return;
		}

		TPolicyTemplate.Builder builder = new TPolicyTemplate.Builder(id + "_templ", node.getType());
		builder.setName(id);
		builder.setProperties(convertPropertyAssignments(node.getProperties(), getPropertyTypeName(node.getType())));
		this.policyTemplates.add(builder.build());

		TPolicy.Builder pbuilder = new TPolicy.Builder(node.getType());
		pbuilder.setName(id);
		pbuilder.setPolicyRef(new QName(id + "_templ"));

		/* if PolicyDefinition has targets the resulting Policy is added to the target else its added to the
		 * BoundaryDefinition of the Service Template
		 */
		if (node.getTargets() == null || node.getTargets().size() == 0) {
			this.addPolicy("boundary", pbuilder.build());
		} else {
			for (QName target : node.getTargets()) {
				this.addPolicy(target.toString(), pbuilder.build());
			}
		}
	}

	/**
	 * Adds TOSCA XML Policy to Map<String, TPolicy> policies
	 *
	 * @param target Key of the map
	 */
	private void addPolicy(String target, TPolicy policy) {
		if (this.policies.containsKey(target)) {
			this.policies.get(target).add(policy);
		} else {
			List<TPolicy> policies = new ArrayList<>();
			policies.add(policy);
			this.policies.put(target, policies);
		}
	}

	/**
	 * Converts TOSCA YAML TImportDefinitions and returns list of TOSCA XML TImports
	 */
	private TImport convert(TImportDefinition node, String name) {
		Reader reader = new Reader();
		String namespace = node.getNamespaceUri() == null ? this.namespace : node.getNamespaceUri();
		try {
			org.eclipse.winery.model.tosca.yaml.TServiceTemplate serviceTemplate = reader.readImportDefinition(node, path, namespace);
			Converter converter = new Converter(this.repository);
			Definitions definitions = converter.convertY2X(serviceTemplate, getFileNameFromFile(node.getFile()), namespace, path, outPath);
			WriterUtils.saveDefinitions(definitions, outPath, namespace, name);
			TImport.Builder builder = new TImport.Builder(Namespaces.XML_NS);
			builder.setLocation(WriterUtils.getDefinitionsLocation(namespace, name));
			builder.setNamespace(namespace);
			return builder.build();
		} catch (MultiException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getFileNameFromFile(String filename) {
		return filename.substring(filename.lastIndexOf(File.separator) + 1, filename.lastIndexOf("."));
	}

	/**
	 * Convert A list of TOSCA YAML PolicyType targets to TOSCA XML PolicyType AppliesTo
	 *
	 * @param targetList list of TOSCA YAML PolicyType targets
	 * @return TOSCA XML PolicyType AppliesTo
	 */
	private TAppliesTo convertTargets(List<QName> targetList) {
		if (targetList == null || targetList.size() == 0) {
			return null;
		}

		List<TAppliesTo.NodeTypeReference> references = new ArrayList<>();
		for (QName nodeRef : targetList) {
			TAppliesTo.NodeTypeReference ref = new TAppliesTo.NodeTypeReference();
			ref.setTypeRef(nodeRef);
			references.add(ref);
		}

		TAppliesTo appliesTo = new TAppliesTo();
		appliesTo.getNodeTypeReference().addAll(references);
		return appliesTo;
	}

	/**
	 * Converts a map of TOSCA YAML PropertyAssignment to TOSCA XML EntityTemplate.Properties
	 */
	private TEntityTemplate.Properties convertPropertyAssignments(Map<String, TPropertyAssignment> propertyMap, QName type) {
		if (Objects.isNull(propertyMap)) return null;
		TEntityTemplate.Properties properties = new TEntityTemplate.Properties();
		properties.setAny(assignmentBuilder.getAssignment(propertyMap, type));
		return properties;
	}

	/**
	 * Converts TOSCA YAML ArtifactDefinitions to TOSCA XML NodeTypeImplementations and ArtifactTemplates
	 */
	private void convertNodeTypeImplementation(
		Map<String, TArtifactDefinition> implArtifacts,
		Map<String, TArtifactDefinition> deplArtifacts, String type) {
		this.nodeTypeImplementations.add(new TNodeTypeImplementation.Builder(type + "_impl", new QName(type))
			.setDeploymentArtifacts(convertDeploymentArtifacts(deplArtifacts))
			.setImplementationArtifacts(convertImplementationArtifact(implArtifacts))
			.build()
		);
	}

	private TOperation convert(TOperationDefinition node, String id) {
		return new TOperation.Builder(id)
			.addDocumentation(node.getDescription())
			.addInputParameters(convertParameters(node.getInputs()))
			.addOutputParameters(convertParameters(node.getOutputs()))
			.build();
	}

	private List<TParameter> convertParameters(Map<String, TPropertyAssignmentOrDefinition> node) {
		return node.entrySet().stream()
			.map(entry -> {
				if (entry.getValue() instanceof TPropertyDefinition) {
					return convertParameter((TPropertyDefinition) entry.getValue(), entry.getKey());
				} else {
					return null;
				}
			}).filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	private TParameter convertParameter(TPropertyDefinition node, String id) {
		return new TParameter.Builder(
			id,
			TypeConverter.INSTANCE.convert(node.getType()).getLocalPart(),
			node.getRequired()
		).build();
	}

	public void convert(TAttributeDefinition node, String id) {
		// Attributes are not converted
	}

	private Object convert(org.eclipse.winery.model.tosca.yaml.TGroupType node, String name) {
		// GroupTypes are not converted
		return null;
	}

	public Object convert(org.eclipse.winery.model.tosca.yaml.TDataType node, String name) {
		TImport importDefinition = new TImport.Builder(Namespaces.XML_NS)
			.setLocation(Util.URLencode(this.namespace) + ".xsd")
			.build();
		if (!this.imports.contains(importDefinition)) {
			this.imports.add(importDefinition);
		}
		return null;
	}

	@SuppressWarnings({"unchecked"})
	private <V, T> List<T> convert(List<? extends Map<String, V>> node) {
		return node.stream()
			.flatMap(map -> map.entrySet().stream())
			.map((Map.Entry<String, V> entry) -> {
				if (entry.getValue() instanceof TImportDefinition) {
					return (T) convert((TImportDefinition) entry.getValue(), entry.getKey());
				} else if (entry.getValue() instanceof org.eclipse.winery.model.tosca.yaml.TRequirementDefinition) {
					return (T) convert((org.eclipse.winery.model.tosca.yaml.TRequirementDefinition) entry.getValue(), entry.getKey());
				} else if (entry.getValue() instanceof TRequirementAssignment) {
					return (T) convert((TRequirementAssignment) entry.getValue(), entry.getKey());
				} else {
					V v = entry.getValue();
					assert (v instanceof TImportDefinition ||
						v instanceof org.eclipse.winery.model.tosca.yaml.TRequirementDefinition ||
						v instanceof TRequirementAssignment);
					return null;
				}
			})
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	@SuppressWarnings({"unchecked"})
	private <V, T> List<T> convert(@NonNull Map<String, V> map) {
		return map.entrySet().stream()
			.map((Map.Entry<String, V> entry) -> {
				if (entry.getValue() == null) {
					return null;
				} else if (entry.getValue() instanceof org.eclipse.winery.model.tosca.yaml.TRelationshipType) {
					return convert((org.eclipse.winery.model.tosca.yaml.TRelationshipType) entry.getValue(), entry.getKey());
				} else if (entry.getValue() instanceof org.eclipse.winery.model.tosca.yaml.TRelationshipTemplate) {
					return convert((org.eclipse.winery.model.tosca.yaml.TRelationshipTemplate) entry.getValue(), entry.getKey());
				} else if (entry.getValue() instanceof org.eclipse.winery.model.tosca.yaml.TArtifactType) {
					return convert((org.eclipse.winery.model.tosca.yaml.TArtifactType) entry.getValue(), entry.getKey());
				} else if (entry.getValue() instanceof TArtifactDefinition) {
					return convert((TArtifactDefinition) entry.getValue(), entry.getKey());
				} else if (entry.getValue() instanceof org.eclipse.winery.model.tosca.yaml.TCapabilityType) {
					return convert((org.eclipse.winery.model.tosca.yaml.TCapabilityType) entry.getValue(), entry.getKey());
				} else if (entry.getValue() instanceof org.eclipse.winery.model.tosca.yaml.TCapabilityDefinition) {
					return convert((org.eclipse.winery.model.tosca.yaml.TCapabilityDefinition) entry.getValue(), entry.getKey());
				} else if (entry.getValue() instanceof org.eclipse.winery.model.tosca.yaml.TPolicyType) {
					return convert((org.eclipse.winery.model.tosca.yaml.TPolicyType) entry.getValue(), entry.getKey());
				} else if (entry.getValue() instanceof org.eclipse.winery.model.tosca.yaml.TRequirementDefinition) {
					return convert((org.eclipse.winery.model.tosca.yaml.TRequirementDefinition) entry.getValue(), entry.getKey());
				} else if (entry.getValue() instanceof TInterfaceType) {
					assert (!interfaceTypes.containsKey(new QName(entry.getKey())));
					this.interfaceTypes.put(new QName(entry.getKey()), (TInterfaceType) entry.getValue());
					return null;
				} else if (entry.getValue() instanceof TInterfaceDefinition) {
					return convert((TInterfaceDefinition) entry.getValue(), entry.getKey());
				} else if (entry.getValue() instanceof TOperationDefinition) {
					return convert((TOperationDefinition) entry.getValue(), entry.getKey());
				} else if (entry.getValue() instanceof org.eclipse.winery.model.tosca.yaml.TNodeTemplate) {
					return convert((org.eclipse.winery.model.tosca.yaml.TNodeTemplate) entry.getValue(), entry.getKey());
				} else if (entry.getValue() instanceof org.eclipse.winery.model.tosca.yaml.TDataType) {
					return convert((org.eclipse.winery.model.tosca.yaml.TDataType) entry.getValue(), entry.getKey());
				} else if (entry.getValue() instanceof org.eclipse.winery.model.tosca.yaml.TGroupType) {
					return convert((org.eclipse.winery.model.tosca.yaml.TGroupType) entry.getValue(), entry.getKey());
				} else if (entry.getValue() instanceof org.eclipse.winery.model.tosca.yaml.TNodeType) {
					return convert((org.eclipse.winery.model.tosca.yaml.TNodeType) entry.getValue(), entry.getKey());
				} else if (entry.getValue() instanceof TImportDefinition) {
					return convert((TImportDefinition) entry.getValue(), entry.getKey());
				} else if (entry.getValue() instanceof org.eclipse.winery.model.tosca.yaml.TPolicyType) {
					return convert((org.eclipse.winery.model.tosca.yaml.TPolicyType) entry.getValue(), entry.getKey());
				} else {
					V v = entry.getValue();
					System.err.println(v);
					assert (v instanceof org.eclipse.winery.model.tosca.yaml.TRelationshipType ||
						v instanceof org.eclipse.winery.model.tosca.yaml.TRelationshipTemplate ||
						v instanceof org.eclipse.winery.model.tosca.yaml.TArtifactType ||
						v instanceof TArtifactDefinition ||
						v instanceof org.eclipse.winery.model.tosca.yaml.TCapabilityType ||
						v instanceof org.eclipse.winery.model.tosca.yaml.TCapabilityDefinition ||
						v instanceof org.eclipse.winery.model.tosca.yaml.TPolicyType ||
						v instanceof org.eclipse.winery.model.tosca.yaml.TRequirementDefinition ||
						v instanceof TInterfaceType ||
						v instanceof TInterfaceDefinition ||
						v instanceof TOperationDefinition ||
						v instanceof org.eclipse.winery.model.tosca.yaml.TNodeTemplate ||
						v instanceof org.eclipse.winery.model.tosca.yaml.TDataType ||
						v instanceof org.eclipse.winery.model.tosca.yaml.TGroupType ||
						v instanceof org.eclipse.winery.model.tosca.yaml.TNodeType ||
						v instanceof TImportDefinition ||
						v instanceof TPolicyDefinition
					);
					return null;
				}
			})
			.flatMap(entry -> {
				if (entry instanceof List) {
					return ((List<T>) entry).stream();
				} else {
					return (Stream<T>) Stream.of(entry);
				}
			})
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}
}
