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
 *     Lukas Balzer - added boundary definitions component
 */
import { NgModule } from '@angular/core';
import { InstanceComponent } from './instance.component';
import { EditXMLComponent } from './sharedComponents/editXML/editXML.component';
import { VisualAppearanceComponent } from './sharedComponents/visualAppearance/visualAppearance.component';
import { DocumentationComponent } from './sharedComponents/documentation/documentation.component';
import { InheritanceComponent } from './sharedComponents/inheritance/inheritance.component';
import { InterfacesComponent } from './sharedComponents/interfaces/interfaces.component';
import { ImplementationsComponent } from './sharedComponents/implementations/implementations.component';
import { RequirementDefinitionsComponent } from './nodeTypes/requirementDefinitions/requirementDefinitions.component';
import { InstanceStatesComponent } from './sharedComponents/instanceStates/instanceStates.component';
import { CapabilityDefinitionsComponent } from './nodeTypes/capabilityDefinitions/capabilityDefinitions.component';
import { PropertiesDefinitionComponent } from './sharedComponents/propertiesDefinition/propertiesDefinition.component';
import { InstanceResolver } from './instance.resolver';
import { AppliesToComponent } from './policyTypes/appliesTo/appliesTo.component';
import { LanguageComponent } from './policyTypes/language/language.component';
import { TopologyTemplateComponent } from './serviceTemplates/topologyTemplate/topologyTemplate.component';
import { PlansComponent } from './serviceTemplates/plans/plans.component';
import { BoundaryDefinitionsComponent } from './serviceTemplates/boundaryDefinitions/boundaryDefinitions.component';
import { boundaryDefinitionsRoutes } from './serviceTemplates/boundaryDefinitions/boundaryDefinitions.module';
import { ValidSourcesAndTargetsComponent } from './relationshipTypes/validSourcesAndTargets/validSourcesAndTargets.component';
import { FilesComponent } from './artifactTemplates/filesTag/files.component';
import { PropertiesComponent } from './sharedComponents/properties/properties.component';
import { RequiredCapabilityTypeComponent } from './requirementTypes/requiredCapabilityType/requiredCapabilityType.component';
import { ImplementationArtifactsComponent } from './sharedComponents/implementationArtifacts/implementationArtifacts.component';
import { DeploymentArtifactsComponent } from './nodeTypeImplementations/deploymentArtifacts/deploymentArtifacts.component';
import { LoggerComponent } from './admin/logger/logger.component';
import { PlanLanguagesComponent } from './admin/planLanguages/planLanguages.component';
import { NamespacesComponent } from './admin/namespaces/namespaces.component';
import { RepositoryComponent } from './admin/repository/repository.component';
import { ConstraintTypesComponent } from './admin/contraintTypes/constraintTypes.component';
import { PlanTypesComponent } from './admin/planTypes/planTypes.component';
import { TagComponent } from './serviceTemplates/tag/tag.component';
import { RouterModule } from '@angular/router';
import { selfServiceRoutes } from './serviceTemplates/selfServicePortal/selfServicePortalRouter.module';
import { SelfServicePortalComponent } from './serviceTemplates/selfServicePortal/selfServicePortal.component';

const instanceRoutes = [
    {
        path: 'admin',
        component: InstanceComponent,
        resolve: { resolveData: InstanceResolver },
        children: [
            { path: 'namespaces', component: NamespacesComponent },
            { path: 'repository', component: RepositoryComponent },
            { path: 'planlanguages', component: PlanLanguagesComponent },
            { path: 'plantypes', component: PlanTypesComponent },
            { path: 'constrainttypes', component: ConstraintTypesComponent },
            { path: 'log', component: LoggerComponent },
            { path: '', redirectTo: 'namespaces', pathMatch: 'full'}
        ]
    },
    {
        path: ':section/:namespace/:instanceId',
        component: InstanceComponent,
        resolve: { resolveData: InstanceResolver },
        children: [
            { path: 'appliesto', component: AppliesToComponent },
            {
                path: 'boundarydefinitions',
                component: BoundaryDefinitionsComponent,
                children: boundaryDefinitionsRoutes
            },
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
            { path: 'selfserviceportal', component: SelfServicePortalComponent, children: selfServiceRoutes },
            { path: 'sourceinterfaces', component: InterfacesComponent },
            { path: 'targetinterfaces', component: InterfacesComponent },
            { path: 'tags', component: TagComponent},
            { path: 'topologytemplate', component: TopologyTemplateComponent },
            { path: 'validsourcesandtargets', component: ValidSourcesAndTargetsComponent },
            { path: 'visualappearance', component: VisualAppearanceComponent },
            { path: 'xml', component: EditXMLComponent }
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
