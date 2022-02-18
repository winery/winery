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
 ********************************************************************************/

export enum align {
    Horizontal = 'Horizontal',
    Vertical = 'Vertical',
}

export enum definitionType {
    CapabilityDefinitions = '/capabilitydefinitions/',
    RequirementDefinitions = '/requirementdefinitions/',
}

export enum urlElement {
    PolicyTemplateURL = '/policytemplates/',
    ArtifactTemplateURL = '/artifacttemplates/',
    NodeTypeURL = '/nodetypes/',
    RequirementTypeURL = '/requirementtypes/',
    CapabilityTypeURL = '/capabilitytypes/',
    RelationshipTypeURL = '/relationshiptypes/',
    ReadMe = '/readme',
    ServiceTemplates = '/servicetemplates/',
    TopologyTemplate = '/topologytemplate/',
    NodeTemplates = 'nodetemplates/',
    YamlArtifacts = '/yamlartifacts'
}

export enum TableType {
    Requirements = 'Requirements',
    Capabilities = 'Capabilities',
    Policies = 'Policies',
    DeploymentArtifacts = 'DeploymentArtifacts',
    YamlArtifacts = 'YamlArtifacts'
}

export enum toggleModalType {
    Requirements = 'REQUIREMENTS',
    Capabilities = 'CAPABILITIES',
    DeploymentArtifacts = 'DEPLOYMENT_ARTIFACTS',
    Policies = 'POLICIES',
}

export enum PropertyDefinitionType {
    NONE = 'NONE',
    KV = 'KV',
    XML = 'XML',
    YAML = 'YAML',
}

export enum PropertyKVType {
    XSD_STRING = 'xsd:string',
    XSD_FLOAT = 'xsd:float',
    XSD_DECIMAL = 'xsd:decimal',
    XSD_ANYURI = 'xsd:anyURI',
    XSD_QNAME = 'xsd:QName',
}

export enum LiveModelingStates {
    DISABLED = 'DISABLED',
    INIT = 'INIT',
    DEPLOY = 'DEPLOY',
    TERMINATE = 'TERMINATE',
    TERMINATED = 'TERMINATED',
    ENABLED = 'ENABLED',
    RECONFIGURATE = 'RECONFIGURATE',
    UPDATE = 'UPDATE',
    ERROR = 'ERROR'
}

export enum PlanTypes {
    BuildPlan = 'http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/BuildPlan',
    TerminationPlan = 'http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/TerminationPlan',
    ManagementPlan = 'http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/ManagementPlan',
    TransformationPlan = 'http://opentosca.org/plantypes/TransformationPlan'
}

export enum ServiceTemplateInstanceStates {
    INITIAL = 'INITIAL',
    CREATING = 'CREATING',
    CREATED = 'CREATED',
    DELETING = 'DELETING',
    DELETED = 'DELETED',
    ERROR = 'ERROR',
    MIGRATING = 'MIGRATING',
    MIGRATED = 'MIGRATED',
    NOT_AVAILABLE = 'NOT_AVAILABLE',
}

export enum NodeTemplateInstanceStates {
    INITIAL = 'INITIAL',
    CREATING = 'CREATING',
    CREATED = 'CREATED',
    CONFIGURING = 'CONFIGURING',
    CONFIGURED = 'CONFIGURED',
    STARTING = 'STARTING',
    STARTED = 'STARTED',
    STOPPING = 'STOPPING',
    STOPPED = 'STOPPED',
    DELETING = 'DELETING',
    DELETED = 'DELETED',
    ERROR = 'ERROR',
    MIGRATED = 'MIGRATED',
    NOT_AVAILABLE = 'NOT_AVAILABLE',
}

export enum LiveModelingLogTypes {
    SUCCESS = 'success',
    INFO = 'info',
    WARNING = 'warning',
    DANGER = 'danger',
    CONTAINER = 'container'
}

export enum LiveModelingButtons {
    START,
    TERMINATE,
    REFRESH,
    SWITCH,
    RECONFIGURATE,
}

export enum ReconfigureOptions {
    NONE,
    REDEPLOY,
    TRANSFORM,
}

export enum AdaptationAction {
    START_NODE,
    STOP_NODE
}
