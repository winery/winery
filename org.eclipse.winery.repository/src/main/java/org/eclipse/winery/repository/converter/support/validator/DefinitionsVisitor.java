/********************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.converter.support.validator;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.winery.model.tosca.yaml.TNodeTemplate;
import org.eclipse.winery.model.tosca.yaml.TRepositoryDefinition;
import org.eclipse.winery.repository.converter.support.validator.support.Parameter;
import org.eclipse.winery.repository.converter.support.validator.support.Result;

public class DefinitionsVisitor extends ImportVisitor {
    private Map<String, List<String>> nodeTemplates;
    private Map<String, List<String>> repositoryDefinitions;

    public DefinitionsVisitor(String namespace, Path path) {
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
