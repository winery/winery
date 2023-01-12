/*******************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.edmm.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.winery.edmm.EdmmManager;
import org.eclipse.winery.edmm.EdmmUtils;
import org.eclipse.winery.edmm.utils.ZipUtility;
import org.eclipse.winery.model.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeImplementationId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import io.github.edmm.core.parser.Entity;
import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.model.DeploymentModel;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdmmImporter {

    private final static Logger logger = LoggerFactory.getLogger(EdmmImporter.class);

    private final static String EDMM_IMPORTED_NAMESPACE = "https://opentosca.org/import/edmm/";

    private final Map<QName, TNodeType> nodeTypes;
    private final Map<QName, TNodeTypeImplementation> nodeTypeImplementations;
    private final Map<String, Map.Entry<QName, TNodeType>> normalizedNodeTypes = new HashMap<>();

    private final Map<QName, TRelationshipType> relationshipTypes;

    private final Map<String, Map.Entry<QName, TRelationshipType>> normalizedRelationshipTypes = new HashMap<>();
    private final Map<QName, TRelationshipTypeImplementation> relationshipTypeImplementations;

    private final Map<QName, TArtifactTemplate> artifactTemplates;
    private final Map<String, Map.Entry<QName, TArtifactTemplate>> normalizedArtifactTemplates = new HashMap<>();

    private final Map<EdmmType, QName> edmmToToscaMap;

    private final IRepository repository;

    public EdmmImporter() {
        logger.debug("Initializing EDMM Importer...");

        this.repository = RepositoryFactory.getRepository();

        this.nodeTypes = repository.getQNameToElementMapping(NodeTypeId.class);
        this.relationshipTypes = repository.getQNameToElementMapping(RelationshipTypeId.class);
        this.nodeTypeImplementations = repository.getQNameToElementMapping(NodeTypeImplementationId.class);
        this.relationshipTypeImplementations = repository.getQNameToElementMapping(RelationshipTypeImplementationId.class);
        this.artifactTemplates = repository.getQNameToElementMapping(ArtifactTemplateId.class);

        normalizeToscaTypes();

        EdmmManager edmmManager = EdmmManager.forRepository(repository);
        this.edmmToToscaMap = edmmManager.getEdmmOneToToscaMap();

        logger.info("Initialized EDMM Importer!");
    }

    public void importFromStream(InputStream uploadedInputStream, boolean overwrite) {
        Path tempFile = null;
        try {
            tempFile = File.createTempFile("edmm-import", "winery").toPath();
            Files.copy(uploadedInputStream, tempFile);

            transform(tempFile, overwrite);
        } catch (IOException e) {
            logger.error("Cloud not save uploaded file!");
            throw new RuntimeException(e);
        } finally {
            if (tempFile != null) {
                try {
                    FileUtils.forceDelete(tempFile.toFile());
                } catch (IOException e) {
                    logger.warn("Could not delete temp file!");
                }
            }
        }
    }

    public boolean transform(Path edmmFilePath, boolean override) {
        String pathString = edmmFilePath.toString();
        logger.info("Received path \"{}\" to import.", pathString);

        Path workingDirectory = edmmFilePath;
        Path edmmEntryFilePath = null;

        if (pathString.endsWith(".zip")) {
            try {
                workingDirectory = ZipUtility.unpack(
                    edmmFilePath,
                    Files.createTempDirectory(edmmFilePath.getFileName().toString()).toAbsolutePath()
                );
            } catch (IOException e) {
                logger.error("Could not create temporary directory!", e);
                return false;
            }
        } else if (pathString.endsWith(".yml") || pathString.endsWith(".yaml")) {
            workingDirectory = edmmFilePath.getParent();
            edmmEntryFilePath = edmmFilePath;
        }

        if (edmmEntryFilePath == null) {
            Path rootDir = workingDirectory;
            File[] files = workingDirectory.toFile()
                .listFiles((dir, name) ->
                    dir.equals(rootDir.toFile()) && (name.endsWith(".yml") || name.endsWith(".yaml"))
                );

            if (files == null || files.length == 0) {
                logger.error("Could not find EDMM file!");
                return false;
            }

            edmmEntryFilePath = files[0].toPath();
        }

        try {
            // If the EDMM Model does not contain components, the DeploymentModel.of throws an IllegalStateException
            DeploymentModel deploymentModel = DeploymentModel.of(edmmEntryFilePath.toFile());
            logger.info("Successfully imported EDMM deployment model\"{}\"", deploymentModel.getName());
            return importEdmmModel(deploymentModel, override);
        } catch (Exception e) {
            logger.error("Error while loading EDMM model!", e);
            return false;
        }
    }

    public boolean transform(String edmmYaml, boolean override) {
        return importEdmmModel(DeploymentModel.of(edmmYaml), override);
    }

    private boolean importEdmmModel(DeploymentModel deploymentModel, boolean override) {
        logger.info("Starting to import \"{}\"", deploymentModel.getName());

        EntityGraph deploymentModelGraph = deploymentModel.getGraph();
        Optional<Entity> componentTypesOptional = deploymentModelGraph.getEntity(EntityGraph.COMPONENT_TYPES);
        if (componentTypesOptional.isPresent()) {
            Set<Entity> componentTypes = componentTypesOptional.get().getChildren();
            logger.debug("Found {} Component Types to import", componentTypes.size());

            componentTypes.forEach(this::importComponentTypes);
        }

        Optional<Entity> relationTypesOptional = deploymentModelGraph.getEntity(EntityGraph.RELATION_TYPES);
        if (relationTypesOptional.isPresent()) {
            Set<Entity> relationTypes = relationTypesOptional.get().getChildren();
            logger.debug("Found {} Relation Types to import", relationTypes.size());

            relationTypes.forEach(this::importRelationTypes);
        }

        Optional<Entity> componentsOptional = deploymentModelGraph.getEntity(EntityGraph.COMPONENT_TYPES);
        if (componentsOptional.isPresent()) {
            Set<Entity> components = componentsOptional.get().getChildren();
            logger.debug("Found {} Components to import", components.size());

            importEdmmApplication(deploymentModel, components, override);
        }

        return true;
    }

    private void importRelationTypes(Entity entity) {
        String typeName = entity.getName();

        QName qName = this.edmmToToscaMap.get(new EdmmType(typeName));
        TRelationshipType relationshipType = this.relationshipTypes.get(qName);
        if (qName == null) {
            Map.Entry<QName, TRelationshipType> equivalentRelationshipType = normalizedRelationshipTypes.get(typeName);
            if (equivalentRelationshipType == null) {

                logger.info("Creating new Relationship Type \"{}\"", typeName);

                // todo

            } else {
                qName = equivalentRelationshipType.getKey();
                relationshipType = equivalentRelationshipType.getValue();
            }
        } else {
            logger.info("Found existing Relationship Type matching requested Type! Reusing it...");
            logger.info("Type was: \"{}\"", qName);
        }
    }

    private void importEdmmApplication(DeploymentModel deploymentModel, Set<Entity> components, boolean override) {
        String deploymentModelName = deploymentModel.getName() != null
            ? deploymentModel.getName()
            : "Imported-EDMM_" + System.currentTimeMillis();

        ServiceTemplateId serviceTemplateId = new ServiceTemplateId(
            new QName(EDMM_IMPORTED_NAMESPACE + "serviceTemplates", deploymentModelName)
        );
        if (repository.exists(serviceTemplateId) && !override) {
            logger.info("Service Template with id \"{}\" already exists and should not be overridden!", serviceTemplateId.getQName());
            return;
        }

        logger.info("Importing EDMM-Model as Service Template with id \"{}\"", serviceTemplateId.getQName());

        // TODO

    }

    private void importComponentTypes(Entity entity) {
        String typeName = entity.getName();

        Map.Entry<QName, TNodeType> equivalentNodeType = normalizedNodeTypes.get(typeName);
        if (equivalentNodeType == null) {
            logger.info("Creating new Node Type \"{}\"", typeName);

            // todo

        } else {
            logger.info("Found existing Node Type matching requested Type! Reusing it...");
            logger.info("Type was: \"{}\"", typeName);
        }
    }

    /**
     * Loads the required Type Definitions and creates a "normalized" definitions map.
     */
    private void normalizeToscaTypes() {
        nodeTypes.entrySet()
            .forEach(entry -> normalizedNodeTypes.put(
                EdmmUtils.normalizeQName(entry.getKey()),
                entry
            ));

        relationshipTypes.entrySet()
            .forEach(entry -> normalizedRelationshipTypes.put(
                EdmmUtils.normalizeQName(entry.getKey()),
                entry
            ));

        artifactTemplates.entrySet()
            .forEach(entry -> normalizedArtifactTemplates.put(
                EdmmUtils.normalizeQName(entry.getKey()),
                entry
            ));
    }
}
