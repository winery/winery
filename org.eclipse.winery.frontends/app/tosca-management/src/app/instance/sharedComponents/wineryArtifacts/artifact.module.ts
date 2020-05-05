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
import {WineryArtifactComponent} from './artifact.component';
import {BrowserModule} from '@angular/platform-browser';
import {RouterModule} from '@angular/router';
import {WineryTableModule} from '../../../wineryTableModule/wineryTable.module';
import {WineryLoaderModule} from '../../../wineryLoader/wineryLoader.module';
import {WineryModalModule} from '../../../wineryModalModule/winery.modal.module';
import {FormsModule} from '@angular/forms';
import {WineryQNameSelectorModule} from '../../../wineryQNameSelector/wineryQNameSelector.module';
import {WineryComponentExistsModule} from '../../../wineryComponentExists/wineryComponentExists.module';
import {WineryUploaderModule} from '../../../wineryUploader/wineryUploader.module';
import { WineryAddDataModule } from '../../../wineryAddComponentDataModule/addComponentData.module';
import { TooltipModule } from 'ngx-bootstrap';

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
        WineryAddDataModule,
        TooltipModule,
    ],
    exports: [WineryArtifactComponent],
    declarations: [
        WineryArtifactComponent,
    ]
})
export class WineryArtifactModule {
}
