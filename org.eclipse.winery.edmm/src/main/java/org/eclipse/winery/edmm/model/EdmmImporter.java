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

    private final Map<String, Map.Entry<QName, TNodeType>> normalizedNodeTypes = new HashMap<>();
    private final Map<String, Map.Entry<QName, TRelationshipType>> normalizedRelationshipTypes = new HashMap<>();
    private final Map<String, Map.Entry<QName, TNodeTypeImplementation>> normalizedNodeTypeImplementations = new HashMap<>();
    private final Map<String, Map.Entry<QName, TRelationshipTypeImplementation>> normalizedRelationshipTypeImplementations = new HashMap<>();
    private final Map<String, Map.Entry<QName, TArtifactTemplate>> normalizedArtifactTemplates = new HashMap<>();

    private final Map<QName, EdmmType> edmmTypeMappings;
    private final Map<EdmmType, QName> oneToOneMappings;

    private final IRepository repository;

    public EdmmImporter() {
        logger.debug("Initializing EDMM Importer...");

        this.repository = RepositoryFactory.getRepository();

        loadAndNormalizeToscaTypes();

        EdmmManager edmmManager = EdmmManager.forRepository(repository);
        this.oneToOneMappings = edmmManager.getEdmmOneToOneMap();
        this.edmmTypeMappings = edmmManager.getToscaTypeMap();

        logger.info("Initialized EDMM Importer!");
    }

    public void importFromStream(InputStream uploadedInputStream) {
        Path tempFile = null;
        try {
            tempFile = File.createTempFile("edmm-import", "winery").toPath();
            Files.copy(uploadedInputStream, tempFile);

            transform(tempFile);
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

    public boolean transform(Path edmmFilePath) {
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
            return importEddmModel(deploymentModel);
        } catch (Exception e) {
            logger.error("Error while loading EDMM model!", e);
            return false;
        }
    }

    public boolean transform(String edmmYaml) {
        return importEddmModel(DeploymentModel.of(edmmYaml));
    }

    private boolean importEddmModel(DeploymentModel deploymentModel) {
        logger.info("Starting to import \"{}\"", deploymentModel.getName());

        EntityGraph deploymentModelGraph = deploymentModel.getGraph();
        Optional<Entity> componentTypes = deploymentModelGraph.getEntity(EntityGraph.COMPONENT_TYPES);
        if (componentTypes.isPresent()) {
            Set<Entity> types = componentTypes.get().getChildren();
            logger.debug("Found {} Component Types to import", types.size());

            types.forEach(this::importComponentTypes);
        }

        return true;
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

    private void loadAndNormalizeToscaTypes() {
        Map<QName, TNodeType> nodeTypes = repository.getQNameToElementMapping(NodeTypeId.class);
        nodeTypes.entrySet()
            .forEach(entry -> normalizedNodeTypes.put(
                EdmmUtils.normalizeQName(entry.getKey()),
                entry
            ));

        Map<QName, TRelationshipType> relationshipTypes = repository.getQNameToElementMapping(RelationshipTypeId.class);
        relationshipTypes.entrySet()
            .forEach(entry -> normalizedRelationshipTypes.put(
                EdmmUtils.normalizeQName(entry.getKey()),
                entry
            ));

        Map<QName, TNodeTypeImplementation> nodeTypeImplementations = repository.getQNameToElementMapping(NodeTypeImplementationId.class);
        nodeTypeImplementations.entrySet()
            .forEach(entry -> normalizedNodeTypeImplementations.put(
                EdmmUtils.normalizeQName(entry.getKey()),
                entry
            ));

        Map<QName, TRelationshipTypeImplementation> relationshipTypeImplementations = repository.getQNameToElementMapping(RelationshipTypeImplementationId.class);
        relationshipTypeImplementations.entrySet()
            .forEach(entry -> normalizedRelationshipTypeImplementations.put(
                EdmmUtils.normalizeQName(entry.getKey()),
                entry
            ));

        Map<QName, TArtifactTemplate> artifactTemplates = repository.getQNameToElementMapping(ArtifactTemplateId.class);
        artifactTemplates.entrySet()
            .forEach(entry -> normalizedArtifactTemplates.put(
                EdmmUtils.normalizeQName(entry.getKey()),
                entry
            ));
    }
}
