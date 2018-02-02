/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
 ********************************************************************************/
import { NgModule } from '@angular/core';
import { SelfServicePortalComponent } from './selfServicePortal.component';
import { SelfServiceDescriptionComponent } from './selfServicePortalDescription.component';
import { HttpModule } from '@angular/http';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { ImageUploadComponent } from './imageUpload.component';
import { SelfServicePortalImagesComponent } from './selfServicePortalImages.component';
import { SelfServicePortalOptionsComponent } from './selfServicePortalOptions.component';
import { WineryTableModule } from '../../../wineryTableModule/wineryTable.module';
import { WineryUploaderModule } from '../../../wineryUploader/wineryUploader.module';
import { WineryLoaderModule } from '../../../wineryLoader/wineryLoader.module';
import { WineryEditorModule } from '../../../wineryEditorModule/wineryEditor.module';
import { WineryEditXMLModule } from '../../sharedComponents/editXML/editXML.module';

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        HttpModule,
        RouterModule,
        WineryEditorModule,
        WineryUploaderModule,
        WineryTableModule,
        WineryLoaderModule,
        WineryEditXMLModule
    ],
    exports: [],
    declarations: [
        SelfServicePortalComponent,
        SelfServiceDescriptionComponent,
        ImageUploadComponent,
        SelfServicePortalImagesComponent,
        SelfServicePortalOptionsComponent,
    ],
    providers: [],
})
export class SelfServicePortalModule {
}
