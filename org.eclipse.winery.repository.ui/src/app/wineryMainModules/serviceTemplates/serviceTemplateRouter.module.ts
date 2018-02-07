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
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {SectionComponent} from '../../section/section.component';
import {SectionResolver} from '../../section/section.resolver';
import {InstanceComponent} from '../../instance/instance.component';
import {InstanceResolver} from '../../instance/instance.resolver';
import {BoundaryDefinitionsComponent} from '../../instance/serviceTemplates/boundaryDefinitions/boundaryDefinitions.component';
import {boundaryDefinitionsRoutes} from '../../instance/serviceTemplates/boundaryDefinitions/boundaryDefinitions.module';
import {EditXMLComponent} from '../../instance/sharedComponents/editXML/editXML.component';
import {TopologyTemplateComponent} from '../../instance/serviceTemplates/topologyTemplate/topologyTemplate.component';
import {PlansComponent} from '../../instance/serviceTemplates/plans/plans.component';
import {SelfServicePortalComponent} from '../../instance/serviceTemplates/selfServicePortal/selfServicePortal.component';
import {selfServiceRoutes} from '../../instance/serviceTemplates/selfServicePortal/selfServicePortalRouter.module';
import {TagComponent} from '../../instance/serviceTemplates/tag/tag.component';
import {DocumentationComponent} from '../../instance/sharedComponents/documentation/documentation.component';
import {ToscaTypes} from '../../wineryInterfaces/enums';
import {WineryReadmeComponent} from '../../wineryReadmeModule/wineryReadme.component';
import {WineryLicenseComponent} from '../../wineryLicenseModule/wineryLicense.component';

const toscaType = ToscaTypes.ServiceTemplate;

const serviceTemplateRoutes: Routes = [
    {path: toscaType, component: SectionComponent, resolve: {resolveData: SectionResolver}},
    {path: toscaType + '/:namespace', component: SectionComponent, resolve: {resolveData: SectionResolver}},
    {
        path: toscaType + '/:namespace/:localName',
        component: InstanceComponent,
        resolve: {resolveData: InstanceResolver},
        children: [
            {path: 'readme', component: WineryReadmeComponent},
            {path: 'license', component: WineryLicenseComponent},
            {path: 'topologytemplate', component: TopologyTemplateComponent},
            {path: 'plans', component: PlansComponent},
            {
                path: 'selfserviceportal',
                component: SelfServicePortalComponent,
                // loadChildren:
                // 'app/instance/serviceTemplates/selfServicePortal/selfServicePortalRouter.module#SelfServiceRoutingModule'
                children: selfServiceRoutes
            },
            {
                path: 'boundarydefinitions',
                component: BoundaryDefinitionsComponent,
                // loadChildren:
                // 'app/instance/serviceTemplates/boundaryDefinitions/boundaryDefinitions.module#BoundaryDefinitionsModule'
                children: boundaryDefinitionsRoutes
            },
            {path: 'tags', component: TagComponent},
            {path: 'documentation', component: DocumentationComponent},
            {path: 'xml', component: EditXMLComponent},
            {path: '', redirectTo: 'readme', pathMatch: 'full'}
        ]
    }
];

@NgModule({
    imports: [
        RouterModule.forChild(serviceTemplateRoutes),
    ],
    exports: [
        RouterModule
    ],
    providers: [
        SectionResolver,
        InstanceResolver
    ],
})
export class ServiceTemplateRouterModule {
}
