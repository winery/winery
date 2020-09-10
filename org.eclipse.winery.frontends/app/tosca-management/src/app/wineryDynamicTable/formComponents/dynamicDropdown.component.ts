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
    selector: 'winery-dynamic-dropdown',
    template: `
        <div [formGroup]="group">
            <label>{{ config.label }}</label>
            <select class="form-control" [id]="config.key" [formControlName]="config.key">
                <option *ngFor="let opt of config.options" [value]="opt.value">{{opt.label}}</option>
            </select>
        </div>
    `
})
export class DynamicDropdownComponent implements DynamicFieldComponent {
    config: DynamicDropdownData;
    group: FormGroup;
}

export class DynamicDropdownData<T = {}> extends WineryDynamicTableMetadata<T> {
    controlType = 'dropdown';

    constructor(key: string,
                label: string,
                public options: { label: string, value: string }[],
                order?: number,
                defaultValue?: T,
                validation?: ValidatorFn[] | ValidatorFn,
                disabled?: boolean,
                sortTableCol?: boolean,
                isVisible?: boolean) {
        super(key, label, order, defaultValue, disabled, sortTableCol, isVisible);
    }
}
