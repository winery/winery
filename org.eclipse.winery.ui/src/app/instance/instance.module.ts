/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzentter - initial API and implementation
 */

import { NgModule } from '@angular/core';
import { EditXMLComponent } from './editXML/editXML.component';
import { InstanceComponent } from './instance.component';
import { InstanceHeaderComponent } from './instanceHeader/instanceHeader.component';
import { InstanceRouterModule } from './instanceRouter.module';
import { BrowserModule } from '@angular/platform-browser';
import { VisualAppearanceComponent } from './visualAppearance/visualAppearance.component';
import { DocumentationComponent } from './documentation/documentation.component';
import { InheritanceComponent } from './inheritance/inheritance.component';
import { InstanceStatesComponent } from './instanceStates/instanceStates.component';
import { InterfacesComponent } from './interfaces/interfaces.component';
import { ImplementationsComponent } from './implementations/implementations.component';
import { RequirementDefinitionsComponent } from './requirementDefinitions/requirementDefinitions.component';
import { CapabilityDefinitionsComponent } from './capabilityDefinitions/capabilityDefinitions.component';
import { PropertyDefinitionComponent } from './propertiesDefinition/propertiesDefinition.component';
import { RemoveWhiteSpacesPipe } from '../pipes/removeWhiteSpaces.pipe';
import { LoaderModule } from '../loader/loader.module';
import { FormsModule } from '@angular/forms';
import { QNameSelectorComponent } from '../qNameSelector/qNameSelector.component';
import { SelectModule } from 'ng2-select';

@NgModule({
    imports: [
        SelectModule,
        BrowserModule,
        FormsModule,
        LoaderModule,
        InstanceRouterModule,
    ],
    exports: [],
    declarations: [
        CapabilityDefinitionsComponent,
        DocumentationComponent,
        EditXMLComponent,
        ImplementationsComponent,
        InheritanceComponent,
        InstanceComponent,
        InstanceHeaderComponent,
        InstanceStatesComponent,
        InterfacesComponent,
        PropertyDefinitionComponent,
        RemoveWhiteSpacesPipe,
        RequirementDefinitionsComponent,
        VisualAppearanceComponent,
        QNameSelectorComponent,
    ],
    providers: [],
})
export class InstanceModule {
}
