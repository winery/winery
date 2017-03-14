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
 *     Niko Stadelmaier - add admin component
 */

import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { BrowserModule } from '@angular/platform-browser';
import { InstanceComponent } from './instance.component';
import { EditXMLComponent } from './editXML/editXML.component';
import { VisualAppearanceComponent } from './visualAppearance/visualAppearance.component';
import { DocumentationComponent } from './documentation/documentation.component';
import { InheritanceComponent } from './inheritance/inheritance.component';
import { InterfacesComponent } from './interfaces/interfaces.component';
import { ImplementationsComponent } from './implementations/implementations.component';
import { RequirementDefinitionsComponent } from './requirementDefinitions/requirementDefinitions.component';
import { InstanceStatesComponent } from './instanceStates/instanceStates.component';
import { CapabilityDefinitionsComponent } from './capabilityDefinitions/capabilityDefinitions.component';
import { PropertiesDefinitionComponent } from './propertiesDefinition/propertiesDefinition.component';
import { InstanceResolver } from './instance.resolver';
import { PropertiesDefinitionModule } from './propertiesDefinition/propertiesDefinition.module';
import { InstanceStatesModule } from './instanceStates/instanceStates.module';
import { LoggerComponent } from "./logger/logger.component";
import { PlanLanguagesComponent } from "./planLanguages/planLanguages.component";
import { NamespacesComponent } from "./namespaces/namespaces.component";
import { RepositoryComponent } from "./repository/repository.component";
import { ConstraintTypesComponent } from "./contraintTypes/constraintTypes.component";
import { PlanTypesComponent } from "./planTypes/planTypes.component";


const instanceRoutes: Routes = [

    {
        path: 'admin',
        component: InstanceComponent,
        resolve: {resolveData: InstanceResolver},
        children: [
            { path: 'namespaces', component: NamespacesComponent },
            { path: 'repository', component: RepositoryComponent },
            { path: 'planlanguages', component: PlanLanguagesComponent },
            { path: 'plantypes', component: PlanTypesComponent },
            { path: 'contrainttypes', component: ConstraintTypesComponent },
            { path: 'log', component: LoggerComponent },
        ]
    },
    {
        path: ':section/:namespace/:instanceId',
        component: InstanceComponent,
        resolve: { resolveData: InstanceResolver },
        children: [
            { path: 'capabilitydefinitions', component: CapabilityDefinitionsComponent },
            { path: 'documentation', component: DocumentationComponent },
            { path: 'implementations', component: ImplementationsComponent },
            { path: 'inheritance', component: InheritanceComponent },
            { path: 'instancestates', component: InstanceStatesComponent },
            { path: 'interfaces', component: InterfacesComponent },
            { path: 'propertiesdefinition', component: PropertiesDefinitionComponent },
            { path: 'requirementdefinitions', component: RequirementDefinitionsComponent },
            { path: 'sourceinterfaces', component: InterfacesComponent },
            { path: 'targetinterfaces', component: InterfacesComponent },
            { path: 'visualappearance', component: VisualAppearanceComponent},
            { path: 'xml', component: EditXMLComponent },
        ]
    }
];

@NgModule({
    imports: [
        BrowserModule,
        PropertiesDefinitionModule,
        InstanceStatesModule,
        RouterModule.forChild(instanceRoutes)
    ],
    exports: [
        RouterModule
    ],
    providers: [
        InstanceResolver
    ],
})
export class InstanceRouterModule {
}
