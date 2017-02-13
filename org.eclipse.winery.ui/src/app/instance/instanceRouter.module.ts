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
import { PropertyDefinitionComponent } from './propertyDefinition/propertyDefinition.component';

const instanceRoutes: Routes = [
    {
        path: ':section/:namespace/:componentId',
        // path: '',
        component: InstanceComponent,
        children: [
            // { path: '', component: InstanceComponent },
            { path: 'capabilitydefinitions', component: CapabilityDefinitionsComponent },
            { path: 'documentation', component: DocumentationComponent },
            { path: 'implementations', component: ImplementationsComponent },
            { path: 'inheritance', component: InheritanceComponent },
            { path: 'instancestates', component: InstanceStatesComponent },
            { path: 'interfaces', component: InterfacesComponent },
            { path: 'propertydefinition', component: PropertyDefinitionComponent },
            { path: 'requirementdefinitions', component: RequirementDefinitionsComponent },
            { path: 'visualappearance', component: VisualAppearanceComponent},
            { path: 'xml', component: EditXMLComponent }
        ]
    }
];

@NgModule({
    imports: [
        BrowserModule,
        RouterModule.forChild(instanceRoutes)
    ],
    exports: [
        RouterModule
    ],
    providers: [],
})
export class InstanceRouterModule {
}
