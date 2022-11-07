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
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.edmm.EdmmManager;
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

import io.github.edmm.model.DeploymentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdmmImporter {

    private final static Logger logger = LoggerFactory.getLogger(EdmmImporter.class);

    private final Map<QName, TNodeType> nodeTypes;
    private final Map<QName, TRelationshipType> relationshipTypes;
    private final Map<QName, TNodeTypeImplementation> nodeTypeImplementations;
    private final Map<QName, TRelationshipTypeImplementation> relationshipTypeImplementations;
    private final Map<QName, TArtifactTemplate> artifactTemplates;
    private final Map<QName, EdmmType> edmmTypeMappings;
    private final Map<QName, EdmmType> oneToOneMappings;

    private final IRepository repository;

    public EdmmImporter() {
        logger.debug("Initializing EDMM Importer...");

        this.repository = RepositoryFactory.getRepository();
        this.nodeTypes = repository.getQNameToElementMapping(NodeTypeId.class);
        this.relationshipTypes = repository.getQNameToElementMapping(RelationshipTypeId.class);
        this.nodeTypeImplementations = repository.getQNameToElementMapping(NodeTypeImplementationId.class);
        this.relationshipTypeImplementations = repository.getQNameToElementMapping(RelationshipTypeImplementationId.class);
        this.artifactTemplates = repository.getQNameToElementMapping(ArtifactTemplateId.class);

        EdmmManager edmmManager = EdmmManager.forRepository(repository);
        this.oneToOneMappings = edmmManager.getOneToOneMap();
        this.edmmTypeMappings = edmmManager.getTypeMap();

        logger.info("Initialized EDMM Importer!");
    }

    public void importFromStream(InputStream uploadedInputStream) {
        try {
            Path tempFile = File.createTempFile("edmm-import", "winery").toPath();
            Files.copy(uploadedInputStream, tempFile);

            transform(tempFile);
        } catch (IOException e) {
            logger.error("Cloud not save uploaded file!");
            throw new RuntimeException(e);
        }
    }

    public boolean transform(Path edmmFilePath) {
        logger.info("Received path \"{}\" to import.", edmmFilePath);

        Path workingDirectory = edmmFilePath;
        Path edmmEntryFilePath = null;

        if (edmmFilePath.endsWith(".zip")) {
            try {
                workingDirectory = ZipUtility.unpack(
                    edmmFilePath,
                    Files.createTempDirectory(edmmFilePath.getFileName().toString()).toAbsolutePath()
                );
            } catch (IOException e) {
                logger.error("Could not create temporary directory!", e);
                return false;
            }
        } else if (edmmFilePath.endsWith(".yml") || edmmFilePath.endsWith(".yaml")) {
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

        DeploymentModel deploymentModel = DeploymentModel.of(edmmEntryFilePath.toFile());
        logger.info("Successfully imported EDMM deployment model\"{}\"", deploymentModel.getName());

        return importEddmModel(deploymentModel);
    }

    public boolean transform(String edmmYaml) {
        return importEddmModel(DeploymentModel.of(edmmYaml));
    }

    private boolean importEddmModel(DeploymentModel deploymentModel) {
        logger.info("Starting to import \"{}\"", deploymentModel.getName());

        // TODO

        return false;
    }
}
