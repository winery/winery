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

/**
 * constraint operators defined in tosca yaml spec 3.6.3.1
 */
enum ConstraintOperator {
    equal,
    greater_than,
    greater_or_equal,
    less_than,
    less_or_equal,
    in_range,
    valid_values,
    length,
    min_length,
    max_length,
    pattern,
    schema
}

export class ConstraintClause {
    operator: ConstraintOperator;
    value: string | number | [number, number] | any[] | Object;
}

/**
 * Referenced YAML types as defined in YAML spec 3.3.1, as well as the types defined by the spec
 * 
 */
export const knownTypes = ['string', 'integer', 'float', 'boolean', 'timestamp', 'null',
    'version', 'range', 'list', 'map', 'scalar-unit.size', 'scalar-unit.time', 'scalar-unit.frequency', 'scalar-unit.bitrate'];

export class ConstraintChecking {
    /**
     * Checks a value against a given constraint. The first argument is the ConstraintClause to check 
     * @param constraint
     * @param checked
     */
    static isValid(constraint: ConstraintClause, checked: any): boolean {
        switch (constraint.operator) {
            case ConstraintOperator.equal:
                return checked === constraint.value;
            case ConstraintOperator.greater_than:
                return checked > constraint.value;
            case ConstraintOperator.greater_or_equal:
                return checked >= constraint.value;
            case ConstraintOperator.less_than:
                return checked < constraint.value;
            case ConstraintOperator.less_or_equal:
                return checked <= constraint.value;
            case ConstraintOperator.in_range:
                return checked >= constraint.value[0]
                    && checked <= constraint.value[1];
            case ConstraintOperator.valid_values:
                return constraint.value.some(v => checked === v);
            case ConstraintOperator.length:
                return checked.length === constraint.value;
            case ConstraintOperator.min_length:
                return checked.length >= constraint.value;
            case ConstraintOperator.max_length:
                return checked.length <= constraint.value;
            case ConstraintOperator.pattern:
                return new RegExp(constraint.value).test(checked);
            case ConstraintOperator.schema:
                // schema validation is only performed on the Orchestrator
                return true;
            default:
                // if no operator is specified, infer "equal"
                return checked === constraint.value;
        }
    }
}
