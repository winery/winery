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

import {FormsModule} from '@angular/forms';
import {BrowserModule} from '@angular/platform-browser';
import {CollapseModule, TypeaheadModule} from 'ngx-bootstrap';
import {WineryLoaderModule} from '../wineryLoader/wineryLoader.module';
import {WineryNamespaceSelectorComponent} from './wineryNamespaceSelector.component';

@NgModule({
    imports: [
        FormsModule,
        BrowserModule,
        TypeaheadModule,
        WineryLoaderModule,
        CollapseModule.forRoot()
    ],
    exports: [WineryNamespaceSelectorComponent],
    declarations: [WineryNamespaceSelectorComponent],
    providers: [],
})
export class WineryNamespaceSelectorModule {
}
