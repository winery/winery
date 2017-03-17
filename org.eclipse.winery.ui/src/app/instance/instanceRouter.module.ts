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
import { RouterModule } from '@angular/router';
import { AppliesToComponent } from './appliesTo/appliesTo.component';
import { BoundaryDefinitionsComponent } from './boundaryDefinitions/boundaryDefinitions.component';
import { CapabilityDefinitionsComponent } from './capabilityDefinitions/capabilityDefinitions.component';
import { DeploymentArtifactsComponent } from './deploymentArtifacts/deploymentArtifacts.component';
import { DocumentationComponent } from './documentation/documentation.component';
import { EditXMLComponent } from './editXML/editXML.component';
import { FilesComponent } from './files/files.component';
import { ImplementationArtifactsComponent } from './implementationArtifacts/implementationArtifacts.component';
import { ImplementationsComponent } from './implementations/implementations.component';
import { InheritanceComponent } from './inheritance/inheritance.component';
import { InstanceComponent } from './instance.component';
import { InstanceResolver } from './instance.resolver';
import { InstanceStatesComponent } from './instanceStates/instanceStates.component';
import { InterfacesComponent } from './interfaces/interfaces.component';
import { LanguageComponent } from './language/language.component';
import { PlansComponent } from './plans/plans.component';
import { PropertiesComponent } from './properties/properties.component';
import { PropertiesDefinitionComponent } from './propertiesDefinition/propertiesDefinition.component';
import { RequiredCapabilityTypeComponent } from './requiredCapabilityType/requiredCapabilityType.component';
import { RequirementDefinitionsComponent } from './requirementDefinitions/requirementDefinitions.component';
import { SelfservicePortalComponent } from './selfservicePortal/selfservicePortal.component';
import { TagsComponent } from './tags/tags.component';
import { TopologyTemplateComponent } from './topologyTemplate/topologyTemplate.component';
import { ValidSourcesAndTargetsComponent } from './validSourcesAndTargets/validSourcesAndTargets.component';
import { VisualAppearanceComponent } from './visualAppearance/visualAppearance.component';

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
