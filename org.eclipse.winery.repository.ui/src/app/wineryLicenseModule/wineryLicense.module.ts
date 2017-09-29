/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { WineryLicenseComponent } from './wineryLicense.component';
import { WineryPipesModule } from '../wineryPipes/wineryPipes.module';
import { CommonModule } from '@angular/common';
import { BrowserModule } from '@angular/platform-browser';
import { BsDropdownModule } from 'ngx-bootstrap';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        BrowserModule,
        WineryPipesModule,
        BsDropdownModule.forRoot()
    ],
    exports: [
        WineryLicenseComponent
    ],
    declarations: [WineryLicenseComponent],
    providers: [],
})
export class WineryLicenseModule {
}
