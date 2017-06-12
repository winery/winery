/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Philipp Meyer, Tino Stadelmaier - initial API and implementation
 */
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
