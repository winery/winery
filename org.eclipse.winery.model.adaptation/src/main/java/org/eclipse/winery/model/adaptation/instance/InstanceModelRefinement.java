/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.adaptation.instance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.adaptation.instance.plugins.Ec2AmiRefinementPlugin;
import org.eclipse.winery.model.adaptation.instance.plugins.MySqlDbRefinementPlugin;
import org.eclipse.winery.model.adaptation.instance.plugins.MySqlDbmsRefinementPlugin;
import org.eclipse.winery.model.adaptation.instance.plugins.PetClinicRefinementPlugin;
import org.eclipse.winery.model.adaptation.instance.plugins.SpringWebAppRefinementPlugin;
import org.eclipse.winery.model.adaptation.instance.plugins.TomcatRefinementPlugin;
import org.eclipse.winery.model.adaptation.instance.plugins.dockerimage.DockerImageRefinementPlugin;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.DiscoveryPluginDescriptor;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTag;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.transformation.ToscaTransformer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstanceModelRefinement {

    public static final String TAG_DISCOVERY_PLUGINS = "jsonDiscoveryPlugins";

    private static final Logger logger = LoggerFactory.getLogger(InstanceModelRefinement.class);

    private final InstanceModelPluginChooser pluginChooser;
    private final List<InstanceModelRefinementPlugin> plugins;
    private final Map<QName, TNodeType> nodeTypes;

    public InstanceModelRefinement(InstanceModelPluginChooser chooser) {
        this.pluginChooser = chooser;
        this.nodeTypes = RepositoryFactory.getRepository().getQNameToElementMapping(NodeTypeId.class);
        this.plugins = Arrays.asList(
            new TomcatRefinementPlugin(nodeTypes),
            new MySqlDbRefinementPlugin(nodeTypes),
            new MySqlDbmsRefinementPlugin(nodeTypes),
            new PetClinicRefinementPlugin(nodeTypes),
            new SpringWebAppRefinementPlugin(nodeTypes),
            new Ec2AmiRefinementPlugin(nodeTypes),
            new DockerImageRefinementPlugin(nodeTypes)
        );
    }

    public static void updateDiscoveryPluginsInServiceTemplate(
        TServiceTemplate serviceTemplate, ObjectMapper objectMapper, List<DiscoveryPluginDescriptor> discoveryPlugins) {
        try {
            TTag updatedTag = new TTag.Builder(TAG_DISCOVERY_PLUGINS, objectMapper.writeValueAsString(discoveryPlugins))
                .build();
            if (serviceTemplate.getTags() == null) {
                serviceTemplate.setTags(new ArrayList<>());
            }
            serviceTemplate.getTags().removeIf(tTag -> Objects.equals(tTag.getName(), TAG_DISCOVERY_PLUGINS));
            serviceTemplate.getTags().add(updatedTag);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not write terraform deployment technology to JSON string");
        }
    }

    public static List<DiscoveryPluginDescriptor> extractDiscoveryPluginsFromServiceTemplate(
        TServiceTemplate serviceTemplate, ObjectMapper objectMapper) {
        return Optional.ofNullable(serviceTemplate.getTags())
            .flatMap(tTags -> tTags.stream()
                .filter(tTag -> Objects.equals(tTag.getName(), TAG_DISCOVERY_PLUGINS))
                .findAny())
            .map(TTag::getValue)
            .map(s -> {
                CollectionType collectionType = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, DiscoveryPluginDescriptor.class);
                try {
                    return objectMapper.<List<DiscoveryPluginDescriptor>>readValue(s, collectionType);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Deployment technologies tag could not be parsed as JSON", e);
                }
            })
            .orElseGet(ArrayList::new);
    }

    public TTopologyTemplate refine(ServiceTemplateId serviceTemplateId) {
        IRepository repository = RepositoryFactory.getRepository();
        TServiceTemplate serviceTemplate = repository.getElement(serviceTemplateId);
        TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();

        List<DiscoveryPluginDescriptor> discoveryPluginDescriptors = extractDiscoveryPluginsFromServiceTemplate(serviceTemplate, new ObjectMapper());

        if (topologyTemplate == null) {
            logger.error("Cannot refine empty instance model!");
            return null;
        }

        boolean pluginsAreAvailable = true;
        do {
            ToscaGraph topologyGraph = ToscaTransformer.createTOSCAGraph(topologyTemplate);
            List<InstanceModelRefinementPlugin> executablePlugins = this.plugins.stream()
                .filter(plugin -> plugin.isApplicable(topologyTemplate, topologyGraph, discoveryPluginDescriptors))
                .collect(Collectors.toList());
            InstanceModelRefinementPlugin selectedPlugin = pluginChooser.selectPlugin(topologyTemplate, executablePlugins);

            if (selectedPlugin != null) {
                DiscoveryPluginDescriptor discoveryPlugin = discoveryPluginDescriptors.stream()
                    .filter(discoveryPluginDescriptor -> Objects.equals(discoveryPluginDescriptor.getId(),
                        selectedPlugin.getId()))
                    .findAny()
                    .orElseGet(() -> {
                        DiscoveryPluginDescriptor discoveryPluginDescriptor = new DiscoveryPluginDescriptor();
                        discoveryPluginDescriptor.setId(selectedPlugin.getId());
                        discoveryPluginDescriptor.setDiscoveredIds(Collections.emptyList());
                        discoveryPluginDescriptors.add(discoveryPluginDescriptor);
                        return discoveryPluginDescriptor;
                    });
                Set<String> pluginDiscoveredNodeIds = selectedPlugin.apply(topologyTemplate);
                List<String> discoveredIds = new ArrayList<>();
                discoveredIds.addAll(pluginDiscoveredNodeIds);
                discoveredIds.addAll(discoveryPlugin.getDiscoveredIds());
                discoveryPlugin.setDiscoveredIds(discoveredIds);
                updateDiscoveryPluginsInServiceTemplate(serviceTemplate,
                    new ObjectMapper(),
                    discoveryPluginDescriptors);
                try {
                    repository.setElement(serviceTemplateId, serviceTemplate);
                } catch (IOException e) {
                    logger.error("Error persisting Service Template {}", serviceTemplateId.toReadableString());
                }
            } else {
                pluginsAreAvailable = false;
            }
        } while (pluginsAreAvailable);

        return topologyTemplate;
    }
}
