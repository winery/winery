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
import { CapOrReqDefComponent } from '../../instance/nodeTypes/capabilityOrRequirementDefinitions/capOrReqDef.component';
import { CommonModule } from '@angular/common';
import { NodeTypeRouterModule } from './nodeTypeRouter.module';
import { ImplementationsModule } from '../../instance/sharedComponents/implementations/implementations.module';
import { WineryModalModule } from '../../wineryModalModule/winery.modal.module';
import { SpinnerWithInfinityModule } from '../../winerySpinnerWithInfinityModule/winerySpinnerWithInfinity.module';
import { WineryEditorModule } from '../../wineryEditorModule/wineryEditor.module';
import { SelectModule } from 'ng2-select';
import { WineryLoaderModule } from '../../wineryLoader/wineryLoader.module';
import { WineryQNameSelectorModule } from '../../wineryQNameSelector/wineryQNameSelector.module';
import { WineryTableModule } from '../../wineryTableModule/wineryTable.module';
import { FormsModule } from '@angular/forms';
import { VisualAppearanceModule } from '../../instance/sharedComponents/visualAppearance/visualAppearance.module';
import { InstanceStatesModule } from '../../instance/sharedComponents/instanceStates/instanceStates.module';
import { PropertiesDefinitionModule } from '../../instance/sharedComponents/propertiesDefinition/propertiesDefinition.module';
import { InheritanceModule } from '../../instance/sharedComponents/inheritance/inheritance.module';
import { WineryReadmeModule } from '../../wineryReadmeModule/wineryReadme.module';
import { WineryLicenseModule } from '../../wineryLicenseModule/wineryLicense.module';
import { TagModule } from '../../instance/sharedComponents/tag/tag.module';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        VisualAppearanceModule,
        InstanceStatesModule,
        PropertiesDefinitionModule,
        InheritanceModule,
        ImplementationsModule,
        SelectModule,
        SpinnerWithInfinityModule,
        TagModule,
        WineryLoaderModule,
        WineryQNameSelectorModule,
        WineryTableModule,
        WineryModalModule,
        WineryEditorModule,
        NodeTypeRouterModule,
        WineryReadmeModule,
        WineryLicenseModule
    ],
    exports: [],
    declarations: [CapOrReqDefComponent],
    providers: [],
})
export class NodeTypeModule {
}
