/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.tools.deployablecomponents;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TCapabilityDefinition;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRequirementDefinition;
import org.eclipse.winery.model.tosca.TTag;
import org.eclipse.winery.model.tosca.TTags;
import org.eclipse.winery.tools.deployablecomponents.commons.Component;

import org.apache.commons.lang3.tuple.Pair;

class DeployableComponentsToscaConverter {

    List<TNodeType> convertToToscaModel(Map<Component, List<Pair<Component, Integer>>> components) {
        List<TNodeType> nodes = new ArrayList<>();
        for (Map.Entry<Component, List<Pair<Component, Integer>>> entry : components.entrySet()) {
            TNodeType.Builder nodeBuilder = convertComponentToNode(entry.getKey());

            for (Pair<Component, Integer> topEntry : entry.getValue()) {
                // add capabilities for the base component
                nodeBuilder.addCapabilityDefinitions(convertComponentToCapability(topEntry.getKey()));

                // add top components to nodes with base component as requirement
                TNodeType.Builder nodeBuilder2 = convertComponentToNode(topEntry.getKey());
                nodeBuilder2.addRequirementDefinitions(convertComponentToRequirement(entry.getKey()));
                nodes.add(nodeBuilder2.build());
            }
            nodes.add(nodeBuilder.build());
        }
        return nodes;
    }

    private TNodeType.Builder convertComponentToNode(Component component) {
        TNodeType.Builder baseNodeBuilder = new TNodeType.Builder(component.getName());
        TTag versionTag = new TTag(new TTag.Builder().setName("version").setValue(component.getVersion()));
        TTag versionOperatorTag = new TTag(new TTag.Builder().setName("versionOperator").setValue(component.getVersionOperator()));
        baseNodeBuilder.setTags(new TTags(new TTags.Builder().addTag(versionTag).addTag(versionOperatorTag)));
        return baseNodeBuilder;
    }

    private TCapabilityDefinition convertComponentToCapability(Component component) {
        TCapabilityDefinition.Builder builder = new TCapabilityDefinition.Builder(component.getName(), new QName(component.getName() + component.getVersionOperator() + component.getVersion()));
        builder.setUpperBound(component.getVersionOperator() + "_" + component.getVersion());
        return new TCapabilityDefinition(builder);
    }

    private TRequirementDefinition convertComponentToRequirement(Component component) {
        TRequirementDefinition.Builder builder = new TRequirementDefinition.Builder(component.getName(), new QName(component.getName() + component.getVersionOperator() + component.getVersion()));
        builder.setUpperBound(component.getVersionOperator() + "_" + component.getVersion());
        return new TRequirementDefinition(builder);
    }
}
