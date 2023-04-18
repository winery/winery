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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.adaptation.instance.InstanceModelRefinementPlugin;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.topologygraph.matching.IToscaMatcher;
import org.eclipse.winery.topologygraph.matching.ToscaPropertyMatcher;

public class Ec2AmiRefinementPlugin extends InstanceModelRefinementPlugin {
    private static final QName COMPUTE_QNAME = QName.valueOf(
        "{http://docs.oasis-open.org/tosca/ToscaNormativeTypes/nodetypes}Compute");
    private static final QName UBUNTU_20_04_QNAME = QName.valueOf(
        "{https://examples.opentosca.org/edmm/nodetypes}Ubuntu_20.04.2-w1-wip1");
    private static final QName UBUNTU_18_04_QNAME = QName.valueOf(
        "{http://opentosca.org/nodetypes}Ubuntu-VM_18.04-w1");

    private static final String PROPERTY_EC2_AMI = "ec2-ami";

    private final Map<String, QName> typeByAmi = new HashMap<>();

    private final Map<QName, TNodeType> nodeTypes;

    public Ec2AmiRefinementPlugin(Map<QName, TNodeType> nodeTypes) {
        super("ec2ami");
        typeByAmi.put("ami-0af107682aaa86bd0", UBUNTU_20_04_QNAME);
        typeByAmi.put("ami-00105f70a3660f2ae", UBUNTU_18_04_QNAME);
        this.nodeTypes = nodeTypes;
    }

    @Override
    public Set<String> apply(TTopologyTemplate topology) {
        List<TNodeTemplate> nodesToRefineByAmi = topology.getNodeTemplates().stream()
            .filter(node -> this.matchToBeRefined.nodeIdsToBeReplaced.contains(node.getId()) && Objects.equals(node.getType(),
                COMPUTE_QNAME))
            .collect(Collectors.toList());

        Set<String> discoveredNodeIds = new HashSet<>();
        for (TNodeTemplate curNode : nodesToRefineByAmi) {
            Optional.ofNullable(curNode.getProperties())
                .filter(TEntityTemplate.WineryKVProperties.class::isInstance)
                .map(TEntityTemplate.WineryKVProperties.class::cast)
                .map(TEntityTemplate.WineryKVProperties::getKVProperties)
                .map(properties -> properties.get(PROPERTY_EC2_AMI))
                .map(typeByAmi::get)
                .ifPresent(value -> {
                    curNode.setType(value);
                    discoveredNodeIds.add(curNode.getId());
                });
        }

        return discoveredNodeIds;
    }

    @Override
    public Set<String> determineAdditionalInputs(TTopologyTemplate template, ArrayList<String> nodeIdsToBeReplaced) {
        return null;
    }

    @Override
    protected List<TTopologyTemplate> getDetectorGraphs() {
        IRepository repository = RepositoryFactory.getRepository();

        TNodeType computeType = repository.getElement(new NodeTypeId(COMPUTE_QNAME));
        TNodeTemplate compute = ModelUtilities.instantiateNodeTemplate(computeType);

        LinkedHashMap<String, String> computeKvProperties = new LinkedHashMap<>();
        String detectorPropertyRegex = typeByAmi.keySet().stream().collect(Collectors.joining("|", "(", ")"));
        computeKvProperties.put(PROPERTY_EC2_AMI, detectorPropertyRegex);

        TEntityTemplate.WineryKVProperties computeProperties = new TEntityTemplate.WineryKVProperties();
        computeProperties.setKVProperties(computeKvProperties);

        compute.setProperties(computeProperties);

        return Collections.singletonList(new TTopologyTemplate.Builder().addNodeTemplate(compute).build());
    }

    @Override
    protected IToscaMatcher getToscaMatcher() {
        return new ToscaPropertyMatcher(true, true);
    }
}
