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

package org.eclipse.winery.model.adaptation.instance.plugins.dockerimage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.model.adaptation.instance.InstanceModelRefinementPlugin;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.OpenToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.topologygraph.matching.IToscaMatcher;
import org.eclipse.winery.topologygraph.matching.ToscaPropertyMatcher;
import org.eclipse.winery.topologygraph.model.ToscaNode;

public class DockerImageRefinementPlugin extends InstanceModelRefinementPlugin {

    private static final String PROPERTY_IMAGE_ID = "ImageID";

    private final Map<String, ImageRefinementHandler> refinementHandlerByImage = new HashMap<>();
    private final Set<QName> prohibitedTypes;

    private final Map<QName, TNodeType> nodeTypes;

    public DockerImageRefinementPlugin(Map<QName, TNodeType> nodeTypes) {
        super("docker-image");

        this.nodeTypes = nodeTypes;

        List<ImageRefinementHandler> handlers = Arrays.asList(
            // new MongoDbHandler(),
            new WeaveGoHandler(),
            // new WeaveCartsHandler(),
            new WeaveFrontEndHandler());

        handlers
            .forEach(refinementHandler -> refinementHandler.getTargetImages()
                .forEach(targetImage -> refinementHandlerByImage.put(targetImage, refinementHandler)));

        prohibitedTypes = handlers.stream()
            .flatMap(imageRefinementHandler -> imageRefinementHandler.getProhibitedTypes().stream())
            .collect(
                Collectors.toSet());
    }

    @Override
    public Set<String> apply(TTopologyTemplate template) {
        List<TNodeTemplate> nodesToRefineByImage = template.getNodeTemplates().stream()
            .filter(node -> this.matchToBeRefined.nodeIdsToBeReplaced.contains(node.getId())
                && VersionUtils.getNameWithoutVersion(node.getType().getLocalPart()).equals(OpenToscaBaseTypes.dockerContainerNodeType.getLocalPart()))
            .toList();

        Set<String> discoveredNodeIds = new HashSet<>();
        for (TNodeTemplate curNode : nodesToRefineByImage) {
            Optional<String> imageId = Optional.ofNullable(curNode.getProperties())
                .filter(TEntityTemplate.WineryKVProperties.class::isInstance)
                .map(TEntityTemplate.WineryKVProperties.class::cast)
                .map(
                    TEntityTemplate.WineryKVProperties::getKVProperties)
                .map(properties -> properties.get(PROPERTY_IMAGE_ID));

            // TODO - maybe split image id into name and tag for versioning?

            Optional<ImageRefinementHandler> imageRefinementHandler = imageId
                .map(refinementHandlerByImage::get);

            if (imageId.isPresent() && imageRefinementHandler.isPresent()) {
                Set<String> handlerDiscoveredNodeIds = imageRefinementHandler.get()
                    .handleNode(curNode, template, imageId.get());
                discoveredNodeIds.addAll(handlerDiscoveredNodeIds);
            }
        }

        return discoveredNodeIds;
    }

    @Override
    public Set<String> determineAdditionalInputs(
        TTopologyTemplate template,
        ArrayList<String> nodeIdsToBeReplaced) {
        return null;
    }

    @Override
    protected List<TTopologyTemplate> getDetectorGraphs() {
        IRepository repository = RepositoryFactory.getRepository();

        TNodeType computeType = repository.getElement(new NodeTypeId(OpenToscaBaseTypes.dockerContainerNodeType));
        TNodeTemplate compute = ModelUtilities.instantiateNodeTemplate(computeType);

        LinkedHashMap<String, String> computeKvProperties = new LinkedHashMap<>();

        String detectorPropertyRegex = refinementHandlerByImage.keySet()
            .stream()
            .collect(Collectors.joining("|", "(", ")"));

        computeKvProperties.put(PROPERTY_IMAGE_ID, detectorPropertyRegex);

        TEntityTemplate.WineryKVProperties computeProperties = new TEntityTemplate.WineryKVProperties();
        computeProperties.setKVProperties(computeKvProperties);

        compute.setProperties(computeProperties);

        return Collections.singletonList(
            new TTopologyTemplate.Builder()
                .addNodeTemplate(compute)
                .build()
        );
    }

    @Override
    protected IToscaMatcher getToscaMatcher() {
        return new ToscaPropertyMatcher(true, true) {
            @Override
            public boolean isCompatible(
                ToscaNode left,
                ToscaNode right) {
                return !prohibitedTypes.contains(right.getActualType().getQName()) && super.isCompatible(left, right);
            }
        };
    }
}
