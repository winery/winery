/*******************************************************************************
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
 *******************************************************************************/
package org.eclipse.winery.repository.backend.filebased;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.Constants;
import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.GenericId;
import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.XmlId;
import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.common.ids.definitions.CapabilityTypeId;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.common.ids.definitions.InterfaceTypeId;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.common.ids.definitions.PolicyTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeImplementationId;
import org.eclipse.winery.common.ids.definitions.RequirementTypeId;
import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.common.version.WineryVersion;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.yaml.TArtifactDefinition;
import org.eclipse.winery.model.tosca.yaml.TImplementation;
import org.eclipse.winery.model.tosca.yaml.TImportDefinition;
import org.eclipse.winery.model.tosca.yaml.TInterfaceDefinition;
import org.eclipse.winery.model.tosca.yaml.TNodeType;
import org.eclipse.winery.model.tosca.yaml.TOperationDefinition;
import org.eclipse.winery.model.tosca.yaml.TRelationshipType;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.model.tosca.yaml.TTopologyTemplateDefinition;
import org.eclipse.winery.model.tosca.yaml.support.Defaults;
import org.eclipse.winery.model.tosca.yaml.support.TMapImportDefinition;
import org.eclipse.winery.repository.JAXBSupport;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.constants.MediaTypes;
import org.eclipse.winery.repository.converter.X2YConverter;
import org.eclipse.winery.repository.converter.Y2XConverter;
import org.eclipse.winery.repository.converter.support.exception.MultiException;
import org.eclipse.winery.repository.converter.support.reader.YamlReader;
import org.eclipse.winery.repository.converter.support.writer.YamlWriter;

import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YamlRepository extends AbstractFileBasedRepository {

    public static final QName ROOT_TYPE_QNAME = new QName("tosca.entity", "Root");

    private static final Logger LOGGER = LoggerFactory.getLogger(YamlRepository.class);

    private final Pattern namePattern;

    public YamlRepository(Path repositoryRoot) {
        super(repositoryRoot);

        this.fileSystem = this.getRepositoryRoot().getFileSystem();
        this.provider = this.fileSystem.provider();

        this.isLocal = this.getRepositoryRoot().getFileName().toString().equals(Constants.DEFAULT_LOCAL_REPO_NAME);
        LOGGER.debug("Repository root: {}", this.getRepositoryRoot());

        String nameRegex = "(.*)@(.*)@(.*)";
        this.namePattern = Pattern.compile(nameRegex);
    }

    /**
     * Converts RepositoryFileReference to compatible YAML File
     *
     * @param ref RepositoryFileReference
     * @return compatible Path
     **/
    @Override
    public Path ref2AbsolutePath(RepositoryFileReference ref) {
        Path resultPath = super.ref2AbsolutePath(ref);
        GenericId convertedId = convertGenericId(ref.getParent());
        if (convertedId != null) {
            if (convertedId instanceof DefinitionsChildId) {
                String convertedFilename = BackendUtils.getFileNameOfDefinitions((DefinitionsChildId) convertedId);
                return resultPath.resolve(convertedFilename);
            }
        }
        return resultPath;
    }

    /**
     * Converts Generic to compatible YAML Folder
     *
     * @param id GenericId
     * @return compatible Path
     **/
    @Override
    public Path id2AbsolutePath(GenericId id) {
        GenericId convertedId = convertGenericId(id);
        if (convertedId != null) {
            return super.id2AbsolutePath(convertedId);
        } else {
            return super.id2AbsolutePath(id);
        }
    }

    /**
     * Converts Generic id of non existing XML Definitions in compatible YAML Definition
     *
     * @param id GenericId
     * @return converted Generic Id
     **/
    private GenericId convertGenericId(GenericId id) {
        if (id instanceof NodeTypeImplementationId) {
            return new NodeTypeId(((NodeTypeImplementationId) id).getQName());
        } else if (id instanceof RelationshipTypeImplementationId) {
            return new RelationshipTypeId(((RelationshipTypeImplementationId) id).getQName());
        } else if (id instanceof ArtifactTemplateId) {
            QName qName = ((ArtifactTemplateId) id).getQName();
            Matcher nameMatcher = namePattern.matcher(qName.getLocalPart());
            if (nameMatcher.matches()) {
                String typeName = nameMatcher.group(2);
                if (nameMatcher.group(3).equalsIgnoreCase("nodetypes")) {
                    return new NodeTypeId(new QName(qName.getNamespaceURI(), typeName));
                } else {
                    return new RelationshipTypeId(new QName(qName.getNamespaceURI(), typeName));
                }
            } else {
                return new NodeTypeId(new QName(qName.getNamespaceURI(), "Cache"));
            }
        }
        return null;
    }

    /**
     * Converts idClass of non existing XML Definitions to compatible YAML id Classes
     *
     * @param idClasses id Class of target
     * @return converted id Classes
     **/
    @SuppressWarnings("unchecked")
    private <T extends DefinitionsChildId> List<Class<T>> convertDefinitionsChildIdIfNeeded(List<Class<T>> idClasses) {
        List<Class<T>> output = new ArrayList<>();
        if (idClasses.size() == 1) {
            Class<T> idClass = idClasses.get(0);
            if (NodeTypeImplementationId.class.isAssignableFrom(idClass)) {
                output.add((Class<T>) NodeTypeId.class);
                return output;
            } else if (RelationshipTypeImplementationId.class.isAssignableFrom(idClass)) {
                output.add((Class<T>) RelationshipTypeId.class);
                return output;
            } else if (ArtifactTemplateId.class.isAssignableFrom(idClass)) {
                output.add((Class<T>) NodeTypeId.class);
                output.add((Class<T>) RelationshipTypeId.class);
                return output;
            }
        }
        return idClasses;
    }

    /**
     * Checks if YAML Definition exists Artifact Templates are searched in Type
     *
     * @param id generic id of target
     * @return boolean if target exists
     **/
    @Override
    public boolean exists(GenericId id) {
        Path targetPath = id2AbsolutePath(id);
        if (id instanceof ArtifactTemplateId) {
            GenericId convertedId = convertGenericId(id);
            if (convertedId != null) {
                String convertedFilename = BackendUtils.getFileNameOfDefinitions((DefinitionsChildId) convertedId);
                targetPath = targetPath.resolve(convertedFilename);
                return artifactTemplateExistsInType(targetPath, ((ArtifactTemplateId) id).getQName());
            }
        }
        return Files.exists(targetPath);
    }

    /**
     * Checks if referenced File in exists Artifact Templates are searched in Type
     *
     * @param ref Repository File Reference
     * @return boolean if target exists
     **/
    @Override
    public boolean exists(RepositoryFileReference ref) {
        Path targetPath = this.ref2AbsolutePath(ref);
        if (ref.getParent() instanceof ArtifactTemplateId) {
            if (Files.exists(targetPath)) {
                return artifactTemplateExistsInType(targetPath, ((ArtifactTemplateId) ref.getParent()).getQName());
            }
        }
        return Files.exists(targetPath);
    }

    /**
     * Returns name of the artifact that is contained in the artifact name
     *
     * @param name name string
     * @return artifact name
     **/
    private String getNameOfArtifactFromArtifactName(String name) {
        Matcher nameMatcher = namePattern.matcher(name);
        if (nameMatcher.matches()) {
            return nameMatcher.group(1);
        }
        return name;
    }

    /**
     * Returns name of the type that is contained in the artifact name
     *
     * @param name name string
     * @return type name
     **/
    private String getNameOfTypeFromArtifactName(String name) {
        Matcher nameMatcher = namePattern.matcher(name);
        if (nameMatcher.matches()) {
            return nameMatcher.group(2);
        }
        return "Cache";
    }

    /**
     * Returns types main folder that is contained in the artifact name
     *
     * @param name name string
     * @return folder name
     **/
    private String getTypeFromArtifactName(String name) {
        Matcher nameMatcher = namePattern.matcher(name);
        if (nameMatcher.matches()) {
            return nameMatcher.group(3);
        }
        return "nodetypes";
    }

    /**
     * Deletes referenced File Does not delete implementations anymore Deletes artifacts from there referenced type
     *
     * @param ref Repository File Reference
     **/
    @Override
    public void forceDelete(RepositoryFileReference ref) throws IOException {
        if (ref.getParent() instanceof NodeTypeImplementationId || ref.getParent() instanceof RelationshipTypeImplementationId) {
            return;
        }

        if (ref.getParent() instanceof ArtifactTemplateId) {
            deleteArtifact((ArtifactTemplateId) ref.getParent());
        } else {
            super.forceDelete(ref);
        }
    }

    /**
     * Deletes referenced Definition Does not delete implementations anymore Deletes artifacts from there referenced
     * type
     *
     * @param id generic id
     **/
    @Override
    public void forceDelete(GenericId id) {
        if (id instanceof NodeTypeImplementationId || id instanceof RelationshipTypeImplementationId) {
            return;
        }
        if (id instanceof ArtifactTemplateId) {
            deleteArtifact((ArtifactTemplateId) id);
        } else {
            super.forceDelete(id);
        }
    }

    /**
     * Deletes artifacts from there referenced type
     *
     * @param id Artifact Template id
     **/
    private void deleteArtifact(ArtifactTemplateId id) {
        if (getNameOfTypeFromArtifactName(id.getQName().getLocalPart()).equalsIgnoreCase("Cache")) {
            super.forceDelete(id);
        } else {
            Path targetPath = id2AbsolutePath(id);
            GenericId convertedId = convertGenericId(id);
            if (convertedId != null) {
                if (convertedId instanceof DefinitionsChildId) {
                    String convertedFilename = BackendUtils.getFileNameOfDefinitions((DefinitionsChildId) convertedId);
                    targetPath = targetPath.resolve(convertedFilename);
                }
            }

            if (Files.exists(targetPath)) {
                try {
                    TServiceTemplate nodeType = readServiceTemplate(targetPath);
                    String targetArtifactName = getNameOfArtifactFromArtifactName(id.getQName().getLocalPart());
                    if (getTypeFromArtifactName(id.getQName().getLocalPart()).equalsIgnoreCase("nodetypes")) {
                        Map<String, TArtifactDefinition> artifacts = nodeType.getNodeTypes().entrySet().iterator().next().getValue().getArtifacts();
                        nodeType.getNodeTypes().entrySet().iterator().next().setValue(removeImplementation(nodeType.getNodeTypes().entrySet().iterator().next().getValue(), targetArtifactName));
                        artifacts.remove(targetArtifactName);
                        nodeType.getNodeTypes().entrySet().iterator().next().getValue().setArtifacts(artifacts);
                    } else {
                        nodeType.getRelationshipTypes().entrySet().iterator().next().setValue(removeRelationshipArtifact(nodeType.getRelationshipTypes().entrySet().iterator().next().getValue(), targetArtifactName));
                    }
                    YamlWriter writer = new YamlWriter();
                    InputStream output = writer.writeToInputStream(nodeType);
                    writeInputStreamToPath(targetPath, output);
                } catch (Exception e) {
                    LOGGER.error("Error deleting file: {}", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Deletes artifact from yaml relationship type
     *
     * @param relationshipType   TRelationshipType
     * @param targetArtifactName targeted artifact name
     * @return updated node type
     **/
    private TRelationshipType removeRelationshipArtifact(TRelationshipType relationshipType, String targetArtifactName) {
        Map<String, TInterfaceDefinition> interfaces = relationshipType.getInterfaces();
        if (interfaces != null) {
            for (Map.Entry<String, TInterfaceDefinition> interfaceDefinition : interfaces.entrySet()) {
                Map<String, TOperationDefinition> operations = interfaceDefinition.getValue().getOperations();
                if (operations != null) {
                    TOperationDefinition operationWithImplementation = operations.get(targetArtifactName);
                    if (operationWithImplementation != null) {
                        operationWithImplementation.setImplementation(null);
                        operations.replace(targetArtifactName, operationWithImplementation);
                    } else {
                        for (Map.Entry<String, TOperationDefinition> operation : operations.entrySet()) {
                            TOperationDefinition operationDefinition = operation.getValue();
                            if (operationDefinition != null) {
                                TImplementation implementation = operationDefinition.getImplementation();
                                if (implementation != null) {
                                    if (implementation.getPrimaryArtifactName() != null) {
                                        if (implementation.getPrimaryArtifactName().equalsIgnoreCase(targetArtifactName)) {
                                            operationDefinition.setImplementation(null);
                                        } else {
                                            if (implementation.getDependencyArtifactNames() != null) {
                                                List<String> names = implementation.getDependencyArtifactNames();
                                                for (String name : implementation.getDependencyArtifactNames()) {
                                                    if (name.equalsIgnoreCase(targetArtifactName)) {
                                                        names.remove(name);
                                                    }
                                                }
                                                implementation.setDependencyArtifactNames(names);
                                            }
                                        }
                                    }
                                    operationDefinition.setImplementation(implementation);
                                }
                            }
                            operation.setValue(operationDefinition);
                        }
                    }
                }
                TInterfaceDefinition tInterfaceDefinition = interfaceDefinition.getValue();
                tInterfaceDefinition.setOperations(operations);
                interfaceDefinition.setValue(tInterfaceDefinition);
            }
            relationshipType.setInterfaces(interfaces);
        }
        return relationshipType;
    }

    /**
     * Deletes artifact from yaml node type interfaces
     *
     * @param nodeType           TNodeType
     * @param targetArtifactName targeted artifact name
     * @return updated node type
     **/
    private TNodeType removeImplementation(TNodeType nodeType, String targetArtifactName) {
        Map<String, TInterfaceDefinition> interfaces = nodeType.getInterfaces();
        if (interfaces != null) {
            for (Map.Entry<String, TInterfaceDefinition> interfaceDefinition : interfaces.entrySet()) {
                Map<String, TOperationDefinition> operations = interfaceDefinition.getValue().getOperations();
                if (operations != null) {
                    for (Map.Entry<String, TOperationDefinition> operation : operations.entrySet()) {
                        TOperationDefinition operationDefinition = operation.getValue();
                        if (operationDefinition != null) {
                            TImplementation implementation = operationDefinition.getImplementation();
                            if (implementation != null) {
                                if (implementation.getPrimaryArtifactName() != null) {
                                    // TODO
                                    if (implementation.getPrimaryArtifactName().equalsIgnoreCase(targetArtifactName)) {
                                        operationDefinition.setImplementation(null);
                                    } else {
                                        if (implementation.getDependencyArtifactNames() != null) {
                                            List<String> names = implementation.getDependencyArtifactNames();
                                            for (String name : implementation.getDependencyArtifactNames()) {
                                                if (name.equalsIgnoreCase(targetArtifactName)) {
                                                    names.remove(name);
                                                }
                                            }
                                            implementation.setDependencyArtifactNames(names);
                                        }
                                    }
                                }
                                operationDefinition.setImplementation(implementation);
                            }
                        }
                        operation.setValue(operationDefinition);
                    }
                }
                TInterfaceDefinition tInterfaceDefinition = interfaceDefinition.getValue();
                tInterfaceDefinition.setOperations(operations);
                interfaceDefinition.setValue(tInterfaceDefinition);
            }
            nodeType.setInterfaces(interfaces);
        }
        return nodeType;
    }

    /**
     * Gets yaml service template from ref and converts it to xml definitions
     *
     * @param ref Repository File Reference
     * @return xml definitions
     **/
    @Override
    public Definitions definitionsFromRef(RepositoryFileReference ref) throws IOException {
        Path targetPath = this.ref2AbsolutePath(ref);
        if (ref.getParent() instanceof DefinitionsChildId) {
            try {
                QName name = ((DefinitionsChildId) ref.getParent()).getQName();
                Definitions definitions = convertToDefinitions(targetPath, name.getLocalPart(), name.getNamespaceURI());
                return getRequestedDefinition((DefinitionsChildId) ref.getParent(), definitions);
            } catch (MultiException e) {
                LOGGER.debug("Internal error", e);
            }
        }
        return null;
    }

    /**
     * Parses only requested Definition from converted yaml service template
     *
     * @param id          Definitions Child id
     * @param definitions converted definitions
     * @return requested definitions
     **/
    private Definitions getRequestedDefinition(DefinitionsChildId id, Definitions definitions) {
        if (id instanceof ArtifactTemplateId) {
            String artifactName = getNameOfArtifactFromArtifactName(id.getQName().getLocalPart());
            List<TArtifactTemplate> artifactTemplates = definitions.getArtifactTemplates();
            List<TArtifactTemplate> requestedArtifactTemplates = new ArrayList<>();
            for (TArtifactTemplate artifactTemplate : artifactTemplates) {
                if (artifactTemplate.getId().equalsIgnoreCase(artifactName)) {
                    requestedArtifactTemplates.add(artifactTemplate);
                    Definitions.Builder requestedDefinitions = getEmptyDefinition(definitions);
                    requestedDefinitions.addArtifactTemplates(requestedArtifactTemplates);
                    return requestedDefinitions.build();
                }
            }
            // we did not find the artifact template id (this should not happen!)
            LOGGER.error("requested artifact template id (" + id.toReadableString() + ") cannot be extracted from definitions object!");
            return definitions;
        } else {
            Definitions.Builder requestedDefinitions = getEmptyDefinition(definitions);

            if (id instanceof NodeTypeId) {
                requestedDefinitions.addNodeTypes(definitions.getNodeTypes());
            } else if (id instanceof RelationshipTypeId) {
                requestedDefinitions.addRelationshipTypes(definitions.getRelationshipTypes());
            } else if (id instanceof NodeTypeImplementationId) {
                requestedDefinitions.addNodeTypeImplementations(definitions.getNodeTypeImplementations());
            } else if (id instanceof RelationshipTypeImplementationId) {
                requestedDefinitions.addRelationshipTypeImplementations(definitions.getRelationshipTypeImplementations());
            } else if (id instanceof ArtifactTypeId) {
                requestedDefinitions.addArtifactTypes(definitions.getArtifactTypes());
            } else if (id instanceof CapabilityTypeId) {
                requestedDefinitions.addCapabilityTypes(definitions.getCapabilityTypes());
            } else if (id instanceof RequirementTypeId) {
                requestedDefinitions.addRequirementTypes(definitions.getRequirementTypes());
            } else if (id instanceof PolicyTypeId) {
                requestedDefinitions.addPolicyTypes(definitions.getPolicyTypes());
            } else if (id instanceof InterfaceTypeId) {
                requestedDefinitions.addInterfaceTypes(definitions.getInterfaceTypes());
            } else {
                // we do not need to filter anything
                return definitions;
            }

            return requestedDefinitions.build();
        }
    }

    /**
     * Creates empty definition to add requested definition later
     *
     * @param definitions converted definitions
     * @return empty definition builder
     **/
    private Definitions.Builder getEmptyDefinition(Definitions definitions) {
        return (new Definitions.Builder(definitions.getId(), definitions.getTargetNamespace()
        ));
    }

    /**
     * Checks if artifact templates exists in type
     *
     * @param targetPath target path of requested type
     * @param qName      target QName
     * @return boolean if it was found
     **/
    private boolean artifactTemplateExistsInType(Path targetPath, QName qName) {
        try {
            Definitions xmlDefinitions = convertToDefinitions(targetPath, getNameOfTypeFromArtifactName(qName.getLocalPart()), qName.getNamespaceURI());
            List<TArtifactTemplate> artifacts = xmlDefinitions.getArtifactTemplates();
            if (artifacts != null) {
                for (TArtifactTemplate artifact : artifacts) {
                    if (artifact.getId().equalsIgnoreCase(getNameOfArtifactFromArtifactName(qName.getLocalPart()))) {
                        return true;
                    }
                }
            }
        } catch (IOException | MultiException e) {
            LOGGER.debug("Internal error", e);
        }
        return false;
    }

    /**
     * Reads service template from target path and converts it to XML Definition
     *
     * @param targetPath      target path of service template
     * @param id              id of requested Definition
     * @param targetNamespace targetNamespace of requested Definition
     * @return xml definitions
     **/
    private Definitions convertToDefinitions(Path targetPath, String id, String targetNamespace) throws IOException, MultiException {
        TServiceTemplate serviceTemplate = readServiceTemplate(targetPath);
        Y2XConverter converter = new Y2XConverter();
        return converter.convert(serviceTemplate, id, targetNamespace);
    }

    /**
     * Reads service template from target path
     *
     * @param targetPath target path of service template
     * @return yaml service template
     **/
    private TServiceTemplate readServiceTemplate(Path targetPath) throws IOException, MultiException {
        InputStream in = newInputStream(targetPath);
        return new YamlReader().parse(in);
    }

    /**
     * Reads service template from referenced definition
     *
     * @param ref repository file reference
     * @return yaml service template
     **/
    private TServiceTemplate readServiceTemplate(RepositoryFileReference ref) throws IOException, MultiException {
        Path targetPath = ref2AbsolutePath(ref);
        InputStream in = newInputStream(targetPath);
        return new YamlReader().parse(in);
    }

    /**
     * Converts incoming xml definitions input stream to xml definitions
     *
     * @param inputStream xml input stream
     * @return xml definitions
     **/
    private Definitions readInputStream(InputStream inputStream) throws JAXBException {
        return (Definitions) JAXBSupport.createUnmarshaller().unmarshal(inputStream);
    }

    /**
     * Gets all artifact names from targeted type
     *
     * @param target          target path of service template
     * @param idClass         id Class of requested Definition
     * @param targetNamespace targetNamespace of requested Definition
     * @return list of strings
     **/
    private <T extends DefinitionsChildId> List<String> getAllArtifactNamesFromType(Path target, Class<T> idClass, String targetNamespace) {
        List<String> output = new ArrayList<>();
        try {
            String fileName = BackendUtils.getFileNameOfDefinitions(idClass);
            String id = target.getFileName().toString();
            target = target.resolve(fileName);
            Definitions definitions = convertToDefinitions(target, id, targetNamespace);
            List<TArtifactTemplate> artifactTemplates = definitions.getArtifactTemplates();
            if (artifactTemplates != null) {
                for (TArtifactTemplate artifactTemplate : artifactTemplates) {
                    output.add(artifactTemplate.getId() + "@" + id);
                }
            }
        } catch (MultiException | IOException e) {
            LOGGER.debug("Internal error", e);
        }
        return output;
    }

    /**
     * Converts incoming xml input stream to yaml service template and writes it to file
     *
     * @param ref         repository file reference
     * @param inputStream input stream to write to file
     * @param mediaType   Media Type
     **/
    @Override
    public void putContentToFile(RepositoryFileReference ref, InputStream inputStream, MediaType mediaType) throws IOException {
        Path targetPath = this.ref2AbsolutePath(ref);
        inputStream = convertToServiceTemplate(ref, inputStream, mediaType);
        writeInputStreamToPath(targetPath, inputStream);
        if (ref.getParent() instanceof NodeTypeImplementationId || ref.getParent() instanceof RelationshipTypeImplementationId) {
            clearCache();
        }
    }

    /**
     * Reads xml definition input stream converts it to yaml service template and writes it to input stream
     *
     * @param ref         Repository File Reference
     * @param inputStream Input Stream
     * @return yaml service template input stream
     **/
    private InputStream convertToServiceTemplate(RepositoryFileReference ref, InputStream inputStream, MediaType mediaType) {
        //ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (mediaType.equals(MediaTypes.MEDIATYPE_TOSCA_DEFINITIONS)) {
            try {
                //IOUtils.copy(inputStream, outputStream);
                //Definitions definitions = readInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
                Definitions definitions = (Definitions) JAXBSupport.createUnmarshaller().unmarshal(inputStream);
                X2YConverter converter = new X2YConverter(this);
                TServiceTemplate serviceTemplate;
                if (ref.getParent() instanceof NodeTypeImplementationId) {
                    serviceTemplate = readServiceTemplate(ref);
                    serviceTemplate = converter.convertNodeTypeImplementation(serviceTemplate, definitions.getNodeTypeImplementations().get(0));
                } else if (ref.getParent() instanceof RelationshipTypeImplementationId) {
                    serviceTemplate = readServiceTemplate(ref);
                    serviceTemplate = converter.convertRelationshipTypeImplementation(serviceTemplate, definitions.getRelationshipTypeImplementations().get(0));
                } else if (ref.getParent() instanceof NodeTypeId) {
                    serviceTemplate = converter.convert(definitions);
                    if (exists(ref)) {
                        TServiceTemplate oldServiceTemplate = readServiceTemplate(ref);
                        serviceTemplate = replaceOldWithNewData(serviceTemplate, oldServiceTemplate);
                    }
                } else if (ref.getParent() instanceof RelationshipTypeId) {
                    serviceTemplate = converter.convert(definitions);
                    if (exists(ref)) {
                        TServiceTemplate oldServiceTemplate = readServiceTemplate(ref);
                        serviceTemplate = replaceOldRelationshipTypeWithNewData(serviceTemplate, oldServiceTemplate);
                    }
                } else if (ref.getParent() instanceof ArtifactTemplateId) {
                    ArtifactTemplateId id = (ArtifactTemplateId) ref.getParent();
                    TArtifactTemplate artifactTemplate = definitions.getArtifactTemplates().get(0);
                    TArtifactDefinition artifact = converter.convertArtifactTemplate(artifactTemplate);
                    List<TMapImportDefinition> imports = converter.convertImports();
                    Path targetPath = ref2AbsolutePath(ref);
                    if (Files.exists(targetPath)) {
                        serviceTemplate = readServiceTemplate(targetPath);
                        if (serviceTemplate == null) {
                            serviceTemplate = createNewCacheNodeTypeWithArtifact(ref, artifactTemplate, artifact, imports);
                        } else if (getTypeFromArtifactName(id.getQName().getLocalPart()).equalsIgnoreCase("nodetypes")) {
                            TNodeType nodeType = serviceTemplate.getNodeTypes().entrySet().iterator().next().getValue();
                            Map<String, TArtifactDefinition> artifacts = nodeType.getArtifacts();
                            if (artifacts.containsKey(artifactTemplate.getIdFromIdOrNameField())) {
                                artifacts.replace(artifactTemplate.getIdFromIdOrNameField(), artifact);
                            } else {
                                artifacts.put(artifactTemplate.getIdFromIdOrNameField(), artifact);
                            }
                            nodeType.setArtifacts(artifacts);
                            serviceTemplate.getNodeTypes().entrySet().iterator().next().setValue(nodeType);
                            serviceTemplate.setImports(addImports(serviceTemplate.getImports(), imports));
                        } else {
                            TRelationshipType relationshipType = serviceTemplate.getRelationshipTypes().entrySet().iterator().next().getValue();
                            Map<String, TInterfaceDefinition> interfaceDefinitionMap = relationshipType.getInterfaces();
                            relationshipType.setInterfaces(addArtifactToInterfaces(interfaceDefinitionMap, artifact, artifactTemplate.getIdFromIdOrNameField()));
                        }
                    } else {
                        serviceTemplate = createNewCacheNodeTypeWithArtifact(ref, artifactTemplate, artifact, imports);
                    }
                } else {
                    serviceTemplate = converter.convert(definitions);
                    if (exists(ref)) {
                        TServiceTemplate existingServiceTemplate = readServiceTemplate(ref);
                        serviceTemplate = replaceTopologyTemplate(serviceTemplate, existingServiceTemplate);
                    }
                }
                YamlWriter writer = new YamlWriter();
                return writer.writeToInputStream(serviceTemplate);
            } catch (Exception e) {
                LOGGER.error("Error converting service template. Reason: {}", e.getMessage(), e);
            }
            return null;
        } else {
            return inputStream;
        }
    }

    private TServiceTemplate replaceTopologyTemplate(TServiceTemplate newServiceTemplate, TServiceTemplate existingServiceTemplate) {
        if (newServiceTemplate.getTopologyTemplate() != null) {
            if (existingServiceTemplate.getTopologyTemplate() == null) {
                existingServiceTemplate.setTopologyTemplate(new TTopologyTemplateDefinition());
            }
            TTopologyTemplateDefinition newTopologyTemplate = newServiceTemplate.getTopologyTemplate();
            TTopologyTemplateDefinition existingTopologyTemplate = existingServiceTemplate.getTopologyTemplate();
            existingTopologyTemplate.setPolicies(newTopologyTemplate.getPolicies());
            existingTopologyTemplate.setNodeTemplates(newTopologyTemplate.getNodeTemplates());
            existingTopologyTemplate.setRelationshipTemplates(newTopologyTemplate.getRelationshipTemplates());
            if (newTopologyTemplate.getInputs() != null) {
                existingTopologyTemplate.setInputs(newTopologyTemplate.getInputs());
            }
            if (newTopologyTemplate.getOutputs() != null) {
                existingTopologyTemplate.setOutputs(newTopologyTemplate.getOutputs());
            }
            existingTopologyTemplate.setDescription(newTopologyTemplate.getDescription());
            existingTopologyTemplate.setGroups(newTopologyTemplate.getGroups());
        }
        return existingServiceTemplate;
    }

    /**
     * Adds artifact to interface for implementation artifact
     *
     * @param interfaces interfaces of type
     * @param id         name of artifact
     * @param artifact   artifact
     * @return edited interfaces
     **/
    private Map<String, TInterfaceDefinition> addArtifactToInterfaces(Map<String, TInterfaceDefinition> interfaces, TArtifactDefinition artifact, String id) {
        if (artifact.getFile() == null) {
            return interfaces;
        }
        for (Map.Entry<String, TInterfaceDefinition> interfaceDefinitionEntry : interfaces.entrySet()) {
            interfaceDefinitionEntry.setValue(addArtifactFileToTargetOperation(interfaceDefinitionEntry.getValue(), artifact, id));
        }
        return interfaces;
    }

    /**
     * Adds artifacts filepath to interfaces for relationship type artifact templates
     *
     * @param interfaces interfaces of type
     * @param target     name of artifact
     * @param artifact   artifact
     * @return edited interfaces
     **/
    private TInterfaceDefinition addArtifactFileToTargetOperation(TInterfaceDefinition interfaces, TArtifactDefinition artifact, String target) {
        Map<String, TOperationDefinition> operations = interfaces.getOperations();
        for (Map.Entry<String, TOperationDefinition> operation : operations.entrySet()) {
            if (operation.getKey().equalsIgnoreCase(target)) {
                TImplementation implementation = operation.getValue().getImplementation();
                implementation.setPrimaryArtifactName(artifact.getFile());
                TOperationDefinition operationDefinition = operation.getValue();
                operationDefinition.setImplementation(implementation);
                operation.setValue(operationDefinition);
            } else {
                TOperationDefinition operationDefinition = operation.getValue();
                if (operationDefinition.getImplementation() != null) {
                    if (operationDefinition.getImplementation().getPrimaryArtifactName() != null) {
                        if (operationDefinition.getImplementation().getPrimaryArtifactName().equalsIgnoreCase(target)) {
                            TImplementation implementation = operationDefinition.getImplementation();
                            implementation.setPrimaryArtifactName(artifact.getFile());
                            operationDefinition.setImplementation(implementation);
                        }
                    }
                }
                operation.setValue(operationDefinition);
            }
        }
        interfaces.setOperations(operations);
        return interfaces;
    }

    /**
     * Adds new import to existing imports
     *
     * @param oldImports existing imports
     * @param newImport  new import
     * @return edited imports
     **/
    private List<TMapImportDefinition> addImports(List<TMapImportDefinition> oldImports, List<TMapImportDefinition> newImport) {
        if (newImport.isEmpty()) {
            return oldImports;
        }
        if (newImport.get(0).isEmpty()) {
            return oldImports;
        }
        Map.Entry<String, TImportDefinition> targetImport = newImport.get(0).entrySet().iterator().next();
        for (TMapImportDefinition tMapImportDefinition : oldImports) {
            for (Map.Entry<String, TImportDefinition> tImportDefinitionEntry : tMapImportDefinition.entrySet()) {
                if (tImportDefinitionEntry.getKey().equalsIgnoreCase(targetImport.getKey())) {
                    if (tImportDefinitionEntry.getValue().equals(targetImport.getValue())) {
                        return oldImports;
                    }
                }
            }
        }
        oldImports.get(0).put(targetImport.getKey(), targetImport.getValue());
        return oldImports;
    }

    /**
     * Creates new cache node type and saves artifact until it's referenced
     *
     * @param ref              repository file reference
     * @param artifactTemplate new artifact template
     * @param artifact         yaml artifact
     * @param imports          imports
     * @return new yaml service template
     **/
    private TServiceTemplate createNewCacheNodeTypeWithArtifact(RepositoryFileReference ref, TArtifactTemplate artifactTemplate, TArtifactDefinition artifact, List<TMapImportDefinition> imports) {
        TServiceTemplate serviceTemplate = createEmptyCacheNodeType(((ArtifactTemplateId) ref.getParent()).getQName().getNamespaceURI());
        Map<String, TArtifactDefinition> artifacts = new LinkedHashMap<>();
        artifacts.put(artifactTemplate.getIdFromIdOrNameField(), artifact);
        serviceTemplate.getNodeTypes().entrySet().iterator().next().getValue().setArtifacts(artifacts);
        serviceTemplate.setImports(imports);
        return serviceTemplate;
    }

    /**
     * Creates new cache node type to save artifact until it's referenced
     *
     * @param targetNamespace target Namespace of cache node type
     * @return new yaml service template
     **/
    private TServiceTemplate createEmptyCacheNodeType(String targetNamespace) {
        return new TServiceTemplate.Builder(Defaults.TOSCA_DEFINITIONS_VERSION)
            .setNodeType("Cache", (new TNodeType.Builder().addMetadata("targetNamespace", targetNamespace).build()))
            .build();
    }

    /**
     * Clears cache Checks if Cache node types can get deleted
     **/
    private void clearCache() {
        SortedSet<ArtifactTemplateId> artifacts = getAllDefinitionsChildIds(ArtifactTemplateId.class);
        for (ArtifactTemplateId artifact : artifacts) {
            if (getNameOfTypeFromArtifactName(artifact.getQName().getLocalPart()).equalsIgnoreCase("cache")) {
                for (ArtifactTemplateId otherArtifact : artifacts) {
                    if (otherArtifact.getQName().getNamespaceURI().equalsIgnoreCase(artifact.getQName().getNamespaceURI())
                        && getNameOfArtifactFromArtifactName(otherArtifact.getQName().getLocalPart()).equalsIgnoreCase(getNameOfArtifactFromArtifactName(artifact.getQName().getLocalPart()))
                        && !getNameOfTypeFromArtifactName(otherArtifact.getQName().getLocalPart()).equalsIgnoreCase("cache")) {
                        forceDelete(artifact);
                    }
                }
            }
        }
    }

    /**
     * Replaces old data of yaml node type with new data from xml node type to prevent deletion of implementation
     * artifacts
     *
     * @param newData new saved node type
     * @param oldData already saved node type
     * @return edited yaml service template
     **/
    private TServiceTemplate replaceOldWithNewData(TServiceTemplate newData, TServiceTemplate oldData) {
        TNodeType oldNodeType = oldData.getNodeTypes().entrySet().iterator().next().getValue();
        TNodeType newNodeType = newData.getNodeTypes().entrySet().iterator().next().getValue();
        oldNodeType.setMetadata(newNodeType.getMetadata());
        oldNodeType.setProperties(newNodeType.getProperties());
        oldNodeType.setDerivedFrom(newNodeType.getDerivedFrom());
        oldNodeType.setDescription(newNodeType.getDescription());
        oldNodeType.setRequirements(newNodeType.getRequirements());
        oldNodeType.setCapabilities(newNodeType.getCapabilities());
        oldNodeType.setArtifacts(newNodeType.getArtifacts());
        oldNodeType.setInterfaces(newNodeType.getInterfaces());
        oldNodeType.setAttributes(newNodeType.getAttributes());
        oldData.getNodeTypes().entrySet().iterator().next().setValue(oldNodeType);
        return oldData;
    }

    /**
     * Replaces old data of yaml relationship type with new data from xml relationship type to prevent deletion of
     * implementation artifacts
     *
     * @param newData new saved relationship type
     * @param oldData already saved relationship type
     * @return edited yaml service template
     **/
    private TServiceTemplate replaceOldRelationshipTypeWithNewData(TServiceTemplate newData, TServiceTemplate oldData) {
        TRelationshipType oldRelationshipType = oldData.getRelationshipTypes().entrySet().iterator().next().getValue();
        TRelationshipType newRelationshipType = newData.getRelationshipTypes().entrySet().iterator().next().getValue();
        oldRelationshipType.setMetadata(newRelationshipType.getMetadata());
        oldRelationshipType.setProperties(newRelationshipType.getProperties());
        oldRelationshipType.setDerivedFrom(newRelationshipType.getDerivedFrom());
        oldRelationshipType.setDescription(newRelationshipType.getDescription());
        oldRelationshipType.setInterfaces(newRelationshipType.getInterfaces());
        oldData.getRelationshipTypes().entrySet().iterator().next().setValue(oldRelationshipType);
        return oldData;
    }

    /**
     * Creates Set of Definitions Child Id Mapps xml definition to compatible yaml definition
     *
     * @param inputIdClass            requested id class
     * @param omitDevelopmentVersions omit development versions
     * @return set of definitions child id
     **/
    @Override
    public <T extends DefinitionsChildId> SortedSet<T> getDefinitionsChildIds(Class<T> inputIdClass, boolean omitDevelopmentVersions) {
        SortedSet<T> res = new TreeSet<>();
        List<Class<T>> idClasses = new ArrayList<>();
        idClasses.add(inputIdClass);
        idClasses = convertDefinitionsChildIdIfNeeded(idClasses);
        for (Class<T> idClass : idClasses) {
            String rootPathFragment = Util.getRootPathFragment(idClass);
            Path dir = this.getRepositoryRoot().resolve(rootPathFragment);
            if (!Files.exists(dir)) {
                // return empty list if no ids are available
                return res;
            }
            assert (Files.isDirectory(dir));

            final OnlyNonHiddenDirectories onhdf = new OnlyNonHiddenDirectories();

            // list all directories contained in this directory
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir, onhdf)) {
                for (Path nsP : ds) {
                    // the current path is the namespace
                    Namespace ns = new Namespace(nsP.getFileName().toString(), true);
                    try (DirectoryStream<Path> idDS = Files.newDirectoryStream(nsP, onhdf)) {
                        for (Path idP : idDS) {

                            List<XmlId> xmlIds = new ArrayList<>();
                            if (ArtifactTemplateId.class.isAssignableFrom(inputIdClass)) {
                                List<String> artifactNames = getAllArtifactNamesFromType(idP, idClass, ns.getDecoded());
                                for (String artifactName : artifactNames) {
                                    xmlIds.add(new XmlId(artifactName + "@" + Util.getFolderName(idClass), true));
                                }
                            } else {
                                xmlIds.add(new XmlId(idP.getFileName().toString(), true));
                            }

                            for (XmlId xmlId : xmlIds) {
                                if (omitDevelopmentVersions) {
                                    WineryVersion version = VersionUtils.getVersion(xmlId.getDecoded());

                                    if (version.toString().length() > 0 && version.getWorkInProgressVersion() > 0) {
                                        continue;
                                    }
                                }
                                Constructor<T> constructor;
                                try {
                                    constructor = inputIdClass.getConstructor(Namespace.class, XmlId.class);
                                } catch (Exception e) {
                                    LOGGER.debug("Internal error at determining id constructor", e);
                                    // abort everything, return invalid result
                                    return res;
                                }
                                T id;
                                try {
                                    id = constructor.newInstance(ns, xmlId);
                                } catch (InstantiationException
                                    | IllegalAccessException
                                    | IllegalArgumentException
                                    | InvocationTargetException e) {
                                    LOGGER.debug("Internal error at invocation of id constructor", e);
                                    // abort everything, return invalid result
                                    return res;
                                }
                                res.add(id);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                LOGGER.debug("Cannot close ds", e);
            }
        }
        return res;
    }

    @Override
    public void getReferencedRequirementTypeIds(Collection<DefinitionsChildId> ids, TNodeTemplate n) {
        // Do nothing. In Yaml mode, there are no requirement types!
    }
}
