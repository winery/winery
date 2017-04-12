/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Balzer - initial API and implementation
 */
import { NgModule } from '@angular/core';
import { LoaderModule } from '../../loader/loader.module';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { TabsModule, TypeaheadModule } from 'ng2-bootstrap';
import { CommonModule } from '@angular/common';
import { WineryModalModule } from '../../wineryModalModule/winery.modal.module';
import { VisualAppearanceComponent } from './visualAppearance.component';
import { FileUploadModule } from 'ng2-file-upload';
import { WineryUploaderModule } from '../../fileUploaderModal/wineryUploader.module';

/**
 * an angular module for displaying the visualApperances for both the nodeTypes and the relationshipTypes
 * therefore an instance of <code>NodeTypesVisualsApiData</code> or <code>RelationshipTypesVisualsApiData</code> is loaded from the backend
 */
@NgModule({
    imports: [
        TabsModule.forRoot(),
        BrowserModule,
        FormsModule,
        LoaderModule,
        CommonModule,
        WineryModalModule,
        FileUploadModule,
        WineryUploaderModule
    ],
    exports: [],
    declarations: [
        VisualAppearanceComponent,
    ]
})
export class VisualAppearanceModule {
}
