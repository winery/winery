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
import { TabsModule } from 'ngx-bootstrap';
import { SelectModule } from 'ng2-select';
import { WineryLoaderModule } from '../../../wineryLoader/wineryLoader.module';
import { WineryNamespaceSelectorModule } from '../../../wineryNamespaceSelector/wineryNamespaceSelector.module';
import { WineryDuplicateValidatorModule } from '../../../wineryValidators/wineryDuplicateValidator.module';
import { WineryModalModule } from '../../../wineryModalModule/winery.modal.module';
import { WineryTableModule } from '../../../wineryTableModule/wineryTable.module';
import { PropertiesDefinitionComponent } from './propertiesDefinition.component';

@NgModule({
    imports: [
        TabsModule.forRoot(),
        SelectModule,
        BrowserModule,
        FormsModule,
        WineryLoaderModule,
        CommonModule,
        WineryNamespaceSelectorModule,
        WineryModalModule,
        WineryTableModule,
        WineryDuplicateValidatorModule,
    ],
    exports: [],
    declarations: [
        PropertiesDefinitionComponent,
    ],
    providers: [],
})
export class PropertiesDefinitionModule {
}
