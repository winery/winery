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
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { AttributesComponent } from './attributes.component';
import { AttributesService } from './attributes.service';
import { TabsModule } from 'ngx-bootstrap';
import { SelectModule } from 'ng2-select';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { WineryLoaderModule } from '../../../wineryLoader/wineryLoader.module';
import { WineryModalModule } from '../../../wineryModalModule/winery.modal.module';
import { WineryTableModule } from '../../../wineryTableModule/wineryTable.module';
import { WineryDuplicateValidatorModule } from '../../../wineryValidators/wineryDuplicateValidator.module';
import { WineryDynamicTableModule } from '../../../wineryDynamicTable/wineryDynamicTable.module';

@NgModule({
    imports: [
        TabsModule.forRoot(),
        SelectModule,
        BrowserModule,
        FormsModule,
        CommonModule,
        WineryLoaderModule,
        WineryModalModule,
        WineryTableModule,
        WineryDuplicateValidatorModule,
        WineryDynamicTableModule,
    ],
    exports: [],
    declarations: [
        AttributesComponent,
    ],
    providers: [
        AttributesService
    ],
})
export class AttributesModule {
}
