/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Nicole Keppler, Lukas Balzer - initial API and implementation
 */
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { WineryLoaderModule } from '../../../wineryLoader/wineryLoader.module';
import { SelectModule } from 'ng2-select';
import { WineryTableModule } from '../../../wineryTableModule/wineryTable.module';
import { TypeaheadModule, TabsModule, ModalModule } from 'ngx-bootstrap';
import { WineryModalModule } from '../../../wineryModalModule/winery.modal.module';
import { TagComponent } from './tag.component';

import { WineryDuplicateValidatorModule } from '../../../wineryValidators/wineryDuplicateValidator.module';
import { WineryNamespaceSelectorModule } from '../../../wineryNamespaceSelector/wineryNamespaceSelector.module';

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
    declarations: [
        TagComponent
    ],
})
export class TagModule {
}
