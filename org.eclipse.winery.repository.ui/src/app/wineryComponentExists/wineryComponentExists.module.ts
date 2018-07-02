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

import {WineryComponentExistsComponent} from './wineryComponentExists.component';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {BrowserModule} from '@angular/platform-browser';
import {ExistService} from '../wineryUtils/existService';
import {WineryNamespaceSelectorModule} from '../wineryNamespaceSelector/wineryNamespaceSelector.module';
import {WineryPipesModule} from '../wineryPipes/wineryPipes.module';

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        CommonModule,
        WineryNamespaceSelectorModule,
        WineryPipesModule,
    ],
    exports: [WineryComponentExistsComponent],
    declarations: [WineryComponentExistsComponent],
    providers: [
        ExistService
    ],
})
export class WineryComponentExistsModule {
}
