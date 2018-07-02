/********************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
import { NgModule } from '@angular/core';

import { WineryAddComponent } from './addComponent.component';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { WineryModalModule } from '../wineryModalModule/winery.modal.module';
import { WineryNotificationModule } from '../wineryNotificationModule/wineryNotification.module';
import { WineryNamespaceSelectorModule } from '../wineryNamespaceSelector/wineryNamespaceSelector.module';
import { WineryDuplicateValidatorModule } from '../wineryValidators/wineryDuplicateValidator.module';
import { RouterModule } from '@angular/router';
import { WineryLoaderModule } from '../wineryLoader/wineryLoader.module';
import { SelectModule } from 'ng2-select';
import { AlertModule, CollapseModule, TooltipModule } from 'ngx-bootstrap';

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        RouterModule,
        SelectModule,
        TooltipModule,
        AlertModule.forRoot(),
        CollapseModule.forRoot(),
        WineryModalModule,
        WineryLoaderModule,
        WineryNotificationModule,
        WineryNamespaceSelectorModule,
        WineryDuplicateValidatorModule,
    ],
    exports: [WineryAddComponent],
    declarations: [WineryAddComponent],
})
export class WineryAddModule {
}
