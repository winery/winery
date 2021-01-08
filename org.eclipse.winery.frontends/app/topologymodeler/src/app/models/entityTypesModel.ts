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

import { Entity, EntityType, TArtifactType, TDataType, TPolicyType, TTopologyTemplate, VisualEntityType } from './ttopology-template';
import { TopologyModelerConfiguration } from './topologyModelerConfiguration';
import { VersionElement } from './versionElement';
import { Visuals } from './visuals';
import { TPolicy } from './policiesModalData';
import { GroupedNodeTypeModel } from './groupedNodeTypeModel';

/**
 * Internal representation of entity Types
 */
// TODO reconsider bundling the visuals with the entity types
export class EntityTypesModel {
    artifactTemplates: any;
    artifactTypes: TArtifactType[];
    capabilityTypes: EntityType[];
    groupedNodeTypes: GroupedNodeTypeModel[];
    versionElements: VersionElement[];
    nodeVisuals: Visuals[];
    relationshipVisuals: Visuals[];
    policyTemplates: Entity[];
    policyTemplateVisuals: Visuals[];
    policyTypes: TPolicyType[];
    policyTypeVisuals: Visuals[];
    relationshipTypes: VisualEntityType[];
    requirementTypes: EntityType[];
    unGroupedNodeTypes: EntityType[];
    yamlPolicies: TPolicy[];
    dataTypes: TDataType[];
}

/**
 * How data passed to the TopologyModeler has to look
 */
export interface TopologyModelerInputDataFormat {
    configuration: TopologyModelerConfiguration;
    topologyTemplate: TTopologyTemplate;
    visuals: Visuals[];
}
