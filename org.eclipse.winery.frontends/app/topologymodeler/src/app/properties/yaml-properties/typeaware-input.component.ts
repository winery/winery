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
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { TDataType } from '../../models/ttopology-template';
import { DataTypesService } from '../../../../../tosca-management/src/app/instance/dataTypes/dataTypes.service';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { YamlPropertiesComponent } from './yaml-properties.component';
import { BackendService } from '../../services/backend.service';
import { YamlPropertyDefinition } from '../../../../../tosca-management/src/app/model/yaml';

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
        DataTypesService
    ]
})
export class TypeawareInputComponent implements ControlValueAccessor, OnInit, OnChanges {

    @Input()
    type: TDataType;

    // values used to render the view
    isDisabled: boolean;
    _value: any;

    // storage for callbacks
    private _onChange: any;
    private _onTouch: any;

    // this subject helps reduce computation load by debouncing validation triggers while the user adds input.
    private validationDebouncer: Subject<any> = new Subject<any>();
    private availableDataTypes: TDataType[] = [];
    private fullTypeDefinition: YamlPropertyDefinition;
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
            // compute the complete typedefinition including all required properties for this value
            this.type = changes.type.currentValue;
            this.computeApplicablePropertyDefinitions();
        }
    }

    keyup(target: any): void {
        this.validationDebouncer.next(target.value);
    }

    private validateAndDispatch(value: string): void {
        // reset stored errors
        // this.errors = [];
        // FIXME we need to deal with the probably rather common case of failing to parse here
        const structuredValue = this.parseValue(value);
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
        // FIXME otherwise add the validation errors to the NgModel backing this field
    }

    private parseValue(value: string): any {
        let result;
        try {
            result = JSON.parse(value);
        } catch (e) {
            // try reparsing as string
            try {
                result = JSON.parse( '"' + value + '"');
            } catch (e) {
                // this case should never ever fail because we should be able to parse literally anything as a string, so long as we enquote it
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
        // FIXME implement the actual validation of the structured value against it's type
        return true;
    }

    private computeApplicablePropertyDefinitions() {
        // FIXME compute the complete type definition from the given TDataType
        this.fullTypeDefinition = undefined;
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
