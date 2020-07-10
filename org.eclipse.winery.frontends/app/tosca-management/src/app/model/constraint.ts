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

export class Constraint {
    constructor(public key: string, public value: string, public list: string[]) {
    }
}

export const yaml_well_known = [
    'string', 'integer', 'float', 'boolean', 'timestamp', 'null',
    'version', 'range', 'list', 'map',
    'scalar-unit', 'scalar-unit.size', 'scalar-unit.time', 'scalar-unit.frequency', 'scalar-unit.bitrate',
];
/**
 * Referenced YAML types as defined in YAML spec 3.3.1, as well as the types defined by the spec itself.
 */
export type YamlWellKnown =
    'string' | 'integer' | 'float' | 'boolean' | 'timestamp' | 'null' |
    'version'| 'range' | 'list' | 'map' |
    'scalar-unit' | 'scalar-unit.size' | 'scalar-unit.time' | 'scalar-unit.frequency' | 'scalar-unit.bitrate';

/**
 * Checks whether a given value is a declaration of a well-known YAML type as defined in {@link YamlWellKnown} and can act as a type guard.
 */
export function isWellKnown(value: any): value is YamlWellKnown {
    return (typeof value === 'string') && yaml_well_known.includes(value);
}
