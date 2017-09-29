/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ToscaTypes } from '../../wineryInterfaces/enums';
import { SectionComponent } from '../../section/section.component';
import { SectionResolver } from '../../section/section.resolver';
import { InstanceResolver } from '../../instance/instance.resolver';
import { InstanceComponent } from '../../instance/instance.component';
import { ListDefinedTypesAndElementsComponent } from '../../instance/imports/listDefinedTypesAndElements.component';

const toscaType = ToscaTypes.Imports;

const importRoutes: Routes = [
    { path: toscaType, component: SectionComponent, resolve: { resolveData: SectionResolver } },
    // namespace is only used for reuse of the {@link SectionComponent} instead of importType.
    { path: toscaType + '/:xsdSchemaType', component: SectionComponent, resolve: { resolveData: SectionResolver } },
    { path: toscaType + '/:xsdSchemaType/:namespace', component: SectionComponent, resolve: { resolveData: SectionResolver } },
    {
        path: toscaType + '/:xsdSchemaType/:namespace/:localName',
        component: InstanceComponent,
        resolve: { resolveData: InstanceResolver },
        children: [
            { path: 'alldeclaredelementslocalnames', component: ListDefinedTypesAndElementsComponent },
            { path: 'alldefinedtypeslocalnames', component: ListDefinedTypesAndElementsComponent },
            { path: '', redirectTo: 'alldeclaredelementslocalnames', pathMatch: 'full' }
        ]
    }
];

@NgModule({
    imports: [
        RouterModule.forChild(importRoutes)
    ],
    exports: [RouterModule],
})
export class ImportRouterModule {
}
