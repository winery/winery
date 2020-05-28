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

package org.eclipse.winery.repository.export;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TArtifacts;
import org.eclipse.winery.model.tosca.TImport;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;
import org.eclipse.winery.repository.export.entries.YAMLDefinitionsBasedCsarEntry;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YamlToscaExportUtil extends ToscaExportUtil {

    private static final boolean EXPORT_NORMATIVE_TYPES = false;
    private static final Logger LOGGER = LoggerFactory.getLogger(YamlToscaExportUtil.class);

    @Override
    protected Collection<DefinitionsChildId> processDefinitionsElement(IRepository repository, DefinitionsChildId tcId, CsarContentProperties definitionsFileProperties)
        throws RepositoryCorruptException, IOException {
        if (!repository.exists(tcId)) {
            String error = "Component instance " + tcId.toReadableString() + " does not exist.";
            LOGGER.error(error);
            throw new RepositoryCorruptException(error);
        }

        Definitions entryDefinitions = repository.getDefinitions(tcId);
        this.getPrepareForExport(repository, tcId, entryDefinitions);

        Collection<DefinitionsChildId> referencedDefinitionsChildIds = repository.getReferencedDefinitionsChildIds(tcId);

        if (!EXPORT_NORMATIVE_TYPES) {
            referencedDefinitionsChildIds.removeIf(id -> id.getQName().getNamespaceURI().startsWith("tosca"));
        }

        // adjust imports: add imports of definitions
        Collection<TImport> imports = new ArrayList<>();
        for (DefinitionsChildId id : referencedDefinitionsChildIds) {
            this.addToImports(repository, id, imports);
        }

        entryDefinitions.getImport().addAll(imports);

        // END: Definitions modification

        YAMLDefinitionsBasedCsarEntry entry = new YAMLDefinitionsBasedCsarEntry(entryDefinitions);

        // Custom Adjustments for Service Templates
        YamlExportAdjustmentsBuilder adjustmentsBuilder = new YamlExportAdjustmentsBuilder(entry);
        if (!EXPORT_NORMATIVE_TYPES) {
            adjustmentsBuilder.removeNormativeTypeImports();
        }
        entry = adjustmentsBuilder.setMetadataName(tcId).build();

        this.referencesToPathInCSARMap.put(definitionsFileProperties, entry);

        return referencedDefinitionsChildIds;
    }

    /**
     * Prepares the given id for export. Mostly, the contained files are added to the CSAR.
     */
    private void getPrepareForExport(IRepository repository, DefinitionsChildId id, Definitions entryDefinitions) throws IOException {
        if (id instanceof ServiceTemplateId) {
            this.prepareServiceTemplateForExport(repository, (ServiceTemplateId) id, entryDefinitions);
        } else if (id instanceof RelationshipTypeId) {
            this.addVisualAppearanceToCSAR(repository, (RelationshipTypeId) id);
        } else if (id instanceof NodeTypeId) {
            this.addVisualAppearanceToCSAR(repository, (NodeTypeId) id);
            this.prepareNodeTypeForExport(repository, (NodeTypeId) id, entryDefinitions);
        }
    }

    private void prepareNodeTypeForExport(IRepository repository, NodeTypeId id, Definitions entryDefinitions) {

        TNodeType node = repository.getElement(id);
        TArtifacts artifacts = node.getArtifacts();
        if (Objects.nonNull(artifacts)) {

            artifacts.getArtifact().forEach(artifact -> {
                Path p = Paths.get("files", artifact.getId());
                RepositoryFileReference ref = new RepositoryFileReference(id, p, artifact.getFile());
                if (repository.exists(ref)) {
                    putRefAsReferencedItemInCsar(ref);

                    // update file paths in "artifacts" the exported service template
                    entryDefinitions.getNodeTypes()
                        .stream()
                        .filter(nt -> nt.getQName().equals(node.getQName()))
                        .forEach(nt -> {
                            if (nt.getArtifacts() != null) {
                                nt.getArtifacts().getArtifact().stream()
                                    .filter(art -> art.getFile().equals(artifact.getFile()))
                                    .forEach(art -> {
                                        String pathInsideRepo = BackendUtils.getPathInsideRepo(ref);
                                        art.setFile("/" + FilenameUtils.separatorsToUnix(pathInsideRepo));
                                    });
                            }
                        });

                    // update "primary" field in the exported service template
                    entryDefinitions.getNodeTypes()
                        .stream()
                        .filter(nt -> nt.getQName().equals(node.getQName()))
                        .forEach(nt -> {
                            if (nt.getInterfaceDefinitions() != null) {
                                nt.getInterfaceDefinitions().forEach(interfaceDefinition -> {
                                    if (interfaceDefinition.getOperations() != null) {
                                        interfaceDefinition.getOperations().forEach(op -> {
                                            if (op.getImplementation() != null) {
                                                String artifactName = op.getImplementation().getPrimary();
                                                if (artifactName.equalsIgnoreCase(artifact.getName())) {
                                                    String pathInsideRepo = BackendUtils.getPathInsideRepo(ref);
                                                    op.getImplementation().setPrimary("/" + FilenameUtils.separatorsToUnix(pathInsideRepo));
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });

                    // update "dependencies" field in the exported service template
                    entryDefinitions.getNodeTypes()
                        .stream()
                        .filter(nt -> nt.getQName().equals(node.getQName()))
                        .forEach(nt -> {
                            if (nt.getInterfaceDefinitions() != null) {
                                nt.getInterfaceDefinitions().forEach(interfaceDefinition -> {
                                    if (interfaceDefinition.getOperations() != null) {
                                        interfaceDefinition.getOperations().forEach(op -> {
                                            if (op.getImplementation() != null && op.getImplementation().getDependencies() != null) {
                                                List<String> dependencies = op.getImplementation().getDependencies().stream().map(artifactName -> {
                                                    if (artifactName.equalsIgnoreCase(artifact.getName())) {
                                                        String pathInsideRepo = BackendUtils.getPathInsideRepo(ref);
                                                        return "/" + FilenameUtils.separatorsToUnix(pathInsideRepo);
                                                    }
                                                    return artifactName;
                                                }).collect(Collectors.toList());
                                                op.getImplementation().setDependencies(dependencies);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                }
            });
        }
    }

    /**
     * Prepares artifacts in Service Template
     */
    private void prepareServiceTemplateForExport(IRepository repository, ServiceTemplateId id, Definitions entryDefinitions) throws IOException {
        BackendUtils.synchronizeReferences(id);
        TServiceTemplate st = repository.getElement(id);

        if (Objects.nonNull(st.getTopologyTemplate())) {
            for (TNodeTemplate n : st.getTopologyTemplate().getNodeTemplates()) {
                TArtifacts artifacts = n.getArtifacts();
                if (Objects.nonNull(artifacts)) {

                    // update file paths in the exported service template
                    artifacts.getArtifact().forEach(a -> {
                        Path p = Paths.get("files", n.getId(), a.getId());
                        RepositoryFileReference ref = new RepositoryFileReference(id, p, a.getFile());
                        if (repository.exists(ref)) {
                            putRefAsReferencedItemInCsar(ref);
                            entryDefinitions.getServiceTemplates()
                                .stream().filter(Objects::nonNull)
                                .findFirst()
                                .ifPresent(s -> {
                                    if (Objects.nonNull(s.getTopologyTemplate())) {
                                        s.getTopologyTemplate()
                                            .getNodeTemplates()
                                            .stream()
                                            .filter(node -> node.getId().equals(n.getId()))
                                            .forEach(node -> node.getArtifacts()
                                                .getArtifact()
                                                .stream()
                                                .filter(art -> art.getFile().equals(a.getFile()))
                                                .forEach(art -> {
                                                    String pathInsideRepo = BackendUtils.getPathInsideRepo(ref);
                                                    art.setFile("/" + FilenameUtils.separatorsToUnix(pathInsideRepo));
                                                }));
                                    }
                                });
                        }
                    });

                    // TODO: update "primary" field in the exported service template
                }
            }
        }
    }
}
