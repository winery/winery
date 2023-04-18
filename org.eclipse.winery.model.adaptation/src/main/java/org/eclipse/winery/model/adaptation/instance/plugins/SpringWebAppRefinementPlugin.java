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

package org.eclipse.winery.model.adaptation.instance.plugins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.adaptation.instance.InstanceModelRefinementPlugin;
import org.eclipse.winery.model.adaptation.instance.InstanceModelUtils;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.OpenToscaBaseTypes;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import static org.eclipse.winery.model.adaptation.instance.plugins.PetClinicRefinementPlugin.petClinic;

public class SpringWebAppRefinementPlugin extends InstanceModelRefinementPlugin {

    public static final QName springWebApp = QName.valueOf("{http://opentosca.org/nodetypes}SpringWebApp_w1");

    private final Map<QName, TNodeType> nodeTypes;

    public SpringWebAppRefinementPlugin(Map<QName, TNodeType> nodeTypes) {
        super("SpringWebApplication");
        this.nodeTypes = nodeTypes;
    }

    @Override
    public Set<String> apply(TTopologyTemplate topology) {
        Set<String> discoveredNodeIds = new HashSet<>();

        List<String> outputs = InstanceModelUtils.executeCommands(topology, this.matchToBeRefined.nodeIdsToBeReplaced, this.nodeTypes,
            "find /opt/tomcat/latest/webapps -name *.war -not -path \"*docs/*\" | sed -r 's/.*\\/(.+)\\.war/\\1/'"
        );

        String contextPath = outputs.get(0);

        Optional<TNodeTemplate> webAppExsits = topology.getNodeTemplates().stream()
            .filter(node -> this.matchToBeRefined.nodeIdsToBeReplaced.contains(node.getId())
                && (springWebApp.equals(node.getType()) || petClinic.equals(node.getType())))
            .findFirst();

        if (contextPath != null && !contextPath.isBlank() && !contextPath.toLowerCase().contains("no such file or directory")) {
            webAppExsits.ifPresent(app -> {
                discoveredNodeIds.add(app.getId());
                if (app.getProperties() == null) {
                    app.setProperties(new TEntityTemplate.WineryKVProperties());
                }
                if (app.getProperties() instanceof TEntityTemplate.WineryKVProperties) {
                    TEntityTemplate.WineryKVProperties properties = (TEntityTemplate.WineryKVProperties) app.getProperties();
                    properties.getKVProperties().put("context", contextPath.trim());
                }
            });
        }

        if (webAppExsits.isPresent()) {
            TNodeTemplate webApp = webAppExsits.get();

            ArrayList<TNodeTemplate> hostedOnSuccessors = ModelUtilities.getHostedOnSuccessors(topology, webApp);
            if (hostedOnSuccessors.stream()
                .noneMatch(node -> node.getType().getLocalPart().toLowerCase().startsWith("java_"))) {
                hostedOnSuccessors.stream()
                    .filter(node ->
                        ModelUtilities.isOfType(OpenToscaBaseTypes.dockerContainerNodeType, node.getType(), this.nodeTypes) ||
                            ModelUtilities.isOfType(ToscaBaseTypes.compute, node.getType(), this.nodeTypes)
                    ).findFirst()
                    .flatMap(tNodeTemplate ->
                        ModelUtilities.getHostedOnPredecessors(topology, tNodeTemplate)
                            .stream()
                            .filter(node -> node.getType().getLocalPart().toLowerCase().startsWith("java_"))
                            .findFirst()
                    ).ifPresent(javaNode -> {
                        TRelationshipTemplate relationshipTemplate = ModelUtilities.instantiateRelationshipTemplate(
                            RepositoryFactory.getRepository().getElement(new RelationshipTypeId(ToscaBaseTypes.dependsOnRelationshipType)),
                            webApp,
                            javaNode
                        );
                        topology.addRelationshipTemplate(relationshipTemplate);
                        discoveredNodeIds.add(webApp.getId());
                    });
            }
        }

        return discoveredNodeIds;
    }

    @Override
    protected List<TTopologyTemplate> getDetectorGraphs() {
        IRepository repository = RepositoryFactory.getRepository();

        return Arrays.asList(
            createSpringWebAppDetector(repository),
            createPetClinicDetector(repository)
        );
    }

    private TTopologyTemplate createSpringWebAppDetector(IRepository repository) {
        TNodeType springWebAppType = repository.getElement(new NodeTypeId(springWebApp));
        TNodeTemplate springApp = ModelUtilities.instantiateNodeTemplate(springWebAppType);

        return new TTopologyTemplate.Builder()
            .addNodeTemplate(springApp)
            .build();
    }

    private TTopologyTemplate createPetClinicDetector(IRepository repository) {
        TNodeType petClinicType = repository.getElement(new NodeTypeId(petClinic));
        TNodeTemplate petClinic = ModelUtilities.instantiateNodeTemplate(petClinicType);

        return new TTopologyTemplate.Builder()
            .addNodeTemplate(petClinic)
            .build();
    }
}
