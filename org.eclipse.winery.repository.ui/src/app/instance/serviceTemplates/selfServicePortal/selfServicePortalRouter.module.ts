/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier - initial API and implementation
 */
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
