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

import { NgModule } from '@angular/core';

import { EditXMLComponent } from './editXML.component';
import { WineryLoaderModule } from '../../../wineryLoader/wineryLoader.module';
import { WineryEditorModule } from '../../../wineryEditorModule/wineryEditor.module';
import { HttpModule } from '@angular/http';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@NgModule({
    imports: [
        HttpModule,
        CommonModule,
        FormsModule,
        BrowserModule,
        WineryLoaderModule,
        WineryEditorModule
    ],
    exports: [EditXMLComponent],
    declarations: [EditXMLComponent],
    providers: [],
})
export class WineryEditXMLModule {
}
