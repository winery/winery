/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier - initial API and implementation
 */

import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { Ng2TableModule } from 'ng2-table';
import { TableComponent } from './table.component';
import { PaginationModule } from 'ng2-bootstrap';

@NgModule({
    imports: [
        PaginationModule.forRoot(),
        Ng2TableModule,
        BrowserModule,
        FormsModule,
    ],
    exports: [TableComponent],
    declarations: [TableComponent],
    providers: [],
})
export class TableModule {
}
