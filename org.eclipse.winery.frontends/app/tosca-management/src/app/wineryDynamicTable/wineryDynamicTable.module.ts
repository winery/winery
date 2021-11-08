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

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { WineryDynamicFormModalComponent } from './modal/wineryDynamicFormModal.component';
import { WineryDynamicTableComponent } from './wineryDynamicTable.component';
import { WineryTableModule } from '../wineryTableModule/wineryTable.module';
import { WineryModalModule } from '../wineryModalModule/winery.modal.module';
import { DynamicTextComponent } from './formComponents/dynamicText.component';
import { DynamicDropdownComponent } from './formComponents/dynamicDropdown.component';
import { DynamicCheckboxComponent } from './formComponents/dynamicCheckbox.component';
import { WineryDynamicFieldDirective } from './formComponents/dynamic-field/wineryDynamicField.directive';
import { DynamicConstraintsComponent } from './formComponents/dynamicConstraints/dynamicConstraints.component';

@NgModule({
    declarations: [
        WineryDynamicFormModalComponent,
        WineryDynamicTableComponent,
        DynamicTextComponent,
        DynamicDropdownComponent,
        DynamicCheckboxComponent,
        DynamicConstraintsComponent,
        WineryDynamicFieldDirective],
    exports: [
        WineryDynamicTableComponent,
        WineryDynamicFormModalComponent
    ],
    imports: [
        CommonModule, ReactiveFormsModule, BrowserModule, WineryTableModule, WineryModalModule
    ],
    entryComponents: [
        DynamicTextComponent,
        DynamicDropdownComponent,
        DynamicCheckboxComponent,
        DynamicConstraintsComponent
    ]
})
export class WineryDynamicTableModule {
    constructor() {
    }
}
