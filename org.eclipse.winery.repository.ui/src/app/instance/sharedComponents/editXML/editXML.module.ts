/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier, Lukas Harzenetter - initial API and implementation
 */

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
