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
import { BrowserModule } from '@angular/platform-browser';
import { SelectModule } from 'ng2-select';
import { EditXMLComponent } from './editXML/editXML.component';
import { InstanceComponent } from './instance.component';
import { InstanceHeaderComponent } from './instanceHeader/instanceHeader.component';
import { InstanceRouterModule } from './instanceRouter.module';
import { DocumentationComponent } from './documentation/documentation.component';
import { InheritanceComponent } from './inheritance/inheritance.component';
import { RequirementDefinitionsComponent } from './requirementDefinitions/requirementDefinitions.component';
import { CapabilityDefinitionsComponent } from './capabilityDefinitions/capabilityDefinitions.component';
import { RemoveWhiteSpacesPipe } from '../pipes/removeWhiteSpaces.pipe';
import { LoaderModule } from '../loader/loader.module';
import { QNameSelectorComponent } from '../qNameSelector/qNameSelector.component';
import { PropertiesDefinitionModule } from './propertiesDefinition/propertiesDefinition.module';
import { WineryModalModule } from '../wineryModalModule/winery.modal.module';
import { InterfacesModule } from './interfaces/interfaces.module';
import { VisualAppearanceModule } from './visualAppearance/visualAppearance.module';
import { AppliesToComponent } from './appliesTo/appliesTo.component';
import { LanguageComponent } from './language/language.component';
import { LoggerComponent } from './logger/logger.component';
import { PlanTypesComponent } from './planTypes/planTypes.component';
import { ConstraintTypesComponent } from './contraintTypes/constraintTypes.component';
import { PlanLanguagesComponent } from './planLanguages/planLanguages.component';
import { NamespacesComponent } from './namespaces/namespaces.component';
import { WineryTableModule } from '../wineryTableModule/wineryTable.module';
import { DuplicateValidatorModule } from '../validators/duplicateValidator.module';
import { NamespaceSelectorModule } from '../namespaceSelector/namespaceSelector.module';
import { RepositoryModule } from './repository/repository.module';
import { TagModule } from './tag/tag.module';

@NgModule({
    imports: [
        SelectModule,
        BrowserModule,
        FormsModule,
        LoaderModule,
        PropertiesDefinitionModule,
        InstanceRouterModule,
        WineryModalModule,
        InterfacesModule,
        VisualAppearanceModule,
        WineryTableModule,
        DuplicateValidatorModule,
        NamespaceSelectorModule,
        RepositoryModule,
        TagModule
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
        QNameSelectorComponent,
        LoggerComponent,
        PlanTypesComponent,
        ConstraintTypesComponent,
        PlanLanguagesComponent,
        NamespacesComponent,
    ],
    providers: [],
})
export class InstanceModule {
}
