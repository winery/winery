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
import { SectionResolver } from '../../section/section.resolver';
import { InstanceComponent } from '../../instance/instance.component';
import { InstanceResolver } from '../../instance/instance.resolver';
import { ToscaTypes } from '../../wineryInterfaces/enums';
import { RepositoryComponent } from '../../instance/admin/repository/repository.component';
import { TypeWithShortNameComponent } from '../../instance/admin/typesWithShortName/typeWithShortName.component';
import { NamespacesComponent } from '../../instance/admin/namespaces/namespaces.component';
import { LoggerComponent } from '../../instance/admin/logger/logger.component';

const toscaType = ToscaTypes.Admin;

const adminRoutes: Routes = [
    {
        path: toscaType,
        component: InstanceComponent,
        children: [
            { path: 'namespaces', component: NamespacesComponent },
            { path: 'repository', component: RepositoryComponent },
            { path: 'planlanguages', component: TypeWithShortNameComponent },
            { path: 'plantypes', component: TypeWithShortNameComponent },
            { path: 'constrainttypes', component: TypeWithShortNameComponent },
            { path: 'log', component: LoggerComponent },
            { path: '', redirectTo: 'namespaces', pathMatch: 'full' }
        ]
    },
];

@NgModule({
    imports: [
        RouterModule.forChild(adminRoutes),
    ],
    exports: [
        RouterModule
    ],
    providers: [
        SectionResolver,
        InstanceResolver
    ],
})
export class AdminRouterModule {
}
