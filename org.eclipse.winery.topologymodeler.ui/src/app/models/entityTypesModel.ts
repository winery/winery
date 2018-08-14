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

import { TTopologyTemplate, Visuals } from './ttopology-template';
import { TopologyModelerConfiguration } from './topologyModelerConfiguration';

/**
 * Internal representation of entity Types
 */
export class EntityTypesModel {
    artifactTemplates: any;
    artifactTypes: any;
    capabilityTypes: any;
    groupedNodeTypes: any;
    nodeVisuals: Visuals[];
    policyTemplates: any;
    policyTypes: any;
    relationshipTypes: any;
    requirementTypes: any;
    unGroupedNodeTypes: any;
    deploymentArtifacts: any;
}

/**
 * How data passed to the TopologyModeler has to look
 */
export interface TopologyModelerInputDataFormat {
    configuration: TopologyModelerConfiguration;
    topologyTemplate: TTopologyTemplate;
    visuals: Visuals[];
}
