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
 *     Niko Stadelmaier - add admin component, move routing to instance module
 */
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NotFoundComponent } from './404/notFound.component';
import { OtherComponent } from './other/other.component';
import { SectionComponent } from './section/section.component';
import { SectionResolver } from './section/section.resolver';

const appRoutes: Routes = [
    {path: 'other', component: OtherComponent},
    {path: 'notfound', component: NotFoundComponent},
    {path: ':section/:namespace', component: SectionComponent, resolve: {resolveData: SectionResolver}},
    {path: ':section', component: SectionComponent, resolve: {resolveData: SectionResolver}},
    {path: '', redirectTo: '/servicetemplates', pathMatch: 'full'},
    {path: '**', component: NotFoundComponent},
];

@NgModule({
    imports: [
        RouterModule.forRoot(appRoutes, {useHash: true}),
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
