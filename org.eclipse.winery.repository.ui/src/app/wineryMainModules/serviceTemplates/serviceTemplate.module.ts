/********************************************************************************
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
 ********************************************************************************/

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ServiceTemplateRouterModule } from './serviceTemplateRouter.module';
import { TopologyTemplateComponent } from '../../instance/serviceTemplates/topologyTemplate/topologyTemplate.component';
import { PlansComponent } from '../../instance/serviceTemplates/plans/plans.component';
import { WineryLoaderModule } from '../../wineryLoader/wineryLoader.module';
import { WineryModalModule } from '../../wineryModalModule/winery.modal.module';
import { WineryIoParameterModule } from '../../wineryIoParameter/wineryIoParameters.module';
import { WineryUploaderModule } from '../../wineryUploader/wineryUploader.module';
import { SelectModule } from 'ng2-select';
import { FormsModule } from '@angular/forms';
import { WineryTableModule } from '../../wineryTableModule/wineryTable.module';
import { InstanceModule } from '../../instance/instance.module';
import { SelfServicePortalModule } from '../../instance/serviceTemplates/selfServicePortal/selfServicePortal.module';
import { BoundaryDefinitionsModule } from '../../instance/serviceTemplates/boundaryDefinitions/boundaryDefinitions.module';
import { TagModule } from '../../instance/serviceTemplates/tag/tag.module';
import { DocumentationModule } from '../../instance/sharedComponents/documentation/documentation.module';
import { WineryReadmeModule } from '../../wineryReadmeModule/wineryReadme.module';
import { WineryLicenseModule } from '../../wineryLicenseModule/wineryLicense.module';
import { WinerySourceModule } from '../../instance/sharedComponents/artifactSource/source.module';
import { ConstraintCheckingComponent } from '../../instance/serviceTemplates/constraintChecking/constraintChecking.component';
import { WineryEditorModule } from '../../wineryEditorModule/wineryEditor.module';
import { TopologyTemplateModule } from '../../instance/serviceTemplates/topologyTemplate/topologyTemplate.module';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        SelectModule,
        InstanceModule,
        SelfServicePortalModule,
        BoundaryDefinitionsModule,
        TagModule,
        TopologyTemplateModule,
        DocumentationModule,
        WinerySourceModule,
        WineryLoaderModule,
        WineryEditorModule,
        WineryModalModule,
        WineryIoParameterModule,
        WineryUploaderModule,
        WineryTableModule,
        ServiceTemplateRouterModule,
        WineryReadmeModule,
        WineryLicenseModule
    ],
    declarations: [
        PlansComponent,
        ConstraintCheckingComponent,
    ]
})
export class ServiceTemplateModule {
}
