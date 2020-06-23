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
import { AbstractControl, ValidationErrors, Validator } from '@angular/forms';
import { TDataType } from '../../models/ttopology-template';
import { QName } from '../../models/qname';
// TODO this should possibly not be from tosca-management
import { YamlPropertyDefinition } from '../../../../../tosca-management/src/app/model/yaml';
import { Constraint, isWellKnown } from '../../../../../tosca-management/src/app/model/constraint';
import { ConstraintChecking } from '../property-constraints';
import { InheritanceUtils } from '../../models/InheritanceUtils';
import { ToscaUtils } from '../../models/toscaUtils';

export class TypeConformanceValidator implements Validator {

    private fullTypeDefinition: string | { constraints: Constraint[] } | YamlPropertyDefinition[];
    private laxParsing: boolean;

    constructor(private dataTypes: TDataType[], private enforcedType: string | QName) {
        this.precacheMetaInformation();
    }

    validate(control: AbstractControl): ValidationErrors | null {
        // reset stored errors
        const structuredValue = this.parseValue(control.value);
        if (structuredValue === undefined) {
            // this only happens if parsing is not lax OR the value could not be parsed as string after enquoting it
            return { 'typeConformance':  [ 'Could not parse entered value as JSON' ]};
        }
        // we cannot perform "static typechecking" on property functions defined in Section 4.4 of the spec
        if (isPropertyFunction(structuredValue)) {
            return null;
        }
        return this.fulfilsTypeDefinition(structuredValue);
    }

    private precacheMetaInformation() {
        if (isWellKnown(this.enforcedType)) {
            this.fullTypeDefinition = this.enforcedType;
            // these known types need to be parseable as objects because they are
            this.laxParsing = this.enforcedType !== 'list' && this.enforcedType !== 'map' && this.enforcedType !== 'range';
            return;
        }

        const typeIdentifier: string = (this.enforcedType instanceof QName) ? this.enforcedType.qName : this.enforcedType;
        const dataTypeInheritance = InheritanceUtils.getInheritanceAncestry(typeIdentifier, this.dataTypes);
        if (dataTypeInheritance.some(t => t.properties || ToscaUtils.getDefinition(t).properties)) {
            // assume that there's no constraints here, since the constraints don't really work on complex types in the first place
            this.fullTypeDefinition = this.handleComplexDataType(dataTypeInheritance);
            this.laxParsing = false;
            return;
        }
        // FIXME deal with checking for the basetype of the constrained type?!
        // aggregate constraints through the hierarchy
        const allConstraints = [];
        for (const ancestor of dataTypeInheritance) {
            // no need to check for ancestor properties, that's handled by handleComplexDataType
            for (const c of ToscaUtils.getDefinition(ancestor).constraints) {
                allConstraints.push(c);
            }
        }
        this.fullTypeDefinition = { constraints: allConstraints };
        this.laxParsing = true;
    }

    private handleComplexDataType(hierarchy: TDataType[]): YamlPropertyDefinition[] {
        const result = [];
        // it's useful to assume that the types themselves in the hierarchy do not have constraints
        // as such we only need to aggregate the properties enforced by each of the
        for (const parent of hierarchy) {
            const parentDefinition = ToscaUtils.getDefinition(parent);
            if (parentDefinition.properties === undefined) {
                continue;
            }
            // parentDefinition must have YAML properties if they are defined
            for (const property of parentDefinition.properties.properties || []) {
                // FIXME if necessary create a type definition for these ones as well!
                result.push(property);
            }
        }
        return result;
    }

    private fulfilsTypeDefinition(structuredValue: any): ValidationErrors | null {
        if (!this.fullTypeDefinition) {
            console.warn('No full type-definition was computed for type ' + this.enforcedType);
            return null;
        }
        if (typeof this.fullTypeDefinition === 'string') {
            return this.fulfilsWellKnownType(structuredValue, this.fullTypeDefinition) ? null
                : { 'typeConformance': [ `Value was not conform to TOSCA-YAML well known type ${this.fullTypeDefinition}.` ]};
        }
        if (this.fullTypeDefinition['constraints'] !== undefined) {
            // @ts-ignore Typescript doesn't correctly narrow the union type here
            return this.fulfilsKnownConstraints(structuredValue, this.fullTypeDefinition);
        }
        // @ts-ignore Typescript doesn't correctly narrow the union type here
        return this.fulfilsPropertyRequirements(structuredValue, this.fullTypeDefinition);
    }

    private fulfilsWellKnownType(structuredValue: any, knownType: string): boolean {
        switch (knownType) {
            case 'string':
                // consider that this might need to also accept stuff that's parseable as number, boolean or anything else
                return typeof structuredValue === 'string';
            case 'integer':
            case 'float':
                return typeof structuredValue === 'number';
            case 'boolean':
                return typeof structuredValue === 'boolean' || structuredValue === 'yes' || structuredValue === 'no';
            case 'timestamp':
                // FIXME actual check for conformance to typestamp value
                return typeof structuredValue === 'string';
            case 'null':
                // why ever you'd want to do this?
                return structuredValue === null || structuredValue === undefined;
            case 'version':
                return typeof structuredValue === 'string' && structuredValue.match(/\d+\.\d+(\.\d+(\..+?(-\d+)?)?)?/) !== undefined;
            case 'range':
                return structuredValue.isArray && structuredValue.length === 2;
            case 'list':
                return structuredValue.isArray;
            case 'map':
                return structuredValue.isObject;
            case 'scalar-unit.size':
            case 'scalar-unit.time':
            case 'scalar-unit.frequency':
            case 'scalar-unit.bitrate':
                // FIXME check unit conformance
                return typeof structuredValue === 'string';
            default:
                // not actually one of the well-known types here!
                return false;
        }
    }

    private fulfilsKnownConstraints(structuredValue: any, requirements: { constraints: Constraint[] }): ValidationErrors | null {
        let valid = true;
        const errors: ValidationErrors = [];
        for (const constraint of requirements.constraints) {
            if (!ConstraintChecking.isValid({ operator: constraint.key, value: constraint.list || constraint.value }, structuredValue)) {
                errors.push(`Value does not conform to constraint "${constraint.key} - ${constraint.list || constraint.value}"`);
                valid = false;
            }
        }
        return valid ? null : { 'typeConformance': errors };
    }

    private fulfilsPropertyRequirements(structuredValue: any, properties: YamlPropertyDefinition[]): ValidationErrors | null {
        let valid = true;
        const errors: ValidationErrors = [];
        if (structuredValue === undefined || structuredValue === null) {
            // FIXME deal with the question whether the form field is required
            return properties.some(prop => prop.required)
                ? [ `${this.enforcedType} has defined required properties!` ]
                : null;
        }
        for (const member in structuredValue) {
            if (properties.find(prop => prop.name === member) === undefined) {
                errors.push(`Includes the member ${member} that is not defined on the type`);
                valid = false;
            }
        }
        for (const requiredProperty of properties.filter(prop => prop.required)) {
            if (structuredValue[requiredProperty.name] === undefined) {
                errors.push(`Does not include the required member ${requiredProperty.name}`);
                valid = false;
            }
        }
        // FIXME recurse into the object to validate its properties!
        return valid ? null : {'typeConformance' : errors };
    }

    private parseValue(value: string): any {
        let result;
        try {
            result = JSON.parse(value);
        } catch (e) {
            if (!this.laxParsing) {
                // the value is not actually a string or something deriving from it
                // therefore we expect something that's parseable as JSON and bail here
                return undefined;
            }
            // try reparsing as string
            try {
                // this should never ever fail because we should be able to parse literally anything as a string, so long as we enquote it
                result = JSON.parse( '"' + value + '"');
            } catch (e) {
                return undefined;
            }
        }
        return result;
    }
}

const function_keys: string[] = [
    'get_input', 'get_property', 'get_attribute', 'get_operation_output', 'get_nodes_of_type', 'get_artifact'
];

function isPropertyFunction(structuredValue: any) {
    if (structuredValue === undefined || structuredValue === null) { return false; }
    for (const func of function_keys) {
        if (structuredValue.hasOwnProperty(func)) {
            // TODO evaluate whether we'd need to check for that key to be the only key present?
            return true;
        }
    }
    return false;
}
