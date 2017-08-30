/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter - initial API and implementation
 */

export enum YesNoEnum {
    YES = 'YES',
    NO = 'NO'
}

// If you add a new TOSCA Type, you also need to adjust the utils.ts file.
// Default is assumed ToscaTypes.Admin.
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
