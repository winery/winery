/*******************************************************************************
 * Copyright (c) 2017-2022 Contributors to the Eclipse Foundation
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

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { WineryLicenseComponent } from './wineryLicense.component';
import { WineryPipesModule } from '../wineryPipes/wineryPipes.module';
import { CommonModule } from '@angular/common';
import { BrowserModule } from '@angular/platform-browser';
import { MatButtonModule } from '@angular/material/button';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatStepperModule } from '@angular/material/stepper';
import { WineryModalModule } from '../wineryModalModule/winery.modal.module';
import { WineryLoaderModule } from '../wineryLoader/wineryLoader.module';


@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        BrowserModule,
        WineryPipesModule,
        ReactiveFormsModule,
        MatStepperModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        WineryModalModule,
        WineryLoaderModule,
        MatProgressBarModule,
        MatSelectModule,
        MatListModule,
        MatExpansionModule,
        MatIconModule,
    ],
    exports: [
        WineryLicenseComponent
    ],
    declarations: [WineryLicenseComponent],
    providers: [],
})
export class WineryLicenseModule {
}
