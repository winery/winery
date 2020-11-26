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
import * as assert from 'assert';

enum ConstraintOperator {
    equal = 'equal',
    greater_than = 'greater_than',
    greater_or_equal = 'greater_or_equal',
    less_than = 'less_than',
    less_or_equal = 'less_or_equal',
    in_range = 'in_range',
    valid_values = 'valid_values',
    length = 'length',
    min_length = 'min_length',
    max_length = 'max_length',
    pattern = 'pattern',
    schema = 'schema'
}

export class ConstraintClause {
    operator: string;
    value: any; // string | number | [number, number] | any[] | Object;
}

export class ConstraintChecking {
    // FIXME comparison needs to be aware of the builtin scalar-unit types and their units
    /**
     * Checks a value against a given constraint. The first argument is the ConstraintClause to check.
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
                if (Array.isArray(checked)) {
                    // assume we have a range that needs to be checked
                    assert(checked.length === 2, 'Checked array value for in_range operator was not a range');
                    return checked[0] >= constraint.value[0] && (checked[1] <= constraint.value[1] || constraint.value[1] === 'UNBOUNDED');
                }
                return checked >= constraint.value[0]
                    && (checked <= constraint.value[1] || constraint.value[1] === 'UNBOUNDED');
            case ConstraintOperator.valid_values:
                return constraint.value.some(v => checked === v);
            case ConstraintOperator.length:
                return checked.length === constraint.value;
            case ConstraintOperator.min_length:
                return checked.length >= constraint.value;
            case ConstraintOperator.max_length:
                return checked.length <= constraint.value;
            case ConstraintOperator.pattern:
                // we assume that the constraint is valid and therefore the value is a string
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
