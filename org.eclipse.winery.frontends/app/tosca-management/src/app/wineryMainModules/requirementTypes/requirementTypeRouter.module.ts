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
import {EditXMLComponent} from '../../instance/sharedComponents/editXML/editXML.component';
import {DocumentationComponent} from '../../instance/sharedComponents/documentation/documentation.component';
import {ToscaTypes} from '../../model/enums';
import {InheritanceComponent} from '../../instance/sharedComponents/inheritance/inheritance.component';
import {PropertiesDefinitionComponent} from '../../instance/sharedComponents/propertiesDefinition/propertiesDefinition.component';
import {RequiredCapabilityTypeComponent} from '../../instance/requirementTypes/requiredCapabilityType/requiredCapabilityType.component';
import {WineryReadmeComponent} from '../../wineryReadmeModule/wineryReadme.component';
import {WineryLicenseComponent} from '../../wineryLicenseModule/wineryLicense.component';

const toscaType = ToscaTypes.RequirementType;

const requirementTypeRoutes: Routes = [
    {path: toscaType, component: SectionComponent, resolve: {resolveData: SectionResolver}},
    {path: toscaType + '/:namespace', component: SectionComponent, resolve: {resolveData: SectionResolver}},
    {
        path: toscaType + '/:namespace/:localName',
        component: InstanceComponent,
        resolve: {resolveData: InstanceResolver},
        children: [
            {path: 'readme', component: WineryReadmeComponent},
            {path: 'license', component: WineryLicenseComponent},
            {path: 'requiredcapabilitytype', component: RequiredCapabilityTypeComponent},
            {path: 'propertiesdefinition', component: PropertiesDefinitionComponent},
            {path: 'inheritance', component: InheritanceComponent},
            {path: 'documentation', component: DocumentationComponent},
            {path: 'xml', component: EditXMLComponent},
            {path: '', redirectTo: 'readme', pathMatch: 'full'}
        ]
    }
];

@NgModule({
    imports: [
        RouterModule.forChild(requirementTypeRoutes),
    ],
    exports: [
        RouterModule
    ],
    providers: [
        SectionResolver,
        InstanceResolver
    ],
})
export class RequirementTypeRouterModule {
}
