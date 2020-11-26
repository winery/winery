/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
import {Directive, Input, OnChanges, SimpleChanges} from '@angular/core';
import { AbstractControl, FormGroup, NG_VALIDATORS, ValidationErrors, Validator, ValidatorFn, Validators } from '@angular/forms';
import {isNullOrUndefined} from 'util';

export class WineryValidatorObject {

    regEx?: RegExp;
    private active = true;

    constructor(private list: Array<any>, private property?: string, private element?: any) {
    }

    public setRegExp(regExp: RegExp) {
        this.regEx = regExp;
    }

    static duplicateValidator(list?: Array<any>, property?: string, uuid?: string): ValidatorFn {
        return (control: FormGroup): ValidationErrors | null => {
            if (!list || !control.value || control.value === '') {
                return null;
            }
            const value = control.value[property];
            const errors: ValidationErrors = {};
            let duplicate: any = undefined;
            if (uuid) {
                if (!property) {
                    duplicate = list.find(item => item === value && item['uuid'] !== uuid);
                } else {
                    duplicate = list.find(item => item[property] === value && item['uuid'] !== uuid);
                }
            } else {
                if (!property) {
                    duplicate = list.find(item => item === value);
                } else {
                    duplicate = list.find(item => item[property] === value);
                }
            }
            if (duplicate) {
                errors.wineryDuplicateValidator = {
                    message: 'No duplicates allowed',
                    property: property
                };
            }
            return Object.keys(errors).length ? errors : null;
        };
    }

    // deprecated
    validate(compareObject: WineryValidatorObject): ValidatorFn {
        return (control: AbstractControl): { [key: string]: any } => {
            if (isNullOrUndefined(compareObject) || isNullOrUndefined(compareObject.list) || !this.active) {
                return null;
            }
            const name = control.value;
            let no = false;
            if (isNullOrUndefined(compareObject.property)) {
                no = compareObject.list.find(item => item !== this.element && item === name);
            } else {
                no = compareObject.list.find(item => item !== this.element && item[compareObject.property] === name);
            }
            if (!isNullOrUndefined(compareObject.regEx)) {
                no = !compareObject.regEx.test(name);
            }
            return no ? {wineryDuplicateValidator: {name}} : null;
        };
    }

    set isActive(value: boolean) {
        this.active = value;
    }

    get isActive(): boolean {
        return this.active;
    }
}

@Directive({
    selector: '[wineryDuplicateValidator]',
    providers: [{provide: NG_VALIDATORS, useExisting: WineryDuplicateValidatorDirective, multi: true}]
})
export class WineryDuplicateValidatorDirective implements Validator, OnChanges {

    @Input() wineryDuplicateValidator: WineryValidatorObject;

    private valFn = Validators.nullValidator;

    ngOnChanges(changes: SimpleChanges): void {
        const change = changes['wineryDuplicateValidator'];
        if (change && !isNullOrUndefined(this.wineryDuplicateValidator)) {
            const val: WineryValidatorObject = change.currentValue;
            this.valFn = this.wineryDuplicateValidator.validate(val);
        } else {
            this.valFn = Validators.nullValidator;
        }
    }

    validate(control: AbstractControl): { [key: string]: any } {
        return this.valFn(control);
    }
}
