/*******************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
import { SectionComponent } from '../../section/section.component';
import { SectionResolver } from '../../section/section.resolver';
import { InstanceComponent } from '../../instance/instance.component';
import { InstanceResolver } from '../../instance/instance.resolver';
import { ToscaTypes } from '../../model/enums';
import { InheritanceComponent } from '../../instance/sharedComponents/inheritance/inheritance.component';
import { PropertiesDefinitionComponent } from '../../instance/sharedComponents/propertiesDefinition/propertiesDefinition.component';
import { WineryReadmeComponent } from '../../wineryReadmeModule/wineryReadme.component';
import { YamlConstraintsComponent } from '../../instance/sharedComponents/yaml/constraints/yaml-constraints.component';

const toscaType = ToscaTypes.DataType;

// FIXME deal with documentation, notably also the description. Check other EntityType implementation related components for ideas
const dataTypeRoutes: Routes = [
    { path: toscaType, component: SectionComponent, resolve: { resolveData: SectionResolver } },
    { path: toscaType + '/:namespace', component: SectionComponent, resolve: { resolveData: SectionResolver } },
    {
        path: toscaType + '/:namespace/:localName',
        component: InstanceComponent,
        resolve: { resolveData: InstanceResolver },
        children: [
            { path: 'readme', component: WineryReadmeComponent },
            { path: 'inheritance', component: InheritanceComponent },
            { path: 'constraints', component: YamlConstraintsComponent },
            { path: 'properties', component: PropertiesDefinitionComponent },
            // { path: 'license', component: WineryLicenseComponent },
            // { path: 'documentation', component: DocumentationComponent },
            // { path: 'yaml', component: EditXMLComponent },
            // { path: 'templates', component: ImplementationsComponent },
            { path: '', redirectTo: 'readme', pathMatch: 'full' }
        ]
    }
];

@NgModule({
    imports: [
        RouterModule.forChild(dataTypeRoutes),
    ],
    exports: [
        RouterModule
    ],
    providers: [
        SectionResolver,
        InstanceResolver
    ],
})
export class DataTypeRouterModule {
}
