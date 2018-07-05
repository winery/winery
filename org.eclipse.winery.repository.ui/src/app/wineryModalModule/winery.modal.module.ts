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
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { ModalModule } from 'ngx-bootstrap';
import { WineryModalBodyComponent } from './winery.modal.body.component';
import { WineryModalComponent } from './winery.modal.component';
import { WineryModalFooterComponent } from './winery.modal.footer.component';
import { WineryModalHeaderComponent } from './winery.modal.header.component';

/**
 * This module must be imported in order to use the {@link WineryModalComponent}. Documentation on how to use
 * this component can also be found at the {@link WineryModalComponent}.
 */
@NgModule({
    imports: [
        CommonModule,
        BrowserModule,
        ModalModule.forRoot(),
    ],
    exports: [
        WineryModalComponent,
        WineryModalBodyComponent,
        WineryModalHeaderComponent,
        WineryModalFooterComponent,
        ModalModule
    ],
    declarations: [
        WineryModalComponent,
        WineryModalBodyComponent,
        WineryModalHeaderComponent,
        WineryModalFooterComponent,
    ],
    providers: [],
})
export class WineryModalModule {
}
