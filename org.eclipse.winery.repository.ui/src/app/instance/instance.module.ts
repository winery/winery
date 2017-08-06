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
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { BrowserModule } from '@angular/platform-browser';
import { SelectModule } from 'ng2-select';
import { InstanceComponent } from './instance.component';
import { InstanceHeaderComponent } from './instanceHeader/instanceHeader.component';
import { InstanceRouterModule } from './instanceRouter.module';
import { DocumentationComponent } from './sharedComponents/documentation/documentation.component';
import { InheritanceComponent } from './sharedComponents/inheritance/inheritance.component';
import { WineryLoaderModule } from '../wineryLoader/wineryLoader.module';
import { WineryNamespaceSelectorModule } from '../wineryNamespaceSelector/wineryNamespaceSelector.module';
import { RemoveWhiteSpacesPipe } from '../wineryPipes/removeWhiteSpaces.pipe';
import { WineryDuplicateValidatorModule } from '../wineryValidators/wineryDuplicateValidator.module';
import { WineryModalModule } from '../wineryModalModule/winery.modal.module';
import { InterfacesModule } from './sharedComponents/interfaces/interfaces.module';
import { VisualAppearanceModule } from './sharedComponents/visualAppearance/visualAppearance.module';
import { WineryTableModule } from '../wineryTableModule/wineryTable.module';
import { AppliesToComponent } from './policyTypes/appliesTo/appliesTo.component';
import { LanguageComponent } from './policyTypes/language/language.component';
import { TopologyTemplateComponent } from './serviceTemplates/topologyTemplate/topologyTemplate.component';
import { PlansComponent } from './serviceTemplates/plans/plans.component';
import { BoundaryDefinitionsModule } from './serviceTemplates/boundaryDefinitions/boundaryDefinitions.module';
import { ValidSourcesAndTargetsComponent } from './relationshipTypes/validSourcesAndTargets/validSourcesAndTargets.component';
import { FilesComponent } from './artifactTemplates/filesTag/files.component';
import { RequiredCapabilityTypeComponent } from './requirementTypes/requiredCapabilityType/requiredCapabilityType.component';
import { ImplementationsModule } from './sharedComponents/implementations/implementations.module';
import { InstanceStatesComponent } from './sharedComponents/instanceStates/instanceStates.component';
import { LoggerComponent } from './admin/logger/logger.component';
import { NamespacesComponent } from './admin/namespaces/namespaces.component';
import { PropertiesComponent } from './sharedComponents/properties/properties.component';
import { PropertiesDefinitionModule } from './sharedComponents/propertiesDefinition/propertiesDefinition.module';
import { RepositoryModule } from './admin/repository/repository.module';
import { TagModule } from './serviceTemplates/tag/tag.module';
import { TypeWithShortNameComponent } from './admin/typesWithShortName/typeWithShortName.component';
import { SpinnerWithInfinityModule } from '../winerySpinnerWithInfinityModule/winerySpinnerWithInfinity.module';
import { WineryEditorModule } from '../wineryEditorModule/wineryEditor.module';
import { CapOrReqDefComponent } from './nodeTypes/capabilityOrRequirementDefinitions/capOrReqDef.component';
import { SelfServicePortalModule } from './serviceTemplates/selfServicePortal/selfServicePortal.module';
import { WineryUploaderModule } from '../wineryUploader/wineryUploader.module';
import { WineryIoParameterModule } from '../wineryIoParameter/wineryIoParameters.module';
import { WineryQNameSelectorModule } from '../wineryQNameSelector/wineryQNameSelector.module';
import { WineryArtifactModule } from './sharedComponents/wineryArtifacts/artifact.module';
import { WineryEditXMLModule } from './sharedComponents/editXML/editXML.module';

@NgModule({
    imports: [
        HttpModule,
        SelectModule,
        BrowserModule,
        FormsModule,
        WineryLoaderModule,
        PropertiesDefinitionModule,
        SelfServicePortalModule,
        InstanceRouterModule,
        SpinnerWithInfinityModule,
        WineryModalModule,
        InterfacesModule,
        WineryEditorModule,
        VisualAppearanceModule,
        ImplementationsModule,
        WineryTableModule,
        WineryDuplicateValidatorModule,
        WineryNamespaceSelectorModule,
        RepositoryModule,
        TagModule,
        WineryQNameSelectorModule,
        WineryUploaderModule,
        WineryIoParameterModule,
        BoundaryDefinitionsModule,
        WineryEditXMLModule,
        BoundaryDefinitionsModule,
        WineryArtifactModule,
    ],
    exports: [],
    declarations: [
        AppliesToComponent,
        DocumentationComponent,
        InheritanceComponent,
        InstanceComponent,
        InstanceHeaderComponent,
        LanguageComponent,
        RemoveWhiteSpacesPipe,
        TopologyTemplateComponent,
        PlansComponent,
        ValidSourcesAndTargetsComponent,
        FilesComponent,
        PropertiesComponent,
        RequiredCapabilityTypeComponent,
        InstanceStatesComponent,
        LoggerComponent,
        NamespacesComponent,
        TypeWithShortNameComponent,
        CapOrReqDefComponent,
    ],
    providers: [],
})
export class InstanceModule {
}
