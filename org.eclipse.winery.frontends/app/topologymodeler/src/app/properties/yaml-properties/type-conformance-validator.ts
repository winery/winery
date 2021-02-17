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
// TODO this should possibly not be from tosca-management
import { YamlPropertyDefinition } from '../../../../../tosca-management/src/app/model/yaml';
import { Constraint, isWellKnown, YamlWellKnown } from '../../../../../tosca-management/src/app/model/constraint';
import { ConstraintChecking } from '../property-constraints';
import { InheritanceUtils } from '../../models/InheritanceUtils';
import { ToscaUtils } from '../../models/toscaUtils';
import { QName } from 'app/shared/src/app/model/qName';

export class TypeConformanceValidator implements Validator {

    private readonly laxParsing: boolean;
    private readonly enforcedType: TDataType | YamlWellKnown;

    constructor(private dataTypes: TDataType[], private propDef: YamlPropertyDefinition) {
        const typeId = propDef.type;
        if (isWellKnown(typeId)) {
            this.enforcedType = typeId;
        } else {
            // typeId can be a QName or just the name of a type without namespacing
            this.enforcedType = dataTypes.find(t => t.qName === typeId || t.name === typeId);
        }
        if (this.enforcedType === undefined) {
            console.warn('Could not determine enforceable type for definition ' + JSON.stringify(propDef));
        }
        this.laxParsing =  this.determineParsingStandard(this.enforcedType);
    }

    private determineParsingStandard(enforcedType: TDataType | YamlWellKnown): boolean {
        if (isWellKnown(enforcedType)) {
            // these known types need to be parseable as objects because they are
            return enforcedType !== 'list' && enforcedType !== 'map' && enforcedType !== 'range';
        }
        const dataTypeInheritance = InheritanceUtils.getInheritanceAncestry(enforcedType.qName, this.dataTypes);
        return !dataTypeInheritance.some(definesProperties);
    }

    validate(control: AbstractControl): ValidationErrors | null {
        const structuredValue = this.parseValue(control.value);
        if (structuredValue === undefined && (this.propDef.required || control.value !== '')) {
            // this only happens if parsing is not lax OR the value could not be parsed as string after enquoting it
            return { 'typeConformance':  [ 'Could not parse entered value as JSON' ]};
        }
        // if (!this.propDef.required && structuredValue === null) {
        if (structuredValue === null || structuredValue === '') {
            // skip all validation for null values and empty form-fields
            return;
        }
        const results = this.fulfilsTypeDefinition(this.enforcedType, '',  structuredValue);
        for (const error of this.fulfilsConstraints(this.propDef.constraints, structuredValue, '')) {
            results.push(error);
        }
        return results.length === 0 ? null : { 'typeConformance': results };
    }

    private fulfilsTypeDefinition(type: TDataType | YamlWellKnown, valuePath: string, structuredValue: any): string[] {
        // we cannot perform "static typechecking" on property functions defined in Section 4.4 of the spec
        if (isPropertyFunction(structuredValue)) {
            return [];
        }
        if (isWellKnown(type)) {
            return this.fulfilsWellKnownType(structuredValue, type) ? []
                : [ `Value ${valuePath.substr(1)} was not conform to TOSCA-YAML well known type ${type}.` ];
        }
        const hierarchy = InheritanceUtils.getInheritanceAncestry(type.id, this.dataTypes);
        if (hierarchy.some(definesProperties)) {
            return this.fulfilsPropertyRequirements(structuredValue, hierarchy, valuePath);
        }
        return this.fulfilsKnownConstraints(structuredValue, hierarchy, valuePath);
    }

    /**
     * Checks whether a given structured value fulfils the requirements of a well-known type.
     * @param structuredValue the structured value to check
     * @param knownType the well-known type to check the value against
     */
    private fulfilsWellKnownType(structuredValue: any, knownType: YamlWellKnown): boolean {
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
                // FIXME check whether the borders used are in fact scalar values or UNBOUNDED
                return Array.isArray(structuredValue) && structuredValue.length === 2;
            case 'list':
                return Array.isArray(structuredValue);
            case 'map':
                // FIXME need to actually check that this is an object with SOME entries
                return structuredValue !== undefined;
            case 'scalar-unit':
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

    private fulfilsKnownConstraints(structuredValue: any, hierarchy: TDataType[], valuePath: string): string[] {
        const constraints: Constraint[] = [];
        for (const parent of hierarchy) {
            for (const c of ToscaUtils.getDefinition(parent).constraints) {
                constraints.push(c);
            }
        }
        return this.fulfilsConstraints(constraints, structuredValue, valuePath);
    }

    private fulfilsConstraints(constraints: Constraint[], structuredValue: any, valuePath: string) {
        const errors: string[] = [];
        for (const constraint of constraints) {
            if (!ConstraintChecking.isValid({ operator: constraint.key, value: constraint.list || constraint.value }, structuredValue)) {
                errors.push(`Value ${valuePath.substr(1)} does not conform to constraint "${constraint.key} - ${constraint.list || constraint.value}"`);
            }
        }
        return errors;
    }

    private fulfilsPropertyRequirements(structuredValue: any, hierarchy: TDataType[], valuePath: string): string[] {
        const properties: YamlPropertyDefinition[] = [];
        for (const parent of hierarchy) {
            if (definesProperties(parent)) {
                for (const prop of ToscaUtils.getDefinition(parent).propertiesDefinition.properties) {
                    properties.push(prop);
                }
            }
        }

        const errors: string[] = [];
        if (structuredValue === undefined || structuredValue === null) {
            // handling of "required" status is not on this level.
            // Assume no validation errors because structuredValue cannot have properties here
            return [];
        }
        // Verify all properties that are given for:
        //  1. being defined in the first place
        //  2. conforming to the type they are defined as
        //  3. fulfilling the narrowing constraints defined on the property definition itself
        // tslint:disable-next-line:forin
        for (const member in structuredValue) {
            const correspondingProperty = properties.find(prop => prop.name === member);
            if (correspondingProperty === undefined) {
                errors.push(`${valuePath.substr(1)} includes the member ${member} that is not defined on the type`);
                continue;
            }
            const memberType = this.resolveType(correspondingProperty.type);
            if (typeof memberType === 'undefined') {
                errors.push(`Could not resolve type ${correspondingProperty.type} of property ${correspondingProperty.name}`);
                continue;
            }
            const memberValue = structuredValue[member];
            if (memberValue === null && !correspondingProperty.required) {
                continue;
            }
            for (const error of this.fulfilsTypeDefinition(memberType, valuePath + '.' + member, memberValue)) {
                errors.push(error);
            }
            // propertiesDefinitions can include constraints. They are validated here
            const violations = this.fulfilsConstraints(correspondingProperty.constraints, memberValue, valuePath + '.' + member);
            for (const error of violations) {
                errors.push(error);
            }
        }
        for (const requiredProperty of properties.filter(prop => prop.required)) {
            if (structuredValue[requiredProperty.name] === undefined) {
                errors.push(`${valuePath.substr(1)} does not include the required member ${requiredProperty.name}`);
            }
        }
        return errors;
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
                result = JSON.parse( '"' + value.replace(/"/g, '\\"') + '"');
            } catch (e) {
                return undefined;
            }
        }
        return result;
    }

    private resolveType(type: QName | YamlWellKnown | string): YamlWellKnown | TDataType | undefined {
        if (isWellKnown(type)) {
            return type;
        }
        return this.dataTypes.find((t) => t.qName === type || t.id === type);
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

function definesProperties(type: TDataType): boolean {
    const full = ToscaUtils.getDefinition(type);
    return full.properties || (full.propertiesDefinition && full.propertiesDefinition.properties);
}
