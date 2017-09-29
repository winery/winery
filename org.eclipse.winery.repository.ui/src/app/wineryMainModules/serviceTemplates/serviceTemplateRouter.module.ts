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
import { SectionComponent } from '../../section/section.component';
import { SectionResolver } from '../../section/section.resolver';
import { InstanceComponent } from '../../instance/instance.component';
import { InstanceResolver } from '../../instance/instance.resolver';
import { BoundaryDefinitionsComponent } from '../../instance/serviceTemplates/boundaryDefinitions/boundaryDefinitions.component';
import { boundaryDefinitionsRoutes } from '../../instance/serviceTemplates/boundaryDefinitions/boundaryDefinitions.module';
import { EditXMLComponent } from '../../instance/sharedComponents/editXML/editXML.component';
import { TopologyTemplateComponent } from '../../instance/serviceTemplates/topologyTemplate/topologyTemplate.component';
import { PlansComponent } from '../../instance/serviceTemplates/plans/plans.component';
import { SelfServicePortalComponent } from '../../instance/serviceTemplates/selfServicePortal/selfServicePortal.component';
import { selfServiceRoutes } from '../../instance/serviceTemplates/selfServicePortal/selfServicePortalRouter.module';
import { TagComponent } from '../../instance/serviceTemplates/tag/tag.component';
import { DocumentationComponent } from '../../instance/sharedComponents/documentation/documentation.component';
import { ToscaTypes } from '../../wineryInterfaces/enums';
import { WineryReadmeComponent } from '../../wineryReadmeModule/wineryReadme.component';
import { WineryLicenseComponent } from '../../wineryLicenseModule/wineryLicense.component';

const toscaType = ToscaTypes.ServiceTemplate;

const serviceTemplateRoutes: Routes = [
    { path: toscaType, component: SectionComponent, resolve: { resolveData: SectionResolver } },
    { path: toscaType + '/:namespace', component: SectionComponent, resolve: { resolveData: SectionResolver } },
    {
        path: toscaType + '/:namespace/:localName',
        component: InstanceComponent,
        resolve: { resolveData: InstanceResolver },
        children: [
            { path: 'readme', component: WineryReadmeComponent },
            { path: 'license', component: WineryLicenseComponent},
            { path: 'topologytemplate', component: TopologyTemplateComponent },
            { path: 'plans', component: PlansComponent },
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
            { path: 'tags', component: TagComponent },
            { path: 'documentation', component: DocumentationComponent },
            { path: 'xml', component: EditXMLComponent },
            { path: '', redirectTo: 'topologytemplate', pathMatch: 'full' }
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
