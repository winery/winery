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
import { WineryModalModule } from '../../../wineryModalModule/winery.modal.module';
import { BoundaryDefinitionsComponent } from './boundaryDefinitions.component';
import { FileUploadModule } from 'ng2-file-upload';
import { RouterModule } from '@angular/router';
import { PlaceholderComponent } from './placeholder/placeholder.component';
import { InstanceService } from '../../instance.service';
import { PropertyMappingsComponent } from './propertyMappings/propertyMappings.component';
import { PropertyConstraintsComponent } from './propertyConstraints/propertyConstraints.component';
import { RequirementsComponent } from './requirements/requirements.component';
import { WineryTableModule } from '../../../wineryTableModule/wineryTable.module';
import { RequirementsOrCapabilitiesComponent } from './requirementsOrCapabilities/requirementsOrCapabilities.component';
import { CapabilitiesComponent } from './capabilities/capabilities.component';
import { WineryLoaderModule } from '../../../wineryLoader/wineryLoader.module';
import { WineryDuplicateValidatorModule } from '../../../wineryValidators/wineryDuplicateValidator.module';
import { PoliciesComponent } from './policies/policies.component';
import { SelectModule } from 'ng2-select';
import { EditXMLComponent } from '../../sharedComponents/editXML/editXML.component';
import { WineryEditXMLModule } from '../../sharedComponents/editXML/editXML.module';

export const boundaryDefinitionsRoutes = [
    { path: 'properties', component: EditXMLComponent },
    { path: 'propertymappings', component: PropertyMappingsComponent },
    { path: 'propertyconstraints', component: PropertyConstraintsComponent },
    { path: 'requirements', component: RequirementsComponent },
    { path: 'capabilities', component: CapabilitiesComponent },
    { path: 'policies', component: PoliciesComponent },
    { path: 'interfaces', component: PlaceholderComponent },
    { path: 'xml', component: EditXMLComponent },
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
        SelectModule,
        WineryTableModule,
        WineryDuplicateValidatorModule,
        WineryEditXMLModule
    ],
    exports: [],
    declarations: [
        BoundaryDefinitionsComponent,
        PlaceholderComponent,
        PoliciesComponent,
        PropertyConstraintsComponent,
        PropertyMappingsComponent,
        RequirementsComponent,
        CapabilitiesComponent,
        RequirementsOrCapabilitiesComponent,
    ],
    providers: [InstanceService]
})
export class BoundaryDefinitionsModule {
}
