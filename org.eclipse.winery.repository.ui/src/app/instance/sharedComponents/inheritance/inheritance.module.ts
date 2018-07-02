/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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

import { InheritanceComponent } from './inheritance.component';
import { CommonModule } from '@angular/common';
import { WineryModalModule } from '../../../wineryModalModule/winery.modal.module';
import { WineryLoaderModule } from '../../../wineryLoader/wineryLoader.module';
import { FormsModule } from '@angular/forms';
import { WineryQNameSelectorModule } from '../../../wineryQNameSelector/wineryQNameSelector.module';
import { SelectModule } from 'ng2-select';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { WineryAddModule } from '../../../wineryAddComponentModule/addComponent.moudle';

@NgModule({
    imports: [
        CommonModule,
        BrowserModule,
        SelectModule,
        FormsModule,
        CommonModule,
        RouterModule,
        WineryModalModule,
        WineryLoaderModule,
        WineryQNameSelectorModule,
        WineryAddModule,
    ],
    exports: [InheritanceComponent],
    declarations: [InheritanceComponent],
    providers: [],
})
export class InheritanceModule {
}
