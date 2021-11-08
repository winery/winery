/*******************************************************************************
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
 *******************************************************************************/
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';

import { AppComponent } from './app.component';

import { WmContainerComponent } from './components/container/container.component';
import { WmNodeComponent } from './components/node/node.component';
import { WmNodeTemplateComponent } from './components/nodetemplate/node-template.component';
import { WmParameterComponent } from './components/parameter/parameter.component';
import { WmPropertiesComponent } from './components/property/properties.component';
import { WmToolbarComponent } from './components/toolbar/toolbar.component';

import { BroadcastService } from './services/broadcast.service';
import { JsPlumbService } from './services/jsplumb.service';
import { ModelService } from './services/model.service';
import { NodeService } from './services/node.service';
import { WineryService } from './services/winery.service';

import { SharedModule } from './shared/shared.module';
import { HttpService } from './util/http.service';
import { HttpClientModule } from '@angular/common/http';
import { SelectModule } from 'ng2-select';
import { WmInstanceTypeComponent } from './components/instanceType/instanceType.component';

@NgModule({
    declarations: [
        AppComponent,
        WmContainerComponent,
        WmNodeComponent,
        WmNodeTemplateComponent,
        WmParameterComponent,
        WmPropertiesComponent,
        WmToolbarComponent,
        WmInstanceTypeComponent
    ],
    providers: [
        BroadcastService,
        HttpService,
        JsPlumbService,
        ModelService,
        NodeService,
        WineryService,
    ],
    imports: [
        BrowserModule,
        RouterModule.forRoot([]),
        SharedModule,
        HttpClientModule,
        SelectModule,
    ],
    bootstrap: [
        AppComponent,
    ],
})
export class AppModule {

}
