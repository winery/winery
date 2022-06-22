/*******************************************************************************
 * Copyright (c) 2021-2022 Contributors to the Eclipse Foundation
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
import { RouterModule, Routes } from '@angular/router';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { ResearchObjectPublicationComponent } from './researchObjectPublication.component';
import { ResearchObjectMetaDataComponent } from './researchObjectMetaData.component';
import { ResearchObjectFilesComponent } from './researchObjectFiles.component';

export const researchObjectRoutes: Routes = [
    {
        path: 'publication',
        component: ResearchObjectPublicationComponent
    },
    {
        path: 'metadata',
        component: ResearchObjectMetaDataComponent
    },
    {
        path: 'files',
        component: ResearchObjectFilesComponent
    },
    {
        path: '',
        pathMatch: 'full',
        redirectTo: 'metadata'
    }
];

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        HttpModule,
    ],
    exports: [RouterModule],
    declarations: [],
    providers: [],
})
export class ResearchObjectRoutingModule {
}
