/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Balzer - initial API and implementation
 *     Niko Stadelmaier - add PropertyMappingsComponent
 */
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { WineryModalModule } from '../../wineryModalModule/winery.modal.module';
import { BoundaryDefinitionsComponent } from './boundaryDefinitions.component';
import { FileUploadModule } from 'ng2-file-upload';
import { RouterModule } from '@angular/router';
import { PlaceholderComponent } from './placeholder/placeholder.component';
import { InstanceService } from '../instance.service';
import { PropertyMappingsComponent } from './propertyMappings/propertyMappings.component';
import { PropertyConstraintsComponent } from './propertyConstraints/propertyConstraints.component';
import { RequirementsComponent } from './requirements/requirements.component';
import { WineryTableModule } from '../../wineryTableModule/wineryTable.module';
import { RequirementsOrCapabilitesComponent } from './requirementsOrCapabilites/requirementsOrCapabilites.component';
import { CapabilitiesComponent } from './capabilities/capabilities.component';
import { XMLEditorComponent } from './xmlEditor/xmlEditor.component';
import { WineryEditorModule } from '../../wineryEditorModul/wineryEditor.module';
import { WineryLoaderModule } from '../../wineryLoader/wineryLoader.module';
import { WineryDuplicateValidatorModule } from '../../wineryValidators/wineryDuplicateValidator.module';

export const boundaryDefinitionsRoutes = [
    { path: 'properties', component: XMLEditorComponent },
    { path: 'propertymappings', component: PropertyMappingsComponent },
    { path: 'propertyconstraints', component: PropertyConstraintsComponent },
    { path: 'requirements', component: RequirementsComponent },
    { path: 'capabilities', component: CapabilitiesComponent },
    { path: 'policies', component: PlaceholderComponent },
    { path: 'interfaces', component: PlaceholderComponent },
    { path: 'xml', component: XMLEditorComponent },
    { path: '', redirectTo: 'properties', pathMatch: 'full'}
];

@NgModule({
    imports: [
        RouterModule,
        BrowserModule,
        FormsModule,
        WineryLoaderModule,
        CommonModule,
        WineryModalModule,
        FileUploadModule,
        WineryTableModule,
        WineryDuplicateValidatorModule,
        WineryEditorModule
    ],
    exports: [],
    declarations: [
        BoundaryDefinitionsComponent,
        PlaceholderComponent,
        PropertyConstraintsComponent,
        PropertyMappingsComponent,
        RequirementsComponent,
        CapabilitiesComponent,
        RequirementsOrCapabilitesComponent,
        XMLEditorComponent
    ],
    providers: [InstanceService]
})
export class BoundaryDefinitionsModule {
}
