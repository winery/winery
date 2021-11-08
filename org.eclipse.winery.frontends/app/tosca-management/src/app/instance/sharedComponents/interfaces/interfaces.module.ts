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
import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {BrowserModule} from '@angular/platform-browser';
import {WineryLoaderModule} from '../../../wineryLoader/wineryLoader.module';
import {WineryNamespaceSelectorModule} from '../../../wineryNamespaceSelector/wineryNamespaceSelector.module';
import {WineryDuplicateValidatorModule} from '../../../wineryValidators/wineryDuplicateValidator.module';
import {WineryModalModule} from '../../../wineryModalModule/winery.modal.module';
import {WineryTableModule} from '../../../wineryTableModule/wineryTable.module';
import {InterfacesComponent} from './interfaces.component';
import {SelectableListModule} from './selectableList/selectableList.module';
import {ExistService} from '../../../wineryUtils/existService';
import {WineryIoParameterModule} from '../../../wineryIoParameter/wineryIoParameters.module';
import {WineryTargetInterfaceModule} from './targetInterface/wineryTargetInterface.module';
import {WineryComponentExistsModule} from '../../../wineryComponentExists/wineryComponentExists.module';

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        WineryLoaderModule,
        CommonModule,
        WineryComponentExistsModule,
        WineryModalModule,
        WineryTableModule,
        SelectableListModule,
        WineryTargetInterfaceModule,
        WineryDuplicateValidatorModule,
        WineryNamespaceSelectorModule,
        WineryIoParameterModule
    ],
    exports: [InterfacesComponent],
    declarations: [
        InterfacesComponent,
    ],
    providers: [
        ExistService
    ],
})
export class InterfacesModule {
}
