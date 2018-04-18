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
import { NgModule } from '@angular/core';
import { InstanceComponent } from './instance.component';
import { InstanceHeaderComponent } from './instanceHeader/instanceHeader.component';
import { WineryLoaderModule } from '../wineryLoader/wineryLoader.module';
import { WineryModalModule } from '../wineryModalModule/winery.modal.module';
import { WineryPipesModule } from '../wineryPipes/wineryPipes.module';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PropertyRenameComponent } from './instanceHeader/propertyRename/propertyRename.component';
import { FormsModule } from '@angular/forms';
import { AlertModule, BsDropdownModule } from 'ngx-bootstrap';
import { WineryAddVersionModule } from '../wineryVersionModule/wineryVersion.module';

@NgModule({
    imports: [
        CommonModule,
        RouterModule,
        WineryLoaderModule,
        WineryModalModule,
        WineryPipesModule,
        FormsModule,
        WineryAddVersionModule,
        AlertModule.forRoot(),
        BsDropdownModule.forRoot()
    ],
    exports: [InstanceComponent],
    declarations: [
        InstanceComponent,
        InstanceHeaderComponent,
        PropertyRenameComponent
    ],
    providers: [],
})
export class InstanceModule {
}
