/*******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.securechannelinjection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.admin.SolutionsId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.FilebasedRepository;
import org.eclipse.winery.repository.configuration.FileBasedRepositoryConfiguration;

import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.LoggerFactory;

public class SecureChannelInjector {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SecureChannelInjector.class);
    private static final String SEPARATOR = "---";

    private Map<String, TEntityTemplate> context = new HashMap<>();

    /**
     * Wrapper function for Files#readAllLines(Path) that catches the IOException and instead throws a RuntimeException
     */
    private static List<String> readAllLines(Path path) {
        try {
            return Files.readAllLines(path);
        } catch (IOException ioe) {
            LOGGER.error("Couldn't read file", ioe);
            throw new SecureChannelException("Could not read solution");
        }
    }

    /**
     * Attempts to create a secure communication channel between two nodes which are directly connected to each other.
     * To do so, the topology has to meet solution-specific requirements. If these requirements are met, the topology
     * will be modified and saved to another service template.
     *
     * @param id           The id of the service template that has to be secured
     * @param firstNodeId  The Id of the first node of the insecure communication channel
     * @param secondNodeId The Id of the second node of the insecure communication channel
     * @return The id of the new service template
     * @throws IOException            If the new service template cannot be saved
     * @throws SecureChannelException If there is no direct relationship between node1 and node2
     */
    public ServiceTemplateId createSecureChannel(ServiceTemplateId id, @NonNull String firstNodeId, @NonNull String secondNodeId) throws IOException, SecureChannelException {

        IRepository repository = RepositoryFactory.getRepository();
        TServiceTemplate serviceTemplate = repository.getElement(id);
        TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();

        TNodeTemplate node1 = topologyTemplate.getNodeTemplates().stream()
            .filter(node -> node.getId().equals(firstNodeId))
            .findAny().orElse(null);
        TNodeTemplate node2 = topologyTemplate.getNodeTemplates().stream()
            .filter(node -> node.getId().equals(secondNodeId))
            .findAny().orElse(null);

        Objects.requireNonNull(node1, "Could not find Component_1 in the Topology Template");
        Objects.requireNonNull(node2, "Could not find Component_2 in the Topology Template");

        topologyTemplate.getRelationshipTemplates().forEach(relation -> LOGGER.debug(relation.getSourceElement().getRef().getName() + " -> " + relation.getTargetElement().getRef().getName()));
        TRelationshipTemplate relationship = topologyTemplate.getRelationshipTemplates().stream()
            .filter(rel -> rel.getSourceElement().getRef().getId().equals(node1.getId())
                || rel.getSourceElement().getRef().getId().equals(node2.getId()))
            .filter(rel -> rel.getTargetElement().getRef().getId().equals(node1.getId())
                || rel.getTargetElement().getRef().getId().equals(node2.getId()))
            .findAny().orElseThrow(() -> new SecureChannelException("Couldn't find relationship between nodes"));

        TNodeTemplate source;
        TNodeTemplate target;
        if (relationship.getSourceElement().getRef().getId().equals(node1.getId())) {
            source = node1;
            target = node2;
        } else {
            source = node2;
            target = node1;
        }
        LOGGER.debug("Found relationship: " + source.getName() + " -> " + target.getName());
        context.put("sourceNode", source);
        context.put("targetNode", target);

        FilebasedRepository fileRepository = (FilebasedRepository) RepositoryFactory.getRepository(new FileBasedRepositoryConfiguration());
        Path solutionsPath = fileRepository.ref2AbsolutePath(new RepositoryFileReference(new SolutionsId(), ""));
        List<List<String>> solutions = Files.walk(solutionsPath).filter(Files::isRegularFile).map(SecureChannelInjector::readAllLines).collect(Collectors.toList());

        for (List<String> solution : solutions) {
            int separator = solution.indexOf(SEPARATOR);
            List<String> preconditions = solution.subList(0, separator);
            List<String> actions = solution.subList(separator + 1, solution.size());
            PreconditionChecker preconditionChecker = new PreconditionChecker(serviceTemplate, context);
            if (preconditionChecker.checkPreconditions(preconditions)) {
                LOGGER.debug("Fulfilled preconditions");
                TopologyModificator topologyModificator = new TopologyModificator(serviceTemplate, context);
                return topologyModificator.modifyTopology(actions);
            }
        }
        throw new SecureChannelException("Could not find fitting solution for the topology.");
    }
}
