/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { WineryModalModule } from '../../../wineryModalModule/winery.modal.module';
import { BoundaryDefinitionsComponent } from './boundaryDefinitions.component';
import { FileUploadModule } from 'ng2-file-upload';
import { RouterModule, Routes } from '@angular/router';
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
import { InterfacesComponent } from '../../sharedComponents/interfaces/interfaces.component';
import { InterfacesModule } from '../../sharedComponents/interfaces/interfaces.module';
import { WineryQNameSelectorModule } from '../../../wineryQNameSelector/wineryQNameSelector.module';
import { WineryEditorModule } from '../../../wineryEditorModule/wineryEditor.module';

export const boundaryDefinitionsRoutes: Routes = [
    { path: 'properties', component: EditXMLComponent },
    { path: 'propertymappings', component: PropertyMappingsComponent },
    { path: 'propertyconstraints', component: PropertyConstraintsComponent },
    { path: 'requirements', component: RequirementsComponent },
    { path: 'capabilities', component: CapabilitiesComponent },
    { path: 'policies', component: PoliciesComponent },
    { path: 'interfaces', component: InterfacesComponent },
    { path: 'xml', component: EditXMLComponent },
    { path: '', redirectTo: 'properties', pathMatch: 'full' }
];

@NgModule({
    imports: [
        FormsModule,
        WineryLoaderModule,
        CommonModule,
        WineryModalModule,
        FileUploadModule,
        SelectModule,
        InterfacesModule,
        WineryTableModule,
        WineryDuplicateValidatorModule,
        WineryEditXMLModule,
        WineryQNameSelectorModule,
        RouterModule,
        WineryEditorModule
        // RouterModule.forChild(boundaryDefinitionsRoutes),
    ],
    exports: [
        RouterModule
    ],
    declarations: [
        BoundaryDefinitionsComponent,
        PoliciesComponent,
        PropertyConstraintsComponent,
        PropertyMappingsComponent,
        RequirementsComponent,
        CapabilitiesComponent,
        RequirementsOrCapabilitiesComponent,
    ],
    providers: [
        InstanceService
    ]
})
export class BoundaryDefinitionsModule {
}
