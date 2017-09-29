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
package org.eclipse.winery.yaml.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

public class Defaults {
    public static final String TOSCA_NORMATIVE_TYPES = "/tosca_simple_yaml_1_1.yml";
    public static final List<String> YAML_TYPES = new ArrayList<>(Arrays.asList("string", "integer", "float", "boolean", "timestamp", "null"));
    public static final List<String> TOSCA_TYPES = new ArrayList<>(Arrays.asList("list", "map"));
    public static final String TOSCA_DEFINITIONS_VERSION_PATTERN = "tosca_simple_yaml_\\d_\\d|http://docs\\.oasis-open\\.org/tosca/ns/simple/yaml/\\d\\.\\d";
    public static final List<String> TOSCA_NORMATIVE_NAMES = new ArrayList<>(Arrays.asList(
        "tosca.datatypes.Root",
        "tosca.datatypes.TimeInterval", "TimeInterval",
        "tosca.datatypes.network.NetworkInfo", "NetworkInfo",
        "tosca.datatypes.network.PortInfo", "PortInfo",
        "tosca.datatypes.network.PortDef", "PortDef",
        "tosca.datatypes.network.PortSpec", "PortSpec",
        "tosca.artifacts.Root",
        "tosca.artifacts.File", "File",
        "tosca.artifacts.Deployment",
        "tosca.artifacts.Deployment.Image", "Deployment.Image",
        "tosca.artifacts.Deployment.Image.VM",
        "tosca.artifacts.Implementation",
        "tosca.artifacts.Implementation.Bash", "Bash",
        "tosca.artifacts.Implementation.Python", "Python",
        "tosca.capabilities.Root",
        "tosca.capabilities.Node", "Node",
        "tosca.capabilities.Compute", "Compute",
        "tosca.capabilities.Network", "Network",
        "tosca.capabilities.Storage", "Storage",
        "tosca.capabilities.Container", "Container",
        "tosca.capabilities.Endpoint", "Endpoint",
        "tosca.capabilities.Endpoint.Public", "Endpoint.Public",
        "tosca.capabilities.Endpoint.Admin", "Endpoint.Admin",
        "tosca.capabilities.Endpoint.Database", "Endpoint.Database",
        "tosca.capabilities.Attachment", "Attachment",
        "tosca.capabilities.OperatingSystem", "OperatingSystem",
        "tosca.capabilities.Scalable", "Scalable",
        "tosca.capabilities.network.Bindable", "network.Bindable",
        "tosca.relationships.Root",
        "tosca.relationships.DependsOn", "DependsOn",
        "tosca.relationships.HostedOn", "HostedOn",
        "tosca.relationships.ConnectsTo", "ConnectsTo",
        "tosca.relationships.AttachesTo", "AttachesTo",
        "tosca.relationships.RoutesTo", "RoutesTo",
        "tosca.interfaces.Root",
        "tosca.interfaces.node.lifecycle.Standard", "Standard",
        "tosca.interfaces.relationship.Configure", "Configure",
        "tosca.nodes.Root",
        "tosca.nodes.Compute", "Compute",
        "tosca.nodes.SoftwareComponent", "SoftwareComponent",
        "tosca.nodes.WebServer", "WebServer",
        "tosca.nodes.WebApplication", "WebApplication",
        "tosca.nodes.DBMS",
        "tosca.nodes.Database", "Database",
        "tosca.nodes.Storage.ObjectStorage", "ObjectStorage",
        "tosca.nodes.Storage.BlockStorage", "BlockStorage",
        "tosca.nodes.Container.Runtime", "Container.Runtime",
        "tosca.nodes.Container.Application", "Container.Application",
        "tosca.nodes.LoadBalancer", "LoadBalancer",
        "tosca.groups.Root",
        "tosca.policies.Root",
        "tosca.policies.Placement",
        "tosca.policies.Scaling",
        "tosca.policies.Update",
        "tosca.policies.Performance"
    ));
    public static final QName IMPLEMENTATION_ARTIFACTS = new QName(Namespaces.TOSCA_NS, "tosca.artifacts.Implementation");
    public static final QName DEPLOYMENT_ARTIFACTS = new QName(Namespaces.TOSCA_NS, "tosca.artifacts.Deployment");
}
