/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */

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
    Admin = 'admin'
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
