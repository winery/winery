/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter - initial API and implementation
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { LoaderModule } from '../../loader/loader.module';
import { NamespaceSelectorModule } from '../../namespaceSelector/namespaceSelector.module';
import { DuplicateValidatorModule } from '../../validators/duplicateValidator.module';
import { WineryModalModule } from '../../wineryModalModule/winery.modal.module';
import { WineryTableModule } from '../../wineryTableModule/wineryTable.module';
import { InterfacesComponent } from './interfaces.component';
import { SelectableListModule } from './selectableList/selectableList.module';
import { ExistService } from '../../util/existService';

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        LoaderModule,
        CommonModule,
        WineryModalModule,
        WineryTableModule,
        SelectableListModule,
        DuplicateValidatorModule,
        NamespaceSelectorModule,
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
