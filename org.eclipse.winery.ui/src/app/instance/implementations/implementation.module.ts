/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Nicole Keppler - initial API and implementation
 */
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LoaderModule } from '../../loader/loader.module';
import { NgModule } from '@angular/core';
import { SelectModule } from 'ng2-select';
import { WineryTableModule } from '../../wineryTableModule/wineryTable.module';
import { TypeaheadModule, TabsModule, ModalModule } from 'ng2-bootstrap';
import { WineryModalModule } from '../../wineryModalModule/winery.modal.module';
import { ImplementationsComponent } from './implementations.component';
import { RouterModule } from '@angular/router';

@NgModule({
    imports: [
        RouterModule,
        ModalModule.forRoot(),
        WineryTableModule,
        TabsModule.forRoot(),
        SelectModule,
        BrowserModule,
        FormsModule,
        LoaderModule,
        TypeaheadModule.forRoot(),
        CommonModule,
        WineryModalModule,
        ],
    exports: [],
    declarations: [
        ImplementationsComponent
    ],
    providers: [],
})
export class ImplementationsModule {
}
