/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Tino Stadelmaier - initial API and implementation
 */
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
