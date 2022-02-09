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
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { BsDropdownModule, ProgressbarModule, TooltipModule, TypeaheadModule } from 'ngx-bootstrap';
import { ToastrModule } from 'ngx-toastr';
import { NgReduxModule } from '@angular-redux/store';
import { RouterModule } from '@angular/router';
import { WineryModalModule } from '../../../../tosca-management/src/app/wineryModalModule/winery.modal.module';
import { LiveModelingSidebarComponent } from './live-modeling-sidebar.component';
import { LogsComponent } from './logs/logs.component';
import { ResizableModule } from 'angular-resizable-element';
import { AccordionModule } from 'ngx-bootstrap/accordion';
import { BuildplanParametersComponent } from './buildplan-parameters/buildplan-parameters.component';
import { BuildplanOutputsComponent } from './buildplan-outputs/buildplan-outputs.component';
import { NodeTemplateComponent } from './node-template/node-template.component';
import { EnableModalComponent } from './modals/enable-modal/enable-modal.component';
import { DisableModalComponent } from './modals/disable-modal/disable-modal.component';
import { ConfirmModalComponent } from './modals/confirm-modal/confirm-modal.component';
import { SettingsModalComponent } from './modals/settings-modal/settings-modal.component';
import { CsarInfoComponent } from './csar-info/csar-info.component';
import { InputParametersModalComponent } from './modals/input-parameters-modal/input-parameters-modal.component';
import { ReconfigureModalComponent } from './modals/reconfigure-modal/reconfigure-modal.component';
import { ProgressbarComponent } from './progressbar/progressbar.component';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        HttpClientModule,
        BrowserAnimationsModule,
        BsDropdownModule.forRoot(),
        ToastrModule.forRoot(),
        NgReduxModule,
        RouterModule,
        WineryModalModule,
        TypeaheadModule.forRoot(),
        TooltipModule.forRoot(),
        ResizableModule,
        AccordionModule.forRoot(),
        ProgressbarModule.forRoot(),
    ],
    declarations: [
        LiveModelingSidebarComponent,
        LogsComponent,
        CsarInfoComponent,
        BuildplanParametersComponent,
        BuildplanOutputsComponent,
        NodeTemplateComponent,
        EnableModalComponent,
        DisableModalComponent,
        ConfirmModalComponent,
        SettingsModalComponent,
        InputParametersModalComponent,
        ReconfigureModalComponent,
        ProgressbarComponent
    ],
    exports: [
        LiveModelingSidebarComponent
    ],
    entryComponents: [
        EnableModalComponent,
        DisableModalComponent,
        ConfirmModalComponent,
        SettingsModalComponent,
        InputParametersModalComponent,
        ReconfigureModalComponent
    ]
})
export class LiveModelingSidebarModule {

}
