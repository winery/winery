/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
export class CapabilityOrRequirementDefinition {
    name: string = null;
    capabilityType: string = null;
    requirementType: string = null;
    lowerBound: string = null;
    upperBound: string = null;
    constraints: Constraints = null;
    documentation: any[] = [];
    any: any[] = [];
    otherAttributes: any = null;
}

export class CapOrReqDefinition {
    name: string = null;
    type: string = null;
    lowerBound: string = null;
    upperBound: string = null;
}

export class Constraint {
    any: any = null;
    constraintType: string = null;
    id: string = null;
}

export class Constraints {
    constraint: Constraint[] = [];
}

export class CapabilityDefinitionPostData {
    name: string = null;
    capabilityType: string = null;
    lowerBound: string = null;
    upperBound: string = null;
    constraints = {};
}

export class CapOrRegDefinitionsResourceApiData {
    capOrRegDefinitionsList: CapabilityOrRequirementDefinition[];
}
