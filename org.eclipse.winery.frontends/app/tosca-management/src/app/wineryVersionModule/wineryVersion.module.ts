/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
 ********************************************************************************/
import {NgModule} from '@angular/core';
import {WineryVersionComponent} from './wineryVersion.component';
import {CommonModule} from '@angular/common';
import {WineryModalModule} from '../wineryModalModule/winery.modal.module';
import {FormsModule} from '@angular/forms';
import {WineryLoaderModule} from '../wineryLoader/wineryLoader.module';
import {WineryNotificationModule} from '../wineryNotificationModule/wineryNotification.module';
import {WineryDuplicateValidatorModule} from '../wineryValidators/wineryDuplicateValidator.module';
import {RouterModule} from '@angular/router';
import {WineryPipesModule} from '../wineryPipes/wineryPipes.module';
import {BsDropdownModule} from 'ngx-bootstrap';
import {ReferencedDefinitionsComponent} from './referencedDefinitions/referencedDefinitions.component';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule,
        WineryModalModule,
        WineryPipesModule,
        WineryLoaderModule,
        WineryNotificationModule,
        WineryDuplicateValidatorModule,
        BsDropdownModule.forRoot(),
    ],
    exports: [
        WineryVersionComponent
    ],
    declarations: [
        WineryVersionComponent,
        ReferencedDefinitionsComponent
    ],
    providers: [],
})
export class WineryAddVersionModule {
}
