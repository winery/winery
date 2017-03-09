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

import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { InstanceComponent } from './instance/instance.component';
import { OtherComponent } from './other/other.component';
import { SectionComponent } from './section/section.component';
import { SectionResolver } from './section/section.resolver';
import { NotFoundComponent } from './404/notFound.component';
import { NamespaceResolver } from './namespace/namespace.resolver';
import { HttpModule } from '@angular/http';
import { FormsModule } from '@angular/forms';

const appRoutes: Routes = [
    { path: 'admin', component: InstanceComponent },
    { path: 'other', component: OtherComponent },
    { path: 'notfound', component: NotFoundComponent },
    { path: ':section', component: SectionComponent, resolve: { resolveData: SectionResolver } },
    { path: ':section/:namespace', component: SectionComponent, resolve: { resolveData: NamespaceResolver } },
    { path: '', redirectTo: '/servicetemplates', pathMatch: 'full' },
    { path: '**', component: NotFoundComponent },
    // TODO: add namespaces, other routes available in other, etc...
];

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        HttpModule,
        RouterModule.forRoot(appRoutes),
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
