/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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
import { RouterModule } from '@angular/router';
import { ModalModule, TabsModule, TypeaheadModule } from 'ngx-bootstrap';
import { WineryTableModule } from '../../../wineryTableModule/wineryTable.module';
import { SelectModule } from 'ng2-select';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { WineryLoaderModule } from '../../../wineryLoader/wineryLoader.module';
import { CommonModule } from '@angular/common';
import { WineryModalModule } from '../../../wineryModalModule/winery.modal.module';
import { WineryNamespaceSelectorModule } from '../../../wineryNamespaceSelector/wineryNamespaceSelector.module';
import { WineryDuplicateValidatorModule } from '../../../wineryValidators/wineryDuplicateValidator.module';
import { WineryPipesModule } from '../../../wineryPipes/wineryPipes.module';
import { WineryAddModule } from '../../../wineryAddComponentModule/addComponent.moudle';
import { ArtifactsComponent } from './artifacts.component';
import { WineryQNameSelectorModule } from '../../../wineryQNameSelector/wineryQNameSelector.module';
import { ArtifactsService } from './artifacts.service';

@NgModule({
    imports: [
        RouterModule,
        ModalModule.forRoot(),
        WineryTableModule,
        TabsModule.forRoot(),
        SelectModule,
        BrowserModule,
        FormsModule,
        WineryLoaderModule,
        TypeaheadModule.forRoot(),
        CommonModule,
        WineryModalModule,
        WineryNamespaceSelectorModule,
        WineryDuplicateValidatorModule,
        WineryPipesModule,
        WineryAddModule,
        WineryQNameSelectorModule,
    ],
    exports: [],
    declarations: [
        ArtifactsComponent
    ],
    providers: [
        ArtifactsService
    ],
})
export class ArtifactsModule {
}
