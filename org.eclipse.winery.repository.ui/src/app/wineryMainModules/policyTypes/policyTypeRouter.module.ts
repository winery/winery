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
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SectionComponent } from '../../section/section.component';
import { SectionResolver } from '../../section/section.resolver';
import { InstanceComponent } from '../../instance/instance.component';
import { InstanceResolver } from '../../instance/instance.resolver';
import { EditXMLComponent } from '../../instance/sharedComponents/editXML/editXML.component';
import { DocumentationComponent } from '../../instance/sharedComponents/documentation/documentation.component';
import { ToscaTypes } from '../../wineryInterfaces/enums';
import { InheritanceComponent } from '../../instance/sharedComponents/inheritance/inheritance.component';
import { PropertiesDefinitionComponent } from '../../instance/sharedComponents/propertiesDefinition/propertiesDefinition.component';
import { LanguageComponent } from '../../instance/policyTypes/language/language.component';
import { AppliesToComponent } from '../../instance/policyTypes/appliesTo/appliesTo.component';

const toscaType = ToscaTypes.PolicyType;

const policyTypeRoutes: Routes = [
    { path: toscaType, component: SectionComponent, resolve: { resolveData: SectionResolver } },
    { path: toscaType + '/:namespace', component: SectionComponent, resolve: { resolveData: SectionResolver } },
    {
        path: toscaType + '/:namespace/:localName',
        component: InstanceComponent,
        resolve: { resolveData: InstanceResolver },
        children: [
            { path: 'language', component: LanguageComponent },
            { path: 'appliesto', component: AppliesToComponent },
            { path: 'propertiesdefinition', component: PropertiesDefinitionComponent },
            { path: 'inheritance', component: InheritanceComponent },
            { path: 'documentation', component: DocumentationComponent },
            { path: 'xml', component: EditXMLComponent }
        ]
    }
];

@NgModule({
    imports: [
        RouterModule.forChild(policyTypeRoutes),
    ],
    exports: [
        RouterModule
    ],
    providers: [
        SectionResolver,
        InstanceResolver
    ],
})
export class PolicyTypeRouterModule {
}
