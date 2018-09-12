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
 *******************************************************************************/
import { RouterModule, Routes } from '@angular/router';
import { SectionResolver } from '../../section/section.resolver';
import { InstanceResolver } from '../../instance/instance.resolver';
import { ToscaTypes } from '../../model/enums';
import { SectionComponent } from '../../section/section.component';
import { InstanceComponent } from '../../instance/instance.component';
import { WineryReadmeComponent } from '../../wineryReadmeModule/wineryReadme.component';
import { WineryLicenseComponent } from '../../wineryLicenseModule/wineryLicense.component';
import { EditXMLComponent } from '../../instance/sharedComponents/editXML/editXML.component';
import { NgModule } from '@angular/core';
import { RelationMappingsComponent } from '../../instance/patternRefinementModels/relationshipMappings/relationMappings.component';
import { TopologyTemplateComponent } from '../../instance/sharedComponents/topologyTemplate/topologyTemplate.component';

const toscaType = ToscaTypes.PatternRefinementModel;

const patternRefinementRoutes: Routes = [
    { path: toscaType, component: SectionComponent, resolve: { resolveData: SectionResolver } },
    { path: toscaType + '/:namespace', component: SectionComponent, resolve: { resolveData: SectionResolver } },
    {
        path: toscaType + '/:namespace/:localName',
        component: InstanceComponent,
        resolve: { resolveData: InstanceResolver },
        children: [
            { path: 'readme', component: WineryReadmeComponent },
            { path: 'license', component: WineryLicenseComponent },
            { path: 'detector', component: TopologyTemplateComponent },
            { path: 'refinementstructure', component: TopologyTemplateComponent },
            { path: 'relationmappings', component: RelationMappingsComponent },
            { path: 'xml', component: EditXMLComponent },
            { path: '', redirectTo: 'readme', pathMatch: 'full' }
        ]
    }
];

@NgModule({
    imports: [
        RouterModule.forChild(patternRefinementRoutes),
    ],
    exports: [
        RouterModule,
    ],
    providers: [
        SectionResolver,
        InstanceResolver
    ]
})
export class PatternRefinementModelRouterModule {
}
