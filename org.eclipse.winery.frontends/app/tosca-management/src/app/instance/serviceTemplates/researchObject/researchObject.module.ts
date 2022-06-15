/*******************************************************************************
 * Copyright (c) 2021-2022 Contributors to the Eclipse Foundation
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
import { HttpModule } from '@angular/http';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { WineryLoaderModule } from '../../../wineryLoader/wineryLoader.module';
import { ResearchObjectPublicationComponent } from './researchObjectPublication.component';
import { ResearchObjectMetaDataComponent } from './researchObjectMetaData.component';
import { ResearchObjectComponent } from './researchObject.component';
import { SelectModule } from 'ng2-select';
import { FileUploaderModule } from '../../sharedComponents/files/fileUploader/fileUploader.module';
import { ResearchObjectFilesComponent } from './researchObjectFiles.component';
import { FileManagerModule } from '../../sharedComponents/files/fileManager/fileManager.module';
import {
    MatExpansionModule, MatFormFieldModule, MatIconModule, MatInputModule, MatProgressBarModule
} from '@angular/material';
import { MatButtonModule } from '@angular/material/button';


@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        HttpModule,
        RouterModule,
        WineryLoaderModule,
        SelectModule,
        FileUploaderModule,
        FileManagerModule,
        MatExpansionModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatProgressBarModule,
        MatIconModule
    ],
    exports: [],
    declarations: [
        ResearchObjectComponent,
        ResearchObjectPublicationComponent,
        ResearchObjectMetaDataComponent,
        ResearchObjectFilesComponent
    ],
    providers: [],
})
export class ResearchObjectModule {
}
