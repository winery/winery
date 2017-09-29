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
import { EditXMLComponent } from '../../instance/sharedComponents/editXML/editXML.component';
import { DocumentationComponent } from '../../instance/sharedComponents/documentation/documentation.component';
import { ToscaTypes } from '../../wineryInterfaces/enums';
import { PropertiesComponent } from '../../instance/sharedComponents/properties/properties.component';
import { FilesComponent } from '../../instance/artifactTemplates/filesTag/files.component';
import { ArtifactSourceComponent } from '../../instance/artifactTemplates/artifactSource/artifactSource.component';
import { WineryReadmeComponent } from '../../wineryReadmeModule/wineryReadme.component';
import { WineryLicenseComponent } from '../../wineryLicenseModule/wineryLicense.component';

const toscaType = ToscaTypes.ArtifactTemplate;

const artifactTemplateRoutes: Routes = [
    { path: toscaType, component: SectionComponent, resolve: { resolveData: SectionResolver } },
    { path: toscaType + '/:namespace', component: SectionComponent, resolve: { resolveData: SectionResolver } },
    {
        path: toscaType + '/:namespace/:localName',
        component: InstanceComponent,
        resolve: { resolveData: InstanceResolver },
        children: [
            { path: 'readme', component: WineryReadmeComponent },
            { path: 'license', component: WineryLicenseComponent},
            { path: 'files', component: FilesComponent },
            { path: 'source', component: ArtifactSourceComponent },
            { path: 'properties', component: PropertiesComponent },
            { path: 'documentation', component: DocumentationComponent },
            { path: 'xml', component: EditXMLComponent },
            { path: '', redirectTo: 'files', pathMatch: 'full'}
        ]
    }
];

@NgModule({
    imports: [
        RouterModule.forChild(artifactTemplateRoutes),
    ],
    exports: [
        RouterModule
    ],
    providers: [
        SectionResolver,
        InstanceResolver
    ],
})
export class ArtifactTemplateRouterModule {
}
