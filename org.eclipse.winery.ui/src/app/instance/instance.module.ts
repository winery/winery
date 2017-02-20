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

@NgModule({
    imports: [
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
    ],
    providers: [],
})
export class InstanceModule {
}
