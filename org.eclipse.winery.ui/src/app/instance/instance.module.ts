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
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { BrowserModule } from '@angular/platform-browser';
import { SelectModule } from 'ng2-select';
import { LoaderModule } from '../loader/loader.module';
import { RemoveWhiteSpacesPipe } from '../pipes/removeWhiteSpaces.pipe';
import { QNameSelectorComponent } from '../qNameSelector/qNameSelector.component';
import { WineryModalModule } from '../wineryModalModule/winery.modal.module';
import { WineryTableModule } from '../wineryTableModule/wineryTable.module';
import { AppliesToComponent } from './appliesTo/appliesTo.component';
import { BoundaryDefinitionsComponent } from './boundaryDefinitions/boundaryDefinitions.component';
import { CapabilityDefinitionsComponent } from './capabilityDefinitions/capabilityDefinitions.component';
import { DeploymentArtifactsComponent } from './deploymentArtifacts/deploymentArtifacts.component';
import { DocumentationComponent } from './documentation/documentation.component';
import { EditXMLComponent } from './editXML/editXML.component';
import { FilesComponent } from './files/files.component';
import { ImplementationArtifactsComponent } from './implementationArtifacts/implementationArtifacts.component';
import { ImplementationsModule } from './implementations/implementations.module';
import { InheritanceComponent } from './inheritance/inheritance.component';
import { InstanceComponent } from './instance.component';
import { InstanceHeaderComponent } from './instanceHeader/instanceHeader.component';
import { InstanceRouterModule } from './instanceRouter.module';
import { InstanceStatesComponent } from './instanceStates/instanceStates.component';
import { InterfacesModule } from './interfaces/interfaces.module';
import { LanguageComponent } from './language/language.component';
import { PlansComponent } from './plans/plans.component';
import { PropertiesComponent } from './properties/properties.component';
import { PropertiesDefinitionModule } from './propertiesDefinition/propertiesDefinition.module';
import { RequiredCapabilityTypeComponent } from './requiredCapabilityType/requiredCapabilityType.component';
import { RequirementDefinitionsComponent } from './requirementDefinitions/requirementDefinitions.component';
import { SelfservicePortalComponent } from './selfservicePortal/selfservicePortal.component';
import { TagsComponent } from './tags/tags.component';
import { TopologyTemplateComponent } from './topologyTemplate/topologyTemplate.component';
import { ValidSourcesAndTargetsComponent } from './validSourcesAndTargets/validSourcesAndTargets.component';
import { VisualAppearanceComponent } from './visualAppearance/visualAppearance.component';

@NgModule({
    imports: [
        HttpModule,
        SelectModule,
        BrowserModule,
        FormsModule,
        LoaderModule,
        PropertiesDefinitionModule,
        InstanceRouterModule,
        WineryModalModule,
        InterfacesModule,
        ImplementationsModule,
        WineryTableModule,
    ],
    exports: [],
    declarations: [
        AppliesToComponent,
        CapabilityDefinitionsComponent,
        DocumentationComponent,
        EditXMLComponent,
        InheritanceComponent,
        InstanceComponent,
        InstanceHeaderComponent,
        LanguageComponent,
        RemoveWhiteSpacesPipe,
        RequirementDefinitionsComponent,
        VisualAppearanceComponent,
        QNameSelectorComponent,
        TopologyTemplateComponent,
        PlansComponent,
        SelfservicePortalComponent,
        BoundaryDefinitionsComponent,
        TagsComponent,
        ValidSourcesAndTargetsComponent,
        FilesComponent,
        PropertiesComponent,
        RequiredCapabilityTypeComponent,
        ImplementationArtifactsComponent,
        DeploymentArtifactsComponent,
        InstanceStatesComponent,
    ],
    providers: [],
})
export class InstanceModule {
}
