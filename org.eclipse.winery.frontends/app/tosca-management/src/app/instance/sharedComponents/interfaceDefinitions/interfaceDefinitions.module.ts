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
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { WineryLoaderModule } from '../../../wineryLoader/wineryLoader.module';
import { WineryDuplicateValidatorModule } from '../../../wineryValidators/wineryDuplicateValidator.module';
import { WineryModalModule } from '../../../wineryModalModule/winery.modal.module';
import { WineryTableModule } from '../../../wineryTableModule/wineryTable.module';
import { InterfaceDefinitionsComponent } from './interfaceDefinitions.component';
import { SelectableListModule } from '../interfaces/selectableList/selectableList.module';
import { ParametersModule } from '../parameters/parameters.module';
import { InterfaceDefinitionsService } from './interfaceDefinitions.service';
import { SelectModule } from 'ng2-select';
import { ArtifactsModule } from '../artifacts/artifacts.module';
import { DependenciesComponent } from './dependencies/dependencies.component';
import { FilesModule } from '../filesTag/files.module';

@NgModule({
    imports: [
        CommonModule,
        BrowserModule,
        FormsModule,
        SelectModule,
        WineryLoaderModule,
        WineryModalModule,
        WineryTableModule,
        SelectableListModule,
        WineryDuplicateValidatorModule,
        ParametersModule,
        ArtifactsModule,
        FilesModule
    ],
    exports: [
        InterfaceDefinitionsComponent,
    ],
    declarations: [
        InterfaceDefinitionsComponent,
        DependenciesComponent,
    ],
    providers: [
        InterfaceDefinitionsService,
    ],
})
export class InterfaceDefinitionsModule {
}
