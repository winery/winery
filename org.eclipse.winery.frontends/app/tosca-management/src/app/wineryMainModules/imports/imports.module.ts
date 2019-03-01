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
import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SectionModule} from '../../section/section.module';
import {ImportRouterModule} from './importRouting.module';
import {ListDefinedTypesAndElementsComponent} from '../../instance/imports/listDefinedTypesAndElements.component';
import {WineryLoaderModule} from '../../wineryLoader/wineryLoader.module';
import {WineryTableModule} from '../../wineryTableModule/wineryTable.module';

@NgModule({
    imports: [
        CommonModule,
        SectionModule,
        ImportRouterModule,
        WineryLoaderModule,
        WineryTableModule
    ],
    declarations: [
        ListDefinedTypesAndElementsComponent
    ]
})
export class ImportModule {
}
