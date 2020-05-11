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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.winery.model.tosca.yaml.TArtifactType;
import org.eclipse.winery.model.tosca.yaml.TCapabilityType;
import org.eclipse.winery.model.tosca.yaml.TDataType;
import org.eclipse.winery.model.tosca.yaml.TGroupType;
import org.eclipse.winery.model.tosca.yaml.TInterfaceType;
import org.eclipse.winery.model.tosca.yaml.TNodeType;
import org.eclipse.winery.model.tosca.yaml.TPolicyType;
import org.eclipse.winery.model.tosca.yaml.TRelationshipType;
import org.eclipse.winery.model.tosca.yaml.support.Metadata;
import org.eclipse.winery.repository.converter.support.Namespaces;
import org.eclipse.winery.repository.converter.support.validator.support.Parameter;
import org.eclipse.winery.repository.converter.support.validator.support.Result;

public class TypeVisitor extends ImportVisitor {
    private Map<String, List<String>> artifactTypes;
    private Map<String, List<String>> dataTypes;
    private Map<String, List<String>> capabilityTypes;
    private Map<String, List<String>> interfaceTypes;
    private Map<String, List<String>> relationshipTypes;
    private Map<String, List<String>> nodeTypes;
    private Map<String, List<String>> groupTypes;
    private Map<String, List<String>> policyTypes;

    public TypeVisitor(String namespace, Path path) {
        super(namespace, path);
        this.artifactTypes = new LinkedHashMap<>();
        this.dataTypes = new LinkedHashMap<>();
        this.capabilityTypes = new LinkedHashMap<>();
        this.interfaceTypes = new LinkedHashMap<>();
        this.relationshipTypes = new LinkedHashMap<>();
        this.nodeTypes = new LinkedHashMap<>();
        this.groupTypes = new LinkedHashMap<>();
        this.policyTypes = new LinkedHashMap<>();
    }

    @Override
    public Result visit(TArtifactType node, Parameter parameter) {
        this.setArtifactTypes(namespace, parameter.getKey());
        setNormativeTypes(parameter.getKey(), node.getMetadata(), artifactTypes);
        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TDataType node, Parameter parameter) {
        this.setDataTypes(namespace, parameter.getKey());
        setNormativeTypes(parameter.getKey(), node.getMetadata(), dataTypes);
        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TCapabilityType node, Parameter parameter) {
        this.setCapabilityTypes(namespace, parameter.getKey());
        setNormativeTypes(parameter.getKey(), node.getMetadata(), capabilityTypes);
        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TInterfaceType node, Parameter parameter) {
        this.setInterfaceTypes(namespace, parameter.getKey());
        setNormativeTypes(parameter.getKey(), node.getMetadata(), interfaceTypes);
        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TRelationshipType node, Parameter parameter) {
        this.setRelationshipTypes(namespace, parameter.getKey());
        setNormativeTypes(parameter.getKey(), node.getMetadata(), relationshipTypes);
        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TNodeType node, Parameter parameter) {
        this.setNodeTypes(namespace, parameter.getKey());
        setNormativeTypes(parameter.getKey(), node.getMetadata(), nodeTypes);
        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TGroupType node, Parameter parameter) {
        this.setGroupTypes(namespace, parameter.getKey());
        setNormativeTypes(parameter.getKey(), node.getMetadata(), groupTypes);
        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TPolicyType node, Parameter parameter) {
        this.setPolicyTypes(namespace, parameter.getKey());
        setNormativeTypes(parameter.getKey(), node.getMetadata(), policyTypes);
        super.visit(node, parameter);
        return null;
    }

    public void setNormativeTypes(String name, Metadata metadata, Map<String, List<String>> map) {
        if (namespace.equals(Namespaces.TOSCA_NS) && metadata != null) {
            String shorthandName = metadata.get("shorthand_name");
            String typeUri = metadata.get("type_uri");

            if (shorthandName != null && !shorthandName.equals(name)) {
                if (map.containsKey(namespace)) {
                    map.get(namespace).add(shorthandName);
                } else {
                    map.put(namespace, new ArrayList<>(Arrays.asList(shorthandName)));
                }
            } else if (typeUri != null && !typeUri.equals(name)) {
                if (map.containsKey(namespace)) {
                    map.get(namespace).add(typeUri);
                } else {
                    map.put(namespace, new ArrayList<>(Arrays.asList(typeUri)));
                }
            }
        }
    }

    private void setArtifactTypes(String namespace, String name) {
        if (artifactTypes.containsKey(namespace)) {
            artifactTypes.get(namespace).add(name);
        } else {
            artifactTypes.put(namespace, new ArrayList<>(Arrays.asList(name)));
        }
    }

    private void setDataTypes(String namespace, String name) {
        if (dataTypes.containsKey(namespace)) {
            dataTypes.get(namespace).add(name);
        } else {
            dataTypes.put(namespace, new ArrayList<>(Collections.singletonList(name)));
        }
    }

    private void setCapabilityTypes(String namespace, String name) {
        if (capabilityTypes.containsKey(namespace)) {
            capabilityTypes.get(namespace).add(name);
        } else {
            capabilityTypes.put(namespace, new ArrayList<>(Arrays.asList(name)));
        }
    }

    private void setInterfaceTypes(String namespace, String name) {
        if (interfaceTypes.containsKey(namespace)) {
            interfaceTypes.get(namespace).add(name);
        } else {
            interfaceTypes.put(namespace, new ArrayList<>(Arrays.asList(name)));
        }
    }

    private void setRelationshipTypes(String namespace, String name) {
        if (relationshipTypes.containsKey(namespace)) {
            relationshipTypes.get(namespace).add(name);
        } else {
            relationshipTypes.put(namespace, new ArrayList<>(Arrays.asList(name)));
        }
    }

    private void setNodeTypes(String namespace, String name) {
        if (nodeTypes.containsKey(namespace)) {
            nodeTypes.get(namespace).add(name);
        } else {
            nodeTypes.put(namespace, new ArrayList<>(Arrays.asList(name)));
        }
    }

    private void setGroupTypes(String namespace, String name) {
        if (groupTypes.containsKey(namespace)) {
            groupTypes.get(namespace).add(name);
        } else {
            groupTypes.put(namespace, new ArrayList<>(Arrays.asList(name)));
        }
    }

    private void setPolicyTypes(String namespace, String name) {
        if (policyTypes.containsKey(namespace)) {
            policyTypes.get(namespace).add(name);
        } else {
            policyTypes.put(namespace, new ArrayList<>(Arrays.asList(name)));
        }
    }

    public void addDataTypes(List<String> types, String namespace) {
        for (String entry : types) {
            setDataTypes(namespace, entry);
        }
    }

    public Map<String, List<String>> getArtifactTypes() {
        return artifactTypes;
    }

    public Map<String, List<String>> getDataTypes() {
        return dataTypes;
    }

    public Map<String, List<String>> getCapabilityTypes() {
        return capabilityTypes;
    }

    public Map<String, List<String>> getInterfaceTypes() {
        return interfaceTypes;
    }

    public Map<String, List<String>> getRelationshipTypes() {
        return relationshipTypes;
    }

    public Map<String, List<String>> getNodeTypes() {
        return nodeTypes;
    }

    public Map<String, List<String>> getGroupTypes() {
        return groupTypes;
    }

    public Map<String, List<String>> getPolicyTypes() {
        return policyTypes;
    }
}
