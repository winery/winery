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

/**
 * Encompasses all node information emitted from a node needed for showing the correct modal
 */
export interface ToggleModalDataModel {
    any: any;
    capabilities: any;
    color: string;
    currentNodeId: string;
    currentNodePart: string;
    currentProperties: any;
    deploymentArtifacts: any;
    documentation: any;
    id: string;
    imageUrl: string;
    maxInstances: string;
    minInstances: string;
    name: string;
    otherAttributes: any;
    policies: any;
    properties: any;
    requirements: any;
    targetLocations: string;
    type: string;
    x: number;
    y: number;
    currentTableRowIndex: number;
    currentRequirement: ReqCapEntityModel;
    currentCapability: ReqCapEntityModel;
}

export interface ReqCapEntityModel {
    any: any;
    documentation: any;
    id: string;
    name: string;
    otherAttributes: any;
    properties: any;
    type: string;
}
