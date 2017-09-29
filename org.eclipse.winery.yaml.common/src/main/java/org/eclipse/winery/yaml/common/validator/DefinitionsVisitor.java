/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Christoph Kleine - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.yaml.common.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.winery.model.tosca.yaml.TNodeTemplate;
import org.eclipse.winery.model.tosca.yaml.TRepositoryDefinition;
import org.eclipse.winery.yaml.common.validator.support.Parameter;
import org.eclipse.winery.yaml.common.validator.support.Result;

public class DefinitionsVisitor extends ImportVisitor {
    private Map<String, List<String>> nodeTemplates;
    private Map<String, List<String>> repositoryDefinitions;

    public DefinitionsVisitor(String namespace, String path) {
        super(namespace, path);
        this.nodeTemplates = new LinkedHashMap<>();
        this.repositoryDefinitions = new LinkedHashMap<>();
    }

    @Override
    public Result visit(TNodeTemplate node, Parameter parameter) {
        setDefinitions(parameter.getKey(), nodeTemplates);
        return super.visit(node, parameter);
    }

    @Override
    public Result visit(TRepositoryDefinition node, Parameter parameter) {
        setDefinitions(parameter.getKey(), repositoryDefinitions);
        return super.visit(node, parameter);
    }

    private void setDefinitions(String name, Map<String, List<String>> map) {
        if (map.containsKey(namespace)) {
            map.get(namespace).add(name);
        } else {
            map.put(namespace, new ArrayList<>(Collections.singletonList(name)));
        }
    }

    public Map<String, List<String>> getNodeTemplates() {
        return nodeTemplates;
    }

    public Map<String, List<String>> getRepositoryDefinitions() {
        return repositoryDefinitions;
    }
}
