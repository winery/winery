/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.yaml.converter.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

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
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TBoolean;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TDocumentation;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TRequirementType;
import org.eclipse.winery.model.tosca.TTag;
import org.eclipse.winery.model.tosca.TTags;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.propertydefinitionkv.PropertyDefinitionKV;
import org.eclipse.winery.model.tosca.propertydefinitionkv.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.model.tosca.yaml.TArtifactDefinition;
import org.eclipse.winery.model.tosca.yaml.TArtifactType;
import org.eclipse.winery.model.tosca.yaml.TCapabilityAssignment;
import org.eclipse.winery.model.tosca.yaml.TCapabilityDefinition;
import org.eclipse.winery.model.tosca.yaml.TCapabilityType;
import org.eclipse.winery.model.tosca.yaml.TImplementation;
import org.eclipse.winery.model.tosca.yaml.TImportDefinition;
import org.eclipse.winery.model.tosca.yaml.TInterfaceDefinition;
import org.eclipse.winery.model.tosca.yaml.TNodeTemplate;
import org.eclipse.winery.model.tosca.yaml.TNodeType;
import org.eclipse.winery.model.tosca.yaml.TOperationDefinition;
import org.eclipse.winery.model.tosca.yaml.TPolicyDefinition;
import org.eclipse.winery.model.tosca.yaml.TPolicyType;
import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;
import org.eclipse.winery.model.tosca.yaml.TPropertyAssignmentOrDefinition;
import org.eclipse.winery.model.tosca.yaml.TPropertyDefinition;
import org.eclipse.winery.model.tosca.yaml.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.yaml.TRelationshipType;
import org.eclipse.winery.model.tosca.yaml.TRequirementAssignment;
import org.eclipse.winery.model.tosca.yaml.TRequirementDefinition;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.model.tosca.yaml.TSubstitutionMappings;
import org.eclipse.winery.model.tosca.yaml.TTopologyTemplateDefinition;
import org.eclipse.winery.model.tosca.yaml.support.Defaults;
import org.eclipse.winery.model.tosca.yaml.support.Metadata;
import org.eclipse.winery.model.tosca.yaml.support.TMapRequirementAssignment;
import org.eclipse.winery.model.tosca.yaml.support.TMapRequirementDefinition;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateFilesDirectoryId;
import org.eclipse.winery.yaml.common.Namespaces;
import org.eclipse.winery.yaml.common.writer.yaml.Writer;
import org.eclipse.winery.yaml.converter.xml.support.TypeConverter;
import org.eclipse.winery.yaml.converter.xml.support.ValueConverter;

import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class X2YConverter {
	public final static Logger LOGGER = LoggerFactory.getLogger(X2YConverter.class);

	private final IRepository repository;
	private final String path;

	private HashBiMap<String, String> prefixNamespace;
	private Map<DefinitionsChildId, Definitions> importDefinitions;

	public X2YConverter(IRepository repository, String path) {
		this.repository = repository;
		this.path = path;
		this.prefixNamespace = new HashBiMap<>();
		this.importDefinitions = new LinkedHashMap<>();
	}

	public Map<File, TServiceTemplate> convert(Definitions node, String outPath) {
		return convert(node, outPath, new QName(node.getTargetNamespace(), node.getIdFromIdOrNameField()));
	}

	/**
	 * Converts TOSCA XML Definitions to TOSCA YAML ServiceTemplates
	 */
	@NonNull
	public Map<File, TServiceTemplate> convert(Definitions node, String outPath, QName name) {
		if (Objects.isNull(node)) return new LinkedHashMap<>();
		QName tmpName = name;
		
		LOGGER.debug("Convert TServiceTemplate: {}", node.getIdFromIdOrNameField());

		TServiceTemplate.Builder builder = new TServiceTemplate.Builder(Defaults.TOSCA_DEFINITIONS_VERSION)
			.setDescription(convertDocumentation(node.getDocumentation()))
			.setArtifactTypes(convert(node.getArtifactTypes()))
			.setCapabilityTypes(convert(node.getCapabilityTypes()))
			.setRelationshipTypes(convert(node.getRelationshipTypes()))
			.setNodeTypes(convert(node.getNodeTypes()))
			.setPolicyTypes(convert(node.getPolicyTypes()));

		if (node.getServiceTemplates().size() == 1) {
			builder.setTopologyTemplate(convert(node.getServiceTemplates().get(0)));
			if (Objects.nonNull(node.getServiceTemplates().get(0).getName())) {
				tmpName = new QName(node.getServiceTemplates().get(0).getName());
			}
			builder.addMetadata("targetNamespace", node.getTargetNamespace());
		}

		importDefinitions.forEach((id, def) -> {
			String path = this.path + File.separator + id.getGroup() + File.separator + id.getNamespace().getEncoded();
			Map<File, TServiceTemplate> map = new X2YConverter(repository, this.path).convert(def, path, id.getQName());
			Optional.ofNullable(map).orElse(new LinkedHashMap<>())
				.forEach((File key, TServiceTemplate value) ->
					builder.addImports(
						key.getName(),
						new TImportDefinition.Builder(
							Paths.get(this.path).relativize(key.toPath()).toString())
							.setNamespaceUri(def.getTargetNamespace())
							.setNamespacePrefix(getNamespacePrefix(def.getTargetNamespace()))
							.build()
					)
				);
		});

		TServiceTemplate serviceTemplate = builder.build();
		File file = new File(outPath + File.separator + tmpName.getLocalPart() + ".yml");
		if (!file.exists()) {
			//noinspection ResultOfMethodCallIgnored
			file.getParentFile().mkdirs();
		}
		Writer writer = new Writer();
		writer.write(serviceTemplate, file.toString());
		return Collections.singletonMap(file, serviceTemplate);
	}

	public Map<String, TPropertyAssignment> convert(TEntityTemplate tEntityTemplate, TEntityTemplate.Properties node) {
		if (Objects.isNull(node)) return null;
		Properties propertiesKV = ModelUtilities.getPropertiesKV(tEntityTemplate);
		if (Objects.isNull(propertiesKV)) return null;
		return propertiesKV.entrySet().stream()
			.map(entry ->
				new LinkedHashMap.SimpleEntry<>(
					entry.getKey().toString(),
					new TPropertyAssignment.Builder()
						.setValue("\"" + ValueConverter.INSTANCE.convert(entry.getValue()) + "\"")
						.build()
				)
			)
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				Map.Entry::getValue
			));
	}

	public TTopologyTemplateDefinition convert(org.eclipse.winery.model.tosca.TServiceTemplate node) {
		if (Objects.isNull(node)) return null;
		return convert(node.getTopologyTemplate(), node.getBoundaryDefinitions());
	}

	public TTopologyTemplateDefinition convert(TTopologyTemplate node, TBoundaryDefinitions boundary) {
		if (Objects.isNull(node)) return null;
		return new TTopologyTemplateDefinition.Builder()
			.setDescription(convertDocumentation(node.getDocumentation()))
			.setNodeTemplates(convert(node.getNodeTemplates(), node.getRelationshipTemplates()))
			.setRelationshipTemplates(convert(node.getRelationshipTemplates()))
			.setPolicies(convert(
				node.getNodeTemplates().stream()
					.filter(Objects::nonNull)
					.map(org.eclipse.winery.model.tosca.TNodeTemplate::getPolicies)
					.filter(Objects::nonNull)
					.flatMap(p -> p.getPolicy().stream())
					.filter(Objects::nonNull)
					.collect(Collectors.toList())
			))
			.setSubstitutionMappings(convert(boundary))
			.build();
	}

	public Map<String, TNodeTemplate> convert(List<org.eclipse.winery.model.tosca.TNodeTemplate> nodes, List<org.eclipse.winery.model.tosca.TRelationshipTemplate> rTs) {
		if (Objects.isNull(nodes)) return null;
		return nodes.stream()
			.filter(Objects::nonNull)
			.flatMap(entry -> convert(entry, Optional.ofNullable(rTs).orElse(new ArrayList<>())).entrySet().stream())
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	@NonNull
	public Map<String, TNodeTemplate> convert(org.eclipse.winery.model.tosca.TNodeTemplate node, @NonNull List<org.eclipse.winery.model.tosca.TRelationshipTemplate> rTs) {
		if (Objects.isNull(node)) return new LinkedHashMap<>();
		return Collections.singletonMap(
			node.getIdFromIdOrNameField(),
			new TNodeTemplate.Builder(
				convert(
					node.getType(),
					new NodeTypeId(node.getType())
				))
				.setProperties(convert(node, node.getProperties()))
				.setRequirements(convert(node.getRequirements()))
				.addRequirements(rTs.stream()
					.filter(entry -> Objects.nonNull(entry.getSourceElement()) && entry.getSourceElement().getRef().equals(node))
					.map(entry -> new LinkedHashMap.SimpleEntry<>(
						Optional.ofNullable(entry.getName()).orElse(entry.getId()),
						new TRequirementAssignment.Builder()
							.setNode(new QName(entry.getTargetElement().getRef().getId()))
							.build()
					))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
				)
				.setCapabilities(convert(node.getCapabilities()))
				.setArtifacts(convert(node.getDeploymentArtifacts()))
				.build()
		);
	}

	@NonNull
	public Map<String, TRelationshipTemplate> convert(org.eclipse.winery.model.tosca.TRelationshipTemplate node) {
		if (Objects.isNull(node)) return new LinkedHashMap<>();
		return Collections.singletonMap(
			node.getIdFromIdOrNameField(),
			new TRelationshipTemplate.Builder(
				convert(
					node.getType(),
					new RelationshipTypeId(node.getType())
				))
				.setProperties(convert(node, node.getProperties()))
				.build()
		);
	}

	public <T extends org.eclipse.winery.model.tosca.yaml.TEntityType.Builder<T>> T convert(TEntityType node, T builder, Class<? extends TEntityType> clazz) {
		return builder.setDescription(convertDocumentation(node.getDocumentation()))
			.setDerivedFrom(convert(node.getDerivedFrom(), clazz))
			.setMetadata(convert(node.getTags()))
			.addMetadata("targetNamespace", node.getTargetNamespace())
			.addMetadata("abstract", node.getAbstract().value())
			.addMetadata("final", node.getFinal().value())
			.setProperties(convert(node, node.getPropertiesDefinition()));
	}

	public Map<String, TPropertyDefinition> convert(TEntityType type, TEntityType.PropertiesDefinition node) {
		// TODO convert properties beside simple winery properties
		WinerysPropertiesDefinition properties = type.getWinerysPropertiesDefinition();
		if (Objects.isNull(properties) ||
			Objects.isNull(properties.getPropertyDefinitionKVList()) ||
			properties.getPropertyDefinitionKVList().isEmpty()) return null;
		return properties.getPropertyDefinitionKVList().stream()
			.collect(Collectors.toMap(
				PropertyDefinitionKV::getKey,
				entry -> new TPropertyDefinition.Builder(convertType(entry.getType()))
					.setRequired(false)
					.build()
			));
	}

	public Map<String, TArtifactType> convert(org.eclipse.winery.model.tosca.TArtifactType node) {
		return Collections.singletonMap(
			node.getIdFromIdOrNameField(),
			convert(node, new TArtifactType.Builder(), org.eclipse.winery.model.tosca.TArtifactType.class).build()
		);
	}

	public Map<String, TNodeType> convert(org.eclipse.winery.model.tosca.TNodeType node) {
		if (Objects.isNull(node)) return null;
		TNodeTypeImplementation impl = getNodeTypeImplementation(new QName(node.getTargetNamespace(), node.getName()));
		return Collections.singletonMap(
			node.getIdFromIdOrNameField(),
			convert(node, new TNodeType.Builder(), org.eclipse.winery.model.tosca.TNodeType.class)
				.setRequirements(convert(node.getRequirementDefinitions()))
				.setCapabilities(convert(node.getCapabilityDefinitions()))
				.setArtifacts(convert(impl))
				.setInterfaces(convert(node.getInterfaces(), impl))
				.build()
		);
	}

	public Map<String, TArtifactDefinition> convert(TNodeTypeImplementation node) {
		if (Objects.isNull(node)) return null;
		return Stream.of(convert(node.getDeploymentArtifacts()), convert(node.getImplementationArtifacts()))
			.filter(Objects::nonNull)
			.flatMap(entry -> entry.entrySet().stream())
			.filter(Objects::nonNull)
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public Map<String, TRelationshipType> convert(org.eclipse.winery.model.tosca.TRelationshipType node) {
		if (Objects.isNull(node)) return null;
		// TODO Use TRelationshipTypeImplementation artifacts
		TRelationshipTypeImplementation impl = getRelationshipTypeImplementation(new QName(node.getTargetNamespace(), node.getName()));
		return Collections.singletonMap(
			node.getIdFromIdOrNameField(),
			convert(node, new TRelationshipType.Builder(), org.eclipse.winery.model.tosca.TRelationshipType.class)
				.addInterfaces(convert(node.getSourceInterfaces(), impl))
				.addInterfaces(convert(node.getTargetInterfaces(), impl))
				.build()
		);
	}

	public Map<String, TInterfaceDefinition> convert(org.eclipse.winery.model.tosca.TRelationshipType.SourceInterfaces node, TRelationshipTypeImplementation implementation) {
		if (Objects.isNull(node)) return null;
		return node.getInterface().stream()
			.filter(Objects::nonNull)
			.map(entry -> convert(
				entry,
				Optional.ofNullable(implementation.getImplementationArtifacts()).orElse(new TImplementationArtifacts())
					.getImplementationArtifact().stream()
					.filter(impl -> Objects.nonNull(impl) && impl.getInterfaceName().equals(entry.getName()))
					.collect(Collectors.toList())
				)
			)
			.flatMap(entry -> entry.entrySet().stream())
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public Map<String, TInterfaceDefinition> convert(org.eclipse.winery.model.tosca.TRelationshipType.TargetInterfaces node, TRelationshipTypeImplementation implementation) {
		if (Objects.isNull(node)) return null;
		return node.getInterface().stream()
			.filter(Objects::nonNull)
			.map(entry -> convert(
				entry,
				Optional.ofNullable(implementation.getImplementationArtifacts()).orElse(new TImplementationArtifacts())
					.getImplementationArtifact().stream()
					.filter(impl -> Objects.nonNull(impl) && impl.getInterfaceName().equals(entry.getName()))
					.collect(Collectors.toList())
				)
			)
			.flatMap(entry -> entry.entrySet().stream())
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	@NonNull
	public Map<String, TCapabilityType> convert(org.eclipse.winery.model.tosca.TCapabilityType node) {
		if (Objects.isNull(node)) return new LinkedHashMap<>();
		return Collections.singletonMap(
			node.getName(),
			convert(node, new TCapabilityType.Builder(), org.eclipse.winery.model.tosca.TCapabilityType.class)
				.build()
		);
	}

	@NonNull
	public Map<String, TPolicyType> convert(org.eclipse.winery.model.tosca.TPolicyType node) {
		if (Objects.isNull(node)) return new LinkedHashMap<>();
		return Collections.singletonMap(
			node.getName(),
			convert(node, new TPolicyType.Builder(), org.eclipse.winery.model.tosca.TPolicyType.class)
				.build()
		);
	}

	public TSubstitutionMappings convert(TBoundaryDefinitions node) {
		if (Objects.isNull(node)) return null;
		return new TSubstitutionMappings.Builder()
			// TODO Convert Boundary definitions
			.build();
	}

	@NonNull
	public Map<String, TPolicyDefinition> convert(TPolicy node) {
		if (Objects.isNull(node)) return new LinkedHashMap<>();
		return Collections.singletonMap(
			node.getName(),
			new TPolicyDefinition.Builder(
				convert(
					node.getPolicyType(),
					new PolicyTypeId(node.getPolicyRef())
				))
				.setProperties(convertPropertyAssignment(repository.getElement(new PolicyTemplateId(node.getPolicyRef()))))
				.build()
		);
	}

	public Map<String, TPropertyAssignment> convertPropertyAssignment(TPolicyTemplate node) {
		if (Objects.isNull(node)) return null;
		return convert(node, node.getProperties());
	}

	/**
	 * Converts TOSCA XML list of Documentations to TOSCA YAML Description of type string
	 */
	public String convertDocumentation(@NonNull List<TDocumentation> doc) {
		return doc.stream()
			.map(TDocumentation::getContent)
			.flatMap(List::stream)
			.map(Object::toString)
			.collect(Collectors.joining("\n"));
	}

	public QName convert(TEntityType.DerivedFrom node, Class<? extends TEntityType> clazz) {
		if (Objects.isNull(node)) return null;
		DefinitionsChildId id;
		if (clazz.equals(org.eclipse.winery.model.tosca.TNodeType.class)) {
			id = new NodeTypeId(node.getTypeRef());
		} else if (clazz.equals(org.eclipse.winery.model.tosca.TRelationshipType.class)) {
			id = new RelationshipTypeId(node.getTypeRef());
		} else if (clazz.equals(TRequirementType.class)) {
			id = new RequirementTypeId(node.getTypeRef());
		} else if (clazz.equals(org.eclipse.winery.model.tosca.TCapabilityType.class)) {
			id = new CapabilityTypeId(node.getTypeRef());
		} else if (clazz.equals(org.eclipse.winery.model.tosca.TArtifactType.class)) {
			id = new ArtifactTypeId(node.getTypeRef());
		} else {
			id = new PolicyTypeId(node.getTypeRef());
		}
		return getQName(
			id,
			node.getTypeRef().getNamespaceURI(),
			node.getTypeRef().getLocalPart());
	}

	public Metadata convert(TTags node) {
		if (Objects.isNull(node)) return null;
		return node.getTag().stream()
			.filter(Objects::nonNull)
			.collect(Collectors.toMap(
				TTag::getName,
				TTag::getValue,
				(a, b) -> a + "|" + b,
				Metadata::new));
	}

	public Map<String, TInterfaceDefinition> convert(org.eclipse.winery.model.tosca.TNodeType.Interfaces node, TNodeTypeImplementation implementation) {
		if (Objects.isNull(node)) return null;
		return node.getInterface().stream()
			.filter(Objects::nonNull)
			.map(entry -> convert(
				entry,
				Optional.ofNullable(implementation.getImplementationArtifacts()).orElse(new TImplementationArtifacts())
					.getImplementationArtifact().stream()
					.filter(impl -> Objects.nonNull(impl) && impl.getInterfaceName().equals(entry.getName()))
					.collect(Collectors.toList())
				)
			)
			.flatMap(entry -> entry.entrySet().stream())
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	@NonNull
	public Map<String, TInterfaceDefinition> convert(TInterface node, @NonNull List<TImplementationArtifact> impl) {
		if (Objects.isNull(node)) return new LinkedHashMap<>();
		return Collections.singletonMap(
			node.getName(),
			new TInterfaceDefinition.Builder()
				.setOperations(convertOperations(node.getOperation(), impl))
				.build()
		);
	}

	public Map<String, TOperationDefinition> convertOperations(List<TOperation> nodes, @NonNull List<TImplementationArtifact> impl) {
		if (Objects.isNull(nodes)) return null;
		return nodes.stream()
			.filter(Objects::nonNull)
			.flatMap(node -> convert(
				node,
				impl.stream()
					.filter(entry -> Objects.nonNull(entry)
						&& Objects.nonNull(entry.getOperationName())
						&& entry.getOperationName().equals(node.getName()))
					.collect(Collectors.toList())
			).entrySet().stream())
			.filter(Objects::nonNull)
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	@NonNull
	public Map<String, TOperationDefinition> convert(TOperation node, List<TImplementationArtifact> impl) {
		if (Objects.isNull(node)) return new LinkedHashMap<>();
		return Collections.singletonMap(
			node.getName(),
			new TOperationDefinition.Builder()
				.setInputs(convert(node.getInputParameters()))
				.setOutputs(convert(node.getOutputParameters()))
				.setImplementation(convertImplementation(impl))
				.build()
		);
	}

	public TImplementation convertImplementation(List<TImplementationArtifact> node) {
		if (Objects.isNull(node) || node.isEmpty()) return null;
		List<TImplementationArtifact> tmp = new ArrayList<>(node);
		return new TImplementation.Builder(new QName(tmp.remove(0).getName()))
			.setDependencies(tmp.stream()
				.filter(artifact -> Objects.nonNull(artifact) && Objects.nonNull(artifact.getName()))
				.map(artifact -> new QName(artifact.getName()))
				.collect(Collectors.toList())
			)
			.build();
	}

	public List<TMapRequirementDefinition> convert(org.eclipse.winery.model.tosca.TNodeType.RequirementDefinitions node) {
		if (Objects.isNull(node)) return null;
		return node.getRequirementDefinition().stream()
			.filter(Objects::nonNull)
			.map(this::convert)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	public Map<String, TArtifactDefinition> convert(TDeploymentArtifacts node) {
		if (Objects.isNull(node)) return null;
		return node.getDeploymentArtifact().stream()
			.filter(Objects::nonNull)
			.map(ia -> new LinkedHashMap.SimpleEntry<>(ia.getName(), convertArtifactReference(ia.getArtifactRef())))
			.filter(Objects::nonNull)
			.filter(entry -> Objects.nonNull(entry.getValue()))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public Map<String, TArtifactDefinition> convert(TImplementationArtifacts node) {
		if (Objects.isNull(node)) return null;
		return node.getImplementationArtifact().stream()
			.filter(Objects::nonNull)
			.map(ia -> new LinkedHashMap.SimpleEntry<>(ia.getName(), convertArtifactReference(ia.getArtifactRef())))
			.filter(Objects::nonNull)
			.filter(entry -> Objects.nonNull(entry.getValue()))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public TArtifactDefinition convertArtifactReference(QName ref) {
		if (Objects.isNull(ref)) return null;
		return convert(new ArtifactTemplateId(ref));
	}

	public TArtifactDefinition convert(ArtifactTemplateId id) {
		TArtifactTemplate node = repository.getElement(id);
		List<String> files = Optional.ofNullable(repository.getContainedFiles(new ArtifactTemplateFilesDirectoryId(id)))
			.orElse(new TreeSet<>())
			.stream()
			.map(ref -> {
				try {
					InputStream inputStream = repository.newInputStream(ref);
					Path path = Paths.get(this.path, id.getGroup(), id.getNamespace().getEncoded(), node.getIdFromIdOrNameField(), ref.getFileName());
					if (!path.toFile().exists()) {
						//noinspection ResultOfMethodCallIgnored
						path.getParent().toFile().mkdirs();
						Files.copy(inputStream, path);
					}
					return Paths.get(this.path).relativize(path).toString();
				} catch (IOException e) {
					LOGGER.error("Failed to copy Artifact file", e);
					return null;
				}
			})
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
		if (Objects.isNull(node) || Objects.isNull(node.getType()) || Objects.isNull(files) || files.isEmpty())
			return null;
		return new TArtifactDefinition.Builder(getQName(
			new ArtifactTypeId(node.getType()),
			node.getType().getNamespaceURI(),
			node.getType().getLocalPart()
		), files)
			.build();
	}

	public TMapRequirementDefinition convert(org.eclipse.winery.model.tosca.TRequirementDefinition node) {
		if (Objects.isNull(node)) return null;
		QName type = node.getRequirementType();
		if (Objects.isNull(type)) return null;
		TRequirementType requirementType = repository.getElement(new RequirementTypeId(type));
		if (Objects.isNull(requirementType)
			|| Objects.isNull(requirementType.getRequiredCapabilityType())) return null;

		return new TMapRequirementDefinition().setMap(
			Collections.singletonMap(
				node.getName(),
				new TRequirementDefinition.Builder(convert(requirementType))
					.setDescription(convertDocumentation(node.getDocumentation()))
					.setOccurrences(node.getLowerBound(), node.getUpperBound())
					.build()
			)
		);
	}

	public QName convert(@NonNull TRequirementType node) {
		return getQName(
			new CapabilityTypeId(node.getRequiredCapabilityType()),
			node.getRequiredCapabilityType().getNamespaceURI(),
			node.getRequiredCapabilityType().getLocalPart()
		);
	}

	public Map<String, TCapabilityDefinition> convert(org.eclipse.winery.model.tosca.TNodeType.CapabilityDefinitions node) {
		if (Objects.isNull(node) || node.getCapabilityDefinition().isEmpty()) return null;
		return node.getCapabilityDefinition().stream()
			.filter(Objects::nonNull)
			.collect(Collectors.toMap(
				org.eclipse.winery.model.tosca.TCapabilityDefinition::getName,
				this::convert
			));
	}

	public TCapabilityDefinition convert(org.eclipse.winery.model.tosca.TCapabilityDefinition node) {
		return new TCapabilityDefinition.Builder(
			convert(
				node.getCapabilityType(),
				new CapabilityTypeId(node.getCapabilityType())
			))
			.setDescription(convertDocumentation(node.getDocumentation()))
			.setOccurrences(node.getLowerBound(), node.getUpperBound())
			.build();
	}

	public QName convert(QName node, DefinitionsChildId id) {
		if (Objects.isNull(node)) return null;
		return getQName(
			id,
			node.getNamespaceURI(),
			node.getLocalPart()
		);
	}

	public Map<String, TInterfaceDefinition> convert(org.eclipse.winery.model.tosca.TRelationshipType.SourceInterfaces node) {
		if (Objects.isNull(node)) return null;
		return node.getInterface().stream()
			.filter(Objects::nonNull)
			.collect(Collectors.toMap(
				TInterface::getName,
				entry -> new TInterfaceDefinition.Builder()
					.addOperations(convertOperations(entry.getOperation(), new ArrayList<>()))
					.build()
			));
	}

	public Map<String, TInterfaceDefinition> convert(org.eclipse.winery.model.tosca.TRelationshipType.TargetInterfaces node) {
		if (Objects.isNull(node)) return null;
		return node.getInterface().stream()
			.filter(Objects::nonNull)
			.collect(Collectors.toMap(
				TInterface::getName,
				entry -> new TInterfaceDefinition.Builder()
					.addOperations(convert(entry.getOperation()))
					.addOperations(convertOperations(entry.getOperation(), new ArrayList<>()))
					.build()
			));
	}

	public Map<String, TPropertyAssignmentOrDefinition> convert(TOperation.InputParameters node) {
		if (Objects.isNull(node)) return null;
		return node.getInputParameter().stream()
			.filter(Objects::nonNull)
			.collect(Collectors.toMap(
				TParameter::getName,
				entry -> new TPropertyDefinition.Builder(convertType(entry.getType()))
					.setRequired(convert(entry.getRequired()))
					.build()
			));
	}

	public Map<String, TPropertyAssignmentOrDefinition> convert(TOperation.OutputParameters node) {
		if (Objects.isNull(node)) return null;
		return node.getOutputParameter().stream()
			.filter(Objects::nonNull)
			.collect(Collectors.toMap(
				TParameter::getName,
				entry -> new TPropertyDefinition.Builder(convertType(entry.getType()))
					.setRequired(convert(entry.getRequired()))
					.build()
			));
	}

	public boolean convert(TBoolean node) {
		return Objects.nonNull(node) && node.equals(TBoolean.YES);
	}

	private QName convertType(String type) {
		return TypeConverter.INSTANCE.convert(type);
	}

	public Map<String, TCapabilityAssignment> convert(org.eclipse.winery.model.tosca.TNodeTemplate.Capabilities node) {
		if (Objects.isNull(node)) return null;
		return node.getCapability().stream()
			.filter(Objects::nonNull)
			.map(this::convert)
			.filter(Objects::nonNull)
			.flatMap(map -> map.entrySet().stream())
			.filter(Objects::nonNull)
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public Map<String, TCapabilityAssignment> convert(TCapability node) {
		if (Objects.isNull(node)) return null;
		return Collections.singletonMap(
			node.getName(),
			new TCapabilityAssignment.Builder()
				.setProperties(convert(node, node.getProperties()))
				.build()
		);
	}

	public List<TMapRequirementAssignment> convert(org.eclipse.winery.model.tosca.TNodeTemplate.Requirements node) {
		if (Objects.isNull(node)) return null;
		return node.getRequirement().stream()
			.filter(Objects::nonNull)
			.map(this::convert)
			.collect(Collectors.toList());
	}

	public TMapRequirementAssignment convert(TRequirement node) {
		if (Objects.isNull(node)) return null;
		return new TMapRequirementAssignment().setMap(Collections.singletonMap(
			node.getName(),
			new TRequirementAssignment.Builder()
				.setCapability(convert(repository.getElement(new RequirementTypeId(node.getType()))))
				.build()
		));
	}

	private <T, K> Map<String, K> convert(List<T> nodes) {
		return nodes.stream()
			.filter(Objects::nonNull)
			.flatMap(node -> {
				if (node instanceof org.eclipse.winery.model.tosca.TRelationshipTemplate) {
					return convert((org.eclipse.winery.model.tosca.TRelationshipTemplate) node).entrySet().stream();
				} else if (node instanceof org.eclipse.winery.model.tosca.TArtifactType) {
					return convert((org.eclipse.winery.model.tosca.TArtifactType) node).entrySet().stream();
				} else if (node instanceof org.eclipse.winery.model.tosca.TCapabilityType) {
					return convert((org.eclipse.winery.model.tosca.TCapabilityType) node).entrySet().stream();
				} else if (node instanceof org.eclipse.winery.model.tosca.TRelationshipType) {
					return convert((org.eclipse.winery.model.tosca.TRelationshipType) node).entrySet().stream();
				} else if (node instanceof org.eclipse.winery.model.tosca.TNodeType) {
					return convert((org.eclipse.winery.model.tosca.TNodeType) node).entrySet().stream();
				} else if (node instanceof org.eclipse.winery.model.tosca.TPolicyType) {
					return convert((org.eclipse.winery.model.tosca.TPolicyType) node).entrySet().stream();
				}
				throw new AssertionError();
			})
			.peek(entry -> LOGGER.debug("entry: {}", entry))
			.filter(Objects::nonNull)
			.collect(Collectors.toMap(Map.Entry::getKey, entry -> (K) entry.getValue()));
	}

	private String getNamespacePrefix(String uri) {
		if (!prefixNamespace.containsValue(uri)) {
			String prefix = repository.getNamespaceManager().getPrefix(uri);
			if ("tosca".equals(prefix) && !uri.equals(Namespaces.TOSCA_NS)) prefix = prefix.concat("_xml");
			prefixNamespace.put(prefix, uri);
		}
		return prefixNamespace.inverse().get(uri);
	}

	private QName getQName(DefinitionsChildId id, String namespaceURI, String localPart) {
		setImportDefinition(id);
		return new QName(
			namespaceURI,
			localPart,
			this.getNamespacePrefix(namespaceURI)
		);
	}

	private void setImportDefinition(DefinitionsChildId id) {
		this.importDefinitions.put(
			id, repository.getDefinitions(id)
		);
	}

	private TNodeTypeImplementation getNodeTypeImplementation(QName nodeType) {
		return repository.getAllDefinitionsChildIds(NodeTypeImplementationId.class)
			.stream()
			.map(repository::getElement)
			.filter(entry -> entry.getNodeType().equals(nodeType))
			.findAny().orElse(new TNodeTypeImplementation());
	}

	private TRelationshipTypeImplementation getRelationshipTypeImplementation(QName relationshipType) {
		return repository.getAllDefinitionsChildIds(RelationshipTypeImplementationId.class)
			.stream()
			.map(repository::getElement)
			.filter(entry -> entry.getRelationshipType().equals(relationshipType))
			.findAny().orElse(new TRelationshipTypeImplementation());
	}
}
