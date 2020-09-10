/*******************************************************************************
 * Copyright (c) 2018-2020 Contributors to the Eclipse Foundation
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
import { WineryModalModule } from '../../wineryModalModule/winery.modal.module';
import { CommonModule } from '@angular/common';
import { RelationMappingsComponent } from './relationshipMappings/relationMappings.component';
import { HttpClientModule } from '@angular/common/http';
import { WineryNotificationModule } from '../../wineryNotificationModule/wineryNotification.module';
import { InstanceModule } from '../instance.module';
import { BrowserModule } from '@angular/platform-browser';
import { WineryTableModule } from '../../wineryTableModule/wineryTable.module';
import { WineryLoaderModule } from '../../wineryLoader/wineryLoader.module';
import { SelectModule } from 'ng2-select';
import { StayMappingsComponent } from './stayMappings/stayMappings.component';
import { AttributeMappingsComponent } from './attributeMappings/attributeMappings.component';
import { DeploymentArtifactsMappingsComponent } from './deploymentArtifactsMappings/deploymentArtifactsMappings.component';
import { FormsModule } from '@angular/forms';
import { PermutationMappingsComponent } from './permutationMappings/permutationMappings.component';
import { WineryDynamicTableModule } from '../../wineryDynamicTable/wineryDynamicTable.module';
import { PermutationsComponent } from './permutations/permutations.component';
import { AlertModule } from 'ngx-bootstrap';

@NgModule({
    imports: [
        AlertModule.forRoot(),
        CommonModule,
        FormsModule,
        HttpClientModule,
        BrowserModule,
        SelectModule,
        InstanceModule,
        WineryModalModule,
        WineryNotificationModule,
        WineryTableModule,
        WineryLoaderModule,
        WineryDynamicTableModule,
    ],
    exports: [
        RelationMappingsComponent,
        AttributeMappingsComponent,
        StayMappingsComponent,
        DeploymentArtifactsMappingsComponent,
        PermutationMappingsComponent,
    ],
    declarations: [
        RelationMappingsComponent,
        AttributeMappingsComponent,
        StayMappingsComponent,
        DeploymentArtifactsMappingsComponent,
        PermutationMappingsComponent,
        PermutationsComponent,
    ]
})
export class RefinementModelsModule {
}
