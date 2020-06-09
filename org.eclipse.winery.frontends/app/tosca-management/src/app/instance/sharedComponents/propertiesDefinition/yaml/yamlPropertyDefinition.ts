/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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
import { QName } from '../../../../model/qName';
import { SchemaDefinition } from '../../../../../../../topologymodeler/src/app/models/ttopology-template';
import { Constraint, YamlWellKnown } from '../../../../model/constraint';

export class YamlPropertyDefinition {
    constructor(
        public name: string = '',
        public type: QName | YamlWellKnown = 'string',
        public description: string = '',
        public required: boolean = false,
        public defaultValue: any = '',
        public status: string = 'supported',
        public constraints: Constraint[] = [],
        public keySchema: SchemaDefinition = undefined,
        public entrySchema: SchemaDefinition = undefined) { }
}

export function isYaml(value: any): value is YamlPropertyDefinition {
    return (value as YamlPropertyDefinition).name !== undefined;
}
