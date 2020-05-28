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

import { RequirementDefinitionModel } from './requirementDefinitonModel';

/**
 * Encompasses the requirement model
 */
export class RequirementModel {
    public any: any;
    public documentation: any;
    public id?: string;
    public name: string;
    public otherAttributes: any;
    public type?: string;
    public properties?: any;
    public capability?: string;
    public node?: string;
    public relationship?: string;

    static fromRequirementDefinition(def: RequirementDefinitionModel): RequirementModel {
        const result = new RequirementModel();
        result.any = def.any;
        result.documentation = def.documentation;
        result.name = def.name;
        result.otherAttributes = def.otherAttributes;
        result.type = def.requirementType;
        result.capability = def.capability;
        result.node = def.node;
        result.relationship = def.relationship;

        return result;
    }

    constructor() {
    }
}
