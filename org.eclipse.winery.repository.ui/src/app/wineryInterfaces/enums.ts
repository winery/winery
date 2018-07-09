/********************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
export enum YesNoEnum {
    YES = 'YES',
    NO = 'NO'
}

/**
 * ToscaTypes represent the main types found in TOSCA. All of them are components reachable via
 * a main route. Additionally, <code>Admin</code> has been added because it is also a main route.
 *
 * If you add a new TOSCA Type, you also need to adjust the utils.ts file.
 * Default is assumed ToscaTypes.Admin.
 */
export enum ToscaTypes {
    ServiceTemplate = 'servicetemplates',
    NodeType = 'nodetypes',
    RelationshipType = 'relationshiptypes',
    ArtifactType = 'artifacttypes',
    ArtifactTemplate = 'artifacttemplates',
    RequirementType = 'requirementtypes',
    CapabilityType = 'capabilitytypes',
    NodeTypeImplementation = 'nodetypeimplementations',
    RelationshipTypeImplementation = 'relationshiptypeimplementations',
    PolicyType = 'policytypes',
    PolicyTemplate = 'policytemplates',
    Imports = 'imports',
    Admin = 'admin',
    ComplianceRule = 'compliancerules'
}

/**
 * ServiceTemplateTemplateTypes represent the instance types which are available in a Service Template.
 * The types of these templates can be retrieved by using the {@link Utils.getTypeOfServiceTemplateTemplate} method.
 */
export enum ServiceTemplateTemplateTypes {
    CapabilityTemplate = 'capabilityTemplates',
    NodeTemplate = 'nodeTemplates',
    RelationshipTemplate = 'relationshipTemplates',
    RequirementTemplate = 'requirementTemplates'
}

export enum StartNamespaces {
    LocalStorageEntry = 'defaultNamespace',
    DefaultStartNamespace = 'http://www.example.org/tosca'
}

/**
 * BackendAvailabilityStates defines the states for the availability of the backend.
 */
export enum BackendAvailabilityStates {
    Available = 1,
    Unavailable = 0,
    Undefined = -1
}

export enum WineryVersionTypesEnum {
    ComponentVersion = 'componentVersion',
    WineryVersion = 'wineryVersion',
    WipVersion = 'wipVersion'
}
