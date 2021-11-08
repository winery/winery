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
import { FormGroup } from '@angular/forms';

@Component({
    selector: 'winery-dynamic-checkbox',
    template: `
        <div [formGroup]="group">

            <label>{{ config.label }}</label>
            <input type="checkbox" name="required" [formGroup]="group" [formControlName]="config.key" [id]="config.key"/>
        </div>
    `
})
export class DynamicCheckboxComponent implements DynamicFieldComponent {
    config: DynamicCheckboxData;
    group: FormGroup;
}

export class DynamicCheckboxData extends WineryDynamicTableMetadata<boolean> {
    controlType = 'checkbox';

    constructor(key: string,
                label: string,
                defaultValue?: boolean,
                order?: number,
                disabled?: boolean,
                sortTableCol?: boolean,
                isVisible?: boolean) {
        super(key, label, order, defaultValue, disabled, sortTableCol, isVisible);
    }
}
