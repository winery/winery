/*******************************************************************************
 * Copyright (c) 2019-2020 Contributors to the Eclipse Foundation
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

export class YamlRequirementDefinitionApiData {
    name: string = null;
    capability: string = null;
    node: string = null;
    relationship: string = null;
    lowerBound: string = null;
    upperBound: string = null;
    documentation: any[] = [];
    any: any[] = [];
    otherAttributes: any = null;
}

export class YamlRequirementDefinitionPostApiData {
    name: string = null;
    capability: string = null;
    node: string = null;
    relationship: string = null;
    lowerBound: string = null;
    upperBound: string = null;

    static fromData(data: YamlRequirementDefinitionApiData): YamlRequirementDefinitionPostApiData {
        const result = new YamlRequirementDefinitionPostApiData();
        result.capability = data.capability;
        result.lowerBound = data.lowerBound;
        result.upperBound = data.upperBound;
        result.node = data.node;
        result.relationship = data.relationship;
        result.name = data.name;

        return result;
    }
}
