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
import { WineryArtifactComponent } from './artifact.component';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { WineryTableModule } from '../../../wineryTableModule/wineryTable.module';
import { WineryLoaderModule } from '../../../wineryLoader/wineryLoader.module';
import { WineryModalModule } from '../../../wineryModalModule/winery.modal.module';
import { FormsModule } from '@angular/forms';
import { WineryQNameSelectorModule } from '../../../wineryQNameSelector/wineryQNameSelector.module';
import { WineryComponentExistsModule } from '../../../wineryComponentExists/wineryComponentExists.module';
import { WineryUploaderModule } from '../../../wineryUploader/wineryUploader.module';

@NgModule({
    imports: [
        BrowserModule,
        RouterModule,
        WineryComponentExistsModule,
        WineryTableModule,
        WineryLoaderModule,
        WineryModalModule,
        WineryQNameSelectorModule,
        WineryUploaderModule,
        FormsModule,
    ],
    exports: [WineryArtifactComponent],
    declarations: [
        WineryArtifactComponent,
    ]
})
export class WineryArtifactModule {
}
