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
import { Routes, RouterModule } from '@angular/router';
import { BrowserModule } from '@angular/platform-browser';
import { InstanceComponent } from './instance.component';
import { EditXMLComponent } from './editXML/editXML.component';
import { VisualAppearanceComponent } from './visualAppearance/visualAppearance.component';
import { DocumentationComponent } from './documentation/documentation.component';
import { InheritanceComponent } from './inheritance/inheritance.component';
import { InterfacesComponent } from './interfaces/interfaces.component';
import { ImplementationsComponent } from './implementations/implementations.component';
import { ImplementationsModule } from './implementations/implementation.module';
import { RequirementDefinitionsComponent } from './requirementDefinitions/requirementDefinitions.component';
import { InstanceStatesComponent } from './instanceStates/instanceStates.component';
import { CapabilityDefinitionsComponent } from './capabilityDefinitions/capabilityDefinitions.component';
import { PropertiesDefinitionComponent } from './propertiesDefinition/propertiesDefinition.component';
import { InstanceResolver } from './instance.resolver';
import { PropertiesDefinitionModule } from './propertiesDefinition/propertiesDefinition.module';
import { InstanceStatesModule } from './instanceStates/instanceStates.module';
import { AppliesToComponent } from './appliesTo/appliesTo.component';
import { LanguageComponent } from './language/language.component';
import { TopologyTemplateComponent } from './topologyTemplate/topologyTemplate.component';
import { PlansComponent } from './plans/plans.component';
import { SelfservicePortalComponent } from './selfservicePortal/selfservicePortal.component';
import { BoundaryDefinitionsComponent } from './boundaryDefinitions/boundaryDefinitions.component';
import { TagsComponent } from './tags/tags.component';
import { ValidSourcesAndTargetsComponent } from './validSourcesAndTargets/validSourcesAndTargets.component';
import { FilesComponent } from './files/files.component';
import { PropertiesComponent } from './properties/properties.component';
import { RequiredCapabilityTypeComponent } from './requiredCapabilityType/requiredCapabilityType.component';
import { ImplementationArtifactsComponent } from './implementationArtifacts/implementationArtifacts.component';
import { DeploymentArtifactsComponent } from './deploymentArtifacts/deploymentArtifacts.component';

const instanceRoutes = [
    {
        path: ':section/:namespace/:instanceId',
        component: InstanceComponent,
        resolve: { resolveData: InstanceResolver },
        children: [
            { path: 'appliesto', component: AppliesToComponent },
            { path: 'boundarydefinitions', component: BoundaryDefinitionsComponent },
            { path: 'capabilitydefinitions', component: CapabilityDefinitionsComponent },
            { path: 'deploymentartifacts', component: DeploymentArtifactsComponent },
            { path: 'documentation', component: DocumentationComponent },
            { path: 'files', component: FilesComponent },
            { path: 'implementationartifacts', component: ImplementationArtifactsComponent },
            { path: 'implementations', component: ImplementationsComponent },
            { path: 'inheritance', component: InheritanceComponent },
            { path: 'instancestates', component: InstanceStatesComponent },
            { path: 'interfaces', component: InterfacesComponent },
            { path: 'language', component: LanguageComponent },
            { path: 'plans', component: PlansComponent },
            { path: 'properties', component: PropertiesComponent },
            { path: 'propertiesdefinition', component: PropertiesDefinitionComponent },
            { path: 'requiredcapabilitytype', component: RequiredCapabilityTypeComponent },
            { path: 'requirementdefinitions', component: RequirementDefinitionsComponent },
            { path: 'selfserviceportal', component: SelfservicePortalComponent },
            { path: 'sourceinterfaces', component: InterfacesComponent },
            { path: 'tags', component: TagsComponent },
            { path: 'targetinterfaces', component: InterfacesComponent },
            { path: 'topologytemplate', component: TopologyTemplateComponent },
            { path: 'validsourcesandtargets', component: ValidSourcesAndTargetsComponent },
            { path: 'visualappearance', component: VisualAppearanceComponent },
            { path: 'xml', component: EditXMLComponent },
        ]
    }
];

@NgModule({
    imports: [
        BrowserModule,
        PropertiesDefinitionModule,
        ImplementationsModule,
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
