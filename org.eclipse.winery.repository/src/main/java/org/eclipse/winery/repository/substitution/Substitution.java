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

package org.eclipse.winery.repository.substitution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.tosca.HasInheritance;
import org.eclipse.winery.model.tosca.TBoolean;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

public class Substitution {

    public void createSubstitutableNodeType() {

    }

    public void replaceSubstitutableNodeTemplates(TTopologyTemplate topology) {
        IRepository repo = RepositoryFactory.getRepository();

        /*List<TServiceTemplate> serviceTemplates = repo.getAllDefinitionsChildIds(ServiceTemplateId.class)
            .stream()
            .map(repo::getElement)
            .filter(element -> Objects.nonNull(element.getSubstitutableNodeType()))
            .collect(Collectors.toList());*/
        Map<QName, TNodeType> nodeTypes = new HashMap<>();
        repo.getAllDefinitionsChildIds(NodeTypeId.class)
            .forEach(id -> {
                nodeTypes.put(id.getQName(), repo.getElement(id));
            });

        Map<TNodeTemplate, List<Subtypes<TNodeType>>> substitutableNodeTemplates = new HashMap<>();
        topology.getNodeTemplates()
            .forEach(tNodeTemplate -> {
                QName nodeTemplateType = tNodeTemplate.getType();
                collectTypeHierarchy(nodeTypes, nodeTemplateType)
                    .ifPresent(tNodeTypeSubtypes -> substitutableNodeTemplates.put(tNodeTemplate, tNodeTypeSubtypes));
            });
    }

    /**
     * This method collects all elements of the given class <code>T</code> which are derived from the <code>nodeType</code>.
     *
     * @param <T>    a TOSCA definitions type which has inheritance
     * @param types  all available types of the specified class <code>T</code>
     * @param parent the parent
     * @return an <code>Optional</code> containing a tree of subtypes
     */
    <T extends HasInheritance> Optional<List<Subtypes<T>>> collectTypeHierarchy(Map<QName, T> types, QName parent) {
        T type = types.get(parent);

        if (Objects.nonNull(type) && type.getAbstract().equals(TBoolean.YES)) {
            List<Subtypes<T>> subtypes = new ArrayList<>();
            types.forEach((key, current) -> {
                if (Objects.nonNull(current.getDerivedFrom()) && current.getDerivedFrom().getTypeAsQName().equals(parent)) {
                    Subtypes<T> child = new Subtypes<>(current);
                    subtypes.add(child);
                    this.collectTypeHierarchy(types, key)
                        .ifPresent(child::addChildren);
                }
            });
            return Optional.of(subtypes);
        }

        return Optional.empty();
    }
}
