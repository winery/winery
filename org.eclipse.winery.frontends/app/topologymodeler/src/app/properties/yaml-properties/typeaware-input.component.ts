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
import { ControlValueAccessor, FormControl, NG_VALIDATORS, NG_VALUE_ACCESSOR, ValidationErrors, Validator } from '@angular/forms';
import { TDataType } from '../../models/ttopology-template';
import { Subject } from 'rxjs';
import { debounceTime } from 'rxjs/operators';
import { BackendService } from '../../services/backend.service';
import { TypeConformanceValidator } from './type-conformance-validator';
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
    ]
})
export class TypeawareInputComponent implements ControlValueAccessor, OnInit, OnChanges, Validator {

    @Input()
    definition: YamlPropertyDefinition;

    // values used to render the view
    isDisabled: boolean;
    errors: string[] = [];
    _value: any;

    // storage for callbacks
    private _onChange: any = e => {};
    private _onTouch: any = e => {};
    private desugaredValidator: Validator = undefined;

    // this subject helps reduce computation load by debouncing validation triggers while the user adds input.
    private validationDebouncer: Subject<any> = new Subject<any>();
    private availableDataTypes: TDataType[] = [];

    JSON: JSON;

    constructor(private dataTypes: BackendService) {
        this.dataTypes.model$.subscribe(backendModel => {
            this.availableDataTypes = backendModel.dataTypes;
        });
    }

    ngOnInit() {
        this.validationDebouncer.pipe(debounceTime(350))
            .subscribe(control => {
                const errors = this.validate(control);
                if (errors) {
                    this.errors = errors['typeConformance'];
                } else {
                    // clear errors and emit change-event if no validation errors occurred
                    this.errors = [];
                    this._onChange(control.value);
                }
            });
        // this is a fix to make the global javascript object available inside the component template
        this.JSON = JSON;
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes.definition) {
            // compute the complete type definition including all required properties for this value
            this.definition = changes.definition.currentValue;
            this.desugaredValidator = new TypeConformanceValidator(this.availableDataTypes, this.definition);
        }
    }

    keyup(target: any): void {
        this.validationDebouncer.next(target);
        this._onTouch(target);
    }

    validate(control: FormControl): ValidationErrors | null {
        return this.desugaredValidator !== undefined && this.desugaredValidator.validate(control);
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
}
