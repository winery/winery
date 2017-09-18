/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Nicole Keppler, Lukas Balzer - initial API and implementation
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { ModalModule, TabsModule, TypeaheadModule } from 'ngx-bootstrap';
import { SelectModule } from 'ng2-select';
import { WineryLoaderModule } from '../../../wineryLoader/wineryLoader.module';
import { WineryNamespaceSelectorModule } from '../../../wineryNamespaceSelector/wineryNamespaceSelector.module';
import { WineryDuplicateValidatorModule } from '../../../wineryValidators/wineryDuplicateValidator.module';
import { WineryModalModule } from '../../../wineryModalModule/winery.modal.module';
import { WineryTableModule } from '../../../wineryTableModule/wineryTable.module';
import { ImplementationsComponent } from './implementations.component';

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
    ],
    exports: [],
    declarations: [
        ImplementationsComponent
    ],
    providers: [],
})
export class ImplementationsModule {
}
