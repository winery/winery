/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
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
