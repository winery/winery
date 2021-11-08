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

import { WineryDynamicTableMetadata } from '../wineryDynamicTableMetadata';
import { Component } from '@angular/core';
import { DynamicFieldComponent } from './dynamic-field/dynamicFieldComponent';
import { FormGroup, ValidatorFn } from '@angular/forms';

@Component({
    selector: 'winery-dynamic-text-field',
    template: `
        <div [formGroup]="group">
            <label>{{ config.label }}</label>
            <input class="form-control" [formGroup]="group" [formControlName]="config.key">
            <div>
                <div *ngIf="textForm.errors && (textForm.dirty || textForm.touched)"
                     class="alert alert-danger">
                    <div [hidden]="!textForm.errors.required">Field is required</div>
                </div>
                <div *ngIf="parentFormGroup.errors && (textForm.dirty || textForm.touched)
                && this.config.key === parentFormGroup.errors.wineryDuplicateValidator.property"
                     class="alert alert-danger">
                    <div
                        [hidden]="!parentFormGroup.errors.wineryDuplicateValidator">
                        {{parentFormGroup.errors.wineryDuplicateValidator.message}}
                    </div>
                </div>
            </div>
        </div>
    `
})
export class DynamicTextComponent implements DynamicFieldComponent {
    config: DynamicTextData;
    group: FormGroup;

    get textForm() {
        return this.group.get(this.config.key);
    }

    get parentFormGroup() {
        return this.group;
    }
}

export class DynamicTextData extends WineryDynamicTableMetadata<string> {
    controlType = 'textbox';

    constructor(key: string,
                label: string,
                order?: number,
                validation?: ValidatorFn[] | ValidatorFn,
                defaultValue?: string,
                disabled?: boolean,
                sortTableCol?: boolean,
                isVisible?: boolean) {
        super(key, label, order, defaultValue, disabled, sortTableCol, isVisible, validation);
    }
}
