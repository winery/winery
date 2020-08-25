/*******************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { TabsModule } from 'ngx-bootstrap';
import { SelectModule } from 'ng2-select';
import { WineryLoaderModule } from '../../../wineryLoader/wineryLoader.module';
import { WineryNamespaceSelectorModule } from '../../../wineryNamespaceSelector/wineryNamespaceSelector.module';
import { WineryDuplicateValidatorModule } from '../../../wineryValidators/wineryDuplicateValidator.module';
import { WineryModalModule } from '../../../wineryModalModule/winery.modal.module';
import { WineryTableModule } from '../../../wineryTableModule/wineryTable.module';
import { PropertiesDefinitionComponent } from './propertiesDefinition.component';
import { WineryFeatureToggleModule } from '../../../wineryFeatureToggleModule/winery-feature-toggle.module';
import { WineryDynamicTableModule } from '../../../wineryDynamicTable/wineryDynamicTable.module';

@NgModule({
    imports: [
        TabsModule.forRoot(),
        SelectModule,
        BrowserModule,
        FormsModule,
        WineryLoaderModule,
        CommonModule,
        WineryNamespaceSelectorModule,
        WineryModalModule,
        WineryTableModule,
        WineryDuplicateValidatorModule,
        WineryFeatureToggleModule,
        WineryDynamicTableModule,
    ],
    exports: [
    ],
    declarations: [
        PropertiesDefinitionComponent,
    ],
    providers: [],
})
export class PropertiesDefinitionModule {
}
