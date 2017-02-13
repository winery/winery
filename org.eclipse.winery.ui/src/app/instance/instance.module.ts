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
import { PropertyDefinitionComponent } from './propertyDefinition/propertyDefinition.component';

@NgModule({
    imports: [
        BrowserModule,
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
        RequirementDefinitionsComponent,
        VisualAppearanceComponent,
    ],
    providers: [],
})
export class InstanceModule {
}
