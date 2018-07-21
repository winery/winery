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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.model.tosca.HasInheritance;
import org.eclipse.winery.model.tosca.HasType;
import org.eclipse.winery.model.tosca.TBoolean;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Substitution {

    private static final Logger LOGGER = LoggerFactory.getLogger(Substitution.class);
    private static final String versionAppendix = "substituted";

    public void createSubstitutableNodeType() {

    }

    public void replaceSubstitutableNodeTemplates(ServiceTemplateId serviceTemplateId) {
        IRepository repo = RepositoryFactory.getRepository();
        TServiceTemplate serviceTemplate;

        // 0. Create a new version of the Service Template
        try {
            ServiceTemplateId substitutedServiceTemplateId = new ServiceTemplateId(
                serviceTemplateId.getNamespace().getDecoded(),
                VersionUtils.getNewId(serviceTemplateId, versionAppendix),
                false
            );
            repo.duplicate(serviceTemplateId, substitutedServiceTemplateId);

            // 0.1 load the new version
            serviceTemplate = repo.getElement(substitutedServiceTemplateId);
        } catch (IOException e) {
            LOGGER.debug("Could not create new Service Template version during substitution", e);
            LOGGER.debug("Reusing existing element");
            serviceTemplate = repo.getElement(serviceTemplateId);
        }

        // 0.2 Loading the topology
        TTopologyTemplate topology = Objects.requireNonNull(serviceTemplate.getTopologyTemplate());

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

        Map<QName, TRelationshipType> relationshipTypes = new HashMap<>();
        repo.getAllDefinitionsChildIds(RelationshipTypeId.class)
            .forEach(id -> {
                relationshipTypes.put(id.getQName(), repo.getElement(id));
            });

        // 1. Step: retrieve all Node Templates which must be substituted
        Map<TNodeTemplate, List<Subtypes<TNodeType>>> substitutableNodeTemplates =
            this.collectSubstitutableTemplates(topology.getNodeTemplates(), nodeTypes);

        // 2. Step: retrieve all Relationship Templates which must be substituted
        Map<TRelationshipTemplate, List<Subtypes<TRelationshipType>>> substitutableRelationshipTemplates =
            this.collectSubstitutableTemplates(topology.getRelationshipTemplates(), relationshipTypes);

        // 3. Step: select concrete type to be substituted
        Map<TNodeTemplate, TNodeType> nodeTemplateReplacementMap = new FindFirstSubstitutionStrategy<TNodeTemplate, TNodeType>()
            .getReplacementMap(substitutableNodeTemplates);
        Map<TRelationshipTemplate, TRelationshipType> relationshipTemplateReplacementMap = new FindFirstSubstitutionStrategy<TRelationshipTemplate, TRelationshipType>()
            .getReplacementMap(substitutableRelationshipTemplates);

        // 4. Step: update the type references
        // 4.1 in case of simple NodeTemplate substitution --> everything is inherited --> there is no need to change anything else
        topology.getNodeTemplates()
            .forEach(tNodeTemplate -> {
                TNodeType replacementType = nodeTemplateReplacementMap.get(tNodeTemplate);
                if (Objects.nonNull(replacementType)) {
                    QName qName = new QName(replacementType.getTargetNamespace(), replacementType.getIdFromIdOrNameField());
                    tNodeTemplate.setType(qName);
                }
            });

        // TODO: write test method
        // TODO: check if the type can be substituted with a Service Template and implement this replacement
    }

    /**
     * This method collects all templates of the given <code>templates</code> which must be substituted. Additionally, all children
     * of the template's type are included as a list of trees.
     *
     * @param templates the list of TOSCA templates which have to examied whether they must be substituted
     * @param types     the map of all types of the same kind (e.g. Node Templates and Node Types) identified by their corresponding <code>DefinitionsChildId</code>
     * @param <R>       the class of the templates
     * @param <T>       the class of the types
     * @return a map containing a mapping between substitutable templates and their available sub types which can be used during the substitution
     */
    public <R extends HasType, T extends HasInheritance> Map<R, List<Subtypes<T>>> collectSubstitutableTemplates(List<R> templates, Map<QName, T> types) {
        Map<R, List<Subtypes<T>>> substitutableTypes = new HashMap<>();

        templates.forEach(tNodeTemplate -> {
            QName nodeTemplateType = tNodeTemplate.getTypeAsQName();
            collectTypeHierarchy(types, nodeTemplateType)
                .ifPresent(tNodeTypeSubtypes ->
                    substitutableTypes.put(tNodeTemplate, tNodeTypeSubtypes)
                );
        });

        return substitutableTypes;
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
