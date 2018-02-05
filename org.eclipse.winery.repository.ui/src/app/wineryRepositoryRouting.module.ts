/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
import { NotFoundComponent } from './404/notFound.component';
import { OtherComponent } from './other/other.component';
import { SectionResolver } from './section/section.resolver';

const appRoutes: Routes = [
    { path: 'other', component: OtherComponent },
    { path: 'notfound', component: NotFoundComponent },
    { path: '', redirectTo: '/servicetemplates', pathMatch: 'full' },
    { path: '**', component: NotFoundComponent },
];

@NgModule({
    imports: [
        RouterModule.forRoot(appRoutes,
            {
                useHash: true,
                // enableTracing: !environment.production // uncomment if not needed during development
            }),
    ],
    exports: [
        RouterModule
    ],
    providers: [
        SectionResolver
    ]
})
export class WineryRepositoryRoutingModule {
}
