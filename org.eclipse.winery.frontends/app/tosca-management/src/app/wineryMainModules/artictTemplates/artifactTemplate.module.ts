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
import {NgModule} from '@angular/core';
import {ArtifactTemplateRouterModule} from './artifactTemplateRouter.module';
import {FilesComponent} from '../../instance/artifactTemplates/filesTag/files.component';
import {PropertiesComponent} from '../../instance/sharedComponents/properties/properties.component';
import {WineryModalModule} from '../../wineryModalModule/winery.modal.module';
import {WineryTableModule} from '../../wineryTableModule/wineryTable.module';
import {WineryUploaderModule} from '../../wineryUploader/wineryUploader.module';
import {CommonModule} from '@angular/common';
import {WineryLoaderModule} from '../../wineryLoader/wineryLoader.module';
import {FormsModule} from '@angular/forms';
import {HttpModule} from '@angular/http';
import {TabsModule} from 'ngx-bootstrap';
import {WineryEditorModule} from '../../wineryEditorModule/wineryEditor.module';
import {WineryReadmeModule} from '../../wineryReadmeModule/wineryReadme.module';
import {WineryLicenseModule} from '../../wineryLicenseModule/wineryLicense.module';
import {WinerySourceModule} from '../../instance/sharedComponents/artifactSource/source.module';

@NgModule({
    imports: [
        HttpModule,
        CommonModule,
        FormsModule,
        WineryLoaderModule,
        WineryModalModule,
        WineryTableModule,
        WineryUploaderModule,
        WinerySourceModule,
        TabsModule,
        WineryEditorModule,
        ArtifactTemplateRouterModule,
        WineryReadmeModule,
        WineryLicenseModule
    ],
    declarations: [
        FilesComponent,
        PropertiesComponent,
    ]
})
export class ArtifactTemplateModule {
}
