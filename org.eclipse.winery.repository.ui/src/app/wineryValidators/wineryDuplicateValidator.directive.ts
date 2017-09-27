/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Directive, Input, OnChanges, SimpleChanges } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, Validator, ValidatorFn, Validators } from '@angular/forms';
import { isNullOrUndefined } from 'util';

export class WineryValidatorObject {
    list: Array<any>;
    property?: string;

    constructor(list: Array<any>, property?: string) {
        this.list = list;
        this.property = property;
    }

    validate(compareObject: WineryValidatorObject): ValidatorFn {
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
            return no ? {'wineryDuplicateValidator': {name}} : null;
        };
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
