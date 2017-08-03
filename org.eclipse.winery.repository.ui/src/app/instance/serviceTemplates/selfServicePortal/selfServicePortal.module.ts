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
