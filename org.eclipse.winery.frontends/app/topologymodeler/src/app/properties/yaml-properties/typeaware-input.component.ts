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

import { Component, forwardRef, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { ControlValueAccessor, FormControl, NG_VALIDATORS, NG_VALUE_ACCESSOR } from '@angular/forms';
import { TDataType } from '../../models/ttopology-template';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { BackendService } from '../../services/backend.service';
// FIXME these probably shouldn't be the management constraints
import { Constraint, isWellKnown } from '../../../../../tosca-management/src/app/model/constraint';
import { QName } from '../../models/qname';
import { InheritanceUtils } from '../../models/InheritanceUtils';
import { ToscaUtils } from '../../models/toscaUtils';
import { YamlPropertyDefinition } from '../../../../../tosca-management/src/app/model/yaml';
import { ConstraintChecking } from '../property-constraints';

/**
 * This is an input component that is aware of the DataType that the value it receives must conform to.
 * It's internally validating the input given by the user and respects property functions that compute the actual value
 * of the property later.
 * It will only emit a <tt>changed</tt> event for values that conform to the type it expects.
 * Values, especially complex objects are expected to be specified (and parseable) as JSON.
 */
@Component({
    selector: 'winery-properties-input',
    templateUrl: 'typeaware-input.component.html',
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => TypeawareInputComponent),
            multi: true
        },
        // {
        //     provide: NG_VALIDATORS,
        //     useExisting: forwardRef(() => TypeawareInputComponent),
        //     multi: true
        // },
    ]
})
export class TypeawareInputComponent implements ControlValueAccessor, OnInit, OnChanges {

    @Input()
    type: QName | string;

    // values used to render the view
    isDisabled: boolean;
    errors: string[] = [];
    _value: any;

    // storage for callbacks
    private _onChange: any;
    private _onTouch: any;

    // this subject helps reduce computation load by debouncing validation triggers while the user adds input.
    private validationDebouncer: Subject<any> = new Subject<any>();
    private availableDataTypes: TDataType[] = [];

    private fullTypeDefinition: string | { constraints: Constraint[] } | YamlPropertyDefinition[];
    private laxParsing: boolean;
    JSON: JSON;

    constructor(private dataTypes: BackendService) {
        this.dataTypes.model$.subscribe(backendModel => {
            this.availableDataTypes = backendModel.dataTypes;
        });
    }

    ngOnInit() {
        this.validationDebouncer.pipe(
            debounceTime(350), distinctUntilChanged(),
        ).subscribe(value => this.validateAndDispatch(value));
        // this is a fix to make the global javascript object available inside the component template
        this.JSON = JSON;
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes.type) {
            // compute the complete type definition including all required properties for this value
            this.type = changes.type.currentValue;
            this.computeApplicablePropertyDefinitions();
        }
    }

    keyup(target: any): void {
        this.validationDebouncer.next(target.value);
    }

    private validateAndDispatch(value: string): void {
        // reset stored errors
        this.errors = [];
        const structuredValue = this.parseValue(value);
        if (structuredValue === undefined) {
            // this only happens if parsing is not lax or the value could not be parsed as string after enquoting it
            this.pushError('Could not parse entered value as JSON');
            return;
        }
        // we cannot perform "static typechecking" on property functions defined in Section 4.4 of the spec
        if (isPropertyFunction(structuredValue)) {
            this._onChange(structuredValue);
            return;
        }
        // TODO perform actual static typechecking here
        if (this.fulfilsTypeDefinition(structuredValue)) {
            this._onChange(structuredValue);
            return;
        }
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
                return;
            }
        }
        return result;
    }

    registerOnChange(fn: any): void {
        this._onChange = fn;
    }

    registerOnTouched(fn: any): void {
        this._onTouch = fn;
    }

    writeValue(obj: any): void {
        this._value = obj;
    }

    setDisabledState(isDisabled: boolean) {
        this.isDisabled = isDisabled;
    }

    unQuote(value: string): string {
        if (value === undefined) {
            return '';
        }
        if (value.startsWith('"') && value.endsWith('"')) {
            return value.substr(1, value.length - 2);
        }
        return value;
    }

    private fulfilsTypeDefinition(structuredValue: any) {
        if (!this.fullTypeDefinition) {
            console.warn('No full type-definition was computed for type ' + this.type);
            return true;
        }
        if (typeof this.fullTypeDefinition === 'string') {
            return this.fulfilsWellKnownType(structuredValue, this.fullTypeDefinition);
        }
        if (!this.fullTypeDefinition['constraints'] !== undefined) {
            // @ts-ignore Typescript doesn't correctly narrow the union type here
            return this.fulfilsKnownConstraints(structuredValue, this.fullTypeDefinition);
        }
        // @ts-ignore Typescript doesn't correctly narrow the union type here
        return this.fulfilsPropertyRequirements(structuredValue, this.fullTypeDefinition);
    }

    private computeApplicablePropertyDefinitions() {
        if (isWellKnown(this.type)) {
            this.fullTypeDefinition = this.type;
            // these known types need to be parseable as objects because they are
            this.laxParsing = this.type !== 'list' && this.type !== 'map' && this.type !== 'range';
            return;
        }

        const typeIdentifier: string = (this.type instanceof QName) ? this.type.qName : this.type;
        const dataTypeInheritance = InheritanceUtils.getInheritanceAncestry(typeIdentifier, this.availableDataTypes);
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
            for (const property of parent.properties) {
                // FIXME if necessary create a type definition for these ones as well!
                result.push(property);
            }
        }
        return result;
    }

    private pushError(message: string) {
        this.errors.push(message);
    }

    private fulfilsWellKnownType(structuredValue: any, knownType: string) {
        switch (knownType) {
            case 'string':
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
                return typeof structuredValue === 'string' && structuredValue.match(/\d+\.\d+(\.\d+(\..+?(-\d+)?)?)?/);
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

    private fulfilsKnownConstraints(structuredValue: any, requirements: { constraints: Constraint[] }) {
        let valid = true;
        for (const constraint of requirements.constraints) {
            if (!ConstraintChecking.isValid({ operator: constraint.key, value: constraint.list || constraint.value }, structuredValue)) {
                this.pushError(`Value does not conform to constraint "${constraint.key} - ${constraint.list || constraint.value}"`);
                valid = false;
            }
        }
        return valid;
    }

    private fulfilsPropertyRequirements(structuredValue: any, properties: YamlPropertyDefinition[]) {
        let valid = true;
        for (const member in structuredValue) {
            if (properties.find(prop => prop.name === member) !== undefined) {
                this.pushError(`Includes the member ${member} that is not defined on the type`);
                valid = false;
            }
        }
        for (const requiredProperty of properties.filter(prop => prop.required)) {
            if (structuredValue[requiredProperty.name] === undefined) {
                this.pushError(`Does not include the required member ${requiredProperty.name}`);
                valid = false;
            }
        }
        return valid;
    }
}


const function_keys: string[] = [
    'get_input', 'get_property', 'get_attribute', 'get_operation_output', 'get_nodes_of_type', 'get_artifact'
];

function isPropertyFunction(structuredValue: any) {
    for (const func of function_keys) {
        if (structuredValue.hasOwnProperty(func)) {
            // TODO evaluate whether we'd need to check for that key to be the only key present?
            return true;
        }
    }
    return false;
}
