/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter, Niko Stadelmaier - initial API and implementation
 */

import { Directive, Input, OnChanges, SimpleChanges } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, Validator, ValidatorFn, Validators } from '@angular/forms';
import { isNullOrUndefined } from 'util';

export class ValidatorObject {
    list: Array<any>;
    property?: string;

    constructor(list: Array<any>, property?: string) {
        this.list = list;
        this.property = property;
    }
}

export function duplicateValidator(compareObject: ValidatorObject): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } => {
        if (isNullOrUndefined(compareObject) || isNullOrUndefined(compareObject.list)) {
            return null;
        }
        const name = control.value;
        let no = false;
        if (isNullOrUndefined(compareObject.property)) {
            no = compareObject.list.find(item => item === name);
        } else {
            no = compareObject.list.find(item => item[compareObject.property] === name);
        }
        return no ? { 'duplicateValidator': { name } } : null;
    };
}

@Directive({
    selector: '[duplicateValidator]',
    providers: [{ provide: NG_VALIDATORS, useExisting: DuplicateValidatorDirective, multi: true }]
})
export class DuplicateValidatorDirective implements Validator, OnChanges {
    @Input() duplicateValidator: { list: Array<any>, prop: string };
    private valFn = Validators.nullValidator;

    ngOnChanges(changes: SimpleChanges): void {
        const change = changes['duplicateValidator'];
        if (change) {
            const val: ValidatorObject = change.currentValue;
            this.valFn = duplicateValidator(val);
        } else {
            this.valFn = Validators.nullValidator;
        }
    }

    validate(control: AbstractControl): { [key: string]: any } {
        return this.valFn(control);
    }
}
