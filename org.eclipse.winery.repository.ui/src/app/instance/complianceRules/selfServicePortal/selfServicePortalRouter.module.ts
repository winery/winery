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
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { SelfServiceDescriptionComponent } from './selfServicePortalDescription.component';
import { SelfServicePortalImagesComponent } from './selfServicePortalImages.component';
import { SelfServicePortalOptionsComponent } from './selfServicePortalOptions.component';
import { EditXMLComponent } from '../../sharedComponents/editXML/editXML.component';

export const selfServiceRoutes = [
    {
        path: 'description',
        component: SelfServiceDescriptionComponent
    },
    {
        path: 'images',
        component: SelfServicePortalImagesComponent
    },
    {
        path: 'options',
        component: SelfServicePortalOptionsComponent
    },
    {
        path: 'xml',
        component: EditXMLComponent
    },
    {
        path: '',
        pathMatch: 'full',
        redirectTo: 'description'
    }
];

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        HttpModule,
        RouterModule.forChild(selfServiceRoutes)
    ],
    exports: [RouterModule],
    declarations: [],
    providers: [],
})
export class SelfServiceRoutingModule {
}
